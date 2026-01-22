package com.sellspark.SellsHRMS.repository;

import com.sellspark.SellsHRMS.entity.Epic;
import com.sellspark.SellsHRMS.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EpicRepository extends JpaRepository<Epic, Long> {

    // List<Epic> findByProjectIdAndIsActiveTrue(Long projectId);

    // List<Epic> findByProjectId(Long projectId);

    List<Epic> findByOrganisationIdAndIsActiveTrue(Long organisationId);

    Optional<Epic> findByIdAndOrganisationId(Long id, Long organisationId);

    @Query("SELECT e FROM Epic e WHERE e.project.organisation.id = :orgId AND LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND e.isActive = true")
    List<Epic> searchEpics(@Param("orgId") Long orgId, @Param("keyword") String keyword);

    // @Query("SELECT COUNT(e) FROM Epic e WHERE e.project.id = :projectId AND e.isActive = true")
    // Long countActiveEpicsByProject(@Param("projectId") Long projectId);
}
