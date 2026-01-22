package com.sellspark.SellsHRMS.exception;
import org.springframework.http.HttpStatus;

public class InvalidDateRangeException extends HRMSException {
    public InvalidDateRangeException(String message) {
        super(message, "INVALID_DATE_RANGE", HttpStatus.BAD_REQUEST);
    }
}
