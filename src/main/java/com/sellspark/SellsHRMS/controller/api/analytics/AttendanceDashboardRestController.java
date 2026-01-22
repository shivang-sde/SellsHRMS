package com.sellspark.SellsHRMS.controller.api.analytics;

import com.sellspark.SellsHRMS.dto.dashboard.analytics.attendance.*;
import com.sellspark.SellsHRMS.service.analytics.AttendanceDashboardService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Attendance & Absenteeism Dashboard
 * Provides analytics endpoints for dashboard visualization.
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
public class AttendanceDashboardRestController {

    private final AttendanceDashboardService dashboardService;

    /**
     * ✅ GET /api/dashboard/summary?orgId={orgId}
     * Returns key summary metrics (average attendance, days missed, comparison with
     * previous month)
     */
    @GetMapping("/summary")
    public ResponseEntity<AttendanceDashboardSummaryDTO> getSummary(
            @RequestParam("orgId") @Min(1) Long orgId) {
        log.info("Fetching dashboard summary for organisation: {}", orgId);

        try {
            return ResponseEntity.ok(dashboardService.getSummary(orgId));
        } catch (IllegalArgumentException e) {
            log.error("Invalid organisation ID: {}", orgId, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error fetching dashboard summary", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * ✅ GET /api/dashboard/attendance-trend?orgId={orgId}
     * Returns monthly attendance trend for the last 12 months
     */
    @GetMapping("/attendance-trend")
    public ResponseEntity<List<AttendanceTrendDTO>> getAttendanceTrend(
            @RequestParam("orgId") @Min(1) Long orgId) {
        log.info("Fetching attendance trend for organisation: {}", orgId);

        try {
            return ResponseEntity.ok(dashboardService.getAttendanceTrend(orgId));
        } catch (IllegalArgumentException e) {
            log.error("Invalid organisation ID: {}", orgId, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error fetching attendance trend", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * ✅ GET /api/dashboard/absence-reasons?orgId={orgId}
     * Returns distribution of absence reasons (leave types + "Other" absences)
     */
    @GetMapping("/absence-reasons")
    public ResponseEntity<List<AbsenceReasonDTO>> getAbsenceReasons(
            @RequestParam("orgId") @Min(1) Long orgId) {
        log.info("Fetching absence reasons for organisation: {}", orgId);

        try {
            return ResponseEntity.ok(dashboardService.getAbsenceReasons(orgId));
        } catch (IllegalArgumentException e) {
            log.error("Invalid organisation ID: {}", orgId, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error fetching absence reasons", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * ✅ GET /api/dashboard/days-missed-department?orgId={orgId}
     * Returns total days missed by department (includes zero-count departments)
     */
    @GetMapping("/days-missed-department")
    public ResponseEntity<List<DeptMissedDTO>> getDaysMissedByDepartment(
            @RequestParam("orgId") @Min(1) Long orgId) {
        log.info("Fetching days missed by department for organisation: {}", orgId);

        try {
            return ResponseEntity.ok(dashboardService.getDaysMissedByDept(orgId));
        } catch (IllegalArgumentException e) {
            log.error("Invalid organisation ID: {}", orgId, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error fetching days missed by department", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * ✅ GET /api/dashboard/weekly-hours?orgId={orgId}
     * Returns average weekly working hours by department
     */
    @GetMapping("/weekly-hours")
    public ResponseEntity<List<WeeklyHoursDTO>> getWeeklyHours(
            @RequestParam("orgId") @Min(1) Long orgId) {
        log.info("Fetching weekly hours for organisation: {}", orgId);

        try {
            return ResponseEntity.ok(dashboardService.getWeeklyHours(orgId));
        } catch (IllegalArgumentException e) {
            log.error("Invalid organisation ID: {}", orgId, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error fetching weekly hours", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * ✅ GET /api/dashboard/late-arrivals-trend?orgId={orgId}
     * Returns month-wise late arrivals count for the last 12 months
     */
    @GetMapping("/late-arrivals-trend")
    public ResponseEntity<List<AttendanceTrendDTO>> getLateArrivalsTrend(
            @RequestParam("orgId") @Min(1) Long orgId) {
        log.info("Fetching late arrivals trend for organisation: {}", orgId);

        try {
            return ResponseEntity.ok(dashboardService.getLateArrivalsDayWiseTrend(orgId, 15));
        } catch (IllegalArgumentException e) {
            log.error("Invalid organisation ID: {}", orgId, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error fetching late arrivals trend", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * ✅ GET /api/dashboard/late-arrivals-daily?orgId={orgId}&days={days}
     * Returns day-wise late arrivals trend for the last N days (default 30)
     */
    @GetMapping("/late-arrivals-daily")
    public ResponseEntity<List<AttendanceTrendDTO>> getLateArrivalsDaily(
            @RequestParam("orgId") @Min(1) Long orgId,
            @RequestParam(name = "days", defaultValue = "30") @Min(1) int days) {
        log.info("Fetching day-wise late arrivals for organisation: {} (last {} days)", orgId, days);

        try {
            return ResponseEntity.ok(dashboardService.getLateArrivalsDayWiseTrend(orgId, days));
        } catch (IllegalArgumentException e) {
            log.error("Invalid organisation ID: {}", orgId, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error fetching day-wise late arrivals", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * ✅ Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Attendance Dashboard API is running");
    }
}
