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
public class ActiveIncidentDTO {
    private String id;
    private String urlId;
    private String urlName;
    private String url;
    private LocalDateTime startedAt;
    private String cause;
}