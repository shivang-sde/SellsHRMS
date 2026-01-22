package com.sellspark.SellsHRMS.dto.organisation;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnnouncementDTO {
    private Long id;
    private String title;
    private String message;
    private LocalDateTime validUntil;
}

