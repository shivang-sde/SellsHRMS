package com.sellspark.SellsHRMS.notification.service;

import java.util.List;

import com.sellspark.SellsHRMS.notification.dto.NotificationTemplatePreviewRequestDTO;
import com.sellspark.SellsHRMS.notification.dto.NotificationTemplateResponseDTO;
import com.sellspark.SellsHRMS.notification.dto.NotificationTemplateRequestDTO;
import com.sellspark.SellsHRMS.notification.entity.NotificationTemplate;
import com.sellspark.SellsHRMS.notification.enums.TargetRole;

public interface NotificationTemplateService {
    NotificationTemplate getTemplate(String eventCode, TargetRole targetRole);

    List<NotificationTemplateResponseDTO> getAllTemplates();

    NotificationTemplateResponseDTO getTemplateById(Long id);

    NotificationTemplateResponseDTO createTemplate(NotificationTemplateRequestDTO request);

    NotificationTemplateResponseDTO updateTemplate(Long id, NotificationTemplateRequestDTO request);

    NotificationTemplateResponseDTO toggleTemplateStatus(Long id);

    NotificationTemplateResponseDTO previewTemplate(Long id, NotificationTemplatePreviewRequestDTO request);

    List<NotificationTemplateResponseDTO> seedDefaultTemplates();

}
