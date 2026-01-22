package com.sellspark.SellsHRMS.service.helper;

import com.sellspark.SellsHRMS.entity.ProjectRole;
import com.sellspark.SellsHRMS.repository.ProjectRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectRoleResolver {

    private final ProjectRoleRepository projectRoleRepo;

    /**
     * Resolve a ProjectRole by name, scoped to organisation.
     * If not found, throws a descriptive exception.
     */
    public ProjectRole resolveByName(String roleName, Long organisationId) {
        if (roleName == null || roleName.isBlank())
            throw new IllegalArgumentException("Role name must not be null or empty");

        return projectRoleRepo.findByNameIgnoreCaseAndOrganisationId(roleName.trim(), organisationId)
                .orElseThrow(() -> new RuntimeException("Role '" + roleName + "' not found in organisation " + organisationId));
    }

    /**
     * Resolve a ProjectRole by ID.
     */
    public ProjectRole resolveById(Long roleId) {
        if (roleId == null)
            throw new IllegalArgumentException("Role ID must not be null");

        return projectRoleRepo.findById(roleId)
                .orElseThrow(() -> new RuntimeException("ProjectRole with ID " + roleId + " not found"));
    }
}
