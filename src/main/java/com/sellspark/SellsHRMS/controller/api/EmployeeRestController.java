package com.sellspark.SellsHRMS.controller.api;

import com.sellspark.SellsHRMS.dto.employee.EmployeeCreateRequest;
import com.sellspark.SellsHRMS.dto.employee.EmployeeDetailResponse;
import com.sellspark.SellsHRMS.dto.employee.EmployeeResponse;
import com.sellspark.SellsHRMS.payload.ApiResponse;
import com.sellspark.SellsHRMS.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeRestController {

    private final EmployeeService service;

    @PreAuthorize("hasAuthority('EMPLOYEE_CREATE')")
    @PostMapping
    public EmployeeResponse create(@RequestBody EmployeeCreateRequest request) {
        return service.create(request);
    }

    @PreAuthorize("hasAuthority('EMPLOYEE_EDIT')")
    @PutMapping("/{id}")
    public EmployeeResponse update(@PathVariable Long id, @RequestBody EmployeeCreateRequest request) {
        return service.update(id, request);
    }

    @PreAuthorize("hasAuthority('EMPLOYEE_VIEW_ALL')")
    @GetMapping("/org/{orgId}")
    public List<EmployeeResponse> getAll(@PathVariable Long orgId) {
        return service.getAll(orgId);
    }

    @GetMapping("/subordinates")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getSubordinates(
        @RequestParam Long managerId,
        @RequestParam Long organisationId) {

    List<EmployeeResponse> subordinates = service.getSubordinates(managerId, organisationId);
    return ResponseEntity.ok(ApiResponse.ok("Subordinates fetched successfully", subordinates));
}


    /**
     * Get employee details by ID with role-based visibility.
     * - Employee → can see their own record
     * - Org Admin → can see any employee in their org
     * - Super Admin → can see anyone
     */
    @PreAuthorize("hasAnyAuthority('EMPLOYEE_VIEW_SELF', 'EMPLOYEE_VIEW_ALL')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id, HttpSession session) {
        String role = (String) session.getAttribute("SYSTEM_ROLE");
        Long empId = (Long) session.getAttribute("EMP_ID");
        Long orgId = (Long) session.getAttribute("ORG_ID");

        if ("EMPLOYEE".equals(role)) {
            if (empId == null || !empId.equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You are not allowed to access this employee record");
            }
            return ResponseEntity.ok(service.getById(id));
        }


        if ("ORG_ADMIN".equals(role)) {
            EmployeeDetailResponse emp = service.getByIdAndOrg(id, orgId);
            if (emp == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Employee not found in your organisation");
            }
            return ResponseEntity.ok(emp);
        }

        // Super Admin or other roles — unrestricted
        return ResponseEntity.ok(service.getById(id));
    }

    @PreAuthorize("hasAuthority('EMPLOYEE_DELETE')")
    @DeleteMapping("/{id}")
    public void softDelete(@PathVariable Long id) {
        service.softDelete(id);
    }

    @PatchMapping("/{id}/status")
    public EmployeeResponse updateStatus(@PathVariable Long id, @RequestParam String status) {
        return service.updateStatus(id, status);
    }
}
