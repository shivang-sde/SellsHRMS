package com.sellspark.SellsHRMS.controller.api.auth;

import com.sellspark.SellsHRMS.dto.common.ApiResponse;
import com.sellspark.SellsHRMS.service.auth.OtpService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling OTP-based authentication flow.
 * This version works with the internal OTP simulation,
 * but can easily switch to 3rd party OTP platform later.
 */
@RestController
@RequestMapping("/api/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;

    /**
     * Generate OTP for an identifier (email or phone)
     */
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<String>> generateOtp(@RequestParam String identifier) {
        String otp = otpService.generateOtp(identifier);
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("OTP generated successfully")
                        .data(otp)
                        .build());
    }

    /**
     * Validate OTP entered by user
     */
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<Boolean>> validateOtp(
            @RequestParam String identifier,
            @RequestParam String otpCode) {
        boolean valid = otpService.validateOtp(identifier, otpCode);
        return ResponseEntity.ok(
                ApiResponse.<Boolean>builder()
                        .success(true)
                        .message(valid ? "OTP verified successfully" : "Invalid OTP")
                        .data(valid)
                        .build());
    }
}
