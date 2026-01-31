package com.sellspark.SellsHRMS.exception.util;

import org.springframework.http.HttpStatus;

import com.sellspark.SellsHRMS.exception.core.HRMSException;

public final class Exceptions {
    private Exceptions() {
    }

    public static HRMSException notFound(String entity, Object id) {
        return new HRMSException(
                entity + " not found with id: " + id,
                "NOT_FOUND",
                HttpStatus.NOT_FOUND);
    }

    public static HRMSException invalidOperation(String reason) {
        return new HRMSException(
                reason,
                "INVALID_OPERATION",
                HttpStatus.BAD_REQUEST);
    }

    public static HRMSException conflict(String reason) {
        return new HRMSException(
                reason,
                "CONFLICT",
                HttpStatus.CONFLICT);
    }
}
