package com.sellspark.SellsHRMS.service.impl;

import com.sellspark.SellsHRMS.config.UserPrincipal;
import com.sellspark.SellsHRMS.dto.attendance.PunchRecordResponse;
import com.sellspark.SellsHRMS.dto.leave.LeaveRequestDTO;
import com.sellspark.SellsHRMS.entity.*;
import com.sellspark.SellsHRMS.exception.InvalidOperationException;
import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
import com.sellspark.SellsHRMS.repository.LeaveRepository;
import com.sellspark.SellsHRMS.repository.LeaveTypeRepository;
import com.sellspark.SellsHRMS.repository.OrganisationPolicyRepository;
import com.sellspark.SellsHRMS.service.AttendanceLeaveSettlementService;
import com.sellspark.SellsHRMS.service.LeaveService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AttendanceLeaveSettlementServiceImpl implements AttendanceLeaveSettlementService {

    private final LeaveService leaveService;
    private final LeaveRepository leaveRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final OrganisationPolicyRepository organisationPolicyRepository;

    @Override
    public void processLeaveForAttendance(AttendanceSummary summary, PunchRecordResponse request) {
        if (request.getLeaveTypeId() == null) {
            throw new InvalidOperationException(
                    "Leave type is required when marking attendance as ON_LEAVE or HALF_DAY");
        }

        Employee emp = summary.getEmployee();
        Organisation org = summary.getOrganisation();

        LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("LeaveType", "leave", request.getLeaveReason()));

        // Check if there's already a leave linked, if leave type changed, cancel old
        if (summary.getLeave() != null) {
            Leave oldLeave = summary.getLeave();
            if (oldLeave.getLeaveType().getId().equals(leaveType.getId())
                    && oldLeave.getLeaveStatus() != Leave.LeaveStatus.CANCELED) {
                // For simplicity, always reverse and recreate on edit
                reverseLeaveForAttendance(summary);
            } else {
                reverseLeaveForAttendance(summary);
            }
        }

        double days = summary.getStatus() == AttendanceSummary.AttendanceStatus.ON_LEAVE ? 1.0 : 0.5;

        Leave.DayBreakdown breakdown = Leave.DayBreakdown.FULL_DAY;
        if (summary.getStatus() == AttendanceSummary.AttendanceStatus.HALF_DAY) {
            if ("FIRST_HALF".equals(request.getHalfDayType())) {
                breakdown = Leave.DayBreakdown.FIRST_HALF;
            } else if ("SECOND_HALF".equals(request.getHalfDayType())) {
                breakdown = Leave.DayBreakdown.SECOND_HALF;
            } else {
                throw new InvalidOperationException(
                        "Half Day Type (FIRST_HALF / SECOND_HALF) is required for HALF_DAY status.");
            }
        }

        OrganisationPolicy policy = organisationPolicyRepository.findByOrganisation(org)
                .orElseThrow(() -> new ResourceNotFoundException("OrganisationPolicy", "organisationId", org.getId()));

        LeaveRequestDTO dummyReq = new LeaveRequestDTO();
        dummyReq.setStartDate(summary.getAttendanceDate());
        dummyReq.setEndDate(summary.getAttendanceDate());
        dummyReq.setStartDayBreakdown(breakdown.name());
        dummyReq.setEndDayBreakdown(breakdown.name());
        dummyReq.setIsHalfDay(days == 0.5);

        // Validate Leave
        leaveService.validateLeaveAgainstTypePolicy(emp, leaveType, dummyReq, days, policy);

        String leaveYear = leaveService.getCurrentLeaveYear(org.getId());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long approverUserId = null;
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal) {
            approverUserId = ((UserPrincipal) auth.getPrincipal()).getId();
        }

        Leave newLeave = Leave.builder()
                .organisation(org)
                .employee(emp)
                .leaveType(leaveType)
                .startDate(summary.getAttendanceDate())
                .endDate(summary.getAttendanceDate())
                .startDayBreakdown(breakdown)
                .endDayBreakdown(breakdown)
                .leaveDays(days)
                .reason(request.getLeaveReason() != null ? request.getLeaveReason() : "Attendance regularization")
                .leaveStatus(Leave.LeaveStatus.PENDING)
                .appliedOn(LocalDate.now())
                .source(Leave.LeaveSource.HR_MANUAL)
                .leaveYear(leaveYear)
                .build();

        newLeave = leaveRepository.save(newLeave);

        // Auto approve
        if (Boolean.TRUE.equals(request.getAutoApproveLeave()) || request.getAutoApproveLeave() == null) {
            leaveService.approveLeave(newLeave.getId(), approverUserId, "Auto-approved via Attendance Edit",
                    org.getId());
            newLeave = leaveRepository.findById(newLeave.getId()).orElse(newLeave);
        }

        summary.setLeave(newLeave);
        summary.setSource(AttendanceSummary.AttendanceSource.LEAVE_SYSTEM);
    }

    @Override
    public void reverseLeaveForAttendance(AttendanceSummary summary) {
        if (summary.getLeave() != null) {
            Leave leave = summary.getLeave();
            if (leave.getLeaveStatus() != Leave.LeaveStatus.CANCELED) {
                leaveService.cancelLeave(leave.getId(), leave.getEmployee().getId(), leave.getOrganisation().getId());
            }
            summary.setLeave(null);
        }
    }
}
