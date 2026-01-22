package com.sellspark.SellsHRMS.dto.payroll;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PayRunRequestDTO {
    private Long organisationId; 
    private String payPeriod; 
    private String startDate; 
    private String endDate; 
    private String description;
}
