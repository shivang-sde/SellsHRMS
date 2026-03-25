package com.sellspark.SellsHRMS.dto.payroll;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSalaryAssignmentDTO {

    private Long id;

    private Long employeeId;
    private String employeeName;
    private String employeeDepartmentName;
    private String employeeCode;
    private Long organisationId;
    private Long salaryStructureId;
    private String salaryStructureName;
    private Long taxSlabId;

    private Double monthlyGrossTarget; // Calculated once during assignment
    private Double monthlyNetTarget;
    private Double annualCtc; // monthlyGrossTarget * 12

    private Double basePay;
    private Double variablePay;

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    private String remarks;
    private Boolean active;

    private List<Long> salarySlipIds; // Optional — for UI to show slip history
    
    // Add targetBreakdownJson to expose calculated breakdown
    private String targetBreakdownJson;
}
