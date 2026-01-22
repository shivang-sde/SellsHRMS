package com.sellspark.SellsHRMS.service.impl.payroll;


import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.pdf.PdfWriter;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.sellspark.SellsHRMS.dto.payroll.SalarySlipTemplateDTO;
import com.sellspark.SellsHRMS.dto.payroll.TemplatePreviewRequest;
import com.sellspark.SellsHRMS.entity.Employee;
import com.sellspark.SellsHRMS.entity.EmployeeBank;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.payroll.PayRun;
import com.sellspark.SellsHRMS.entity.payroll.SalaryComponent;
import com.sellspark.SellsHRMS.entity.payroll.SalarySlip;
import com.sellspark.SellsHRMS.entity.payroll.SalarySlipComponent;
import com.sellspark.SellsHRMS.entity.payroll.SalarySlipTemplate;
import com.sellspark.SellsHRMS.entity.payroll.StatutoryComponent;
import com.sellspark.SellsHRMS.exception.InvalidOperationException;
import com.sellspark.SellsHRMS.exception.OrganisationNotFoundException;
import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
import com.sellspark.SellsHRMS.repository.DepartmentRepository;
import com.sellspark.SellsHRMS.repository.DesignationRepository;
import com.sellspark.SellsHRMS.repository.EmployeeBankRepository;
import com.sellspark.SellsHRMS.repository.EmployeeRepository;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;
import com.sellspark.SellsHRMS.repository.payroll.PayRunRepository;
import com.sellspark.SellsHRMS.repository.payroll.SalaryComponentRepository;
import com.sellspark.SellsHRMS.repository.payroll.SalarySlipComponentRepository;
import com.sellspark.SellsHRMS.repository.payroll.SalarySlipRepository;
import com.sellspark.SellsHRMS.repository.payroll.SalarySlipTemplateRepository;
import com.sellspark.SellsHRMS.repository.payroll.StatutoryComponentRepository;
import com.sellspark.SellsHRMS.service.payroll.SalarySlipTemplateService;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import freemarker.cache.StringTemplateLoader;

