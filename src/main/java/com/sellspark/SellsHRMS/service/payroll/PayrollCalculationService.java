package com.sellspark.SellsHRMS.service.payroll;

import com.sellspark.SellsHRMS.dto.payroll.PayrollComputationRequest;
import com.sellspark.SellsHRMS.dto.payroll.SalarySlipDTO;
import com.sellspark.SellsHRMS.entity.payroll.PayRun;
import com.sellspark.SellsHRMS.entity.payroll.SalaryComponent;

import java.util.List;
import java.util.Map;

public interface PayrollCalculationService {

    /**
     * Executes payroll computation for all active employees in an organisation.
     */
    List<SalarySlipDTO> runPayroll(Long organisationId, PayRun payRun);

    /**
     * Compute salary for a single employee (preview or dry run).
     */
    SalarySlipDTO computeSingleEmployee(PayrollComputationRequest request);

    /**
     * Calculate gross, deduction, and net totals.
     */
    SalarySlipDTO calculateTotals(SalarySlipDTO slipDTO);

    /**
     * Recompute tax or statutory deductions for updated employee data.
     */
    SalarySlipDTO recalculateDeductions(Long employeeId, Long payRunId);

    /**
     * Compute amount for a single salary component based on formula and context.
     */
    double computeComponentAmount(SalaryComponent comp, Map<String, Object> context);
}
