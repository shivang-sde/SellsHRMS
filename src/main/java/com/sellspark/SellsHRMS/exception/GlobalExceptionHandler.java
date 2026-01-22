package com.sellspark.SellsHRMS.exception;

import com.sellspark.SellsHRMS.dto.error.ErrorResponse;


import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.security.access.AccessDeniedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Global exception handler for all REST controllers
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle custom HRMS exceptions
     */
    @ExceptionHandler(HRMSException.class)
    public ResponseEntity<ErrorResponse> handleHRMSException(
            HRMSException ex, 
            WebRequest request) {
        
        log.error("HRMS Exception: {} - {}", ex.getErrorCode(), ex.getMessage(), ex);
        
        ErrorResponse errorResponse = ErrorResponse.of(
                ex.getHttpStatus().value(),
                ex.getHttpStatus().getReasonPhrase(),
                ex.getErrorCode(),
                ex.getMessage(),
                getPath(request)
        );
        
        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }

    /**
     * Handle validation errors (@Valid annotation)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        
        log.error("Validation failed", ex);
        
        List<ErrorResponse.ValidationError> validationErrors = new ArrayList<>();
        
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            Object rejectedValue = ((FieldError) error).getRejectedValue();
            
            validationErrors.add(ErrorResponse.ValidationError.builder()
                    .field(fieldName)
                    .message(errorMessage)
                    .rejectedValue(rejectedValue)
                    .build());
        });
        
        ErrorResponse errorResponse = ErrorResponse.withValidationErrors(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed for one or more fields",
                getPath(request),
                validationErrors
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ErrorResponse> handleAccessDenied(
                AccessDeniedException ex,
                WebRequest request) {

        log.warn("Access denied: {}", ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.of(
            HttpStatus.FORBIDDEN.value(),
            "Forbidden",
            "ACCESS_DENIED",
            "You do not have permission to perform this action.",
            getPath(request)
    );

    return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
}



    /**
     * Handle missing request parameters
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParams(
            MissingServletRequestParameterException ex,
            WebRequest request) {
        
        log.error("Missing parameter: {}", ex.getParameterName(), ex);
        
        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "MISSING_PARAMETER",
                String.format("Required parameter '%s' is missing", ex.getParameterName()),
                getPath(request)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle type mismatch (e.g., string instead of number)
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            WebRequest request) {
        
        log.error("Type mismatch for parameter: {}", ex.getName(), ex);
        
        String message = String.format(
                "Parameter '%s' should be of type %s but received '%s'",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown",
                ex.getValue()
        );
        
        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "TYPE_MISMATCH",
                message,
                getPath(request)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle malformed JSON
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            WebRequest request) {
        
        log.error("Malformed JSON request", ex);
        
        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "MALFORMED_JSON",
                "Malformed JSON request. Please check your request body",
                getPath(request)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle file upload size exceeded
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceeded(
            MaxUploadSizeExceededException ex,
            WebRequest request) {
        
        log.error("File size exceeded", ex);
        
        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.PAYLOAD_TOO_LARGE.value(),
                "Payload Too Large",
                "FILE_SIZE_EXCEEDED",
                "File size exceeds the maximum allowed limit (5MB)",
                getPath(request)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    /**
     * Handle IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            WebRequest request) {
        
        log.error("Illegal argument", ex);
        
        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "ILLEGAL_ARGUMENT",
                ex.getMessage(),
                getPath(request)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle NullPointerException
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointer(
            NullPointerException ex,
            WebRequest request) {
        
        log.error("Null pointer exception", ex);
        
        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "NULL_POINTER",
                "A null value was encountered where it was not expected. Please contact support.",
                getPath(request)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            WebRequest request) {
        
        String traceId = java.util.UUID.randomUUID().toString();
        MDC.put("traceId", traceId);
        
       log.error("TraceID={} | Unexpected error: {}", traceId, ex.getMessage(), ex);
        
        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "INTERNAL_ERROR",
                "An unexpected error occurred. Please try again later or contact support.",
                getPath(request)
        );

        errorResponse.setTraceId(traceId);
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Extract request path from WebRequest
     */
    private String getPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}