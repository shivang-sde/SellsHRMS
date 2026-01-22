package com.sellspark.SellsHRMS.dto.payroll;

import lombok.*;
import java.time.LocalDate;

import com.sellspark.SellsHRMS.entity.payroll.StatutoryRule;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatutoryRuleDTO {

    private Long id;

    private Long statutoryComponentId;

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    private Double employerContributionPercent;
    private Double employeeContributionPercent;
    private Double minApplicableSalary;
    private Double maxApplicableSalary;

    private Boolean applyProRata;
    private Boolean includeEmployeeContriPercentInCTC;
    private Boolean includeEmployerContriPercentInCTC;

    private StatutoryRule.DeductionCycle deductionCycle;
    private String additionalConfig; // JSON (e.g., custom per-country logic)

    private Boolean active;

    // public enum DeductionCycle {
    //     MONTHLY,
    //     QUARTERLY,
    //     HALF_YEARLY,
    //     YEARLY,
    //     CUSTOM
    // }
}
