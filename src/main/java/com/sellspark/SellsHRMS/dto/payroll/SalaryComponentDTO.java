package com.sellspark.SellsHRMS.dto.payroll;

import lombok.*;
import java.math.BigDecimal;

import jakarta.persistence.Column;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalaryComponentDTO {

    private Long id;

    private String name;
    private String abbreviation;
    private String description;

    private String type;             // EARNING, DEDUCTION
    private String calculationType;  // FIXED, FORMULA, PERCENTAGE


    private Double fixedAmount;
    private String formula;          // e.g. "BASE * 0.10"
    private String componentCondition;

    private Boolean taxable;         
    private Boolean isFlexibleBenefit;
    private Boolean maxFlexibleBenefitAmount;
    private Boolean dependsOnPaymentDays;
    private Boolean includeInCTC;
    private Boolean roundToNearest;
    private Boolean isStatistical; // not shown on payslip
    private Boolean active;

    // statutory(fixed) related config, later think for dynamic statutory component. 
    private Boolean considerForEPF;
    private Boolean considerForESI;
    private Boolean considerForTax;

    private Long organisationId;
}
