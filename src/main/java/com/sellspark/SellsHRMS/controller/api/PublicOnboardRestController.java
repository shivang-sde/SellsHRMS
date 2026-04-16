package com.sellspark.SellsHRMS.controller.api;

import com.sellspark.SellsHRMS.dto.organisation.OrganisationDTO;
import com.sellspark.SellsHRMS.service.OrganisationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.sellspark.SellsHRMS.service.files.FileUploadService;
import com.sellspark.SellsHRMS.dto.files.FileUploadResponseDTO;

import java.time.LocalDate;
import java.util.Map;
import java.util.List;

/**
 * Public REST controller for self-service organisation onboarding.
 * No authentication or role checks — accessible to anyone.
 */
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@Slf4j
public class PublicOnboardRestController {

    private final OrganisationService organisationService;
    private final FileUploadService fileUploadService;

    /**
     * Self-service onboarding endpoint.
     * Creates an organisation + admin user with fixed trial defaults:
     *   - validity  = 6 months from today
     *   - maxEmployees = 20
     */
    @PostMapping("/onboard")
    public ResponseEntity<?> onboard(@RequestBody OrganisationDTO dto) {
        log.info("Public onboarding request for org: {}, admin: {}", dto.getName(), dto.getAdminEmail());

        // ── Validate required fields ─────────────────────────
        if (dto.getName() == null || dto.getName().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Organisation name is required"));
        }
        if (dto.getAdminEmail() == null || dto.getAdminEmail().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Admin email is required"));
        }
        if (dto.getAdminPassword() == null || dto.getAdminPassword().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Admin password is required"));
        }
        if (dto.getAdminFullName() == null || dto.getAdminFullName().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Admin full name is required"));
        }
        if (dto.getPrefix() == null || dto.getPrefix().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Employee code prefix is required"));
        }
        if (dto.getDomain() == null || dto.getDomain().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Organisation domain is required"));
        }

        // ── Check prefix uniqueness ──────────────────────────
        if (organisationService.existsByEmpPrefix(dto.getPrefix())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Prefix '" + dto.getPrefix() + "' is already taken. Please choose another."));
        }

        // ── Enforce hidden trial defaults ────────────────────
        dto.setMaxEmployees(20);
        dto.setValidity(LocalDate.now().plusMonths(6));

        // ── Default padding if not provided ──────────────────
        if (dto.getPadding() == null || dto.getPadding() < 3) {
            dto.setPadding(3);
        }

        try {
            OrganisationDTO created = organisationService.create(dto);
            log.info("Public onboarding complete — org ID: {}", created.getId());
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Organisation created successfully",
                "organisationId", created.getId()
            ));
        } catch (Exception e) {
            log.error("Public onboarding failed", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "message", "Failed to create organisation: " + e.getMessage()
            ));
        }
    }

    /**
     * Public prefix uniqueness check (used by the onboard form).
     */
    @GetMapping("/prefix/{empPrefix}")
    public ResponseEntity<Boolean> checkPrefixPublic(@PathVariable String empPrefix) {
        boolean exists = organisationService.existsByEmpPrefix(empPrefix);
        return ResponseEntity.ok(exists);
    }

    /**
     * Public upload endpoint strictly for capturing logo directly on the onboarding page.
     */
    @PostMapping(value = "/upload-logo", consumes = {"multipart/form-data"})
    public ResponseEntity<?> uploadLogo(@RequestParam("file") MultipartFile file) {
        try {
            List<FileUploadResponseDTO> result = fileUploadService.uploadMultipartFiles(
                    List.of(file), "organisation-logos", "public-onboard"
            );
            if (result != null && !result.isEmpty()) {
                return ResponseEntity.ok(Map.of("url", result.get(0).getFileUrl()));
            }
            return ResponseEntity.badRequest().body(Map.of("message", "Could not upload file"));
        } catch (Exception e) {
            log.error("Failed to upload public logo", e);
            return ResponseEntity.internalServerError().body(Map.of("message", "Upload failed"));
        }
    }
}
