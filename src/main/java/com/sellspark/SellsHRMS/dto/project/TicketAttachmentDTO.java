package com.sellspark.SellsHRMS.dto.project;

import java.time.LocalDateTime;
import lombok.*;


import com.fasterxml.jackson.annotation.JsonInclude;


@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TicketAttachmentDTO {
    private Long id;
    private Long ticketId;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Double fileSizeKB;
    private String description; 
    private Long uploadedById;
    private String uploadedByName;
    private LocalDateTime uploadedAt;
}
