package com.sellspark.SellsHRMS.controller.api.analytics;

import com.sellspark.SellsHRMS.dto.dashboard.analytics.attendance.*;
import com.sellspark.SellsHRMS.service.analytics.AttendanceDashboardService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for Attendance & Absenteeism Dashboard
 * Provides analytics endpoints for dashboard visualization with date filtering.
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
     * ✅ GET /api/dashboard/summary?orgId={orgId}&startDate={startDate}&endDate={endDate}
     * Defaults to last 30 days if range not provided.
     */
    @GetMapping("/summary")
    public ResponseEntity<AttendanceDashboardSummaryDTO> getSummary(
            @RequestParam("orgId") @Min(1) Long orgId,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("Fetching dashboard summary for org: {} from {} to {}", orgId, startDate, endDate);
        
        if (startDate == null) startDate = LocalDate.now().minusDays(30);
        if (endDate == null) endDate = LocalDate.now();

        try {
            return ResponseEntity.ok(dashboardService.getSummary(orgId, startDate, endDate));
        } catch (Exception e) {
            log.error("Error fetching summary", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * ✅ GET /api/dashboard/attendance-trend?orgId={orgId}&startDate={startDate}&endDate={endDate}
     * Defaults to last 12 months.
     */
    @GetMapping("/attendance-trend")
    public ResponseEntity<List<AttendanceTrendDTO>> getAttendanceTrend(
            @RequestParam("orgId") @Min(1) Long orgId,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (startDate == null) startDate = LocalDate.now().minusMonths(12).withDayOfMonth(1);
        if (endDate == null) endDate = LocalDate.now();

        try {
            return ResponseEntity.ok(dashboardService.getAttendanceTrend(orgId, startDate, endDate));
        } catch (Exception e) {
            log.error("Error fetching attendance trend", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * ✅ GET /api/dashboard/absence-reasons?orgId={orgId}&startDate={startDate}&endDate={endDate}
     */
    @GetMapping("/absence-reasons")
    public ResponseEntity<List<AbsenceReasonDTO>> getAbsenceReasons(
            @RequestParam("orgId") @Min(1) Long orgId,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (startDate == null) startDate = LocalDate.now().minusMonths(6);
        if (endDate == null) endDate = LocalDate.now();

        try {
            return ResponseEntity.ok(dashboardService.getAbsenceReasons(orgId, startDate, endDate));
        } catch (Exception e) {
            log.error("Error fetching absence reasons", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * ✅ GET /api/dashboard/days-missed-department?orgId={orgId}&startDate={startDate}&endDate={endDate}
     */
    @GetMapping("/days-missed-department")
    public ResponseEntity<List<DeptMissedDTO>> getDaysMissedByDepartment(
            @RequestParam("orgId") @Min(1) Long orgId,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (startDate == null) startDate = LocalDate.now().minusMonths(1);
        if (endDate == null) endDate = LocalDate.now();

        try {
            return ResponseEntity.ok(dashboardService.getDaysMissedByDept(orgId, startDate, endDate));
        } catch (Exception e) {
            log.error("Error fetching days missed by department", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * ✅ GET /api/dashboard/weekly-hours?orgId={orgId}&startDate={startDate}&endDate={endDate}
     */
    @GetMapping("/weekly-hours")
    public ResponseEntity<List<WeeklyHoursDTO>> getWeeklyHours(
            @RequestParam("orgId") @Min(1) Long orgId,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (startDate == null) startDate = LocalDate.now().minusDays(7);
        if (endDate == null) endDate = LocalDate.now();

        try {
            return ResponseEntity.ok(dashboardService.getWeeklyHours(orgId, startDate, endDate));
        } catch (Exception e) {
            log.error("Error fetching weekly hours", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * ✅ GET /api/dashboard/late-arrivals-daily?orgId={orgId}&startDate={startDate}&endDate={endDate}
     */
    @GetMapping("/late-arrivals-daily")
    public ResponseEntity<List<AttendanceTrendDTO>> getLateArrivalsDaily(
            @RequestParam("orgId") @Min(1) Long orgId,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (startDate == null) startDate = LocalDate.now().minusDays(30);
        if (endDate == null) endDate = LocalDate.now();

        try {
            return ResponseEntity.ok(dashboardService.getLateArrivalsDayWiseTrend(orgId, startDate, endDate));
        } catch (Exception e) {
            log.error("Error fetching day-wise late arrivals", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * ✅ GET /api/dashboard/late-arrivals-trend?orgId={orgId}&startDate={startDate}&endDate={endDate}
     */
    @GetMapping("/late-arrivals-trend")
    public ResponseEntity<List<AttendanceTrendDTO>> getLateArrivalsTrend(
            @RequestParam("orgId") @Min(1) Long orgId,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (startDate == null) startDate = LocalDate.now().minusMonths(12).withDayOfMonth(1);
        if (endDate == null) endDate = LocalDate.now();

        try {
            return ResponseEntity.ok(dashboardService.getLateArrivalsTrend(orgId, startDate, endDate));
        } catch (Exception e) {
            log.error("Error fetching late arrivals trend", e);
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
