package com.sellspark.SellsHRMS.notification.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sellspark.SellsHRMS.notification.dto.NotificationEventDTO;
import com.sellspark.SellsHRMS.notification.dto.NotificationPreferenceDTO;
import com.sellspark.SellsHRMS.notification.entity.NotificationEvent;
import com.sellspark.SellsHRMS.notification.entity.NotificationPreference;
import com.sellspark.SellsHRMS.notification.exception.NotificationDisabledException;
import com.sellspark.SellsHRMS.notification.mapper.NotificationEventMapper;
import com.sellspark.SellsHRMS.notification.mapper.NotificationPreferenceMapper;
import com.sellspark.SellsHRMS.notification.repository.NotificationEventRepository;
import com.sellspark.SellsHRMS.notification.repository.NotificationPreferenceRepository;
import com.sellspark.SellsHRMS.notification.service.NotificationService;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationPreferenceRepository preferenceRepo;
    private final OrganisationRepository orgRepo;
    private final NotificationEventRepository eventRepo;
    private final NotificationEventMapper notificationEventMapper;
    private final NotificationPreferenceMapper notificationPreferenceMapper;

    @Override
    public NotificationPreferenceDTO setPreference(NotificationPreferenceDTO dto) {
        NotificationPreference preference = preferenceRepo
                .findByOrganisationIdAndNotificationEventId(
                        dto.getOrgId(),
                        dto.getEventId())
                .orElseGet(() -> new NotificationPreference());
        preference.setOrganisation(orgRepo.findById(dto.getOrgId()).get());
        preference.setNotificationEvent(eventRepo.findById(dto.getEventId()).get());

        preference.setEmailEnabled(dto.getEmailEnabled());
        return notificationPreferenceMapper.toDto(preferenceRepo.save(preference));
    }

    @Override
    public List<NotificationPreferenceDTO> getPreferences(Long orgId) {
        return preferenceRepo.findByOrganisationId(orgId).stream()
                .map(notificationPreferenceMapper::toDto)
                .toList();
    }

    @Override
    public void togglePreference(Long orgId, Long eventId) {
        NotificationPreference preference = preferenceRepo
                .findByOrganisationIdAndNotificationEventId(orgId, eventId)
                .orElseThrow(() -> new RuntimeException("Preference not found"));

        preference.setEmailEnabled(!preference.getEmailEnabled());
        preferenceRepo.save(preference);
    }

    @Override
    public NotificationEventDTO saveEvent(NotificationEventDTO dto) {

        NotificationEvent event = eventRepo.findByEventCode(dto.getEventCode())
                .orElseGet(() -> NotificationEvent.builder()
                        .eventCode(dto.getEventCode())
                        .module(dto.getModule())
                        .description(dto.getDescription())
                        .isActive(dto.getIsActive())
                        .build());

        return notificationEventMapper.toDto(eventRepo.save(event));
    }

    @Override
    public List<NotificationEventDTO> getAllEvents() {
        return eventRepo.findAll().stream()
                .map(notificationEventMapper::toDto)
                .toList();
    }

    @Override
    public void deleteEvent(Long id) {
        NotificationEvent event = eventRepo.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
        eventRepo.delete(event);
    }

    @Override
    public void toggleEventStatus(Long id) {
        NotificationEvent event = eventRepo.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
        event.setIsActive(!event.getIsActive());
        eventRepo.save(event);
    }

    @Override
    public boolean isNotificationEnabled(Long orgId, String eventCode) {
        // If orgId is null, check for global/default preferences
        if (orgId == null) {
            return true; // Allow system notifications by default
        }

        Optional<NotificationEvent> eventOpt = eventRepo.findByEventCode(eventCode);
        if (eventOpt.isEmpty() || !eventOpt.get().getIsActive()) {
            return false;
        }

        return preferenceRepo.existsByOrganisationIdAndNotificationEventId(orgId, eventOpt.get().getId());
    }

    /**
     * Validate and throw if disabled
     */
    @Override
    public void validateNotificationEnabled(Long orgId, String eventCode) {
        if (!isNotificationEnabled(orgId, eventCode)) {
            throw new NotificationDisabledException(
                    String.format("Notification disabled for event=%s, org=%s",
                            eventCode, orgId));
        }
    }

}
