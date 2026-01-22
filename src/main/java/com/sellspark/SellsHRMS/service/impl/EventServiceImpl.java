package com.sellspark.SellsHRMS.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sellspark.SellsHRMS.dto.event.EventRequestDTO;
import com.sellspark.SellsHRMS.dto.event.EventResponseDTO;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.OrganisationEvent;
import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
import com.sellspark.SellsHRMS.exception.UnauthorizedAccessException;
import com.sellspark.SellsHRMS.repository.OrganisationEventRepository;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;
import com.sellspark.SellsHRMS.service.EventService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
class EventServiceImpl implements EventService {

    private final OrganisationEventRepository eventRepository;
    private final OrganisationRepository organisationRepository;

    @Override
    public EventResponseDTO createEvent(Long orgId, EventRequestDTO request) {
        Organisation org = organisationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organisation not found"));

        OrganisationEvent event = OrganisationEvent.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .location(request.getLocation())
                .type(OrganisationEvent.EventType.valueOf(request.getType()))
                .organisation(org)
                .build();

        event = eventRepository.save(event);
        return toResponseDTO(event);
    }

    @Override
    public EventResponseDTO updateEvent(Long eventId, Long orgId, EventRequestDTO request) {
        OrganisationEvent event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));

        if (!event.getOrganisation().getId().equals(orgId)) {
            throw new UnauthorizedAccessException();
        }

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setLocation(request.getLocation());
        event.setType(OrganisationEvent.EventType.valueOf(request.getType()));

        event = eventRepository.save(event);
        return toResponseDTO(event);
    }

    @Override
    public void deleteEvent(Long eventId, Long orgId) {
        OrganisationEvent event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));

        if (!event.getOrganisation().getId().equals(orgId)) {
            throw new UnauthorizedAccessException();
        }

        eventRepository.delete(event);
    }

    @Override
    public EventResponseDTO getEventById(Long eventId, Long orgId) {
        OrganisationEvent event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));

        if (!event.getOrganisation().getId().equals(orgId)) {
            throw new UnauthorizedAccessException();
        }

        return toResponseDTO(event);
    }

    @Override
    public List<EventResponseDTO> getAllEvents(Long orgId) {
        Organisation org = organisationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organisation", "id", orgId));

        return eventRepository.findByOrganisationOrderByStartDateDesc(org).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventResponseDTO> getUpcomingEvents(Long orgId) {
        Organisation org = organisationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organisation", "id", orgId));

        LocalDate today = LocalDate.now();
        return eventRepository.findByOrganisationAndStartDateGreaterThanEqualOrderByStartDateAsc(org, today).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventResponseDTO> getEventsByType(Long orgId, String type) {
        Organisation org = organisationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organisation", "id", orgId));

        OrganisationEvent.EventType eventType = OrganisationEvent.EventType.valueOf(type);
        return eventRepository.findByOrganisationAndTypeOrderByStartDateDesc(org, eventType).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private EventResponseDTO toResponseDTO(OrganisationEvent event) {
        return EventResponseDTO.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .location(event.getLocation())
                .type(event.getType().toString())
                .organisationId(event.getOrganisation().getId())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }
}
