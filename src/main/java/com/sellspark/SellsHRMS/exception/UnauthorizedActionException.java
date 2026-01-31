package com.sellspark.SellsHRMS.exception;

import org.springframework.http.HttpStatus;

import com.sellspark.SellsHRMS.exception.core.HRMSException;

public class UnauthorizedActionException extends HRMSException {
    public UnauthorizedActionException(String message) {
        super(message, "UNAUTHORIZED_ACTION", HttpStatus.FORBIDDEN);
    }

    public UnauthorizedActionException() {
        super(
                "You do not have permission to perform this action",
                "UNAUTHORIZED_ACTION",
                HttpStatus.FORBIDDEN);
    }
}