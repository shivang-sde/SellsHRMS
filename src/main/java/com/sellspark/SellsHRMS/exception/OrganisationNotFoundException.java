package com.sellspark.SellsHRMS.exception;

public class OrganisationNotFoundException extends ResourceNotFoundException {
    public OrganisationNotFoundException(Long id) {
        super("Organisation", "id", id);
    }
}
