package com.sellspark.SellsHRMS.controller.api.payroll;

import com.sellspark.SellsHRMS.dto.payroll.SalarySlipDTO;
import com.sellspark.SellsHRMS.service.payroll.TaxComputationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payroll/tax-computation")
@RequiredArgsConstructor
public class TaxComputationRestController {

    private final TaxComputationService taxService;

    @GetMapping("/preview/{employeeId}")
    public ResponseEntity<?> previewTax(@PathVariable Long employeeId) {
        return ResponseEntity.ok(taxService.previewTaxComputation(employeeId));
    }

    @PostMapping("/recalculate/{employeeId}")
    public ResponseEntity<SalarySlipDTO> recalculate(@PathVariable Long employeeId,
                                                     @RequestParam Long payRunId) {
        return ResponseEntity.ok(taxService.recalculateTax(employeeId, payRunId));
    }

    @PostMapping("/validate-formula")
    public ResponseEntity<?> validateFormula(@RequestBody String formula) {
        return ResponseEntity.ok(taxService.validateFormula(formula));
    }
}
