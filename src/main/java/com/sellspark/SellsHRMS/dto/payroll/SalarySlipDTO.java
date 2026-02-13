package com.sellspark.SellsHRMS.dto.payroll;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.sellspark.SellsHRMS.entity.payroll.SalarySlip.SlipStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalarySlipDTO {

    private Long id;

    private Long employeeId;
    private String employeeName;
    private String employeeCode;
    private Long assignmentId;

    private Long departmentId;
    private String departmentName;
    private Long designationId;
    private String designationName;
    private String panNumber;
    private String uanNumber;

    // Bank Details (CRITICAL for Bank Transfer Excel)
    private String bankName;
    private String bankBranch;
    private String bankAccountNumber;

    private String bankIfscCode;

    // Pay Period Info
    private Long payRunId;
    private String payMonth;
    private Integer payYear;
    private LocalDate fromDate;
    private LocalDate toDate;

    // Attendance Info
    private Double workingDays;
    private Double paymentDays;
    private Double lopDays;

    // Salary Info

    private Double basePay; // The Monthly Baseline Base
    private Double grossPay;
    private Double targetGross; // From Assignment (The 100% attendance gross)
    private Double actualGross; // The pro-rated gross earned this month
    private Double totalDeductions;
    private Double monthlyGrossTarget;
    private Double monthlyNetTarget;
    private Double annualCtc;
    private String targetBreakdownJson;
    private Double netPay;
    private String netPayInWords; // Helpful for UI/PDF
    private Double statutoryContributionOrg;

    private String pdfUrl;
    private String pdfPath;

    private SlipStatus status;

    private List<SalarySlipComponentDTO> components; // nested breakdown

    private Boolean isCredited;
    private LocalDateTime creditedAt;

}
