package com.sellspark.SellsHRMS.dto.attendance;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PunchOutRequest {
    private Long punchId;
    private LocalDateTime punchOut;
}