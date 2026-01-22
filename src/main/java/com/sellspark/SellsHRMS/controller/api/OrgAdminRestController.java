package com.sellspark.SellsHRMS.controller.api;

import com.sellspark.SellsHRMS.dto.employee.EmployeeCreateRequest;
import com.sellspark.SellsHRMS.dto.employee.EmployeeDetailResponse;
import com.sellspark.SellsHRMS.dto.employee.EmployeeResponse;
import com.sellspark.SellsHRMS.dto.admin.OrgAdminPatchRequest;
import com.sellspark.SellsHRMS.dto.admin.OrgAdminSummaryDTO;
import com.sellspark.SellsHRMS.service.EmployeeService;
import com.sellspark.SellsHRMS.service.OrganisationAdminService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/org-admin")
@RequiredArgsConstructor
public class OrgAdminRestController {

    private final EmployeeService employeeService;
    private final OrganisationAdminService orgAdminService;

    // ORG ADMIN PATCH UPDATE
    @PatchMapping("/{adminId}")
    public ResponseEntity<OrgAdminSummaryDTO> patchUpdateOrgAdmin(
            @PathVariable Long adminId,
            @RequestBody OrgAdminPatchRequest patchRequest) {

        OrgAdminSummaryDTO updated = orgAdminService.patchUpdate(adminId, patchRequest);
        return ResponseEntity.ok(updated);
    }

    // -------------------------------------------------------------
    // EMPLOYEE CRUD
    // -------------------------------------------------------------

    @PostMapping("/employee")
    public ResponseEntity<EmployeeResponse> createEmployee(@RequestBody EmployeeCreateRequest request) {
        return ResponseEntity.ok(employeeService.create(request));
    }

    @PutMapping("/employee/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long id,
            @RequestBody EmployeeCreateRequest request) {
        return ResponseEntity.ok(employeeService.update(id, request));
    }

    @GetMapping("/employee/{id}")
    public ResponseEntity<EmployeeDetailResponse> getEmployee(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getById(id));
    }

    @DeleteMapping("/employee/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        employeeService.softDelete(id); // <-- uses flag-based deletion
        return ResponseEntity.ok().build();
    }

    @GetMapping("/employees/{orgId}")
    public ResponseEntity<List<EmployeeResponse>> getEmployeesByOrg(@PathVariable Long orgId) {
        return ResponseEntity.ok(employeeService.getAll(orgId));
    }

    // update status: ACTIVE / SUSPENDED / TERMINATED
    @PatchMapping("/employee/{id}/status")
    public ResponseEntity<EmployeeResponse> updateEmployeeStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return ResponseEntity.ok(employeeService.updateStatus(id, status));
    }

    // DASHBOARD
    @GetMapping("/stats/employees/{orgId}")
    public ResponseEntity<Integer> getEmployeeCount(@PathVariable Long orgId) {
        return ResponseEntity.ok(orgAdminService.getEmployeeCount(orgId));
    }
}
