package com.sellspark.SellsHRMS.dto.project;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskAttachmentDTO {
    private Long id;
    private Long taskId;

    private String fileName;
    private String fileUrl;
    private String fileType;
    private Double fileSizeKB;
    private String externalLink;
    private String description;
    private Long uploadedById;
    private String uploadedByName;

    private LocalDateTime uploadedAt;
}
