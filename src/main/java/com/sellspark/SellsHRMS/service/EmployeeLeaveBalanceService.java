package com.sellspark.SellsHRMS.service;

import com.sellspark.SellsHRMS.entity.EmployeeLeaveBalance;
import com.sellspark.SellsHRMS.entity.Employee;
import com.sellspark.SellsHRMS.entity.LeaveType;

import java.util.List;

public interface EmployeeLeaveBalanceService {

    // Initialize new employee leave balances at joining or start of FY
    void initializeBalancesForEmployee(Employee employee, Long orgId, String financialYear);

    // Monthly accrual job
    void processMonthlyAccrual(Long orgId, String financialYear);

    // Carry forward job at FY end
    void carryForwardBalances(Long orgId, String currentFY, String nextFY);

    // Update balance on leave approval or cancellation
    void updateBalance(Employee employee, LeaveType leaveType, String financialYear, Double leaveDays, boolean restore);

    // Fetch balance summary
    List<EmployeeLeaveBalance> getBalancesForEmployee(Long employeeId, String financialYear);

}
