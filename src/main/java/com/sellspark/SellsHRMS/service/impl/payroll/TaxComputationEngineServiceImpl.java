package com.sellspark.SellsHRMS.service.impl.payroll;


import com.sellspark.SellsHRMS.entity.Employee;
import com.sellspark.SellsHRMS.entity.payroll.EmployeeSalaryAssignment;
import com.sellspark.SellsHRMS.entity.payroll.IncomeTaxRule;
import com.sellspark.SellsHRMS.entity.payroll.IncomeTaxSlab;
import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
import com.sellspark.SellsHRMS.repository.payroll.IncomeTaxRuleRepository;
import com.sellspark.SellsHRMS.repository.payroll.IncomeTaxSlabRepository;
import com.sellspark.SellsHRMS.service.payroll.TaxComputationEngineService;
import com.sellspark.SellsHRMS.utils.FormulaExpressionEvaluator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaxComputationEngineServiceImpl implements TaxComputationEngineService {

    private final IncomeTaxSlabRepository taxSlabRepository;
    private final IncomeTaxRuleRepository taxRuleRepository;

    @Override
    public double compute(Employee employee, EmployeeSalaryAssignment assignment, double grossMonthly) {

         if (assignment == null || assignment.getTaxSlab() == null) {
            log.warn("⚠️ No tax slab assigned for employee {}", employee.getId());
            return 0.0;
        }

        IncomeTaxSlab slab = taxSlabRepository.findById(assignment.getTaxSlab().getId()).orElseThrow(() -> new ResourceNotFoundException("Tax Slab", "Id", assignment.getTaxSlab().getId()));
           // dentify applicable tax slab for employee country/org
        String countryCode = assignment.getOrganisation().getCountryCode();
        int remainingMonths = getRemainingMonths(countryCode);
        double annualGross = grossMonthly * remainingMonths;
        
        
        // Apply standard exemption
        double exemption = Optional.ofNullable(slab.getStandardExemptionLimit()).orElse(0.0);
        double taxableIncome = Math.max(0, annualGross - exemption);


         // Apply provisional declared exemptions (future extension)
        double declaredExemptions = getProvisionalDeclarations(employee, taxableIncome);
        taxableIncome = Math.max(0, taxableIncome - declaredExemptions);

        List<IncomeTaxRule> rules = taxRuleRepository.findByTaxSlabId(slab.getId());
        if (rules.isEmpty()) {
            log.warn("⚠️ No tax rules found for slab {}", slab.getId());
            return 0.0;
        }

        // Sort ascending by income range for deterministic calculation
        rules.sort(Comparator.comparing(IncomeTaxRule::getMinIncome));

        // Compute tax progressively
        double annualTax = computeTaxFromRules(rules, taxableIncome, countryCode);

        // Monthly TDS (spread over remaining months)
        double monthlyTDS = annualTax / remainingMonths;
        log.info("💰 [{}] AnnualGross={} | Taxable={} | AnnualTax={} | MonthlyTDS={}",
                employee.getEmployeeCode(), annualGross, taxableIncome, annualTax, monthlyTDS);

        return roundToTwoDecimals(monthlyTDS);
    }

    // ───────────────────────────────────────────────
    // Progressive slab-based tax calculation
    // ───────────────────────────────────────────────
    private double computeTaxFromRules(List<IncomeTaxRule> rules, double income, String countryCode) {
        double tax = 0.0;

        for (IncomeTaxRule rule : rules) {
            // Optional condition support (like COUNTRY == 'IN' && income > X)
            if (rule.getCondition() != null && !rule.getCondition().isBlank()) {
                Map<String, Object> ctx = Map.of("INCOME", income, "COUNTRY", countryCode);
                if (!FormulaExpressionEvaluator.evaluateCondition(rule.getCondition(), ctx)) {
                    continue;
                }
            }

            double min = Optional.ofNullable(rule.getMinIncome()).orElse(0.0);
            double max = Optional.ofNullable(rule.getMaxIncome()).orElse(Double.MAX_VALUE);

            if (income > min) {
                double taxablePortion = Math.min(income, max) - min;
                if (taxablePortion > 0) {
                    double taxRate = Optional.ofNullable(rule.getDeductionPercent()).orElse(0.0);
                    double portionTax = taxablePortion * (taxRate / 100.0);
                    tax += portionTax;

                    log.debug("🧮 Rule: {} - {} @ {}% = {}",
                            min, max == Double.MAX_VALUE ? "∞" : max, taxRate, portionTax);
                }
            }
        }
        return tax;
    }

    // ───────────────────────────────────────────────
    // Placeholder — phase 2: fetch from EmployeeTaxDeclaration
    // ───────────────────────────────────────────────
    private double getProvisionalDeclarations(Employee employee, double taxableIncome) {
        try {
            if ("IN".equalsIgnoreCase(employee.getOrganisation().getCountryCode())) {
                // Simple assumption: 10% declared up to ₹1.5L
                return Math.min(150000.0, taxableIncome * 0.1);
            }
        } catch (Exception e) {
            log.warn("Unable to compute provisional declarations for emp {}: {}", employee.getId(), e.getMessage());
        }
        return 0.0;
    }

    // ───────────────────────────────────────────────
    // Remaining months in financial year
    // ───────────────────────────────────────────────
    private int getRemainingMonths(String countryCode) {
        LocalDate today = LocalDate.now();
        Month current = today.getMonth();

        // India FY = Apr–Mar
        if ("IN".equalsIgnoreCase(countryCode)) {
            int currentMonth = current.getValue();
            if (currentMonth >= 4) {
                return 12 - (currentMonth - 4); // e.g., May = 11 remaining
            } else {
                return 3 - (currentMonth - 1); // Jan–Mar
            }
        }

        // Default: calendar year
        return 12 - current.getValue() + 1;
    }

    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}