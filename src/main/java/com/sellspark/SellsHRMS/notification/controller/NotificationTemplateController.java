package com.sellspark.SellsHRMS.notification.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sellspark.SellsHRMS.notification.dto.NotificationTemplatePreviewRequestDTO;
import com.sellspark.SellsHRMS.notification.dto.NotificationTemplateRequestDTO;
import com.sellspark.SellsHRMS.notification.dto.NotificationTemplateResponseDTO;
import com.sellspark.SellsHRMS.notification.service.NotificationTemplateService;
import com.sellspark.SellsHRMS.payload.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/superadmin/notification/templates")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('SUPER_ADMIN')")
public class NotificationTemplateController {

    private final NotificationTemplateService notificationTemplateService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationTemplateResponseDTO>>> getAllTemplates() {
        return ResponseEntity.ok(ApiResponse.ok("Templates fetched successfully",
                notificationTemplateService.getAllTemplates()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NotificationTemplateResponseDTO>> getTemplate(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Template fetched successfully",
                    notificationTemplateService.getTemplateById(id)));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(ex.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NotificationTemplateResponseDTO>> createTemplate(
            @Valid @RequestBody NotificationTemplateRequestDTO request) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Template created successfully",
                    notificationTemplateService.createTemplate(request)));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<NotificationTemplateResponseDTO>> updateTemplate(
            @PathVariable Long id,
            @Valid @RequestBody NotificationTemplateRequestDTO request) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Template updated successfully",
                    notificationTemplateService.updateTemplate(id, request)));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(ex.getMessage()));
        }
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<NotificationTemplateResponseDTO>> toggleTemplate(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Template status updated successfully",
                    notificationTemplateService.toggleTemplateStatus(id)));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(ex.getMessage()));
        }
    }

    @PostMapping("/{id}/preview")
    public ResponseEntity<ApiResponse<NotificationTemplateResponseDTO>> previewTemplate(
            @PathVariable Long id,
            @RequestBody(required = false) NotificationTemplatePreviewRequestDTO request) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Template preview generated successfully",
                    notificationTemplateService.previewTemplate(id, request)));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(ex.getMessage()));
        }
    }

    @PostMapping("/seed-defaults")
    public ResponseEntity<ApiResponse<List<NotificationTemplateResponseDTO>>> seedDefaults() {
        return ResponseEntity.ok(ApiResponse.ok("Default templates seeded successfully",
                notificationTemplateService.seedDefaultTemplates()));
    }
}
