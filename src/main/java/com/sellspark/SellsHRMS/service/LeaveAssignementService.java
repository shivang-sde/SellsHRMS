package com.sellspark.SellsHRMS.service;

import java.util.Map;

public interface LeaveAssignementService {
    
    // Assign leave type to employees
// LeaveAssignmentResponseDTO assignLeaveType(LeaveAssignmentRequestDTO request);

// Get leave assignments with filters
// List<LeaveAssignmentResponseDTO> getLeaveAssignments(Long orgId, Long leaveTypeId, Long employeeId);

// Get employee's leave assignments
// List<LeaveAssignmentResponseDTO> getEmployeeLeaveAssignments(Long empId);

// Get employees assigned to a leave type
// List<EmployeeBasicInfo> getLeaveTypeEmployees(Long leaveTypeId);

// Update leave assignment
// LeaveAssignmentResponseDTO updateLeaveAssignment(Long id, Map<String, Object> updates);

// Revoke leave assignment
void revokeLeaveAssignment(Long assignmentId);

// Bulk assign leave type
// List<LeaveAssignmentResponseDTO> bulkAssignLeaveType(Long orgId, Long leaveTypeId, List<Long> empIds);

// Get leave type statistics
Map<String, Object> getLeaveTypeStats(Long leaveTypeId);
}
