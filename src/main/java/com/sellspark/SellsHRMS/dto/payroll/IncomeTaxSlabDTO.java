package com.sellspark.SellsHRMS.dto.payroll;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncomeTaxSlabDTO {

    private Long id;

    private String name;
    private String countryCode;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    private Boolean allowTaxExemption;
    private Double standardExemptionLimit;

    private Long organisationId;

    private List<IncomeTaxRuleDTO> rules; // nested
}
