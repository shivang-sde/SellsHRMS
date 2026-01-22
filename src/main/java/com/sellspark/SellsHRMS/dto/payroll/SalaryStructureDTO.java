package com.sellspark.SellsHRMS.dto.payroll;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalaryStructureDTO {

    private Long id;
    private String name;
    private String description;

    private String type;
    private Boolean active;

    private Double leaveEncashmentRate;
    private String currency;

    private String payrollFrequency; // MONTHLY, WEEKLY, BIWEEKLY
    
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    private Long organisationId;
    private String countryCode;
    private List<Long> componentIds;


    private List<SalaryComponentDTO> components; // nested list
}
