package com.sellspark.SellsHRMS.dto.dashboard;

import java.util.List;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KBDashboardDTO {
    private Long subjectId;
    private String subjectTitle;
    private List<TopicDTO> topics;
}


