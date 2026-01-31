package com.sellspark.SellsHRMS.dto.attendance;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class PunchOutRequest {
    private Long punchId;
    private Instant punchOut;
}