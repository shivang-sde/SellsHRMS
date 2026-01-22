package com.sellspark.SellsHRMS.service.payroll;

import com.sellspark.SellsHRMS.dto.payroll.SalarySlipDTO;

public interface TaxComputationService {

    /**
     * Returns a tax computation preview for an employee.
     * Includes gross projection, exemptions, taxable income, and estimated TDS.
     */
    Object previewTaxComputation(Long employeeId);

    /**
     * Forces tax recomputation for an employee and specific pay run.
     * Returns updated SalarySlipDTO with recalculated TDS.
     */
    SalarySlipDTO recalculateTax(Long employeeId, Long payRunId);

    /**
     * Validates the syntax of a tax formula or condition string.
     * Used when admin defines custom conditions for rules.
     */
    Object validateFormula(String formula);
}
