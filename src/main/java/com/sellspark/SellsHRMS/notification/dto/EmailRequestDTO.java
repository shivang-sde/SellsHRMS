package com.sellspark.SellsHRMS.notification.dto;

import java.util.Map;

import com.sellspark.SellsHRMS.notification.enums.EmailType;
import com.sellspark.SellsHRMS.notification.enums.TargetRole;
import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequestDTO {

    private Long orgId;

    private String eventCode;

    private TargetRole targetRole;

    // Primary recipient
    @NonNull
    private String toEmail;

    private String toName;

    // Optional CC/BCC
    private List<String> ccEmails;

    private List<String> bccEmails;

    // Optional manual override subject
    private String subject;

    // Optional if specific template name used
    private String templateName;

    // Variables for Thymeleaf
    private Map<String, Object> templateVariables;

    @Builder.Default
    private Integer retryCount = 3;

    @Builder.Default
    private EmailType emailType = EmailType.TENANT;
}