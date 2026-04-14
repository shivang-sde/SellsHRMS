package com.sellspark.SellsHRMS.controller.api.verification;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.sellspark.SellsHRMS.dto.common.ApiResponse;
import com.sellspark.SellsHRMS.dto.verification.*;
import com.sellspark.SellsHRMS.service.verification.DocumentVerificationService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST API for document verification.
 * All endpoints accept multipart (form fields + document file upload).
 */
@Slf4j
@RestController
@RequestMapping("/api/verify")
@RequiredArgsConstructor
public class DocumentVerificationRestController {

    private final DocumentVerificationService verificationService;

    // ─── Status ─────────────────────────────────────────────────────

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<VerificationStatusDTO>> getStatus(HttpSession session) {
        Long orgId = getOrgId(session);
        VerificationStatusDTO status = verificationService.getVerificationStatus(orgId);
        return ResponseEntity.ok(ApiResponse.ok("Verification status retrieved", status));
    }

    // ─── PAN Verification (multipart: pan, name, dateOfBirth, file) ──

    @PostMapping(value = "/pan", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ApiResponse<VerificationResponse>> verifyPan(
            @RequestParam("pan") String pan,
            @RequestParam("name") String name,
            @RequestParam(value = "dateOfBirth", required = false) String dateOfBirth,
            @RequestParam("file") MultipartFile file,
            HttpSession session) {
        Long orgId = getOrgId(session);
        log.info("[API] PAN verification for org {} with document upload", orgId);

        PanVerificationRequest request = PanVerificationRequest.builder()
                .pan(pan).name(name).dateOfBirth(dateOfBirth).build();

        VerificationResponse response = verificationService.verifyPan(orgId, request, file);
        return ResponseEntity.ok(ApiResponse.ok(response.getMessage(), response));
    }

    // ─── Aadhaar OTP Generation (multipart: aadhaarNumber, file) ─────

    @PostMapping(value = "/aadhaar/otp", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ApiResponse<VerificationResponse>> generateAadhaarOtp(
            @RequestParam("aadhaarNumber") String aadhaarNumber,
            @RequestParam("file") MultipartFile file,
            HttpSession session) {
        Long orgId = getOrgId(session);
        log.info("[API] Aadhaar OTP generation for org {} with document upload", orgId);

        AadhaarOtpRequest request = AadhaarOtpRequest.builder()
                .aadhaarNumber(aadhaarNumber).build();

        VerificationResponse response = verificationService.generateAadhaarOtp(orgId, request, file);
        return ResponseEntity.ok(ApiResponse.ok(response.getMessage(), response));
    }

    // ─── Aadhaar OTP Verification (JSON — no file needed) ───────────

    @PostMapping("/aadhaar/verify")
    public ResponseEntity<ApiResponse<VerificationResponse>> verifyAadhaarOtp(
            @RequestBody AadhaarOtpVerifyRequest request, HttpSession session) {
        Long orgId = getOrgId(session);
        log.info("[API] Aadhaar OTP verification for org {}", orgId);
        VerificationResponse response = verificationService.verifyAadhaarOtp(orgId, request);
        return ResponseEntity.ok(ApiResponse.ok(response.getMessage(), response));
    }

    // ─── GST Verification (multipart: gstin, file) ──────────────────

    @PostMapping(value = "/gst", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ApiResponse<VerificationResponse>> verifyGst(
            @RequestParam("gstin") String gstin,
            @RequestParam("file") MultipartFile file,
            HttpSession session) {
        Long orgId = getOrgId(session);
        log.info("[API] GST verification for org {} with document upload", orgId);

        GstVerificationRequest request = GstVerificationRequest.builder()
                .gstin(gstin).build();

        VerificationResponse response = verificationService.verifyGst(orgId, request, file);
        return ResponseEntity.ok(ApiResponse.ok(response.getMessage(), response));
    }

    // ─── TAN Manual Upload (multipart: tan, file) ───────────────────

    @PostMapping(value = "/tan", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ApiResponse<VerificationResponse>> uploadTan(
            @RequestParam("file") MultipartFile file,
            @RequestParam("tan") String tanNumber,
            HttpSession session) {
        Long orgId = getOrgId(session);
        log.info("[API] TAN upload for org {}", orgId);
        VerificationResponse response = verificationService.uploadTanDocument(orgId, tanNumber, file);
        return ResponseEntity.ok(ApiResponse.ok(response.getMessage(), response));
    }

    // ─── Generate Verification Token (for email resume) ─────────────

    @PostMapping("/token/generate")
    public ResponseEntity<ApiResponse<String>> generateToken(HttpSession session) {
        Long orgId = getOrgId(session);
        String email = (String) session.getAttribute("EMAIL");
        String token = verificationService.generateVerificationToken(orgId, email);
        return ResponseEntity.ok(ApiResponse.ok("Verification token generated", token));
    }

    // ─── Helper ─────────────────────────────────────────────────────

    private Long getOrgId(HttpSession session) {
        Long orgId = (Long) session.getAttribute("ORG_ID");
        if (orgId == null) {
            throw new RuntimeException("Organisation not found in session");
        }
        return orgId;
    }
}
