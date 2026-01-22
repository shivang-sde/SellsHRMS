package com.sellspark.SellsHRMS.dto.payroll;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollComputationRequest {

    private Long organisationId;
    private Long employeeId;
    private Long salaryStructureId;
    private Long taxSlabId;

    private Double basePay;
    private Double variablePay;

    private LocalDate fromDate;
    private LocalDate toDate;

    private Boolean previewOnly;
}
