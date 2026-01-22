package com.sellspark.SellsHRMS.dto.kb;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;



@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class KBTopicResponseDTO {
    private Long id;
    private Long subjectId;
    private String subjectTitle;
    private String title;
    private String content;
    private String attachmentUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

