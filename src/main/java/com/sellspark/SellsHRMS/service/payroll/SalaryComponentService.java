package com.sellspark.SellsHRMS.service.payroll;

import com.sellspark.SellsHRMS.dto.payroll.SalaryComponentDTO;
import java.util.List;

public interface SalaryComponentService {

    /**
     * Create a new salary component (earning, deduction, reimbursement, etc.)
     */
    SalaryComponentDTO createComponent(SalaryComponentDTO dto);

    /**
     * Update an existing salary component.
     */
    SalaryComponentDTO updateComponent(Long id, SalaryComponentDTO dto);

    /**
     * Soft delete (deactivate) salary component.
     */
    void deactivateComponent(Long id);

    /**
     * Get component by ID.
     */
    SalaryComponentDTO getComponent(Long id);

    /**
     * Get all active components for an organisation.
     */
    List<SalaryComponentDTO> getActiveComponents(Long organisationId);

    /**
     * Get all components by country for multi-country support.
     */
    // List<SalaryComponentDTO> getComponentsByCountry(String countryCode);
}
