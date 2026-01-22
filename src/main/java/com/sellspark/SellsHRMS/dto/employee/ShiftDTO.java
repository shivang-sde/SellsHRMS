package com.sellspark.SellsHRMS.dto.employee;

import lombok.Data;
import java.time.LocalTime;

@Data
public class ShiftDTO {
    private Long orgId;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer breakMinutes;
    private Boolean isNightShift;
}