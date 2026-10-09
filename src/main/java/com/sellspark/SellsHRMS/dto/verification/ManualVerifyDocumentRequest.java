package com.sellspark.SellsHRMS.dto.verification;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request DTO for manually verifying a single document.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManualVerifyDocumentRequest {

    @NotNull(message = "Document type is required")
    private DocumentType documentType;

    /**
     * Optional reason/comment for the manual verification.
     * Useful for audit trail.
     */
    private String verificationNote;
}
