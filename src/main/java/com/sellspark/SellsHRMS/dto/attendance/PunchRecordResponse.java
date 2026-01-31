package com.sellspark.SellsHRMS.dto.attendance;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PunchRecordResponse {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String department;
    private String employeeCode;
    private LocalDateTime punchIn;
    private LocalDateTime punchOut;
    private Double workHours;
    private String punchSource;
    private String status; // IN_PROGRESS, COMPLETED
}
