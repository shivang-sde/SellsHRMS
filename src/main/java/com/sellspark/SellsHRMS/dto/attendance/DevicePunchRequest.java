package com.sellspark.SellsHRMS.dto.attendance;

import lombok.Data;
import java.time.Instant;

@Data
public class DevicePunchRequest {
    private String employeeCode;
    private String biometricId;
    private String action; // IN or OUT
    private Instant timestamp;
    private Double lat;
    private Double lng;
}
