package com.sellspark.SellsHRMS.dto.dashboard;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProjectSummaryDTO {
    private Long id;
    private String name;
    private String status;
    private String priority;
    private LocalDate endDate;
}
