package com.sellspark.SellsHRMS.repository.payroll;

import com.sellspark.SellsHRMS.entity.payroll.SalaryStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SalaryStructureRepository extends JpaRepository<SalaryStructure, Long> {

    @Query("SELECT DISTINCT s FROM SalaryStructure s " +
           "LEFT JOIN FETCH s.components " +
           "WHERE s.organisation.id = :orgId AND s.active = true")
    List<SalaryStructure> findByOrganisationIdAndActiveTrue(@Param("orgId") Long orgId);

    List<SalaryStructure> findByCountryCodeAndActiveTrue(String countryCode);

    boolean existsByNameAndOrganisationId(String name, Long organisationId);
}
