package com.sellspark.SellsHRMS.notification.strategy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.sellspark.SellsHRMS.notification.dto.EmailRequestDTO;
import com.sellspark.SellsHRMS.notification.entity.NotificationTemplate;
import com.sellspark.SellsHRMS.notification.entity.OrgEmailConfig;
import com.sellspark.SellsHRMS.notification.enums.DeliveryStatus;
import com.sellspark.SellsHRMS.notification.enums.EmailType;
import com.sellspark.SellsHRMS.notification.exception.InvalidSMTPConfigException;
import com.sellspark.SellsHRMS.notification.logger.StructuredLogger;
import com.sellspark.SellsHRMS.notification.repository.OrganisationEmailConfigRepository;
import com.sellspark.SellsHRMS.notification.service.NotificationTemplateService;
import com.sellspark.SellsHRMS.notification.service.TemplateRenderer;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TenantSmtpSender implements MailSenderStrategy {

    private final OrganisationEmailConfigRepository configRepository;
    private final StructuredLogger structuredLogger;
    private final NotificationTemplateService notificationTemplateService;
    private final TemplateRenderer templateRenderer;

    @Override
    public DeliveryStatus send(EmailRequestDTO request) {

        OrgEmailConfig config = configRepository
                .findByOrgIdAndIsActiveTrue(request.getOrgId())
                .orElseThrow(() -> new InvalidSMTPConfigException(
                        "No active SMTP config found for orgId: " + request.getOrgId()));

        // Rate limit check
        config.resetHourlyCounterIfNeeded();

        if (!config.canSendEmail()) {
            structuredLogger.logRateLimitExceeded(
                    request.getOrgId().toString(),
                    request.getEventCode());

            return DeliveryStatus.RATE_LIMITED;
        }

        JavaMailSender mailSender = createMailSender(config);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // ===============================
            // Load DB Template
            // ===============================
            NotificationTemplate template = notificationTemplateService.getTemplate(
                    request.getEventCode(),
                    request.getTargetRole());

            Map<String, Object> variables = new HashMap<>(Optional.ofNullable(
                    request.getTemplateVariables()).orElse(Collections.emptyMap()));
            // ===============================
            // Render Subject + Body
            // ===============================
            String subject = templateRenderer.render(
                    template.getSubject(),
                    variables);

            String body = templateRenderer.render(
                    template.getBody(),
                    variables);

            // ===============================
            // Prepare Email
            // ===============================
            helper.setFrom(
                    config.getFromEmail(),
                    config.getFromName());

            helper.setTo(request.getToEmail());
            helper.setSubject(subject);
            helper.setText(body, true);

            // Optional CC
            if (request.getCcEmails() != null &&
                    !request.getCcEmails().isEmpty()) {

                helper.setCc(
                        request.getCcEmails()
                                .toArray(new String[0]));
            }

            // Optional BCC
            if (request.getBccEmails() != null &&
                    !request.getBccEmails().isEmpty()) {

                helper.setBcc(
                        request.getBccEmails()
                                .toArray(new String[0]));
            }

            // ===============================
            // Send
            // ===============================
            mailSender.send(mimeMessage);

            // Update counters
            config.incrementSentCounters();
            configRepository.save(config);

            structuredLogger.logEmailSent(
                    request.getOrgId().toString(),
                    request.getEventCode(),
                    request.getToEmail(),
                    "TENANT_SMTP");

            return DeliveryStatus.SENT;

        } catch (Exception ex) {

            log.error("Email send failed for org: {}, to: {}, event: {}",
                    request.getOrgId(),
                    request.getToEmail(),
                    request.getEventCode(),
                    ex);

            structuredLogger.logEmailFailed(
                    request.getOrgId().toString(),
                    request.getEventCode(),
                    request.getToEmail(),
                    ex.getMessage(),
                    "TENANT_SMTP");

            return DeliveryStatus.FAILED;
        }
    }

    @Override
    public boolean supports(Long orgId, EmailType emailType) {
        return EmailType.TENANT.equals(emailType)
                || (emailType == null && orgId != null);
    }

    @Override
    public String getSenderName() {
        return "TenantSmtpSender";
    }

    // ==================================================
    // SMTP Builder
    // ==================================================
    private JavaMailSender createMailSender(OrgEmailConfig config) {

        JavaMailSenderImpl sender = new JavaMailSenderImpl();

        sender.setHost(config.getSmtpHost());
        sender.setPort(config.getSmtpPort());
        sender.setUsername(config.getSmtpUsername());
        sender.setPassword(config.getSmtpPassword());

        Properties props = sender.getJavaMailProperties();

        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");

        if (Boolean.TRUE.equals(config.getUseTls())) {
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
        }

        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");

        return sender;
    }
}