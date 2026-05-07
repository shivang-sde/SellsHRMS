package com.sellspark.SellsHRMS.monitoring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UrlDTO {
    private String id;
    private String name;
    private String url;
    private String currentStatus;
    private Double uptimePercentage;
    private Integer lastResponseTime;
    private String lastCheckedAt;
    private Boolean isActive;
}