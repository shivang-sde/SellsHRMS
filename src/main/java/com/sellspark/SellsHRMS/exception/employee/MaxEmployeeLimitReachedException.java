package com.sellspark.SellsHRMS.exception.employee;

import org.springframework.http.HttpStatus;

import com.sellspark.SellsHRMS.exception.core.HRMSException;

public class MaxEmployeeLimitReachedException extends HRMSException {
    public MaxEmployeeLimitReachedException(int maxLimit) {
        super(
                String.format("Maximum employee limit (%d) has been reached for this organization", maxLimit),
                "MAX_EMPLOYEE_LIMIT_REACHED",
                HttpStatus.FORBIDDEN);
    }
}