package com.sellspark.SellsHRMS.dto.mapper;

import com.sellspark.SellsHRMS.dto.leave.LeaveRequestDTO;
import com.sellspark.SellsHRMS.dto.leave.LeaveResponseDTO;
import com.sellspark.SellsHRMS.entity.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Year;

@Component
public class LeaveMapper {

    /**
     * Convert LeaveRequestDTO → Leave Entity
     */
    public Leave toEntity(LeaveRequestDTO dto, Employee employee, LeaveType leaveType, Organisation organisation) {
        if (dto == null) return null;

        Leave.DayBreakdown startBreakdown = parseDayBreakdown(dto.getStartDayBreakdown(), dto.getIsHalfDay());
        Leave.DayBreakdown endBreakdown = parseDayBreakdown(dto.getEndDayBreakdown(), dto.getIsHalfDay());

        return Leave.builder()
                .organisation(organisation)
                .employee(employee)
                .leaveType(leaveType)
                .startDate(dto.getStartDate())
                .startDayBreakdown(startBreakdown)
                .endDate(dto.getEndDate())
                .endDayBreakdown(endBreakdown)
                .reason(dto.getReason())
                .leaveDays(calculateLeaveDays(dto))
                .leaveYear(Year.now().toString())
                .appliedOn(LocalDate.now())
                .leaveStatus(Leave.LeaveStatus.PENDING)
                .source(Leave.LeaveSource.EMPLOYEE_APPLY)
                .build();
    }

    /**
     * Convert Leave → LeaveResponseDTO
     */
    public LeaveResponseDTO toDTO(Leave leave) {
        if (leave == null) return null;

        return LeaveResponseDTO.builder()
                .id(leave.getId())
                .employeeId(leave.getEmployee() != null ? leave.getEmployee().getId() : null)
                .employeeName(leave.getEmployee() != null
                        ? leave.getEmployee().getFirstName() + " " + leave.getEmployee().getLastName()
                        : null)
                .leaveTypeId(leave.getLeaveType() != null ? leave.getLeaveType().getId() : null)
                .leaveTypeName(leave.getLeaveType() != null ? leave.getLeaveType().getName() : null)
                .reason(leave.getReason())
                .startDate(leave.getStartDate())
                .startDayBreakdown(leave.getStartDayBreakdown() != null ? leave.getStartDayBreakdown().name() : null)
                .endDate(leave.getEndDate())
                .endDayBreakdown(leave.getEndDayBreakdown() != null ? leave.getEndDayBreakdown().name() : null)
                .leaveDays(leave.getLeaveDays())
                .leaveYear(leave.getLeaveYear())
                .status(leave.getLeaveStatus() != null ? leave.getLeaveStatus().name() : null)
                .approverName(leave.getApprovedBy() != null
                        ? leave.getApprovedBy().getFirstName() + " " + leave.getApprovedBy().getLastName()
                        : null)
                .approverById(leave.getApprovedBy() != null ? leave.getApprovedBy().getId() : null)
                .approverRemarks(leave.getApproverRemarks())
                .appliedOn(leave.getAppliedOn())
                .approvedOn(leave.getApprovedOn())
                .build();
    }

    /**
     * Update an existing Leave entity from a DTO
     */
    public void updateEntity(Leave leave, LeaveRequestDTO dto) {
        if (leave == null || dto == null) return;

        if (dto.getStartDate() != null) leave.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) leave.setEndDate(dto.getEndDate());
        if (dto.getReason() != null) leave.setReason(dto.getReason());

        if (dto.getStartDayBreakdown() != null)
            leave.setStartDayBreakdown(parseDayBreakdown(dto.getStartDayBreakdown(), dto.getIsHalfDay()));
        if (dto.getEndDayBreakdown() != null)
            leave.setEndDayBreakdown(parseDayBreakdown(dto.getEndDayBreakdown(), dto.getIsHalfDay()));

        leave.setLeaveDays(calculateLeaveDays(dto));
    }

    // -------------------------------------------------------------
    // 🔹 Helper Methods
    // -------------------------------------------------------------

    private Leave.DayBreakdown parseDayBreakdown(String breakdown, Boolean isHalfDay) {
        if (isHalfDay != null && isHalfDay) {
            if ("FIRST_HALF".equalsIgnoreCase(breakdown)) return Leave.DayBreakdown.FIRST_HALF;
            if ("SECOND_HALF".equalsIgnoreCase(breakdown)) return Leave.DayBreakdown.SECOND_HALF;
            return Leave.DayBreakdown.FULL_DAY;
        }
        if (breakdown == null) return Leave.DayBreakdown.FULL_DAY;
        try {
            return Leave.DayBreakdown.valueOf(breakdown.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return Leave.DayBreakdown.FULL_DAY;
        }
    }

    private Double calculateLeaveDays(LeaveRequestDTO dto) {
        if (dto.getStartDate() == null || dto.getEndDate() == null) return 0.0;

        long totalDays = dto.getEndDate().toEpochDay() - dto.getStartDate().toEpochDay() + 1;
        boolean isHalfDay = dto.getIsHalfDay() != null && dto.getIsHalfDay();

        // If half-day leave requested
        if (isHalfDay || "FIRST_HALF".equalsIgnoreCase(dto.getStartDayBreakdown()) ||
                "SECOND_HALF".equalsIgnoreCase(dto.getStartDayBreakdown())) {
            return 0.5;
        }

        return (double) totalDays;
    }


    
}
