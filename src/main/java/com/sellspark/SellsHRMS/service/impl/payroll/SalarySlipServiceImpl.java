package com.sellspark.SellsHRMS.service.impl.payroll;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.sellspark.SellsHRMS.dto.payroll.SalarySlipDTO;
import com.sellspark.SellsHRMS.entity.payroll.SalarySlip;
import com.sellspark.SellsHRMS.mapper.SalarySlipMapper;
import com.sellspark.SellsHRMS.repository.payroll.SalarySlipRepository;
import com.sellspark.SellsHRMS.service.payroll.SalarySlipService;
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
public class SalarySlipServiceImpl implements SalarySlipService {

    private final SalarySlipRepository slipRepo;
    private final SalarySlipTemplateService salarySlipTemplateService;
    private final SalarySlipMapper salarySlipMapper;

    /** ---------------- Get Slip DTO ---------------- **/
    @Override
    public SalarySlipDTO getSalarySlipDtoById(Long id) {
        SalarySlip slip = slipRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Salary Slip not found"));

        return salarySlipMapper.toDTO(slip);
    }

    @Override
    public SalarySlip saveSlip(SalarySlip slip) {
        return slipRepo.save(slip);
    }

    @Override
    public List<SalarySlip> getEmployeeSlips(Long empId) {
        return slipRepo.findByEmployee_IdOrderByPayRun_YearDescPayRun_MonthDesc(empId);
    }

    @Override
    public SalarySlip getSlip(Long slipId) {
        return slipRepo.findById(slipId)
                .orElseThrow(() -> new RuntimeException("Salary Slip not found"));
    }

    @Override
    public List<SalarySlipDTO> getAllByEmployee(Long empId) {
        return slipRepo.findByEmployee_IdOrderByPayRun_YearDescPayRun_MonthDesc(empId)
                .stream()
                .map(salarySlipMapper::toDTO)
                .collect(Collectors.toList());
    }

    /** ---------------- PDF Generation ---------------- **/
    @Override
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
