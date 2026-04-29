package com.sellspark.SellsHRMS.notification.strategy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.sellspark.SellsHRMS.notification.dto.EmailRequestDTO;
import com.sellspark.SellsHRMS.notification.enums.DeliveryStatus;
import com.sellspark.SellsHRMS.notification.enums.EmailType;
import com.sellspark.SellsHRMS.notification.logger.StructuredLogger;
import jakarta.mail.internet.MimeMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SystemSmtpSender implements MailSenderStrategy {

    private final JavaMailSender systemMailSender;
    private final StructuredLogger structuredLogger;

    @Value("${notification.system.from-email:noreply@sellspark.com}")
    private String systemFromEmail;

    @Value("${notification.system.from-name:Sellspark HRMS}")
    private String systemFromName;

    @Override
    public DeliveryStatus send(EmailRequestDTO request) {
        try {
            MimeMessage mimeMessage = systemMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(systemFromEmail, systemFromName);
            helper.setTo(request.getToEmail());
            helper.setSubject("[HRMS] " + request.getSubject());
            helper.setText(renderSystemTemplate(request), true);

            if (request.getCcEmails() != null && !request.getCcEmails().isEmpty()) {
                helper.setCc(request.getCcEmails().toArray(new String[0]));
            }

            if (request.getBccEmails() != null && !request.getBccEmails().isEmpty()) {
                helper.setBcc(request.getBccEmails().toArray(new String[0]));
            }

            systemMailSender.send(mimeMessage);
            structuredLogger.logEmailSent(
                    request.getOrgId() != null ? request.getOrgId().toString() : "SYSTEM",
                    request.getEventCode(),
                    request.getToEmail(),
                    "SYSTEM");
            return DeliveryStatus.SENT;
        } catch (Exception e) {
            structuredLogger.logEmailFailed(
                    request.getOrgId() != null ? request.getOrgId().toString() : "SYSTEM",
                    request.getEventCode(),
                    request.getToEmail(),
                    e.getMessage(),
                    "SYSTEM");
            return DeliveryStatus.FAILED;
        }

    }

    @Override
    public boolean supports(Long orgId, EmailType emailType) {
        return EmailType.SYSTEM.equals(emailType)
                || (orgId == null || EmailType.SYSTEM.equals(emailType));
    }

    @Override
    public String getSenderName() {
        return "SystemSmtpSender";
    }

    private String renderSystemTemplate(EmailRequestDTO request) {
        return "<html><body>" +
                "<div style='font-family:Arial,sans-serif;max-width:600px;margin:0 auto;'>" +
                "<h2 style='color:#2c3e50;'>" + request.getSubject() + "</h2>" +
                "<p>Dear " + request.getToName() + ",</p>" +
                "<p>This is an automated notification from Sellspark HRMS.</p>" +
                "<hr/><small style='color:#7f8c8d;'>Org: " +
                (request.getOrgId() != null ? request.getOrgId() : "Platform") +
                " | Event: " + request.getEventCode() + "</small>" +
                "</div></body></html>";
    }
}
