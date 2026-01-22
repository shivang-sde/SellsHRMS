package com.sellspark.SellsHRMS.dto.kb;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KBDashboardDTO {
    private Long id;
    private String title;
    private String description;
    private Integer topicCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<KBTopicSummaryDTO> recentTopics;
}
