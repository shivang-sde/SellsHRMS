package com.sellspark.SellsHRMS.notification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EmailTestRequestDTO {

    @NotNull(message = "Organization ID is required")
    private Long orgId;

    private String testName = "HRMS Notification Test";

    @NotBlank(message = "Test email is required")
    @Email(message = "Invalid email address")
    private String testEmail;

    @NotBlank(message = "Subject is required")
    private String subject = "Test Email";

    @NotBlank(message = "Body is required")
    private String body = "This is a test email";

}
