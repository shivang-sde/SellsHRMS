package com.sellspark.SellsHRMS.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;

import com.sellspark.SellsHRMS.dto.mapper.ProjectAttachmentMapper;
import com.sellspark.SellsHRMS.dto.project.ProjectAttachmentDTO;
import com.sellspark.SellsHRMS.entity.Employee;
import com.sellspark.SellsHRMS.entity.Project;
import com.sellspark.SellsHRMS.entity.ProjectAttachment;
import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
import com.sellspark.SellsHRMS.exception.UnauthorizedActionException;
import com.sellspark.SellsHRMS.repository.EmployeeRepository;
import com.sellspark.SellsHRMS.repository.ProjectAttachmentRepository;
import com.sellspark.SellsHRMS.repository.ProjectRepository;
import com.sellspark.SellsHRMS.service.ProjectAttachmentService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectAttachmentServiceImpl implements ProjectAttachmentService {

    private final ProjectRepository projectRepo;
    private final EmployeeRepository employeeRepo;
    private final ProjectAttachmentRepository attachmentRepo;

    @Transactional
    @Override
    public ProjectAttachmentDTO addAttachment(Long projectId, Long employeeId, ProjectAttachmentDTO dto) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        Employee uploader = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Uploader not found"));

        ProjectAttachment entity = ProjectAttachmentMapper.toEntity(dto, project, uploader);
        return ProjectAttachmentMapper.toDTO(attachmentRepo.save(entity));
    }

    @Override
    public List<ProjectAttachmentDTO> getAttachments(Long projectId) {
        return attachmentRepo.findByProjectId(projectId)
                .stream().map(ProjectAttachmentMapper::toDTO).toList();
    }

    @Transactional
    @Override
    public void deleteAttachment(Long attachmentId, Long employeeId) {
        ProjectAttachment att = attachmentRepo.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found"));

        if (!att.getUploadedBy().getId().equals(employeeId))
            throw new UnauthorizedActionException("You are not allowed to delete this attachment");

        attachmentRepo.delete(att);
    }
}
