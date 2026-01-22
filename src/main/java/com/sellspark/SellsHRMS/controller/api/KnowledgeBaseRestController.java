package com.sellspark.SellsHRMS.controller.api;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import com.sellspark.SellsHRMS.dto.kb.KBDashboardDTO;
import com.sellspark.SellsHRMS.dto.kb.KBSubjectRequestDTO;
import com.sellspark.SellsHRMS.dto.kb.KBSubjectResponseDTO;
import com.sellspark.SellsHRMS.dto.kb.KBSubjectWithTopicsDTO;
import com.sellspark.SellsHRMS.dto.kb.KBTopicRequestDTO;
import com.sellspark.SellsHRMS.dto.kb.KBTopicResponseDTO;
import com.sellspark.SellsHRMS.service.KnowledgeBaseService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/kb/org")
@RequiredArgsConstructor
public class KnowledgeBaseRestController {

    private final KnowledgeBaseService kbService;

    // Dashboard: only few topics per subject
    @GetMapping("/{orgId}/dashboard")
    public List<KBDashboardDTO> getDashboardKB(@PathVariable Long orgId) {
        return kbService.getDashboardKnowledgeBase(orgId);
    }

    // All subjects with topics
    @GetMapping("/{orgId}/all")
    public List<KBDashboardDTO> getAllSubjects(@PathVariable Long orgId) {
        return kbService.getAllSubjectsWithTopics(orgId);
    }

    @PostMapping("/{orgId}/subjects")
    public ResponseEntity<?> createSubject(@PathVariable Long orgId, @RequestBody KBSubjectRequestDTO request, HttpSession session) {

        KBSubjectResponseDTO response = kbService.createSubject(orgId, request);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Subject created successfully",
                "data", response
        ));
    }
 
    @GetMapping("/{orgId}/subjects")
    public ResponseEntity<?> getAllSubjects(@PathVariable Long orgId, HttpSession session) {
        var subjects = kbService.getAllSubjects(orgId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", subjects
        ));
    }

    @GetMapping("/{orgId}/subjects/{subjectId}")
    public ResponseEntity<?> getSubjectById(@PathVariable Long orgId, @PathVariable Long subjectId, HttpSession session) {
        
        KBSubjectResponseDTO subject = kbService.getSubjectById(subjectId, orgId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", subject
        ));
    }

    @GetMapping("/{orgId}/subjects/{subjectId}/with-topics")
    public ResponseEntity<?> getSubjectWithTopics(@PathVariable Long orgId, @PathVariable Long subjectId, HttpSession session) {
        KBSubjectWithTopicsDTO data = kbService.getSubjectWithTopics(subjectId, orgId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", data
        ));
    }

    @PatchMapping("/{orgId}/subjects/{subjectId}")
    public ResponseEntity<?> updateSubject(
        @PathVariable Long orgId,
            @PathVariable Long subjectId,
            @RequestBody KBSubjectRequestDTO request,
            HttpSession session) {
       
        KBSubjectResponseDTO response = kbService.updateSubject(subjectId, orgId, request);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Subject updated successfully",
                "data", response
        ));
    }

    @DeleteMapping("/{orgId}/subjects/{subjectId}")
    public ResponseEntity<?> deleteSubject(@PathVariable Long orgId, @PathVariable Long subjectId, HttpSession session) {

        kbService.deleteSubject(subjectId, orgId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Subject deleted successfully"
        ));
    }

    // ==================== Topic Endpoints ====================

    @PostMapping("/{orgId}/topics")
    public ResponseEntity<?> createTopic(@PathVariable Long orgId, @RequestBody KBTopicRequestDTO request, HttpSession session) {
        
        KBTopicResponseDTO response = kbService.createTopic(orgId, request);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Topic created successfully",
                "data", response
        ));
    }

    @GetMapping("/{orgId}/subjects/{subjectId}/topics")
    public ResponseEntity<?> getTopicsBySubject(@PathVariable Long orgId, @PathVariable Long subjectId, HttpSession session) {
        var topics = kbService.getTopicsBySubject(subjectId, orgId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", topics
        ));
    }

    @GetMapping("/{orgId}/topics/{topicId}")
    public ResponseEntity<?> getTopicById(@PathVariable Long orgId, @PathVariable Long topicId, HttpSession session) {

        KBTopicResponseDTO topic = kbService.getTopicById(topicId, orgId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", topic
        ));
    }

    @GetMapping("/{orgId}/topics/search")
    public ResponseEntity<?> searchTopics(@PathVariable Long orgId, @RequestParam String keyword, HttpSession session) {
        
        var topics = kbService.searchTopics(orgId, keyword);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", topics
        ));
    }

    @PatchMapping("/{orgId}/topics/{topicId}")
    public ResponseEntity<?> updateTopic(
            @PathVariable Long orgId,
            @PathVariable Long topicId,
            @RequestBody KBTopicRequestDTO request,
            HttpSession session) {
        KBTopicResponseDTO response = kbService.updateTopic(topicId, orgId, request);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Topic updated successfully",
                "data", response
        ));
    }

    @DeleteMapping("/{orgId}/topics/{topicId}")
    public ResponseEntity<?> deleteTopic(@PathVariable Long orgId, @PathVariable Long topicId, HttpSession session) {
        
        kbService.deleteTopic(topicId, orgId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Topic deleted successfully"
        ));
    }
}
