package com.sellspark.SellsHRMS.controller.api.payroll;

import com.sellspark.SellsHRMS.dto.payroll.StatutoryComponentDTO;
import com.sellspark.SellsHRMS.dto.payroll.StatutoryRuleDTO;
import com.sellspark.SellsHRMS.service.payroll.StatutorySetupService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payroll/statutory")
@RequiredArgsConstructor
public class StatutoryRestController {

    private final StatutorySetupService service;

    // ───────────── COMPONENTS ─────────────
    @PostMapping("/components")
    public ResponseEntity<?> createComponent(@RequestBody StatutoryComponentDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.createComponent(dto));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error: " + ex.getMessage());
        }
    }

    @PutMapping("/components/{id}")
    public ResponseEntity<?> updateComponent(@PathVariable Long id, @RequestBody StatutoryComponentDTO dto) {
        try {
            return ResponseEntity.ok(service.updateComponent(id, dto));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error: " + ex.getMessage());
        }
    }

    @PatchMapping("/components/{id}/deactivate")
    public ResponseEntity<?> deactivateComponent(@PathVariable Long id) {
        service.deactivateComponent(id);
        return ResponseEntity.ok("Component deactivated successfully.");
    }

    // @GetMapping("/{orgId}/components/{countryCode}")
    // public ResponseEntity<List<StatutoryComponentDTO>> getByCountry(@PathVariable Long orgId, @PathVariable String countryCode) {
    //     return ResponseEntity.ok(service.getAllComponents(orgId, countryCode));
    // }

    @GetMapping("/components/id/{id}")
    public ResponseEntity<?> getComponent(@PathVariable Long id) {
        return ResponseEntity.ok(service.getComponent(id));
    }

    @GetMapping("/components/organisation/{orgId}")
    public ResponseEntity<?> getAllComponents(@PathVariable Long orgId) {
        return ResponseEntity.ok(service.getAllComponents(orgId));
    }

    // ───────────── RULES ─────────────
    @PostMapping("/components/{componentId}/rules")
    public ResponseEntity<?> createRule(@PathVariable Long componentId, @RequestBody StatutoryRuleDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.createRule(componentId, dto));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error: " + ex.getMessage());
        }
    }

    @PutMapping("/rules/{id}")
    public ResponseEntity<?> updateRule(@PathVariable Long id, @RequestBody StatutoryRuleDTO dto) {
        try {
            return ResponseEntity.ok(service.updateRule(id, dto));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error: " + ex.getMessage());
        }
    }

    @PatchMapping("/rules/{id}/deactivate")
    public ResponseEntity<?> deactivateRule(@PathVariable Long id) {
        service.deactivateRule(id);
        return ResponseEntity.ok("Rule deactivated successfully.");
    }

    @GetMapping("/components/{componentId}/rules")
    public ResponseEntity<List<StatutoryRuleDTO>> getRulesByComponent(@PathVariable Long componentId) {
        return ResponseEntity.ok(service.getRulesByComponent(componentId));
    }

    // @GetMapping("/{orgId}/rules/active")
    // public ResponseEntity<List<StatutoryRuleDTO>> getActiveRules(
    //         @PathVariable Long orgId,
    //         @RequestParam String countryCode,
    //         @RequestParam(required = false) String stateCode) {
    //     return ResponseEntity.ok(service.getActiveRules(orgId, countryCode, stateCode));
    // }
}
