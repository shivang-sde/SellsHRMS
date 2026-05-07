package com.sellspark.SellsHRMS.monitoring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupSummaryDTO {
    private String groupId;
    private String groupName;
    private Long totalUrls;
    private Double avgUptime;
    private Long activeIncidents;
    private Long totalMembers;
    private String healthStatus; // "healthy", "warning", "critical"
}