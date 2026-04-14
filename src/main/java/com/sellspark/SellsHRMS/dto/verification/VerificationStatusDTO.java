package com.sellspark.SellsHRMS.dto.verification;

import lombok.*;

/**
 * DTO containing verification status for all document types in the organisation.
 * Used to populate the wizard UI with current state.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationStatusDTO {
    private Long organisationId;
    private String organisationName;

    // Document numbers (masked for display)
    private String pan;
    private String aadhaar;
    private String gst;
    private String tan;

    // Verification statuses
    private boolean panVerified;
    private boolean aadhaarVerified;
    private boolean gstVerified;
    private boolean tanVerified;

    // Document URLs
    private String panUrl;
    private String aadhaarUrl;
    private String gstUrl;
    private String tanUrl;
    private String aadhaarPhotoUrl;

    // Count for access gate
    private int verifiedCount;

    // Current step the wizard should show
    private int currentStep;
}
