package com.sellspark.SellsHRMS.dto.kb;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;



@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class KBSubjectWithTopicsDTO {
    private Long id;
    private String title;
    private String description;
    private List<KBTopicResponseDTO> topics;
    private LocalDateTime createdAt;
}
