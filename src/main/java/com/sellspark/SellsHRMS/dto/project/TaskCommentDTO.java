package com.sellspark.SellsHRMS.dto.project;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskCommentDTO {
    private Long id;
    private Long taskId;
    private Long employeeId;
    private String employeeName;
    private String comment;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<TaskAttachmentDTO> attachments;
}
