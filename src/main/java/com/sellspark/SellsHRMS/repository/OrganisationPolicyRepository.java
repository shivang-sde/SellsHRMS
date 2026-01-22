package com.sellspark.SellsHRMS.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.OrganisationPolicy;


public interface OrganisationPolicyRepository extends JpaRepository<OrganisationPolicy, Long> {
    Optional<OrganisationPolicy> findByOrganisation(Organisation org);

   @Query("SELECT op FROM OrganisationPolicy op WHERE op.organisation.id = :organisationId")
Optional<OrganisationPolicy> findByOrganisationId(@Param("organisationId") Long organisationId);

}

