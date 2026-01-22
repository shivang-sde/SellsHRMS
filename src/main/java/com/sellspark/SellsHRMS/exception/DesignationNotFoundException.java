package com.sellspark.SellsHRMS.exception;

public class DesignationNotFoundException extends ResourceNotFoundException {
    public DesignationNotFoundException(Long id) {
        super("Designation", "id", id);
    }
}
