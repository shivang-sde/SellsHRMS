package com.sellspark.SellsHRMS.service.impl.payroll;


import com.sellspark.SellsHRMS.dto.payroll.SalaryComponentDTO;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.payroll.SalaryComponent;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;
import com.sellspark.SellsHRMS.repository.payroll.SalaryComponentRepository;
import com.sellspark.SellsHRMS.service.payroll.SalaryComponentService;
import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SalaryComponentServiceImpl implements SalaryComponentService {

    private final SalaryComponentRepository componentRepository;
    private final OrganisationRepository organisationRepository;

    // ------------------ CREATE ------------------
    @Override
    public SalaryComponentDTO createComponent(SalaryComponentDTO dto) {
        Organisation org = organisationRepository.findById(dto.getOrganisationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organisation not found"));

        SalaryComponent component = new SalaryComponent();
        mapDtoToEntity(dto, component);
        component.setOrganisation(org);
        componentRepository.save(component);

        return mapEntityToDto(component);
    }

    // ------------------ UPDATE ------------------
    @Override
    public SalaryComponentDTO updateComponent(Long id, SalaryComponentDTO dto) {
        SalaryComponent component = componentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salary component not found"));

        mapDtoToEntity(dto, component);
        componentRepository.save(component);
        return mapEntityToDto(component);
    }

    // ------------------ DEACTIVATE (SOFT DELETE) ------------------
    @Override
    public void deactivateComponent(Long id) {
        SalaryComponent component = componentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salary component not found"));
        component.setActive(false);
        componentRepository.save(component);
    }

    // ------------------ GET BY ID ------------------
    @Override
    public SalaryComponentDTO getComponent(Long id) {
        return componentRepository.findById(id)
                .map(this::mapEntityToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Salary component not found"));
    }

    // ------------------ GET ACTIVE BY ORG ------------------
    @Override
    public List<SalaryComponentDTO> getActiveComponents(Long organisationId) {
        return componentRepository.findByOrganisationIdAndActiveTrue(organisationId)
                .stream().map(this::mapEntityToDto).collect(Collectors.toList());
    }

    // ------------------ GET BY COUNTRY ------------------
    // @Override
    // public List<SalaryComponentDTO> getComponentsByCountry(String countryCode) {
    //     return componentRepository.findByOrganisation_CountryCodeAndActiveTrue(countryCode)
    //             .stream().map(this::mapEntityToDto).collect(Collectors.toList());
    // }

    // ------------------ MAPPING HELPERS ------------------
    private SalaryComponentDTO mapEntityToDto(SalaryComponent e) {
        return SalaryComponentDTO.builder()
                .id(e.getId())
                .name(e.getName())
                .abbreviation(e.getAbbreviation())
                .type(e.getType() != null ? e.getType().name() : null)
                .calculationType(e.getCalculationType() != null ? e.getCalculationType().name() : null)
                .formula(e.getFormula())
                .componentCondition(e.getComponentCondition())
                .taxable(e.getIsTaxApplicable())
                .isFlexibleBenefit(e.getIsFlexibleBenefit())
                .dependsOnPaymentDays(e.getDependsOnPaymentDays())
                .includeInCTC(e.getIncludeInCTC())
                .roundToNearest(e.getRoundToNearest())
                .active(e.getActive())
                .organisationId(e.getOrganisation() != null ? e.getOrganisation().getId() : null)
                .build();
    }

    private void mapDtoToEntity(SalaryComponentDTO d, SalaryComponent e) {
        
        if (d.getName() != null) e.setName(d.getName());
        if (d.getAbbreviation() != null) e.setAbbreviation(d.getAbbreviation());
        if (d.getType() != null)
            e.setType(SalaryComponent.ComponentType.valueOf(d.getType().toUpperCase()));
        if (d.getCalculationType() != null)
            e.setCalculationType(SalaryComponent.CalculationType.valueOf(d.getCalculationType().toUpperCase()));
        e.setFormula(d.getFormula());
        e.setAmount(d.getFixedAmount());
        e.setComponentCondition(d.getComponentCondition());
        e.setIsTaxApplicable(Boolean.TRUE.equals(d.getTaxable()));
        e.setIsFlexibleBenefit(Boolean.TRUE.equals(d.getIsFlexibleBenefit()));
        e.setDependsOnPaymentDays(Boolean.TRUE.equals(d.getDependsOnPaymentDays()));
        e.setIncludeInCTC(Boolean.TRUE.equals(d.getIncludeInCTC()));
        e.setRoundToNearest(Boolean.TRUE.equals(d.getRoundToNearest()));
        if (d.getActive() != null) e.setActive(d.getActive());
    }
}

