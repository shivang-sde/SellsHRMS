package com.sellspark.SellsHRMS.repository;

import com.sellspark.SellsHRMS.entity.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MilestoneRepository extends JpaRepository<Milestone, Long> {

    // List<Milestone> findByOrganisationIdAndIsActiveTrue(Long organisationId);

    // List<Milestone> findByProjectIdAndIsActiveTrue(Long projectId);

    @Query("SELECT m FROM Milestone m WHERE m.targetDate BETWEEN :start AND :end AND m.organisation.id = :orgId AND m.isActive = true")
    List<Milestone> findMilestonesWithinRange(@Param("orgId") Long orgId, @Param("start") LocalDate start, @Param("end") LocalDate end);

    // @Query("SELECT COUNT(m) FROM Milestone m WHERE m.project.id = :projectId AND m.status = :status AND m.isActive = true")
    // Long countCompletedByProject(@Param("projectId") Long projectId, @Param("status") Milestone.MilestoneStatus status);
}
