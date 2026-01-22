package com.sellspark.SellsHRMS.exception;

import org.springframework.http.HttpStatus;

public class InvalidOperationException extends HRMSException {
    
    public InvalidOperationException(String message) {
        super(message, "INVALID_OPERATION", HttpStatus.BAD_REQUEST);
    }
}
