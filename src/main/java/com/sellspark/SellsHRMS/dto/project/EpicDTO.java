package com.sellspark.SellsHRMS.dto.project;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Epic Data Transfer Object representing a major feature group under a project.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EpicDTO {
    private Long id;
    private Long projectId;
    private String projectName;
    private String name;
    private String description;
    private String status; // PLANNING, IN_PROGRESS, COMPLETED, CANCELLED
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Optional summary / analytics fields
    private Long totalTasks;
    private Long completedTasks;
    private Double progressPercentage;

    // Permissions (based on current logged-in user)
    private Boolean canEdit;
    private Boolean canDelete;

    // Nested tasks (optional for detailed view)
    private List<Long> taskIds;
}
