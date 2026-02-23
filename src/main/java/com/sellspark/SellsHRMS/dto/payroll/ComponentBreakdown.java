package com.sellspark.SellsHRMS.dto.payroll;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComponentBreakdown {
    private String name;
    private String code;
    private String type;
    private String formula;
    private Double evaluatedValue;
    private Map<String, Object> contextUsed;
    private String remarks;
}
