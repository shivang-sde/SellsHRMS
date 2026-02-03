package com.sellspark.SellsHRMS.dto.mapper;

import com.sellspark.SellsHRMS.dto.project.ProjectAttachmentDTO;
import com.sellspark.SellsHRMS.entity.Employee;
import com.sellspark.SellsHRMS.entity.Project;
import com.sellspark.SellsHRMS.entity.ProjectAttachment;

public class ProjectAttachmentMapper {
    public static ProjectAttachmentDTO toDTO(ProjectAttachment a) {
        if (a == null)
            return null;
        return ProjectAttachmentDTO.builder()
                .id(a.getId())
                .title(a.getTitle())
                .description(a.getDescription())
                .url(a.getUrl())
                .type(a.getType().name())
                .uploadedById(a.getUploadedBy().getId())
                .uploadedByName(a.getUploadedBy().getFirstName() + " " + a.getUploadedBy().getLastName())
                .uploadedAt(a.getUploadedAt())
                .build();
    }

    public static ProjectAttachment toEntity(ProjectAttachmentDTO dto, Project project, Employee uploader) {
        return ProjectAttachment.builder()
                .project(project)
                .uploadedBy(uploader)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .url(dto.getUrl())
                .type(ProjectAttachment.AttachmentType.valueOf(dto.getType()))
                .build();
    }
}
