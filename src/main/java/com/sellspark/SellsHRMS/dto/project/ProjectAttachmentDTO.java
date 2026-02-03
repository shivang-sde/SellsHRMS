package com.sellspark.SellsHRMS.dto.project;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectAttachmentDTO {
    private Long id;
    private String title;
    private String description;
    private String url;
    private String type; // FILE, VIDEO_LINK, etc.
    private Long uploadedById;
    private String uploadedByName;
    private LocalDateTime uploadedAt;
}
