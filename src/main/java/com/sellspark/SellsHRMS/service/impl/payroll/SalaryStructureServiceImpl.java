package com.sellspark.SellsHRMS.service.impl.payroll;

import com.sellspark.SellsHRMS.dto.payroll.SalaryComponentDTO;
import com.sellspark.SellsHRMS.dto.payroll.SalaryStructureDTO;
import com.sellspark.SellsHRMS.dto.payroll.StructureUpdatePreviewDTO;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.payroll.EmployeeSalaryAssignment;
import com.sellspark.SellsHRMS.entity.payroll.SalaryComponent;
import com.sellspark.SellsHRMS.entity.payroll.SalaryStructure;
import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;
import com.sellspark.SellsHRMS.repository.payroll.EmployeeSalaryAssignmentRepository;
import com.sellspark.SellsHRMS.repository.payroll.SalaryComponentRepository;
import com.sellspark.SellsHRMS.repository.payroll.SalaryStructureRepository;
import com.sellspark.SellsHRMS.service.payroll.EmployeeSalaryAssignmentService;
import com.sellspark.SellsHRMS.service.payroll.SalaryStructureService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SalaryStructureServiceImpl implements SalaryStructureService {

    private final SalaryStructureRepository structureRepository;
    private final SalaryComponentRepository componentRepository;
    private final OrganisationRepository organisationRepository;
    private final EmployeeSalaryAssignmentRepository assignmentRepository;

    // We need to inject this to trigger baseline recalculations during migration
    private final EmployeeSalaryAssignmentService assignmentService;

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
            List<SalaryComponent> comps = componentRepository.findAllById(dto.getComponentIds());
            structure.setComponents(comps);
        }

        structureRepository.save(structure);
        return mapEntityToDto(structure);
    }

    @Override
    public StructureUpdatePreviewDTO previewImpact(Long structureId, List<Long> newComponentIds) {
        List<EmployeeSalaryAssignment> assignments = assignmentRepository
                .findBySalaryStructureIdAndActiveTrue(structureId);

        // Get the actual components for the "New" simulation
        List<SalaryComponent> newComponents = componentRepository.findAllById(newComponentIds);

        double currentTotal = 0.0;
        double newTotal = 0.0;
        Map<String, Double> breakdown = new HashMap<>();

        for (EmployeeSalaryAssignment assignment : assignments) {
            currentTotal += assignment.getMonthlyGrossTarget();

            // SIMULATION: Create a temporary clone of the assignment
            // We don't save this!
            EmployeeSalaryAssignment tempAssignment = new EmployeeSalaryAssignment();
            tempAssignment.setBasePay(assignment.getBasePay());
            tempAssignment.setEmployee(assignment.getEmployee());
            tempAssignment.setOrganisation(assignment.getOrganisation());

            // Use a dummy structure with the NEW components
            SalaryStructure tempStructure = new SalaryStructure();
            tempStructure.setComponents(newComponents);
            tempAssignment.setSalaryStructure(tempStructure);

            // Run the calculation logic
            assignmentService.calculateAndSetBaseline(tempAssignment);

            newTotal += tempAssignment.getMonthlyGrossTarget();

            double diff = tempAssignment.getMonthlyGrossTarget() - assignment.getMonthlyGrossTarget();
            breakdown.put(assignment.getEmployee().getFirstName() + " " + assignment.getEmployee().getLastName(), diff);
        }

        return StructureUpdatePreviewDTO.builder()
                .employeeCount(assignments.size())
                .currentTotalMonthlyGross(currentTotal)
                .newTotalMonthlyGross(newTotal)
                .difference(newTotal - currentTotal)
                .impactBreakdown(breakdown)
                .build();
    }

    /**
     * ---------------- VERSIONED UPDATE ----------------
     * If the structure is already assigned to employees, we clone it
     * to keep history intact.
     */
    @Override
    public SalaryStructureDTO updateStructureWithVersion(Long id, SalaryStructureDTO dto) {
        SalaryStructure existing = structureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salary Structure not found"));
        // 1. Check if financial components changed
        List<Long> existingCompIds = existing.getComponents().stream()
                .map(SalaryComponent::getId).sorted().toList();
        List<Long> newCompIds = dto.getComponentIds() != null ? dto.getComponentIds().stream().sorted().toList()
                : List.of();

        // Check if anyone is currently using this structure
        boolean componentsChanged = !existingCompIds.equals(newCompIds);
        boolean isCurrentlyAssigned = assignmentRepository.existsBySalaryStructureIdAndActiveTrue(id);

        if (componentsChanged && isCurrentlyAssigned) {
            log.info("Financial components changed for structure {}. Versioning...", id);
            // 2. Create a NEW structure (Version 2)
            SalaryStructure newVersion = new SalaryStructure();
            mapDtoToEntity(dto, newVersion);
            newVersion.setOrganisation(existing.getOrganisation());
            newVersion.setComponents(componentRepository.findAllById(dto.getComponentIds()));

            SalaryStructure savedVersion = structureRepository.save(newVersion);
            // 3. Deactivate the old version so it's not used for NEW assignments
            existing.setActive(false);
            structureRepository.save(existing);

            // 4. 🔥 CRITICAL: Migrate active employees to the new structure and update
            // their CTC/Gross
            migrateEmployeesToNewStructure(existing.getId(), savedVersion.getId());

            return mapEntityToDto(savedVersion);
        } else {
            // No one is using it, safe to update directly
            mapDtoToEntity(dto, existing);
            if (dto.getComponentIds() != null) {
                existing.setComponents(componentRepository.findAllById(dto.getComponentIds()));
            }
            return mapEntityToDto(structureRepository.save(existing));
        }
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
            List<SalaryComponent> comps = componentRepository.findAllById(dto.getComponentIds());
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
        clone.setCurrency(existing.getCurrency());
        clone.setLeaveEncashmentRate(existing.getLeaveEncashmentRate());
        clone.setComponents(new ArrayList<>(existing.getComponents())); // Copy component list
        clone.setActive(true);

        return mapEntityToDto(structureRepository.save(clone));
    }

    /**
     * Finds all employees on the old structure and moves them to the new one,
     * triggering a recalculation of their monthly targets/CTC.
     */
    private void migrateEmployeesToNewStructure(Long oldId, Long newId) {
        List<EmployeeSalaryAssignment> activeAssignments = assignmentRepository
                .findBySalaryStructureIdAndActiveTrue(oldId);

        SalaryStructure newStructure = structureRepository.findById(newId).orElseThrow();

        for (EmployeeSalaryAssignment assignment : activeAssignments) {
            assignment.setSalaryStructure(newStructure);

            // This calls the method in AssignmentService that runs
            // FormulaExpressionEvaluator and updates MonthlyGrossTarget, AnnualCtc, etc.
            assignmentService.calculateAndSetBaseline(assignment);
        }
        assignmentRepository.saveAll(activeAssignments);
        log.info("Migrated {} employees to new structure version {}", activeAssignments.size(), newId);
    }

    // ---------------- MAPPERS ----------------
    private SalaryStructureDTO mapEntityToDto(SalaryStructure e) {
        // log.info("salary struture {}, salary compo {}", e.getName(),
        // e.getComponents().size());
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
                                                .calculationType(
                                                        c.getCalculationType() != null ? c.getCalculationType().name()
                                                                : null)
                                                .fixedAmount(c.getAmount() != null ? c.getAmount() : null)
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
