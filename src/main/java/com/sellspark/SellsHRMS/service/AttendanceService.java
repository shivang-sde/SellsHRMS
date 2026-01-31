package com.sellspark.SellsHRMS.service;

import com.sellspark.SellsHRMS.dto.attendance.*;
import com.sellspark.SellsHRMS.entity.AttendanceSummary;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    PunchRecordResponse punchIn(PunchInRequest request);
    PunchRecordResponse punchOut(PunchOutRequest request);
    PunchRecordResponse getTodayPunch(Long employeeId);
    List<PunchRecordResponse> getEmployeeAttendance(Long employeeId, LocalDate startDate, LocalDate endDate);
    List<PunchRecordResponse> getOrgAttendanceByDate(Long orgId, LocalDate date);
    List<PunchRecordResponse> getTodayOrgAttendance(Long orgId);
    AttendanceSummaryResponse getAttendanceSummary(Long employeeId, LocalDate startDate, LocalDate endDate);
    List<AttendanceSummary> getOrgAttendanceSummary(Long orgId, LocalDate date);
    List<PunchRecordResponse> getOrgAttendanceByRange(Long orgId, LocalDate startDate, LocalDate endDate);



}