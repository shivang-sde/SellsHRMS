package com.sellspark.SellsHRMS.monitoring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUrlRequest {
    private String name;
    private String url;
    private String method;
    private Integer checkInterval;
    private Integer timeout;
    private Integer failureThreshold;
    private Boolean isActive;
    private String groupId;
}
