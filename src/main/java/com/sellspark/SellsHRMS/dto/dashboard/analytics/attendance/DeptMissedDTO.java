package com.sellspark.SellsHRMS.dto.dashboard.analytics.attendance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for department-wise days missed
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeptMissedDTO {

    private String departmentName;
    private Long daysMissed;
}
