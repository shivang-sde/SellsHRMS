package com.sellspark.SellsHRMS.dto.attendance;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PunchRecordResponse {
    private Long punchId;
    private Long summaryId;
    private Long employeeId;
    private String employeeName;
    private String department;
    private String employeeCode;
    private String attendanceDate;
    private LocalDateTime punchIn;
    private LocalDateTime punchOut;
    private Double workHours;
    private String punchSource;
    private String punchedFrom;
    private String status; // IN_PROGRESS, COMPLETED
    private Boolean isLate;
    private Boolean isEarlyOut;
    private String remarks;
}
