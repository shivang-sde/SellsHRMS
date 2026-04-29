package com.sellspark.SellsHRMS.notification.event;

import java.util.List;
import java.util.Map;

import com.sellspark.SellsHRMS.notification.enums.EmailType;
import com.sellspark.SellsHRMS.notification.enums.TargetRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEventData {

    // Tenant / Organisation
    private Long orgId;

    // Event info
    private String eventCode;

    // Who this notification is for
    private TargetRole targetRole;

    // Main recipient
    private String recipientEmail;
    private String recipientName;

    // Optional CC / BCC
    private List<String> ccEmails;
    private List<String> bccEmails;

    // Optional manual subject override
    // If null => subject from DB template
    private String subject;

    // Optional template override
    // If null => resolve by eventCode + role
    private String templateName;

    // Dynamic variables for Thymeleaf template
    private Map<String, Object> templateVariables;

    // SMTP type
    @Builder.Default
    private EmailType emailType = EmailType.TENANT;

    // Retry attempts
    @Builder.Default
    private Integer retryCount = 3;
}