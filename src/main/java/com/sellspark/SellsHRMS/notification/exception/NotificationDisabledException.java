package com.sellspark.SellsHRMS.notification.exception;

public class NotificationDisabledException extends RuntimeException {

    public NotificationDisabledException(String message) {
        super(message);
    }

    public NotificationDisabledException(String message, Throwable cause) {
        super(message, cause);
    }
}
