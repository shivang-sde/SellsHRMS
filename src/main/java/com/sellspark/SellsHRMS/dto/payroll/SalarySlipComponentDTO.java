package com.sellspark.SellsHRMS.dto.payroll;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalarySlipComponentDTO {

    private Long id;

    private Long componentId;          // link to SalaryComponent
    private String componentName;      // e.g. "Basic Pay", "PF"
    private String componentAbbreviation; // e.g. BASIC, PF
    private String componentType;      // EARNING, DEDUCTION

    private Boolean isStatutory;       // true = PF/ESI/TDS
    private Long statutoryComponentId; // optional reference for PF/ESI/PT

    private Double amount;             // computed value
    private String calculationLog;     // e.g. "(BASIC + DA) * 0.12"
}
