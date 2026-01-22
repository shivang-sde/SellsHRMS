package com.sellspark.SellsHRMS.exception;

public class EmployeeNotFoundException extends ResourceNotFoundException {
    public EmployeeNotFoundException(Long id) {
        super("Employee", "id", id);
    }

    public EmployeeNotFoundException(String code) {
        super("Employee", "code", code);
    }
}
