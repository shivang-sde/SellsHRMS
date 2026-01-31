package com.sellspark.SellsHRMS.exception.organisation;

import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;

public class OrganisationNotFoundException extends ResourceNotFoundException {
    public OrganisationNotFoundException(Long id) {
        super("Organisation", "id", id);
    }
}
