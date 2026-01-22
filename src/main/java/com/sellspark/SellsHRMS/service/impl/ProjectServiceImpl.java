package com.sellspark.SellsHRMS.service.impl;

import com.sellspark.SellsHRMS.dto.dashboard.ProjectDTO;
import com.sellspark.SellsHRMS.entity.*;
import com.sellspark.SellsHRMS.entity.Project.*;
import com.sellspark.SellsHRMS.exception.InvalidOperationException;
import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
import com.sellspark.SellsHRMS.exception.UnauthorizedActionException;
import com.sellspark.SellsHRMS.mapper.ProjectMapper;
import com.sellspark.SellsHRMS.repository.*;
import com.sellspark.SellsHRMS.service.ProjectService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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




        // Auto-add creator as Project Manager and member
        ProjectMember pm = new ProjectMember();
        pm.setProject(saved);
        pm.setEmployee(creator);
        pm.setOrganisation(org);
        // pm.setRole("PROJECT_MANAGER");
        pm.setIsActive(true);
        memberRepo.save(pm);

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

        if (dto.getName() != null) project.setName(dto.getName());
        if (dto.getDescription() != null) project.setDescription(dto.getDescription());
        if (dto.getPriority() != null) project.setPriority(Priority.valueOf(dto.getPriority()));
        if (dto.getStatus() != null) project.setStatus(ProjectStatus.valueOf(dto.getStatus()));
        if (dto.getStartDate() != null) project.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) project.setEndDate(dto.getEndDate());
        if (dto.getActualEndDate() != null) project.setActualEndDate(dto.getActualEndDate());

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

    public void addProjectMember(Long projectId, Long empId){
        
    }

    // ================================================================
// 🟢 ADD MEMBERS TO PROJECT
// ================================================================
@Transactional
public void addMembers(Long projectId, List<Long> empIds, Long organisationId, Long addedById) {

    Project project = findActiveProject(projectId, organisationId);
    if (!canEditProject(addedById, project)) {
        throw new UnauthorizedActionException("You are not authorized to add members to this project.");
    }

    Organisation org = project.getOrganisation();
    List<Employee> employees = employeeRepo.findAllById(empIds);

    for (Employee emp : employees) {

        boolean alreadyExists = memberRepo.existsByProjectIdAndEmployeeIdAndIsActiveTrue(projectId, emp.getId());
        if (alreadyExists) continue; // skip duplicates

        ProjectMember member = new ProjectMember();
        member.setProject(project);
        member.setEmployee(emp);
        member.setOrganisation(org);
        member.setJoinedAt(LocalDateTime.now());
        member.setIsActive(true);
        member.setAllocationPercentage(BigDecimal.valueOf(100)); // default 100%

        // Optional: assign default project role (e.g., "MEMBER")
        ProjectRole defaultRole = projectRoleRepo.findByNameIgnoreCase("MEMBER")
                .orElse(null);
        member.setRole(defaultRole);

        memberRepo.save(member);
    }
}



    @Transactional
public void removeMember(Long projectId, Long empId, Long orgId, Long actorId) {
    Project project = findActiveProject(projectId, orgId);
    if (!canEditProject(actorId, project))
        throw new UnauthorizedActionException("You are not authorized to remove members.");

    // prevent removing core roles
    if (empId.equals(project.getCreatedBy().getId()) ||
        (project.getProjectManager() != null && empId.equals(project.getProjectManager().getId())) ||
        (project.getProjectTeamLead() != null && empId.equals(project.getProjectTeamLead().getId()))) {
        throw new InvalidOperationException("You cannot remove core project roles.");
    }

    ProjectMember member = memberRepo
        .findByProjectIdAndEmployeeId(projectId, empId)
        .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

    member.setIsActive(false);
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
        if (tasks.isEmpty()) throw new InvalidOperationException("No tasks found for this project.");

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
                .orElseThrow(() -> new ResourceNotFoundException("Project not found for organisation"))
                ;
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
