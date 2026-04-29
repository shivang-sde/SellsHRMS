package com.sellspark.SellsHRMS.notification.exception;

public class InvalidSMTPConfigException extends RuntimeException {
    public InvalidSMTPConfigException(String message) {
        super(message);
    }

    public InvalidSMTPConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
