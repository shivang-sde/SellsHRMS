package com.sellspark.SellsHRMS.dto.attendance;

import lombok.Data;
import java.time.LocalDateTime;


@Data
public class PunchInRequest {
    private Long employeeId;
    private LocalDateTime punchIn;
    private String source; // WEB, MOBILE, BIOMETRIC
}
