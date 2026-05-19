package com.sellspark.SellsHRMS.service;

import com.sellspark.SellsHRMS.dto.attendance.PunchRecordResponse;
import com.sellspark.SellsHRMS.entity.AttendanceSummary;

public interface AttendanceLeaveSettlementService {

    /**
     * Processes attendance status change to ON_LEAVE or HALF_DAY
     */
    void processLeaveForAttendance(AttendanceSummary summary, PunchRecordResponse request);

    /**
     * Reverses leave if attendance is changed from ON_LEAVE/HALF_DAY to something else
     */
    void reverseLeaveForAttendance(AttendanceSummary summary);

}
