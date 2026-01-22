package com.sellspark.SellsHRMS.dto.dashboard;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sellspark.SellsHRMS.dto.project.ProjectMemberDTO;
import com.sellspark.SellsHRMS.dto.project.TicketDTO;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Universal Project Data Transfer Object for multi-tenant HRMS Work Management.
 * Supports multiple methodologies (Agile, Kanban, Waterfall, etc.)
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectDTO {

    private Long id;
    private Long organisationId;

    private String name;
    private String description;

    // Core project classification
    private String projectType;          // SOFTWARE_DEVELOPMENT, HR, FINANCE, etc.
    private String methodology;          // AGILE, KANBAN, WATERFALL, etc.
    private String status;               // PLANNING, IN_PROGRESS, COMPLETED, etc.
    private String priority;             // LOW, MEDIUM, HIGH, CRITICAL

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate actualEndDate;

    // Department information
    private Long departmentId;
    private String departmentName;

    // Leadership
    private Long projectManagerId;
    private String projectManagerName;
    private String projectManagerEmail;

    private Long projectTeamLeadId;
    private String projectTeamLeadName;
    private String projectTeamLeadEmail;

    // Audit
    private Long createdById;
    private String createdByName;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Members (full DTOs)
    private List<ProjectMemberDTO> members;

    private List<TicketDTO> tickets;

    private int totalTickets;
    private int completedTickets;

    // // Task summary metrics
    // private int totalTasks;
    // private int completedTasks;
    // private int pendingTasks;

    // Optional progress computed on the fly (e.g., % complete)
    private Double progressPercentage;

    // Runtime permission flags (contextual for logged-in user)
    // private Boolean canEdit;          // if user has MANAGE_PROJECT or is manager
    // private Boolean canDelete;        // if user is project manager or creator
    // private Boolean canAddTask;       // if user has CREATE_TASK
    // private Boolean canComment;       // if user has COMMENT
}
