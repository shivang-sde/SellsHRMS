package com.sellspark.SellsHRMS.exception;

import org.springframework.http.HttpStatus;

public class LeaveAlreadyProcessedException extends HRMSException {
    public LeaveAlreadyProcessedException(String status) {
        super(
            String.format("Leave has already been %s and cannot be modified", status),
            "LEAVE_ALREADY_PROCESSED",
            HttpStatus.CONFLICT
        );
    }
}
