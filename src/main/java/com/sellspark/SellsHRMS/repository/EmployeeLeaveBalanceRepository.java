package com.sellspark.SellsHRMS.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sellspark.SellsHRMS.entity.Employee;
import com.sellspark.SellsHRMS.entity.EmployeeLeaveBalance;
import com.sellspark.SellsHRMS.entity.LeaveType;

public interface EmployeeLeaveBalanceRepository extends JpaRepository<EmployeeLeaveBalance, Long> {

    Optional<EmployeeLeaveBalance> findByEmployeeAndLeaveTypeAndLeaveYear(Employee employee,
                                                                              LeaveType leaveType,
                                                                              String leaveYear);

    List<EmployeeLeaveBalance> findByEmployeeAndLeaveYear(Employee employee, String leaveYear);

    Optional<EmployeeLeaveBalance> findTopByEmployeeIdAndLeaveTypeIdOrderByIdDesc(Long empId, Long leaveTypeId);



    // Get all balances for an employee in a financial year
List<EmployeeLeaveBalance> findByEmployeeIdAndLeaveYear(Long empId, String fy);
        // Get all balances for an organization
List<EmployeeLeaveBalance> findByOrganisationId(Long orgId);


Optional<EmployeeLeaveBalance> findByEmployeeIdAndLeaveTypeIdAndLeaveYear(Long employeeId,
                                                                          Long leaveTypeId,
                                                                          String leaveYear);

// Get balances for leave type
List<EmployeeLeaveBalance> findByLeaveTypeId(Long leaveTypeId);
}

