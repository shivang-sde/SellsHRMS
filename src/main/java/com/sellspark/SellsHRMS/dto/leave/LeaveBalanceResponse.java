package com.sellspark.SellsHRMS.dto.leave;

import lombok.Data;

@Data
public class LeaveBalanceResponse {
    private Long employeeId;
    private String employeeName;
    private Long leaveTypeId;
    private String leaveTypeName;
    private Integer annualLimit;
    private Integer usedLeaves;
    private Integer remainingLeaves;
}
