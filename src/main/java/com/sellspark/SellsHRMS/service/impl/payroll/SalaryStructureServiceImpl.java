package com.sellspark.SellsHRMS.service.impl.payroll;

import com.sellspark.SellsHRMS.dto.payroll.SalaryComponentDTO;
import com.sellspark.SellsHRMS.dto.payroll.SalaryStructureDTO;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.payroll.SalaryComponent;
import com.sellspark.SellsHRMS.entity.payroll.SalaryStructure;
import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;
import com.sellspark.SellsHRMS.repository.payroll.SalaryComponentRepository;
import com.sellspark.SellsHRMS.repository.payroll.SalaryStructureRepository;
import com.sellspark.SellsHRMS.service.payroll.SalaryStructureService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SalaryStructureServiceImpl implements SalaryStructureService {

    private final SalaryStructureRepository structureRepository;
    private final SalaryComponentRepository componentRepository;
    private final OrganisationRepository organisationRepository;

    // ---------------- CREATE ----------------
    @Override
    public SalaryStructureDTO createStructure(SalaryStructureDTO dto) {
        Organisation org = organisationRepository.findById(dto.getOrganisationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organisation not found"));

        log.info("comps {} comId {}", dto.getComponents(), dto.getComponentIds());

        SalaryStructure structure = new SalaryStructure();
        mapDtoToEntity(dto, structure);
        structure.setOrganisation(org);

        // Attach components if any
        if (dto.getComponentIds() != null && !dto.getComponentIds().isEmpty()) {
            List<SalaryComponent> comps = componentRepository.findAllById( dto.getComponentIds());
            structure.setComponents(comps);
        }

        structureRepository.save(structure);
        return mapEntityToDto(structure);
    }

    // ---------------- UPDATE ----------------
    @Override
    public SalaryStructureDTO updateStructure(Long id, SalaryStructureDTO dto) {
        SalaryStructure structure = structureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salary Structure not found"));

        mapDtoToEntity(dto, structure);

        log.info("comps {} comId {}", dto.getComponents(), dto.getComponentIds());

        // If components provided → replace
        if (dto.getComponentIds() != null && !dto.getComponentIds().isEmpty()) {
            List<SalaryComponent> comps = componentRepository.findAllById( dto.getComponentIds());
            structure.setComponents(comps);
        }

        structureRepository.save(structure);
        return mapEntityToDto(structure);
    }

    // ---------------- DEACTIVATE ----------------
    @Override
    public void deactivateStructure(Long id) {
        SalaryStructure structure = structureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salary Structure not found"));
         log.info("deactivated salary struc before {}", structure.getActive());
        structure.setActive(false);
        structureRepository.save(structure);

        log.info("deactivated salary struc after {}", structure.getActive());
    }

    // ---------------- GET ONE ----------------
    @Override
    public SalaryStructureDTO getStructure(Long id) {
        return structureRepository.findById(id)
                .map(this::mapEntityToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Salary Structure not found"));
    }

    // ---------------- GET ALL BY ORG ----------------
    @Override
    public List<SalaryStructureDTO> getAllStructures(Long orgId) {
        return structureRepository.findByOrganisationIdAndActiveTrue(orgId)
                .stream().map(this::mapEntityToDto).collect(Collectors.toList());
    }

    // ---------------- ASSIGN COMPONENTS ----------------
    @Override
    public SalaryStructureDTO assignComponents(Long structureId, List<Long> componentIds) {
        SalaryStructure structure = structureRepository.findById(structureId)
                .orElseThrow(() -> new ResourceNotFoundException("Salary Structure not found"));
        List<SalaryComponent> comps = componentRepository.findAllById(componentIds);
        structure.setComponents(comps);
        structureRepository.save(structure);
        return mapEntityToDto(structure);
    }

    // ---------------- CLONE STRUCTURE ----------------
    @Override
    public SalaryStructureDTO cloneStructure(Long structureId, String newName) {
        SalaryStructure existing = structureRepository.findById(structureId)
                .orElseThrow(() -> new ResourceNotFoundException("Salary Structure not found"));

        SalaryStructure clone = new SalaryStructure();
        clone.setName(newName);
        clone.setDescription(existing.getDescription());
        clone.setPayrollFrequency(existing.getPayrollFrequency());
        clone.setType(existing.getType());
        clone.setOrganisation(existing.getOrganisation());
        clone.setCountryCode(existing.getCountryCode());
        clone.setComponents(existing.getComponents());
        clone.setActive(true);

        structureRepository.save(clone);
        return mapEntityToDto(clone);
    }

    // ---------------- MAPPERS ----------------
    private SalaryStructureDTO mapEntityToDto(SalaryStructure e) {
        // log.info("salary struture {}, salary compo {}", e.getName(), e.getComponents().size());
        return SalaryStructureDTO.builder()
                .id(e.getId())
                .name(e.getName())
                .description(e.getDescription())
                .type(e.getType() != null ? e.getType().name() : null)
                .payrollFrequency(e.getPayrollFrequency() != null ? e.getPayrollFrequency().name() : null)
                .organisationId(e.getOrganisation() != null ? e.getOrganisation().getId() : null)
                .currency(e.getCurrency())
                .leaveEncashmentRate(e.getLeaveEncashmentRate())
                .countryCode(e.getCountryCode())
                .active(e.getActive())
                .components(
                        e.getComponents() != null
                                ? e.getComponents().stream()
                                .filter(SalaryComponent::getActive)
                                .map(c -> SalaryComponentDTO.builder()
                                        .id(c.getId())
                                        .name(c.getName())
                                        .abbreviation(c.getAbbreviation())
                                        .description(c.getDescription())
                                        .type(c.getType() != null ? c.getType().name() : null)
                                        .calculationType(c.getCalculationType() != null ? c.getCalculationType().name() : null)
                                        .fixedAmount(c.getAmount() != null ? c.getAmount() : null )
                                        .formula(c.getFormula())
                                        .componentCondition(c.getComponentCondition())
                                        .taxable(c.getIsTaxApplicable())
                                        .dependsOnPaymentDays(c.getDependsOnPaymentDays())
                                        .includeInCTC(c.getIncludeInCTC())
                                        .roundToNearest(c.getRoundToNearest())
                                        .active(c.getActive())
                                        .build())
                                .collect(Collectors.toList())
                                : null)
                                .componentIds(e.getComponents() != null
                    ? e.getComponents().stream().map(SalaryComponent::getId).collect(Collectors.toList())
                    : null)
    
                .build();
    }

    private void mapDtoToEntity(SalaryStructureDTO dto, SalaryStructure entity) {
    entity.setName(dto.getName());
    entity.setDescription(dto.getDescription());
    entity.setCurrency(dto.getCurrency());
    entity.setLeaveEncashmentRate(dto.getLeaveEncashmentRate());
    entity.setActive(dto.getActive() != null ? dto.getActive() : true);
    entity.setCountryCode(dto.getCountryCode());
    
    if (dto.getPayrollFrequency() != null) {
        entity.setPayrollFrequency(SalaryStructure.PayrollFrequency.valueOf(dto.getPayrollFrequency()));
    }

    if (dto.getType() != null) {
        entity.setType(SalaryStructure.StructureType.valueOf(dto.getType()));
    }
}

}

