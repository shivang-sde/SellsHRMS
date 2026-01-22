package com.sellspark.SellsHRMS.dto.leave;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveRequestDTO {
    private Long leaveTypeId;
    private LocalDate startDate;
    private String startDayBreakdown;
    private LocalDate endDate;
    private String endDayBreakdown;
    private String reason;
    private Boolean isHalfDay; // optional
}


