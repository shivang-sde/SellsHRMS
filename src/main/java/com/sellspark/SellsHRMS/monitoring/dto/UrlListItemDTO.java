package com.sellspark.SellsHRMS.monitoring.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UrlListItemDTO {
    private String id;
    private String name;
    private String url;
    private String currentStatus;
    private Double uptimePercentage;
    private Integer lastResponseTime;
    private LocalDateTime lastCheckedAt;
    private Boolean isActive;
    private Integer checkInterval;
}