package com.sellspark.SellsHRMS.dto.payroll;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatutoryComponentDTO {

    private Long id;

    private String code;        // e.g., "EPF", "ESI", "SSS"
    private String name;
    private String description;

    private Long organisationId;
    private String countryCode;
    private String stateCode;

    private Boolean isActive;

    private List<Long> ruleIds; // child rules under this component
}
