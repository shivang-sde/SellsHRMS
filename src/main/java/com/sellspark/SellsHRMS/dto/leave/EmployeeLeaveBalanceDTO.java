package com.sellspark.SellsHRMS.dto.leave;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeLeaveBalanceDTO {

    private Long id;

    // Employee details
    private Long employeeId;
    private String employeeName;
    private String employeeCode; // optional, if you have one
    private String departmentName;

    // Leave Type details
    private Long leaveTypeId;
    private String leaveTypeName;
    private Boolean isPaid;

    // Financial info
    private String leaveYear;

    // Leave stats
    private Double openingBalance;
    private Double accrued;
    private Double availed;
    private Double carriedForward;
    private Double encashed;
    private Double closingBalance;
}
