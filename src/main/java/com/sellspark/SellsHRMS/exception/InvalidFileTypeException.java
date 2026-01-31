package com.sellspark.SellsHRMS.exception;

import org.springframework.http.HttpStatus;

import com.sellspark.SellsHRMS.exception.core.HRMSException;

public class InvalidFileTypeException extends HRMSException {
    public InvalidFileTypeException(String allowedTypes) {
        super(
                String.format("Invalid file type. Allowed types: %s", allowedTypes),
                "INVALID_FILE_TYPE",
                HttpStatus.BAD_REQUEST);
    }
}
