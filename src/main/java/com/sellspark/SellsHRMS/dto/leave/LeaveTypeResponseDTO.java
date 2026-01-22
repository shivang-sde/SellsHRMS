package com.sellspark.SellsHRMS.dto.leave;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveTypeResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Integer annualLimit;
    private Boolean isPaid;
    private Boolean carryForwardAllowed;
    private Integer carryForwardLimit;
    private Boolean encashable;
    private Boolean requiresApproval;
    private Boolean availableDuringProbation;
    private Boolean allowHalfDay;
    private Boolean includeHolidaysInLeave;
    private Boolean visibleToEmployees;
    private Integer maxConsecutiveDays;
    private String accrualMethod;
    private String applicableGender;
    private String resetCycle;
    private Double accrualRate;
    private Integer validityDays;
    private Long orgId;
}
