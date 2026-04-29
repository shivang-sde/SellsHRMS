package com.sellspark.SellsHRMS.notification.dto;

import java.util.Map;

import lombok.Data;

@Data
public class NotificationTemplatePreviewRequestDTO {
    private Map<String, String> sampleData;
}
