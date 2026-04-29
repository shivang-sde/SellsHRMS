package com.sellspark.SellsHRMS.notification.strategy;

import com.sellspark.SellsHRMS.notification.dto.EmailRequestDTO;
import com.sellspark.SellsHRMS.notification.enums.DeliveryStatus;
import com.sellspark.SellsHRMS.notification.enums.EmailType;

public interface MailSenderStrategy {

    /**
     * Send email using configured SMTP settings
     * 
     * @param request Email request details
     * @return Delivery status
     */
    DeliveryStatus send(EmailRequestDTO request);

    /**
     * Check if this strategy can handle the request
     */
    boolean supports(Long orgId, EmailType emailType);

    /**
     * Get sender name for logging
     */
    String getSenderName();
}
