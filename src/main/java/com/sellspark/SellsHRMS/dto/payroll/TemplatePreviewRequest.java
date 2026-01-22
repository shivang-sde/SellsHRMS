package com.sellspark.SellsHRMS.dto.payroll;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

/**
 * Request DTO for template preview
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public
class TemplatePreviewRequest {
    private String templateHtml;
    private String configJson;
    private Map<String, Object> customData; // Optional: allow custom mock data
}
