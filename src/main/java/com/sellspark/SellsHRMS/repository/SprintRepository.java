package com.sellspark.SellsHRMS.repository;

import com.sellspark.SellsHRMS.entity.Project;
import com.sellspark.SellsHRMS.entity.Sprint;
import com.sellspark.SellsHRMS.entity.Sprint.SprintStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, Long> {

    // List<Sprint> findByEpicIdAndIsActiveTrue(Long epicId);

    // List<Sprint> findByProjectId(Long projectId);

    List<Sprint> findByOrganisationIdAndIsActiveTrue(Long organisationId);

    Optional<Sprint> findByIdAndOrganisationId(Long id, Long organisationId);

    List<Sprint> findByStatusAndOrganisationId(SprintStatus status, Long organisationId);

    @Query("SELECT s FROM Sprint s WHERE s.startDate <= :today AND s.endDate >= :today AND s.isActive = true AND s.organisation.id = :orgId")
    List<Sprint> findActiveSprints(@Param("orgId") Long orgId, @Param("today") LocalDate today);

    // @Query("SELECT COUNT(s) FROM Sprint s WHERE s.epic.id = :epicId AND s.isActive = true")
    // Long countByEpic(@Param("epicId") Long epicId);
}
