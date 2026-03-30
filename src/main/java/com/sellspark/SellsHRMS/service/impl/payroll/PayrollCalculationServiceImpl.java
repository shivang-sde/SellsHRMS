package com.sellspark.SellsHRMS.service.impl.payroll;

import com.sellspark.SellsHRMS.dto.payroll.SalarySlipDTO;
import com.sellspark.SellsHRMS.dto.payroll.SalarySlipComponentDTO;
import com.sellspark.SellsHRMS.dto.payroll.PayrollComputationRequest;
import com.sellspark.SellsHRMS.entity.*;
import com.sellspark.SellsHRMS.entity.payroll.*;
import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
import com.sellspark.SellsHRMS.repository.*;
import com.sellspark.SellsHRMS.repository.payroll.*;
import com.sellspark.SellsHRMS.service.helper.SalaryComponentDependencySorter;
import com.sellspark.SellsHRMS.service.impl.payroll.StatutoryComputationEngineImpl.StatutoryResult;
import com.sellspark.SellsHRMS.service.payroll.PayrollCalculationService;
import com.sellspark.SellsHRMS.service.payroll.StatutoryComputationEngineService;
import com.sellspark.SellsHRMS.service.payroll.TaxComputationEngineService;
import com.sellspark.SellsHRMS.utils.FormulaExpressionEvaluator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PayrollCalculationServiceImpl implements PayrollCalculationService {

    private final OrganisationRepository organisationRepository;
    private final EmployeeSalaryAssignmentRepository assignmentRepository;
    private final SalarySlipRepository slipRepository;
    private final StatutoryComputationEngineService statutoryEngine;
    private final TaxComputationEngineService taxEngine;
    private final AttendanceSummaryRepository attendanceSummaryRepository;

    // ───────────────────────────────────────────────
    // Run Payroll for full organisation / pay run
    // ───────────────────────────────────────────────
    @Override
    public List<SalarySlipDTO> runPayroll(Long organisationId, PayRun payRun) {
        Organisation org = organisationRepository.findById(organisationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organisation not found"));

        List<EmployeeSalaryAssignment> assignments = assignmentRepository
                .findByOrganisationIdAndActiveTrue(organisationId);

        List<SalarySlipDTO> slips = new ArrayList<>();
        for (EmployeeSalaryAssignment assignment : assignments) {
            SalarySlipDTO slip = computeForEmployee(assignment, payRun, org);
            slips.add(slip);
        }
        return slips;
    }

    // ───────────────────────────────────────────────
    // Single Employee (manual payslip generation)
    // ───────────────────────────────────────────────
    @Override
    public SalarySlipDTO computeSingleEmployee(PayrollComputationRequest request) {
        EmployeeSalaryAssignment assignment = assignmentRepository
                .findByEmployeeIdAndActiveTrue(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee salary assignment not found"));

        Organisation org = assignment.getOrganisation();
        PayRun dummyRun = new PayRun();
        dummyRun.setId(-1L);
        LocalDate now = LocalDate.now();
        dummyRun.setStartDate(now.withDayOfMonth(1));
        dummyRun.setEndDate(now.withDayOfMonth(now.lengthOfMonth()));
        dummyRun.setMonth(now.getMonthValue());
        dummyRun.setYear(now.getYear());
        dummyRun.setPeriodLabel(now.getMonth().name() + "" + now.getYear());

        return computeForEmployee(assignment, dummyRun, org);
    }

    // ───────────────────────────────────────────────
    // Core computation for one employee
    // ───────────────────────────────────────────────
    private SalarySlipDTO computeForEmployee(EmployeeSalaryAssignment assignment, PayRun payRun, Organisation org) {

        // Prevent duplication
        Optional<SalarySlip> existing = slipRepository
                .findByEmployee_IdAndPayRun_Id(assignment.getEmployee().getId(), payRun.getId());
        if (existing.isPresent()) {
            log.info("Payslip already exists for employee {} in payrun {}", assignment.getEmployee().getId(),
                    payRun.getId());
            return mapToDTO(existing.get());
        }

        SalaryStructure structure = assignment.getSalaryStructure();
        if (structure == null)
            throw new ResourceNotFoundException("Salary structure not assigned to employee");

        LocalDate from = payRun.getStartDate();
        LocalDate to = payRun.getEndDate();

        // ───── Attendance derived days ─────
        Map<String, Double> att = calculateAttendanceStats(assignment.getEmployee().getId(), from, to);

        log.info("Attendance stats: {}", att);
        double workingDays = att.getOrDefault("WORKING_DAYS", 0.0);
        double paymentDays = att.getOrDefault("PAYMENT_DAYS", 0.0);
        double lopDays = att.getOrDefault("LOP_DAYS", 0.0);

        // ───── Build runtime context for formulas ─────
        Map<String, Object> context = buildContext(assignment, org);
        log.info("🧾 Context before att computation: {}", context);

        context.put("WORKING_DAYS", workingDays);
        context.put("PAYMENT_DAYS", paymentDays);
        context.put("LOP_DAYS", lopDays);

        List<SalarySlipComponent> slipComponents = new ArrayList<>();
        Map<SalaryComponent, Double> componentValueMap = new HashMap<>();

        double totalEarnings = 0.0;
        double totalDeductions = 0.0;

        // 1. Base Pay Pro-rata
        double pratedBase = round(assignment.getBasePay() * (paymentDays / workingDays));
        totalEarnings += pratedBase;
        // Add Base Pay as the first component
        slipComponents.add(
                createSlipComponent(null, "Base Pay", "BASE", "EARNING", pratedBase, "Pro-rated Base Pay", context));

        // Crucial: Add BASE to context so other components can use it
        context.put("BASE", pratedBase);
        context.put("COMP:BASE", pratedBase);

        // ───── Step 2:Loop Salary Structure Components ─────
        List<SalaryComponent> orderedComponents = SalaryComponentDependencySorter
                .sortByDependencies(structure.getComponents());

        for (SalaryComponent comp : orderedComponents) {
            if (Boolean.FALSE.equals(comp.getActive()))
                continue;
            if (!FormulaExpressionEvaluator.evaluateCondition(comp.getComponentCondition(), context))
                continue;

            log.info("Condition '{}' evaluated to {} for component {}",
                    comp.getComponentCondition(),
                    FormulaExpressionEvaluator.evaluateCondition(comp.getComponentCondition(), context),
                    comp.getAbbreviation());

            double amount = computeComponentAmount(comp, context);

            log.info("component {} -> amount {}", comp.getAbbreviation(), amount);

            // Apply pro-rata if depends on payment days
            if (Boolean.TRUE.equals(comp.getDependsOnPaymentDays()) && workingDays > 0) {
                amount = amount * (paymentDays / workingDays);
            }
            amount = round(amount);

            // Add to context for formulas
            context.put(comp.getAbbreviation(), amount); // for formula references
            context.put("COMP:" + comp.getAbbreviation(), amount); // for COMP:XXX references
            componentValueMap.put(comp, amount);

            slipComponents.add(createSlipComponent(comp, comp.getName(), comp.getAbbreviation(), comp.getType().name(),
                    amount, comp.getFormula(), context));

            log.info("component computed map -> {}", componentValueMap);

            if (comp.getType() == SalaryComponent.ComponentType.EARNING)
                totalEarnings += amount;
            else
                totalDeductions += amount;
        }

        // ───── Step 3: Statutory deductions ─────
        Map<String, StatutoryResult> statutoryMap = statutoryEngine.computeDetailed(assignment,
                componentValueMap, org);

        double totalEmployerContribution = 0.0;

        for (var entry : statutoryMap.entrySet()) {
            StatutoryResult result = entry.getValue();
            double empDed = round(result.employeeDeduction());
            // 1. Employee Part (Deduction)
            totalDeductions += empDed;
            slipComponents.add(createStatutoryComponent(entry.getKey(), empDed, "Statutory"));
            // 2. Employer Part (Contribution - NOT a deduction)
            totalEmployerContribution += round(result.employerContribution());
        }

        // ───── Step 4: Income tax (TDS) ─────
        double tds = round(taxEngine.compute(assignment.getEmployee(), assignment, totalEarnings));
        log.info("tds {}", tds);
        if (tds > 0) {
            totalDeductions += tds;
            slipComponents.add(createStatutoryComponent("INCOME TAX (TDS)", tds, "Income Tax Slab Calculation"));
        }

        // // Remove old TDS if any (for re-runs)
        // slipComponents.removeIf(c -> "INCOME TAX
        // (TDS)".equals(c.getComponentName()));
        // totalDeduction = totalDeduction - slipComponents.stream()
        // .filter(c -> "INCOME TAX (TDS)".equals(c.getComponentName()))
        // .mapToDouble(SalarySlipComponent::getAmount).sum();

        // ───── Step 5:Final Net ─────
        // double netPay = round(totalEarnings - totalDeductions);

        // ───── Step 6: Build and save slip ─────
        SalarySlip slip = SalarySlip.builder()
                .grossPay(round(totalEarnings))
                .status(SalarySlip.SlipStatus.GENERATED)
                .organisation(org)
                .employee(assignment.getEmployee())
                .assignment(assignment)
                .payRun(payRun)
                .workingDays(workingDays)
                .paymentDays(paymentDays)
                .lopDays(lopDays)
                .grossPay(round(totalEarnings))
                .totalDeductions(round(totalDeductions))
                .statutoryContributionOrg(round(totalEmployerContribution))
                .netPay(round(totalEarnings - totalDeductions))
                .components(slipComponents)
                .build();
        // Then set slip reference for components
        slipComponents.forEach(c -> c.setSalarySlip(slip));
        slip.setComponents(slipComponents);

        // save components
        slipRepository.save(slip);

        SalarySlipDTO dto = mapToDTO(slip);
        log.info("Salary slip DTO: {}", dto);

        // generateAndStorePdf(slip, dto, org, payRun); // PDF generation

        return dto;
    }

    // Once all slips are generated in a pay run,
    // you can asynchronously generate all PDFs (to avoid blocking payroll
    // processing) using:
    // @Async("fileProcessingExecutor")
    // public void generateAndStorePdfAsync(SalarySlip slip, Organisation org,
    // PayRun payRun) { ... }

    // ───────────────────────────────────────────────
    // Build runtime context for formulas
    // ───────────────────────────────────────────────
    private Map<String, Object> buildContext(EmployeeSalaryAssignment assignment, Organisation org) {

        log.info("base pay assignment {}", assignment.getBasePay());
        double basePay = Optional.ofNullable(assignment.getBasePay()).orElse(0.0);
        double varPay = Optional.ofNullable(assignment.getVariablePay()).orElse(0.0);
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("BASE", basePay);
        ctx.put("BASEPAY", basePay);
        ctx.put("VARPAY", varPay);
        ctx.put("COUNTRY", org.getCountryCode());
        ctx.put("ORG_ID", org.getId());
        ctx.put("DATE_NOW", LocalDate.now());
        return ctx;
    }

    // ───────────────────────────────────────────────
    // Formula / Fixed / Percentage calculation
    // ───────────────────────────────────────────────
    @Override
    public double computeComponentAmount(SalaryComponent comp, Map<String, Object> context) {
        log.info("--- computeComponentAmount START ---");
        log.info("Component: {} ({})", comp.getName(), comp.getAbbreviation());
        log.info("Calculation Type: {}", comp.getCalculationType());
        log.info("Formula from DB: '{}'", comp.getFormula());
        log.info("Context keys available: {}", context.keySet());

        double resultAmount = switch (comp.getCalculationType()) {
            case FIXED -> Optional.ofNullable(comp.getAmount()).orElse(0.0);
            case FORMULA -> FormulaExpressionEvaluator.evaluate(comp.getFormula(), context);
            case PERCENTAGE -> {
                // If a formula exists, use it; otherwise, default to BASE * percent
                if (comp.getFormula() != null && !comp.getFormula().isBlank()) {
                    log.info("Processing percentage via formula: {}", comp.getFormula());
                    yield FormulaExpressionEvaluator.evaluate(comp.getFormula(), context);
                }

                // Default fallback (backward compatibility)
                double base = context.containsKey("BASE") ? ((Number) context.get("BASE")).doubleValue() : 0.0;
                double percent = Optional.ofNullable(comp.getAmount()).orElse(0.0);
                log.info("percent {}, base {}, yield {}", percent, base, base * (percent / 100.0));
                yield base * (percent / 100.0);
            }
        };
        log.info("Computed amount for {}: {}", comp.getAbbreviation(), resultAmount);
        log.info("--- computeComponentAmount END ---");
        return resultAmount;
    }

    // ───────────────────────────────────────────────
    // Attendance stats: Working, Payment, LOP
    // ───────────────────────────────────────────────
    private Map<String, Double> calculateAttendanceStats(Long employeeId, LocalDate from, LocalDate to) {
        List<AttendanceSummary> records = attendanceSummaryRepository
                .findByEmployeeIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(employeeId, from, to);

        double present = 0, half = 0, holiday = 0, weekOff = 0, onLeave = 0, absent = 0;

        for (AttendanceSummary rec : records) {
            if (rec.getStatus() == null)
                continue;

            switch (rec.getStatus()) {
                case PRESENT -> present++;
                case HALF_DAY -> half++;
                case HOLIDAY -> holiday++;
                case WEEK_OFF -> weekOff++;
                case ABSENT -> absent++;
                case ON_LEAVE -> onLeave++;
                default -> throw new IllegalArgumentException("Unexpected value: " + rec.getStatus()); // this change is
                                                                                                       // made

            }
        }

        // 1. Calculate days in the pay period (e.g., 30 days)
        long daysInPeriod = java.time.temporal.ChronoUnit.DAYS.between(from, to) + 1;

        // 2. Use double for precision (half / 2.0 = 0.5)
        double paymentDays = present + onLeave + holiday + weekOff + (half / 2.0);

        // 3. LOP is the difference
        double lopDays = daysInPeriod - paymentDays;

        Map<String, Double> map = new HashMap<>();
        map.put("WORKING_DAYS", (double) daysInPeriod);
        map.put("PAYMENT_DAYS", paymentDays);
        map.put("LOP_DAYS", lopDays);
        return map;
    }

    // ───────────────────────────────────────────────
    // Convert to DTO
    // ───────────────────────────────────────────────
    private SalarySlipDTO mapToDTO(SalarySlip slip) {
        EmployeeSalaryAssignment assignment = slip.getAssignment();
        Employee emp = slip.getEmployee();
        EmployeeBank bank = emp.getPrimaryBankAccount();
        return SalarySlipDTO.builder()
                .id(slip.getId())
                .employeeId(slip.getEmployee().getId())
                .employeeName(emp.getFirstName() + " " + emp.getLastName())
                .employeeCode(emp.getEmployeeCode())
                .departmentName(emp.getDepartment().getName())
                .designationName(emp.getDesignation().getTitle())
                .panNumber(emp.getPanNumber())
                .uanNumber(emp.getUanNumber())
                .assignmentId(slip.getAssignment().getId())
                .payRunId(slip.getPayRun().getId())
                .basePay(slip.getAssignment().getBasePay())
                .grossPay(slip.getGrossPay())
                .netPay(slip.getNetPay())
                .totalDeductions(slip.getTotalDeductions())
                // Pulling from Assignment
                .monthlyGrossTarget(assignment.getMonthlyGrossTarget())
                .monthlyNetTarget(assignment.getMonthlyNetTarget())
                .annualCtc(assignment.getAnnualCtc())
                .targetBreakdownJson(assignment.getTargetBreakdownJson())
                // Bank Info
                .bankName(bank != null ? bank.getBankName() : null)
                .bankBranch(bank != null ? bank.getBranch() : null)
                .bankAccountNumber(bank != null ? bank.getAccountNumber() : null)
                .bankIfscCode(bank != null ? bank.getIfscCode() : null)
                // Attendance Info
                .workingDays(slip.getWorkingDays())
                .paymentDays(slip.getPaymentDays())
                .lopDays(slip.getLopDays())
                .fromDate(slip.getFromDate())
                .toDate(slip.getToDate())
                .pdfUrl(slip.getPdfUrl())
                .pdfPath(slip.getPdfPath())
                .components(
                        slip.getComponents().stream()
                                .map(c -> SalarySlipComponentDTO.builder()
                                        .id(c.getId())
                                        .componentId(c.getComponent() != null ? c.getComponent().getId() : null)
                                        .componentName(c.getComponentName())
                                        .componentAbbreviation(c.getComponentAbbreviation())
                                        .componentType(c.getComponentType())
                                        .isStatutory(c.getIsStatutory())
                                        .amount(c.getAmount())
                                        .calculationLog(c.getCalculationLog())
                                        .build())
                                .collect(Collectors.toList()))
                .build();
    }

    @Override
    public SalarySlipDTO calculateTotals(SalarySlipDTO slipDTO) {
        double gross = slipDTO.getComponents().stream()
                .filter(c -> "EARNING".equalsIgnoreCase(c.getComponentType()))
                .mapToDouble(SalarySlipComponentDTO::getAmount).sum();

        double ded = slipDTO.getComponents().stream()
                .filter(c -> "DEDUCTION".equalsIgnoreCase(c.getComponentType()))
                .mapToDouble(SalarySlipComponentDTO::getAmount).sum();

        slipDTO.setGrossPay(gross);
        slipDTO.setTotalDeductions(ded);
        slipDTO.setNetPay(gross - ded);
        return slipDTO;
    }

    private double round(double val) {
        return Math.round(val * 100.0) / 100.0;
    }

    // private boolean hasLeaveBalance(Long employeeId, LeaveType leaveType,
    // LocalDate periodStart) {
    // String year = String.valueOf(periodStart.getYear());
    // Optional<EmployeeLeaveBalance> balanceOpt = leaveBalanceRepository
    // .findByEmployeeIdAndLeaveTypeIdAndLeaveYear(employeeId, leaveType.getId(),
    // year);

    // if (balanceOpt.isEmpty()) return false;

    // EmployeeLeaveBalance b = balanceOpt.get();
    // double available = (b.getOpeningBalance() + b.getAccrued() +
    // b.getCarriedForward())
    // - (b.getAvailed() + b.getEncashed());

    // return available > 0;
    // }

    @Override
    public SalarySlipDTO recalculateDeductions(Long employeeId, Long payRunId) {
        return null; // future enhancement
    }

    private SalarySlipComponent createSlipComponent(SalaryComponent comp, String name, String abbr, String type,
            double amount, String formula, Map<String, Object> context) {

        String detailedLog = String.format(
                "Formula: %s | Evaluated with %s | Result: %.2f",
                formula,
                context.entrySet().stream()
                        .map(e -> e.getKey() + "=" + e.getValue())
                        .collect(Collectors.joining(", ")),
                amount);
        SalarySlipComponent slipComp = new SalarySlipComponent();
        slipComp.setComponent(comp); // Can be null for Base Pay
        slipComp.setComponentName(name);
        slipComp.setComponentAbbreviation(abbr);
        slipComp.setComponentType(type);
        slipComp.setAmount(round(amount));
        slipComp.setCalculationLog(detailedLog);
        slipComp.setIsStatutory(false);
        return slipComp;
    }

    private SalarySlipComponent createStatutoryComponent(String name, double amount, String log) {
        SalarySlipComponent statComp = new SalarySlipComponent();
        statComp.setComponentName(name);
        statComp.setComponentAbbreviation(name); // Usually PF, ESI, TDS
        statComp.setComponentType("DEDUCTION");
        statComp.setAmount(round(amount));
        statComp.setCalculationLog(log);
        statComp.setIsStatutory(true);
        return statComp;
    }

}
