package com.sellspark.SellsHRMS.service.impl.analytics;

import com.sellspark.SellsHRMS.dto.dashboard.analytics.attendance.*;
import com.sellspark.SellsHRMS.repository.EmployeeRepository;
import com.sellspark.SellsHRMS.repository.analytics.AttendanceDashboardRepository;
import com.sellspark.SellsHRMS.service.analytics.AttendanceDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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

    @Override
    public AttendanceDashboardSummaryDTO getSummary(Long orgId, LocalDate startDate, LocalDate endDate) {
        validateOrgId(orgId);
        Long totalActiveEmp = empRepo.countByOrganisationIdAndDeletedFalse(orgId);

        // Previous Period Calculation (same duration)
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        LocalDate prevStartDate = startDate.minusDays(daysBetween);
        LocalDate prevEndDate = endDate.minusDays(daysBetween);

        try {
            BigDecimal currentAvgAttendance = dashboardRepository.calculateAverageAttendance(orgId, startDate, endDate);
            BigDecimal prevAvgAttendance = dashboardRepository.calculateAverageAttendance(orgId, prevStartDate, prevEndDate);

            Long currentMissed = dashboardRepository.countDaysMissed(orgId, startDate, endDate);
            Long prevMissed = dashboardRepository.countDaysMissed(orgId, prevStartDate, prevEndDate);

            Long todayLateArrivals = dashboardRepository.countTodayLateArrivals(orgId, LocalDate.now());

            return AttendanceDashboardSummaryDTO.builder()
                    .averageAttendance(currentAvgAttendance != null ? currentAvgAttendance : BigDecimal.ZERO)
                    .previousAttendance(prevAvgAttendance != null ? prevAvgAttendance : BigDecimal.ZERO)
                    .totalDaysMissed(currentMissed != null ? currentMissed : 0L)
                    .previousDaysMissed(prevMissed != null ? prevMissed : 0L)
                    .todayLateArrivals(todayLateArrivals != null ? todayLateArrivals : 0L)
                    .activeEmployees(totalActiveEmp)
                    .build();

        } catch (Exception e) {
            log.error("Error calculating summary for orgId={}", orgId, e);
            return AttendanceDashboardSummaryDTO.builder()
                    .averageAttendance(BigDecimal.ZERO)
                    .previousAttendance(BigDecimal.ZERO)
                    .totalDaysMissed(0L)
                    .previousDaysMissed(0L)
                    .todayLateArrivals(0L)
                    .activeEmployees(totalActiveEmp)
                    .build();
        }
    }

    @Override
    public List<AttendanceTrendDTO> getAttendanceTrend(Long orgId, LocalDate startDate, LocalDate endDate) {
        validateOrgId(orgId);
        try {
            return dashboardRepository.getAttendanceTrend(orgId, startDate, endDate);
        } catch (Exception e) {
            log.error("Error fetching attendance trend", e);
            return List.of();
        }
    }

    @Override
    public List<AbsenceReasonDTO> getAbsenceReasons(Long orgId, LocalDate startDate, LocalDate endDate) {
        validateOrgId(orgId);
        try {
            List<AbsenceReasonDTO> reasons = dashboardRepository.getAbsenceReasonsMerged(orgId, startDate, endDate);
            if (reasons == null || reasons.isEmpty()) return List.of();

            long total = reasons.stream().mapToLong(AbsenceReasonDTO::getCount).sum();
            reasons.forEach(r -> r.calculatePercentage(total));
            return reasons;
        } catch (Exception e) {
            log.error("Error fetching absence reasons", e);
            return List.of();
        }
    }

    @Override
    public List<DeptMissedDTO> getDaysMissedByDept(Long orgId, LocalDate startDate, LocalDate endDate) {
        validateOrgId(orgId);
        try {
            return dashboardRepository.getAllDepartmentsDaysMissed(orgId, startDate, endDate);
        } catch (Exception e) {
            log.error("Error fetching days missed by dept", e);
            return List.of();
        }
    }

    @Override
    public List<WeeklyHoursDTO> getWeeklyHours(Long orgId, LocalDate startDate, LocalDate endDate) {
        validateOrgId(orgId);
        Instant startInstant = startDate.atStartOfDay(ZoneId.of("UTC")).toInstant();
        Instant endInstant = endDate.plusDays(1).atStartOfDay(ZoneId.of("UTC")).toInstant();

        try {
            return dashboardRepository.getAllDepartmentsAverageWeeklyHours(orgId, startInstant, endInstant);
        } catch (Exception e) {
            log.error("Error fetching weekly hours", e);
            return List.of();
        }
    }

    @Override
    public List<AttendanceTrendDTO> getLateArrivalsTrend(Long orgId, LocalDate startDate, LocalDate endDate) {
        validateOrgId(orgId);
        try {
            return dashboardRepository.getLateArrivalsTrend(orgId, startDate, endDate);
        } catch (Exception e) {
            log.error("Error fetching late arrivals trend", e);
            return List.of();
        }
    }

    @Override
    public List<AttendanceTrendDTO> getLateArrivalsDayWiseTrend(Long orgId, LocalDate startDate, LocalDate endDate) {
        validateOrgId(orgId);
        try {
            List<Object[]> raw = dashboardRepository.getLateArrivalsDayWiseTrend(orgId, startDate, endDate);
            List<AttendanceTrendDTO> result = new ArrayList<>();

            for (Object[] row : raw) {
                LocalDate date;
                if (row[0] instanceof java.sql.Date) {
                    date = ((java.sql.Date) row[0]).toLocalDate();
                } else if (row[0] instanceof LocalDate) {
                    date = (LocalDate) row[0];
                } else {
                    continue;
                }
                
                Double count = ((Number) row[1]).doubleValue();
                String label = date.getMonth().toString().substring(0, 3) + " " + String.format("%02d", date.getDayOfMonth());
                result.add(new AttendanceTrendDTO(label, count));
            }
            return result;
        } catch (Exception e) {
            log.error("Error fetching daily late arrivals", e);
            return List.of();
        }
    }

    private void validateOrgId(Long orgId) {
        if (orgId == null || orgId <= 0)
            throw new IllegalArgumentException("Invalid organisation ID: " + orgId);
    }
}
