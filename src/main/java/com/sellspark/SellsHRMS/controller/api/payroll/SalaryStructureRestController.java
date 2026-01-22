package com.sellspark.SellsHRMS.controller.api.payroll;
import com.sellspark.SellsHRMS.dto.payroll.SalaryStructureDTO;
import com.sellspark.SellsHRMS.service.payroll.SalaryStructureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/payroll/salary-structures")
@RequiredArgsConstructor
public class SalaryStructureRestController {

    private final SalaryStructureService service;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody SalaryStructureDTO dto) {
        try {
            log.info("sakry struct dto, {} ", dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(service.createStructure(dto));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody SalaryStructureDTO dto) {
        try {
            return ResponseEntity.ok(service.updateStructure(id, dto));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.getStructure(id));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + ex.getMessage());
        }
    }

    @GetMapping("/organisation/{orgId}")
    public ResponseEntity<List<SalaryStructureDTO>> getAll(@PathVariable Long orgId) {
        return ResponseEntity.ok(service.getAllStructures(orgId));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable Long id) {
        try {
            log.info("deactivating struc id {}", id);
            service.deactivateStructure(id);
            return ResponseEntity.ok("Salary structure deactivated successfully.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + ex.getMessage());
        }
    }

    @PostMapping("/{id}/components")
    public ResponseEntity<?> assignComponents(@PathVariable Long id, @RequestBody List<Long> componentIds) {
        try {
            return ResponseEntity.ok(service.assignComponents(id, componentIds));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + ex.getMessage());
        }
    }

    @PostMapping("/{id}/clone")
    public ResponseEntity<?> cloneStructure(@PathVariable Long id, @RequestParam String newName) {
        try {
            return ResponseEntity.ok(service.cloneStructure(id, newName));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + ex.getMessage());
        }
    }
}
