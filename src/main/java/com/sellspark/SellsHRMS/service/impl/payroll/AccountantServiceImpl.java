package com.sellspark.SellsHRMS.service.impl.payroll;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.pdf.PdfWriter;
import com.sellspark.SellsHRMS.dto.common.PagedResponse;
import com.sellspark.SellsHRMS.dto.payroll.SalarySlipDTO;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.User;
import com.sellspark.SellsHRMS.entity.payroll.PayRun;
import com.sellspark.SellsHRMS.entity.payroll.SalarySlip;
import com.sellspark.SellsHRMS.exception.core.HRMSException;
import com.sellspark.SellsHRMS.mapper.SalarySlipMapper;
import com.sellspark.SellsHRMS.repository.UserRepository;
import com.sellspark.SellsHRMS.repository.payroll.PayRunRepository;
import com.sellspark.SellsHRMS.repository.payroll.SalarySlipRepository;
import com.sellspark.SellsHRMS.service.payroll.AccountantService;
import com.sellspark.SellsHRMS.service.payroll.SalarySlipTemplateService;
import com.sellspark.SellsHRMS.specification.SalarySlipSpecification;
import com.sellspark.SellsHRMS.utils.PaginationUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AccountantServiceImpl implements AccountantService {

    @Value("${app.upload.base-dir}")
    private String baseDir;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.upload.url-path}")
    private String uploadUrlPath;

    private final SalarySlipRepository salarySlipRepository;
    private final PayRunRepository payRunRepository;
    private final SalarySlipTemplateService salaryTemplateService;
    private final SalarySlipMapper mapper;
    private final UserRepository userRepository;

    @Override
    public PagedResponse<SalarySlipDTO> getSalarySlips(
            Long orgId, Integer month, Integer year,
            Boolean credited, Long departmentId,
            String search, int page, int size, String sort) {

        log.info("service impl: Fetching salary slips for organization: {}", orgId);
        log.info("service impl: Month: {}", month);
        log.info("service impl: Year: {}", year);
        log.info("service impl: Credited: {}", credited);
        log.info("service impl: Department ID: {}", departmentId);
        log.info("service impl: Search: {}", search);
        log.info("service impl: Page: {}", page);
        log.info("service impl: Size: {}", size);
        log.info("service impl: Sort: {}", sort);

        // Auto-select current or latest pay run if not passed
        if (month == null || year == null) {
            LocalDate today = LocalDate.now();
            month = today.getMonthValue();
            year = today.getYear();

            Optional<PayRun> current = payRunRepository.findByOrganisation_IdAndMonthAndYear(orgId, month, year);
            if (current.isEmpty()) {
                Optional<PayRun> latest = payRunRepository.findTopByOrganisation_IdOrderByYearDescMonthDesc(orgId);
                if (latest.isEmpty()) {
                    throw new HRMSException("No pay run found", "PAYRUN_NOT_FOUND", HttpStatus.NOT_FOUND);
                }
                month = latest.get().getMonth();
                year = latest.get().getYear();
            }
        }

        // 2. Pagination + Sorting
        Sort sortSpec = Sort.unsorted();
        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(","); // (field, direction) i.e name , desc
            String field = parts[0]; // name
            Sort.Direction direction = (parts.length > 1 && "desc".equalsIgnoreCase(parts[1]))
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            sortSpec = Sort.by(direction, field);
        }
        Pageable pageable = PageRequest.of(page, size, sortSpec);

        // 🔹 3. Build dynamic specification

        Specification<SalarySlip> spec = SalarySlipSpecification.buildSpecification(
                orgId, month, year, departmentId, credited, search);

        // 🔹 4. Execute paginated + filtered query
        Page<SalarySlip> slips = salarySlipRepository.findAll(spec, pageable);

        log.info("service impl: Slips found: {}", slips.getTotalElements());

        // 🔹 5. Convert to DTO + Pagination Metadata
        Page<SalarySlipDTO> dtoPage = slips.map(mapper::toDTO);
        return PaginationUtils.toPagedResponse(dtoPage);
    }

    @Override
    public SalarySlipDTO markSalaryCredited(Long orgId, Long slipId, Long accountantUserId) {
        SalarySlip slip = salarySlipRepository.findById(slipId)
                .orElseThrow(() -> new HRMSException("Salary slip not found", "SLIP_NOT_FOUND", HttpStatus.NOT_FOUND));

        if (!slip.getOrganisation().getId().equals(orgId))
            throw new HRMSException("Unauthorized access", "ORG_ACCESS_DENIED", HttpStatus.FORBIDDEN);

        if (Boolean.TRUE.equals(slip.getIsCredited()))
            throw new HRMSException("Already credited", "ALREADY_CREDITED", HttpStatus.BAD_REQUEST);

        User accountant = userRepository.findById(accountantUserId)
                .orElseThrow(
                        () -> new HRMSException("Accountant not found", "ACCOUNTANT_NOT_FOUND", HttpStatus.NOT_FOUND));

        if (!User.SystemRole.ACCOUNTANT.equals(accountant.getSystemRole()))
            throw new HRMSException("Unauthorized role", "INVALID_ROLE", HttpStatus.FORBIDDEN);

        slip.setIsCredited(true);
        slip.setCreditedAt(LocalDateTime.now());
        salarySlipRepository.save(slip);

        return mapper.toDTO(slip);
    }

    @Override
    public PagedResponse<SalarySlipDTO> markBulkSalaryCredited(Long orgId, List<Long> slipIds, Long accountantUserId) {
        List<SalarySlipDTO> updated = slipIds.stream()
                .map(id -> markSalaryCredited(orgId, id, accountantUserId))
                .collect(Collectors.toList());
        return PagedResponse.<SalarySlipDTO>builder().content(updated).meta(null).build();
    }

    @Override
    public String generateSlipPdf(Long orgId, Long slipId) {
        // 1️⃣ Fetch slip + validate org access
        SalarySlip slip = salarySlipRepository.findById(slipId)
                .orElseThrow(() -> new HRMSException("Salary slip not found", "SLIP_NOT_FOUND", HttpStatus.NOT_FOUND));

        if (!slip.getOrganisation().getId().equals(orgId))
            throw new HRMSException("Unauthorized access", "ORG_ACCESS_DENIED", HttpStatus.FORBIDDEN);

        Organisation org = slip.getOrganisation();
        PayRun payRun = slip.getPayRun();

        // 2️⃣ Generate PDF using existing method
        generateAndStorePdf(slip, mapper.toDTO(slip), org, payRun);

        log.info("✅ PDF generated for slip {}: {}", slipId, slip.getPdfUrl());
        return slip.getPdfUrl();
    }

    @Override
    public List<String> generateBulkSlipPdfs(Long orgId, List<Long> slipIds) {
        List<String> urls = new ArrayList<>();

        for (Long id : slipIds) {
            try {
                String url = generateSlipPdf(orgId, id);
                urls.add(url);
            } catch (Exception e) {
                log.error("Failed to generate PDF for slip {}: {}", id, e.getMessage());
            }
        }
        return urls;
    }

    private void generateAndStorePdf(SalarySlip slip, SalarySlipDTO dto, Organisation org, PayRun payRun) {
        try {
            // Step 1: Render the HTML using the organisation’s default template
            String html = salaryTemplateService.renderSalarySlip(slip.getId(), org.getId());

            // Step 2: Convert HTML → PDF
            byte[] pdfBytes = convertHtmlToPdf(html);

            // Step 3: Store PDF file
            String folderPath = "org-" + org.getId() + "/payslips";
            String safePeriodLabel = payRun.getPeriodLabel().replace(" ", "-");
            String fileName = "payslip-" + slip.getEmployee().getEmployeeCode() + "-" + payRun.getId() + "-"
                    + safePeriodLabel + ".pdf";

            String relativePath = folderPath + "/" + fileName;
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
            salarySlipRepository.save(slip);

            log.info("Payslip PDF generated for empCode={}, payRun={}, path={}, url={}",
                    slip.getEmployee().getEmployeeCode(), payRun.getId(), slip.getPdfPath(), slip.getPdfUrl());
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