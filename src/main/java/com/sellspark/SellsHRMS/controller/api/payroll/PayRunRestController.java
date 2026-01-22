package com.sellspark.SellsHRMS.controller.api.payroll;

import com.sellspark.SellsHRMS.dto.payroll.PayRunDTO;
import com.sellspark.SellsHRMS.dto.payroll.PayRunDetailDTO;
import com.sellspark.SellsHRMS.dto.payroll.PayRunRequestDTO;
import com.sellspark.SellsHRMS.dto.payroll.SalarySlipDTO;
import com.sellspark.SellsHRMS.entity.payroll.PayRun;
import com.sellspark.SellsHRMS.service.payroll.PayRunService;
import com.sellspark.SellsHRMS.service.payroll.PayrollCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/payroll/payruns")
@RequiredArgsConstructor
public class PayRunRestController {

    private final PayRunService payRunService;
    private final PayrollCalculationService payrollCalculationService;

    @PostMapping
    public ResponseEntity<PayRunDTO> create(@RequestBody PayRunRequestDTO request) {

    return ResponseEntity.ok(
        payRunService.createPayRun(request)
    );
}


  
    @GetMapping("/{id}/slips")
    public ResponseEntity<PayRunDetailDTO> getPayRunDetails(@PathVariable Long id) {
        return ResponseEntity.ok(payRunService.getPayRunDetails(id));
    }

  
    @PostMapping("/{payRunId}/process")
    public ResponseEntity<List<SalarySlipDTO>> process(@PathVariable Long payRunId) {
        PayRunDTO payRun = payRunService.getPayRun(payRunId);
        PayRun entity = new PayRun();
        entity.setId(payRunId);
        entity.setStartDate(payRun.getStartDate());
        entity.setEndDate(payRun.getEndDate());
        List<SalarySlipDTO> slips = payrollCalculationService.runPayroll(payRun.getOrganisationId(), entity);
        return ResponseEntity.ok(slips);
    }

    // ✅ Get PayRuns by Organisation
    @GetMapping("/organisation/{orgId}")
    public ResponseEntity<List<PayRunDTO>> getByOrg(@PathVariable Long orgId) {
        return ResponseEntity.ok(payRunService.getPayRuns(orgId));
    }
}
