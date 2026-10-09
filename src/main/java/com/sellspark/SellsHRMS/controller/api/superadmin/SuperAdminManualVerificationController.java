package com.sellspark.SellsHRMS.controller.api.superadmin;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.sellspark.SellsHRMS.config.UserPrincipal;
import com.sellspark.SellsHRMS.dto.common.ApiResponse;
import com.sellspark.SellsHRMS.dto.verification.DocumentType;
import com.sellspark.SellsHRMS.dto.verification.ManualVerificationResponse;
import com.sellspark.SellsHRMS.service.verification.DocumentVerificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller for Super Admin manual KYC verification operations.
 * Provides endpoints to manually verify individual documents or all documents
 * for an organisation.
 * 
 * All endpoints require SUPER_ADMIN authority and are secured with Spring Security.
 */
@Slf4j
@RestController
@RequestMapping("/api/superadmin/organisation/{organisationId}/documents")
@RequiredArgsConstructor
public class SuperAdminManualVerificationController {

    private final DocumentVerificationService verificationService;

    /**
     * Manually verify a single document for an organisation.
     * 
     * PUT /api/superadmin/organisation/{organisationId}/documents/{documentType}/verify
     * 
     * Supported document types: PAN, AADHAAR, GST, TAN
     * 
     * @param organisationId The organisation ID
     * @param documentType The document type to verify
     * @param request Optional request body with verification note
     * @return ResponseEntity with ManualVerificationResponse
     */
    @PutMapping("/{documentType}/verify")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ManualVerificationResponse>> verifyDocument(
            @PathVariable Long organisationId,
            @PathVariable String documentType,
            @RequestBody(required = false) com.sellspark.SellsHRMS.dto.verification.ManualVerifyDocumentRequest request) {
        
        try {
            // Parse and validate document type
            DocumentType docType = parseDocumentType(documentType);
            
            // Get the acting Super Admin's email from the authentication
            String actingUserEmail = getCurrentUserEmail();
            
            String verificationNote = request != null ? request.getVerificationNote() : null;
            
            log.info("[API] Manual verification request: org={}, doc={}, user={}", 
                    organisationId, docType, actingUserEmail);
            
            ManualVerificationResponse response = verificationService.manuallyVerifyDocument(
                    organisationId, docType, actingUserEmail, verificationNote);
            
            return ResponseEntity.ok(ApiResponse.ok(response.getMessage(), response));
            
        } catch (IllegalArgumentException e) {
            log.warn("[API] Invalid document type: {}", documentType);
            return ResponseEntity.badRequest().body(ApiResponse.error(
                    "Invalid document type: " + documentType + ". Supported types: PAN, AADHAAR, GST, TAN"));
        } catch (RuntimeException e) {
            log.error("[API] Manual verification failed for org {}: {}", organisationId, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Manually verify all documents for an organisation.
     * 
     * PUT /api/superadmin/organisation/{organisationId}/documents/verify-all
     * 
     * This is an atomic operation - if any document fails validation,
     * no changes are made.
     * 
     * @param organisationId The organisation ID
     * @param request Optional request body with verification note
     * @return ResponseEntity with ManualVerificationResponse
     */
    @PutMapping("/verify-all")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ManualVerificationResponse>> verifyAllDocuments(
            @PathVariable Long organisationId,
            @RequestBody(required = false) com.sellspark.SellsHRMS.dto.verification.ManualVerifyAllRequest request) {
        
        try {
            // Get the acting Super Admin's email from the authentication
            String actingUserEmail = getCurrentUserEmail();
            
            String verificationNote = request != null ? request.getVerificationNote() : null;
            
            log.info("[API] Manual verify-all request: org={}, user={}", 
                    organisationId, actingUserEmail);
            
            ManualVerificationResponse response = verificationService.manuallyVerifyAllDocuments(
                    organisationId, actingUserEmail, verificationNote);
            
            return ResponseEntity.ok(ApiResponse.ok(response.getMessage(), response));
            
        } catch (RuntimeException e) {
            log.error("[API] Manual verify-all failed for org {}: {}", organisationId, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // ─── Helpers ─────────────────────────────────────────────────────

    /**
     * Parse string document type to DocumentType enum.
     */
    private DocumentType parseDocumentType(String documentType) {
        try {
            return DocumentType.valueOf(documentType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid document type: " + documentType);
        }
    }

    /**
     * Get the current authenticated user's email.
     */
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }
        
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getEmail();
        } else if (principal instanceof org.springframework.security.core.userdetails.User springUser) {
            return springUser.getUsername();
        } else if (principal instanceof String) {
            return (String) principal;
        } else {
            throw new RuntimeException("Unable to determine user email from authentication");
        }
    }
}
