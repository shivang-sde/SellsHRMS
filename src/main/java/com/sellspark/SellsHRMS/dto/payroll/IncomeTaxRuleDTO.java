package com.sellspark.SellsHRMS.dto.payroll;

import com.sellspark.SellsHRMS.entity.payroll.IncomeTaxSlab;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncomeTaxRuleDTO {

    private Long id;

    private Long taxSlabId;
    private Double minIncome;
    private Double maxIncome;
    private Double deductionPercent;

    
    private IncomeTaxSlab incomeTaxSlab;
    @Builder.Default
    private boolean isActive = false;

    private String condition; // Optional JSON/EL logic
}
