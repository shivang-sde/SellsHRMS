package com.sellspark.SellsHRMS.controller.api;

import com.sellspark.SellsHRMS.dto.dashboard.ProjectDTO;
import com.sellspark.SellsHRMS.entity.Project.ProjectStatus;
import com.sellspark.SellsHRMS.entity.Project.ProjectType;
import com.sellspark.SellsHRMS.payload.ApiResponse;
import com.sellspark.SellsHRMS.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for managing Projects.
 * Supports CRUD operations, filtering, and completion tracking.
 */
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectRestController {

    private final ProjectService projectService;

    // ----------------------------------------------------------------
    // CREATE PROJECT
    // ----------------------------------------------------------------
    @PostMapping
    public ResponseEntity<ApiResponse<ProjectDTO>> createProject(
            @RequestBody ProjectDTO dto,
            @RequestParam Long organisationId,
            @RequestParam Long createdById) {

        ProjectDTO created = projectService.createProject(dto, organisationId, createdById);
        return ResponseEntity.ok(ApiResponse.ok("Project created successfully", created));
    }

    // ----------------------------------------------------------------
    // UPDATE PROJECT
    // ----------------------------------------------------------------
    @PutMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectDTO>> updateProject(
            @PathVariable Long projectId,
            @RequestBody ProjectDTO dto,
            @RequestParam Long organisationId,
            @RequestParam Long employeeId) {

        ProjectDTO updated = projectService.updateProject(projectId, dto, organisationId, employeeId);
        return ResponseEntity.ok(ApiResponse.ok("Project updated successfully", updated));
    }

    // ----------------------------------------------------------------
    // GET PROJECT DETAILS
    // ----------------------------------------------------------------
    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectDTO>> getProjectById(
            @PathVariable Long projectId,
            @RequestParam Long organisationId,
            @RequestParam Long employeeId) {

        ProjectDTO project = projectService.getProjectById(projectId, organisationId, employeeId);
        return ResponseEntity.ok(ApiResponse.ok("Project retrieved successfully", project));
    }

    // ----------------------------------------------------------------
    // DELETE PROJECT
    // ----------------------------------------------------------------
    @DeleteMapping("/{projectId}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(
            @PathVariable Long projectId,
            @RequestParam Long organisationId,
            @RequestParam Long employeeId) {

        projectService.deleteProject(projectId, organisationId, employeeId);
        return ResponseEntity.ok(ApiResponse.ok("Project deleted successfully", null));
    }

    @PostMapping("/{projectId}/members")
    public ResponseEntity<ApiResponse<Void>> addMembers(
        @PathVariable Long projectId,
        @RequestBody Map<String, List<Long>> body,
        @RequestParam Long organisationId,
        @RequestParam Long employeeId) {
    List<Long> employeeIds = body.get("employeeIds");
    projectService.addMembers(projectId, employeeIds, organisationId, employeeId);
    return ResponseEntity.ok(ApiResponse.ok("Members added successfully", null));
}

 @DeleteMapping("/{projectId}/members/{memberId}")
public ResponseEntity<ApiResponse<Void>> removeMember(
        @PathVariable Long projectId,
        @PathVariable Long memberId,
        @RequestParam Long organisationId,
        @RequestParam Long employeeId) {
    projectService.removeMember(projectId, memberId, organisationId, employeeId);
    return ResponseEntity.ok(ApiResponse.ok("Member removed successfully", null));
}



    // ----------------------------------------------------------------
    // MARK PROJECT COMPLETION
    // ----------------------------------------------------------------
    @PutMapping("/{projectId}/completion")
    public ResponseEntity<ApiResponse<Void>> updateCompletion(
            @PathVariable Long projectId,
            @RequestParam Long organisationId,
            @RequestParam Long actorEmpId) {

        projectService.updateProjectCompletion(projectId, organisationId, actorEmpId);
        return ResponseEntity.ok(ApiResponse.ok("Project completion updated successfully", null));
    }

    // ----------------------------------------------------------------
    // LIST ALL PROJECTS
    // ----------------------------------------------------------------
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectDTO>>> getAllProjects(
            @RequestParam Long organisationId) {

        List<ProjectDTO> projects = projectService.getAllProjects(organisationId);
        return ResponseEntity.ok(ApiResponse.ok("All projects fetched successfully", projects));
    }

    // ----------------------------------------------------------------
    // FILTER PROJECTS BY STATUS
    // ----------------------------------------------------------------
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<ProjectDTO>>> getProjectsByStatus(
            @RequestParam Long organisationId,
            @PathVariable ProjectStatus status) {

        List<ProjectDTO> projects = projectService.getProjectsByStatus(organisationId, status);
        return ResponseEntity.ok(ApiResponse.ok("Projects filtered by status", projects));
    }

    // ----------------------------------------------------------------
    // FILTER PROJECTS BY TYPE
    // ----------------------------------------------------------------
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<ProjectDTO>>> getProjectsByType(
            @RequestParam Long organisationId,
            @PathVariable ProjectType type) {

        List<ProjectDTO> projects = projectService.getProjectsByType(organisationId, type);
        return ResponseEntity.ok(ApiResponse.ok("Projects filtered by type", projects));
    }

    // ----------------------------------------------------------------
    // FILTER PROJECTS BY DEPARTMENT
    // ----------------------------------------------------------------
    @GetMapping("/department/{deptId}")
    public ResponseEntity<ApiResponse<List<ProjectDTO>>> getProjectsByDepartment(
            @RequestParam Long organisationId,
            @PathVariable Long deptId) {

        List<ProjectDTO> projects = projectService.getProjectsByDepartment(organisationId, deptId);
        return ResponseEntity.ok(ApiResponse.ok("Projects filtered by department", projects));
    }

    // ----------------------------------------------------------------
    // PROJECTS BY EMPLOYEE
    // ----------------------------------------------------------------
    @GetMapping("/employee/{empId}")
    public ResponseEntity<ApiResponse<List<ProjectDTO>>> getProjectsByEmployee(
            @RequestParam Long organisationId,
            @PathVariable Long empId) {

        List<ProjectDTO> projects = projectService.getProjectsByEmployee(organisationId, empId);
        return ResponseEntity.ok(ApiResponse.ok("Projects assigned to employee fetched", projects));
    }

    // ----------------------------------------------------------------
    // SEARCH PROJECTS
    // ----------------------------------------------------------------
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProjectDTO>>> searchProjects(
            @RequestParam Long organisationId,
            @RequestParam String keyword) {

        List<ProjectDTO> projects = projectService.searchProjects(organisationId, keyword);
        return ResponseEntity.ok(ApiResponse.ok("Projects search results", projects));
    }

    // ----------------------------------------------------------------
    // ENUM CONFIG (for frontend dropdowns)
    // ----------------------------------------------------------------
    @GetMapping("/features/types")
    public ResponseEntity<ApiResponse<ProjectType[]>> getProjectTypes() {
        return ResponseEntity.ok(ApiResponse.ok("Available project types", ProjectType.values()));
    }

    @GetMapping("/features/statuses")
    public ResponseEntity<ApiResponse<ProjectStatus[]>> getProjectStatuses() {
        return ResponseEntity.ok(ApiResponse.ok("Available project statuses", ProjectStatus.values()));
    }
}
