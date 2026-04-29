package com.sellspark.SellsHRMS.notification.service.impl;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.sellspark.SellsHRMS.notification.dto.EmailRequestDTO;
import com.sellspark.SellsHRMS.notification.factory.MailSenderFactory;
import com.sellspark.SellsHRMS.notification.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final MailSenderFactory mailSenderFactory;

    /**
     * Send email asynchronously using appropriate strategy
     */
    @Async("notificationExecutor")
    public void sendEmailAsync(EmailRequestDTO request) {
        try {
            mailSenderFactory.sendEmail(request);
        } catch (Exception e) {
            log.error("Async email send failed for event: {} to: {}",
                    request.getEventCode(), request.getToEmail(), e);
            // Could implement retry logic here or queue for later
        }
    }

    /**
     * Send email synchronously (for testing or critical emails)
     */
    @Override
    public void sendEmailSync(EmailRequestDTO request) {
        mailSenderFactory.sendEmail(request);
    }

}
