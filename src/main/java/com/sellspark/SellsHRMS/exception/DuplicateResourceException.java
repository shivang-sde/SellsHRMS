package com.sellspark.SellsHRMS.exception;

import org.springframework.http.HttpStatus;

import com.sellspark.SellsHRMS.exception.core.HRMSException;

public class DuplicateResourceException extends HRMSException {

    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {

        super(
                String.format("%s already exists with %s: '%s'", resourceName, fieldName, fieldValue),
                "DUPLICATE_RESOURCE",
                HttpStatus.CONFLICT);
    }

    public DuplicateResourceException(String string) {
        super(string, "PAY_RUN_EXIST", HttpStatus.CONFLICT);
    }

}
