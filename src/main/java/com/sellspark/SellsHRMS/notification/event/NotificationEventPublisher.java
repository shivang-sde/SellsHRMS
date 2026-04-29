package com.sellspark.SellsHRMS.notification.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.sellspark.SellsHRMS.notification.enums.TargetRole;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    /*
     * Publish a notification event for processing
     */
    public void publish(NotificationEventData eventData) {
        eventPublisher.publishEvent(eventData);
    }

    /*
     * Publish a notification event for processing
     */
    public void publish(Long orgId, String eventCode, TargetRole targetRole,
            String recipentEmail, String recipentName) {
        publish(NotificationEventData.builder()
                .orgId(orgId)
                .eventCode(eventCode)
                .targetRole(targetRole)
                .recipientEmail(recipentEmail)
                .recipientName(recipentName)
                .build());
    }
}
