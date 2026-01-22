package com.sellspark.SellsHRMS.dto.attendance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlySummaryDTO {

    private Long employeeId;
    private String firstName;
    private String lastName;

    private Long presentCount;
    private Long absentCount;
    private Long halfDayCount;
    private Long leaveCount;

    private Double totalWorkHours;

}
