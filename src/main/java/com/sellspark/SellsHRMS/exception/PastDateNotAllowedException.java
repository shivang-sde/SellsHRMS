package com.sellspark.SellsHRMS.exception;

import org.springframework.http.HttpStatus;

import com.sellspark.SellsHRMS.exception.core.HRMSException;

public class PastDateNotAllowedException extends HRMSException {
    public PastDateNotAllowedException(String action) {
        super(
                String.format("Cannot %s for past dates", action),
                "PAST_DATE_NOT_ALLOWED",
                HttpStatus.BAD_REQUEST);
    }
}