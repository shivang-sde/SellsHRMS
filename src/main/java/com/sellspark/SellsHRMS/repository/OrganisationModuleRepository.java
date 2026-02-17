package com.sellspark.SellsHRMS.repository;

import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.OrganisationModule;
import com.sellspark.SellsHRMS.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrganisationModuleRepository extends JpaRepository<OrganisationModule, Long> {

    @Query("SELECT om.module.code FROM OrganisationModule om " +
            "WHERE om.organisation.id = :orgId AND om.enabled = true")
    List<String> findActiveModuleCodesByOrganisationId(@Param("orgId") Long orgId);

    List<OrganisationModule> findByOrganisationId(Long orgId);

    Optional<OrganisationModule> findByOrganisationAndModule(Organisation org, Module module);

}
