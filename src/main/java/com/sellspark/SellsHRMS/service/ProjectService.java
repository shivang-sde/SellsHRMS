package com.sellspark.SellsHRMS.service;

import com.sellspark.SellsHRMS.dto.dashboard.ProjectDTO;
import com.sellspark.SellsHRMS.entity.Project.ProjectStatus;
import com.sellspark.SellsHRMS.entity.Project.ProjectType;
import java.util.List;

/**
 * Project Service Interface for managing project lifecycle, 
 * member access, and permission-aware CRUD operations.
 */
public interface ProjectService {

    // ---------------------- Core CRUD ----------------------

    ProjectDTO createProject(ProjectDTO projectDTO, Long organisationId, Long createdByEmpId);

    ProjectDTO updateProject(Long projectId, ProjectDTO projectDTO, Long organisationId, Long employeeId);

    ProjectDTO getProjectById(Long projectId, Long organisationId, Long employeeId);

    void deleteProject(Long projectId, Long organisationId, Long employeeId);


    void addMembers(Long projectId,  List<Long> employeeIds, Long organisationId, Long employeeId);

     void removeMember(Long projectId, Long empId, Long orgId, Long actorId);

    // ---------------------- Retrieval / Filters ----------------------

    List<ProjectDTO> getAllProjects(Long organisationId);

    List<ProjectDTO> getProjectsByStatus(Long organisationId, ProjectStatus status);

    List<ProjectDTO> getProjectsByType(Long organisationId, ProjectType projectType);

    List<ProjectDTO> getProjectsByDepartment(Long organisationId, Long departmentId);

    List<ProjectDTO> getProjectsByEmployee(Long organisationId, Long employeeId);

    List<ProjectDTO> searchProjects(Long organisationId, String keyword);

    

    // ---------------------- Business Logic ----------------------

    /**
     * Auto-updates project completion percentage and status 
     * based on task progress.
     */
    void updateProjectCompletion(Long projectId, Long organisationId, Long actorEmpId);
}
