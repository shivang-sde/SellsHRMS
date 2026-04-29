package com.sellspark.SellsHRMS.notification.event;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.sellspark.SellsHRMS.notification.dto.EmailRequestDTO;
import com.sellspark.SellsHRMS.notification.enums.DeliveryStatus;
import com.sellspark.SellsHRMS.notification.enums.EmailType;
import com.sellspark.SellsHRMS.notification.exception.NotificationDisabledException;
import com.sellspark.SellsHRMS.notification.factory.MailSenderFactory;
import com.sellspark.SellsHRMS.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final MailSenderFactory mailSenderFactory;

    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotificationEvent(NotificationEventData eventData) {

        try {
            log.debug("Processing notification event: {} for org: {}",
                    eventData.getEventCode(), eventData.getOrgId());

            // Check if notification is enabled for this org + role
            boolean isEnabled = notificationService.isNotificationEnabled(
                    eventData.getOrgId(), eventData.getEventCode());

            if (!isEnabled) {
                log.debug("Notification disabled for event: {} for org: {}",
                        eventData.getEventCode(), eventData.getOrgId());
                return;
            }

            // Build email request
            EmailRequestDTO emailRequest = EmailRequestDTO.builder()
                    .orgId(eventData.getOrgId())
                    .eventCode(eventData.getEventCode())
                    .targetRole(eventData.getTargetRole())

                    .toName(eventData.getRecipientName())
                    .toEmail(eventData.getRecipientEmail())

                    .ccEmails(eventData.getCcEmails())
                    .bccEmails(eventData.getBccEmails())

                    .subject(eventData.getSubject())
                    .templateVariables(eventData.getTemplateVariables())
                    .templateName(eventData.getTemplateName())

                    .emailType(EmailType.TENANT)
                    .retryCount(eventData.getRetryCount())
                    .build();

            DeliveryStatus status = mailSenderFactory.sendEmail(emailRequest);
            if (status == DeliveryStatus.SENT) {
                log.debug("Notification sent successfully for event: {} for org: {}",
                        eventData.getEventCode(),
                        eventData.getOrgId());
            } else {
                log.warn("Notification not sent. Status: {} event: {} org: {}",
                        status,
                        eventData.getEventCode(),
                        eventData.getOrgId());
            }

        } catch (NotificationDisabledException e) {
            log.debug("Notification intentionally skipped: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Invalid event data: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Failed to process notification event: {}", eventData.getEventCode(), e);
            // could queue for retry
        }

    }
}
