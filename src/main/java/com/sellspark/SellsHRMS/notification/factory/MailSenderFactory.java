package com.sellspark.SellsHRMS.notification.factory;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sellspark.SellsHRMS.notification.dto.EmailRequestDTO;
import com.sellspark.SellsHRMS.notification.enums.DeliveryStatus;
import com.sellspark.SellsHRMS.notification.enums.EmailType;
import com.sellspark.SellsHRMS.notification.strategy.MailSenderStrategy;

@Component
public class MailSenderFactory {

    private final List<MailSenderStrategy> strategies;
    private final Map<String, MailSenderStrategy> strategyMap;

    public MailSenderFactory(List<MailSenderStrategy> strategies) {
        this.strategies = strategies;
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        MailSenderStrategy::getSenderName,
                        Function.identity()));
    }

    /**
     * Get appropriate sender based on orgId and emailType
     */
    public MailSenderStrategy getSender(Long orgId, EmailType emailType) {
        // Priority: explicit type > org-based detection > default system
        if (EmailType.SYSTEM.equals(emailType)) {
            return strategyMap.get("SystemSmtpSender");
        }

        if (EmailType.TENANT.equals(emailType)) {
            return strategyMap.get("TenantSmtpSender");
        }

        // Auto-detect: if orgId provided, try tenant; else system
        for (MailSenderStrategy strategy : strategies) {
            if (strategy.supports(orgId, emailType)) {
                return strategy;
            }
        }

        // Fallback to system sender
        return strategyMap.get("SystemSmtpSender");
    }

    /**
     * Send email using appropriate strategy
     */
    public DeliveryStatus sendEmail(EmailRequestDTO request) {
        MailSenderStrategy sender = getSender(request.getOrgId(), request.getEmailType());
        return sender.send(request);
    }
}
