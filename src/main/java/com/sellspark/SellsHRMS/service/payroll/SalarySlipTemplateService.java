package com.sellspark.SellsHRMS.service.payroll;


import com.sellspark.SellsHRMS.dto.payroll.SalarySlipTemplateDTO;
import com.sellspark.SellsHRMS.dto.payroll.TemplatePreviewRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface SalarySlipTemplateService {

    /**
     * Get all templates for an organisation
     */
    List<SalarySlipTemplateDTO> getAllTemplates(Long orgId);

    /**
     * Get template by ID (with org validation)
     */
    SalarySlipTemplateDTO getTemplateById(Long id, Long orgId);

    /**
     * Get default template for an organisation
     */
    SalarySlipTemplateDTO getDefaultTemplate(Long orgId);

    /**
     * Save or update template
     */
    SalarySlipTemplateDTO saveTemplate(SalarySlipTemplateDTO templateDTO, Long orgId);

    /**
     * Delete template (soft delete)
     */
    boolean deleteTemplate(Long id, Long orgId);

    /**
     * Set template as default
     */
    boolean setAsDefault(Long id, Long orgId);

    /**
     * Generate preview HTML with mock data
     */
    String generatePreview(TemplatePreviewRequest request, Long orgId);

    /**
     * Render actual salary slip with real data
     */
    String renderSalarySlip(Long salarySlipId, Long orgId);

    /**
     * Get mock data for preview
     */
    Map<String, Object> getMockData(Long orgId);

    /**
     * Get available fields for template designer
     */
    Map<String, List<Map<String, String>>> getAvailableFields(Long orgId);

    /**
     * Upload logo for template
     */
    String uploadLogo( Long orgId, MultipartFile file) throws Exception;

    /**
     * Generate PDF from salary slip
     */
    byte[] generatePDF(Long salarySlipId, Long orgId) throws Exception;
}
