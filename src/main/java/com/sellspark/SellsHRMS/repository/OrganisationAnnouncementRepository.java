package com.sellspark.SellsHRMS.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.OrganisationAnnouncement;

public interface OrganisationAnnouncementRepository extends JpaRepository<OrganisationAnnouncement, Long> {
    @Query("SELECT a FROM OrganisationAnnouncement a WHERE a.organisation.id = :orgId AND a.isActive = true AND " +
           "(a.validUntil IS NULL OR a.validUntil > :now)")
    List<OrganisationAnnouncement> findActiveByOrganisationId(@Param("orgId") Long orgId, @Param("now") LocalDateTime now);


    List<OrganisationAnnouncement> findByOrganisation(Organisation organisation);
    List<OrganisationAnnouncement> findByOrganisationOrderByCreatedAtDesc(Organisation organisation);
    List<OrganisationAnnouncement> findByOrganisationAndIsActiveTrueOrderByCreatedAtDesc(Organisation organisation);
}