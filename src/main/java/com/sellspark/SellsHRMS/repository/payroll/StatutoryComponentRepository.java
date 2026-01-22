package com.sellspark.SellsHRMS.repository.payroll;

import com.sellspark.SellsHRMS.entity.payroll.StatutoryComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StatutoryComponentRepository extends JpaRepository<StatutoryComponent, Long> {

    List<StatutoryComponent> findByOrganisationId(Long organisationId);


    List<StatutoryComponent> findByOrganisation_IdAndIsActiveTrue(Long orgId);

    boolean existsByCode(String code);


}
