package com.sellspark.SellsHRMS.repository;

import com.sellspark.SellsHRMS.entity.Project;
import com.sellspark.SellsHRMS.entity.Project.ProjectStatus;
import com.sellspark.SellsHRMS.entity.Project.ProjectType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByOrganisationIdAndIsActiveTrue(Long organisationId);

    Optional<Project> findByIdAndOrganisationId(Long id, Long organisationId);

    List<Project> findByOrganisationIdAndStatusAndIsActiveTrue(Long organisationId, ProjectStatus status);

    List<Project> findByOrganisationIdAndProjectTypeAndIsActiveTrue(Long organisationId, ProjectType projectType);

    List<Project> findByOrganisationIdAndDepartmentIdAndIsActiveTrue(Long organisationId, Long departmentId);

    @Query("SELECT p FROM Project p WHERE p.projectManager.id = :employeeId AND p.isActive = true")
    List<Project> findByProjectManagerId(@Param("employeeId") Long employeeId);

    @Query("SELECT DISTINCT p FROM Project p " +
           "LEFT JOIN p.projectMembers pm " +
           "WHERE p.organisation.id = :orgId " +
           "AND (p.projectManager.id = :empId OR pm.employee.id = :empId) " +
           "AND p.isActive = true")
    List<Project> findProjectsByEmployeeInvolvement(@Param("orgId") Long orgId, @Param("empId") Long empId);

    @Query("SELECT COUNT(p) FROM Project p WHERE p.organisation.id = :orgId AND p.status = :status AND p.isActive = true")
    Long countByOrganisationIdAndStatus(@Param("orgId") Long orgId, @Param("status") ProjectStatus status);

    @Query("SELECT p FROM Project p WHERE p.organisation.id = :orgId " +
           "AND LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "AND p.isActive = true")
    List<Project> searchProjectsByName(@Param("orgId") Long orgId, @Param("keyword") String keyword);
}