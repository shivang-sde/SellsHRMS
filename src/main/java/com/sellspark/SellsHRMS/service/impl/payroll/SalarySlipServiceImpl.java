package com.sellspark.SellsHRMS.service.impl.payroll;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.sellspark.SellsHRMS.dto.payroll.SalarySlipComponentDTO;
import com.sellspark.SellsHRMS.dto.payroll.SalarySlipDTO;
import com.sellspark.SellsHRMS.entity.payroll.SalarySlip;
import com.sellspark.SellsHRMS.repository.payroll.SalarySlipRepository;
import com.sellspark.SellsHRMS.service.payroll.SalarySlipTemplateService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SalarySlipServiceImpl {

    private final SalarySlipRepository slipRepo;
    private final SalarySlipTemplateService salarySlipTemplateService;

    /** ---------------- Get Slip DTO ---------------- **/
    public SalarySlipDTO getSlipDtoById(Long id) {
        SalarySlip slip = slipRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Salary Slip not found"));

        return toDTO(slip);
    }

    public SalarySlip saveSlip(SalarySlip slip) {
        return slipRepo.save(slip);
    }

    public List<SalarySlip> getEmployeeSlips(Long empId) {
        return slipRepo.findByEmployee_IdOrderByFromDateDesc(empId);
    }

    public SalarySlip getSlip(Long slipId) {
        return slipRepo.findById(slipId)
                .orElseThrow(() -> new RuntimeException("Salary Slip not found"));
    }

    public List<SalarySlipDTO> getAllByEmployee(Long empId) {
        return slipRepo.findByEmployee_IdOrderByPayRun_YearDescPayRun_MonthDesc(empId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private SalarySlipDTO toDTO(SalarySlip slip) {
    return SalarySlipDTO.builder()
            .id(slip.getId())
            .employeeId(slip.getEmployee().getId())
            .employeeName(slip.getEmployee().getFirstName() + " " + slip.getEmployee().getLastName())
            .grossPay(slip.getGrossPay())
            .totalDeductions(slip.getTotalDeductions())
            .netPay(slip.getNetPay())
            .fromDate(slip.getFromDate())
            .toDate(slip.getToDate())
            .pdfUrl(slip.getPdfUrl())
            .payRunId(slip.getPayRun() != null ? slip.getPayRun().getId() : null)
            .components(
                    slip.getComponents().stream()
                            .map(c -> {
                                String componentName = null;
                                String componentType = null;
                                Long componentId = null;

                                // Handle SalaryComponent
                                if (c.getComponent() != null) {
                                    componentId = c.getComponent().getId();
                                    componentName = c.getComponentName() != null
                                            ? c.getComponentName()
                                            : c.getComponent().getName();
                                    componentType = c.getComponentType() != null
                                            ? c.getComponentType()
                                            : (c.getComponent().getType() != null
                                                ? c.getComponent().getType().name()
                                                : null);
                                }

                                // Handle StatutoryComponent
                                else if (c.getStatutoryComponent() != null) {
                                    componentId = c.getStatutoryComponent().getId();
                                    componentName = c.getStatutoryComponent().getName();
                                    componentType = c.getComponentType(); // already stored in DB
                                }

                                // Fallback for manual or tax components
                                else {
                                    componentName = c.getComponentName();
                                    componentType = c.getComponentType();
                                }

                                return SalarySlipComponentDTO.builder()
                                        .id(c.getId())
                                        .componentId(componentId)
                                        .componentName(componentName)
                                        .componentType(componentType)
                                        .amount(c.getAmount())
                                        .calculationLog(c.getCalculationLog())
                                        .build();
                            })
                            .collect(Collectors.toList())
            )
            .build();
}


    /** ---------------- PDF Generation ---------------- **/
    public SalarySlip generatePdfForSlip(Long slipId, Long orgId) {
        SalarySlip slip = getSlip(slipId);
        try {
            // 1️⃣ Render FreeMarker HTML
            String html = salarySlipTemplateService.renderSalarySlip(slip.getId(), orgId);

            // 2️⃣ Prepare directories and file path
            Path dir = Paths.get("uploads", "salary-slips", String.valueOf(orgId));
            Files.createDirectories(dir);

            String fileName = "payslip-" + slip.getEmployee().getEmployeeCode() + "-" + slip.getId() + ".pdf";
            Path pdfPath = dir.resolve(fileName);

            // 3️⃣ Render styled PDF using Flying Saucer
            try (OutputStream os = new FileOutputStream(pdfPath.toFile())) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.useFastMode();

                // Base URI ensures embedded logo/images resolve correctly
                String baseUri = new File("uploads").toURI().toString();
                builder.withHtmlContent(html, baseUri);
                builder.toStream(os);
                builder.run();
            }

            // 4️⃣ Update DB with relative path
            String relativePath = "/uploads/salary-slips/" + orgId + "/" + fileName;
            slip.setPdfUrl(relativePath);
            log.info("Generated payslip PDF at: {}", relativePath);

            return saveSlip(slip);

        } catch (Exception e) {
            log.error("PDF generation failed for slip {}: {}", slipId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage(), e);
        }
    }
}
