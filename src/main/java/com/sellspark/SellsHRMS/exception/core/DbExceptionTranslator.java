package com.sellspark.SellsHRMS.exception.core;

import java.sql.SQLException;
import java.util.Optional;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class DbExceptionTranslator {

    private DbExceptionTranslator() {
    }

    /**
     * Try to translate a throwable into HRMSException. Return null if not
     * translatable.
     */
    public static HRMSException translate(Throwable t) {
        String traceId = MDC.get("traceId");

        // If it's already an HRMSException, return it (no double-wrapping).
        if (t instanceof HRMSException hrms) {
            return hrms;
        }

        // If Spring already wrapped it
        if (t instanceof DataIntegrityViolationException dive) {
            log.debug("[TRACE:{}] Received DataIntegrityViolationException: {}", traceId, dive.getMessage());
            return translateFromThrowable(dive);
        }

        if (t instanceof InvalidDataAccessResourceUsageException usageEx) {
            log.error("[TRACE:{}] Invalid SQL or JPA query: {}", traceId, usageEx.getMessage(), usageEx);

            // try to find the underlying sqlException
            Throwable cause = usageEx.getCause();
            while (cause != null) {
                if (cause instanceof SQLException sqlEx) {
                    return translateFromSqlException(sqlEx);
                }
                cause = cause.getCause();
            }
            return new HRMSException(
                    "Internal database query syntax error. Please contact the administrator.",
                    "SQL_SYNTAX_ERROR",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Hibernate's ConstraintViolationException may be thrown directly
        if (t instanceof ConstraintViolationException hibernateCve) {
            log.debug("[TRACE:{}] Received Hibernate ConstraintViolationException: {}", traceId,
                    hibernateCve.getMessage());
            return translateFromHibernate(hibernateCve);
        }

        // Sometimes PersistenceException or other wrappers contain the nested cause
        // Walk the cause chain and try to locate a known exception
        Throwable current = t;
        while (current != null) {
            if (current instanceof ConstraintViolationException hibernateChainCve) {
                return translateFromHibernate(hibernateChainCve);
            }
            if (current instanceof DataIntegrityViolationException diveChain) {
                return translateFromThrowable(diveChain);
            }
            if (current instanceof SQLException sqlEx) {
                return translateFromSqlException(sqlEx);
            }
            current = current.getCause();
        }

        // Not a DB integrity error we can translate
        log.debug("[TRACE:{}] Unable to translate exception type: {}", traceId, t.getClass().getName());
        return null;
    }

    private static HRMSException translateFromThrowable(Throwable t) {
        Throwable root = (t instanceof DataIntegrityViolationException dive) ? dive.getMostSpecificCause()
                : t.getCause();
        if (root instanceof ConstraintViolationException) {
            return translateFromHibernate((ConstraintViolationException) root);
        }
        if (root instanceof SQLException sqlEx) {
            return translateFromSqlException(sqlEx);
        }
        // try walking cause chain as fallback
        Throwable current = t;
        while (current != null) {
            if (current instanceof SQLException s) {
                return translateFromSqlException(s);
            }
            current = current.getCause();
        }
        return null;
    }

    private static HRMSException translateFromHibernate(ConstraintViolationException hibernateEx) {
        String traceId = MDC.get("traceId");
        log.error("[TRACE:{}] Hibernate constraint violation: constraintName={}, message={}",
                traceId,
                hibernateEx.getConstraintName(),
                hibernateEx.getMessage(),
                hibernateEx);

        // Try to extract underlying SQLException if present
        SQLException sqlEx = Optional.ofNullable(hibernateEx.getSQLException()).orElse(null);
        if (sqlEx != null) {
            return translateFromSqlException(sqlEx);
        }

        // Fallback based on constraint name if available
        String constraint = hibernateEx.getConstraintName();
        if (constraint != null && constraint.toLowerCase().contains("fk")) {
            return new HRMSException("This record cannot be deleted because it is referenced by other data.",
                    "RESOURCE_IN_USE", HttpStatus.CONFLICT);
        }

        return new HRMSException("A database constraint was violated.", "DB_CONSTRAINT_VIOLATION",
                HttpStatus.CONFLICT);
    }

    private static HRMSException translateFromSqlException(SQLException sqlEx) {

        String sqlState = sqlEx.getSQLState();
        if ("23000".equals(sqlState)) {
            return new HRMSException("A data integrity constraint was violated.", "INTEGRITY_ERROR",
                    HttpStatus.CONFLICT);
        }

        String traceId = MDC.get("traceId");
        int errorCode = sqlEx.getErrorCode();
        String msg = sqlEx.getMessage() == null ? "" : sqlEx.getMessage();

        log.error("[TRACE:{}] SQLException detected: SQLState={}, ErrorCode={}, Message={}",
                traceId,
                sqlEx.getSQLState(),
                errorCode,
                msg,
                sqlEx);

        switch (errorCode) {

            // -------------------- FOREIGN KEY CONSTRAINTS --------------------
            case 1451: // Cannot delete/update parent row: FK constraint fails
                return new HRMSException(
                        resolveForeignKeyMessage(msg),
                        "RESOURCE_IN_USE",
                        HttpStatus.CONFLICT);

            case 1452: // Cannot add/update child row: FK constraint fails
                return new HRMSException(
                        "Cannot create or update record because a referenced entity does not exist.",
                        "INVALID_REFERENCE",
                        HttpStatus.BAD_REQUEST);

            // -------------------- UNIQUE / DUPLICATE --------------------
            case 1062: // Duplicate entry for unique key
                return new HRMSException(
                        "Duplicate record detected. Please use unique values.",
                        "DUPLICATE_ENTRY",
                        HttpStatus.CONFLICT);

            // -------------------- NOT NULL / REQUIRED FIELD --------------------
            case 1048: // Column cannot be null
                return new HRMSException(
                        "Missing required data. A mandatory field was null.",
                        "NULL_CONSTRAINT",
                        HttpStatus.BAD_REQUEST);

            case 1364: // Field doesn't have a default value
                return new HRMSException(
                        "Missing required field value. No default value is defined.",
                        "MISSING_DEFAULT",
                        HttpStatus.BAD_REQUEST);

            // -------------------- SYNTAX / INVALID SQL --------------------
            case 1064: // SQL syntax error
                return new HRMSException(
                        "Internal database query syntax error. Please contact the administrator.",
                        "SQL_SYNTAX_ERROR",
                        HttpStatus.INTERNAL_SERVER_ERROR);

            case 1146: // Table doesn't exist
                return new HRMSException(
                        "The requested database table does not exist. Contact administrator.",
                        "TABLE_NOT_FOUND",
                        HttpStatus.INTERNAL_SERVER_ERROR);

            case 1054: // Unknown column
                return new HRMSException(
                        "A database column referenced in the query does not exist. Please contact support.",
                        "UNKNOWN_COLUMN",
                        HttpStatus.INTERNAL_SERVER_ERROR);

            // -------------------- DATA TYPE / VALUE ERRORS --------------------
            case 1264: // Out of range value for column
            case 1265: // Data truncated for column
                return new HRMSException(
                        "Invalid or out-of-range value provided for one of the fields.",
                        "INVALID_DATA_VALUE",
                        HttpStatus.BAD_REQUEST);

            case 1366: // Incorrect integer value / data type mismatch
                return new HRMSException(
                        "Invalid data type provided. Please check your input values.",
                        "DATA_TYPE_MISMATCH",
                        HttpStatus.BAD_REQUEST);

            // -------------------- LOCKS / DEADLOCKS --------------------
            case 1205: // Lock wait timeout exceeded
                return new HRMSException(
                        "Database operation timed out due to locking. Please retry.",
                        "LOCK_TIMEOUT",
                        HttpStatus.CONFLICT);

            case 1213: // Deadlock found when trying to get lock
                return new HRMSException(
                        "Database deadlock occurred. Please retry the operation.",
                        "DEADLOCK",
                        HttpStatus.CONFLICT);

            // -------------------- CONNECTION / SERVER --------------------
            case 2002: // Can't connect to MySQL server
            case 2006: // MySQL server has gone away
                return new HRMSException(
                        "Database connection error. Please try again later.",
                        "DB_CONNECTION_ERROR",
                        HttpStatus.SERVICE_UNAVAILABLE);

            // -------------------- DEFAULT FALLBACK --------------------
            default:
                return new HRMSException(
                        "A database error occurred. Please contact support if it persists.",
                        "DB_ERROR",
                        HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private static String resolveForeignKeyMessage(String msg) {
        // Use message contents to map to meaningful user message.
        // Add more mappings as your schema grows.
        String lower = msg == null ? "" : msg.toLowerCase();
        if (lower.contains("tbl_employee") && lower.contains("tbl_designation")) {
            return "This designation cannot be deleted because it is assigned to one or more employees.";
        }
        if (lower.contains("tbl_employee") && lower.contains("tbl_department")) {
            return "This department cannot be deleted because employees are still assigned to it.";
        }
        if (lower.contains("tbl_leave_request") && lower.contains("tbl_leave_type")) {
            return "This leave type cannot be deleted because there are leave requests using it.";
        }
        return "This record is being used elsewhere and cannot be deleted.";
    }
}
