package com.sellspark.SellsHRMS.service.payroll;

import com.sellspark.SellsHRMS.dto.payroll.IncomeTaxSlabDTO;
import com.sellspark.SellsHRMS.dto.payroll.IncomeTaxRuleDTO;
import java.util.List;

public interface IncomeTaxSetupService {

    // ───────── SLABS ─────────
    IncomeTaxSlabDTO createTaxSlab(IncomeTaxSlabDTO dto);

    IncomeTaxSlabDTO updateTaxSlab(Long id, IncomeTaxSlabDTO dto);

    void deactivateTaxSlab(Long id);

    List<IncomeTaxSlabDTO> getActiveSlabsOfOrgByCountryCode(Long orgId, String countryCode);

    List<IncomeTaxSlabDTO> getActiveSlabsOfOrg(Long orgId);

    IncomeTaxSlabDTO getTaxSlab(Long id);

    // ───────── RULES ─────────
    IncomeTaxRuleDTO createTaxRule(Long slabId, IncomeTaxRuleDTO dto);

    IncomeTaxRuleDTO updateTaxRule(Long id, IncomeTaxRuleDTO dto);

    void deactivateTaxRule(Long id);

    List<IncomeTaxRuleDTO> getRulesBySlab(Long slabId);
}
