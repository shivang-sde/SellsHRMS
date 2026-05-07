package com.sellspark.SellsHRMS.monitoring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// SlowestUrlDTO.java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlowestUrlDTO {
    private String urlId;
    private String name;
    private String url;
    private Double avgResponseTime;
    private Long checkCount;
}