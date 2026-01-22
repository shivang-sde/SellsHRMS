package com.sellspark.SellsHRMS.repository.payroll;

import com.sellspark.SellsHRMS.entity.payroll.SalaryComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SalaryComponentRepository extends JpaRepository<SalaryComponent, Long> {

    List<SalaryComponent> findByOrganisationId(Long organisationId);

     List<SalaryComponent> findByOrganisationIdAndActiveTrue(Long orgId);

    //  List<SalaryComponent> findByOrganisation_CountryCodeAndActiveTrue(String code);

    // List<SalaryComponent> findByCountryCode(String countryCode);

    List<SalaryComponent> findByActiveTrue();

    boolean existsByNameAndOrganisationId(String name, Long organisationId);
}
