package com.sellspark.SellsHRMS.notification.service;

import com.sellspark.SellsHRMS.notification.dto.EmailRequestDTO;

public interface EmailService {

    /**
     * Send email asynchronously using appropriate strategy
     */
    public void sendEmailAsync(EmailRequestDTO request);

    /**
     * Send email synchronously (for testing or critical emails)
     */
    public void sendEmailSync(EmailRequestDTO request);
}
