package com.sellspark.SellsHRMS.service;

import java.util.List;

import com.sellspark.SellsHRMS.dto.project.ProjectAttachmentDTO;

public interface ProjectAttachmentService {

    ProjectAttachmentDTO addAttachment(Long projectId, Long employeeId, ProjectAttachmentDTO dto);

    List<ProjectAttachmentDTO> getAttachments(Long projectId);

    void deleteAttachment(Long attachmentId, Long employeeId);

}