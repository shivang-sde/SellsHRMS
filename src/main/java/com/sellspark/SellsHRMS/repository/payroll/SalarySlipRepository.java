package com.sellspark.SellsHRMS.repository.payroll;

import com.sellspark.SellsHRMS.dto.payroll.SalarySlipDTO;
import com.sellspark.SellsHRMS.entity.payroll.SalarySlip;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalarySlipRepository extends JpaRepository<SalarySlip, Long> {

    // 🔹 All salary slips under a specific PayRun
    List<SalarySlip> findByPayRun_Id(Long payRunId);

    // 🔹 All salary slips for a given employee (for My Payslips page)
    List<SalarySlip> findByEmployee_IdOrderByFromDateDesc(Long employeeId);

    List<SalarySlip> findByEmployee_IdOrderByPayRun_YearDescPayRun_MonthDesc(Long empId);

    // 🔹 Fetch single slip by employee & payrun (to prevent duplicate slips)
    Optional<SalarySlip> findByEmployee_IdAndPayRun_Id(Long employeeId, Long payRunId);

    // 🔹 All slips for organisation via PayRun relation
    List<SalarySlip> findByPayRun_Organisation_Id(Long organisationId);
    
   // Fetch everything needed for PDF/DTO building
    @EntityGraph(attributePaths = {
        "employee",
        "employee.department",
        "employee.designation",
        "components",
        "components.component",           // <-- fetch SalaryComponent
        "components.statutoryComponent",  // <-- fetch StatutoryComponent
        "payRun",
        "organisation"
    })
    Optional<SalarySlip> findById(Long id);

}
