package com.sellspark.SellsHRMS.controller.api;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sellspark.SellsHRMS.dto.dashboard.DashboardDataDTO;
import com.sellspark.SellsHRMS.service.AnnouncementService;
import com.sellspark.SellsHRMS.service.EmployeeService;
import com.sellspark.SellsHRMS.service.EventService;
import com.sellspark.SellsHRMS.service.HolidayService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
public class OrgDashboardRestController {

    private final EmployeeService employeeService;
    private final EventService eventService;
    private final AnnouncementService announcementService;
    private final HolidayService holidayService;  // if not present, we’ll create one

    @GetMapping("/org/{orgId}")
    public ResponseEntity<?> getDashboardData(@PathVariable Long orgId) {
       
        if (orgId == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Organisation not found in session"));
        }

        log.info("Fetching dashboard data for organisation {}", orgId);
        LocalDate today = LocalDate.now();
        LocalDate weekEnd = today.plusDays(7);

        DashboardDataDTO dashboard = DashboardDataDTO.builder()
                .birthdays(employeeService.findUpcomingBirthdays(orgId, today, weekEnd))
                .anniversaries(employeeService.findUpcomingWorkAnniversaries(orgId, today, weekEnd))
                .holidays(holidayService.getUpcomingHolidays(orgId, today, weekEnd))
                .events(eventService.getUpcomingEvents(orgId))
                .announcements(announcementService.getActiveAnnouncements(orgId))
                .build();

        log.info("dash...", dashboard);
        

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", dashboard
        ));
    }
}