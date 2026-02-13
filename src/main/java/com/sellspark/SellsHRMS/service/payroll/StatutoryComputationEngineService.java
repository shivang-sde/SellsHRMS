package com.sellspark.SellsHRMS.service.payroll;

import com.sellspark.SellsHRMS.entity.Employee;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.payroll.EmployeeSalaryAssignment;
import com.sellspark.SellsHRMS.entity.payroll.SalaryComponent;
import com.sellspark.SellsHRMS.service.impl.payroll.StatutoryComputationEngineImpl.StatutoryResult;

import java.util.Map;

public interface StatutoryComputationEngineService {
    /**
     * Compute all statutory deductions for an employee during payroll.
     * 
     * @param assignment      EmployeeSalaryAssignment data
     * @param componentValues Computed salary component values (e.g., BASIC →
     *                        20,000, HRA → 5,000)
     * @param organisation    Organisation context
     * @return Map of statutory code → deduction amount (employee + employer)
     */
    Map<String, StatutoryResult> computeDetailed(EmployeeSalaryAssignment assignment,
            Map<SalaryComponent, Double> componentValues,
            Organisation organisation);
}
