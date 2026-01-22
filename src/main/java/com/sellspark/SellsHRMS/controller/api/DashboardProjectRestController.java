package com.sellspark.SellsHRMS.controller.api;

import com.sellspark.SellsHRMS.dto.dashboard.MyWorkSummaryDTO;
import com.sellspark.SellsHRMS.service.DashboardProjectService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardProjectRestController {

    private final DashboardProjectService dashboardService;

    @GetMapping("/my-work")
    public ResponseEntity<MyWorkSummaryDTO> getMyDashboard(@RequestParam Long organisationId,
                                                           @RequestParam Long employeeId) {
        return ResponseEntity.ok(dashboardService.getEmployeeDashboard(organisationId, employeeId));
    }
}
