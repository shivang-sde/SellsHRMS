package com.sellspark.SellsHRMS.dto.kb;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class KBSubjectResponseDTO {
    private Long id;
    private String title;
    private String description;
    private Long organisationId;
    private Integer topicCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
