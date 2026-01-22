package com.sellspark.SellsHRMS.controller.api;

import com.sellspark.SellsHRMS.dto.announcement.*;
import com.sellspark.SellsHRMS.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

// ==================== Announcement Controller ====================
@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
@Slf4j
public class AnnouncementRestController {

    private final AnnouncementService announcementService;

    @PostMapping
    public ResponseEntity<?> createAnnouncement(@RequestBody AnnouncementRequestDTO request, HttpSession session) {
        Long orgId = (Long) session.getAttribute("ORG_ID");
        AnnouncementResponseDTO response = announcementService.createAnnouncement(orgId, request);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Announcement created successfully",
                "data", response
        ));
    }

    @GetMapping
    public ResponseEntity<?> getAllAnnouncements(HttpSession session) {
        Long orgId = (Long) session.getAttribute("ORG_ID");
        var announcements = announcementService.getAllAnnouncements(orgId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", announcements
        ));
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActiveAnnouncements(HttpSession session) {
        Long orgId = (Long) session.getAttribute("ORG_ID");
        var announcements = announcementService.getActiveAnnouncements(orgId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", announcements
        ));
    }

    @GetMapping("/{announcementId}")
    public ResponseEntity<?> getAnnouncementById(@PathVariable Long announcementId, HttpSession session) {
        Long orgId = (Long) session.getAttribute("ORG_ID");
        AnnouncementResponseDTO announcement = announcementService.getAnnouncementById(announcementId, orgId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", announcement
        ));
    }

    @PatchMapping("/{announcementId}")
    public ResponseEntity<?> updateAnnouncement(
            @PathVariable Long announcementId,
            @RequestBody AnnouncementRequestDTO request,
            HttpSession session) {
        Long orgId = (Long) session.getAttribute("ORG_ID");
        AnnouncementResponseDTO response = announcementService.updateAnnouncement(announcementId, orgId, request);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Announcement updated successfully",
                "data", response
        ));
    }

    @PatchMapping("/{announcementId}/toggle")
    public ResponseEntity<?> toggleStatus(@PathVariable Long announcementId, HttpSession session) {
        Long orgId = (Long) session.getAttribute("ORG_ID");
        announcementService.toggleAnnouncementStatus(announcementId, orgId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Announcement status toggled successfully"
        ));
    }

    @DeleteMapping("/{announcementId}")
    public ResponseEntity<?> deleteAnnouncement(@PathVariable Long announcementId, HttpSession session) {
        Long orgId = (Long) session.getAttribute("ORG_ID");
        announcementService.deleteAnnouncement(announcementId, orgId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Announcement deleted successfully"
        ));
    }
}