package com.sellspark.SellsHRMS.controller.api.payroll;

import com.sellspark.SellsHRMS.dto.payroll.SalaryComponentDTO;
import com.sellspark.SellsHRMS.service.payroll.SalaryComponentService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payroll/salary-components")
@RequiredArgsConstructor
public class SalaryComponentRestController {

    private final SalaryComponentService service;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody SalaryComponentDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.createComponent(dto));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody SalaryComponentDTO dto) {
        try {
            return ResponseEntity.ok(service.updateComponent(id, dto));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.getComponent(id));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + ex.getMessage());
        }
    }

    @GetMapping("/organisation/{orgId}")
    public ResponseEntity<List<SalaryComponentDTO>> getActiveByOrg(@PathVariable Long orgId) {
        return ResponseEntity.ok(service.getActiveComponents(orgId));
    }

    // @GetMapping("/country/{countryCode}")
    // public ResponseEntity<List<SalaryComponentDTO>> getByCountry(@PathVariable String countryCode) {
    //     return ResponseEntity.ok(service.getComponentsByCountry(countryCode));
    // }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable Long id) {
        try {
            service.deactivateComponent(id);
            return ResponseEntity.ok("Component deactivated successfully.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + ex.getMessage());
        }
    }
}
