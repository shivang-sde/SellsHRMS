package com.sellspark.SellsHRMS.dto.leave;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class LeaveDTO {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private Long leaveTypeId;
    private String leaveTypeName;
    private String reason;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double leaveDays;
    private String financialYear;
    private String status;
    private String approverName;
    private String approverRemarks;
    private LocalDate appliedOn;
    private LocalDate approvedOn;
}
