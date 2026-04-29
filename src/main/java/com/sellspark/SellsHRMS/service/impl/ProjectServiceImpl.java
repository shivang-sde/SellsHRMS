package com.sellspark.SellsHRMS.service.impl;

import com.sellspark.SellsHRMS.dto.dashboard.ProjectDTO;
import com.sellspark.SellsHRMS.entity.*;
import com.sellspark.SellsHRMS.entity.Project.*;
import com.sellspark.SellsHRMS.exception.InvalidOperationException;
import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
import com.sellspark.SellsHRMS.exception.UnauthorizedActionException;
import com.sellspark.SellsHRMS.mapper.ProjectMapper;
import com.sellspark.SellsHRMS.notification.enums.TargetRole;
import com.sellspark.SellsHRMS.notification.event.NotificationEventData;
import com.sellspark.SellsHRMS.notification.event.NotificationEventPublisher;
import com.sellspark.SellsHRMS.repository.*;
import com.sellspark.SellsHRMS.service.ProjectService;

import lombok.RequiredArgsConstructor;

import org.springframework.jmx.export.notification.NotificationPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Complete implementation of ProjectService with full business logic,
 * permission checks, and multi-tenant isolation.
 */
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepo;
    private final EmployeeRepository employeeRepo;
    private final DepartmentRepository departmentRepo;
    private final OrganisationRepository organisationRepo;
    private final ProjectMemberRepository memberRepo;
    private final ProjectRoleRepository projectRoleRepo;
    private final TaskRepository taskRepo;

    private final NotificationEventPublisher notificationEventPublisher;

    // --------------------------------------------------------------------
    // 🟢 CREATE PROJECT
    // --------------------------------------------------------------------
    @Transactional
    @Override
    public ProjectDTO createProject(ProjectDTO dto, Long organisationId, Long createdByEmpId) {

        Organisation org = organisationRepo.findById(organisationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organisation not found"));

        Employee creator = employeeRepo.findById(createdByEmpId)
                .orElseThrow(() -> new ResourceNotFoundException("Creator not found"));

        Department dept = null;
        if (dto.getDepartmentId() != null) {
            dept = departmentRepo.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        }

        Employee manager = null;
        if (dto.getProjectManagerId() != null) {
            manager = employeeRepo.findById(dto.getProjectManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
        }

        if (dto.getProjectManagerId() == null) {
            manager = creator; // Default if not selected
        }

        Employee teamLead = null;
        if (dto.getProjectTeamLeadId() != null) {
            teamLead = employeeRepo.findById(dto.getProjectTeamLeadId())
                    .orElseThrow(() -> new ResourceNotFoundException("Team Lead not found"));
        }

        if (dto.getProjectType() == null || dto.getMethodology() == null)
            throw new InvalidOperationException("Project type and methodology are required");

        Project project = ProjectMapper.toEntity(dto, org, dept, manager, teamLead, creator);

        Project saved = projectRepo.save(project);

        // avoiding the scenarios in which manager or team lead is same as creator

        // Auto-add creator as member
        ProjectMember pm = new ProjectMember();
        pm.setProject(saved);
        pm.setEmployee(creator);
        pm.setOrganisation(org);
        pm.setIsActive(true);
        memberRepo.save(pm);

        // Add manager if different from creator
        if (manager != null && !creator.getId().equals(manager.getId())) {
            ProjectMember mpm = new ProjectMember();
            mpm.setProject(saved);
            mpm.setEmployee(manager);
            mpm.setOrganisation(org);
            mpm.setIsActive(true);
            memberRepo.save(mpm);

            notificationEventPublisher.publish(
                    NotificationEventData.builder()
                            .orgId(organisationId)
                            .eventCode("PROJECT_MEMBER_ADDED")
                            .targetRole(TargetRole.EMPLOYEE)
                            .recipientEmail(manager.getEmail())
                            .recipientName(manager.getFullName())
                            .templateVariables(Map.of(
                                    "recipientName", manager.getFullName(),
                                    "projectName", project.getName(),
                                    "projectRole", "Manager",
                                    "addedBy", creator.getFullName()))
                            .build());
        }

        // Add team lead if different from creator
        if (teamLead != null && !creator.getId().equals(teamLead.getId())) {
            ProjectMember tmpm = new ProjectMember();
            tmpm.setProject(saved);
            tmpm.setEmployee(teamLead);
            tmpm.setOrganisation(org);
            tmpm.setIsActive(true);
            memberRepo.save(tmpm);

            notificationEventPublisher.publish(
                    NotificationEventData.builder()
                            .orgId(organisationId)
                            .eventCode("PROJECT_MEMBER_ADDED")
                            .targetRole(TargetRole.EMPLOYEE)
                            .recipientEmail(teamLead.getEmail())
                            .recipientName(teamLead.getFullName())
                            .templateVariables(Map.of(
                                    "recipientName", teamLead.getFullName(),
                                    "projectName", project.getName(),
                                    "projectRole", "Team Lead",
                                    "addedBy", creator.getFullName()))
                            .build());
        }

        return ProjectMapper.toDTO(saved, createdByEmpId);
    }

    // --------------------------------------------------------------------
    // 🟡 UPDATE PROJECT
    // --------------------------------------------------------------------
    @Transactional
    @Override
    public ProjectDTO updateProject(Long projectId, ProjectDTO dto, Long organisationId, Long employeeId) {

        Project project = findActiveProject(projectId, organisationId);

        if (!canEditProject(employeeId, project))
            throw new UnauthorizedActionException("You are not authorized to update this project.");

        if (dto.getName() != null)
            project.setName(dto.getName());
        if (dto.getDescription() != null)
            project.setDescription(dto.getDescription());
        if (dto.getPriority() != null)
            project.setPriority(Priority.valueOf(dto.getPriority()));
        if (dto.getStatus() != null)
            project.setStatus(ProjectStatus.valueOf(dto.getStatus()));
        if (dto.getStartDate() != null)
            project.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null)
            project.setEndDate(dto.getEndDate());
        if (dto.getActualEndDate() != null)
            project.setActualEndDate(dto.getActualEndDate());

        if (dto.getProjectManagerId() != null) {
            Employee manager = employeeRepo.findById(dto.getProjectManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
            project.setProjectManager(manager);
        }

        if (dto.getProjectTeamLeadId() != null) {
            Employee lead = employeeRepo.findById(dto.getProjectTeamLeadId())
                    .orElseThrow(() -> new ResourceNotFoundException("Team Lead not found"));
            project.setProjectTeamLead(lead);
        }

        projectRepo.save(project);

        return ProjectMapper.toDTO(project, employeeId);
    }

    // ================================================================
    // 🟢 ADD MEMBERS TO PROJECT
    // ================================================================
    @Transactional
    public void addMembers(Long projectId, List<Long> empIds, Long organisationId, Long addedById) {
        Project project = findActiveProject(projectId, organisationId);
        if (!canEditProject(addedById, project)) {
            throw new UnauthorizedActionException("You are not authorized to add members.");
        }

        Employee addedBy = employeeRepo.findById(addedById)
                .orElseThrow(() -> new ResourceNotFoundException("Added By Employee not found."));

        for (Long empId : empIds) {

            Optional<ProjectMember> existing = memberRepo.findByProjectIdAndEmployeeId(projectId, empId);

            Employee emp = employeeRepo.findById(empId)
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

            boolean shouldNotify = false;

            if (existing.isPresent()) {

                ProjectMember member = existing.get();

                if (Boolean.TRUE.equals(member.getIsActive())) {
                    continue; // already active -> no notification
                }

                member.setIsActive(true);
                member.setJoinedAt(LocalDateTime.now());
                member.setLeftAt(null);

                memberRepo.save(member);

                shouldNotify = true;
            } else {

                ProjectMember member = ProjectMember.builder()
                        .project(project)
                        .employee(emp)
                        .organisation(project.getOrganisation())
                        .joinedAt(LocalDateTime.now())
                        .isActive(true)
                        .allocationPercentage(BigDecimal.valueOf(100))
                        .role(projectRoleRepo.findByNameIgnoreCase("MEMBER").orElse(null))
                        .build();

                memberRepo.save(member);

                shouldNotify = true;
            }

            if (shouldNotify) {

                notificationEventPublisher.publish(
                        NotificationEventData.builder()
                                .orgId(organisationId)
                                .eventCode("PROJECT_MEMBER_ADDED")
                                .targetRole(TargetRole.EMPLOYEE)
                                .recipientEmail(emp.getEmail())
                                .recipientName(emp.getFullName())
                                .templateVariables(Map.of(
                                        "recipientName", emp.getFullName(),
                                        "projectName", project.getName(),
                                        "projectRole", "Member",
                                        "addedBy", addedBy.getFullName()))
                                .build());
            }
        }
    }

    @Transactional
    public void removeMember(Long projectId, Long empId, Long orgId, Long actorId) {
        Project project = findActiveProject(projectId, orgId);

        boolean isCreator = actorId.equals(project.getCreatedBy().getId());
        boolean isPM = project.getProjectManager() != null && actorId.equals(project.getProjectManager().getId());
        boolean isTL = project.getProjectTeamLead() != null && actorId.equals(project.getProjectTeamLead().getId());

        // Only Creator, PM, or TL can trigger removal
        if (!isCreator && !isPM && !isTL)
            throw new UnauthorizedActionException("Not authorized to remove members.");

        // HIERARCHY RULES
        if (empId.equals(project.getCreatedBy().getId())) {
            throw new InvalidOperationException("Project Creator cannot be removed.");
        }
        // PM can only be removed by Creator
        if (project.getProjectManager() != null && empId.equals(project.getProjectManager().getId()) && !isCreator) {
            throw new InvalidOperationException("Only the Creator can remove the Project Manager.");
        }
        // TL can be removed by Creator or PM
        if (project.getProjectTeamLead() != null && empId.equals(project.getProjectTeamLead().getId()) && !isCreator
                && !isPM) {
            throw new InvalidOperationException("Only the Creator or PM can remove the Team Lead.");
        }

        // Find and Deactivate
        ProjectMember member = memberRepo.findByProjectIdAndEmployeeIdAndIsActiveTrue(projectId, empId)
                .orElseThrow(() -> new ResourceNotFoundException("Active member not found"));

        member.setIsActive(false);
        member.setLeftAt(LocalDateTime.now()); // Mark the exit time
        memberRepo.save(member);
    }

    // --------------------------------------------------------------------
    // 🔵 GET PROJECT DETAILS
    // --------------------------------------------------------------------
    @Override
    public ProjectDTO getProjectById(Long projectId, Long organisationId, Long employeeId) {

        Project project = findActiveProject(projectId, organisationId);

        if (!isMember(employeeId, project))
            throw new UnauthorizedActionException("Access denied — You are not a member of this project.");

        return ProjectMapper.toDTO(project, employeeId);
    }

    // --------------------------------------------------------------------
    // 🧾 LIST PROJECTS
    // --------------------------------------------------------------------
    @Override
    public List<ProjectDTO> getAllProjects(Long organisationId) {
        return projectRepo.findByOrganisationIdAndIsActiveTrue(organisationId)
                .stream()
                .map(p -> ProjectMapper.toDTO(p, null))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> getProjectsByStatus(Long organisationId, ProjectStatus status) {
        return projectRepo.findByOrganisationIdAndStatusAndIsActiveTrue(organisationId, status)
                .stream().map(p -> ProjectMapper.toDTO(p, null))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> getProjectsByType(Long organisationId, ProjectType type) {
        return projectRepo.findByOrganisationIdAndProjectTypeAndIsActiveTrue(organisationId, type)
                .stream().map(p -> ProjectMapper.toDTO(p, null))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> getProjectsByDepartment(Long organisationId, Long deptId) {
        return projectRepo.findByOrganisationIdAndDepartmentIdAndIsActiveTrue(organisationId, deptId)
                .stream().map(p -> ProjectMapper.toDTO(p, null))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> getProjectsByEmployee(Long organisationId, Long empId) {
        return projectRepo.findProjectsByEmployeeInvolvement(organisationId, empId)
                .stream().map(p -> ProjectMapper.toDTO(p, empId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> searchProjects(Long organisationId, String keyword) {
        return projectRepo.searchProjectsByName(organisationId, keyword)
                .stream().map(p -> ProjectMapper.toDTO(p, null))
                .collect(Collectors.toList());
    }

    // --------------------------------------------------------------------
    // 🔴 DELETE PROJECT
    // --------------------------------------------------------------------
    @Transactional
    @Override
    public void deleteProject(Long projectId, Long organisationId, Long employeeId) {
        Project project = findActiveProject(projectId, organisationId);

        if (!canDeleteProject(employeeId, project))
            throw new UnauthorizedActionException("You are not authorized to delete this project.");

        project.setIsActive(false);
        projectRepo.save(project);
    }

    // --------------------------------------------------------------------
    // 🟣 UPDATE COMPLETION
    // --------------------------------------------------------------------
    @Transactional
    @Override
    public void updateProjectCompletion(Long projectId, Long organisationId, Long actorEmpId) {

        Project project = findActiveProject(projectId, organisationId);

        if (!canEditProject(actorEmpId, project))
            throw new UnauthorizedActionException("You are not authorized to update completion status.");

        List<Task> tasks = taskRepo.findByProjectId(projectId);
        if (tasks.isEmpty())
            throw new InvalidOperationException("No tasks found for this project.");

        long completed = tasks.stream().filter(t -> t.getStatus() == Task.TaskStatus.DONE).count();
        long total = tasks.size();
        double progress = (completed * 100.0 / total);

        if (progress == 100.0) {
            project.setStatus(ProjectStatus.COMPLETED);
            project.setActualEndDate(LocalDate.now());
        } else if (progress > 0) {
            project.setStatus(ProjectStatus.IN_PROGRESS);
        } else {
            project.setStatus(ProjectStatus.PLANNING);
        }

        projectRepo.save(project);
    }

    // --------------------------------------------------------------------
    // 🔧 PRIVATE UTILITIES
    // --------------------------------------------------------------------
    private Project findActiveProject(Long id, Long orgId) {
        return projectRepo.findByIdAndOrganisationId(id, orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found for organisation"));
    }

    private boolean canEditProject(Long empId, Project project) {
        return empId.equals(project.getCreatedBy().getId())
                || (project.getProjectManager() != null && empId.equals(project.getProjectManager().getId()))
                || (project.getProjectTeamLead() != null && empId.equals(project.getProjectTeamLead().getId()));
    }

    private boolean canDeleteProject(Long empId, Project project) {
        return empId.equals(project.getCreatedBy().getId())
                || (project.getProjectManager() != null && empId.equals(project.getProjectManager().getId()));
    }

    private boolean isMember(Long empId, Project project) {
        return canEditProject(empId, project)
                || memberRepo.existsByProjectIdAndEmployeeIdAndIsActiveTrue(project.getId(), empId);
    }
}
