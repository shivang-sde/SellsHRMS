package com.sellspark.SellsHRMS.controller.api.payroll;

import com.sellspark.SellsHRMS.dto.payroll.StatutoryComponentMappingDTO;
import com.sellspark.SellsHRMS.service.payroll.StatutoryComponentMappingService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payroll/statutory-mappings")
@RequiredArgsConstructor
public class StatutoryComponentMappingRestController {

    private final StatutoryComponentMappingService mappingService;

    // ----------------------------------------------------
    // 🟢 CREATE NEW MAPPING
    // ----------------------------------------------------
    @PostMapping
    public ResponseEntity<?> createMapping(@RequestBody StatutoryComponentMappingDTO dto) {
        try {
            StatutoryComponentMappingDTO created = mappingService.createMapping(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error creating mapping: " + ex.getMessage());

        }
    }

    // ----------------------------------------------------
    // 🟢 UPDATE MAPPING
    // ----------------------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMapping(@PathVariable Long id,
                                           @RequestBody StatutoryComponentMappingDTO dto) {
        try {
            StatutoryComponentMappingDTO updated = mappingService.updateMapping(id, dto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating mapping: " + ex.getMessage());
        }
    }

    // ----------------------------------------------------
    // 🟢 GET MAPPING BY ID
    // ----------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<?> getMappingById(@PathVariable Long id) {
        try {
            StatutoryComponentMappingDTO dto = mappingService.getMappingById(id);
            return ResponseEntity.ok(dto);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Mapping not found: " + ex.getMessage());
        }
    }

    // ----------------------------------------------------
    // 🟢 GET ALL MAPPINGS BY ORGANISATION
    // ----------------------------------------------------
    @GetMapping("/organisation/{orgId}")
    public ResponseEntity<?> getMappingsByOrganisation(@PathVariable Long orgId) {
        List<StatutoryComponentMappingDTO> list = mappingService.getMappingsByOrganisation(orgId);
        return ResponseEntity.ok(list);
    }

    // ----------------------------------------------------
    // 🟢 GET ALL MAPPINGS FOR SPECIFIC STATUTORY COMPONENT
    // ----------------------------------------------------
    @GetMapping("/organisation/{orgId}/component/{statutoryComponentId}")
    public ResponseEntity<?> getMappingsByStatutoryComponent(@PathVariable Long orgId,
                                                             @PathVariable Long statutoryComponentId) {
        List<StatutoryComponentMappingDTO> list =
                mappingService.getMappingsByStatutoryComponent(orgId, statutoryComponentId);
        return ResponseEntity.ok(list);
    }

    // ----------------------------------------------------
    // 🟡 DEACTIVATE MAPPING (SOFT DELETE)
    // ----------------------------------------------------
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateMapping(@PathVariable Long id) {
        try {
            mappingService.deactivateMapping(id);
            return ResponseEntity.ok("Mapping deactivated successfully.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error deactivating mapping: " + ex.getMessage());
        }
    }
}
