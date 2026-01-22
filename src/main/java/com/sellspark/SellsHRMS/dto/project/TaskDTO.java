package com.sellspark.SellsHRMS.dto.project;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskDTO {

    private Long id;
    // private String taskKey;

    private String title;
    private String description;

    private String priority;  // LOW, MEDIUM, HIGH, URGENT
    private String status;    // BACKLOG, TO_DO, IN_PROGRESS, REVIEW, DONE

    // For reminder/self-task
    private Boolean isSelfTask;
    private LocalDateTime reminderAt;
    private Boolean reminderEnabled;

    // Associations
    private Long ticketId;
    private String ticketTitle;
    private Long projectId;
    private String projectName;

    // People
    private Long assigneeId;
    private String assigneeName;
    private Long reporterId;
    private String reporterName;
    private Long createdById;
    private String createdByName;

    // System fields
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Attachments and history
    private List<TaskAttachmentDTO> attachments;
    private List<TaskActivityDTO> activityLog;
}
