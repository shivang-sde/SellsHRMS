package com.sellspark.SellsHRMS.controller.api.payroll;

import com.sellspark.SellsHRMS.dto.payroll.EmployeeSalaryAssignmentDTO;
import com.sellspark.SellsHRMS.service.payroll.EmployeeSalaryAssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/payroll/assignments")
@RequiredArgsConstructor
public class EmployeeSalaryAssignmentRestController {

    private final EmployeeSalaryAssignmentService service;

    @PostMapping
    public ResponseEntity<?> assign(@RequestBody EmployeeSalaryAssignmentDTO dto) {
        log.info("assignning salary struc");
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.assignSalaryStructure(dto));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody EmployeeSalaryAssignmentDTO dto) {
        try {
            return ResponseEntity.ok(service.updateAssignment(id, dto));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.getAssignment(id));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + ex.getMessage());
        }
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<EmployeeSalaryAssignmentDTO> getByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(service.getAssignmentsByEmployee(employeeId));
    }

    @GetMapping("/organisation/{orgId}")
    public ResponseEntity<List<EmployeeSalaryAssignmentDTO>> getActiveByOrg(@PathVariable Long orgId) {
        return ResponseEntity.ok(service.getActiveAssignments(orgId));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable Long id) {
        try {
            service.deactivateAssignment(id);
            return ResponseEntity.ok("Assignment deactivated successfully.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + ex.getMessage());
        }
    }
}
