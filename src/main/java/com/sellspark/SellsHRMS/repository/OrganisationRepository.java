package com.sellspark.SellsHRMS.repository;

import com.sellspark.SellsHRMS.entity.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganisationRepository extends JpaRepository<Organisation, Long> {

    Optional<Organisation> findByName(String name);

    Optional<Organisation> findByDomain(String domain);
}
