package com.sellspark.SellsHRMS.service.payroll;

import com.sellspark.SellsHRMS.dto.payroll.StatutoryComponentDTO;
import com.sellspark.SellsHRMS.dto.payroll.StatutoryRuleDTO;
import java.util.List;

public interface StatutorySetupService {

    // ───────── COMPONENTS ─────────
    StatutoryComponentDTO createComponent(StatutoryComponentDTO dto);

    StatutoryComponentDTO updateComponent(Long id, StatutoryComponentDTO dto);

    void deactivateComponent(Long id);

    List<StatutoryComponentDTO> getAllComponents(Long orgId);

    StatutoryComponentDTO getComponent(Long id);

    // ───────── RULES ─────────
    StatutoryRuleDTO createRule(Long componentId, StatutoryRuleDTO dto);

    StatutoryRuleDTO updateRule(Long id, StatutoryRuleDTO dto);

    void deactivateRule(Long id);

    List<StatutoryRuleDTO> getRulesByComponent(Long componentId);

    // List<StatutoryRuleDTO> getActiveRules(Long orgId, String countryCode, String stateCode);
}
