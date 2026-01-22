package com.sellspark.SellsHRMS.controller.api;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import com.sellspark.SellsHRMS.dto.employee.EmployeeResponse;
import com.sellspark.SellsHRMS.dto.event.EventRequestDTO;
import com.sellspark.SellsHRMS.dto.event.EventResponseDTO;
import com.sellspark.SellsHRMS.service.EmployeeService;
import com.sellspark.SellsHRMS.service.EventService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventRestController {

    private final EmployeeService employeeService;
    private final EventService eventService;

    @GetMapping("/{orgId}/birthdays")
    public List<EmployeeResponse> getUpcomingBirthdays(@PathVariable Long orgId) {
        LocalDate today = LocalDate.now();
        LocalDate weekEnd = today.plusDays(7);
        return employeeService.findUpcomingBirthdays(orgId, today, weekEnd);
    }

    @GetMapping("/{orgId}/anniversaries")
    public List<EmployeeResponse> getUpcomingAnniversaries(@PathVariable Long orgId) {
        LocalDate today = LocalDate.now();
        LocalDate weekEnd = today.plusDays(7);
        return employeeService.findUpcomingWorkAnniversaries(orgId, today, weekEnd);
    }



    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody EventRequestDTO request, HttpSession session) {
        Long orgId = (Long) session.getAttribute("ORG_ID");
        EventResponseDTO response = eventService.createEvent(orgId, request);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Event created successfully",
                "data", response
        ));
    }

    @GetMapping
    public ResponseEntity<?> getAllEvents(HttpSession session) {
        Long orgId = (Long) session.getAttribute("ORG_ID");
        var events = eventService.getAllEvents(orgId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", events
        ));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<?> getUpcomingEvents(HttpSession session) {
        Long orgId = (Long) session.getAttribute("ORG_ID");
        var events = eventService.getUpcomingEvents(orgId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", events
        ));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<?> getEventsByType(@PathVariable String type, HttpSession session) {
        Long orgId = (Long) session.getAttribute("ORG_ID");
        var events = eventService.getEventsByType(orgId, type);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", events
        ));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<?> getEventById(@PathVariable Long eventId, HttpSession session) {
        Long orgId = (Long) session.getAttribute("ORG_ID");
        EventResponseDTO event = eventService.getEventById(eventId, orgId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", event
        ));
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<?> updateEvent(
            @PathVariable Long eventId,
            @RequestBody EventRequestDTO request,
            HttpSession session) {
        Long orgId = (Long) session.getAttribute("ORG_ID");
        EventResponseDTO response = eventService.updateEvent(eventId, orgId, request);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Event updated successfully",
                "data", response
        ));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long eventId, HttpSession session) {
        Long orgId = (Long) session.getAttribute("ORG_ID");
        eventService.deleteEvent(eventId, orgId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Event deleted successfully"
        ));
    }
}
