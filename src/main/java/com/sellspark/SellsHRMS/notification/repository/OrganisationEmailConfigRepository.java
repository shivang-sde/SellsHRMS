package com.sellspark.SellsHRMS.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import com.sellspark.SellsHRMS.notification.entity.OrgEmailConfig;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganisationEmailConfigRepository extends JpaRepository<OrgEmailConfig, Long> {

    Optional<OrgEmailConfig> findByOrgIdAndIsActiveTrue(Long orgId);

    boolean existsByOrgId(Long orgId);
}
