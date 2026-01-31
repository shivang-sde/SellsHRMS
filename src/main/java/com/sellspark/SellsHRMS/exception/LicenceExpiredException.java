package com.sellspark.SellsHRMS.exception;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;

import com.sellspark.SellsHRMS.exception.core.HRMSException;

public class LicenceExpiredException extends HRMSException {
    public LicenceExpiredException(LocalDate expiryDate) {
        super(
                String.format("Your organisation license expired on %s. Please contact support to renew.", expiryDate),
                "ORG_LICENSE_EXPIRED",
                HttpStatus.FORBIDDEN);
    }
}
