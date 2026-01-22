package com.sellspark.SellsHRMS.mapper;

import com.sellspark.SellsHRMS.dto.project.ProjectMemberDTO;
import com.sellspark.SellsHRMS.entity.*;

import java.util.List;
import java.util.stream.Collectors;

public class ProjectMemberMapper {

    // ---------------- ENTITY → DTO ----------------
    public static ProjectMemberDTO toDTO(ProjectMember member) {
        if (member == null) return null;

        return ProjectMemberDTO.builder()
                .id(member.getId())
                .projectId(member.getProject() != null ? member.getProject().getId() : null)
                .projectName(member.getProject() != null ? member.getProject().getName() : null)
                .employeeId(member.getEmployee() != null ? member.getEmployee().getId() : null)
                .employeeName(member.getEmployee() != null
                        ? member.getEmployee().getFirstName() + " " + member.getEmployee().getLastName() : null)
                .employeeEmail(member.getEmployee() != null ? member.getEmployee().getEmail() : null)
                .departmentName(member.getEmployee() != null && member.getEmployee().getDepartment() != null
                        ? member.getEmployee().getDepartment().getName() : null)
                .role(member.getRole() != null ? member.getRole().getName() : null)
                .allocationPercentage(member.getAllocationPercentage())
                .joinedAt(member.getJoinedAt())
                .leftAt(member.getLeftAt())
                .isActive(member.getIsActive())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }

    // ---------------- DTO → ENTITY ----------------
    public static ProjectMember toEntity(ProjectMemberDTO dto, Project project, Employee employee,
                                         Organisation organisation, ProjectRole role) {
        if (dto == null) return null;

        return ProjectMember.builder()
                .id(dto.getId())
                .project(project)
                .employee(employee)
                .organisation(organisation)
                .role(role) // Pass ProjectRole object explicitly
                .allocationPercentage(dto.getAllocationPercentage())
                .joinedAt(dto.getJoinedAt())
                .leftAt(dto.getLeftAt())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .build();
    }

    // ---------------- UPDATE EXISTING ENTITY ----------------
    public static void updateEntity(ProjectMember existing, ProjectMemberDTO dto, ProjectRole newRole) {
        if (existing == null || dto == null) return;

        if (newRole != null) existing.setRole(newRole);
        if (dto.getAllocationPercentage() != null)
            existing.setAllocationPercentage(dto.getAllocationPercentage());
        if (dto.getJoinedAt() != null)
            existing.setJoinedAt(dto.getJoinedAt());
        if (dto.getLeftAt() != null)
            existing.setLeftAt(dto.getLeftAt());
        if (dto.getIsActive() != null)
            existing.setIsActive(dto.getIsActive());
    }

    // ---------------- LIST MAPPERS ----------------
    public static List<ProjectMemberDTO> toDTOList(List<ProjectMember> members) {
        return members == null ? List.of()
                : members.stream().map(ProjectMemberMapper::toDTO).collect(Collectors.toList());
    }
}
