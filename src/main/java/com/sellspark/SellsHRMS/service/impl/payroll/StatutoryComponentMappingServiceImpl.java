package com.sellspark.SellsHRMS.service.impl.payroll;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sellspark.SellsHRMS.dto.payroll.StatutoryComponentMappingDTO;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.payroll.SalaryComponent;
import com.sellspark.SellsHRMS.entity.payroll.StatutoryComponent;
import com.sellspark.SellsHRMS.entity.payroll.StatutoryComponentMapping;
import com.sellspark.SellsHRMS.exception.InvalidOperationException;
import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;
import com.sellspark.SellsHRMS.repository.payroll.SalaryComponentRepository;
import com.sellspark.SellsHRMS.repository.payroll.StatutoryComponentMappingRepository;
import com.sellspark.SellsHRMS.repository.payroll.StatutoryComponentRepository;
import com.sellspark.SellsHRMS.service.payroll.StatutoryComponentMappingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class StatutoryComponentMappingServiceImpl implements StatutoryComponentMappingService {

    private final StatutoryComponentMappingRepository mappingRepository;
    private final StatutoryComponentRepository statutoryComponentRepository;
    private final SalaryComponentRepository salaryComponentRepository;
    private final OrganisationRepository organisationRepository;

    // -------------------- CREATE --------------------
    @Override
    public StatutoryComponentMappingDTO createMapping(StatutoryComponentMappingDTO dto) {
        validateInput(dto);



        StatutoryComponent statutoryComponent = statutoryComponentRepository.findById(dto.getStatutoryComponentId())
                .orElseThrow(() -> new ResourceNotFoundException("Statutory Component not found"));

        SalaryComponent salaryComponent = salaryComponentRepository.findById(dto.getSalaryComponentId())
                .orElseThrow(() -> new ResourceNotFoundException("Salary Component not found"));

        Organisation organisation = organisationRepository.findById(dto.getOrganisationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organisation not found"));

        if (mappingRepository.existsByStatutoryComponentIdAndSalaryComponentIdAndOrganisationId(
            dto.getStatutoryComponentId(),
            dto.getSalaryComponentId(),
             dto.getOrganisationId()
            )) {
        throw new InvalidOperationException("Mapping already exists between these components.");
}


        StatutoryComponentMapping mapping = StatutoryComponentMapping.builder()
                .statutoryComponent(statutoryComponent)
                .salaryComponent(salaryComponent)
                .organisation(organisation)
                .countryCode(dto.getCountryCode())
                .stateCode(dto.getStateCode())
                .employeePercent(statutoryComponent.getRules().getFirst().getEmployeeContributionPercent()) // later you have to make sure only one rules for the one component or if planing to have more then make sure you get the latest one.
                .employerPercent(statutoryComponent.getRules().getFirst().getEmployeeContributionPercent())
                .customRuleConfig(dto.getCustomRuleConfig())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .includeInCalculation(dto.getIncludeInCalculation() != null ? dto.getIncludeInCalculation() : true)
                .build();

        mappingRepository.save(mapping);
        return convertToDTO(mapping);
    }

    // -------------------- UPDATE --------------------
    @Override
    public StatutoryComponentMappingDTO updateMapping(Long id, StatutoryComponentMappingDTO dto) {
        StatutoryComponentMapping mapping = mappingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mapping not found"));

        if (dto.getEmployeePercent() != null)
            mapping.setEmployeePercent(dto.getEmployeePercent());  // override and exiting will be same no override concepts for now
        if (dto.getEmployerPercent() != null)
            mapping.setEmployerPercent(dto.getEmployerPercent());
        if (dto.getCustomRuleConfig() != null)
            mapping.setCustomRuleConfig(dto.getCustomRuleConfig());
        // if (dto.getCountryCode() != null)
        //     mapping.setCountryCode(dto.getCountryCode());
        // if (dto.getStateCode() != null)
        //     mapping.setStateCode(dto.getStateCode());
        if (dto.getActive() != null)
            mapping.setActive(dto.getActive());
        if (dto.getIncludeInCalculation() != null)
            mapping.setIncludeInCalculation(dto.getIncludeInCalculation());

        mappingRepository.save(mapping);
        return convertToDTO(mapping);
    }

    // -------------------- DEACTIVATE (SOFT DELETE) --------------------
    @Override
    public void deactivateMapping(Long id) {
        StatutoryComponentMapping mapping = mappingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mapping not found"));
        mapping.setActive(false);
        mappingRepository.save(mapping);
    }

    // -------------------- FETCH BY ID --------------------
    @Override
    public StatutoryComponentMappingDTO getMappingById(Long id) {
        StatutoryComponentMapping mapping = mappingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mapping not found"));
        return convertToDTO(mapping);
    }

    // -------------------- FETCH BY ORGANISATION --------------------
    @Override
    public List<StatutoryComponentMappingDTO> getMappingsByOrganisation(Long organisationId) {
        return mappingRepository.findByOrganisationIdAndActiveTrue(organisationId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // -------------------- FETCH BY STATUTORY COMPONENT --------------------
    @Override
    public List<StatutoryComponentMappingDTO> getMappingsByStatutoryComponent(Long orgId, Long statutoryComponentId) {
        return mappingRepository.findByStatutoryComponentIdAndOrganisationId(statutoryComponentId, orgId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // -------------------- INTERNAL CONVERSION --------------------
    private StatutoryComponentMappingDTO convertToDTO(StatutoryComponentMapping mapping) {
        StatutoryComponent sc = mapping.getStatutoryComponent();
        return StatutoryComponentMappingDTO.builder()
                .id(mapping.getId())
                .statutoryComponentName(sc.getName())
                .statutoryComponentId(sc.getId())
                .salaryComponentName(mapping.getSalaryComponent().getAbbreviation())
                .salaryComponentId(mapping.getSalaryComponent().getId())
                .organisationId(mapping.getOrganisation().getId())
                .countryCode(mapping.getCountryCode())
                .stateCode(mapping.getStateCode())
                .employeePercent(mapping.getEmployeePercent() != null 
                    ? mapping.getEmployeePercent() 
                    : sc.getRules().getFirst().getEmployeeContributionPercent() )
                .employerPercent(mapping.getEmployerPercent() != null 
                    ? mapping.getEmployerPercent()
                    : sc.getRules().getFirst().getEmployerContributionPercent()  )
                .customRuleConfig(mapping.getCustomRuleConfig())
                .active(mapping.getActive())
                .includeInCalculation(mapping.getIncludeInCalculation())
                .build();
    }

    private void validateInput(StatutoryComponentMappingDTO dto) {
        if (dto.getStatutoryComponentId() == null)
            throw new IllegalArgumentException("Statutory Component ID is required");
        if (dto.getSalaryComponentId() == null)
            throw new IllegalArgumentException("Salary Component ID is required");
        if (dto.getOrganisationId() == null)
            throw new IllegalArgumentException("Organisation ID is required");
    }
}
