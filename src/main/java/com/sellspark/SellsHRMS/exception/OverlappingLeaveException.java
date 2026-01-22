package com.sellspark.SellsHRMS.exception;

import org.springframework.http.HttpStatus;

public class OverlappingLeaveException extends HRMSException {
    public OverlappingLeaveException() {
        super(
            "You already have a leave application for the selected dates",
            "OVERLAPPING_LEAVE",
            HttpStatus.CONFLICT
        );
    }
}