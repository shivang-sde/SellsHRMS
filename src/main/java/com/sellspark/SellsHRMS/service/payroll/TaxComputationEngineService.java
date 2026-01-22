package com.sellspark.SellsHRMS.service.payroll;

import com.sellspark.SellsHRMS.entity.Employee;
import com.sellspark.SellsHRMS.entity.payroll.EmployeeSalaryAssignment;

public interface TaxComputationEngineService {

    /**
     * Calculates the applicable tax deduction (TDS) for an employee.
     * @param employee Employee details
     * @param assignment Active salary assignment
     * @param grossMonthly Current month’s gross income
     * @return Monthly TDS deduction amount
     */
    double compute(Employee employee, EmployeeSalaryAssignment assignment, double grossMonthly);
}