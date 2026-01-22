package com.sellspark.SellsHRMS.exception;

import org.springframework.http.HttpStatus;

public class EmployeeInactiveException extends HRMSException {
    public EmployeeInactiveException(String employeeCode) {
        super(
            String.format("Employee %s is inactive and cannot perform this action", employeeCode),
            "EMPLOYEE_INACTIVE",
            HttpStatus.FORBIDDEN
        );
    }
}
