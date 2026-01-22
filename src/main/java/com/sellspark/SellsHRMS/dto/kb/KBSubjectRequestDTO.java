package com.sellspark.SellsHRMS.dto.kb;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

// ==================== Subject DTOs ====================
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class KBSubjectRequestDTO {
    private String title;
    private String description;
}