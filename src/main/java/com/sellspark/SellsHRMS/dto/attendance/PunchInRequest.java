package com.sellspark.SellsHRMS.dto.attendance;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class PunchInRequest {
    private Long employeeId;
    private Instant punchIn;
    private String source; // WEB, MOBILE, BIOMETRIC
    private String punchedFrom;
    private Double lat;
    private Double lng;

}
