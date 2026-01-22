package com.sellspark.SellsHRMS.exception;

import org.springframework.http.HttpStatus;

public class InsufficientLeaveBalanceException extends HRMSException {
    
     public InsufficientLeaveBalanceException(double requested, double available) {
        super(
            String.format("Insufficient leave balance. Requested: %d days, Available: %d days", 
                         requested, available),
            "INSUFFICIENT_LEAVE_BALANCE",
            HttpStatus.BAD_REQUEST
        );
    }

    public InsufficientLeaveBalanceException(String message) {
        super(message, "INSUFFICIENT_LEAVE_BALANCE", HttpStatus.BAD_REQUEST);
    }
    
}
