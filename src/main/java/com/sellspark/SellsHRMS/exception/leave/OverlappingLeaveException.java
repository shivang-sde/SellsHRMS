package com.sellspark.SellsHRMS.exception.leave;

import org.springframework.http.HttpStatus;

import com.sellspark.SellsHRMS.exception.core.HRMSException;

public class OverlappingLeaveException extends HRMSException {
    public OverlappingLeaveException() {
        super(
                "You already have a leave application for the selected dates",
                "OVERLAPPING_LEAVE",
                HttpStatus.CONFLICT);
    }
}