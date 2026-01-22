package com.sellspark.SellsHRMS.dto.payroll;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for Salary Slip Template
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalarySlipTemplateDTO {
    private Long id;
    private Long orgId;

    private String templateName;
    private String templateHtml;
    private String configJson;
    private Boolean isDefault;
    private Boolean isActive;
    private String logoUrl;
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
