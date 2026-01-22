package com.sellspark.SellsHRMS.dto.dashboard;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TicketSummaryDTO {
    private Long id;
    private String title;
    private String status;
    private LocalDate endDate;
    private String projectName;
}
