package com.sellspark.SellsHRMS.dto.kb;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public
class KBTopicSummaryDTO {
    private Long id;
    private String title;
    private String contentPreview; // First 100 characters
    private Boolean hasAttachment;
    private LocalDateTime createdAt;
}