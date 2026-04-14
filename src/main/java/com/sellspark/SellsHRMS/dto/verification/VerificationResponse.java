package com.sellspark.SellsHRMS.dto.verification;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

/**
 * Unified response DTO for all document verification steps.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VerificationResponse {
    private boolean verified;
    private String documentType; // PAN, AADHAAR, GST, TAN
    private String transactionId;
    private String message;
    private String refId;        // Aadhaar OTP flow ref_id
    private Integer nameMatchScore; // fuzzy matching score (0-100)
    private Map<String, Object> details; // API response details
}
