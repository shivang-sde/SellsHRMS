package com.sellspark.SellsHRMS.dto.payroll;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StructureUpdatePreviewDTO {
    private int employeeCount;
    private Double currentTotalMonthlyGross;
    private Double newTotalMonthlyGross;
    private Double difference;
    // Map of Employee Name -> Change in CTC
    private Map<String, Double> impactBreakdown;
}
