package com.sellspark.SellsHRMS.repository.payroll;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sellspark.SellsHRMS.entity.payroll.SalarySlipComponentChange;

@Repository
public interface SalarySlipComponentChangeRepository extends JpaRepository<SalarySlipComponentChange, Long> {

    List<SalarySlipComponentChange> findBySalarySlip_Id(Long salarySlipId);
}
