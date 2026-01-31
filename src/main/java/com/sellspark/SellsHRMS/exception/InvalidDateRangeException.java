package com.sellspark.SellsHRMS.exception;

import org.springframework.http.HttpStatus;

import com.sellspark.SellsHRMS.exception.core.HRMSException;

public class InvalidDateRangeException extends HRMSException {
    public InvalidDateRangeException(String message) {
        super(message, "INVALID_DATE_RANGE", HttpStatus.BAD_REQUEST);
    }
}
