package com.sellspark.SellsHRMS.notification.dto;

import java.util.List;

import com.sellspark.SellsHRMS.notification.enums.TargetRole;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationTemplateResponseDTO {
    private Long id;
    private String eventCode;
    private TargetRole targetRole;
    private String subject;
    private String body;
    private Boolean isActive;
    private String updatedTime;
    private List<String> variables;
}