import java.io.StringWriter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalarySlipTemplateServiceImpl implements SalarySlipTemplateService {

    private final SalarySlipTemplateRepository templateRepository;
    private final OrganisationRepository organisationRepository;
    private final EmployeeRepository employeeRepository;
    private final SalarySlipRepository salarySlipRepository;
    private final SalarySlipComponentRepository salarySlipComponentRepository;
    private final SalaryComponentRepository salaryComponentRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final EmployeeBankRepository employeeBankRepository;
    private final PayRunRepository payRunRepository;
    private final StatutoryComponentRepository statutoryComponentRepository;

    private static final String UPLOAD_DIR = "uploads/salary-templates/";

    

    @Override
    public List<SalarySlipTemplateDTO> getAllTemplates(Long orgId) {
        return templateRepository.findByOrganisation_IdAndIsActiveTrue(orgId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SalarySlipTemplateDTO getTemplateById(Long id, Long orgId) {
        return templateRepository.findByIdAndOrganisation_Id(id, orgId)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Override
    public SalarySlipTemplateDTO getDefaultTemplate(Long orgId) {
        return templateRepository.findByOrganisation_IdAndIsDefaultTrueAndIsActiveTrue(orgId)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Override
    @Transactional
    public SalarySlipTemplateDTO saveTemplate(SalarySlipTemplateDTO templateDTO, Long orgId) {
        SalarySlipTemplate template;

        Organisation org = organisationRepository.findById(orgId)
        .orElseThrow(() -> new OrganisationNotFoundException(orgId));
        if (templateDTO.getId() != null) {
            // Update existing
            template = templateRepository.findByIdAndOrganisation_Id(templateDTO.getId(), templateDTO.getOrgId())
                    .orElseThrow(() -> new RuntimeException("Template not found"));
    
        } else {
            // Create new
            template = new SalarySlipTemplate();
            template.setOrganisation(org);;
        }

        // Check duplicate name
        if (templateRepository.existsByOrganisation_IdAndTemplateNameAndIsActiveTrue(
                templateDTO.getOrgId(), templateDTO.getTemplateName())) {
            if (template.getId() == null || !template.getTemplateName().equals(templateDTO.getTemplateName())) {
                throw new RuntimeException("Template name already exists");
            }
        }

        template.setTemplateName(templateDTO.getTemplateName());
        template.setTemplateHtml(templateDTO.getTemplateHtml());
        template.setConfigJson(templateDTO.getConfigJson());
        template.setLogoUrl(templateDTO.getLogoUrl());

        // Handle default setting
        if (templateDTO.getIsDefault() != null && templateDTO.getIsDefault()) {
            templateRepository.unsetDefaultForOrganisation(templateDTO.getOrgId());
            template.setIsDefault(true);
        } else {
            template.setIsDefault(false);
        }

        try {
            renderTemplateWithData(templateDTO.getTemplateHtml(), getMockData(orgId));
        } catch (Exception e) {
        throw new RuntimeException("Template syntax invalid: " + e.getMessage());
        }

        template = templateRepository.save(template);
        return convertToDTO(template);
    }

    @Override
    @Transactional
    public boolean deleteTemplate(Long id, Long orgId) {
    SalarySlipTemplate salarySlipTemplate = templateRepository.findByIdAndOrganisation_Id(id, orgId)
        .orElseThrow(() -> new ResourceNotFoundException("Salary Slip Template", "Id", id));

    // Mark as inactive instead of active
    salarySlipTemplate.setIsActive(false);

    return templateRepository.save(salarySlipTemplate) != null;
}


    @Override
    @Transactional
    public boolean setAsDefault(Long id, Long orgId) {
        return templateRepository.findByIdAndOrganisation_Id(id, orgId)
                .map(template -> {
                    templateRepository.unsetDefaultForOrganisation(orgId);
                    template.setIsDefault(true);
                    templateRepository.save(template);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public String generatePreview(TemplatePreviewRequest request, Long orgId) {
        Map<String, Object> mockData = request.getCustomData() != null 
            ? request.getCustomData() 
            : getMockData(orgId);

        
        return renderTemplateWithData(request.getTemplateHtml(), mockData);

    }

    @Override
    public String renderSalarySlip(Long salarySlipId, Long orgId) {
        SalarySlip salarySlip = salarySlipRepository.findById(salarySlipId)
                .orElseThrow(() -> new RuntimeException("Salary slip not found"));

        if (!salarySlip.getEmployee().getOrganisation().getId().equals(orgId)) {
            throw new RuntimeException("Unauthorized access");
        }

        SalarySlipTemplate template = templateRepository
                .findByOrganisation_IdAndIsDefaultTrueAndIsActiveTrue(orgId)
                .orElseThrow(() -> new RuntimeException("No default template found"));

        Map<String, Object> data = buildRealData(salarySlip);
        log.info("buil real data  {}", data);
        return renderTemplateWithData(template.getTemplateHtml(), data);

    }

    @Override
    public Map<String, Object> getMockData(Long orgId) {
        Map<String, Object> data = new HashMap<>();

        // Organisation data
        Organisation org = organisationRepository.findById(orgId).orElse(null);
        if (org != null) {
            Map<String, String> orgData = new HashMap<>();
            orgData.put("name", org.getName());
            orgData.put("address", org.getAddress());
            orgData.put("email", org.getContactEmail() != null ? org.getContactEmail() : "");
            orgData.put("phone", org.getContactPhone() != null ? org.getContactPhone() : "");
            orgData.put("logoUrl", org.getLogoUrl() != null ? org.getLogoUrl() : "");
            data.put("organisation", orgData);
        }

        // Mock Employee data
        Map<String, String> empData = new HashMap<>();
        empData.put("employeeCode", "EMP001");
        empData.put("firstName", "John");
        empData.put("lastName", "Doe");
        empData.put("email", "john.doe@company.com");
        empData.put("phone", "+91 9876543210");
        empData.put("department", "Engineering");
        empData.put("designation", "Senior Software Engineer");
        empData.put("joiningDate", "01-Jan-2020");
        empData.put("panNumber", "ABCDE1234F");
        empData.put("uan", "123456789012");
        data.put("employee", empData);

        // Mock Bank data
        Map<String, String> bankData = new HashMap<>();
        bankData.put("bankName", "HDFC Bank");
        bankData.put("accountNumber", "1234567890");
        bankData.put("ifscCode", "HDFC0001234");
        data.put("bank", bankData);

        // Mock PayRun data
        Map<String, String> payRunData = new HashMap<>();
        payRunData.put("month", LocalDate.now().getMonth().name());
        payRunData.put("year", String.valueOf(LocalDate.now().getYear()));
        payRunData.put("payPeriod", LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        payRunData.put("payDate", LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")));
        data.put("payRun", payRunData);

        // Mock Salary Components
        List<Map<String, Object>> earnings = new ArrayList<>();
        earnings.add(createComponent("Basic Salary", 50000.00));
        earnings.add(createComponent("HRA", 20000.00));
        earnings.add(createComponent("Conveyance", 1600.00));
        earnings.add(createComponent("Special Allowance", 8400.00));

        data.put("earnings", earnings);

        List<Map<String, Object>> deductions = new ArrayList<>();
        deductions.add(createComponent("Provident Fund", 6000.00));
        deductions.add(createComponent("Professional Tax", 200.00));
        deductions.add(createComponent("Income Tax (TDS)", 5000.00));

        data.put("deductions", deductions);

        // Mock Summary
        Map<String, String> summary = new HashMap<>();
        summary.put("totalEarnings", "₹80,000.00");
        summary.put("totalDeductions", "₹11,200.00");
        summary.put("netPay", "₹68,800.00");
        summary.put("netPayInWords", "Sixty Eight Thousand Eight Hundred Rupees Only");
        data.put("summary", summary);

        return data;
    }

    @Override
    public Map<String, List<Map<String, String>>> getAvailableFields(Long orgId) {
        Map<String, List<Map<String, String>>> fields = new LinkedHashMap<>();

        // Organisation fields
        fields.put("organisation", Arrays.asList(
            createField("name", "Company Name"),
            createField("address", "Address"),
            createField("email", "Email"),
            createField("phone", "Phone"),
            createField("logoUrl", "Logo")
        ));

        // Employee fields
        fields.put("employee", Arrays.asList(
            createField("employeeCode", "Employee Code"),
            createField("firstName", "First Name"),
            createField("lastName", "Last Name"),
            createField("email", "Email"),
            createField("phone", "Phone"),
            createField("department", "Department"),
            createField("designation", "Designation"),
            createField("joiningDate", "Joining Date")

        ));

        // Bank fields
        fields.put("bank", Arrays.asList(
            createField("bankName", "Bank Name"),
            createField("accountNumber", "Account Number"),
            createField("ifscCode", "IFSC Code")
        ));

        // PayRun fields
        fields.put("payRun", Arrays.asList(
            createField("month", "Month"),
            createField("year", "Year"),
            createField("payPeriod", "Pay Period"),
            createField("payDate", "Pay Date")
        ));

        // Salary Components from database
        List<SalaryComponent> components = salaryComponentRepository.findByOrganisationIdAndActiveTrue(orgId);
        List<StatutoryComponent> stComponents = statutoryComponentRepository.findByOrganisation_IdAndIsActiveTrue(orgId);
        List<Map<String, String>> earningFields = new ArrayList<>();
        List<Map<String, String>> deductionFields = new ArrayList<>();

        for (SalaryComponent component : components) {
            Map<String, String> field = createField(component.getAbbreviation(), component.getName());
            if (component.getType() != null && component.getType().name().equalsIgnoreCase("EARNING")) {
                earningFields.add(field);
            } else if (component.getType() != null && component.getType().name().equalsIgnoreCase("DEDUCTION")) {
                deductionFields.add(field);
            }
        }

        for(StatutoryComponent component : stComponents ) {
            Map<String, String> filed = createField(component.getCode(), component.getName());
            deductionFields.add(filed);
        }

        fields.put("earnings", earningFields);
        fields.put("deductions", deductionFields);

        // Summary fields
        fields.put("summary", Arrays.asList(
            createField("basePay", "Base Salary"),
            createField("totalEarnings", "Total Earnings"),
            createField("totalDeductions", "Total Deductions"),
            createField("netPay", "Net Pay"),
            createField("netPayInWords", "Net Pay (In Words)")
        ));

        return fields;
    }

    @Override
    public String uploadLogo(Long orgId, MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new FileNotFoundException("file is empty");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new InvalidOperationException("Only image files are allowed");
        }

        // Create directory if not exists
        Path uploadPath = Paths.get(UPLOAD_DIR + orgId);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = "logo_" + System.currentTimeMillis() + extension;

        // Save file
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath);

        // Return URL (adjust based on your server configuration)
        return "/uploads/salary-templates/" + orgId + "/" + filename;
    }

    @Override
    public byte[] generatePDF(Long salarySlipId, Long orgId) throws Exception {
    String html = renderSalarySlip(salarySlipId, orgId);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
        PdfRendererBuilder builder = new PdfRendererBuilder();

        builder.useFastMode();
        builder.withHtmlContent(html,  "file:///" + new File("uploads").getAbsolutePath() + "/"); // base URI optional, useful for relative image paths
        builder.toStream(baos);
        builder.run();

        return baos.toByteArray();
    } catch (Exception e) {
        log.error("Error generating styled PDF: {}", e.getMessage(), e);
        throw new Exception("Failed to generate PDF: " + e.getMessage());
    }
}


    private String renderTemplateWithData(String templateHtml, Map<String, Object> data) {
    try {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        // Load the HTML string into FreeMarker
        StringTemplateLoader loader = new StringTemplateLoader();
        loader.putTemplate("salarySlipTemplate", templateHtml);
        cfg.setTemplateLoader(loader);

        Template template = cfg.getTemplate("salarySlipTemplate");

        StringWriter out = new StringWriter();
        template.process(data, out);
        return out.toString();

    } catch (Exception e) {
        log.error("Error rendering FreeMarker template: {}", e.getMessage(), e);
        throw new RuntimeException("Failed to render template: " + e.getMessage());
    }
}







    // Helper methods
    private String replacePlaceholders(String template, Map<String, Object> data) {
        String result = template;
        
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, String> nestedMap = (Map<String, String>) value;
                for (Map.Entry<String, String> nestedEntry : nestedMap.entrySet()) {
                    String placeholder = "${" + key + "." + nestedEntry.getKey() + "}";
                    String replacement = nestedEntry.getValue() != null ? nestedEntry.getValue() : "";
                    result = result.replace(placeholder, replacement);
                }
            } else if (value instanceof List) {
                // Handle lists (for earnings/deductions)
                // This will be handled in the template with special markers
                continue;
            } else {
                String placeholder = "${" + key + "}";
                String replacement = value != null ? value.toString() : "";
                result = result.replace(placeholder, replacement);
            }
        }
        
        return result;
    }

    private Map<String, Object> buildRealData(SalarySlip salarySlip) {
        Map<String, Object> data = new HashMap<>();
        
        // Load organisation
        Organisation org = organisationRepository.findById(salarySlip.getOrganisation().getId()).orElse(null);
        if (org != null) {
            Map<String, String> orgData = new HashMap<>();
            orgData.put("name", org.getName());
            orgData.put("address", org.getAddress());
            orgData.put("email", org.getContactEmail());
            orgData.put("phone", org.getContactPhone());
            orgData.put("logoUrl", org.getLogoUrl());
            data.put("organisation", orgData);
        }
        
        // Load employee
        Employee emp = employeeRepository.findById(salarySlip.getEmployee().getId()).orElse(null);
        if (emp != null) {
            Map<String, String> empData = new HashMap<>();
            empData.put("employeeCode", emp.getEmployeeCode());
            empData.put("firstName", emp.getFirstName());
            empData.put("lastName", emp.getLastName());
            empData.put("email", emp.getEmail());
            empData.put("phone", emp.getPhone());
            empData.put("department", emp.getDepartment() != null ? emp.getDepartment().getName() : "");
            empData.put("designation", emp.getDesignation() != null ? emp.getDesignation().getTitle() : "");
            empData.put("joiningDate", emp.getDateOfJoining() != null ? 
            emp.getDateOfJoining().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")) : "");
            data.put("employee", empData);
            
            // Load bank details
            EmployeeBank bank = employeeBankRepository.findByEmployeeIdAndIsPrimaryAccountTrue(emp.getId());
            Map<String, String> bankData = new HashMap<>();
                bankData.put("bankName", bank != null ? bank.getBankName() : "");
                bankData.put("accountNumber", bank != null ? bank.getAccountNumber() : "");
                bankData.put("ifscCode", bank != null ? bank.getIfscCode() : "");
                data.put("bank", bankData);
            
        }
        
        // Load PayRun
        Map<String, String> payRunData = new HashMap<>();
        PayRun payRun = salarySlip.getPayRun();
        payRunData.put("month", String.valueOf(payRun.getMonth()));
        payRunData.put("year", String.valueOf(payRun.getYear()));
        payRunData.put("payPeriod", salarySlip.getPayRun().getPeriodLabel());
        payRunData.put("payDate", salarySlip.getGeneratedAt() != null ?
            salarySlip.getGeneratedAt().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")) : "");
        data.put("payRun", payRunData);
        
        // Load components
        List<SalarySlipComponent> components = salarySlipComponentRepository.findBySalarySlip_Id(salarySlip.getId());
        List<Map<String, Object>> earnings = new ArrayList<>();
        List<Map<String, Object>> deductions = new ArrayList<>();

        // Add Base Pay
        Map<String, Object> basePay = new HashMap<>();
        basePay.put("name", "Base pay");
        basePay.put("amount", salarySlip.getAssignment().getBasePay());
        earnings.add(basePay);
        
        
        for (SalarySlipComponent comp : components) {
            Map<String, Object> compData = toComponentData(comp);
            if ("EARNING".equalsIgnoreCase(comp.getComponentType())) {
             earnings.add(compData);
            } else if ("DEDUCTION".equalsIgnoreCase(comp.getComponentType())) {
            deductions.add(compData);
            }
        }

        
        data.put("earnings", earnings);
        data.put("deductions", deductions);
        
        // Summary
        Map<String, String> summary = new HashMap<>();
        // include basepay later for proper pay breakdown
        //summary.put("Base Pay", formatAmount(salarySlip.getBasePay() != null ? salarySlip.getBasePay(): "Base Pay"));
        summary.put("totalEarnings", formatAmount(salarySlip.getGrossPay()));
        summary.put("totalDeductions", formatAmount(salarySlip.getTotalDeductions()));
        summary.put("netPay", formatAmount(salarySlip.getNetPay()));
        summary.put("netPayInWords", convertToWords(salarySlip.getNetPay()));
        data.put("summary", summary);
        
        return data;

    }

    private Map<String, Object> createComponent(String name, Double amount) {
        Map<String, Object> comp = new HashMap<>();
        comp.put("name", name);
        comp.put("amount", amount);
        return comp;
    }

    private Map<String, Object> toComponentData(SalarySlipComponent comp) {
    String name = Optional.ofNullable(comp.getComponentName())
            .orElseGet(() -> {
                if (comp.getComponent() != null) return comp.getComponent().getName();
                if (comp.getStatutoryComponent() != null) return comp.getStatutoryComponent().getName();
                return "Unknown Component";
            });

    Map<String, Object> data = new HashMap<>();
    data.put("name", name);
    data.put("amount", comp.getAmount());
    return data;
}


    private Map<String, String> createField(String key, String label) {
        Map<String, String> field = new HashMap<>();
        field.put("key", key);
        field.put("label", label);
        return field;
    }

    private String formatAmount(Double amount) {
        if (amount == null) return "₹0.00";
        return String.format("₹%,.2f", amount);
    }

    private String convertToWords(Double amount) {
        // Simplified number to words conversion
        // You can enhance this with a proper library
        if (amount == null) return "Zero Rupees Only";
        long rupees = amount.longValue();
        return "Rupees " + rupees + " Only"; // Placeholder - implement full conversion
    }

    private SalarySlipTemplateDTO convertToDTO(SalarySlipTemplate entity) {
        SalarySlipTemplateDTO dto = new SalarySlipTemplateDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}