package com.sellspark.SellsHRMS.service.analytics;

import java.util.List;

import com.sellspark.SellsHRMS.dto.dashboard.analytics.attendance.AbsenceReasonDTO;
import com.sellspark.SellsHRMS.dto.dashboard.analytics.attendance.AttendanceDashboardSummaryDTO;
import com.sellspark.SellsHRMS.dto.dashboard.analytics.attendance.AttendanceTrendDTO;
import com.sellspark.SellsHRMS.dto.dashboard.analytics.attendance.DeptMissedDTO;
import com.sellspark.SellsHRMS.dto.dashboard.analytics.attendance.WeeklyHoursDTO;

public interface AttendanceDashboardService {

  /**
   * * Get overall dashboard summary with current and previous period metrics.
   * 
   * @param orgId organisation ID
   * @return summary DTO
   */
  AttendanceDashboardSummaryDTO getSummary(Long orgId);

  /**
   * * Get monthly attendance trend for the last 12 months.
   * * @param orgId organisation ID *
   * 
   * @return list of attendance trend DTOs
   **/
  List<AttendanceTrendDTO> getAttendanceTrend(Long orgId);

  /**
   * * Get absence reasons distribution with percentages.
   * * @param orgId organisation ID
   * * @return list of absence reason DTOs *
   */

  List<AbsenceReasonDTO> getAbsenceReasons(Long orgId);

  List<AttendanceTrendDTO> getLateArrivalsDayWiseTrend(Long orgId, int days);

  List<AttendanceTrendDTO> getLateArrivalsTrend(Long orgId);

  /**
   * * Get days missed grouped by department.
   * * @param orgId organisation ID
   * * @return list of department missed DTOs
   */

  List<DeptMissedDTO> getDaysMissedByDept(Long orgId);

  /**
   * * Get average weekly hours by department. *
   * 
   * @param orgId organisation ID *
   * @return list of weekly hours DTOs
   */

  List<WeeklyHoursDTO> getWeeklyHours(Long orgId);
}
