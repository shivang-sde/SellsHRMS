package com.sellspark.SellsHRMS.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferenceDTO {

    @NotNull(message = "Organization ID is required")
    private Long orgId;

    @NotNull(message = "Event ID is required")
    private Long eventId;

    @NotBlank(message = "Event code is required")
    private String eventCode;

    @NotNull(message = "Email enabled is required")
    private Boolean emailEnabled;

}
