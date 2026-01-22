package com.sellspark.SellsHRMS.exception;

import org.springframework.http.HttpStatus;

public class OrganisationMismatchException extends HRMSException {
    public OrganisationMismatchException() {
        super(
            "You can only access resources from your own organization",
            "ORGANIZATION_MISMATCH",
            HttpStatus.FORBIDDEN
        );
    }
} 
