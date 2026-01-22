package com.sellspark.SellsHRMS.service.impl.payroll;

import com.sellspark.SellsHRMS.dto.payroll.StatutoryComponentDTO;
import com.sellspark.SellsHRMS.dto.payroll.StatutoryRuleDTO;
import com.sellspark.SellsHRMS.entity.payroll.StatutoryRule.DeductionCycle;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.payroll.StatutoryComponent;
import com.sellspark.SellsHRMS.entity.payroll.StatutoryRule;
import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;
import com.sellspark.SellsHRMS.repository.payroll.StatutoryComponentRepository;
import com.sellspark.SellsHRMS.repository.payroll.StatutoryRuleRepository;
import com.sellspark.SellsHRMS.service.payroll.StatutorySetupService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StatutorySetupServiceImpl implements StatutorySetupService {

    private final StatutoryComponentRepository componentRepository;
    private final StatutoryRuleRepository ruleRepository;
    private final OrganisationRepository organisationRepository;

    // ───────────── COMPONENTS ─────────────
    @Override
    public StatutoryComponentDTO createComponent(StatutoryComponentDTO dto) {
        Organisation org = organisationRepository.findById(dto.getOrganisationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organisation not found"));

        StatutoryComponent component = new StatutoryComponent();
        mapDtoToEntity(dto, component);
        component.setOrganisation(org);

        componentRepository.save(component);
        return mapEntityToDto(component);
    }

    @Override
    public StatutoryComponentDTO updateComponent(Long id, StatutoryComponentDTO dto) {
        StatutoryComponent component = componentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Statutory component not found"));
        mapDtoToEntity(dto, component);
        componentRepository.save(component);
        return mapEntityToDto(component);
    }

    @Override
    public void deactivateComponent(Long id) {
        StatutoryComponent component = componentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Statutory component not found"));
        component.setIsActive(false);
        componentRepository.save(component);
    }

    @Override
    public List<StatutoryComponentDTO> getAllComponents(Long orgId) {
        return componentRepository.findByOrganisation_IdAndIsActiveTrue(orgId)
                .stream().map(this::mapEntityToDto).collect(Collectors.toList());
    }

    @Override
    public StatutoryComponentDTO getComponent(Long id) {
        return componentRepository.findById(id)
                .map(this::mapEntityToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Statutory component not found"));
    }

    // ───────────── RULES ─────────────
    @Override
    public StatutoryRuleDTO createRule(Long componentId, StatutoryRuleDTO dto) {
        StatutoryComponent component = componentRepository.findById(componentId)
                .orElseThrow(() -> new ResourceNotFoundException("Statutory component not found"));

        StatutoryRule rule = new StatutoryRule();
        mapDtoToEntity(dto, rule);
        rule.setStatutoryComponent(component);

        ruleRepository.save(rule);
        return mapEntityToDto(rule);
    }

    @Override
    public StatutoryRuleDTO updateRule(Long id, StatutoryRuleDTO dto) {
        StatutoryRule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Statutory rule not found"));
        mapDtoToEntity(dto, rule);
        ruleRepository.save(rule);
        return mapEntityToDto(rule);
    }

    @Override
    public void deactivateRule(Long id) {
        StatutoryRule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Statutory rule not found"));
        rule.setActive(false);
        ruleRepository.save(rule);
    }

    @Override
    public List<StatutoryRuleDTO> getRulesByComponent(Long componentId) {
        return ruleRepository.findByStatutoryComponentId(componentId)
                .stream().map(this::mapEntityToDto).collect(Collectors.toList());
    }

    // @Override
    // public List<StatutoryRuleDTO> getActiveRules(Long orgId, String countryCode, String stateCode) {
    //     return ruleRepository.findActiveRulesByOrgIdAnd(orgId)
    //             .stream().map(this::mapEntityToDto).collect(Collectors.toList());
    // }

    // ───────────── MAPPERS ─────────────
    private StatutoryComponentDTO mapEntityToDto(StatutoryComponent e) {
        return StatutoryComponentDTO.builder()
                .id(e.getId())
                .code(e.getCode())
                .name(e.getName())
                .description(e.getDescription())
                .organisationId(e.getOrganisation() != null ? e.getOrganisation().getId() : null)
                .countryCode(e.getCountryCode())
                .stateCode(e.getStateCode())
                .isActive(e.getIsActive())
                .build();
    }

    private void mapDtoToEntity(StatutoryComponentDTO d, StatutoryComponent e) {
        if (d.getCode() != null) e.setCode(d.getCode());
        if (d.getName() != null) e.setName(d.getName());
        if (d.getDescription() != null) e.setDescription(d.getDescription());
        if (d.getCountryCode() != null) e.setCountryCode(d.getCountryCode());
        if (d.getStateCode() != null) e.setStateCode(d.getStateCode());
        if (d.getIsActive() != null) e.setIsActive(d.getIsActive());
    }

    private StatutoryRuleDTO mapEntityToDto(StatutoryRule e) {
        return StatutoryRuleDTO.builder()
                .id(e.getId())
                .statutoryComponentId(e.getStatutoryComponent() != null ? e.getStatutoryComponent().getId() : null)
                .effectiveFrom(e.getEffectiveFrom())
                .effectiveTo(e.getEffectiveTo())
                .employerContributionPercent(e.getEmployerContributionPercent())
                .employeeContributionPercent(e.getEmployeeContributionPercent())
                .minApplicableSalary(e.getMinApplicableSalary())
                .maxApplicableSalary(e.getMaxApplicableSalary())
                .applyProRata(e.getApplyProRata())
                .deductionCycle(e.getDeductionCycle())
                .active(e.getActive())
                .build();
    }

    private void mapDtoToEntity(StatutoryRuleDTO d, StatutoryRule e) {
        e.setEffectiveFrom(d.getEffectiveFrom());
        e.setEffectiveTo(d.getEffectiveTo());
        e.setEmployerContributionPercent(d.getEmployerContributionPercent());
        e.setEmployeeContributionPercent(d.getEmployeeContributionPercent());
        e.setMinApplicableSalary(d.getMinApplicableSalary());
        e.setMaxApplicableSalary(d.getMaxApplicableSalary());
        e.setApplyProRata(d.getApplyProRata());
        if (d.getDeductionCycle() != null)
            e.setDeductionCycle(d.getDeductionCycle());
        if (d.getActive() != null) e.setActive(d.getActive());
    }
}

