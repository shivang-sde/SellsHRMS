package com.sellspark.SellsHRMS.dto.dashboard.analytics.attendance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for monthly attendance trend data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceTrendDTO {

    private String monthLabel;
    private Double attendanceRate;

    /**
     * Constructor used in JPQL projection
     */
    public AttendanceTrendDTO(Integer year, Integer month, Double attendanceRate) {
        this.monthLabel = formatMonthLabel(year, month);
        this.attendanceRate = attendanceRate;
    }

    private String formatMonthLabel(Integer year, Integer month) {
        String[] months = {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };
        if (month == null || month < 1 || month > 12) return "";
        return months[month - 1] + " " + year;
    }
}
