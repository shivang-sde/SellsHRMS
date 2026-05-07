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
public class MonitorUrlDTO {
    private String id;
    private String name;
    private String url;
    private String method;
    private Integer checkInterval;
    private Integer timeout;
    private Integer failureThreshold;
    private String currentStatus;
    private Double uptimePercentage;
    private Integer lastResponseTime;
    private Integer lastStatusCode;
    private String lastError;
    private LocalDateTime lastCheckedAt;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private String createdByName;
}