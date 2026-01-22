package com.sellspark.SellsHRMS.service.impl.payroll;

import com.sellspark.SellsHRMS.dto.payroll.SalarySlipDTO;
import com.sellspark.SellsHRMS.entity.Employee;
import com.sellspark.SellsHRMS.entity.payroll.EmployeeSalaryAssignment;
import com.sellspark.SellsHRMS.entity.payroll.SalarySlip;
import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
import com.sellspark.SellsHRMS.repository.EmployeeRepository;
import com.sellspark.SellsHRMS.repository.payroll.EmployeeSalaryAssignmentRepository;
import com.sellspark.SellsHRMS.repository.payroll.SalarySlipRepository;
import com.sellspark.SellsHRMS.service.payroll.TaxComputationEngineService;
import com.sellspark.SellsHRMS.service.payroll.TaxComputationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class TaxComputationServiceImpl implements TaxComputationService {

    private final TaxComputationEngineService taxEngine;
    private final EmployeeRepository employeeRepository;
    private final EmployeeSalaryAssignmentRepository assignmentRepository;
    private final SalarySlipRepository slipRepository;

    // ───────────────────────────────────────────────
    // 🟢 PREVIEW TAX COMPUTATION
    // ───────────────────────────────────────────────
    @Override
    public Object previewTaxComputation(Long employeeId) {
        Employee emp = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));

        EmployeeSalaryAssignment assignment = assignmentRepository.findByEmployeeIdAndActiveTrue(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Salary Assignment", "employeeId", employeeId));

        // Estimate current month gross (basic + variable)
        double gross = assignment.getBasePay() + (assignment.getVariablePay() != null ? assignment.getVariablePay() : 0.0);

        double tds = taxEngine.compute(emp, assignment, gross);

        // Return structured preview
        Map<String, Object> preview = new HashMap<>();
        preview.put("employeeId", employeeId);
        preview.put("employeeName", emp.getFirstName() + " " + emp.getLastName());
        preview.put("countryCode", assignment.getOrganisation().getCountryCode());
        preview.put("grossMonthly", gross);
        preview.put("estimatedAnnualGross", gross * 12);
        preview.put("estimatedTDS", tds);
        preview.put("netPayEstimate", gross - tds);

        return preview;
    }

    // ───────────────────────────────────────────────
    // 🟡 RECOMPUTE TAX FOR EXISTING PAYSLIP
    // ───────────────────────────────────────────────
    @Override
    public SalarySlipDTO recalculateTax(Long employeeId, Long payRunId) {
        SalarySlip slip = slipRepository.findByEmployee_IdAndPayRun_Id(employeeId, payRunId)
                .orElseThrow(() -> new ResourceNotFoundException("SalarySlip", "payRunId", payRunId));

        Employee emp = slip.getEmployee();
        EmployeeSalaryAssignment assignment = slip.getAssignment();

        double gross = slip.getGrossPay();
        double newTds = taxEngine.compute(emp, assignment, gross);

        slip.setTotalDeductions(slip.getTotalDeductions() + newTds);
        slip.setNetPay(slip.getGrossPay() - slip.getTotalDeductions());
        slipRepository.save(slip);

        // Convert to DTO for response
        SalarySlipDTO dto = new SalarySlipDTO();
        dto.setId(slip.getId());
        dto.setEmployeeId(employeeId);
        dto.setPayRunId(payRunId);
        dto.setGrossPay(slip.getGrossPay());
        dto.setTotalDeductions(slip.getTotalDeductions());
        dto.setNetPay(slip.getNetPay());
        dto.setFromDate(slip.getFromDate());
        dto.setToDate(slip.getToDate());

        return dto;
    }

    // ───────────────────────────────────────────────
    // 🧩 VALIDATE TAX FORMULA
    // ───────────────────────────────────────────────
    @Override
    public Object validateFormula(String formula) {
        Map<String, Object> response = new HashMap<>();
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");

        try {
            Object result = engine.eval(formula.replace("=", "==")); // basic syntax validation
            response.put("valid", true);
            response.put("message", "Formula syntax is valid.");
            response.put("result", result);
        } catch (ScriptException e) {
            response.put("valid", false);
            response.put("message", "Invalid formula syntax: " + e.getMessage());
        }
        return response;
    }
}
