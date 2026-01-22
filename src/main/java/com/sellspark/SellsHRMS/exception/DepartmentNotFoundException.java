package com.sellspark.SellsHRMS.exception;

public class DepartmentNotFoundException extends ResourceNotFoundException {
    public DepartmentNotFoundException(Long id) {
        super("Department", "id", id);
    }
}
