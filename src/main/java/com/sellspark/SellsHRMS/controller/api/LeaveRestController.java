package com.sellspark.SellsHRMS.controller.api;

import com.sellspark.SellsHRMS.dto.leave.*;
import com.sellspark.SellsHRMS.service.LeaveService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sellspark.SellsHRMS.dto.mapper.LeaveMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
@Slf4j
public class LeaveRestController {

    private final LeaveService leaveService;


    // ---------------------------------------------------------
    // 🧍 Employee Endpoints
    // ---------------------------------------------------------

    @PostMapping("/apply")
    public ResponseEntity<?> applyLeave(@RequestBody LeaveRequestDTO request, HttpSession session) {
        Long empId = (Long) session.getAttribute("EMP_ID");
        Long orgId = (Long) session.getAttribute("ORG_ID");

        LeaveResponseDTO response = leaveService.applyLeave(orgId, empId, request);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Leave applied successfully",
                "data", response
        ));
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyLeaves(HttpSession session) {
        Long empId = (Long) session.getAttribute("EMP_ID");
        Long orgId = (Long) session.getAttribute("ORG_ID");
        String ly = leaveService.getCurrentLeaveYear(orgId);

        log.info("Fetching leaves for Employee ID: {} for Leave Year: {}", empId, ly);
        List<LeaveResponseDTO> leaves = leaveService.getEmployeeLeaves(empId, ly);
        log.info("leaves {}", leaves.size());
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", leaves
        ));
    }
    

    @PatchMapping("/{id}/update")
    public ResponseEntity<?> updateLeave(
            @PathVariable Long id,
            @RequestBody LeaveRequestDTO request,
            HttpSession session) {

        Long empId = (Long) session.getAttribute("EMP_ID");
        Long orgId = (Long) session.getAttribute("ORG_ID");

        LeaveResponseDTO response = leaveService.updateLeave(id, request, empId,  orgId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Leave updated successfully",
                "data", response
        ));
    }   

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<?> cancelLeave(@PathVariable Long id, HttpSession session) {
        Long empId = (Long) session.getAttribute("EMP_ID");
        Long orgId = (Long) session.getAttribute("ORG_ID");

        leaveService.cancelLeave(id, empId, orgId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Leave cancelled successfully"
        ));
    }

    @GetMapping("/balances")
    public ResponseEntity<?> getMyBalances(HttpSession session) {
        Long empId = (Long) session.getAttribute("EMP_ID");
        long orgId = (Long) session.getAttribute("ORG_ID");
        String ly = leaveService.getCurrentLeaveYear(orgId);

        log.info("Fetching leave balances for Employee ID: {} for Leave Year: {}", empId, ly);
        var balances = leaveService.getEmployeeAllBalances(empId, ly);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", balances
        ));
    }

    // ---------------------------------------------------------
    // 🧑‍💼 Manager / Admin Endpoints
    // ---------------------------------------------------------

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingLeaves(HttpSession session) {
        Long orgId = (Long) session.getAttribute("ORG_ID");

        List<LeaveResponseDTO> leaves = leaveService.getPendingLeaves(orgId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", leaves
        ));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllLeaves(HttpSession session) {
        Long orgId = (Long) session.getAttribute("ORG_ID");
        List<LeaveResponseDTO> leaves = leaveService.getAllLeaves(orgId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", leaves
        ));
    }


    // Org Admin: Get org-level balances
@GetMapping("/org/{orgId}/balances")
public ResponseEntity<?> getOrgEmployeeLeaveBalances(@PathVariable Long orgId) {
    var balances = leaveService.getOrgEmployeeLeaveBalances(orgId);
    return ResponseEntity.ok(Map.of("success", true, "data", balances));
}
    

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveLeave(
            @PathVariable Long id,
            @RequestBody Map<String, String> req,
            HttpSession session) {

        Long approverId = req.getOrDefault("approverId", null) != null ?
                Long.parseLong(req.get("approverId")) :
                (Long) session.getAttribute("EMP_ID");   
        Long orgId = req.getOrDefault("orgId", null) != null ?
                Long.parseLong(req.get("orgId")) :
                (Long) session.getAttribute("ORG_ID");
        String remarks = req.getOrDefault("remarks", "");

        LeaveResponseDTO response = leaveService.approveLeave(id, approverId, remarks, orgId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Leave approved successfully",
                "data", response
        ));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectLeave(
            @PathVariable Long id,
            @RequestBody Map<String, String> req,
            HttpSession session) {

        Long approverId = req.getOrDefault("approverId", null) != null ?
                Long.parseLong(req.get("approverId")) :
                (Long) session.getAttribute("EMP_ID");
        Long orgId = req.getOrDefault("orgId", null) != null ?
                Long.parseLong(req.get("orgId")) :
                (Long) session.getAttribute("ORG_ID");
        String remarks = req.getOrDefault("remarks", "");

        LeaveRequestDTO response = leaveService.rejectLeave(id, approverId, remarks, orgId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Leave rejected successfully",
                "data", response
        ));
    }

    @GetMapping("/status")
    public ResponseEntity<?> getLeavesByStatus(
            @RequestParam String status,
            HttpSession session) {

        Long orgId = (Long) session.getAttribute("ORG_ID");
        List<LeaveResponseDTO> leaves = leaveService.getLeavesByStatus(orgId, status);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", leaves
        ));
    }

    @GetMapping("/range")
    public ResponseEntity<?> getLeavesBetweenDates(
            @RequestParam String from,
            @RequestParam String to,
            HttpSession session) {

        Long orgId = (Long) session.getAttribute("ORG_ID");
        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);

        List<LeaveResponseDTO> leaves = leaveService.getLeavesBetweenDates(orgId, fromDate, toDate);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", leaves
        ));
    }

    // ---------------------------------------------------------
    // 📊 Reports / Analytics
    // ---------------------------------------------------------

    @GetMapping("/stats")
    public ResponseEntity<?> getLeaveStats(HttpSession session) {
        Long orgId = (Long) session.getAttribute("ORG_ID");
        var stats = leaveService.getLeaveStatistics(orgId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", stats
        ));
    }

    @GetMapping("/my/stats")
    public ResponseEntity<?> getEmployeeLeaveStats(HttpSession session) {
        Long empId = (Long) session.getAttribute("EMP_ID");
        Long orgId = (Long) session.getAttribute("ORG_ID");
        String ly = leaveService.getCurrentLeaveYear(orgId);

        var stats = leaveService.getEmployeeLeaveStats(empId, ly );
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", stats
        ));
    }
}
