package com.sellspark.SellsHRMS.dto.event;

import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponseDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String location;
    private String type;
    private Long organisationId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}