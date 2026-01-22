// package com.sellspark.SellsHRMS.service.impl;

// import com.sellspark.SellsHRMS.dto.project.EpicDTO;
// import com.sellspark.SellsHRMS.entity.*;
// import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
// import com.sellspark.SellsHRMS.exception.UnauthorizedActionException;
// import com.sellspark.SellsHRMS.mapper.EpicMapper;
// import com.sellspark.SellsHRMS.repository.*;
// import com.sellspark.SellsHRMS.service.EpicService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
// import java.util.List;
// import java.util.stream.Collectors;

// /**
//  * Complete Epic Service implementation with permission-aware logic.
//  */
// @Service
// @RequiredArgsConstructor
// public class EpicServiceImpl implements EpicService {

//     private final EpicRepository epicRepo;
//     private final ProjectRepository projectRepo;
//     private final EmployeeRepository employeeRepo;
//     private final ProjectMemberRepository memberRepo;
//     private final OrganisationRepository organisationRepo;

//     // --------------------------------------------------------------------
//     // 🟢 CREATE EPIC
//     // --------------------------------------------------------------------
//     @Transactional
//     @Override
//     public EpicDTO createEpic(EpicDTO dto, Long orgId, Long projectId, Long createdByEmpId) {

//         Project project = projectRepo.findByIdAndOrganisationId(projectId, orgId)
//                 .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

//         if (!isMember(createdByEmpId, project))
//             throw new UnauthorizedActionException("You are not authorized to create an epic in this project.");

//         Epic epic = EpicMapper.toEntity(dto, project);
//         Epic saved = epicRepo.save(epic);
//         return EpicMapper.toDTO(saved, createdByEmpId);
//     }

//     // --------------------------------------------------------------------
//     // 🟡 UPDATE EPIC
//     // --------------------------------------------------------------------
//     @Transactional
//     @Override
//     public EpicDTO updateEpic(Long epicId, EpicDTO dto, Long orgId, Long empId) {
//         Epic epic = findEpic(epicId, orgId);

//         if (!canEditEpic(empId, epic.getProject()))
//             throw new UnauthorizedActionException("You are not authorized to update this epic.");

//         if (dto.getName() != null) epic.setName(dto.getName());
//         if (dto.getDescription() != null) epic.setDescription(dto.getDescription());
//         if (dto.getStatus() != null)
//             epic.setStatus(Epic.EpicStatus.valueOf(dto.getStatus()));

//         epicRepo.save(epic);
//         return EpicMapper.toDTO(epic, empId);
//     }

//     // --------------------------------------------------------------------
//     // 🔵 GET EPIC BY ID
//     // --------------------------------------------------------------------
//     @Override
//     public EpicDTO getEpicById(Long epicId, Long orgId, Long empId) {
//         Epic epic = findEpic(epicId, orgId);

//         if (!isMember(empId, epic.getProject()))
//             throw new UnauthorizedActionException("You are not a member of this project.");

//         return EpicMapper.toDTO(epic, empId);
//     }

//     // --------------------------------------------------------------------
//     // 🧾 LIST EPICS BY PROJECT
//     // --------------------------------------------------------------------
//     // @Override
//     // public List<EpicDTO> getEpicsByProject(Long projectId, Long orgId, Long empId) {
//     //     Project project = projectRepo.findByIdAndOrganisationId(projectId, orgId)
//     //             .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

//     //     if (!isMember(empId, project))
//     //         throw new UnauthorizedActionException("Access denied to project epics.");

//     //     return epicRepo.findByProjectIdAndIsActiveTrue(projectId)
//     //             .stream().map(e -> EpicMapper.toDTO(e, empId))
//     //             .collect(Collectors.toList());
//     // }

//     // --------------------------------------------------------------------
//     // 🔴 DELETE EPIC
//     // --------------------------------------------------------------------
//     @Transactional
//     @Override
//     public void deleteEpic(Long epicId, Long orgId, Long empId) {
//         Epic epic = findEpic(epicId, orgId);

//         if (!canDeleteEpic(empId, epic.getProject()))
//             throw new UnauthorizedActionException("You are not authorized to delete this epic.");

//         epicRepo.delete(epic);
//     }

//     // --------------------------------------------------------------------
//     // 🔧 PRIVATE UTILITIES
//     // --------------------------------------------------------------------
//     private Epic findEpic(Long epicId, Long orgId) {
//         Epic epic = epicRepo.findById(epicId)
//                 .orElseThrow(() -> new ResourceNotFoundException("Epic not found"));
//         if (!epic.getProject().getOrganisation().getId().equals(orgId))
//             throw new UnauthorizedActionException("Cross-organisation access denied.");
//         return epic;
//     }

//     private boolean isMember(Long empId, Project project) {
//         return canEditEpic(empId, project)
//                 || memberRepo.existsByProjectIdAndEmployeeIdAndIsActiveTrue(project.getId(), empId);
//     }

//     private boolean canEditEpic(Long empId, Project project) {
//         return empId.equals(project.getCreatedBy().getId())
//                 || (project.getProjectManager() != null && empId.equals(project.getProjectManager().getId()))
//                 || (project.getProjectTeamLead() != null && empId.equals(project.getProjectTeamLead().getId()));
//     }

//     private boolean canDeleteEpic(Long empId, Project project) {
//         return empId.equals(project.getCreatedBy().getId())
//                 || (project.getProjectManager() != null && empId.equals(project.getProjectManager().getId()));
//     }
// }
