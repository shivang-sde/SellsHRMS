package com.sellspark.SellsHRMS.dto.attendance;

import lombok.Data;

@Data
public class AttendanceSummaryResponse {
    private Long employeeId;
    private String employeeName;
    private Integer totalDaysPresent;
    private Integer totalDaysAbsent;
    private Double totalWorkHours;
    private Integer lateCheckIns;
    private Integer earlyCheckOuts;
}
