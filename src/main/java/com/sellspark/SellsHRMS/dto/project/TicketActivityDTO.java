package com.sellspark.SellsHRMS.dto.project;


import lombok.*;
import java.time.LocalDateTime;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TicketActivityDTO {
    private Long id;
    private Long ticketId;
    private Long employeeId;
    private String employeeName;
    private String activityType;
    private String description;
    private LocalDateTime createdAt;
}
