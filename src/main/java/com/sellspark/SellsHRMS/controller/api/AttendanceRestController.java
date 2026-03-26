package com.sellspark.SellsHRMS.controller.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sellspark.SellsHRMS.dto.attendance.AttendanceSummaryResponse;
import com.sellspark.SellsHRMS.dto.attendance.PunchInRequest;
import com.sellspark.SellsHRMS.dto.attendance.PunchOutRequest;
import com.sellspark.SellsHRMS.dto.attendance.PunchPreCheckResponse;
import com.sellspark.SellsHRMS.dto.attendance.PunchRecordResponse;
import com.sellspark.SellsHRMS.dto.holiday.HolidayResponse;
import com.sellspark.SellsHRMS.entity.OrganisationPolicy;
import com.sellspark.SellsHRMS.service.AttendanceService;
import com.sellspark.SellsHRMS.service.HolidayService;
import com.sellspark.SellsHRMS.service.LeaveService;
import com.sellspark.SellsHRMS.service.OrganisationPolicyService;

import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("api/attendance")
@RequiredArgsConstructor
public class AttendanceRestController {

    private final AttendanceService attendanceService;
    private final HolidayService holidayService;
    private final LeaveService leaveService;
    private final OrganisationPolicyService policyService;
    private final com.sellspark.SellsHRMS.repository.OrganisationRepository organisationRepository;

    // Employee punches in
    @PostMapping("/punch-in")
    public ResponseEntity<PunchRecordResponse> punchIn(@RequestBody PunchInRequest request) {
        return ResponseEntity.ok(attendanceService.punchIn(request));
    }

    // Employee punches out
    @PostMapping("/punch-out")
    public ResponseEntity<PunchRecordResponse> punchOut(@RequestBody PunchOutRequest request) {
        return ResponseEntity.ok(attendanceService.punchOut(request));
    }

    // Get today's punch record for an employee
    @GetMapping("/today/{employeeId}")
    public ResponseEntity<PunchRecordResponse> getTodayPunch(@PathVariable Long employeeId) {
        return ResponseEntity.ok(attendanceService.getTodayPunch(employeeId));
    }

    // Get punch records by employee and date range
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<PunchRecordResponse>> getEmployeeAttendance(@PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(attendanceService.getEmployeeAttendance(employeeId, startDate, endDate));

    }

    // Get attendance summary for an employee
    @GetMapping("/summary/{employeeId}")
    public ResponseEntity<AttendanceSummaryResponse> getAttedanceSummary(@PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ResponseEntity.ok(attendanceService.getAttendanceSummary(employeeId, startDate, endDate));
    }

    // Admin: Get all attendance for today
    @GetMapping("/today/org/{orgId}")
    public ResponseEntity<List<PunchRecordResponse>> getTodayOrgAttendance(@PathVariable Long orgId) {
        return ResponseEntity.ok(attendanceService.getTodayOrgAttendance(orgId));
    }

    @GetMapping("/org/{orgId}")
    public ResponseEntity<List<PunchRecordResponse>> getOrgAttendanceByDate(@PathVariable Long orgId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ResponseEntity.ok(attendanceService.getOrgAttendanceByRange(orgId, startDate, endDate));
    }

    @PutMapping("/update")
    public ResponseEntity<PunchRecordResponse> updateAttendance(@RequestBody PunchRecordResponse request) {
        return ResponseEntity.ok(attendanceService.updateAttendance(request));
    }

    /**
     * Single pre-check endpoint for the Punch-In card.
     *
     * <p>Runs every guard in priority order:
     * holiday → leave → week-off → shift-window
     * and returns a unified {@link PunchPreCheckResponse} so the front-end
     * only needs one AJAX call.
     *
     * @param employeeId the employee requesting to punch in
     * @param orgId      the organisation the employee belongs to
     */
    @GetMapping("/pre-check")
    public ResponseEntity<PunchPreCheckResponse> punchPreCheck(
            @RequestParam Long employeeId,
            @RequestParam Long orgId) {

        java.time.ZoneId zoneId = organisationRepository.findById(orgId)
                .map(org -> java.time.ZoneId.of(org.getTimeZone()))
                .orElse(java.time.ZoneId.systemDefault());

        LocalDate today = LocalDate.now(zoneId);
        LocalTime now   = LocalTime.now(zoneId);
        DateTimeFormatter hhmm = DateTimeFormatter.ofPattern("HH:mm");

        // ── 1. Holiday check ──────────────────────────────────────────────────
        boolean isHoliday = holidayService.isHoliday(orgId, today);
        String holidayName = null;
        String holidayType = null;
        if (isHoliday) {
            HolidayResponse hr = holidayService.getHolidayByDate(orgId, today);
            if (hr != null) {
                holidayName = hr.getHolidayName();
                holidayType = hr.getHolidayType();
            }
        }

        // ── 2. Leave check ────────────────────────────────────────────────────
        boolean isOnLeave = leaveService.checkLeave(employeeId, today);

        // ── 3. Week-off check ─────────────────────────────────────────────────
        OrganisationPolicy policy = policyService.getPolicyForOrg(orgId)
                .orElse(null);

        boolean isWeekOff = false;
        String weekOffDay = null;
        LocalTime officeStart  = LocalTime.of(10, 0);
        LocalTime officeClosed = LocalTime.of(19, 0);
        int lateGraceMinutes   = 10;

        if (policy != null) {
            DayOfWeek dayOfWeek = today.getDayOfWeek();
            if (policy.getWeekOffDays() != null && policy.getWeekOffDays().contains(dayOfWeek)) {
                isWeekOff  = true;
                weekOffDay = dayOfWeek.name();
            }
            if (policy.getOfficeStart()  != null) officeStart  = policy.getOfficeStart();
            if (policy.getOfficeClosed() != null) officeClosed = policy.getOfficeClosed();
            if (policy.getLateGraceMinutes() != null) lateGraceMinutes = policy.getLateGraceMinutes();
        }

        // ── 4. Shift-window check ─────────────────────────────────────────────
        //  Allow punch-in from (officeStart - 30 min) up to officeClosed
        LocalTime punchWindowOpen = officeStart.minusMinutes(30);
        String shiftStatus;
        boolean withinShiftWindow;
        if (now.isBefore(punchWindowOpen)) {
            shiftStatus       = "BEFORE_SHIFT";
            withinShiftWindow = false;
        } else if (now.isAfter(officeClosed)) {
            shiftStatus       = "AFTER_SHIFT";
            withinShiftWindow = false;
        } else {
            shiftStatus       = "WITHIN_SHIFT";
            withinShiftWindow = true;
        }

        // ── 5. Compute final verdict ──────────────────────────────────────────
        boolean canPunchIn = !isHoliday && !isOnLeave && !isWeekOff && withinShiftWindow;

        PunchPreCheckResponse response = PunchPreCheckResponse.builder()
                .holiday(isHoliday)
                .holidayName(holidayName)
                .holidayType(holidayType)
                .onLeave(isOnLeave)
                .weekOff(isWeekOff)
                .weekOffDay(weekOffDay)
                .officeStart(officeStart.format(hhmm))
                .officeClosed(officeClosed.format(hhmm))
                .lateGraceMinutes(lateGraceMinutes)
                .withinShiftWindow(withinShiftWindow)
                .shiftStatus(shiftStatus)
                .canPunchIn(canPunchIn)
                .build();

        return ResponseEntity.ok(response);
    }

}