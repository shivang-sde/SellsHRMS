package com.sellspark.SellsHRMS.repository;

import com.sellspark.SellsHRMS.entity.OrganisationModule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrganisationModuleRepository extends JpaRepository<OrganisationModule, Long> {
    List<OrganisationModule> findByOrganisationId(Long orgId);
}
