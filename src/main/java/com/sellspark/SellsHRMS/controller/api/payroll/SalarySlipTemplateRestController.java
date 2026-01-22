package com.sellspark.SellsHRMS.controller.api.payroll;

import com.sellspark.SellsHRMS.dto.payroll.SalarySlipTemplateDTO;
import com.sellspark.SellsHRMS.dto.payroll.TemplatePreviewRequest;
import com.sellspark.SellsHRMS.service.payroll.SalarySlipTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/salary-slip-template")
@RequiredArgsConstructor
public class SalarySlipTemplateRestController {

    private final SalarySlipTemplateService templateService;

    // ───────────────────────────────────────────────
    // Get all templates
    // ───────────────────────────────────────────────
    @GetMapping("/{orgId}/list")
public ResponseEntity<?> getAllTemplates(@PathVariable Long orgId) {
    try {
        if (orgId == null) throw new RuntimeException("User not authenticated");
        List<SalarySlipTemplateDTO> templates = templateService.getAllTemplates(orgId);
        return ResponseEntity.ok(Map.of("success", true, "data", templates));
    } catch (Exception ex) {
        log.error("Error fetching templates list", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("success", false, "error", ex.getMessage()));
    }
}

    // ───────────────────────────────────────────────
    // Get template by ID
    // ───────────────────────────────────────────────
    @GetMapping("/{orgId}/template/{id}")
    public ResponseEntity<?> getTemplateById(@PathVariable Long orgId, @PathVariable Long id) {
        try {
            if (orgId == null) throw new RuntimeException("User not authenticated");
            SalarySlipTemplateDTO dto = templateService.getTemplateById(id, orgId);
            if (dto == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "error", "Template not found"));
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                  "data", dto
                ));
        } catch (Exception ex) {
                log.error("Error fetching template by ID", ex);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of(
                            "success", false,
                            "error", "An unexpected error occurred. Please try again later or contact support."
                        ));
        }
    }


    // ───────────────────────────────────────────────
    // Get default template
    // ───────────────────────────────────────────────
    @GetMapping("/{orgId}/default")
    public ResponseEntity<?> getDefaultTemplate(@PathVariable Long orgId) {
        try {
            if (orgId == null) throw new RuntimeException("User not authenticated");
            SalarySlipTemplateDTO dto = templateService.getDefaultTemplate(orgId);
            return ResponseEntity.ok(dto);
        } catch (Exception ex) {
            log.error("Error fetching default template", ex);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    // ───────────────────────────────────────────────
    // Save or update template
    // ───────────────────────────────────────────────
    @PostMapping("/{orgId}/save")
    public ResponseEntity<?> saveTemplate(@PathVariable Long orgId, @RequestBody SalarySlipTemplateDTO templateDTO) {
        try {
            if (orgId == null) throw new RuntimeException("User not authenticated");
            templateDTO.setOrgId(orgId);
            SalarySlipTemplateDTO saved = templateService.saveTemplate(templateDTO, orgId);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception ex) {
            log.error("Error saving template", ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    // ───────────────────────────────────────────────
    // Delete template
    // ───────────────────────────────────────────────
    @DeleteMapping("/{orgId}/template/{id}")
    public ResponseEntity<?> deleteTemplate(@PathVariable Long orgId, @PathVariable Long id) {
        try {
            if (orgId == null) throw new RuntimeException("User not authenticated");
            boolean deleted = templateService.deleteTemplate(id, orgId);
            return ResponseEntity.ok(Map.of("deleted", deleted));
        } catch (Exception ex) {
            log.error("Error deleting template", ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    // ───────────────────────────────────────────────
    // Set template as default
    // ───────────────────────────────────────────────
    @PutMapping("/{orgId}/template/{id}/set-default")
    public ResponseEntity<?> setAsDefault(@PathVariable Long orgId, @PathVariable Long id) {
        try {
            if (orgId == null) throw new RuntimeException("User not authenticated");
            boolean result = templateService.setAsDefault(id, orgId);
            return ResponseEntity.ok(Map.of("success", result));
        } catch (Exception ex) {
            log.error("Error setting template as default", ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    // ───────────────────────────────────────────────
    // Generate preview with mock data
    // ───────────────────────────────────────────────
    @PostMapping("/{orgId}/preview")
    public ResponseEntity<?> generatePreview(@PathVariable Long orgId, @RequestBody TemplatePreviewRequest request) {
        try {
            if (orgId == null) throw new RuntimeException("User not authenticated");
            String html = templateService.generatePreview(request, orgId);
            return ResponseEntity.ok(Map.of("success", true, "data", html));
        } catch (Exception ex) {
            log.error("Error generating preview", ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "error", ex.getMessage()));
        }
    }

    // ───────────────────────────────────────────────
    // Get mock data for preview
    // ───────────────────────────────────────────────
    @GetMapping("/{orgId}/mock-data")
    public ResponseEntity<?> getMockData(@PathVariable Long orgId) {
        try {
            if (orgId == null) throw new RuntimeException("User not authenticated");
            Map<String, Object> mockData = templateService.getMockData(orgId);
            return ResponseEntity.ok(Map.of("success", true, "data", mockData));
        } catch (Exception ex) {
            log.error("Error getting mock data", ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "error", ex.getMessage()));
        }
    }

    // ───────────────────────────────────────────────
    // Get available fields for template designer
    // ───────────────────────────────────────────────
    @GetMapping("/{orgId}/available-fields")
    public ResponseEntity<?> getAvailableFields(@PathVariable Long orgId) {
        try {
            if (orgId == null) throw new RuntimeException("User not authenticated");
            Map<String, List<Map<String, String>>> fields = templateService.getAvailableFields(orgId);
            return ResponseEntity.ok(Map.of("success", true, "data", fields));
        } catch (Exception ex) {
            log.error("Error getting available fields", ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "error", ex.getMessage()));
        }
    }

    // ───────────────────────────────────────────────
    // Upload logo
    // ───────────────────────────────────────────────
    @PostMapping("/{orgId}/upload-logo")
    public ResponseEntity<?> uploadLogo(@PathVariable Long orgId, @RequestParam("file") MultipartFile file) {
        try {
            if (orgId == null) throw new RuntimeException("User not authenticated");
            String url = templateService.uploadLogo(orgId, file);
            return ResponseEntity.ok(Map.of("success", true, "data", url));
        } catch (Exception ex) {
            log.error("Error uploading logo", ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "error", ex.getMessage()));
        }
    }

    // ───────────────────────────────────────────────
    // Render actual salary slip
    // ───────────────────────────────────────────────
    @GetMapping("/{orgId}/render/{salarySlipId}")
    public ResponseEntity<?> renderSalarySlip(@PathVariable Long orgId, @PathVariable Long salarySlipId) {
        try {
            if (orgId == null) throw new RuntimeException("User not authenticated");
            String html = templateService.renderSalarySlip(salarySlipId, orgId);
            return ResponseEntity.ok(Map.of("success", true, "data", html));
        } catch (Exception ex) {
            log.error("Error rendering salary slip", ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "error", ex.getMessage()));
        }
    }

    // ───────────────────────────────────────────────
    // Generate PDF for salary slip
    // ───────────────────────────────────────────────
    @GetMapping("/{orgId}/pdf/{salarySlipId}")
    public ResponseEntity<?> generatePDF(@PathVariable Long orgId, @PathVariable Long salarySlipId) {
        try {
            if (orgId == null) throw new RuntimeException("User not authenticated");
            byte[] pdf = templateService.generatePDF(salarySlipId, orgId);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "inline; filename=salary-slip.pdf")
                    .body(pdf);
        } catch (Exception ex) {
            log.error("Error generating PDF", ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "error", ex.getMessage()));
        }
    }
}
