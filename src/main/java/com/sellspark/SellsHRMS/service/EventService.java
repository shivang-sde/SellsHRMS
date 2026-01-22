package com.sellspark.SellsHRMS.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sellspark.SellsHRMS.dto.event.EventRequestDTO;
import com.sellspark.SellsHRMS.dto.event.EventResponseDTO;

@Service
public interface EventService {

    EventResponseDTO createEvent(Long orgId, EventRequestDTO request);
     EventResponseDTO updateEvent(Long eventId, Long orgId, EventRequestDTO request);
     void deleteEvent(Long eventId, Long orgId);
     EventResponseDTO getEventById(Long eventId, Long orgId);
      List<EventResponseDTO> getAllEvents(Long orgId);
       List<EventResponseDTO> getUpcomingEvents(Long orgId);
     List<EventResponseDTO> getEventsByType(Long orgId, String type);
}