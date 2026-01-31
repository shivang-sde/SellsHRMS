package com.sellspark.SellsHRMS.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class PasswordValidationException extends RuntimeException {
    private final HttpStatus status;

    public PasswordValidationException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST; // 400 (doesn't trigger logout)
    }

    public PasswordValidationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
