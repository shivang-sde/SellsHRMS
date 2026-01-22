package com.sellspark.SellsHRMS.exception;

import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties.Http;
import org.springframework.http.HttpStatus;

import lombok.Getter;


/**
 * Base exception class for all custom exceptions
 */
@Getter
public class HRMSException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatus;

    public HRMSException(String message, String errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public HRMSException(String message, String errorCode, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

}
