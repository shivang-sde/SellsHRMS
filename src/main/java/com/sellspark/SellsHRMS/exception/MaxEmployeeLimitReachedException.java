package com.sellspark.SellsHRMS.exception;

import org.springframework.http.HttpStatus;

public class MaxEmployeeLimitReachedException extends HRMSException {
    public MaxEmployeeLimitReachedException(int maxLimit) {
        super(
            String.format("Maximum employee limit (%d) has been reached for this organization", maxLimit),
            "MAX_EMPLOYEE_LIMIT_REACHED",
            HttpStatus.FORBIDDEN
        );
    }
}