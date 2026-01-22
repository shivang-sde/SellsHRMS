package com.sellspark.SellsHRMS.dto.payroll;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatutoryComponentMappingDTO {
    private Long id;
    private Long statutoryComponentId;
    private String statutoryComponentName;
    private Long salaryComponentId;
    private String salaryComponentName;
    private Long organisationId;
    private String countryCode;
    private String stateCode;
    private Double employeePercent;
    private Double employerPercent;
    private String customRuleConfig;
    private Boolean active;
    private Boolean includeInCalculation;
}
