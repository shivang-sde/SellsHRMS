package com.sellspark.SellsHRMS.notification.service;

import com.sellspark.SellsHRMS.notification.dto.EmailConfigDTO;

public interface EmailConfigService {
    public EmailConfigDTO saveConfig(EmailConfigDTO dto);

    public EmailConfigDTO updateConfig(EmailConfigDTO dto);

    public boolean testSmtpConnection(EmailConfigDTO dto);

    public EmailConfigDTO getActiveConfig(Long OrgId);

}