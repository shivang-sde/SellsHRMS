package com.sellspark.SellsHRMS.repository.payroll;

import com.sellspark.SellsHRMS.entity.payroll.IncomeTaxRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IncomeTaxRuleRepository extends JpaRepository<IncomeTaxRule, Long> {

    List<IncomeTaxRule> findByTaxSlabId(Long taxSlabId);
}
