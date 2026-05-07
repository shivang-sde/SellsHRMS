package com.sellspark.SellsHRMS.service.analytics;

import com.sellspark.SellsHRMS.dto.dashboard.analytics.attendance.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for Attendance and Absenteeism Dashboard Analytics
 */
public interface AttendanceDashboardService {

    /**
     * Get overall summary for dashboard metrics (attendance %, days missed, etc.)
     */
    AttendanceDashboardSummaryDTO getSummary(Long orgId, LocalDate startDate, LocalDate endDate);

    /**
     * Get attendance trend for the specified range
     */
    List<AttendanceTrendDTO> getAttendanceTrend(Long orgId, LocalDate startDate, LocalDate endDate);

    /**
     * Get absence reasons distribution for the specified range
     */
    List<AbsenceReasonDTO> getAbsenceReasons(Long orgId, LocalDate startDate, LocalDate endDate);

    /**
     * Get department-wise days missed for the specified range
     */
    List<DeptMissedDTO> getDaysMissedByDept(Long orgId, LocalDate startDate, LocalDate endDate);

    /**
     * Get average weekly hours per department for the specified range
     */
    List<WeeklyHoursDTO> getWeeklyHours(Long orgId, LocalDate startDate, LocalDate endDate);

    /**
     * Get month-wise late arrivals count for the specified range
     */
    List<AttendanceTrendDTO> getLateArrivalsTrend(Long orgId, LocalDate startDate, LocalDate endDate);

    /**
     * Get day-wise late arrivals trend for the specified range
     */
    List<AttendanceTrendDTO> getLateArrivalsDayWiseTrend(Long orgId, LocalDate startDate, LocalDate endDate);
}
