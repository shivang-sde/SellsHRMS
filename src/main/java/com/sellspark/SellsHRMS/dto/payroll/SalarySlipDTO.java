package com.sellspark.SellsHRMS.dto.payroll;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalarySlipDTO {

    private Long id;

    private Long employeeId;
    private String employeeName;
    private Long assignmentId;
    private Long payRunId;

    private LocalDate fromDate;
    private LocalDate toDate;

    private Integer workingDays;
    private Integer paymentDays;
    private Integer lopDays;

    private Double basePay;
    private Double grossPay;
    private Double totalDeductions;
    private Double netPay;

    private String pdfUrl;
    private String pdfPath;

    private List<SalarySlipComponentDTO> components; // nested breakdown
}
