package com.sellspark.SellsHRMS.exception.organisation;

import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;

public class DesignationNotFoundException extends ResourceNotFoundException {
    public DesignationNotFoundException(Long id) {
        super("Designation", "id", id);
    }
}
