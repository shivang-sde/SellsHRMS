package com.sellspark.SellsHRMS.dto.project;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Sprint Data Transfer Object representing a sprint under a project.
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SprintDTO {
    private Long id;
    private Long projectId;
    private String projectName;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status; // PLANNED, ACTIVE, COMPLETED, CANCELLED
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Optional analytics
    private Long totalTasks;
    private Long completedTasks;
    private Double progressPercentage;

    // Permissions
    private Boolean canEdit;
    private Boolean canDelete;

    // Optional nested task IDs for detailed view
    private List<Long> taskIds;
}
