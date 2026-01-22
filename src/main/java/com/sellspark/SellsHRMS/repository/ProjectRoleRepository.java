package com.sellspark.SellsHRMS.repository;

import com.sellspark.SellsHRMS.entity.ProjectRole;
import com.sellspark.SellsHRMS.entity.ProjectRolePermission;
import com.sellspark.SellsHRMS.entity.ProjectRolePermission.ProjectPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRoleRepository extends JpaRepository<ProjectRole, Long> {

    List<ProjectRole> findByProjectIdAndIsActiveTrue(Long projectId);

    @Query("""
        SELECT pr FROM ProjectRole pr 
        JOIN pr.permissions p 
        WHERE pr.project.id = :projectId AND p.permission = :permission AND pr.isActive = true
    """)
    List<ProjectRole> findRolesByProjectAndPermission(
            @Param("projectId") Long projectId,
            @Param("permission") ProjectPermission permission
    );

    Optional<ProjectRole> findByNameIgnoreCase(String name);

    Optional<ProjectRole> findByNameIgnoreCaseAndOrganisationId(String name, Long organisationId);
}