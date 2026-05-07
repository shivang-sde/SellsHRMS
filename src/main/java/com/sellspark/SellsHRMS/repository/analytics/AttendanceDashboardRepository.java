package com.sellspark.SellsHRMS.repository.analytics;

import com.sellspark.SellsHRMS.dto.dashboard.analytics.attendance.AbsenceReasonDTO;
import com.sellspark.SellsHRMS.dto.dashboard.analytics.attendance.AttendanceTrendDTO;
import com.sellspark.SellsHRMS.dto.dashboard.analytics.attendance.DeptMissedDTO;
import com.sellspark.SellsHRMS.dto.dashboard.analytics.attendance.WeeklyHoursDTO;
import com.sellspark.SellsHRMS.entity.AttendanceSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository for Attendance and Absenteeism Dashboard Analytics
 */
@Repository
public interface AttendanceDashboardRepository extends JpaRepository<AttendanceSummary, Long> {

    /**
     * Calculate average attendance percentage for an organization
     * Counts PRESENT and WFH as attended
     */
    @Query("""
                SELECT COALESCE(
                    (COUNT(CASE WHEN a.status IN ('PRESENT', 'WFH') THEN 1 END) * 100.0 /
                     NULLIF(COUNT(a.id), 0)),
                    0
                )
                FROM AttendanceSummary a
                WHERE a.organisation.id = :orgId
                AND a.attendanceDate BETWEEN :startDate AND :endDate
            """)
    BigDecimal calculateAverageAttendance(
            @Param("orgId") Long orgId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("""
                SELECT COALESCE(COUNT(a.id), 0)
                FROM AttendanceSummary a
                WHERE a.organisation.id = :orgId
                  AND a.status IN ('ABSENT', 'ON_LEAVE', 'HALF_DAY', 'SHORT_DAY')
                  AND a.attendanceDate BETWEEN :startDate AND :endDate
            """)
    Long countDaysMissed(
            @Param("orgId") Long orgId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Get monthly attendance trend for an organization
     */
    @Query("""
                SELECT new com.sellspark.SellsHRMS.dto.dashboard.analytics.attendance.AttendanceTrendDTO(
                    YEAR(a.attendanceDate),
                    MONTH(a.attendanceDate),
                    COALESCE(
                        (COUNT(CASE WHEN a.status IN ('PRESENT', 'WFH') THEN 1 END) * 100.0 /
                         NULLIF(COUNT(a.id), 0)),
                        0
                    )
                )
                FROM AttendanceSummary a
                WHERE a.organisation.id = :orgId
                AND a.attendanceDate BETWEEN :startDate AND :endDate
                GROUP BY YEAR(a.attendanceDate), MONTH(a.attendanceDate)
                ORDER BY YEAR(a.attendanceDate), MONTH(a.attendanceDate)
            """)
    List<AttendanceTrendDTO> getAttendanceTrend(
            @Param("orgId") Long orgId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Get absence reasons: merge leave types + plain absences (Others)
     */
    @Query("""
                SELECT new com.sellspark.SellsHRMS.dto.dashboard.analytics.attendance.AbsenceReasonDTO(
                    COALESCE(lt.name, 'Other'),
                    COUNT(a.id)
                )
                FROM AttendanceSummary a
                LEFT JOIN a.leave l
                LEFT JOIN l.leaveType lt
                WHERE a.organisation.id = :orgId
                AND a.status IN ('ON_LEAVE', 'ABSENT')
                AND a.attendanceDate BETWEEN :startDate AND :endDate
                GROUP BY COALESCE(lt.name, 'Other')
                ORDER BY COUNT(a.id) DESC
            """)
    List<AbsenceReasonDTO> getAbsenceReasonsMerged(
            @Param("orgId") Long orgId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Days missed by department, including departments with zero absences
     */
    @Query("""
                SELECT new com.sellspark.SellsHRMS.dto.dashboard.analytics.attendance.DeptMissedDTO(
                    d.name,
                    COALESCE(COUNT(a.id), 0)
                )
                FROM Department d
                LEFT JOIN Employee e ON e.department.id = d.id
                LEFT JOIN AttendanceSummary a ON a.employee.id = e.id
                    AND a.status IN ('ABSENT', 'ON_LEAVE', 'HALF_DAY', 'SHORT_DAY')
                    AND a.attendanceDate BETWEEN :startDate AND :endDate
                WHERE d.organisation.id = :orgId
                GROUP BY d.name
                ORDER BY COUNT(a.id) DESC
            """)
    List<DeptMissedDTO> getAllDepartmentsDaysMissed(
            @Param("orgId") Long orgId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Average weekly hours by department (including zero-activity departments)
     * Using Instant for punchIn comparison
     */
    @Query("""
                SELECT new com.sellspark.SellsHRMS.dto.dashboard.analytics.attendance.WeeklyHoursDTO(
                    d.id,
                    d.name,
                    COALESCE(AVG(p.workHours), 0)
                )
                FROM Department d
                LEFT JOIN Employee e ON e.department.id = d.id
                LEFT JOIN PunchInOut p ON p.employee.id = e.id
                    AND p.punchIn >= :startDate AND p.punchIn <= :endDate
                WHERE d.organisation.id = :orgId
                GROUP BY d.id, d.name
                ORDER BY d.name
            """)
    List<WeeklyHoursDTO> getAllDepartmentsAverageWeeklyHours(
            @Param("orgId") Long orgId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate);

    /**
     * Get late arrivals trend (month-wise) - Replaced native with JPQL
     */
    @Query("""
                SELECT new com.sellspark.SellsHRMS.dto.dashboard.analytics.attendance.AttendanceTrendDTO(
                    YEAR(a.attendanceDate),
                    MONTH(a.attendanceDate),
                    CAST(COUNT(a.id) AS double)
                )
                FROM AttendanceSummary a
                WHERE a.organisation.id = :orgId
                  AND a.isLate = true
                  AND a.attendanceDate BETWEEN :startDate AND :endDate
                GROUP BY YEAR(a.attendanceDate), MONTH(a.attendanceDate)
                ORDER BY YEAR(a.attendanceDate), MONTH(a.attendanceDate)
            """)
    List<AttendanceTrendDTO> getLateArrivalsTrend(
            @Param("orgId") Long orgId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Day-wise late arrivals count (JPQL version)
     */
    @Query("""
                SELECT a.attendanceDate, COUNT(a.id)
                FROM AttendanceSummary a
                WHERE a.organisation.id = :orgId
                  AND a.isLate = true
                  AND a.attendanceDate BETWEEN :startDate AND :endDate
                GROUP BY a.attendanceDate
                ORDER BY a.attendanceDate
            """)
    List<Object[]> getLateArrivalsDayWiseTrend(
            @Param("orgId") Long orgId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("""
                SELECT COUNT(a.id)
                FROM AttendanceSummary a
                WHERE a.organisation.id = :orgId
                  AND a.isLate = TRUE
                  AND a.attendanceDate = :date
            """)
    Long countTodayLateArrivals(@Param("orgId") Long orgId, @Param("date") LocalDate date);

}
