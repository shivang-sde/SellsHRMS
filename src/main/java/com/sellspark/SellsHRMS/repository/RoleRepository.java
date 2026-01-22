package com.sellspark.SellsHRMS.repository;

import com.sellspark.SellsHRMS.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

       List<Role> findByOrganisation_Id(Long organisationId);

    Optional<Role> findByOrganisation_IdAndNameIgnoreCase(Long organisationId, String name);
    
     List<Role> findByOrganisationId(Long organisationId);
    Optional<Role> findByOrganisationIdAndNameIgnoreCase(Long organisationId, String name);

}
