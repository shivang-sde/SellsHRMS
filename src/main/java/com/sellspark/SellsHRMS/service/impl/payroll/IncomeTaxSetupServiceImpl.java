package com.sellspark.SellsHRMS.service.impl.payroll;



import com.sellspark.SellsHRMS.dto.payroll.IncomeTaxSlabDTO;
import com.sellspark.SellsHRMS.dto.payroll.IncomeTaxRuleDTO;
import com.sellspark.SellsHRMS.entity.payroll.IncomeTaxSlab;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.payroll.IncomeTaxRule;
import com.sellspark.SellsHRMS.exception.OrganisationNotFoundException;
import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
import com.sellspark.SellsHRMS.repository.payroll.IncomeTaxSlabRepository;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;
import com.sellspark.SellsHRMS.repository.payroll.IncomeTaxRuleRepository;
import com.sellspark.SellsHRMS.service.payroll.IncomeTaxSetupService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class IncomeTaxSetupServiceImpl implements IncomeTaxSetupService {

    private final IncomeTaxSlabRepository slabRepository;
    private final IncomeTaxRuleRepository ruleRepository;
    private final OrganisationRepository orgRepo;

    // ───────────────────── SLABS ─────────────────────
    @Override
    public IncomeTaxSlabDTO createTaxSlab(IncomeTaxSlabDTO dto) {
        IncomeTaxSlab slab = new IncomeTaxSlab();
        mapDtoToEntity(dto, slab);
        slabRepository.save(slab);
        return mapEntityToDto(slab);
    }

    @Override
    public IncomeTaxSlabDTO updateTaxSlab(Long id, IncomeTaxSlabDTO dto) {
        IncomeTaxSlab slab = slabRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tax Slab not found"));
        mapDtoToEntity(dto, slab);
        slabRepository.save(slab);
        return mapEntityToDto(slab);
    }

    @Override
    public void deactivateTaxSlab(Long id) {
        IncomeTaxSlab slab = slabRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tax Slab not found"));
        slab.setAllowTaxExemption(false);
        slabRepository.save(slab);
    }

    @Override
    public List<IncomeTaxSlabDTO> getActiveSlabsOfOrgByCountryCode(Long orgId, String countryCode) {
        return slabRepository.findByOrganisation_CountryCodeAndIsActiveTrue(countryCode)
                .stream().map(this::mapEntityToDto).collect(Collectors.toList());
    }

    @Override
    public List<IncomeTaxSlabDTO> getActiveSlabsOfOrg(Long orgId) {
        return slabRepository.findByOrganisation_Id(orgId)
                .stream().map(this::mapEntityToDto).collect(Collectors.toList());
    }

    @Override
    public IncomeTaxSlabDTO getTaxSlab(Long id) {
        return slabRepository.findById(id)
                .map(this::mapEntityToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Tax Slab not found"));
    }

    // ───────────────────── RULES ─────────────────────
    @Override
    public IncomeTaxRuleDTO createTaxRule(Long slabId, IncomeTaxRuleDTO dto) {
        IncomeTaxSlab slab = slabRepository.findById(slabId)
                .orElseThrow(() -> new ResourceNotFoundException("Tax Slab not found"));

        IncomeTaxRule rule = new IncomeTaxRule();
        mapDtoToEntity(dto, rule);
        rule.setTaxSlab(slab);
        ruleRepository.save(rule);

        return mapEntityToDto(rule);
    }

    @Override
    public IncomeTaxRuleDTO updateTaxRule(Long id, IncomeTaxRuleDTO dto) {
        IncomeTaxRule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tax Rule not found"));
        mapDtoToEntity(dto, rule);
        ruleRepository.save(rule);
        return mapEntityToDto(rule);
    }

    @Override
    public void deactivateTaxRule(Long id) {
        IncomeTaxRule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tax Rule not found"));
        ruleRepository.delete(rule);
    }

    @Override
    public List<IncomeTaxRuleDTO> getRulesBySlab(Long slabId) {
        return ruleRepository.findByTaxSlabId(slabId)
                .stream().map(this::mapEntityToDto).collect(Collectors.toList());
    }

    // ───────────────────── MAPPERS ─────────────────────
    private IncomeTaxSlabDTO mapEntityToDto(IncomeTaxSlab e) {
        return IncomeTaxSlabDTO.builder()
                .id(e.getId())
                .name(e.getName())
                .organisationId(e.getOrganisation().getId())
                .countryCode(e.getCountryCode())
                .effectiveFrom(e.getEffectiveFrom())
                .effectiveTo(e.getEffectiveTo())
                .allowTaxExemption(e.getAllowTaxExemption())
                .standardExemptionLimit(e.getStandardExemptionLimit())
                .build();
    }

    private void mapDtoToEntity(IncomeTaxSlabDTO d, IncomeTaxSlab e) {
        Organisation org = orgRepo.findById(d.getOrganisationId())
        .orElseThrow(() -> new OrganisationNotFoundException(d.getOrganisationId()));
        if (d.getName() != null) e.setName(d.getName());
        if (d.getCountryCode() != null) e.setCountryCode(d.getCountryCode());
        e.setOrganisation(org);
        e.setEffectiveFrom(d.getEffectiveFrom());
        e.setEffectiveTo(d.getEffectiveTo());
        if (d.getAllowTaxExemption() != null) e.setAllowTaxExemption(d.getAllowTaxExemption());
        if (d.getStandardExemptionLimit() != null) e.setStandardExemptionLimit(d.getStandardExemptionLimit());
    }

    private IncomeTaxRuleDTO mapEntityToDto(IncomeTaxRule e) {
        return IncomeTaxRuleDTO.builder()
                .id(e.getId())
                .minIncome(e.getMinIncome())
                .maxIncome(e.getMaxIncome())
                .deductionPercent(e.getDeductionPercent())
                .condition(e.getCondition())
                .taxSlabId(e.getTaxSlab() != null ? e.getTaxSlab().getId() : null)
                .build();
    }

    private void mapDtoToEntity(IncomeTaxRuleDTO d, IncomeTaxRule e) {
        if (d.getMinIncome() != null) e.setMinIncome(d.getMinIncome());
        if (d.getMaxIncome() != null) e.setMaxIncome(d.getMaxIncome());
        if (d.getDeductionPercent() != null) e.setDeductionPercent(d.getDeductionPercent());
        e.setCondition(d.getCondition());
    }
}
