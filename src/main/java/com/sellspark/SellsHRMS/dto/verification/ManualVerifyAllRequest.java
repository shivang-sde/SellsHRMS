package com.sellspark.SellsHRMS.dto.verification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request DTO for manually verifying all documents for an organisation.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManualVerifyAllRequest {

    /**
     * Optional reason/comment for the bulk manual verification.
     * Useful for audit trail.
     */
    private String verificationNote;
}
