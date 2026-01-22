package com.sellspark.SellsHRMS.controller.api;

import com.sellspark.SellsHRMS.dto.project.ProjectMemberDTO;
import com.sellspark.SellsHRMS.payload.ApiResponse;
import com.sellspark.SellsHRMS.service.ProjectMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/projects/members")
@RequiredArgsConstructor
public class ProjectMemberRestController {

    private final ProjectMemberService projectMemberService;

    // CREATE
    @PostMapping
    public ResponseEntity<ApiResponse<ProjectMemberDTO>> addMember(
            @RequestBody ProjectMemberDTO dto,
            @RequestParam Long organisationId,
            @RequestParam Long actorId) {

        ProjectMemberDTO created = projectMemberService.addMember(dto, organisationId, actorId);
        return ResponseEntity.ok(ApiResponse.ok("Project member added successfully", created));
    }

    // UPDATE
    @PutMapping("/{memberId}")
    public ResponseEntity<ApiResponse<ProjectMemberDTO>> updateMember(
            @PathVariable Long memberId,
            @RequestBody ProjectMemberDTO dto,
            @RequestParam Long organisationId,
            @RequestParam Long actorId) {

        ProjectMemberDTO updated = projectMemberService.updateMember(memberId, dto, organisationId, actorId);
        return ResponseEntity.ok(ApiResponse.ok("Project member updated successfully", updated));
    }

    // DELETE
    @DeleteMapping("/{memberId}")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @PathVariable Long memberId,
            @RequestParam Long organisationId,
            @RequestParam Long actorId) {

        projectMemberService.removeMember(memberId, organisationId, actorId);
        return ResponseEntity.ok(ApiResponse.ok("Project member removed successfully", null));
    }

    // GET BY ID
    @GetMapping("/{memberId}")
    public ResponseEntity<ApiResponse<ProjectMemberDTO>> getMemberById(
            @PathVariable Long memberId,
            @RequestParam Long organisationId) {

        ProjectMemberDTO member = projectMemberService.getMemberById(memberId, organisationId);
        return ResponseEntity.ok(ApiResponse.ok("Project member fetched", member));
    }

    // LIST BY PROJECT
    @GetMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<List<ProjectMemberDTO>>> getMembersByProject(
            @PathVariable Long projectId,
            @RequestParam Long organisationId) {

        List<ProjectMemberDTO> members = projectMemberService.getMembersByProject(projectId, organisationId);
        return ResponseEntity.ok(ApiResponse.ok("Members for project fetched", members));
    }

    // LIST BY EMPLOYEE
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<List<ProjectMemberDTO>>> getMembersByEmployee(
            @PathVariable Long employeeId,
            @RequestParam Long organisationId) {

        List<ProjectMemberDTO> members = projectMemberService.getMembersByEmployee(employeeId, organisationId);
        return ResponseEntity.ok(ApiResponse.ok("Projects where employee is member fetched", members));
    }
}
