package com.sellspark.SellsHRMS.mapper;

import com.sellspark.SellsHRMS.dto.dashboard.ProjectDTO;
import com.sellspark.SellsHRMS.dto.project.ProjectMemberDTO;
import com.sellspark.SellsHRMS.entity.*;
import java.util.List;
import java.util.stream.Collectors;
// Add the import for ProjectMemberMapper if it exists in your project, or check the correct package path

public class ProjectMapper {

    public static ProjectDTO toDTO(Project project, Long currentUserId) {

        if (project == null) return null;

        // long totalTasks = project.getTasks() != null ? project.getTasks().size() : 0;
        // long completedTasks = project.getTasks() != null
        //         ? project.getTasks().stream()
        //             .filter(t -> t.getStatus() == Task.TaskStatus.DONE).count()
        //         : 0; 
    

        ProjectDTO dto = ProjectDTO.builder()
                .id(project.getId())
                .organisationId(project.getOrganisation().getId())
                .name(project.getName())
                .description(project.getDescription())
                .projectType(project.getProjectType() != null ? project.getProjectType().name() : null)
                .methodology(project.getMethodology() != null ? project.getMethodology().name() : null)
                .status(project.getStatus() != null ? project.getStatus().name() : null)
                .priority(project.getPriority() != null ? project.getPriority().name() : null)
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .actualEndDate(project.getActualEndDate())
                .departmentId(project.getDepartment() != null ? project.getDepartment().getId() : null)
                .departmentName(project.getDepartment() != null ? project.getDepartment().getName() : null)
                .projectManagerId(project.getProjectManager() != null ? project.getProjectManager().getId() : null)
                .projectManagerName(project.getProjectManager() != null ? project.getProjectManager().getFirstName() + project.getProjectManager().getLastName() : null)
                .projectManagerEmail(project.getProjectManager() != null ? project.getProjectManager().getEmail() : null)
                .projectTeamLeadId(project.getProjectTeamLead() != null ? project.getProjectTeamLead().getId() : null)
                .projectTeamLeadName(project.getProjectTeamLead() != null ? project.getProjectTeamLead().getFirstName() + project.getProjectTeamLead().getLastName() : null)
                .projectTeamLeadEmail(project.getProjectTeamLead() != null ? project.getProjectTeamLead().getEmail() : null)
                .createdById(project.getCreatedBy() != null ? project.getCreatedBy().getId() : null)
                .createdByName(project.getCreatedBy() != null ? project.getCreatedBy().getFirstName() + project.getCreatedBy().getLastName() : null)
                .isActive(project.getIsActive())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                // .progressPercentage(progress)
                .members(toMemberDTOList(project.getProjectMembers()))
        
                .build();

        return dto;
    }

    public static Project toEntity(ProjectDTO dto, Organisation org, Department dept,
                                   Employee manager, Employee lead, Employee creator) {

        return Project.builder()
                .organisation(org)
                .department(dept)
                .projectManager(manager)
                .projectTeamLead(lead)
                .createdBy(creator)
                .name(dto.getName())
                .description(dto.getDescription())
                .methodology(Project.ProjectMethodology.valueOf(dto.getMethodology()))
                .projectType(Project.ProjectType.valueOf(dto.getProjectType()))
                .status(Project.ProjectStatus.valueOf(dto.getStatus()))
                .priority(Project.Priority.valueOf(dto.getPriority()))
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .actualEndDate(dto.getActualEndDate())
                .isActive(true)
                .build();
    }

    private static List<ProjectMemberDTO> toMemberDTOList(List<ProjectMember> members) {
        if (members == null) return List.of();
        return members.stream().map(ProjectMemberMapper::toDTO).collect(Collectors.toList());
    }
}
