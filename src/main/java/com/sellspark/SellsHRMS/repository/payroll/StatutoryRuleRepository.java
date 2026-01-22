package com.sellspark.SellsHRMS.repository.payroll;

import com.sellspark.SellsHRMS.entity.payroll.StatutoryRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface StatutoryRuleRepository extends JpaRepository<StatutoryRule, Long> {

    List<StatutoryRule> findByStatutoryComponentId(Long componentId);

//     List<StatutoryRule> findByOrganisation_IdAndActiveTrue(Long orgId);
// @Query("""
//     SELECT r FROM StatutoryRule r 
//     WHERE r.active = true 
//     AND r.statutoryComponent.organisation.id = :orgId 
// """)
// List<StatutoryRule> findActiveRulesByOrgIdAnd(@Param("orgId") Long orgId);


    List<StatutoryRule> findByEffectiveFromLessThanEqualAndEffectiveToGreaterThanEqual(LocalDate from, LocalDate to);
}
