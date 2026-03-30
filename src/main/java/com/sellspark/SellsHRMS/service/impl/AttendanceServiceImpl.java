package com.sellspark.SellsHRMS.service.impl;

import com.sellspark.SellsHRMS.dto.attendance.*;
import com.sellspark.SellsHRMS.entity.*;
import com.sellspark.SellsHRMS.exception.AttendanceAlreadyMarkedException;
import com.sellspark.SellsHRMS.exception.InvalidOperationException;
import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
import com.sellspark.SellsHRMS.exception.employee.EmployeeInactiveException;
import com.sellspark.SellsHRMS.exception.employee.EmployeeNotFoundException;
import com.sellspark.SellsHRMS.repository.*;
import com.sellspark.SellsHRMS.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.sellspark.SellsHRMS.config.UserPrincipal;
import com.sellspark.SellsHRMS.utils.EmployeeHierarchyUtil;
import java.util.Set;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AttendanceServiceImpl implements AttendanceService {

    private final PunchInOutRepository punchRepo;
    private final AttendanceSummaryRepository summaryRepo;
    private final EmployeeRepository employeeRepo;
    private final LeaveRepository leaveRepo;
    private final HolidayRepository holidayRepo;
    private final OrganisationPolicyRepository policyRepo;
    private final DeviceRepository deviceRepo;
    private final EmployeeHierarchyUtil employeeHierarchyUtil;

    @Override
    public PunchRecordResponse punchIn(PunchInRequest request) {
        log.info("Processing punch in for employee: {}", request.getEmployeeId());

        Employee employee = employeeRepo.findById(request.getEmployeeId())
                .orElseThrow(() -> new EmployeeNotFoundException(request.getEmployeeId()));

        OrganisationPolicy policy = policyRepo.findByOrganisation(employee.getOrganisation())
                .orElseThrow(() -> new ResourceNotFoundException("Organisation policy", "policy",
                        employee.getOrganisation().getName()));

        // Check if employee is active
        if (employee.getStatus() != Employee.EmployeeStatus.ACTIVE) {
            throw new EmployeeInactiveException(employee.getEmployeeCode());
        }

        // check if on leave
        Optional<Leave> leave = leaveRepo.findApprovedLeaveByEmployeeAndDate(employee.getId(), LocalDate.now());
        if (leave.isPresent()) {
            throw new AttendanceAlreadyMarkedException("Can't punch in! You are on leave today");
        }
        ZoneId zoneId = ZoneId.of(employee.getOrganisation().getTimeZone());
        LocalDate attendanceDate = LocalDate.ofInstant(request.getPunchIn(), zoneId);

        // Check if already punched in today
        AttendanceSummary existingSummary = summaryRepo
                .findByEmployeeIdAndAttendanceDate(employee.getId(), attendanceDate)
                .orElseThrow(() -> new ResourceNotFoundException("AttendanceSummary", "employeeId", employee.getId()));

        if (existingSummary.getEffectivePunchIn() != null) {
            throw new AttendanceAlreadyMarkedException("Already punched in today");
        }
        log.info("No existing punch in found for today. Proceeding with punch  {}", request);

        // 1. Create punch record
        PunchInOut punch = PunchInOut.builder()
                .organisation(employee.getOrganisation())
                .employee(employee)
                .punchIn(request.getPunchIn())
                .punchSource(parsePunchSource(request.getSource()))
                .punchedFrom(parsePunchedFrom(request.getPunchedFrom()))
                .attendanceDate(attendanceDate)
                .lat(request.getLat())
                .lng(request.getLng())
                .build();
        try {
            punch = punchRepo.save(punch);
        } catch (DataIntegrityViolationException e) {
            throw new AttendanceAlreadyMarkedException("Already punched in today");
        }
        // 2. Update attendance summary
        // AttendanceSummary summary = summaryRepo
        // .findByEmployeeIdAndAttendanceDate(employee.getId(), attendanceDate)
        // .orElseThrow(() -> new ResourceNotFoundException(
        // "Attendance summary not found. Please contact administrator."));

        existingSummary.setStatus(AttendanceSummary.AttendanceStatus.PRESENT);
        existingSummary.setPunchRecord(punch);
        existingSummary.setEffectivePunchIn(request.getPunchIn());
        existingSummary.setSource(AttendanceSummary.AttendanceSource.PUNCH_SYSTEM);
        existingSummary.setRemarks("Punched in via " + request.getPunchedFrom());

        // Check if late (example: 9:30 AM grace period)
        // TODO: Get shift timing from employee's shift configuration
        LocalDateTime punchIn = LocalDateTime.ofInstant(request.getPunchIn(), zoneId);
        LocalTime allowedTime = policy.getOfficeStart().plusMinutes(policy.getLateGraceMinutes());

        if (punchIn.toLocalTime().isAfter(allowedTime)) {
            existingSummary.setIsLate(true);
        }

        summaryRepo.save(existingSummary);
        log.info("Punch in successful for employee: {}", employee.getEmployeeCode());
        return mapToResponse(punch, existingSummary, zoneId);
    }

    @Override
    public PunchRecordResponse punchOut(PunchOutRequest request) {
        log.info("Processing punch out for punch ID: {}", request.getPunchId());

        PunchInOut punch = punchRepo.findById(request.getPunchId())
                .orElseThrow(() -> new ResourceNotFoundException("Punch record", "id", request.getPunchId()));

        ZoneId zoneId = ZoneId.of(punch.getOrganisation().getTimeZone());

        OrganisationPolicy policy = policyRepo.findByOrganisation(punch.getOrganisation())
                .orElseThrow(() -> new ResourceNotFoundException("Organisation policy", "policy",
                        punch.getOrganisation().getName()));

        // check if on leave
        Optional<Leave> leave = leaveRepo.findApprovedLeaveByEmployeeAndDate(punch.getEmployee().getId(),
                LocalDate.now());
        if (leave.isPresent()) {
            throw new AttendanceAlreadyMarkedException("Can't punch out! You are on leave today");
        }

        if (punch.getPunchOut() != null) {
            throw new AttendanceAlreadyMarkedException("punched out");
        }

        // Validate punch out time is after punch in
        if (request.getPunchOut().isBefore(punch.getPunchIn())) {
            throw new InvalidOperationException("Punch out time cannot be before punch in time");
        }

        punch.setPunchOut(request.getPunchOut());

        // Calculate work hours
        Duration duration = Duration.between(punch.getPunchIn(), punch.getPunchOut());
        double hours = duration.toMinutes() / 60.0;
        punch.setWorkHours(hours);
        punchRepo.save(punch);

        // Update attendance summary
        AttendanceSummary summary = summaryRepo
                .findByEmployeeIdAndAttendanceDate(
                        punch.getEmployee().getId(),
                        LocalDate.now(zoneId))
                .orElseThrow(() -> new ResourceNotFoundException("Attendance summary not found"));

        summary.setEffectivePunchOut(request.getPunchOut());
        summary.setWorkHours(hours);

        double fullDayHours = policy.getStandardDailyHours();
        double halfDayHours = fullDayHours / 2;

        // Determine status based on hours worked
        if (hours >= fullDayHours) {
            summary.setStatus(AttendanceSummary.AttendanceStatus.PRESENT);
        } else if (hours >= halfDayHours) {
            summary.setStatus(AttendanceSummary.AttendanceStatus.HALF_DAY);
            summary.setRemarks("Half day - worked " + String.format("%.2f", hours) + " hours");
        } else {
            summary.setStatus(AttendanceSummary.AttendanceStatus.SHORT_DAY);
            summary.setRemarks("Short day - worked " + String.format("%.2f", hours) + " hours");
        }

        // Check if early out (example: before 5:30 PM)
        // TODO: Get shift timing from employee's shift configuration
        LocalDateTime punchOut = LocalDateTime.ofInstant(request.getPunchOut(), zoneId);
        LocalTime allowedExit = policy.getOfficeClosed().minusMinutes(policy.getEarlyOutGraceMinutes());

        if (punchOut.toLocalTime().isBefore(allowedExit)) {
            summary.setIsEarlyOut(true);
        }

        summaryRepo.save(summary);

        return mapToResponse(punch, summary, zoneId);
    }

    @Override
    public PunchRecordResponse updateAttendance(PunchRecordResponse request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        Long currentEmployeeId = principal.getEmployeeId();
        String role = principal.getSystemRole();
        boolean isOrgAdmin = "ORG_ADMIN".equals(role) || "SUPER_ADMIN".equals(role);

        AttendanceSummary summary = summaryRepo.findById(request.getSummaryId())
                .orElseThrow(() -> new ResourceNotFoundException("Attendance summary not found"));

        if (summary.getEmployee() == null) {
            throw new InvalidOperationException("Attendance summary has no associated employee");
        }

        Long targetEmployeeId = summary.getEmployee().getId();

        if (!isOrgAdmin) {
            if (targetEmployeeId.equals(currentEmployeeId)) {
                throw new InvalidOperationException("You cannot edit your own attendance record");
            }

            Set<Long> subordinateIds = employeeHierarchyUtil.getAllSubordinateIds(currentEmployeeId);
            if (!subordinateIds.contains(targetEmployeeId)) {
                throw new InvalidOperationException("You are not authorized to edit this attendance record");
            }
        }

        ZoneId zoneId = ZoneId.of(summary.getOrganisation().getTimeZone());
        Instant punchIn = request.getPunchIn() != null ? request.getPunchIn().atZone(zoneId).toInstant() : null;
        Instant punchOut = request.getPunchOut() != null ? request.getPunchOut().atZone(zoneId).toInstant() : null;

        if (punchIn != null && punchOut != null && punchIn.isAfter(punchOut)) {
            throw new InvalidOperationException("Punch In time cannot be after Punch Out time");
        }

        OrganisationPolicy policy = policyRepo.findByOrganisation(summary.getOrganisation())
                .orElseThrow(() -> new ResourceNotFoundException("Organisation policy", "policy",
                        summary.getOrganisation().getName()));

        Double workHours = 0.0;
        if (punchIn != null && punchOut != null) {
            workHours = Duration.between(punchIn, punchOut).toMinutes() / 60.0;
        }

        boolean isLate = false;
        if (punchIn != null) {
            LocalDateTime punchInDateTime = LocalDateTime.ofInstant(punchIn, zoneId);
            LocalTime allowedTime = policy.getOfficeStart().plusMinutes(policy.getLateGraceMinutes());
            if (punchInDateTime.toLocalTime().isAfter(allowedTime)) {
                isLate = true;
            }
        }

        boolean isEarlyOut = false;
        if (punchOut != null) {
            LocalDateTime punchOutDateTime = LocalDateTime.ofInstant(punchOut, zoneId);
            LocalTime allowedExit = policy.getOfficeClosed().minusMinutes(policy.getEarlyOutGraceMinutes());
            if (punchOutDateTime.toLocalTime().isBefore(allowedExit)) {
                isEarlyOut = true;
            }
        }

        PunchInOut punch = summary.getPunchRecord();

        if (punch != null) {
            punch.setPunchIn(punchIn);
            punch.setPunchOut(punchOut);
            punch.setWorkHours(workHours);

            if (request.getPunchSource() != null && !request.getPunchSource().isBlank()) {
                punch.setPunchSource(PunchInOut.Source.valueOf(request.getPunchSource()));
            }
            if (request.getPunchedFrom() != null && !request.getPunchedFrom().isBlank()) {
                punch.setPunchedFrom(PunchInOut.PUNCHFROM.valueOf(request.getPunchedFrom()));
            }
            if (punchIn != null) {
                punch.setAttendanceDate(LocalDate.ofInstant(punchIn, zoneId));
            }
            punchRepo.save(punch);
        }

        summary.setEffectivePunchIn(punchIn);
        summary.setEffectivePunchOut(punchOut);
        summary.setWorkHours(workHours);

        // Status is still updated from request if UI permits, but derived flags are
        // backend authoritative
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            summary.setStatus(AttendanceSummary.AttendanceStatus.valueOf(request.getStatus()));
        }

        summary.setIsLate(isLate);
        summary.setIsEarlyOut(isEarlyOut);

        if (request.getRemarks() != null) {
            summary.setRemarks(request.getRemarks());
        }
        summaryRepo.save(summary);

        return mapToResponse(punch, summary, zoneId);
    }

    @Override
    public List<PunchRecordResponse> getOrgAttendanceByRange(
            Long orgId,
            LocalDate startDate,
            LocalDate endDate) {

        List<AttendanceSummary> summaries = summaryRepo
                .findWithEmployeeAndDepartmentByEmployeeIdAndDateRange(orgId, startDate, endDate);

        summaries = filterSummariesByPermission(summaries);

        ZoneId zoneId = summaries.stream()
                .findFirst()
                .map(s -> ZoneId.of(s.getOrganisation().getTimeZone()))
                .orElse(ZoneId.systemDefault());

        List<PunchRecordResponse> response = summaries.stream()
                .map(summary -> mapToResponse(summary.getPunchRecord(), summary, zoneId))
                .collect(Collectors.toList());

        return response;
    }

    @Override
    public PunchRecordResponse getTodayPunch(Long employeeId) {
        Employee emp = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        ZoneId zoneId = ZoneId.of(emp.getOrganisation().getTimeZone());
        LocalDate today = LocalDate.now(zoneId);

        AttendanceSummary summary = summaryRepo
                .findByEmployeeIdAndAttendanceDate(employeeId, today)
                .orElseThrow(() -> new ResourceNotFoundException("AttendanceSummary", "employeeId", employeeId));

        if (summary.getPunchRecord() != null) {
            return mapToResponse(summary.getPunchRecord(), summary, zoneId);
        }

        // Return empty response if no punch yet
        PunchRecordResponse response = new PunchRecordResponse();
        response.setEmployeeId(employeeId);
        response.setStatus(summary.getStatus().name());
        return response;
    }

    @Override
    public List<PunchRecordResponse> getEmployeeAttendance(
            Long employeeId,
            LocalDate startDate,
            LocalDate endDate) {

        List<AttendanceSummary> summaries = summaryRepo
                .findByEmployeeIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(employeeId, startDate, endDate);

        ZoneId zoneId = summaries.isEmpty()
                ? ZoneId.systemDefault()
                : ZoneId.of(summaries.get(0).getEmployee().getOrganisation().getTimeZone());

        return summaries.stream()
                .map(summary -> mapToResponse(summary.getPunchRecord(), summary, zoneId))
                .collect(Collectors.toList());
    }

    @Override
    public List<PunchRecordResponse> getOrgAttendanceByDate(Long orgId, LocalDate date) {
        List<AttendanceSummary> summaries = summaryRepo
                .findByOrganisationIdAndAttendanceDateOrderByAttendanceDateDesc(orgId, date);

        if (summaries.isEmpty()) {
            return Collections.emptyList();
        }

        summaries = filterSummariesByPermission(summaries);

        if (summaries.isEmpty()) {
            return Collections.emptyList();
        }

        ZoneId zoneId = ZoneId.of(summaries.get(0).getOrganisation().getTimeZone());

        return summaries.stream()
                .map(summary -> {
                    PunchRecordResponse response = new PunchRecordResponse();
                    response.setSummaryId(summary.getId());
                    Employee emp = summary.getEmployee();
                    if (emp != null) {
                        response.setEmployeeId(emp.getId());
                        response.setEmployeeName(emp.getFirstName() + " " + emp.getLastName());
                        response.setEmployeeCode(emp.getEmployeeCode());
                        response.setDepartment(emp.getDepartment().getName());
                    }
                    response.setStatus(summary.getStatus() != null ? summary.getStatus().name() : "UNKNOWN");
                    response.setWorkHours(summary.getWorkHours() != null ? summary.getWorkHours() : 0.0);
                    response.setPunchIn(safeConvert(summary.getEffectivePunchIn(), zoneId));
                    response.setPunchOut(safeConvert(summary.getEffectivePunchOut(), zoneId));
                    response.setIsLate(summary.getIsLate());
                    response.setIsEarlyOut(summary.getIsEarlyOut());
                    response.setRemarks(summary.getRemarks());

                    PunchInOut punch = summary.getPunchRecord();
                    if (punch != null) {
                        response.setPunchId(punch.getId());
                        response.setPunchSource(punch.getPunchSource().name());
                    } else {
                        response.setPunchSource("SYSTEM");
                    }

                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceSummary> getOrgAttendanceSummary(Long orgId, LocalDate date) {
        List<AttendanceSummary> summaries = summaryRepo
                .findByOrganisationIdAndAttendanceDateOrderByAttendanceDateDesc(orgId, date);
        return filterSummariesByPermission(summaries);
    }

    @Override
    public List<PunchRecordResponse> getTodayOrgAttendance(Long orgId) {
        return getOrgAttendanceByDate(orgId, LocalDate.now());
    }

    @Override
    public AttendanceSummaryResponse getAttendanceSummary(
            Long employeeId,
            LocalDate startDate,
            LocalDate endDate) {

        List<AttendanceSummary> summaries = summaryRepo
                .findByEmployeeIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(employeeId, startDate, endDate);

        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        AttendanceSummaryResponse response = new AttendanceSummaryResponse();
        response.setEmployeeId(employeeId);
        response.setEmployeeName(employee.getFirstName() + " " + employee.getLastName());

        int totalPresent = 0;
        int totalAbsent = 0;
        double totalHours = 0;
        int lateCount = 0;
        int earlyOutCount = 0;

        for (AttendanceSummary summary : summaries) {
            switch (summary.getStatus()) {
                case PRESENT:
                case HALF_DAY:
                    totalPresent++;
                    break;
                case ABSENT:
                    totalAbsent++;
                    break;
                default:
                    break;
            }

            if (summary.getWorkHours() != null) {
                totalHours += summary.getWorkHours();
            }

            if (Boolean.TRUE.equals(summary.getIsLate())) {
                lateCount++;
            }

            if (Boolean.TRUE.equals(summary.getIsEarlyOut())) {
                earlyOutCount++;
            }
        }

        response.setTotalDaysPresent(totalPresent);
        response.setTotalDaysAbsent(totalAbsent);
        response.setTotalWorkHours(totalHours);
        response.setLateCheckIns(lateCount);
        response.setEarlyCheckOuts(earlyOutCount);

        return response;
    }

    // Helper method to map entity to response
    private PunchRecordResponse mapToResponse(PunchInOut punch, AttendanceSummary summary, ZoneId zoneId) {
        PunchRecordResponse response = new PunchRecordResponse();

        // Punch details
        if (punch != null) {
            response.setPunchId(punch.getId());
            response.setPunchSource(
                    punch.getPunchSource() != null && !"null".equals(punch.getPunchSource().name())
                            ? punch.getPunchSource().name()
                            : "SYSTEM");
        } else {
            response.setPunchId(0L);
            response.setPunchSource("SYSTEM");
        }

        response.setSummaryId(summary.getId());

        // Employee details
        Employee emp = summary.getEmployee();
        if (emp != null) {
            response.setEmployeeId(emp.getId());

            String firstName = emp.getFirstName() != null ? emp.getFirstName() : "";
            String lastName = emp.getLastName() != null ? emp.getLastName() : "";
            response.setEmployeeName((firstName + " " + lastName).trim());

            response.setEmployeeCode(emp.getEmployeeCode() != null ? emp.getEmployeeCode() : "N/A");
            response.setDepartment(emp.getDepartment() != null ? emp.getDepartment().getName() : "Unassigned");
        } else {
            response.setEmployeeId(0L);
            response.setEmployeeName("Unknown Employee");
            response.setEmployeeCode("N/A");
            response.setDepartment("Unassigned");
        }

        // Attendance data
        response.setAttendanceDate(
                summary.getAttendanceDate() != null ? summary.getAttendanceDate().toString() : "N/A");
        response.setPunchIn(
                summary.getEffectivePunchIn() != null ? safeConvert(summary.getEffectivePunchIn(), zoneId) : null);
        response.setPunchOut(
                summary.getEffectivePunchOut() != null ? safeConvert(summary.getEffectivePunchOut(), zoneId) : null);
        response.setWorkHours(summary.getWorkHours() != null ? summary.getWorkHours() : 0.0);

        response.setIsLate(summary.getIsLate() != null ? summary.getIsLate() : false);
        response.setIsEarlyOut(summary.getIsEarlyOut() != null ? summary.getIsEarlyOut() : false);

        response.setStatus(summary.getStatus() != null ? summary.getStatus().name() : "UNKNOWN");
        response.setRemarks(summary.getRemarks() != null ? summary.getRemarks() : "No remarks");

        return response;
    }

    private List<AttendanceSummary> filterSummariesByPermission(List<AttendanceSummary> summaries) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return Collections.emptyList();
        }
        Object princ = auth.getPrincipal();
        if (!(princ instanceof UserPrincipal)) {
            return Collections.emptyList();
        }

        UserPrincipal principal = (UserPrincipal) princ;

        // ORG_ADMIN and SUPER_ADMIN have all access implicitly
        if ("ORG_ADMIN".equals(principal.getSystemRole()) || "SUPER_ADMIN".equals(principal.getSystemRole())) {
            return summaries;
        }

        if (principal.hasAnyPermission("EMPLOYEE_VIEW_ALL")) {
            return summaries;
        } else if (principal.hasAnyPermission("EMPLOYEE_VIEW_TEAM")) {
            Long empId = principal.getEmployeeId();
            if (empId == null)
                return Collections.emptyList();

            Set<Long> subordinateIds = employeeHierarchyUtil.getAllSubordinateIds(empId);
            if (subordinateIds == null || subordinateIds.isEmpty()) {
                return Collections.emptyList();
            }

            return summaries.stream()
                    .filter(s -> s.getEmployee() != null && subordinateIds.contains(s.getEmployee().getId()))
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList(); // Unauthorized to view org level data
        }
    }

    private LocalDateTime safeConvert(Instant instant, ZoneId zoneId) {
        return instant != null ? LocalDateTime.ofInstant(instant, zoneId) : null;

    }

    private PunchInOut.Source parsePunchSource(String value) {
        if (value == null || value.isBlank()) {
            log.warn("Punch source missing, defaulting to WEB");
            return PunchInOut.Source.WEB;
        }
        try {
            return PunchInOut.Source.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Invalid punch source: {}", value);
            return PunchInOut.Source.WEB;
        }
    }

    private PunchInOut.PUNCHFROM parsePunchedFrom(String value) {
        if (value == null || value.isBlank()) {
            log.warn("PunchedFrom value missing, defaulting to WFO");
            return PunchInOut.PUNCHFROM.WFO; // or return null if you prefer optional
        }
        try {
            return PunchInOut.PUNCHFROM.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Invalid punchedFrom value: {}", value);
            throw new InvalidOperationException("Invalid punchedFrom value: " + value);
        }
    }

    @Override
    public PunchRecordResponse processDevicePunch(String apiKey, DevicePunchRequest request) {
        log.info("Processing device punch for api key: {}", apiKey);

        Device device = deviceRepo.findByApiKey(apiKey)
                .orElseThrow(() -> new InvalidOperationException("Invalid API Key"));

        if (device.getStatus() != Device.Status.ACTIVE) {
            throw new InvalidOperationException("Device is inactive");
        }
        ZoneId zoneId = ZoneId.of(device.getOrganisation().getTimeZone());

        Employee employee;

        if (request.getBiometricId() != null && !request.getBiometricId().isBlank()) {
            employee = employeeRepo.findByOrganisationIdAndBiometricId(
                    device.getOrganisation().getId(),
                    request.getBiometricId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Employee not found with Biometric ID: " + request.getBiometricId()));
        } else if (request.getEmployeeCode() != null && !request.getEmployeeCode().isBlank()) {
            employee = employeeRepo.findByOrganisationIdAndEmployeeCode(
                    device.getOrganisation().getId(),
                    request.getEmployeeCode())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Employee not found with Code: " + request.getEmployeeCode()));
        } else {
            throw new InvalidOperationException("Either Employee Code or Biometric ID must be provided");
        }

        // Ensure strict tenant isolation (redundant with the query above but good for
        // safety)
        if (!employee.getOrganisation().getId().equals(device.getOrganisation().getId())) {
            throw new InvalidOperationException("Employee does not belong to the device's organisation");
        }

        if ("IN".equalsIgnoreCase(request.getAction())) {
            PunchInRequest punchInRequest = new PunchInRequest();
            punchInRequest.setEmployeeId(employee.getId());
            punchInRequest.setPunchIn(request.getTimestamp());
            punchInRequest.setSource(PunchInOut.Source.BIOMETRIC.name());
            punchInRequest.setLat(request.getLat());
            punchInRequest.setLng(request.getLng());
            punchInRequest.setPunchedFrom(PunchInOut.PUNCHFROM.WFO.name()); // Assume WFO for devices
            return punchIn(punchInRequest);
        } else if ("OUT".equalsIgnoreCase(request.getAction())) {
            // Find the active punch record for today
            AttendanceSummary summary = summaryRepo
                    .findByEmployeeIdAndAttendanceDate(employee.getId(), LocalDate.now(zoneId))
                    .orElseThrow(() -> new ResourceNotFoundException("No attendance record found for today"));

            if (summary.getPunchRecord() == null) {
                throw new ResourceNotFoundException("No punch-in record found to punch out");
            }

            PunchOutRequest punchOutRequest = new PunchOutRequest();
            punchOutRequest.setPunchId(summary.getPunchRecord().getId());
            punchOutRequest.setPunchOut(request.getTimestamp());
            return punchOut(punchOutRequest);
        } else {
            throw new InvalidOperationException("Invalid action: " + request.getAction());
        }
    }

}