package com.sellspark.SellsHRMS.notification.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sellspark.SellsHRMS.payload.ApiResponse;
import com.sellspark.SellsHRMS.notification.dto.EmailRequestDTO;
import com.sellspark.SellsHRMS.notification.service.EmailService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/notification/email")
@RequiredArgsConstructor
public class EmailRestController {

    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Void>> sendEmail(@RequestBody EmailRequestDTO request) {
        try {
            emailService.sendEmailSync(request);
            return ResponseEntity.ok(ApiResponse.ok("Email sent successfully", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail("Failed to send email: " + e.getMessage()));
        }
    }

}
