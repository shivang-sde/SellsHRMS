package com.sellspark.SellsHRMS.service;

import com.sellspark.SellsHRMS.entity.Leave;

/**
 * Service for synchronizing approved leave records to AttendanceSummary.
 * This is a narrowly scoped service specifically for leave-to-attendance synchronization
 * to avoid circular dependencies between LeaveService and AttendanceLeaveSettlementService.
 */
public interface AttendanceLeaveSyncService {

    /**
     * Synchronizes an approved leave to the corresponding AttendanceSummary records.
     * For each date in the leave's date range, updates the existing AttendanceSummary
     * to reflect the leave status and reference the approved leave.
     *
     * @param leave the approved leave to synchronize
     */
    void syncApprovedLeaveToAttendance(Leave leave);
}