package com.sellspark.SellsHRMS.notification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EmailConfigDTO {

    @NotNull(message = "Organization ID is required")
    private Long orgId;

    @NotBlank(message = "Host is required")
    private String smtpHost;

    @NotNull(message = "Port is required")
    private Integer smtpPort;

    @NotBlank(message = "Username is required")
    private String smtpUsername;

    @NotBlank(message = "Password is required")
    private String smtpPassword;

    @NotNull(message = "Use TLS is required")
    private Boolean useTls;

    @NotNull(message = "Use SSL is required")
    private Boolean useSsl;

    @NotBlank(message = "From email is required")
    @Email(message = "Invalid email format")
    private String fromEmail;

    @NotBlank(message = "From name is required")
    private String fromName;

    @Builder.Default
    private Boolean isActive = true;

    @Min(1)
    @Builder.Default
    private Integer dailyLimit = 100;

    @Min(1)
    @Builder.Default
    private Integer hourlyLimit = 20;

    @Builder.Default
    private Integer sentToday = 0;

    @Builder.Default
    private Integer sentThisHour = 0;

    private java.time.LocalDateTime lastResetAt;

    private java.time.LocalDateTime createdAt;

    private java.time.LocalDateTime updatedAt;
}
