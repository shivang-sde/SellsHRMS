package com.sellspark.SellsHRMS.repository.payroll;

import com.sellspark.SellsHRMS.entity.payroll.SalarySlip;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalarySlipRepository extends JpaRepository<SalarySlip, Long>, JpaSpecificationExecutor<SalarySlip> {

        // 🔹 All salary slips under a specific PayRun
        List<SalarySlip> findByPayRun_Id(Long payRunId);

        List<SalarySlip> findByOrganisation_IdAndIsCreditedFalse(Long orgId);

        Page<SalarySlip> findByOrganisation_IdAndPayRun_MonthAndPayRun_Year(
                        Long orgId, Integer month, Integer year, Pageable pageable);

        Page<SalarySlip> findByOrganisation_IdAndPayRun_MonthAndPayRun_YearAndIsCredited(
                        Long orgId, Integer month, Integer year, Boolean credited, Pageable pageable);

        Page<SalarySlip> findByOrganisation_IdAndPayRun_MonthAndPayRun_YearAndEmployee_Department_Id(
                        Long orgId, Integer month, Integer year, Long departmentId, Pageable pageable);

        Page<SalarySlip> findByOrganisation_IdAndPayRun_MonthAndPayRun_YearAndEmployee_Department_IdAndIsCredited(
                        Long orgId, Integer month, Integer year, Long departmentId, Boolean credited,
                        Pageable pageable);

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
                        "components.component", // <-- fetch SalaryComponent
                        "components.statutoryComponent", // <-- fetch StatutoryComponent
                        "payRun",
                        "organisation"
        })
        Optional<SalarySlip> findById(Long id);

}
