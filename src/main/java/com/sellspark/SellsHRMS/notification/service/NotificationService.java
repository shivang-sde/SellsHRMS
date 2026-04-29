package com.sellspark.SellsHRMS.notification.service;

import java.util.List;

import com.sellspark.SellsHRMS.notification.dto.NotificationEventDTO;
import com.sellspark.SellsHRMS.notification.dto.NotificationPreferenceDTO;

public interface NotificationService {
    public NotificationPreferenceDTO setPreference(NotificationPreferenceDTO dto);

    public List<NotificationPreferenceDTO> getPreferences(Long orgId);

    public void togglePreference(Long orgId, Long eventId);

    public NotificationEventDTO saveEvent(NotificationEventDTO dto);

    public List<NotificationEventDTO> getAllEvents();

    public boolean isNotificationEnabled(Long orgId, String eventCode);

    public void validateNotificationEnabled(Long orgId, String eventCode);

    // public NotificationEvent getEvent(Long id, String eventCode);

    // public NotificationEvent updateEvent(NotificationEventDTO dto);

    public void deleteEvent(Long id);

    public void toggleEventStatus(Long eventId);

}
