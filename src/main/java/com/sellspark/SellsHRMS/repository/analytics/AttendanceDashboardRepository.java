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
import java.time.LocalDate;
import java.time.LocalDateTime;
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
                AND a.attendanceDate = :startDate
            """)
    BigDecimal calculateAverageAttendance(
            @Param("orgId") Long orgId,
            @Param("startDate") LocalDate startDate);

    /**
     * Count total days missed (ABSENT, ON_LEAVE, HALF_DAY, SHORT_DAY)
     */
    @Query("""
                SELECT COUNT(a.id)
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
                AND a.attendanceDate >= :startDate
                GROUP BY YEAR(a.attendanceDate), MONTH(a.attendanceDate)
                ORDER BY YEAR(a.attendanceDate), MONTH(a.attendanceDate)
            """)
    List<AttendanceTrendDTO> getAttendanceTrend(
            @Param("orgId") Long orgId,
            @Param("startDate") LocalDate startDate);

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
                AND a.attendanceDate >= :startDate
                GROUP BY COALESCE(lt.name, 'Other')
                ORDER BY COUNT(a.id) DESC
            """)
    List<AbsenceReasonDTO> getAbsenceReasonsMerged(
            @Param("orgId") Long orgId,
            @Param("startDate") LocalDate startDate);

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
                    AND a.attendanceDate >= :startDate
                WHERE d.organisation.id = :orgId
                GROUP BY d.name
                ORDER BY COUNT(a.id) DESC
            """)
    List<DeptMissedDTO> getAllDepartmentsDaysMissed(
            @Param("orgId") Long orgId,
            @Param("startDate") LocalDate startDate);

    /**
     * Average weekly hours by department (including zero-activity departments)
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
                    AND p.punchIn >= :startDate
                WHERE d.organisation.id = :orgId
                GROUP BY d.id, d.name
                ORDER BY d.name
            """)
    List<WeeklyHoursDTO> getAllDepartmentsAverageWeeklyHours(
            @Param("orgId") Long orgId,
            @Param("startDate") LocalDateTime startDate);

    /**
     * Get late arrivals trend (month-wise)
     */
    @Query(value = """
                SELECT YEAR(attendance_date) AS yr, MONTH(attendance_date) AS mon, COUNT(*) AS late_count
                FROM tbl_attendance_summary
                WHERE organisation_id = :orgId
                  AND is_late = 1
                  AND attendance_date >= :startDate
                GROUP BY YEAR(attendance_date), MONTH(attendance_date)
                ORDER BY YEAR(attendance_date), MONTH(attendance_date)
            """, nativeQuery = true)
    List<Object[]> getLateArrivalsTrend(
            @Param("orgId") Long orgId,
            @Param("startDate") LocalDate startDate);

    /**
     * Day-wise late arrivals count (last 15 or 30 days)
     */
    @Query(value = """
                SELECT attendance_date, COUNT(*) AS late_count
                FROM tbl_attendance_summary
                WHERE organisation_id = :orgId
                  AND is_late = 1
                  AND attendance_date >= :startDate
                GROUP BY attendance_date
                ORDER BY attendance_date
            """, nativeQuery = true)
    List<Object[]> getLateArrivalsDayWiseTrend(
            @Param("orgId") Long orgId,
            @Param("startDate") LocalDate startDate);

}
