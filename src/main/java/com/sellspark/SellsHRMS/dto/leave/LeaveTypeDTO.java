package com.sellspark.SellsHRMS.dto.leave;


import lombok.*;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class LeaveTypeDTO {
    private Long id;
    private String name;
    private String description;
    private Integer annualLimit;
    private Boolean isPaid;
    private Boolean allowHalfDay;
    private Boolean encashable;
    private Boolean carryForwardAllowed;
    private Boolean visibleToEmployees;
}

