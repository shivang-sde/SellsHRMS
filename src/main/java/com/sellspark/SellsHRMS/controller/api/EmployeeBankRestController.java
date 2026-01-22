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
public class EmployeeBankRestController {

    private final EmployeeBankService bankService;

    @PostMapping
    public ResponseEntity<EmployeeBankResponse> create(@RequestBody EmployeeBankRequest req) {
        return ResponseEntity.ok(bankService.create(req));
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<List<EmployeeBankResponse>> getByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(bankService.getByEmployee(employeeId));
    }

    @PutMapping("/{bankId}")
    public ResponseEntity<EmployeeBankResponse> update(
            @PathVariable Long bankId,
            @RequestBody EmployeeBankRequest req) {

        return ResponseEntity.ok(bankService.update(bankId, req));
    }

    @DeleteMapping("/{bankId}")
    public ResponseEntity<?> delete(@PathVariable Long bankId) {
        bankService.delete(bankId);
        return ResponseEntity.ok().build();
    }
}
