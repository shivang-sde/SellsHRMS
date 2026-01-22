package com.sellspark.SellsHRMS.service.impl.analytics;

import com.sellspark.SellsHRMS.dto.dashboard.analytics.attendance.*;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.exception.OrganisationNotFoundException;
import com.sellspark.SellsHRMS.repository.EmployeeRepository;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;
import com.sellspark.SellsHRMS.repository.analytics.AttendanceDashboardRepository;
import com.sellspark.SellsHRMS.service.analytics.AttendanceDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

/**
 * Service layer for Attendance and Absenteeism Dashboard Analytics
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AttendanceDashboardServiceImpl implements AttendanceDashboardService {

    private final AttendanceDashboardRepository dashboardRepository;
    private final EmployeeRepository empRepo;

    /**
     * Get overall summary for dashboard metrics (attendance %, days missed, etc.)
     */
    @Override
    public AttendanceDashboardSummaryDTO getSummary(Long orgId) {

        Long totalActiveEmp = empRepo.countByOrganisationIdAndDeletedFalse(orgId);

        LocalDate today = LocalDate.now();
        LocalDate yersterday = LocalDate.now().minusDays(1);
        LocalDate startOfCurrentMonth = today.withDayOfMonth(1);
        LocalDate endOfCurrentMonth = today.with(TemporalAdjusters.lastDayOfMonth());

        LocalDate startOfPreviousMonth = startOfCurrentMonth.minusMonths(1);
        LocalDate endOfPreviousMonth = startOfPreviousMonth.with(TemporalAdjusters.lastDayOfMonth());

        try {

            BigDecimal currentDayAvgAttendance = dashboardRepository.calculateAverageAttendance(
                    orgId, today);

            // last day attebdays
            BigDecimal previousDayAvgAttendance = dashboardRepository.calculateAverageAttendance(
                    orgId, yersterday);

            Long currentMonthDaysMissed = dashboardRepository.countDaysMissed(
                    orgId, today, startOfCurrentMonth);

            Long previousMonthDaysMissed = dashboardRepository.countDaysMissed(
                    orgId, endOfPreviousMonth, startOfPreviousMonth);

            return AttendanceDashboardSummaryDTO.builder()
                    .averageAttendance(currentDayAvgAttendance != null ? currentDayAvgAttendance : BigDecimal.ZERO)
                    .previousAttendance(previousDayAvgAttendance != null ? previousDayAvgAttendance : BigDecimal.ZERO)
                    .totalDaysMissed(currentMonthDaysMissed != null ? currentMonthDaysMissed : 0L)
                    .previousDaysMissed(previousMonthDaysMissed != null ? previousMonthDaysMissed : 0L)
                    .activeEmployees(totalActiveEmp)
                    .build();

        } catch (Exception e) {
            log.error("Error calculating dashboard summary for orgId={}", orgId, e);
            return AttendanceDashboardSummaryDTO.builder()
                    .averageAttendance(BigDecimal.ZERO)
                    .previousAttendance(BigDecimal.ZERO)
                    .totalDaysMissed(0L)
                    .previousDaysMissed(0L)
                    .build();
        }
    }

    /**
     * Get monthly attendance trend for last 12 months
     */
    @Override
    public List<AttendanceTrendDTO> getAttendanceTrend(Long orgId) {
        validateOrgId(orgId);
        LocalDate startDate = LocalDate.now().minusMonths(12).withDayOfMonth(1);

        try {
            List<AttendanceTrendDTO> trend = dashboardRepository.getAttendanceTrend(orgId, startDate);
            return trend != null ? trend : List.of();
        } catch (Exception e) {
            log.error("Error fetching attendance trend for orgId={}", orgId, e);
            return List.of();
        }
    }

    /**
     * Get absence reasons (leave types + 'Other' for plain absences)
     */
    @Override
    public List<AbsenceReasonDTO> getAbsenceReasons(Long orgId) {
        validateOrgId(orgId);
        LocalDate startDate = LocalDate.now().minusMonths(6);

        try {
            List<AbsenceReasonDTO> reasons = dashboardRepository.getAbsenceReasonsMerged(orgId, startDate);
            if (reasons == null || reasons.isEmpty()) {
                return List.of();
            }

            long total = reasons.stream()
                    .mapToLong(AbsenceReasonDTO::getCount)
                    .sum();

            reasons.forEach(r -> r.calculatePercentage(total));
            return reasons;

        } catch (Exception e) {
            log.error("Error fetching absence reasons for orgId={}", orgId, e);
            return List.of();
        }
    }

    /**
     * Get department-wise days missed (includes departments with 0)
     */
    @Override
    public List<DeptMissedDTO> getDaysMissedByDept(Long orgId) {
        validateOrgId(orgId);
        LocalDate startDate = LocalDate.now().minusMonths(1);

        try {
            List<DeptMissedDTO> deptData = dashboardRepository.getAllDepartmentsDaysMissed(orgId, startDate);
            return deptData != null ? deptData : List.of();
        } catch (Exception e) {
            log.error("Error fetching days missed by department for orgId={}", orgId, e);
            return List.of();
        }
    }

    /**
     * Get average weekly hours per department (includes departments with 0)
     */
    @Override
    public List<WeeklyHoursDTO> getWeeklyHours(Long orgId) {
        validateOrgId(orgId);
        LocalDateTime startDate = LocalDate.now().minusMonths(3).atStartOfDay();

        try {
            List<WeeklyHoursDTO> data = dashboardRepository.getAllDepartmentsAverageWeeklyHours(orgId, startDate);
            return data != null ? data : List.of();
        } catch (Exception e) {
            log.error("Error fetching weekly hours for orgId={}", orgId, e);
            return List.of();
        }
    }

    /**
     * Get month-wise late arrivals count for the last 12 months
     */
    @Override
    public List<AttendanceTrendDTO> getLateArrivalsTrend(Long orgId) {
        validateOrgId(orgId);
        LocalDate startDate = LocalDate.now().minusMonths(12).withDayOfMonth(1);

        try {
            List<Object[]> raw = dashboardRepository.getLateArrivalsTrend(orgId, startDate);
            List<AttendanceTrendDTO> result = new ArrayList<>();

            for (Object[] row : raw) {
                // Safely convert year and month regardless of SQL numeric type
                int year = ((Number) row[0]).intValue();
                int month = ((Number) row[1]).intValue();
                double count = ((Number) row[2]).doubleValue();

                result.add(new AttendanceTrendDTO(year, month, count));
            }

            return result;

        } catch (Exception e) {
            log.error("Error fetching late arrivals trend for orgId={}", orgId, e);
            return List.of();
        }
    }

    /**
     * Get day-wise late arrivals trend (e.g., last 15/30 days)
     */
    @Override
    public List<AttendanceTrendDTO> getLateArrivalsDayWiseTrend(Long orgId, int days) {
        validateOrgId(orgId);
        LocalDate startDate = LocalDate.now().minusDays(days);

        try {
            List<Object[]> raw = dashboardRepository.getLateArrivalsDayWiseTrend(orgId, startDate);
            List<AttendanceTrendDTO> result = new ArrayList<>();

            for (Object[] row : raw) {
                java.sql.Date sqlDate = (java.sql.Date) row[0];
                LocalDate date = sqlDate.toLocalDate(); // ✅ Proper conversion
                Double count = ((Number) row[1]).doubleValue();

                // Using day label format, e.g. "Jan 05"
                String label = date.getMonth().toString().substring(0, 3) + " "
                        + String.format("%02d", date.getDayOfMonth());

                result.add(new AttendanceTrendDTO(label, count));
            }

            return result;

        } catch (Exception e) {
            log.error("Error fetching day-wise late arrivals for orgId={}", orgId, e);
            return List.of();
        }
    }

    private void validateOrgId(Long orgId) {
        if (orgId == null || orgId <= 0)
            throw new IllegalArgumentException("Invalid organisation ID: " + orgId);
    }
}
