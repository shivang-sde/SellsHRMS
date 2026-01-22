package com.sellspark.SellsHRMS.dto.dashboard;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TaskSummaryDTO {
    private Long id;
    private String title;
    private String status;
    private String projectName;
    private String description;
    private LocalDateTime dueDate;
}
