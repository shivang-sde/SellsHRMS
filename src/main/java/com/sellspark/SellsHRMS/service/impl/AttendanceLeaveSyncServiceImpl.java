package com.sellspark.SellsHRMS.service.impl;

import com.sellspark.SellsHRMS.entity.AttendanceSummary;
import com.sellspark.SellsHRMS.entity.Leave;
import com.sellspark.SellsHRMS.repository.AttendanceSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AttendanceLeaveSyncServiceImpl implements com.sellspark.SellsHRMS.service.AttendanceLeaveSyncService {

    private final AttendanceSummaryRepository attendanceSummaryRepository;

    @Override
    public void syncApprovedLeaveToAttendance(Leave leave) {
        log.info("Synchronizing approved leave {} to attendance summaries for employee {}", 
                leave.getId(), leave.getEmployee().getId());

        // Generate all dates in the leave range
        List<LocalDate> dateRange = generateDateRange(leave.getStartDate(), leave.getEndDate());
        
        for (LocalDate date : dateRange) {
            // Find existing attendance summary for this employee and date
            AttendanceSummary summary = attendanceSummaryRepository
                    .findByEmployeeIdAndAttendanceDate(leave.getEmployee().getId(), date)
                    .orElse(null);

            if (summary != null) {
                updateAttendanceSummaryForLeave(summary, leave, date);
            } else {
                log.warn("No existing AttendanceSummary found for employee {} on date {}. " +
                        "Skipping synchronization for this date.", 
                        leave.getEmployee().getId(), date);
            }
        }
    }

    /**
     * Updates the attendance summary with leave information based on the leave type
     * and day breakdown for the specific date.
     */
    private void updateAttendanceSummaryForLeave(AttendanceSummary summary, Leave leave, LocalDate date) {
        boolean isHalfDay = isHalfDayForDate(leave, date);
        
        if (isHalfDay) {
            summary.setStatus(AttendanceSummary.AttendanceStatus.HALF_DAY);
        } else {
            summary.setStatus(AttendanceSummary.AttendanceStatus.ON_LEAVE);
        }
        
        summary.setLeave(leave);
        summary.setSource(AttendanceSummary.AttendanceSource.LEAVE_SYSTEM);
        
        log.debug("Updated AttendanceSummary {} for employee {} on date {} to status {} with leave {}", 
                summary.getId(), leave.getEmployee().getId(), date, summary.getStatus(), leave.getId());
    }

    /**
     * Determines if a specific date in the leave range should be treated as half-day.
     */
    private boolean isHalfDayForDate(Leave leave, LocalDate date) {
        // Single day leave - use leaveDays to determine if it's half day
        if (leave.getStartDate().equals(leave.getEndDate())) {
            // For single day, if leaveDays is 0.5, it's a half day
            return leave.getLeaveDays() != null && leave.getLeaveDays() == 0.5;
        }
        
        // Multi-day leave - check if this is the first or last day with half-day breakdown
        boolean isFirstDay = date.equals(leave.getStartDate());
        boolean isLastDay = date.equals(leave.getEndDate());
        
        if (isFirstDay) {
            // First day is half day if startDayBreakdown is FIRST_HALF or SECOND_HALF
            return leave.getStartDayBreakdown() != null && 
                   (leave.getStartDayBreakdown() == Leave.DayBreakdown.FIRST_HALF || 
                    leave.getStartDayBreakdown() == Leave.DayBreakdown.SECOND_HALF);
        } else if (isLastDay) {
            // Last day is half day if endDayBreakdown is FIRST_HALF or SECOND_HALF
            return leave.getEndDayBreakdown() != null && 
                   (leave.getEndDayBreakdown() == Leave.DayBreakdown.FIRST_HALF || 
                    leave.getEndDayBreakdown() == Leave.DayBreakdown.SECOND_HALF);
        }
        
        // Intermediate days in multi-day leave are always full day
        return false;
    }

    /**
     * Generates a list of dates from startDate to endDate (inclusive).
     */
    private List<LocalDate> generateDateRange(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate current = startDate;
        
        while (!current.isAfter(endDate)) {
            dates.add(current);
            current = current.plusDays(1);
        }
        
        return dates;
    }
}