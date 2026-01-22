package com.sellspark.SellsHRMS.controller.api;

import com.sellspark.SellsHRMS.dto.organisation.DepartmentDTO;
import com.sellspark.SellsHRMS.service.DepartmentService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentRestController {

    private final DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<DepartmentDTO> create(@RequestBody DepartmentDTO dto) {
        DepartmentDTO created = departmentService.createDepartment(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDTO> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }

    @GetMapping("/org/{orgId}")
    public ResponseEntity<List<DepartmentDTO>> getAll(@PathVariable Long orgId) {
        return ResponseEntity.ok(departmentService.getAllDepartmentsByOrgId(orgId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DepartmentDTO> patch(@PathVariable Long id, @RequestBody DepartmentDTO dto) {
        return ResponseEntity.ok(departmentService.patchUpdateDepartment(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }
}
