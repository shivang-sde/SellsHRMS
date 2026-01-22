package com.sellspark.SellsHRMS.service.payroll;

import com.sellspark.SellsHRMS.dto.payroll.EmployeeSalaryAssignmentDTO;
import java.util.List;


public interface EmployeeSalaryAssignmentService {

    EmployeeSalaryAssignmentDTO assignSalaryStructure(EmployeeSalaryAssignmentDTO dto);

    EmployeeSalaryAssignmentDTO updateAssignment(Long id, EmployeeSalaryAssignmentDTO dto);

    void deactivateAssignment(Long id);

    EmployeeSalaryAssignmentDTO getAssignment(Long id);

    EmployeeSalaryAssignmentDTO getAssignmentsByEmployee(Long employeeId);

    List<EmployeeSalaryAssignmentDTO> getActiveAssignments(Long orgId);

    /**
     * Bulk-assign a salary structure to multiple employees by designation or grade.
     */
    // List<EmployeeSalaryAssignmentDTO> bulkAssign(Long structureId, String designation, String grade);
}
