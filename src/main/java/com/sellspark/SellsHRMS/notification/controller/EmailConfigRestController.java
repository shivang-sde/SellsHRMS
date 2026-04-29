package com.sellspark.SellsHRMS.notification.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sellspark.SellsHRMS.payload.ApiResponse;
import com.sellspark.SellsHRMS.notification.dto.EmailConfigDTO;
import com.sellspark.SellsHRMS.notification.service.EmailConfigService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications/email-config")
@RequiredArgsConstructor
public class EmailConfigRestController {

    private final EmailConfigService emailConfigService;

    @PostMapping
    public ResponseEntity<ApiResponse<EmailConfigDTO>> saveEmailConfig(@RequestBody EmailConfigDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Email config saved successfully", emailConfigService.saveConfig(dto)));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<EmailConfigDTO>> updateEmailConfig(@RequestBody EmailConfigDTO dto) {
        return ResponseEntity
                .ok(ApiResponse.ok("Email config updated successfully", emailConfigService.updateConfig(dto)));
    }

    @PostMapping("/test")
    public ResponseEntity<ApiResponse<Boolean>> testEmailConfig(@RequestBody EmailConfigDTO dto) {
        boolean testResult = emailConfigService.testSmtpConnection(dto);
        return ResponseEntity.ok(ApiResponse.ok("SMTP connection test " + (testResult ? "successful" : "failed"),
                testResult));
    }

    @GetMapping("/org/{orgId}")
    public ResponseEntity<ApiResponse<EmailConfigDTO>> getActiveEmailConfig(@PathVariable Long orgId) {
        try {
            return ResponseEntity
                    .ok(ApiResponse.ok("Email config fetched successfully", emailConfigService.getActiveConfig(orgId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail("No SMTP Configuration Found."));
        }
    }

}
