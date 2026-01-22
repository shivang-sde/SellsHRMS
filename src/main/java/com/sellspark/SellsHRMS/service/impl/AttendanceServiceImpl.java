package com.sellspark.SellsHRMS.service.impl;

import com.sellspark.SellsHRMS.dto.attendance.*;
import com.sellspark.SellsHRMS.entity.*;
import com.sellspark.SellsHRMS.exception.AttendanceAlreadyMarkedException;
import com.sellspark.SellsHRMS.exception.EmployeeInactiveException;
import com.sellspark.SellsHRMS.exception.EmployeeNotFoundException;
import com.sellspark.SellsHRMS.exception.InvalidOperationException;
import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
import com.sellspark.SellsHRMS.repository.*;
import com.sellspark.SellsHRMS.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AttendanceServiceImpl implements AttendanceService {

    private final PunchInOutRepository punchRepo;
    private final AttendanceSummaryRepository summaryRepo;
    private final EmployeeRepository employeeRepo;

    @Override
    public PunchRecordResponse punchIn(PunchInRequest request) {
        log.info("Processing punch in for employee: {}", request.getEmployeeId());

        Employee employee = employeeRepo.findById(request.getEmployeeId())
                .orElseThrow(() -> new EmployeeNotFoundException(request.getEmployeeId()));

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


        // 1. Create punch record
        PunchInOut punch = PunchInOut.builder()
                .organisation(employee.getOrganisation())
                .employee(employee)
                .punchIn(request.getPunchIn())
                .punchSource(PunchInOut.Source.valueOf(request.getSource()))
                .build();
        punch = punchRepo.save(punch);

        // 2. Update attendance summary
        AttendanceSummary summary = summaryRepo
                .findByEmployeeIdAndAttendanceDate(employee.getId(), today)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Attendance summary not found. Please contact administrator."
                ));

        summary.setStatus(AttendanceSummary.AttendanceStatus.PRESENT);
        summary.setPunchRecord(punch);
        summary.setEffectivePunchIn(request.getPunchIn());
        summary.setSource(AttendanceSummary.AttendanceSource.PUNCH_SYSTEM);
        summary.setRemarks("Punched in via " + request.getSource());

        // Check if late (example: 9:30 AM grace period)
        // TODO: Get shift timing from employee's shift configuration
        if (request.getPunchIn().getHour() > 9 || 
            (request.getPunchIn().getHour() == 9 && request.getPunchIn().getMinute() > 30)) {
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
                    LocalDate.now()
                )
                .orElseThrow(() -> new ResourceNotFoundException("Attendance summary not found"));

        summary.setEffectivePunchOut(request.getPunchOut());
        summary.setWorkHours(hours);

        // Determine status based on hours worked
        if (hours >= 8) {
            summary.setStatus(AttendanceSummary.AttendanceStatus.PRESENT);
        } else if (hours >= 4) {
            summary.setStatus(AttendanceSummary.AttendanceStatus.HALF_DAY);
            summary.setRemarks("Half day - worked " + String.format("%.2f", hours) + " hours");
        } else {
            summary.setStatus(AttendanceSummary.AttendanceStatus.SHORT_DAY);
            summary.setRemarks("Short day - worked " + String.format("%.2f", hours) + " hours");
        }

        // Check if early out (example: before 5:30 PM)
        // TODO: Get shift timing from employee's shift configuration
        if (request.getPunchOut().getHour() < 17 || 
            (request.getPunchOut().getHour() == 17 && request.getPunchOut().getMinute() < 30)) {
            summary.setIsEarlyOut(true);
        }

        summaryRepo.save(summary);

        return mapToResponse(punch, summary);
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
                .findByEmployeeIdAndAttendanceDateBetween(employeeId, startDate, endDate);

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
                        response.setPunchIn(summary.getEffectivePunchIn());
                        response.setPunchOut(summary.getEffectivePunchOut());
                        return response;
                    }
                })
                .collect(Collectors.toList());
    }



    @Override
    public List<PunchRecordResponse> getOrgAttendanceByDate(Long orgId, LocalDate date) {
        List<AttendanceSummary> summaries = summaryRepo
                .findByOrganisationIdAndAttendanceDate(orgId, date);

        return summaries.stream()
                .map(summary -> {
                    PunchRecordResponse response = new PunchRecordResponse();
                    response.setEmployeeId(summary.getEmployee().getId());
                    response.setEmployeeName(
                        summary.getEmployee().getFirstName() + " " + 
                        summary.getEmployee().getLastName()
                    );
                    response.setEmployeeCode(summary.getEmployee().getEmployeeCode());
                    response.setStatus(summary.getStatus().name());
                    response.setWorkHours(summary.getWorkHours());
                    response.setPunchIn(summary.getEffectivePunchIn());
                    response.setPunchOut(summary.getEffectivePunchOut());
                    
                    if (summary.getPunchRecord() != null) {
                        response.setId(summary.getPunchRecord().getId());
                        response.setPunchSource(summary.getPunchRecord().getPunchSource().name());
                    }
                    
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceSummary>getOrgAttendanceSummary(Long orgId, LocalDate date) {
        return summaryRepo.findByOrganisationIdAndAttendanceDate(orgId, date);
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
                .findByEmployeeIdAndAttendanceDateBetween(employeeId, startDate, endDate);

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
            punch.getEmployee().getLastName()
        );
        response.setEmployeeCode(punch.getEmployee().getEmployeeCode());
        response.setPunchIn(punch.getPunchIn());
        response.setPunchOut(punch.getPunchOut());
        response.setWorkHours(punch.getWorkHours());
        response.setPunchSource(punch.getPunchSource().name());
        response.setStatus(summary.getStatus().name());
        return response;
    }
}