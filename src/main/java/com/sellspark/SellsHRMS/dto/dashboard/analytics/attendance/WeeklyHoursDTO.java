package com.sellspark.SellsHRMS.dto.dashboard.analytics.attendance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for average weekly hours by department
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyHoursDTO {
    private Long departmentId;
    private String departmentName;
    private Double averageHours;
}
