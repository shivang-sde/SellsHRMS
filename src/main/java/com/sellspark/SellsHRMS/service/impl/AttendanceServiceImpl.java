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
import java.util.stream.Collectors;

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

        LocalDate today = LocalDate.now();

        // Check if already punched in today
        // Check if already punched in today
        AttendanceSummary existingSummary = summaryRepo
                .findByEmployeeIdAndAttendanceDate(employee.getId(), today)
                .orElseThrow(() -> new ResourceNotFoundException("AttendanceSummary", "employeeId", employee.getId()));

        if (existingSummary != null &&
                existingSummary.getStatus() == AttendanceSummary.AttendanceStatus.PRESENT &&
                existingSummary.getEffectivePunchIn() != null) {
            throw new AttendanceAlreadyMarkedException("punched in");
        }
        log.info("No existing punch in found for today. Proceeding with punch  {}", request);

        // 1. Create punch record
        PunchInOut punch = PunchInOut.builder()
                .organisation(employee.getOrganisation())
                .employee(employee)
                .punchIn(request.getPunchIn())
                .punchSource(parsePunchSource(request.getSource()))
                .punchedFrom(parsePunchedFrom(request.getPunchedFrom()))
                .lat(request.getLat())
                .lng(request.getLng())
                .build();
        punch = punchRepo.save(punch);

        // 2. Update attendance summary
        AttendanceSummary summary = summaryRepo
                .findByEmployeeIdAndAttendanceDate(employee.getId(), today)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Attendance summary not found. Please contact administrator."));

        summary.setStatus(AttendanceSummary.AttendanceStatus.PRESENT);
        summary.setPunchRecord(punch);
        summary.setEffectivePunchIn(request.getPunchIn());
        summary.setSource(AttendanceSummary.AttendanceSource.PUNCH_SYSTEM);
        summary.setRemarks("Punched in via " + request.getPunchedFrom());

        // Check if late (example: 9:30 AM grace period)
        // TODO: Get shift timing from employee's shift configuration
        LocalDateTime punchIn = LocalDateTime.ofInstant(request.getPunchIn(), ZoneId.systemDefault());
        LocalTime allowedTime = policy.getOfficeStart().plusMinutes(policy.getLateGraceMinutes());

        if (punchIn.toLocalTime().isAfter(allowedTime)) {
            summary.setIsLate(true);
        }

        summaryRepo.save(summary);
        log.info("Punch in successful for employee: {}", employee.getEmployeeCode());
        return mapToResponse(punch, summary);
    }

    @Override
    public PunchRecordResponse punchOut(PunchOutRequest request) {
        log.info("Processing punch out for punch ID: {}", request.getPunchId());

        PunchInOut punch = punchRepo.findById(request.getPunchId())
                .orElseThrow(() -> new ResourceNotFoundException("Punch record", "id", request.getPunchId()));

        OrganisationPolicy policy = policyRepo.findByOrganisation(punch.getOrganisation())
                .orElseThrow(() -> new ResourceNotFoundException("Organisation policy", "policy",
                        punch.getOrganisation().getName()));

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
                        LocalDate.now())
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
        LocalDateTime punchOut = LocalDateTime.ofInstant(request.getPunchOut(), ZoneId.systemDefault());
        LocalTime allowedExit = policy.getOfficeClosed().minusMinutes(policy.getEarlyOutGraceMinutes());

        if (punchOut.toLocalTime().isBefore(allowedExit)) {
            summary.setIsEarlyOut(true);
        }

        summaryRepo.save(summary);

        return mapToResponse(punch, summary);
    }

    @Override
    public List<PunchRecordResponse> getOrgAttendanceByRange(
            Long orgId,
            LocalDate startDate,
            LocalDate endDate) {

        List<AttendanceSummary> summaries = summaryRepo
                .findByOrganisationIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(orgId, startDate, endDate);

        summaries = filterSummariesByPermission(summaries);

        List<PunchRecordResponse> response = summaries.stream()
                .map(summary -> {
                    PunchRecordResponse r = new PunchRecordResponse();
                    r.setEmployeeId(summary.getEmployee().getId());
                    r.setEmployeeName(summary.getEmployee().getFirstName() + " " + summary.getEmployee().getLastName());
                    r.setEmployeeCode(summary.getEmployee().getEmployeeCode());
                    r.setStatus(summary.getStatus().name());
                    r.setWorkHours(summary.getWorkHours());
                    r.setPunchIn(summary.getEffectivePunchIn() != null
                            ? LocalDateTime.ofInstant(summary.getEffectivePunchIn(), ZoneId.systemDefault())
                            : null);
                    r.setPunchOut(summary.getEffectivePunchOut() != null
                            ? LocalDateTime.ofInstant(summary.getEffectivePunchOut(), ZoneId.systemDefault())
                            : null);
                    if (summary.getPunchRecord() != null) {
                        r.setId(summary.getPunchRecord().getId());
                        r.setPunchSource(summary.getPunchRecord().getPunchSource().name());
                    }
                    r.setIsLate(summary.getIsLate());
                    r.setIsEarlyOut(summary.getIsEarlyOut());
                    r.setRemarks(summary.getRemarks());
                    return r;
                })
                .collect(Collectors.toList());

        return response;
    }

    @Override
    public PunchRecordResponse getTodayPunch(Long employeeId) {
        LocalDate today = LocalDate.now();

        AttendanceSummary summary = summaryRepo
                .findByEmployeeIdAndAttendanceDate(employeeId, today)
                .orElseThrow(() -> new ResourceNotFoundException("AttendanceSummary", "employeeId", employeeId));

        if (summary.getPunchRecord() != null) {
            return mapToResponse(summary.getPunchRecord(), summary);
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

        return summaries.stream()
                .map(summary -> {
                    if (summary.getPunchRecord() != null) {
                        return mapToResponse(summary.getPunchRecord(), summary);
                    } else {
                        // For non-punch records (holidays, leaves, absences)
                        PunchRecordResponse response = new PunchRecordResponse();
                        response.setEmployeeId(employeeId);
                        response.setStatus(summary.getStatus().name());
                        response.setWorkHours(summary.getWorkHours());
                        response.setPunchIn(safeConvert(summary.getEffectivePunchIn()));
                        response.setPunchOut(safeConvert(summary.getEffectivePunchOut()));
                        response.setIsLate(summary.getIsLate());
                        response.setIsEarlyOut(summary.getIsEarlyOut());
                        response.setRemarks(summary.getRemarks());
                        return response;
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<PunchRecordResponse> getOrgAttendanceByDate(Long orgId, LocalDate date) {
        List<AttendanceSummary> summaries = summaryRepo
                .findByOrganisationIdAndAttendanceDateOrderByAttendanceDateDesc(orgId, date);

        summaries = filterSummariesByPermission(summaries);

        return summaries.stream()
                .map(summary -> {
                    PunchRecordResponse response = new PunchRecordResponse();

                    Employee emp = summary.getEmployee();
                    if (emp != null) {
                        response.setEmployeeId(emp.getId());
                        response.setEmployeeName(emp.getFirstName() + " " + emp.getLastName());
                        response.setEmployeeCode(emp.getEmployeeCode());
                        response.setDepartment(emp.getDepartment().getName());
                    }
                    response.setStatus(summary.getStatus() != null ? summary.getStatus().name() : "UNKNOWN");
                    response.setWorkHours(summary.getWorkHours() != null ? summary.getWorkHours() : 0.0);
                    response.setPunchIn(safeConvert(summary.getEffectivePunchIn()));
                    response.setPunchOut(safeConvert(summary.getEffectivePunchOut()));
                    response.setIsLate(summary.getIsLate());
                    response.setIsEarlyOut(summary.getIsEarlyOut());
                    response.setRemarks(summary.getRemarks());

                    PunchInOut punch = summary.getPunchRecord();
                    if (punch != null) {
                        response.setId(punch.getId());
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
    private PunchRecordResponse mapToResponse(PunchInOut punch, AttendanceSummary summary) {
        PunchRecordResponse response = new PunchRecordResponse();
        response.setId(punch.getId());
        response.setEmployeeId(punch.getEmployee().getId());
        response.setEmployeeName(
                punch.getEmployee().getFirstName() + " " +
                        punch.getEmployee().getLastName());
        response.setDepartment(punch.getEmployee().getDepartment().getName());
        response.setEmployeeCode(punch.getEmployee().getEmployeeCode());
        response.setPunchIn(safeConvert(punch.getPunchIn()));
        response.setPunchOut(safeConvert(punch.getPunchOut()));
        response.setPunchSource(punch.getPunchSource().name());
        response.setIsLate(summary.getIsLate());
        response.setIsEarlyOut(summary.getIsEarlyOut());
        response.setStatus(summary.getStatus().name());
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
            subordinateIds.add(empId); // Add self

            return summaries.stream()
                    .filter(s -> s.getEmployee() != null && subordinateIds.contains(s.getEmployee().getId()))
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList(); // Unauthorized to view org level data
        }
    }

    private LocalDateTime safeConvert(Instant instant) {
        return instant != null ? LocalDateTime.ofInstant(instant, ZoneId.systemDefault()) : null;

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
            AttendanceSummary summary = summaryRepo.findByEmployeeIdAndAttendanceDate(employee.getId(), LocalDate.now())
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