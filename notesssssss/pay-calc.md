package com.sellspark.SellsHRMS.service.impl.payroll;

import com.sellspark.SellsHRMS.entity.Employee;
import com.sellspark.SellsHRMS.entity.payroll.*;
import com.sellspark.SellsHRMS.repository.payroll.*;
import com.sellspark.SellsHRMS.service.payroll.*;
import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PayrollCalculationEngineServiceImpl implements PayrollCalculationEngineService {

    private final EmployeeSalaryAssignmentRepository assignmentRepo;
    private final SalaryComponentRepository componentRepo;
    private final StatutoryComponentRepository statutoryRepo;
    private final StatutoryComponentMappingRepository mappingRepo;
    private final StatutoryComputationEngineService statutoryEngine;
    private final TaxComputationEngineService taxEngine;
    private final FormulaEvaluatorService formulaEvaluator;
    private final PayRunRepository payRunRepo;
    private final SalarySlipRepository slipRepo;
    private final SalarySlipComponentRepository slipComponentRepo;

    @Override
    public PayRun runPayRun(Long orgId, LocalDate startDate, LocalDate endDate) {
        log.info("▶ Running PayRun for Org: {} | Period: {} - {}", orgId, startDate, endDate);

        // Create PayRun record
        PayRun payRun = PayRun.builder()
                .organisation(new Organisation(orgId))
                .startDate(startDate)
                .endDate(endDate)
                .status(PayRun.PayRunStatus.READY)
                .build();

        payRunRepo.save(payRun);

        // Step 1️⃣: Get all active employee salary assignments
        List<EmployeeSalaryAssignment> assignments = assignmentRepo
                .findByOrganisationIdAndActiveTrue(orgId);

        double totalGross = 0, totalDeduction = 0;

        // Step 2️⃣: Iterate employees
        for (EmployeeSalaryAssignment assignment : assignments) {
            SalarySlip slip = generateSlipForEmployee(assignment, payRun, startDate, endDate);
            payRun.getSalarySlips().add(slip);
            totalGross += slip.getGrossPay();
            totalDeduction += slip.getTotalDeductions();
        }

        payRun.setTotalGross(totalGross);
        payRun.setTotalDeduction(totalDeduction);
        payRun.setTotalNet(totalGross - totalDeduction);
        payRun.setStatus(PayRun.PayRunStatus.COMPLETED);

        return payRunRepo.save(payRun);
    }

    // ---------------------------------------------------------------------------------------------------
    // 🧮 Generate Individual Employee Payslip
    // ---------------------------------------------------------------------------------------------------
    private SalarySlip generateSlipForEmployee(EmployeeSalaryAssignment assignment, PayRun payRun, LocalDate from, LocalDate to) {
        log.info("Generating salary slip for employee {}", assignment.getEmployee().getId());

        SalarySlip slip = new SalarySlip();
        slip.setAssignment(assignment);
        slip.setEmployee(assignment.getEmployee());
        slip.setPayRun(payRun);
        slip.setFromDate(from);
        slip.setToDate(to);

        double gross = 0.0, deductions = 0.0;

        List<SalarySlipComponent> components = new ArrayList<>();

        // --- STEP 1: EARNINGS & DEDUCTIONS from Structure ---
        for (SalaryComponent sc : assignment.getSalaryStructure().getComponents()) {
            double amount = computeComponentAmount(assignment, sc, components);

            SalarySlipComponent comp = new SalarySlipComponent();
            comp.setSalarySlip(slip);
            comp.setComponent(sc);
            comp.setAmount(amount);
            comp.setComponentType(sc.getType().name());
            comp.setSourceEngine(sc.getCalculationType().name());
            comp.setCalculationLog("Calculated using: " + sc.getCalculationType());
            components.add(comp);

            if (sc.getType() == SalaryComponent.ComponentType.EARNING) gross += amount;
            else deductions += amount;
        }

        // --- STEP 2: STATUTORY DEDUCTIONS ---
        List<StatutoryComponent> statutoryList = statutoryRepo.findByOrganisationIdAndIsActiveTrue(assignment.getOrganisation().getId());
        for (StatutoryComponent stat : statutoryList) {
            double deduction = statutoryEngine.compute(stat, assignment, components);
            if (deduction > 0) {
                SalarySlipComponent statComp = new SalarySlipComponent();
                statComp.setSalarySlip(slip);
                statComp.setStatutoryComponent(stat);
                statComp.setAmount(deduction);
                statComp.setComponentType("DEDUCTION");
                statComp.setSourceEngine("STATUTORY_ENGINE");
                statComp.setIsStatutory(true);
                statComp.setCalculationLog("Computed using statutory mappings for " + stat.getName());
                components.add(statComp);
                deductions += deduction;
            }
        }

        // --- STEP 3: TAX DEDUCTION ---
        double tds = taxEngine.compute(assignment.getEmployee(), assignment, gross);
        if (tds > 0) {
            SalarySlipComponent taxComp = new SalarySlipComponent();
            taxComp.setSalarySlip(slip);
            taxComp.setAmount(tds);
            taxComp.setComponentType("DEDUCTION");
            taxComp.setSourceEngine("TAX_ENGINE");
            taxComp.setCalculationLog("Monthly TDS deduction");
            components.add(taxComp);
            deductions += tds;
        }

        // --- STEP 4: Summary ---
        slip.setGrossPay(gross);
        slip.setTotalDeductions(deductions);
        slip.setNetPay(gross - deductions);
        slip.setComponents(components);

        slipRepo.save(slip);
        slipComponentRepo.saveAll(components);

        return slip;
    }

    // ---------------------------------------------------------------------------------------------------
    // 🧮 Compute salary component amount (EARNING/DEDUCTION)
    // ---------------------------------------------------------------------------------------------------
    private double computeComponentAmount(EmployeeSalaryAssignment assignment, SalaryComponent sc, List<SalarySlipComponent> prevComps) {
        double amount = 0.0;

        switch (sc.getCalculationType()) {
            case FIXED -> amount = Optional.ofNullable(sc.getAmount()).orElse(0.0);
            case PERCENTAGE -> amount = assignment.getBasePay() * (sc.getAmount() / 100);
            case FORMULA -> {
                Map<String, Double> variableContext = buildFormulaContext(assignment, prevComps);
                amount = formulaEvaluator.evaluate(sc.getFormula(), variableContext);
            }
        }

        // Round if required
        if (Boolean.TRUE.equals(sc.getRoundToNearest())) amount = Math.round(amount);

        return amount;
    }

    private Map<String, Double> buildFormulaContext(EmployeeSalaryAssignment assignment, List<SalarySlipComponent> comps) {
        Map<String, Double> context = new HashMap<>();
        context.put("BASE", assignment.getBasePay());
        for (SalarySlipComponent c : comps) {
            if (c.getComponent() != null)
                context.put(c.getComponent().getAbbreviation().toUpperCase(), c.getAmount());
        }
        return context;
    }
}
