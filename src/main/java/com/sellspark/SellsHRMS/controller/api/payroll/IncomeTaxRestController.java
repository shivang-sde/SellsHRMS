
package com.sellspark.SellsHRMS.controller.api.payroll;

import com.sellspark.SellsHRMS.dto.payroll.IncomeTaxSlabDTO;
import com.sellspark.SellsHRMS.dto.payroll.IncomeTaxRuleDTO;
import com.sellspark.SellsHRMS.service.payroll.IncomeTaxSetupService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payroll/tax")
@RequiredArgsConstructor
public class IncomeTaxRestController {

    private final IncomeTaxSetupService service;

    // ─────────────── SLABS ───────────────
    @PostMapping("/slabs")
    public ResponseEntity<?> createSlab(@RequestBody IncomeTaxSlabDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.createTaxSlab(dto));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error: " + ex.getMessage());
        }
    }

    @PutMapping("/slabs/{id}")
    public ResponseEntity<?> updateSlab(@PathVariable Long id, @RequestBody IncomeTaxSlabDTO dto) {
        try {
            return ResponseEntity.ok(service.updateTaxSlab(id, dto));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error: " + ex.getMessage());
        }
    }

    @PatchMapping("/slabs/{id}/deactivate")
    public ResponseEntity<?> deactivateSlab(@PathVariable Long id) {
        service.deactivateTaxSlab(id);
        return ResponseEntity.ok("Tax slab deactivated successfully.");
    }

    @GetMapping("/slabs/organisation/{orgId}")
    public ResponseEntity<List<IncomeTaxSlabDTO>> getActiveSlabs(@PathVariable Long orgId) {
        return ResponseEntity.ok(service.getActiveSlabsOfOrg(orgId));
    } 

    @GetMapping("/slabs/{id}")
    public ResponseEntity<?> getSlab(@PathVariable Long id) {
        return ResponseEntity.ok(service.getTaxSlab(id));
    }

    // ─────────────── RULES ───────────────
    @PostMapping("/slabs/{slabId}/rules")
    public ResponseEntity<?> createRule(@PathVariable Long slabId, @RequestBody IncomeTaxRuleDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.createTaxRule(slabId, dto));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error: " + ex.getMessage());
        }
    }

    @PutMapping("/rules/{id}")
    public ResponseEntity<?> updateRule(@PathVariable Long id, @RequestBody IncomeTaxRuleDTO dto) {
        try {
            return ResponseEntity.ok(service.updateTaxRule(id, dto));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error: " + ex.getMessage());
        }
    }

    @DeleteMapping("/rules/{id}")
    public ResponseEntity<?> deleteRule(@PathVariable Long id) {
        try {
            service.deactivateTaxRule(id);
            return ResponseEntity.ok("Tax rule deleted successfully.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + ex.getMessage());
        }
    }

    @GetMapping("/slabs/{slabId}/rules")
    public ResponseEntity<List<IncomeTaxRuleDTO>> getRulesBySlab(@PathVariable Long slabId) {
        return ResponseEntity.ok(service.getRulesBySlab(slabId));
    }
}
