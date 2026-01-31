package com.sellspark.SellsHRMS.exception;

import org.springframework.http.HttpStatus;

import com.sellspark.SellsHRMS.exception.core.HRMSException;

public class FileUploadException extends HRMSException {
    public FileUploadException(String message) {
        super(message, "FILE_UPLOAD_ERROR", HttpStatus.BAD_REQUEST);
    }

    public FileUploadException(String message, Throwable cause) {
        super(message, "FILE_UPLOAD_ERROR", HttpStatus.BAD_REQUEST, cause);
    }
}
