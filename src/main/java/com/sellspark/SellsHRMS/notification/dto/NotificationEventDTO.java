package com.sellspark.SellsHRMS.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEventDTO {

    @NotNull(message = "Id is required")
    private Long id;

    @NotBlank(message = "Module is required")
    @Size(max = 50, message = "Module must be less than 50 characters")
    private String module;

    @NotBlank(message = "Event code is required")
    @Size(max = 50, message = "Event code must be less than 50 characters")
    private String eventCode;

    @NotBlank(message = "Description is required")
    @Size(max = 255, message = "Description must be less than 255 characters")
    private String description;

    @NotNull(message = "Is active is required")
    private Boolean isActive;

}
