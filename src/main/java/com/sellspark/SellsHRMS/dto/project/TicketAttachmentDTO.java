package com.sellspark.SellsHRMS.dto.project;

import java.time.LocalDateTime;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sellspark.SellsHRMS.entity.TicketAttachment;

@Getter
@Setter
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

    // ✅ Safe static mapper (covers all relevant fields)
    public static TicketAttachmentDTO fromEntity(TicketAttachment attachment) {
        if (attachment == null)
            return null;

        return TicketAttachmentDTO.builder()
                .id(attachment.getId())
                .ticketId(
                        attachment.getTicket() != null
                                ? attachment.getTicket().getId()
                                : null)
                .fileName(attachment.getFileName())
                .fileUrl(attachment.getFileUrl())
                .fileType(null) // if you don’t store type in DB; keep placeholder
                .fileSizeKB(attachment.getFileSizeKB())
                .description(attachment.getDescription())
                .uploadedAt(attachment.getUploadedAt())
                .uploadedById(
                        attachment.getUploadedBy() != null
                                ? attachment.getUploadedBy().getId()
                                : null)
                .uploadedByName(
                        attachment.getUploadedBy() != null
                                ? attachment.getUploadedBy().getFirstName()
                                        + (attachment.getUploadedBy().getLastName() != null
                                                ? " " + attachment.getUploadedBy().getLastName()
                                                : "")
                                : null)
                .build();
    }
}
