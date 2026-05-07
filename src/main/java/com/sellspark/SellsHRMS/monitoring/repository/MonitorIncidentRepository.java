package com.sellspark.SellsHRMS.monitoring.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sellspark.SellsHRMS.monitoring.entity.MonitorIncident;

// MonitorIncidentRepository
public interface MonitorIncidentRepository extends JpaRepository<MonitorIncident, String> {

    @Query("SELECT i FROM MonitorIncident i WHERE i.url.id = :urlId AND i.isResolved = false")
    Optional<MonitorIncident> findActiveIncidentByUrlId(@Param("urlId") String urlId);

    @Query("SELECT i FROM MonitorIncident i WHERE i.url.id = :urlId AND i.isResolved = false")
    Optional<MonitorIncident> findActiveByUrlId(@Param("urlId") String urlId);

    Optional<MonitorIncident> findByUrlIdAndIsResolvedFalse(String urlId);

    @Query("SELECT i FROM MonitorIncident i JOIN i.url u WHERE u.organisation.id = :orgId AND i.isResolved = false")
    List<MonitorIncident> findActiveIncidentsByOrganisation(@Param("orgId") Long orgId);

    List<MonitorIncident> findByUrlIdOrderByStartedAtDesc(String urlId);

    @Query("SELECT i FROM MonitorIncident i WHERE i.url.organisation.id = :orgId ORDER BY i.startedAt DESC")
    Page<MonitorIncident> findByOrganisationId(@Param("orgId") Long orgId, Pageable pageable);

    @Query("SELECT i FROM MonitorIncident i WHERE i.url.organisation.id = :orgId AND i.isResolved = :resolved " +
            "ORDER BY i.startedAt DESC")
    Page<MonitorIncident> findByOrganisationIdAndResolved(@Param("orgId") Long orgId,
            @Param("resolved") Boolean resolved,
            Pageable pageable);

    @Query("SELECT i FROM MonitorIncident i WHERE i.url.organisation.id = :orgId AND i.isResolved = false")
    List<MonitorIncident> findActiveByOrganisationId(@Param("orgId") Long orgId);

    @Query("SELECT COUNT(i) FROM MonitorIncident i WHERE i.url.organisation.id = :orgId AND i.isResolved = false")
    Long countActiveByOrganisationId(@Param("orgId") Long orgId);
}
