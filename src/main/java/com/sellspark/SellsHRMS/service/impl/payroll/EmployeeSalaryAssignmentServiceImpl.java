package com.sellspark.SellsHRMS.service.impl.payroll;

import com.sellspark.SellsHRMS.dto.payroll.EmployeeSalaryAssignmentDTO;
import com.sellspark.SellsHRMS.entity.Employee;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.payroll.EmployeeSalaryAssignment;
import com.sellspark.SellsHRMS.entity.payroll.IncomeTaxSlab;
import com.sellspark.SellsHRMS.entity.payroll.SalaryStructure;
import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
import com.sellspark.SellsHRMS.repository.EmployeeRepository;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;
import com.sellspark.SellsHRMS.repository.payroll.EmployeeSalaryAssignmentRepository;
import com.sellspark.SellsHRMS.repository.payroll.IncomeTaxSlabRepository;
import com.sellspark.SellsHRMS.repository.payroll.SalaryStructureRepository;
import com.sellspark.SellsHRMS.service.payroll.EmployeeSalaryAssignmentService;

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
public class EmployeeSalaryAssignmentServiceImpl implements EmployeeSalaryAssignmentService {

    private final EmployeeSalaryAssignmentRepository assignmentRepository;
    private final EmployeeRepository employeeRepository;
    private final OrganisationRepository organisationRepository;
    private final SalaryStructureRepository structureRepository;
    private final IncomeTaxSlabRepository taxSlabRepository;

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
            ? taxSlabRepository.findById(dto.getTaxSlabId()).orElseThrow(() ->  new ResourceNotFoundException("Tax Slabs", "Id", dto.getTaxSlabId()))
            : null;// optional

        EmployeeSalaryAssignment assignment = new EmployeeSalaryAssignment();
        log.info("emp slry assign new", assignment);
        mapDtoToEntity(dto, assignment);
        assignment.setEmployee(employee);
        assignment.setOrganisation(org);
        assignment.setSalaryStructure(structure);
        assignment.setTaxSlab(taxSlab);

        assignmentRepository.save(assignment);
        return mapEntityToDto(assignment);
    }

    // ---------------- UPDATE ----------------
    @Override
    public EmployeeSalaryAssignmentDTO updateAssignment(Long id, EmployeeSalaryAssignmentDTO dto) {
        EmployeeSalaryAssignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salary assignment not found"));

        mapDtoToEntity(dto, assignment);

        if (dto.getSalaryStructureId() != null) {
            SalaryStructure structure = structureRepository.findById(dto.getSalaryStructureId())
                    .orElseThrow(() -> new ResourceNotFoundException("Salary structure not found"));
            assignment.setSalaryStructure(structure);
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
        EmployeeSalaryAssignment employeeSalaryAssignment = assignmentRepository.findByEmployeeIdAndActiveTrue(employeeId)
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
        log.info("emp slry assignment enity {}", e);
        return EmployeeSalaryAssignmentDTO.builder()
                .id(e.getId() != null ? e.getId() : null)
                .employeeId(e.getEmployee() != null ? e.getEmployee().getId() : null)
                .employeeName(e.getEmployee() != null ? e.getEmployee().getFirstName() + " " + e.getEmployee().getLastName() : null)
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
                .build();
    }

    private void mapDtoToEntity(EmployeeSalaryAssignmentDTO d, EmployeeSalaryAssignment e) {
        log.info("mapdto to enity slry asign");
        if (d.getBasePay() != null) e.setBasePay(d.getBasePay());
        if (d.getVariablePay() != null) e.setVariablePay(d.getVariablePay());
        e.setRemarks(d.getRemarks());
        e.setEffectiveFrom(d.getEffectiveFrom());
        e.setEffectiveTo(d.getEffectiveTo());
        if (d.getActive() != null) e.setActive(d.getActive());
    }
}
