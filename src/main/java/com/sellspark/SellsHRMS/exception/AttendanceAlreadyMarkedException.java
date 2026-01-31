package com.sellspark.SellsHRMS.exception;

import org.springframework.http.HttpStatus;

import com.sellspark.SellsHRMS.exception.core.HRMSException;

public class AttendanceAlreadyMarkedException extends HRMSException {
    public AttendanceAlreadyMarkedException(String action) {
        super(
                String.format("Already %s for today", action),
                "ATTENDANCE_ALREADY_MARKED",
                HttpStatus.CONFLICT);
    }
}
