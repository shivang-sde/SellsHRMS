package com.sellspark.SellsHRMS.controller.api;

import com.sellspark.SellsHRMS.dto.employee.EmployeeBankRequest;
import com.sellspark.SellsHRMS.dto.employee.EmployeeBankResponse;
import com.sellspark.SellsHRMS.service.EmployeeBankService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee/bank")
@RequiredArgsConstructor
@org.springframework.security.access.prepost.PreAuthorize("hasAnyAuthority('ORG_ADMIN', 'EMPLOYEE_VIEW_SELF', 'EMPLOYEE_VIEW_TEAM', 'EMPLOYEE_VIEW_ALL', 'EMPLOYEE_CREATE', 'EMPLOYEE_EDIT')")
public class EmployeeBankRestController {

    private final EmployeeBankService bankService;

    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyAuthority('ORG_ADMIN', 'EMPLOYEE_CREATE', 'EMPLOYEE_EDIT')")
    public ResponseEntity<EmployeeBankResponse> create(@RequestBody EmployeeBankRequest req) {
        return ResponseEntity.ok(bankService.create(req));
    }

    @GetMapping("/{employeeId}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyAuthority('ORG_ADMIN', 'EMPLOYEE_VIEW_SELF', 'EMPLOYEE_VIEW_TEAM', 'EMPLOYEE_VIEW_ALL')")
    public ResponseEntity<List<EmployeeBankResponse>> getByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(bankService.getByEmployee(employeeId));
    }

    @PutMapping("/{bankId}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyAuthority('ORG_ADMIN', 'EMPLOYEE_EDIT')")
    public ResponseEntity<EmployeeBankResponse> update(
            @PathVariable Long bankId,
            @RequestBody EmployeeBankRequest req) {

        return ResponseEntity.ok(bankService.update(bankId, req));
    }

    @DeleteMapping("/{bankId}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyAuthority('ORG_ADMIN', 'EMPLOYEE_EDIT')")
    public ResponseEntity<?> delete(@PathVariable Long bankId) {
        bankService.delete(bankId);
        return ResponseEntity.ok().build();
    }
}
