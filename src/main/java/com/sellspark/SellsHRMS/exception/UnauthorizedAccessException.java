package com.sellspark.SellsHRMS.exception;

import org.springframework.http.HttpStatus;

import com.sellspark.SellsHRMS.exception.core.HRMSException;

public class UnauthorizedAccessException extends HRMSException {
    public UnauthorizedAccessException(String message) {
        super(message, "UNAUTHORIZED_ACCESS", HttpStatus.FORBIDDEN);
    }

    public UnauthorizedAccessException() {
        super(
                "You do not have permission to perform this action",
                "UNAUTHORIZED_ACCESS",
                HttpStatus.FORBIDDEN);
    }
}