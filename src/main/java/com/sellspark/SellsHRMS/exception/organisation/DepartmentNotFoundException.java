package com.sellspark.SellsHRMS.exception.organisation;

import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;

public class DepartmentNotFoundException extends ResourceNotFoundException {
    public DepartmentNotFoundException(Long id) {
        super("Department", "id", id);
    }
}
