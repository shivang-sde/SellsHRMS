package com.sellspark.SellsHRMS.service;

import com.sellspark.SellsHRMS.dto.attendance.MonthlySummaryDTO;
import com.sellspark.SellsHRMS.dto.leave.EmployeeLeaveBalanceDTO;
import com.sellspark.SellsHRMS.dto.leave.LeaveRequestDTO;
import com.sellspark.SellsHRMS.dto.leave.LeaveResponseDTO;
import com.sellspark.SellsHRMS.dto.leave.LeaveTypeResponseDTO;
import com.sellspark.SellsHRMS.entity.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface LeaveService {

    // ============ Leave Type Management ============
    // LeaveType createLeaveType(LeaveType leaveType, Long orgId);
    // LeaveType updateLeaveType(Long id, LeaveType leaveType, Long orgId);
    // void deleteLeaveType(Long id, Long orgId);
    // LeaveType getLeaveTypeById(Long id, Long orgId);
    // List<LeaveType> getAllLeaveTypes(Long orgId);
    // List<LeaveType> getVisibleLeaveTypesForEmployees(Long orgId);

    // ============ Employee Leave Applications ============
    LeaveResponseDTO applyLeave(Long orgId, Long employeeId, LeaveRequestDTO request);
    LeaveResponseDTO updateLeave(Long leaveId, LeaveRequestDTO leave, Long employeeId, Long orgId);
    void cancelLeave(Long leaveId, Long employeeId, Long orgId);
    LeaveResponseDTO approveLeave(Long leaveId, Long approverId, String remarks, Long orgId);
    LeaveRequestDTO rejectLeave(Long leaveId, Long approverId, String remarks, Long orgId);
    LeaveResponseDTO getLeaveById(Long id, Long orgId);

    // ============ Retrieval ============
    List<LeaveResponseDTO> getAllLeaves(Long orgId);
    List<LeaveResponseDTO> getEmployeeLeaves(Long employeeId, String financialYear);
    List<LeaveResponseDTO> getPendingLeaves(Long orgId);
    List<LeaveResponseDTO> getLeavesByStatus(Long orgId, String status);
    List<LeaveResponseDTO> getLeavesBetweenDates(Long orgId, LocalDate from, LocalDate to);

    // ============ Leave Balance ============
    EmployeeLeaveBalance getEmployeeLeaveBalance(Long employeeId, Long leaveTypeId, String financialYear);

    
   List<EmployeeLeaveBalanceDTO> getEmployeeAllBalances(Long employeeId, String financialYear);
    void initializeLeaveBalancesForEmployee(Long employeeId, Long orgId, String financialYear);
    List<EmployeeLeaveBalanceDTO> getOrgEmployeeLeaveBalances(Long orgId);


    // ============ Accrual & Carry Forward ============
    void accrueMonthlyLeaves(Long orgId, String financialYear);

    // ============ Utilities ============
    // Double calculateLeaveDays(LocalDate startDate, LocalDate endDate, Long orgId);
    double calculateProratedBalance(LocalDate joiningDate, LeaveType type, OrganisationPolicy policy);
     void validateLeaveAgainstTypePolicy(Employee emp, LeaveType type, LeaveRequestDTO req, double days, OrganisationPolicy policy);

    LocalDate getFYStartDate(Long orgId);
    LocalDate getFYEndDate(Long orgId);
    LocalDate getLeaveYearStartDate(Long orgId);
    LocalDate getLeaveYearEndDate(Long orgId);
    String getCurrentFinancialYear(Long orgId);
    String getCurrentLeaveYear(Long orgId);
    void validateLeaveDates(LocalDate start, LocalDate end);
    double calculateLeaveDays(LeaveRequestDTO req, Long orgId, LeaveType leaveType);

    // ============ Analytics ============
    Map<String, Object> getLeaveStatistics(Long orgId);
    Map<String, Object> getEmployeeLeaveStats(Long employeeId, String financialYear);


    void initializeNewLeaveTypeBalances(LeaveType leaveType, String financialYear, Long orgId);

    // boolean existsOverlappingLeave(Long employeeId, LocalDate startDate, LocalDate endDate);




    // Dashboard methods
// EmployeeLeaveDashboardDTO getEmployeeLeaveDashboard(Long empId, String fy);
// ManagerDashboardDTO getManagerDashboard(Long orgId);

// Leave type methods
// List<LeaveTypeCardDTO> getEmployeeLeaveTypesWithStats(Long empId, String fy);
// List<LeaveTypeResponseDTO> getAvailableLeaveTypesForEmployee(Long empId);

// Summary and eligibility
// MonthlySummaryDTO getMonthlyLeaveSummary(Long empId, Integer month, Integer year);
// LeaveEligibilityDTO checkLeaveEligibility(Long empId, Long leaveTypeId, LocalDate start, LocalDate end);

// Calendar and history
// TeamLeaveCalendarDTO getTeamLeaveCalendar(Long orgId, Long managerId, String month, String year);
// List<LeaveResponseDTO> getEmployeeLeaveHistory(Long empId, Long leaveTypeId, String status, String fy);

// Counts
// LeaveCountsDTO getLeaveCountsByStatus(Long orgId);
}
