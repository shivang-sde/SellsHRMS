package com.sellspark.SellsHRMS.repository.payroll;

import com.sellspark.SellsHRMS.entity.payroll.IncomeTaxSlab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IncomeTaxSlabRepository extends JpaRepository<IncomeTaxSlab, Long> {

    List<IncomeTaxSlab> findByOrganisation_CountryCodeAndIsActiveTrue(String countryCode);

    List<IncomeTaxSlab> findByOrganisation_Id(Long orgId);

    List<IncomeTaxSlab> findByOrganisation_IdAndCountryCodeAndIsActiveTrue(Long orgId, String countryCode);

    Optional<IncomeTaxSlab> findByCountryCodeAndEffectiveFromLessThanEqualAndEffectiveToGreaterThanEqual(String countryCode, LocalDate from, LocalDate to);

    boolean existsByNameAndCountryCode(String name, String countryCode);
}
