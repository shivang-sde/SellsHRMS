package com.sellspark.SellsHRMS.dto.project;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReminderDTO {
    private Long id;
    private String title;
    private String type; // "TASK" or "TICKET"
    private String projectName;
    private String status;
    private String priority;
    private LocalDateTime dueAt; // unified field
}
