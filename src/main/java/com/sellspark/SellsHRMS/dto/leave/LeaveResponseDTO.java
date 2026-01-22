package com.sellspark.SellsHRMS.dto.leave;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveResponseDTO {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private Long leaveTypeId;
    private String leaveTypeName;
    private String reason;
    private LocalDate startDate;
    private String startDayBreakdown;
    private LocalDate endDate;
    private String endDayBreakdown;
    private Double leaveDays;
    private String leaveYear;
    private String status;
    private String approverName;
    private Long approverById;
    private String approverRemarks;
    private LocalDate appliedOn;
    private LocalDate approvedOn;
    
}





