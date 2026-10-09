package com.sellspark.SellsHRMS.dto.verification;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response DTO for manual verification operations.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManualVerificationResponse {

    private boolean success;
    private String message;
    private DocumentType documentType;
    private List<DocumentType> verifiedDocuments;
    private Map<DocumentType, Boolean> verificationResults;
    private Long organisationId;
    private String organisationName;

    public static ManualVerificationResponse successForDocument(
            Long orgId, String orgName, DocumentType docType, String message) {
        return ManualVerificationResponse.builder()
                .success(true)
                .message(message)
                .documentType(docType)
                .organisationId(orgId)
                .organisationName(orgName)
                .build();
    }

    public static ManualVerificationResponse successForAll(
            Long orgId, String orgName, List<DocumentType> verifiedDocs, String message) {
        return ManualVerificationResponse.builder()
                .success(true)
                .message(message)
                .verifiedDocuments(verifiedDocs)
                .organisationId(orgId)
                .organisationName(orgName)
                .build();
    }

    public static ManualVerificationResponse failure(String message) {
        return ManualVerificationResponse.builder()
                .success(false)
                .message(message)
                .build();
    }
}
