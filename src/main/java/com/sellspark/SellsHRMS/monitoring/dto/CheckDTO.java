package com.sellspark.SellsHRMS.monitoring.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckDTO {
    private String id;
    private Integer statusCode;
    private Integer responseTime;
    private Boolean isUp;
    private String error;
    private LocalDateTime checkedAt;
}