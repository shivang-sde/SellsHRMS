package com.sellspark.SellsHRMS.repository.payroll;

import com.sellspark.SellsHRMS.entity.payroll.EmployeeSalaryAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeSalaryAssignmentRepository extends JpaRepository<EmployeeSalaryAssignment, Long> {

    Optional<EmployeeSalaryAssignment> findByEmployeeIdAndActiveTrue(Long employeeId);

    List<EmployeeSalaryAssignment> findByOrganisationIdAndActiveTrue(Long orgId);


    Optional<EmployeeSalaryAssignment> findByEmployeeIdAndEffectiveFromLessThanEqualAndEffectiveToGreaterThanEqual(
            Long employeeId, LocalDate from, LocalDate to);

    List<EmployeeSalaryAssignment> findBySalaryStructureId(Long structureId);
}
