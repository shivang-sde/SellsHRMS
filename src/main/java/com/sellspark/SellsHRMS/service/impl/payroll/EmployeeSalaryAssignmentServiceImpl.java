package com.sellspark.SellsHRMS.service.impl.payroll;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sellspark.SellsHRMS.dto.payroll.EmployeeSalaryAssignmentDTO;
import com.sellspark.SellsHRMS.entity.Employee;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.payroll.EmployeeSalaryAssignment;
import com.sellspark.SellsHRMS.entity.payroll.IncomeTaxSlab;
import com.sellspark.SellsHRMS.entity.payroll.SalaryComponent;
import com.sellspark.SellsHRMS.entity.payroll.SalaryStructure;
import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
import com.sellspark.SellsHRMS.repository.EmployeeRepository;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;
import com.sellspark.SellsHRMS.repository.payroll.EmployeeSalaryAssignmentRepository;
import com.sellspark.SellsHRMS.repository.payroll.IncomeTaxSlabRepository;
import com.sellspark.SellsHRMS.repository.payroll.SalaryStructureRepository;
import com.sellspark.SellsHRMS.service.helper.SalaryComponentDependencySorter;
import com.sellspark.SellsHRMS.service.impl.payroll.StatutoryComputationEngineImpl.StatutoryResult;
import com.sellspark.SellsHRMS.service.payroll.EmployeeSalaryAssignmentService;
import com.sellspark.SellsHRMS.service.payroll.PayrollCalculationService;
import com.sellspark.SellsHRMS.service.payroll.StatutoryComputationEngineService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeSalaryAssignmentServiceImpl implements EmployeeSalaryAssignmentService {

    private final EmployeeSalaryAssignmentRepository assignmentRepository;
    private final PayrollCalculationService payrollCalculationService;
    private final StatutoryComputationEngineService statutoryEngine;
    private final EmployeeRepository employeeRepository;
    private final OrganisationRepository organisationRepository;
    private final SalaryStructureRepository structureRepository;
    private final IncomeTaxSlabRepository taxSlabRepository;
    private final ObjectMapper objectMapper;

    // ---------------- CREATE (Assign) ----------------
    @Override
    public EmployeeSalaryAssignmentDTO assignSalaryStructure(EmployeeSalaryAssignmentDTO dto) {
        log.info("inside service impl {}", dto);
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        Organisation org = organisationRepository.findById(dto.getOrganisationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organisation not found"));
        SalaryStructure structure = structureRepository.findById(dto.getSalaryStructureId())
                .orElseThrow(() -> new ResourceNotFoundException("Salary structure not found"));
        IncomeTaxSlab taxSlab = dto.getTaxSlabId() != null
                ? taxSlabRepository.findById(dto.getTaxSlabId())
                        .orElseThrow(() -> new ResourceNotFoundException("Tax Slabs", "Id", dto.getTaxSlabId()))
                : null;// optional

        EmployeeSalaryAssignment assignment = new EmployeeSalaryAssignment();
        mapDtoToEntity(dto, assignment);
        assignment.setEmployee(employee);
        assignment.setOrganisation(org);
        assignment.setSalaryStructure(structure);
        assignment.setTaxSlab(taxSlab);

        log.info("emp slry assign new {}", employee.getFirstName());
        /*
         * ----------- Futre Implementation -------------------
         * 
         * in futre when you assign a salary calculate the ctc of the person also
         * -> basePay + earning components(from salary components ) + employer
         * contributions + other benefits
         */

        calculateAndSetBaseline(assignment);

        assignmentRepository.save(assignment);
        return mapEntityToDto(assignment);
    }

    // ---------------- UPDATE ----------------
    @Override
    public EmployeeSalaryAssignmentDTO updateAssignment(Long id, EmployeeSalaryAssignmentDTO dto) {
        EmployeeSalaryAssignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salary assignment not found"));

        // Check if sensitive fields changed to trigger recalculation
        boolean structureChanged = dto.getSalaryStructureId() != null &&
                !dto.getSalaryStructureId().equals(assignment.getSalaryStructure().getId());
        boolean basePayChanged = dto.getBasePay() != null && !dto.getBasePay().equals(assignment.getBasePay());
        boolean forceRecalc = "recalc".equalsIgnoreCase(dto.getRemarks());
        
        mapDtoToEntity(dto, assignment);

        if (structureChanged) {
            SalaryStructure structure = structureRepository.findById(dto.getSalaryStructureId()).orElseThrow();
            assignment.setSalaryStructure(structure);
        }

        if (structureChanged || basePayChanged || forceRecalc) {
            // RE-CALC BASELINE
            calculateAndSetBaseline(assignment);
        }

        if (dto.getTaxSlabId() != null) {
            IncomeTaxSlab slab = taxSlabRepository.findById(dto.getTaxSlabId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tax slab not found"));
            assignment.setTaxSlab(slab);
        }

        assignmentRepository.save(assignment);
        return mapEntityToDto(assignment);
    }

    // ---------------- DEACTIVATE ----------------
    @Override
    public void deactivateAssignment(Long id) {
        EmployeeSalaryAssignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salary assignment not found"));
        assignment.setActive(false);
        assignmentRepository.save(assignment);
    }

    // ---------------- GET BY ID ----------------
    @Override
    public EmployeeSalaryAssignmentDTO getAssignment(Long id) {
        return assignmentRepository.findById(id)
                .map(this::mapEntityToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Salary assignment not found"));
    }

    // ---------------- GET BY EMPLOYEE ----------------
    @Override
    public EmployeeSalaryAssignmentDTO getAssignmentsByEmployee(Long employeeId) {
        EmployeeSalaryAssignment employeeSalaryAssignment = assignmentRepository
                .findByEmployeeIdAndActiveTrue(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Salary struture assigned to employee not found"));

        return mapEntityToDto(employeeSalaryAssignment);
    }

    // ---------------- GET ACTIVE BY ORG ----------------
    @Override
    public List<EmployeeSalaryAssignmentDTO> getActiveAssignments(Long orgId) {
        return assignmentRepository.findByOrganisationIdAndActiveTrue(orgId)
                .stream().map(this::mapEntityToDto).collect(Collectors.toList());
    }

    // ---------------- MAPPERS ----------------
    private EmployeeSalaryAssignmentDTO mapEntityToDto(EmployeeSalaryAssignment e) {

        return EmployeeSalaryAssignmentDTO.builder()
                .id(e.getId() != null ? e.getId() : null)
                .employeeId(e.getEmployee() != null ? e.getEmployee().getId() : null)
                .employeeName(
                        e.getEmployee() != null ? e.getEmployee().getFirstName() + " " + e.getEmployee().getLastName()
                                : null)
                .monthlyGrossTarget(e.getMonthlyGrossTarget())
                .monthlyNetTarget(e.getMonthlyNetTarget())
                .annualCtc(e.getAnnualCtc())
                .employeeDepartmentName(e.getEmployee() != null ? e.getEmployee().getDepartment().getName() : null)
                .employeeCode(e.getEmployee().getEmployeeCode() != null ? e.getEmployee().getEmployeeCode() : null)
                .organisationId(e.getOrganisation() != null ? e.getOrganisation().getId() : null)
                .salaryStructureId(e.getSalaryStructure() != null ? e.getSalaryStructure().getId() : null)
                .salaryStructureName(e.getSalaryStructure() != null ? e.getSalaryStructure().getName() : null)
                .taxSlabId(e.getTaxSlab() != null ? e.getTaxSlab().getId() : null)
                .basePay(e.getBasePay())
                .variablePay(e.getVariablePay())
                .effectiveFrom(e.getEffectiveFrom())
                .effectiveTo(e.getEffectiveTo())
                .remarks(e.getRemarks())
                .active(e.getActive())
                .targetBreakdownJson(e.getTargetBreakdownJson())
                .build();
    }

    private void mapDtoToEntity(EmployeeSalaryAssignmentDTO d, EmployeeSalaryAssignment e) {

        if (d.getBasePay() != null)
            e.setBasePay(d.getBasePay());
        if (d.getVariablePay() != null)
            e.setVariablePay(d.getVariablePay());
        e.setRemarks(d.getRemarks());
        e.setEffectiveFrom(d.getEffectiveFrom());
        e.setEffectiveTo(d.getEffectiveTo());
        if (d.getActive() != null)
            e.setActive(d.getActive());
    }

    /**
     * Logic to calculate what the employee SHOULD earn with 100% attendance.
     */

    @Override
    public void calculateAndSetBaseline(EmployeeSalaryAssignment assignment) {
        SalaryStructure structure = assignment.getSalaryStructure();
        if (structure == null)
            return;

        // Simulate context for 100% attendance
        Map<String, Object> context = new HashMap<>();
        context.put("BASE", assignment.getBasePay());
        context.put("VARPAY", assignment.getVariablePay() != null ? assignment.getVariablePay() : 0.0);
        context.put("WORKING_DAYS", 30.0); // Standard month baseline
        context.put("PAYMENT_DAYS", 30.0);
        context.put("LOP_DAYS", 0.0);

        double totalEarnings = assignment.getBasePay(); // Start with Base
        double totalDeductions = 0.0;
        Map<String, Double> breakdownMap = new LinkedHashMap<>();
        breakdownMap.put("Basic Pay", assignment.getBasePay());

        Map<SalaryComponent, Double> componentValueMap = new HashMap<>();
        List<SalaryComponent> orderedComponents = SalaryComponentDependencySorter
                .sortByDependencies(structure.getComponents());

        log.info("========== BASELINE CALCULATION LOGGING START ==========");
        log.info("Ordered Components to process: {}", orderedComponents.stream().map(SalaryComponent::getAbbreviation).collect(Collectors.toList()));

        for (SalaryComponent comp : orderedComponents) {
            log.info("Checking component: {}", comp.getAbbreviation());
            if (Boolean.FALSE.equals(comp.getActive())) {
                log.info("Component {} is Inactive. Skipping.", comp.getAbbreviation());
                continue;
            }

            // Use existing FormulaExpressionEvaluator logic here
            log.info("Baseline Calc: Processing Component {} ({}) with context keys {}", comp.getName(), comp.getAbbreviation(), context.keySet());
            double amount = payrollCalculationService.computeComponentAmount(comp, context);
            log.info("Baseline Calc: Evaluated Component {} ({}) -> Amount: {}", comp.getName(), comp.getAbbreviation(), amount);
            
            context.put(comp.getAbbreviation(), amount);
            context.put("COMP:" + comp.getAbbreviation(), amount);
            // Add to context so subsequent formulas can use this component
            componentValueMap.put(comp, amount);
            breakdownMap.put(comp.getName(), round(amount));

            if (comp.getType() == SalaryComponent.ComponentType.EARNING) {
                totalEarnings += amount;
            } else if (comp.getType() == SalaryComponent.ComponentType.DEDUCTION) {
                totalDeductions += amount;
            }
        }

        // Get Employee & Employer Statutory amounts
        // This is the "hidden" cost like Employer PF contribution
        Map<String, StatutoryResult> statutory = statutoryEngine.computeDetailed(
                assignment, componentValueMap, assignment.getOrganisation());

        // Sum up only Employer Contributions for CTC
        double totalEmpStatutory = statutory.values().stream().mapToDouble(StatutoryResult::employeeDeduction).sum();
        double totalOrgStatutory = statutory.values().stream().mapToDouble(StatutoryResult::employerContribution).sum();

        statutory.forEach((code, res) -> breakdownMap.put(code + " (Emp)", res.employeeDeduction()));

        assignment.setMonthlyGrossTarget(round(totalEarnings));
        // Net = Earnings - Component Deductions - Statutory Deductions
        assignment.setMonthlyNetTarget(round(totalEarnings - totalDeductions - totalEmpStatutory));
        // CTC = (Earnings + Employer Statutory) * 12
        assignment.setAnnualCtc(round((totalEarnings + totalOrgStatutory) * 12));

        try {
            assignment.setTargetBreakdownJson(objectMapper.writeValueAsString(breakdownMap));
        } catch (Exception ignored) {
        }
    }

    private Double round(double baselineGross) {
        return Math.round(baselineGross * 100.0) / 100.0;
    }

}
