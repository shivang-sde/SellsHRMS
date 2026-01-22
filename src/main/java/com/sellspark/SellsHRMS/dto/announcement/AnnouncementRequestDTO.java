package com.sellspark.SellsHRMS.dto.announcement;

import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnouncementRequestDTO {
    private String title;
    private String message;
    private LocalDateTime validUntil;
}