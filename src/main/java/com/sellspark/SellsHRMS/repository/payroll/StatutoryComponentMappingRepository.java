package com.sellspark.SellsHRMS.repository.payroll;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sellspark.SellsHRMS.entity.payroll.StatutoryComponentMapping;

@Repository
public interface StatutoryComponentMappingRepository extends JpaRepository<StatutoryComponentMapping, Long> {

    List<StatutoryComponentMapping> findByOrganisationIdAndActiveTrue(Long organisationId);

    List<StatutoryComponentMapping> findByOrganisationIdAndCountryCodeAndActiveTrue(Long organisationId, String countryCode);

    List<StatutoryComponentMapping> findByStatutoryComponentIdAndOrganisationId(Long statutoryComponentId, Long organisationId);

    List<StatutoryComponentMapping> findBySalaryComponentIdAndOrganisationId(Long salaryComponentId, Long organisationId);

    boolean existsByStatutoryComponentIdAndSalaryComponentIdAndOrganisationId(
            Long statutoryComponentId, Long salaryComponentId, Long organisationId
    );
}
