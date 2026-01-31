package com.sellspark.SellsHRMS.exception.organisation;

import org.springframework.http.HttpStatus;

import com.sellspark.SellsHRMS.exception.core.HRMSException;

public class OrganisationMismatchException extends HRMSException {
    public OrganisationMismatchException() {
        super(
                "You can only access resources from your own organization",
                "ORGANIZATION_MISMATCH",
                HttpStatus.FORBIDDEN);
    }
}
