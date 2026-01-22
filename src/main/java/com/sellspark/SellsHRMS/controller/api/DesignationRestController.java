package com.sellspark.SellsHRMS.controller.api;

import com.sellspark.SellsHRMS.dto.organisation.DesignationDTO;
import com.sellspark.SellsHRMS.service.DesignationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/designations")
@RequiredArgsConstructor
public class DesignationRestController {

    private final DesignationService designationService;

    @PostMapping
    public ResponseEntity<DesignationDTO> create(@RequestBody DesignationDTO dto) {
        DesignationDTO created = designationService.createDesignation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/org/{orgId}")
    public ResponseEntity<List<DesignationDTO>> getAll(@PathVariable Long orgId) {
        return ResponseEntity.ok(designationService.getAllDesignationsByOrgId(orgId));
    }

    @GetMapping("/department/{deptId}")
    public ResponseEntity<List<DesignationDTO>> getByDepartment(@PathVariable Long deptId) {
    return ResponseEntity.ok(designationService.getDesignationsByDeptId(deptId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DesignationDTO> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(designationService.getDesignationById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DesignationDTO> patch(@PathVariable Long id, @RequestBody DesignationDTO dto) {
        return ResponseEntity.ok(designationService.patchUpdateDesignation(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        designationService.deleteDesignation(id);
        return ResponseEntity.noContent().build();
    }
}
