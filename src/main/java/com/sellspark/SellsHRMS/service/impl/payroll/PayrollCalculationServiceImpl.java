package com.sellspark.SellsHRMS.service.impl.payroll;

import com.sellspark.SellsHRMS.dto.payroll.SalarySlipDTO;
import com.sellspark.SellsHRMS.dto.payroll.SalarySlipComponentDTO;
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.pdf.PdfWriter;
import com.sellspark.SellsHRMS.dto.leave.LeaveBalanceResponse;
import com.sellspark.SellsHRMS.dto.payroll.PayrollComputationRequest;
import com.sellspark.SellsHRMS.entity.*;
import com.sellspark.SellsHRMS.entity.payroll.*;
import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
import com.sellspark.SellsHRMS.repository.*;
import com.sellspark.SellsHRMS.repository.payroll.*;
import com.sellspark.SellsHRMS.service.AttendanceService;
import com.sellspark.SellsHRMS.service.LeaveService;
import com.sellspark.SellsHRMS.service.helper.SalaryComponentDependencySorter;
import com.sellspark.SellsHRMS.service.payroll.PayrollCalculationService;
import com.sellspark.SellsHRMS.service.payroll.PayslipPdfService;
import com.sellspark.SellsHRMS.service.payroll.SalarySlipTemplateService;
import com.sellspark.SellsHRMS.service.payroll.StatutoryComputationEngineService;
import com.sellspark.SellsHRMS.service.payroll.TaxComputationEngineService;
import com.sellspark.SellsHRMS.utils.FormulaExpressionEvaluator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PayrollCalculationServiceImpl implements PayrollCalculationService {

    @Value("${app.upload.base-dir}")
    private String baseDir;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.upload.url-path}")
    private String uploadUrlPath;


    private final OrganisationRepository organisationRepository;
    @Autowired
    private final PayslipPdfService payslipPdfService;
    private final EmployeeRepository employeeRepository;
    private final AttendanceService attendanceService;
    private final EmployeeLeaveBalanceRepository leaveBalanceRepository;
    private final EmployeeSalaryAssignmentRepository assignmentRepository;
    private final SalaryStructureRepository structureRepository;
    private final SalarySlipRepository slipRepository;
    private final SalarySlipComponentRepository slipComponentRepository;
    private final StatutoryComputationEngineService statutoryEngine;
    private final TaxComputationEngineService taxEngine;
    private final LeaveService leaveService;
    private final AttendanceSummaryRepository attendanceSummaryRepository;
    
    // Inject SalarySlipTemplateService
    private final SalarySlipTemplateService templateService;


    // ───────────────────────────────────────────────
    // Run Payroll for full organisation / pay run
    // ───────────────────────────────────────────────
    @Override
    public List<SalarySlipDTO> runPayroll(Long organisationId, PayRun payRun) {
        Organisation org = organisationRepository.findById(organisationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organisation not found"));

        List<EmployeeSalaryAssignment> assignments =
                assignmentRepository.findByOrganisationIdAndActiveTrue(organisationId);

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
    // Core computation for one employee (Optimized)
    // ───────────────────────────────────────────────
private SalarySlipDTO computeForEmployee(EmployeeSalaryAssignment assignment, PayRun payRun, Organisation org) {

    // Prevent duplication
    Optional<SalarySlip> existing = slipRepository
            .findByEmployee_IdAndPayRun_Id(assignment.getEmployee().getId(), payRun.getId());
    if (existing.isPresent()) {
        log.info("Payslip already exists for employee {} in payrun {}", assignment.getEmployee().getId(), payRun.getId());
        return mapToDTO(existing.get());
    }

    SalaryStructure structure = assignment.getSalaryStructure();
    if (structure == null)
        throw new ResourceNotFoundException("Salary structure not assigned to employee");

    // ───── Build runtime context for formulas ─────
    Map<String, Object> context = buildContext(assignment, org);
    log.info("🧾 Context before att computation: {}", context);

    LocalDate from = payRun.getStartDate();
    LocalDate to = payRun.getEndDate();

    // ───── Attendance derived days ─────
    Map<String, Integer> att = calculateAttendanceStats(assignment.getEmployee().getId(), from, to);

    log.info("Attendance stats: {}", att);
    double workingDays = (double) att.getOrDefault("WORKING_DAYS", 0);
    double paymentDays = (double) att.getOrDefault("PAYMENT_DAYS", 0);
    double lopDays = (double) att.getOrDefault("LOP_DAYS", 0);

    context.put("WORKING_DAYS", workingDays);
    context.put("PAYMENT_DAYS", paymentDays);
    context.put("LOP_DAYS", lopDays);


    log.info("context after att {}", context);

    double totalEarning = 0.0;
    double totalDeduction = 0.0;

    totalEarning += assignment.getBasePay(); // add base pay;
    List<SalarySlipComponent> slipComponents = new ArrayList<>();

    Map<SalaryComponent, Double> componentValueMap = new HashMap<>();

    // ───── Step 1: Compute salary structure components ─────
    List<SalaryComponent> orderedComponents = SalaryComponentDependencySorter.sortByDependencies(structure.getComponents());

    for (SalaryComponent comp : orderedComponents) {
        if (Boolean.FALSE.equals(comp.getActive())) continue;

        boolean applicable = FormulaExpressionEvaluator.evaluateCondition(comp.getComponentCondition(), context);
        if (!applicable) continue;

        double amount = computeComponentAmount(comp, context);

        log.info("component {} -> amount {}", comp.getAbbreviation(), amount);

        // Apply pro-rata if depends on payment days
        if (Boolean.TRUE.equals(comp.getDependsOnPaymentDays()) && workingDays > 0) {
            double ratio = paymentDays / workingDays;
            amount *= ratio;
            log.info("component {} -> pro-rata amount {}", comp.getAbbreviation(), amount); // pro-rata calc
        }

        // Add to context for formulas
        context.put(comp.getAbbreviation(), amount);          // for formula references
        context.put("COMP:" + comp.getAbbreviation(), amount); // for COMP:XXX references
        componentValueMap.put(comp, amount);

        log.info("component computed map -> {}", componentValueMap);
        SalarySlipComponent slipComp = new SalarySlipComponent();
        slipComp.setComponent(comp);
        slipComp.setComponentName(comp.getName());
        slipComp.setComponentAbbreviation(comp.getAbbreviation());
        slipComp.setComponentType(comp.getType().name());
        slipComp.setAmount(round(amount));
        slipComp.setCalculationLog(comp.getFormula());
        slipComponents.add(slipComp);

        if (comp.getType() == SalaryComponent.ComponentType.EARNING)
            totalEarning += amount;
        else if (comp.getType() == SalaryComponent.ComponentType.DEDUCTION)
            totalDeduction += amount;
    }

    // ───── Step 2: Statutory deductions ─────
    Map<String, Double> statutoryDeductions = statutoryEngine.compute(assignment.getEmployee(), componentValueMap, org);
    for (Map.Entry<String, Double> entry : statutoryDeductions.entrySet()) {
        double empDeduction = entry.getValue();

        // Apply pro-rata if needed
        if (workingDays > 0) {
            empDeduction *= paymentDays / workingDays;
        }

        SalarySlipComponent statComp = new SalarySlipComponent();
        statComp.setIsStatutory(true);
        statComp.setComponentName(entry.getKey());
        statComp.setComponentType("DEDUCTION");
        statComp.setAmount(round(empDeduction));
        statComp.setCalculationLog("Auto computed by Statutory Engine");
        slipComponents.add(statComp);

        totalDeduction += empDeduction;
    }

    // ───── Step 3: Income tax (TDS) ─────
    double tds = taxEngine.compute(assignment.getEmployee(), assignment, totalEarning);
    log.info("tds {}", tds);

    // Remove old TDS if any (for re-runs)
    slipComponents.removeIf(c -> "INCOME TAX (TDS)".equals(c.getComponentName()));
    totalDeduction = totalDeduction - slipComponents.stream()
            .filter(c -> "INCOME TAX (TDS)".equals(c.getComponentName()))
            .mapToDouble(SalarySlipComponent::getAmount).sum();

    if (tds > 0) {
        SalarySlipComponent tdsComp = new SalarySlipComponent();
        tdsComp.setIsStatutory(false);
        tdsComp.setComponentName("INCOME TAX (TDS)");
        tdsComp.setComponentType("DEDUCTION");
        tdsComp.setAmount(round(tds));
        tdsComp.setCalculationLog("Auto computed by Tax Engine");
        slipComponents.add(tdsComp);
        totalDeduction += tds;
    }

    // ───── Step 4: Net Pay ─────
    double netPay = round(totalEarning - totalDeduction);

    // ───── Step 5: Build and save slip ─────
    SalarySlip slip = new SalarySlip();
    slip.setStatus(SalarySlip.SlipStatus.GENERATED);
    slip.setOrganisation(org);
    slip.setEmployee(assignment.getEmployee());
    slip.setAssignment(assignment);
    slip.setPayRun(payRun);
    slip.setFromDate(from);
    slip.setToDate(to);
    slip.setWorkingDays(workingDays);
    slip.setPaymentDays(paymentDays);
    slip.setLopDays(lopDays);
    slip.setGrossPay(round(totalEarning));
    slip.setTotalDeductions(round(totalDeduction));
    slip.setNetPay(netPay);
    slip.setComponents(slipComponents);

    slipRepository.save(slip);
    slipComponents.forEach(c -> c.setSalarySlip(slip));
    slipComponentRepository.saveAll(slipComponents);

    SalarySlipDTO dto = mapToDTO(slip);
    log.info("Salary slip DTO: {}", dto);

    generateAndStorePdf(slip, dto, org, payRun); // PDF generation

    return dto;
}


           // Once all slips are generated in a pay run,
            // you can asynchronously generate all PDFs (to avoid blocking payroll processing) using:
            // @Async("fileProcessingExecutor")
            // public void generateAndStorePdfAsync(SalarySlip slip, Organisation org, PayRun payRun) { ... }

    // ───────────────────────────────────────────────
    // Build runtime context for formulas
    // ───────────────────────────────────────────────
    private Map<String, Object> buildContext(EmployeeSalaryAssignment assignment, Organisation org) {

        log.info("base pay assignment {}", assignment.getBasePay());
        double basePay = Optional.ofNullable(assignment.getBasePay()).orElse(0.0);
        double varPay  = Optional.ofNullable(assignment.getVariablePay()).orElse(0.0);
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
    private double computeComponentAmount(SalaryComponent comp, Map<String, Object> context) {
        return switch (comp.getCalculationType()) {
            case FIXED -> Optional.ofNullable(comp.getAmount()).orElse(0.0);
            case FORMULA -> FormulaExpressionEvaluator.evaluate(comp.getFormula(), context);
            case PERCENTAGE -> {
                // If a formula exists, use it; otherwise, default to BASE * percent
                if (comp.getFormula() != null && !comp.getFormula().isBlank()) {
                    yield FormulaExpressionEvaluator.evaluate(comp.getFormula(), context);
                }

                // Default fallback (backward compatibility)
                double base = context.containsKey("BASE") ? ((Number) context.get("BASE")).doubleValue() : 0.0;
                double percent = Optional.ofNullable(comp.getAmount()).orElse(0.0);
                log.info("percent {}, base {}, yeild {}", percent, base, base * (percent / 100.0));
                 yield base * (percent / 100.0);
            }
        };
    }

    // ───────────────────────────────────────────────
    // Attendance stats: Working, Payment, LOP
    // ───────────────────────────────────────────────
    private Map<String, Integer> calculateAttendanceStats(Long employeeId, LocalDate from, LocalDate to) {
        List<AttendanceSummary> records =
                attendanceSummaryRepository.findByEmployeeIdAndAttendanceDateBetween(employeeId, from, to);

        int present = 0, half = 0, holiday = 0, weekOff = 0, onLeave = 0, absent = 0;

        for (AttendanceSummary rec : records) {
            if (rec.getStatus() == null) continue;

            switch (rec.getStatus()) {
                case PRESENT -> present++;
                case HALF_DAY -> half++;
                case HOLIDAY -> holiday++;
                case WEEK_OFF -> weekOff++;
                case ABSENT -> absent++;
                case ON_LEAVE -> onLeave++;
                    
            }
        }

        // later convert int to double for precision days, and handling half days
        int totalDays = records.size();
        int workingDays = totalDays; // keep month full — includes holidays/weekoffs
        int paymentDays = present + onLeave + holiday + weekOff + (half / 2);
        int lopDays =   absent + Math.max(0, workingDays - paymentDays);

        Map<String, Integer> map = new HashMap<>();
        map.put("WORKING_DAYS", workingDays);
        map.put("PAYMENT_DAYS", paymentDays);
        map.put("LOP_DAYS", lopDays);
        return map;
    }


    // ───────────────────────────────────────────────
    // Convert to DTO
    // ───────────────────────────────────────────────
    private SalarySlipDTO mapToDTO(SalarySlip slip) {
        return SalarySlipDTO.builder()
                .id(slip.getId())
                .employeeId(slip.getEmployee().getId())
                .assignmentId(slip.getAssignment().getId())
                .payRunId(slip.getPayRun().getId())
                .basePay(slip.getAssignment().getBasePay())
                .grossPay(slip.getGrossPay())
                .totalDeductions(slip.getTotalDeductions())
                .netPay(slip.getNetPay())
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
                                .collect(Collectors.toList())
                )
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


    // private boolean hasLeaveBalance(Long employeeId, LeaveType leaveType, LocalDate periodStart) {
    // String year = String.valueOf(periodStart.getYear());
    // Optional<EmployeeLeaveBalance> balanceOpt = leaveBalanceRepository
    //         .findByEmployeeIdAndLeaveTypeIdAndLeaveYear(employeeId, leaveType.getId(), year);

    // if (balanceOpt.isEmpty()) return false;

    // EmployeeLeaveBalance b = balanceOpt.get();
    // double available = (b.getOpeningBalance() + b.getAccrued() + b.getCarriedForward())
    //         - (b.getAvailed() + b.getEncashed());

    // return available > 0;
    // }


    @Override
    public SalarySlipDTO recalculateDeductions(Long employeeId, Long payRunId) {
        return null; // future enhancement
    }



   private void generateAndStorePdf(SalarySlip slip, SalarySlipDTO dto, Organisation org, PayRun payRun) {
    try {
        // Step 1: Render the HTML using the organisation’s default template
        String html = templateService.renderSalarySlip(slip.getId(), org.getId());

        // Step 2: Convert HTML → PDF
        byte[] pdfBytes = convertHtmlToPdf(html);

        // Step 3: Store PDF file
        String folderPath = "org-" + org.getId() + "/payslips";
        String safePeriodLabel = payRun.getPeriodLabel().replace(" ", "-");
        String fileName = "payslip-" + slip.getEmployee().getEmployeeCode() + "-" + payRun.getId() + "-" + safePeriodLabel + ".pdf";

        String relativePath =  folderPath + "/" + fileName;
        // String pdfUrl = uploadUrlPath + "/" + relativePath;

        // Public URL (for frontend) 
        String pdfUrl = baseUrl + uploadUrlPath + "/" + relativePath;


        Path dir = Paths.get(baseDir).resolve(folderPath);
        Files.createDirectories(dir);
        Path filePath = dir.resolve(fileName);
        Files.write(filePath, pdfBytes);

        slip.setPdfPath(relativePath); // filesystem relative path 
        slip.setPdfUrl(pdfUrl); // public URL for frontend
        slip.setStatus(SalarySlip.SlipStatus.GENERATED);
        slipRepository.save(slip);

        log.info("Payslip PDF generated for empCode={}, payRun={}, path={}, url={}", slip.getEmployee().getEmployeeCode(), payRun.getId(), slip.getPdfPath(), slip.getPdfUrl());
    } catch (Exception e) {
        log.error("Failed to generate/store PDF for employee {}: {}",
                  slip.getEmployee().getEmployeeCode(), e.getMessage(), e);
    }
}


private byte[] convertHtmlToPdf(String htmlContent) throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Document document = new Document(PageSize.A4);


    PdfWriter.getInstance(document, baos);
    document.open();

    HTMLWorker htmlWorker = new HTMLWorker(document);
    htmlWorker.parse(new StringReader(htmlContent));

    document.close();
    return baos.toByteArray();
}

}
