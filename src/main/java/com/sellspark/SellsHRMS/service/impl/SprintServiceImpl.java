// package com.sellspark.SellsHRMS.service.impl;

// import com.sellspark.SellsHRMS.dto.project.SprintDTO;
// import com.sellspark.SellsHRMS.entity.*;
// import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
// import com.sellspark.SellsHRMS.exception.UnauthorizedActionException;
// import com.sellspark.SellsHRMS.mapper.SprintMapper;
// import com.sellspark.SellsHRMS.repository.*;
// import com.sellspark.SellsHRMS.service.SprintService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
// import java.util.List;
// import java.util.stream.Collectors;

// @Service
// @RequiredArgsConstructor
// public class SprintServiceImpl implements SprintService {

//     private final SprintRepository sprintRepo;
//     private final ProjectRepository projectRepo;
//     private final ProjectMemberRepository memberRepo;
//     private final EmployeeRepository employeeRepo;

//     // --------------------------------------------------------------------
//     // 🟢 CREATE SPRINT
//     // --------------------------------------------------------------------
//     @Transactional
//     @Override
//     public SprintDTO createSprint(SprintDTO dto, Long orgId, Long projectId, Long createdByEmpId) {
//         Project project = projectRepo.findByIdAndOrganisationId(projectId, orgId)
//                 .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

//         if (!isMember(createdByEmpId, project))
//             throw new UnauthorizedActionException("You are not authorized to create a sprint in this project.");

//         Sprint sprint = SprintMapper.toEntity(dto, project);
//         Sprint saved = sprintRepo.save(sprint);
//         return SprintMapper.toDTO(saved, createdByEmpId);
//     }

//     // --------------------------------------------------------------------
//     // 🟡 UPDATE SPRINT
//     // --------------------------------------------------------------------
//     @Transactional
//     @Override
//     public SprintDTO updateSprint(Long sprintId, SprintDTO dto, Long orgId, Long empId) {
//         Sprint sprint = findSprint(sprintId, orgId);

//         if (!canEditSprint(empId, sprint.getProject()))
//             throw new UnauthorizedActionException("You are not authorized to update this sprint.");

//         if (dto.getName() != null) sprint.setName(dto.getName());
//         if (dto.getStartDate() != null) sprint.setStartDate(dto.getStartDate());
//         if (dto.getEndDate() != null) sprint.setEndDate(dto.getEndDate());
//         if (dto.getStatus() != null)
//             sprint.setStatus(Sprint.SprintStatus.valueOf(dto.getStatus()));

//         sprintRepo.save(sprint);
//         return SprintMapper.toDTO(sprint, empId);
//     }

//     // --------------------------------------------------------------------
//     // 🔵 GET SPRINT BY ID
//     // --------------------------------------------------------------------
//     @Override
//     public SprintDTO getSprintById(Long sprintId, Long orgId, Long empId) {
//         Sprint sprint = findSprint(sprintId, orgId);
//         if (!isMember(empId, sprint.getProject()))
//             throw new UnauthorizedActionException("You are not a member of this project.");
//         return SprintMapper.toDTO(sprint, empId);
//     }

//     // --------------------------------------------------------------------
//     // 🧾 LIST SPRINTS BY PROJECT
//     // --------------------------------------------------------------------
//     @Override
//     public List<SprintDTO> getSprintsByProject(Long projectId, Long orgId, Long empId) {
//         Project project = projectRepo.findByIdAndOrganisationId(projectId, orgId)
//                 .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

//         if (!isMember(empId, project))
//             throw new UnauthorizedActionException("Access denied to project sprints.");

//         return sprintRepo.findByProjectId(projectId)
//                 .stream()
//                 .map(s -> SprintMapper.toDTO(s, empId))
//                 .collect(Collectors.toList());
//     }

//     // --------------------------------------------------------------------
//     // 🔴 DELETE SPRINT
//     // --------------------------------------------------------------------
//     @Transactional
//     @Override
//     public void deleteSprint(Long sprintId, Long orgId, Long empId) {
//         Sprint sprint = findSprint(sprintId, orgId);

//         if (!canDeleteSprint(empId, sprint.getProject()))
//             throw new UnauthorizedActionException("You are not authorized to delete this sprint.");

//         sprintRepo.delete(sprint);
//     }

//     // --------------------------------------------------------------------
//     // 🔧 PRIVATE UTILITIES
//     // --------------------------------------------------------------------
//     private Sprint findSprint(Long sprintId, Long orgId) {
//         Sprint sprint = sprintRepo.findById(sprintId)
//                 .orElseThrow(() -> new ResourceNotFoundException("Sprint not found"));
//         if (!sprint.getProject().getOrganisation().getId().equals(orgId))
//             throw new UnauthorizedActionException("Cross-organisation access denied.");
//         return sprint;
//     }

//     private boolean isMember(Long empId, Project project) {
//         return canEditSprint(empId, project)
//                 || memberRepo.existsByProjectIdAndEmployeeIdAndIsActiveTrue(project.getId(), empId);
//     }

//     private boolean canEditSprint(Long empId, Project project) {
//         return empId.equals(project.getCreatedBy().getId())
//                 || (project.getProjectManager() != null && empId.equals(project.getProjectManager().getId()))
//                 || (project.getProjectTeamLead() != null && empId.equals(project.getProjectTeamLead().getId()));
//     }

//     private boolean canDeleteSprint(Long empId, Project project) {
//         return empId.equals(project.getCreatedBy().getId())
//                 || (project.getProjectManager() != null && empId.equals(project.getProjectManager().getId()));
//     }
// }
