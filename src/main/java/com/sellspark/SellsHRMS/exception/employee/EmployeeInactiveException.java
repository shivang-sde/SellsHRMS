package com.sellspark.SellsHRMS.exception.employee;

import org.springframework.http.HttpStatus;

import com.sellspark.SellsHRMS.exception.core.HRMSException;

public class EmployeeInactiveException extends HRMSException {
    public EmployeeInactiveException(String employeeCode) {
        super(
                String.format("Employee %s is inactive and cannot perform this action", employeeCode),
                "EMPLOYEE_INACTIVE",
                HttpStatus.FORBIDDEN);
    }
}
