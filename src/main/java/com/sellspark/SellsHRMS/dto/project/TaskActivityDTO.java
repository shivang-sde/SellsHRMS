package com.sellspark.SellsHRMS.dto.project;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskActivityDTO {
    private Long id;
    private Long taskId;
    private String activityType; // TASK_CREATED, STATUS_CHANGED, COMMENT_ADDED, etc.

    private String oldValue;
    private String newValue;

    private Long employeeId;
    private String employeeName;

    private LocalDateTime createdAt;
}

