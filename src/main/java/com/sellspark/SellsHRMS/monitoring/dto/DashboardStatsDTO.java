package com.sellspark.SellsHRMS.monitoring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsDTO {
    private Long totalUrls;
    private Long upCount;
    private Long downCount;
    private Long pendingCount;
    private Double avgUptime;
    private Long totalGroups;
    private Long activeIncidents;
    private Integer avgResponseTime;
}
