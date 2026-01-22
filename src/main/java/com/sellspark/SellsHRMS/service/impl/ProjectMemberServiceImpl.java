package com.sellspark.SellsHRMS.service.impl;

import com.sellspark.SellsHRMS.dto.project.ProjectMemberDTO;
import com.sellspark.SellsHRMS.entity.*;
import com.sellspark.SellsHRMS.mapper.ProjectMemberMapper;
import com.sellspark.SellsHRMS.repository.*;
import com.sellspark.SellsHRMS.service.ProjectMemberService;
import com.sellspark.SellsHRMS.service.helper.ProjectRoleResolver;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectMemberServiceImpl implements ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepo;
    private final ProjectRepository projectRepo;
    private final EmployeeRepository employeeRepo;
    private final ProjectRoleResolver roleResolver;

    // -------------------- CREATE --------------------
    @Override
    public ProjectMemberDTO addMember(ProjectMemberDTO dto, Long organisationId, Long actorEmpId) {
        Project project = projectRepo.findById(dto.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));
        Employee employee = employeeRepo.findById(dto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        Organisation org = project.getOrganisation();

        // Validate actor permissions
        validateActorRole(project.getId(), actorEmpId, List.of("PROJECT_MANAGER", "TEAM_LEAD"));

        // Resolve role for new member
        ProjectRole role = roleResolver.resolveByName(dto.getRole(), organisationId);

        // Avoid duplicates
        Optional<ProjectMember> existing = projectMemberRepo.findByProjectIdAndEmployeeIdAndIsActiveTrue(project.getId(), employee.getId());
        if (existing.isPresent()) throw new RuntimeException("Employee already part of this project");

        ProjectMember member = ProjectMemberMapper.toEntity(dto, project, employee, org, role);
        projectMemberRepo.save(member);

        return ProjectMemberMapper.toDTO(member);
    }

    // -------------------- UPDATE --------------------
    @Override
    public ProjectMemberDTO updateMember(Long memberId, ProjectMemberDTO dto, Long organisationId, Long actorEmpId) {
        ProjectMember member = projectMemberRepo.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Project member not found"));

        Project project = member.getProject();
        validateActorRole(project.getId(), actorEmpId,
                List.of("PROJECT_MANAGER", "TEAM_LEAD", "SELF:" + member.getEmployee().getId()));

        ProjectRole role = (dto.getRole() != null)
                ? roleResolver.resolveByName(dto.getRole(), organisationId)
                : member.getRole();

        ProjectMemberMapper.updateEntity(member, dto, role);
        projectMemberRepo.save(member);

        return ProjectMemberMapper.toDTO(member);
    }

    // -------------------- DELETE --------------------
    @Override
    public void removeMember(Long memberId, Long organisationId, Long actorEmpId) {
        ProjectMember member = projectMemberRepo.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Project member not found"));

        Project project = member.getProject();
        validateActorRole(project.getId(), actorEmpId, List.of("PROJECT_MANAGER", "TEAM_LEAD"));

        member.setIsActive(false);
        member.setLeftAt(java.time.LocalDateTime.now());
        projectMemberRepo.save(member);
    }

    // -------------------- READ --------------------
    @Override
    public ProjectMemberDTO getMemberById(Long memberId, Long organisationId) {
        ProjectMember member = projectMemberRepo.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Project member not found"));
        return ProjectMemberMapper.toDTO(member);
    }

    @Override
    public List<ProjectMemberDTO> getMembersByProject(Long projectId, Long organisationId) {
        List<ProjectMember> members = projectMemberRepo.findByProjectIdAndIsActiveTrue(projectId);
        return members.stream().map(ProjectMemberMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ProjectMemberDTO> getMembersByEmployee(Long employeeId, Long organisationId) {
        List<ProjectMember> members = projectMemberRepo.findByEmployeeIdAndIsActiveTrue(employeeId);
        return members.stream().map(ProjectMemberMapper::toDTO).collect(Collectors.toList());
    }

    // -------------------- HELPER METHODS --------------------
    private void validateActorRole(Long projectId, Long actorEmpId, List<String> allowedRoles) {
        Optional<ProjectMember> actorMemberOpt =
                projectMemberRepo.findByProjectIdAndEmployeeIdAndIsActiveTrue(projectId, actorEmpId);

        if (actorMemberOpt.isEmpty()) {
            throw new RuntimeException("Actor is not part of this project");
        }

        ProjectMember actorMember = actorMemberOpt.get();
        String actorRole = actorMember.getRole() != null ? actorMember.getRole().getName() : "UNKNOWN";

        // Self-update exception (for employees updating themselves)
        if (allowedRoles.stream().anyMatch(r -> r.startsWith("SELF:"))) {
            String selfClause = "SELF:" + actorMember.getEmployee().getId();
            if (allowedRoles.contains(selfClause)) return;
        }

        if (!allowedRoles.contains(actorRole)) {
            throw new RuntimeException("Insufficient permission for this action. Required roles: " + allowedRoles);
        }
    }
}
