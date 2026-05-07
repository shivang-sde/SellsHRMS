package com.sellspark.SellsHRMS.monitoring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateUrlRequest {
    private String name;
    private String url;
    private String method; // GET, POST, HEAD
    private Integer checkInterval;
    private Integer timeout;
    private Integer failureThreshold;
    private String groupId; // Optional
}