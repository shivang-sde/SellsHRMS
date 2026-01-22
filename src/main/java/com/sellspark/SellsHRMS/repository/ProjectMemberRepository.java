package com.sellspark.SellsHRMS.repository;

import com.sellspark.SellsHRMS.entity.ProjectMember;
import com.sellspark.SellsHRMS.entity.ProjectRolePermission.ProjectPermission;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    @Query("""
    SELECT COUNT(pm) > 0 
    FROM ProjectMember pm
    JOIN pm.role pr
    JOIN pr.permissions perm
    WHERE pm.project.id = :projectId
      AND pm.employee.id = :employeeId
      AND perm.permission = :permission
      AND pm.isActive = true
    """)
    boolean existsByProjectIdAndEmployeeIdAndRolePermissionsPermission(
            @Param("projectId") Long projectId,
            @Param("employeeId") Long employeeId,
            @Param("permission") ProjectPermission permission
    );

    Optional<ProjectMember> findByProjectIdAndEmployeeIdAndIsActiveTrue(Long projectId, Long empId);

    List<ProjectMember> findByProjectIdAndIsActiveTrue(Long projectId);
    List<ProjectMember> findByEmployeeIdAndIsActiveTrue(Long empId);

    boolean existsByProjectIdAndEmployeeIdAndIsActiveTrue(Long projectId, Long empId);


    @Query("SELECT pm FROM ProjectMember pm WHERE pm.project.id = :projectId AND pm.isActive = true")
    List<ProjectMember> findActiveMembersByProject(@Param("projectId") Long projectId);

    Optional<ProjectMember> findByProjectIdAndEmployeeId(Long projectId, Long employeeId);

    @Query("SELECT COUNT(pm) FROM ProjectMember pm WHERE pm.project.id = :projectId AND pm.isActive = true")
    Long countActiveMembersByProject(@Param("projectId") Long projectId);
}
