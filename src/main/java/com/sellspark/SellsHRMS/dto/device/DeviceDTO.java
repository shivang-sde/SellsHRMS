package com.sellspark.SellsHRMS.dto.device;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DeviceDTO {
    private Long id;
    private String name;
    private String deviceCode;
    private String apiKey; // Only shown upon creation usually, but keeping it here for simplicity as per
                           // requirements
    private String status;
    private LocalDateTime createdAt;
}
