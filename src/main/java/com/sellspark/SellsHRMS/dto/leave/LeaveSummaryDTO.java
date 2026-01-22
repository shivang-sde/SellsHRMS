package com.sellspark.SellsHRMS.dto.leave;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveSummaryDTO {
    private Long id;
    private String employeeName;
    private String leaveTypeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double leaveDays;
    private String status;
}
