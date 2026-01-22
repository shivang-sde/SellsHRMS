package com.sellspark.SellsHRMS.service;

import com.sellspark.SellsHRMS.entity.ProjectRolePermission.ProjectPermission;
import com.sellspark.SellsHRMS.repository.ProjectMemberRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class
 ProjectPermissionService {

    private final ProjectMemberRepository projectMemberRepo;

    /**
     * Check if an employee has a specific permission on a project
     */
    public boolean hasPermission(Long projectId, Long employeeId, ProjectPermission permission) {
        return projectMemberRepo.existsByProjectIdAndEmployeeIdAndRolePermissionsPermission(
                projectId, employeeId, permission);
    }
}
