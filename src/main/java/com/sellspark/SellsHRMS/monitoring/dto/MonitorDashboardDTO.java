package com.sellspark.SellsHRMS.monitoring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonitorDashboardDTO {
    private DashboardStatsDTO stats;
    private List<ActiveIncidentDTO> activeIncidents;
    private List<UrlListItemDTO> downUrls;
    private List<UrlListItemDTO> recentUrls;
    private ResponseTimeChartDTO responseTimeData;
}
