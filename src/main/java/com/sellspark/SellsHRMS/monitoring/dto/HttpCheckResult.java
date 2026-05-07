package com.sellspark.SellsHRMS.monitoring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HttpCheckResult {
    private Integer statusCode;
    private Integer responseTime;
    private boolean isUp;
    private String error;

    public static HttpCheckResult success(Integer statusCode, Integer responseTime, boolean isUp) {
        return HttpCheckResult.builder()
                .statusCode(statusCode)
                .responseTime(responseTime)
                .isUp(isUp)
                .build();
    }

    public static HttpCheckResult failure(Integer statusCode, Integer responseTime, boolean isUp, String error) {
        return HttpCheckResult.builder()
                .statusCode(statusCode)
                .responseTime(responseTime)
                .isUp(isUp)
                .error(error)
                .build();
    }
}