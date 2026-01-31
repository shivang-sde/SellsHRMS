package com.sellspark.SellsHRMS.controller.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sellspark.SellsHRMS.dto.attendance.AttendanceSummaryResponse;
import com.sellspark.SellsHRMS.dto.attendance.PunchInRequest;
import com.sellspark.SellsHRMS.dto.attendance.PunchOutRequest;
import com.sellspark.SellsHRMS.dto.attendance.PunchRecordResponse;
import com.sellspark.SellsHRMS.entity.AttendanceSummary;
import com.sellspark.SellsHRMS.service.AttendanceService;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("api/attendance")
@RequiredArgsConstructor
public class AttendanceRestController {
    
    private final AttendanceService attendanceService;


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
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate ) {
    return ResponseEntity.ok(attendanceService.getEmployeeAttendance(employeeId, startDate, endDate));
    
    }

    // Get attendance summary for an employee
    @GetMapping("/summary/{employeeId}")
    public ResponseEntity<AttendanceSummaryResponse> getAttedanceSummary(@PathVariable Long employeeId, 
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate ) {

        return ResponseEntity.ok(attendanceService.getAttendanceSummary(employeeId, startDate, endDate));
    }


     // Admin: Get all attendance for today
     @GetMapping("/today/org/{orgId}")
     public ResponseEntity<List<PunchRecordResponse>> getTodayOrgAttendance(@PathVariable Long orgId) {
        return ResponseEntity.ok(attendanceService.getTodayOrgAttendance(orgId));
    }

    @GetMapping("/org/{orgId}")
    public ResponseEntity<List<PunchRecordResponse>> getOrgAttendanceByDate(    @PathVariable Long orgId, 
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate ) {
        
        return ResponseEntity.ok(attendanceService.getOrgAttendanceByRange(orgId, startDate, endDate));
    }


}