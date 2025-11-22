package com.sellspark.SellsHRMS.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.OrganisationAdmin;
import java.util.List;

@Repository
public interface OrganisationAdminRepository extends JpaRepository<OrganisationAdmin, Long> {
    Optional<OrganisationAdmin> findByEmail(String email);

    List<OrganisationAdmin> findByOrganisation(Organisation organisation);

}
