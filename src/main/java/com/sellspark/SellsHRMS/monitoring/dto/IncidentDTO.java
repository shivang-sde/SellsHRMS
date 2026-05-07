package com.sellspark.SellsHRMS.monitoring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IncidentDTO {
    private String id;
    private String urlId;
    private String urlName;
    private String url;
    private String startedAt;
    private String endedAt;
    private Integer durationSeconds;
    private String cause;
    private Boolean resolved;
    private Boolean notificationSent;
}