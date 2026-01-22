package com.sellspark.SellsHRMS.dto.dashboard.analytics.attendance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Summary DTO for dashboard top metrics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDashboardSummaryDTO {

    private BigDecimal averageAttendance;
    private BigDecimal previousAttendance;
    private Long totalDaysMissed;
    private Long previousDaysMissed;
    private Long activeEmployees;
    private Long todayLateArrivals;

    public BigDecimal getAttendanceChange() {
        if (previousAttendance == null || averageAttendance == null) {
            return BigDecimal.ZERO;
        }
        return averageAttendance.subtract(previousAttendance);
    }

    public Long getDaysMissedChange() {
        if (previousDaysMissed == null || totalDaysMissed == null) {
            return 0L;
        }
        return totalDaysMissed - previousDaysMissed;
    }
}
