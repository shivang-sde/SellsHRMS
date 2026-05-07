package com.sellspark.SellsHRMS.monitoring.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UptimeTrendDTO {
    private List<String> labels; // dates "dd MMM"
    private List<Double> values; // uptime percentage
}