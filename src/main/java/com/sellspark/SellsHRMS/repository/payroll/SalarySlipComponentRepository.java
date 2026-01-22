package com.sellspark.SellsHRMS.repository.payroll;

import com.sellspark.SellsHRMS.entity.payroll.SalarySlipComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SalarySlipComponentRepository extends JpaRepository<SalarySlipComponent, Long> {

    // 🔹 All components in a particular salary slip
    List<SalarySlipComponent> findBySalarySlip_Id(Long salarySlipId);

    // 🔹 All components of a specific type (EARNING/DEDUCTION) in slip
    List<SalarySlipComponent> findBySalarySlip_IdAndComponent_Type(Long salarySlipId, String componentType);

    // 🔹 All statutory deductions in a payslip
    List<SalarySlipComponent> findBySalarySlip_IdAndIsStatutoryTrue(Long salarySlipId);
}
