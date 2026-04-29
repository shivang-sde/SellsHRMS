package com.sellspark.SellsHRMS.notification.dto;

import com.sellspark.SellsHRMS.notification.enums.TargetRole;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificationTemplateRequestDTO {

    @NotBlank(message = "Event code is required")
    private String eventCode;

    @NotNull(message = "Target role is required")
    private TargetRole targetRole;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Body is required")
    private String body;

    private Boolean isActive;
}
