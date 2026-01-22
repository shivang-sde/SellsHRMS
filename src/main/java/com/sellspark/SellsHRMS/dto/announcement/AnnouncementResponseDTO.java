package com.sellspark.SellsHRMS.dto.announcement;

import lombok.*;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnouncementResponseDTO {
    private Long id;
    private String title;
    private String message;
    private LocalDateTime validUntil;
    private Boolean isActive;
    private Long organisationId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}