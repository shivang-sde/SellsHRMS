package com.sellspark.SellsHRMS.notification.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sellspark.SellsHRMS.notification.dto.NotificationEventDTO;
import com.sellspark.SellsHRMS.notification.dto.NotificationPreferenceDTO;
import com.sellspark.SellsHRMS.notification.entity.NotificationEvent;
import com.sellspark.SellsHRMS.notification.enums.TargetRole;
import com.sellspark.SellsHRMS.notification.service.NotificationService;
import com.sellspark.SellsHRMS.payload.ApiResponse;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor

@RequestMapping("/api/notifications")
public class NotificationRestController {

    private NotificationService notificationService;

    @PostMapping("/preference")
    public ResponseEntity<ApiResponse<NotificationPreferenceDTO>> setPreference(
            @RequestBody NotificationPreferenceDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Preference set successfully", notificationService.setPreference(dto)));
    }

    @GetMapping("/preferences/{orgId}")
    public ResponseEntity<ApiResponse<List<NotificationPreferenceDTO>>> getPreferences(@PathVariable Long orgId) {
        return ResponseEntity.ok(
                ApiResponse.ok("Preferences fetched successfully", notificationService.getPreferences(orgId)));
    }

    @PatchMapping("/preferences/{orgId}/toggle")
    public ResponseEntity<ApiResponse<Void>> togglePreference(@PathVariable Long orgId,
            @RequestBody NotificationPreferenceDTO dto) {
        notificationService.togglePreference(orgId, dto.getEventId());
        return ResponseEntity.ok(ApiResponse.ok("Preference toggled successfully", null));
    }

    @PostMapping("/events")
    public ResponseEntity<ApiResponse<NotificationEventDTO>> saveEvent(@RequestBody NotificationEventDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Event saved successfully", notificationService.saveEvent(dto)));
    }

    @GetMapping("/events")
    public ResponseEntity<ApiResponse<List<NotificationEventDTO>>> getAllEvents() {
        return ResponseEntity.ok(ApiResponse.ok("Events fetched successfully", notificationService.getAllEvents()));
    }

    @PatchMapping("/events/{id}/toggle")
    public ResponseEntity<ApiResponse<NotificationEvent>> toggleEventStatus(@PathVariable Long id) {
        notificationService.toggleEventStatus(id);
        return ResponseEntity.ok(ApiResponse.ok("Event status toggled successfully", null));
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<ApiResponse<NotificationEvent>> deleteEvent(@PathVariable Long id) {
        notificationService.deleteEvent(id);
        return ResponseEntity.ok(ApiResponse.ok("Event deleted successfully", null));
    }

}
