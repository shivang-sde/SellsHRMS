package com.sellspark.SellsHRMS.controller.api;

import com.sellspark.SellsHRMS.dto.employee.EmployeeCreateRequest;
import com.sellspark.SellsHRMS.dto.employee.EmployeeDetailResponse;
import com.sellspark.SellsHRMS.dto.employee.EmployeeResponse;
import com.sellspark.SellsHRMS.payload.ApiResponse;
import com.sellspark.SellsHRMS.service.EmployeeService;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@AllArgsConstructor
public class EmployeeRestController {

    private final EmployeeService service;

    @PreAuthorize("hasAnyAuthority('EMPLOYEE_CREATE', 'ORG_ADMIN')")
    @PostMapping
    public EmployeeResponse create(@RequestBody EmployeeCreateRequest request) {
        return service.create(request);
    }

    @PreAuthorize("hasAnyAuthority('EMPLOYEE_EDIT', 'ORG_ADMIN')")
    @PutMapping("/{id}")
    public EmployeeResponse update(@PathVariable Long id, @RequestBody EmployeeCreateRequest request) {
        return service.update(id, request);
    }

    @PreAuthorize("hasAnyAuthority('ORG_ADMIN', 'EMPLOYEE_VIEW_ALL')")
    @GetMapping("/org/{orgId}")
    public List<EmployeeResponse> getAll(@PathVariable Long orgId) {
        return service.getAll(orgId);
    }

    /**
     * Permission-aware employee list endpoint.
     * - ORG_ADMIN / EMPLOYEE_VIEW_ALL → all employees in org
     * - EMPLOYEE_VIEW_TEAM → only subordinates (reporting hierarchy)
     */
    @PreAuthorize("hasAnyAuthority('ORG_ADMIN', 'EMPLOYEE_VIEW_ALL', 'EMPLOYEE_VIEW_TEAM')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getByPermission(
            HttpSession session, Authentication authentication) {

        Long orgId = (Long) session.getAttribute("ORG_ID");
        Long empId = (Long) session.getAttribute("EMP_ID");

        boolean isAdmin = hasAuthority(authentication, "ORG_ADMIN");
        boolean canViewAll = hasAuthority(authentication, "EMPLOYEE_VIEW_ALL");
        boolean canViewTeam = hasAuthority(authentication, "EMPLOYEE_VIEW_TEAM");

        List<EmployeeResponse> employees;

        if (isAdmin || canViewAll) {
            employees = service.getAll(orgId);
        } else if (canViewTeam && empId != null) {
            employees = service.getSubordinates(empId, orgId);
        } else {
            employees = List.of();
        }

        return ResponseEntity.ok(ApiResponse.ok("Employees fetched", employees));
    }

    private boolean hasAuthority(Authentication auth, String authority) {
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals(authority));
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
    // @PreAuthorize("hasAnyAuthority('EMPLOYEE_VIEW_SELF', 'EMPLOYEE_VIEW_ALL')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id, HttpSession session, Authentication authentication) {
        Long currentEmpId = (Long) session.getAttribute("EMP_ID");
        Long orgId = (Long) session.getAttribute("ORG_ID");

        boolean isOrgAdmin = hasAuthority(authentication, "ORG_ADMIN");
        boolean canViewAll = hasAuthority(authentication, "EMPLOYEE_VIEW_ALL");
        boolean canViewTeam = hasAuthority(authentication, "EMPLOYEE_VIEW_TEAM");

        // 1. ORG_ADMIN or EMPLOYEE_VIEW_ALL can view any employee in the same
        // organisation
        if (isOrgAdmin || canViewAll) {
            EmployeeDetailResponse emp = service.getByIdAndOrg(id, orgId);
            if (emp == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found in your organisation");
            }
            return ResponseEntity.ok(emp);
        }

        // 2. EMPLOYEE_VIEW_TEAM can view subordinates
        if (canViewTeam && currentEmpId != null) {
            if (service.isSubordinate(currentEmpId, id)) {
                return ResponseEntity.ok(service.getById(id));
            }
        }

        // 4. Default to Forbidden if no permission matches
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("You do not have permission to view this employee record");
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
