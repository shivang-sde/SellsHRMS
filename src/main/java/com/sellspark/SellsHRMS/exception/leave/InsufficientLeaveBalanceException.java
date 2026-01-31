package com.sellspark.SellsHRMS.exception.leave;

import org.springframework.http.HttpStatus;

import com.sellspark.SellsHRMS.exception.core.HRMSException;

public class InsufficientLeaveBalanceException extends HRMSException {

    public InsufficientLeaveBalanceException(double requested, double available) {
        super(
                String.format("Insufficient leave balance. Requested: %.2f days, Available: %.2f days",
                        requested, available),
                "INSUFFICIENT_LEAVE_BALANCE",
                HttpStatus.BAD_REQUEST);
    }

    public InsufficientLeaveBalanceException(String message) {
        super(message, "INSUFFICIENT_LEAVE_BALANCE", HttpStatus.BAD_REQUEST);
    }
}
