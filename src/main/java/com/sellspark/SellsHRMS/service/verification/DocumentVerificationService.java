package com.sellspark.SellsHRMS.service.verification;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.sellspark.SellsHRMS.dto.verification.*;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.VerificationToken;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;
import com.sellspark.SellsHRMS.repository.VerificationTokenRepository;
import com.sellspark.SellsHRMS.utils.FileStorageUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Orchestrates all document verification steps.
 * Enforces immutability (cannot re-verify) and stores transaction IDs for
 * audit.
 * All steps now accept document file uploads stored in
 * panUrl/aadharUrl/gstUrl/tanUrl.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentVerificationService {

    private final SandboxApiService sandboxApi;
    private final NameMatchingService nameMatcher;
    private final OrganisationRepository organisationRepo;
    private final VerificationTokenRepository tokenRepo;
    private final FileStorageUtils fileStorageUtils;

    // ─── Status ─────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public VerificationStatusDTO getVerificationStatus(Long orgId) {
        Organisation org = findOrg(orgId);

        int currentStep = 1;
        if (org.isPanVerified())
            currentStep = 2;
        if (org.isAadharVerified())
            currentStep = 3;
        if (org.isGstVerified())
            currentStep = 4;
        if (org.isTanVerified())
            currentStep = 5; // all done

        return VerificationStatusDTO.builder()
                .organisationId(org.getId())
                .organisationName(org.getName())
                .pan(maskPan(org.getPan()))
                .aadhaar(maskAadhaar(org.getAadhar()))
                .gst(org.getGst())
                .tan(org.getTan())
                .panVerified(org.isPanVerified())
                .aadhaarVerified(org.isAadharVerified())
                .gstVerified(org.isGstVerified())
                .tanVerified(org.isTanVerified())
                .panUrl(org.getPanUrl())
                .aadhaarUrl(org.getAadharUrl())
                .gstUrl(org.getGstUrl())
                .tanUrl(org.getTanUrl())
                .aadhaarPhotoUrl(org.getAadhaarPhotoUrl())
                .verifiedCount(org.getVerifiedDocumentCount())
                .currentStep(currentStep)
                .build();
    }

    // ─── PAN Verification ───────────────────────────────────────────

    @Transactional
    public VerificationResponse verifyPan(Long orgId, PanVerificationRequest request, MultipartFile file) {
        Organisation org = findOrg(orgId);

        if (org.isPanVerified()) {
            return VerificationResponse.builder()
                    .verified(true).documentType("PAN")
                    .message("PAN is already verified. Cannot re-verify.")
                    .build();
        }

        // Save uploaded PAN document first
        String panDocUrl = saveDocument(file, orgId, "pan");
        if (panDocUrl != null) {
            org.setPanUrl(panDocUrl);
            log.info("[VERIFY_PAN] Document saved for org {}: {}", orgId, panDocUrl);
        }

        try {
            JsonNode result = sandboxApi.verifyPan(request.getPan(), request.getName(), request.getDateOfBirth());

            String txnId = result.path("transaction_id").asText("");
            JsonNode data = result.path("data");
            log.info("[VERIFY_PAN] Data: {}", data);

            boolean isValid = "true".equalsIgnoreCase(data.path("valid").asText())
                    || data.path("status").asText("").equalsIgnoreCase("VALID");

            String aadhaarSeeding = data.path("aadhaar_seeding_status").asText("NA");

            Map<String, Object> details = new HashMap<>();

            details.put("aadhaarSeedingStatus", aadhaarSeeding);
            details.put("panStatus", data.path("status").asText(""));
            details.put("category", data.path("category").asText(""));

            if (isValid) {
                org.setPan(request.getPan());
                org.setPanVerified(true);
                org.setPanTransactionId(txnId);
                organisationRepo.save(org);

                log.info("[VERIFY_PAN] ✅ Org {} PAN verified. TxnId: {}", orgId, txnId);

                return VerificationResponse.builder()
                        .verified(true).documentType("PAN").transactionId(txnId)
                        .message("PAN verified successfully")
                        .details(details).build();
            } else {
                // Still save the org (document URL was set)
                organisationRepo.save(org);

                String reason = !isValid ? "PAN is invalid" : "PAN is invalid";
                log.warn("[VERIFY_PAN] ❌ Org {} PAN failed: {}", orgId, reason);

                return VerificationResponse.builder()
                        .verified(false).documentType("PAN").transactionId(txnId)
                        .message(reason)
                        .details(details).build();
            }

        } catch (Exception e) {
            // Still save the document URL even if API fails
            organisationRepo.save(org);
            log.error("[VERIFY_PAN] Error for org {}: {}", orgId, e.getMessage());
            return VerificationResponse.builder()
                    .verified(false).documentType("PAN")
                    .message("Verification failed: " + e.getMessage())
                    .build();
        }
    }

    // ─── Aadhaar OTP Generation ─────────────────────────────────────

    @Transactional
    public VerificationResponse generateAadhaarOtp(Long orgId, AadhaarOtpRequest request, MultipartFile file) {
        Organisation org = findOrg(orgId);

        if (org.isAadharVerified()) {
            return VerificationResponse.builder()
                    .verified(true).documentType("AADHAAR")
                    .message("Aadhaar is already verified. Cannot re-verify.")
                    .build();
        }

        // Save uploaded Aadhaar document
        String aadhaarDocUrl = saveDocument(file, orgId, "aadhaar");
        if (aadhaarDocUrl != null) {
            org.setAadharUrl(aadhaarDocUrl);
            log.info("[VERIFY_AADHAAR] Document saved for org {}: {}", orgId, aadhaarDocUrl);
        }

        try {
            JsonNode result = sandboxApi.generateAadhaarOtp(request.getAadhaarNumber());

            String txnId = result.path("transaction_id").asText("");
            String refId = result.path("data").path("reference_id").asText(
                    result.path("data").path("ref_id").asText(
                            result.path("reference_id").asText(
                                    result.path("ref_id").asText(""))));

            org.setAadhar(request.getAadhaarNumber());
            organisationRepo.save(org);

            log.info("[VERIFY_AADHAAR] OTP generated for org {}. RefId: {}", orgId, refId);

            return VerificationResponse.builder()
                    .verified(false).documentType("AADHAAR").transactionId(txnId)
                    .refId(refId).message("OTP sent to Aadhaar-linked mobile number")
                    .build();

        } catch (Exception e) {
            organisationRepo.save(org);
            log.error("[VERIFY_AADHAAR] OTP error for org {}: {}", orgId, e.getMessage());
            return VerificationResponse.builder()
                    .verified(false).documentType("AADHAAR")
                    .message("OTP generation failed: " + e.getMessage())
                    .build();
        }
    }

    // ─── Aadhaar OTP Verification ───────────────────────────────────

    @Transactional
    public VerificationResponse verifyAadhaarOtp(Long orgId, AadhaarOtpVerifyRequest request) {
        Organisation org = findOrg(orgId);

        if (org.isAadharVerified()) {
            return VerificationResponse.builder()
                    .verified(true).documentType("AADHAAR")
                    .message("Aadhaar is already verified. Cannot re-verify.")
                    .build();
        }

        try {
            JsonNode result = sandboxApi.verifyAadhaarOtp(request.getOtp(), request.getRefId());

            String txnId = result.path("transaction_id").asText("");
            JsonNode data = result.path("data");

            // Extract photo (base64)
            String photoBase64 = data.path("photo_link").asText(
                    data.path("profile_image").asText(""));

            String photoUrl = null;
            if (photoBase64 != null && !photoBase64.isBlank()) {
                try {
                    String base64Content = photoBase64.startsWith("data:")
                            ? photoBase64
                            : "data:image/jpeg;base64," + photoBase64;
                    photoUrl = fileStorageUtils.saveBase64File(
                            base64Content, "aadhaar-photo.jpg",
                            "verification", "org-" + orgId);
                    log.info("[VERIFY_AADHAAR] Photo saved: {}", photoUrl);
                } catch (Exception photoErr) {
                    log.warn("[VERIFY_AADHAAR] Could not save photo: {}", photoErr.getMessage());
                }
            }

            Map<String, Object> details = new HashMap<>();
            details.put("name", data.path("full_name").asText(data.path("name").asText("")));
            details.put("dob", data.path("dob").asText(""));
            details.put("gender", data.path("gender").asText(""));
            details.put("address", data.path("address").asText(""));

            org.setAadharVerified(true);
            org.setAadhaarTransactionId(txnId);
            if (photoUrl != null) {
                org.setAadhaarPhotoUrl(photoUrl);
            }
            organisationRepo.save(org);

            log.info("[VERIFY_AADHAAR] ✅ Org {} Aadhaar verified. TxnId: {}", orgId, txnId);

            return VerificationResponse.builder()
                    .verified(true).documentType("AADHAAR").transactionId(txnId)
                    .message("Aadhaar verified successfully")
                    .details(details).build();

        } catch (Exception e) {
            log.error("[VERIFY_AADHAAR] Error for org {}: {}", orgId, e.getMessage());
            return VerificationResponse.builder()
                    .verified(false).documentType("AADHAAR")
                    .message("OTP verification failed: " + e.getMessage())
                    .build();
        }
    }

    // ─── GST Verification ───────────────────────────────────────────

    @Transactional
    public VerificationResponse verifyGst(Long orgId, GstVerificationRequest request, MultipartFile file) {
        Organisation org = findOrg(orgId);

        if (org.isGstVerified()) {
            return VerificationResponse.builder()
                    .verified(true).documentType("GST")
                    .message("GST is already verified. Cannot re-verify.")
                    .build();
        }

        if (org.getName() == null || org.getName().isBlank()) {
            throw new RuntimeException("Organisation name is required for GST verification");
        }

        // Save uploaded GST document
        String gstDocUrl = saveDocument(file, orgId, "gst");
        if (gstDocUrl != null) {
            org.setGstUrl(gstDocUrl);
            log.info("[VERIFY_GST] Document saved for org {}: {}", orgId, gstDocUrl);
        }

        try {
            JsonNode result = sandboxApi.searchGstin(request.getGstin());

            String txnId = result.path("transaction_id").asText("");
            JsonNode data = result.path("data").path("data");

            log.info("[VERIFY_GST] Data: {}", data);

            String tradeName = data.path("tradeNam").asText("");
            String legalName = data.path("lgnm").asText("");

            int tradeScore = nameMatcher.calculateMatchScore(org.getName(), tradeName);
            int legalScore = nameMatcher.calculateMatchScore(org.getName(), legalName);
            int bestScore = Math.max(tradeScore, legalScore);
            boolean nameMatch = bestScore >= 60;

            String gstStatus = data.path("gstin_status").asText(
                    data.path("sts").asText(""));

            Map<String, Object> details = new HashMap<>();
            details.put("tradeName", tradeName);
            details.put("legalName", legalName);
            details.put("gstStatus", gstStatus);
            details.put("nameMatchScore", bestScore);
            details.put("businessType", data.path("constitution_of_business").asText(
                    data.path("ctb").asText("")));
            details.put("registrationDate", data.path("date_of_registration").asText(
                    data.path("rgdt").asText("")));
            details.put("principalAddress", data.path("principal_place_of_business").asText(""));

            if (nameMatch) {
                org.setGst(request.getGstin());
                org.setGstVerified(true);
                org.setGstTransactionId(txnId);
                organisationRepo.save(org);

                log.info("[VERIFY_GST] ✅ Org {} GST verified. TxnId: {}", orgId, txnId);

                return VerificationResponse.builder()
                        .verified(true).documentType("GST").transactionId(txnId)
                        .nameMatchScore(bestScore).message("GST verified successfully")
                        .details(details).build();
            } else {
                organisationRepo.save(org);
                log.warn("[VERIFY_GST] ❌ Org {} name mismatch. Score: {}", orgId, bestScore);

                return VerificationResponse.builder()
                        .verified(false).documentType("GST").transactionId(txnId)
                        .nameMatchScore(bestScore)
                        .message("Business name mismatch (score: " + bestScore + "/100). Org: '"
                                + org.getName() + "' vs GST: '" + tradeName + "' / '" + legalName + "'")
                        .details(details).build();
            }

        } catch (Exception e) {
            organisationRepo.save(org);
            log.error("[VERIFY_GST] Error for org {}: {}", orgId, e.getMessage());
            return VerificationResponse.builder()
                    .verified(false).documentType("GST")
                    .message("GST verification failed: " + e.getMessage())
                    .build();
        }
    }

    // ─── TAN Manual Upload ──────────────────────────────────────────

    @Transactional
    public VerificationResponse uploadTanDocument(Long orgId, String tanNumber, MultipartFile file) {
        Organisation org = findOrg(orgId);

        if (org.isTanVerified()) {
            return VerificationResponse.builder()
                    .verified(true).documentType("TAN")
                    .message("TAN is already verified. Cannot re-upload.")
                    .build();
        }

        try {
            String fileUrl = saveDocument(file, orgId, "tan");

            org.setTan(tanNumber);
            org.setTanUrl(fileUrl);
            organisationRepo.save(org);

            log.info("[VERIFY_TAN] Document uploaded for org {}. URL: {}", orgId, fileUrl);

            return VerificationResponse.builder()
                    .verified(false).documentType("TAN")
                    .message("TAN document uploaded. Awaiting admin verification.")
                    .build();

        } catch (Exception e) {
            log.error("[VERIFY_TAN] Upload error for org {}: {}", orgId, e.getMessage());
            return VerificationResponse.builder()
                    .verified(false).documentType("TAN")
                    .message("TAN upload failed: " + e.getMessage())
                    .build();
        }
    }

    // ─── Verification Token (Resumable) ─────────────────────────────

    @Transactional
    public String generateVerificationToken(Long orgId, String email) {
        Organisation org = findOrg(orgId);

        String token = UUID.randomUUID().toString();
        VerificationToken vt = VerificationToken.builder()
                .token(token).organisation(org).email(email)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();
        tokenRepo.save(vt);

        org.setVerificationToken(token);
        org.setVerificationTokenExpiry(LocalDateTime.now().plusDays(7));
        organisationRepo.save(org);

        return token;
    }

    @Transactional(readOnly = true)
    public Organisation validateToken(String token) {
        VerificationToken vt = tokenRepo.findByTokenAndUsedFalse(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired verification token"));
        if (vt.isExpired()) {
            throw new RuntimeException("Verification token has expired");
        }
        return vt.getOrganisation();
    }

    // ─── Helpers ────────────────────────────────────────────────────

    /**
     * Saves a document file to storage under verification/org-{orgId}/{docType}/.
     * Returns the file URL or null if file is empty.
     */
    private String saveDocument(MultipartFile file, Long orgId, String docType) {
        if (file == null || file.isEmpty())
            return null;
        try {
            return fileStorageUtils.saveFile(file, "verification", "org-" + orgId + "/" + docType);
        } catch (Exception e) {
            log.error("[SAVE_DOC] Failed to save {} for org {}: {}", docType, orgId, e.getMessage());
            return null;
        }
    }

    private Organisation findOrg(Long orgId) {
        return organisationRepo.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organisation not found: " + orgId));
    }

    private String maskPan(String pan) {
        if (pan == null || pan.length() < 4)
            return pan;
        return pan.substring(0, 2) + "****" + pan.substring(pan.length() - 2);
    }

    private String maskAadhaar(String aadhaar) {
        if (aadhaar == null || aadhaar.length() < 4)
            return aadhaar;
        return "****-****-" + aadhaar.substring(aadhaar.length() - 4);
    }
}
