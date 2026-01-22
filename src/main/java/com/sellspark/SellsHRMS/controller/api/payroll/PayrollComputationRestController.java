package com.sellspark.SellsHRMS.controller.api.payroll;

import com.sellspark.SellsHRMS.dto.payroll.PayrollComputationRequest;
import com.sellspark.SellsHRMS.dto.payroll.SalarySlipDTO;
import com.sellspark.SellsHRMS.service.payroll.PayrollCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payroll/compute")
@RequiredArgsConstructor
public class PayrollComputationRestController {

    private final PayrollCalculationService payrollService;

    @PostMapping("/employee/{employeeId}")
    public ResponseEntity<SalarySlipDTO> computeSingle(@PathVariable Long employeeId,
                                                       @RequestBody(required = false) PayrollComputationRequest request) {
        if (request == null) {
            request = new PayrollComputationRequest();
            request.setEmployeeId(employeeId);
        }
        return ResponseEntity.ok(payrollService.computeSingleEmployee(request));
    }

    @PostMapping("/recalculate/{employeeId}")
    public ResponseEntity<SalarySlipDTO> recalculateDeductions(@PathVariable Long employeeId,
                                                               @RequestParam Long payRunId) {
        return ResponseEntity.ok(payrollService.recalculateDeductions(employeeId, payRunId));
    }
}
