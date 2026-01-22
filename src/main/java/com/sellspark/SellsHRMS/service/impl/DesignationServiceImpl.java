package com.sellspark.SellsHRMS.service.impl;

import com.sellspark.SellsHRMS.dto.organisation.DesignationDTO;
import com.sellspark.SellsHRMS.entity.Department;
import com.sellspark.SellsHRMS.entity.Designation;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.Role;
import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
import com.sellspark.SellsHRMS.repository.DepartmentRepository;
import com.sellspark.SellsHRMS.repository.DesignationRepository;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;
import com.sellspark.SellsHRMS.repository.RoleRepository;
import com.sellspark.SellsHRMS.service.DesignationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DesignationServiceImpl implements DesignationService {

    private final DesignationRepository designationRepo;
    private final DepartmentRepository departmentRepo;
    private final RoleRepository roleRepo;
    private final OrganisationRepository organisationRepo;

    @Override
    public DesignationDTO createDesignation(DesignationDTO dto) {
        Organisation org = organisationRepo.findById(dto.getOrgId())
                .orElseThrow(() -> new ResourceNotFoundException("Organisation", "id", dto.getOrgId()));

        Department dept = departmentRepo.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", dto.getDepartmentId()));

        Designation designation = Designation.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .department(dept)
                .organisation(org)
                .build();

        if (dto.getRoleId() != null) {
            Role role = roleRepo.findById(dto.getRoleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "id", dto.getRoleId()));

            designation.setRole(role);
            role.setDesignation(designation); // keep both sides in sync
        }

        Designation saved = designationRepo.save(designation);
        return mapToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DesignationDTO getDesignationById(Long id) {
        Designation designation = designationRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Designation", "id", id));

        if (designation.getRole() != null) {
            log.debug("Designation {} -> Role [{} - {}]",
                    designation.getId(),
                    designation.getRole().getId(),
                    designation.getRole().getName());
        } else {
            log.debug("Designation {} has no role assigned.", designation.getId());
        }

        return mapToDTO(designation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DesignationDTO> getAllDesignationsByOrgId(Long orgId) {
        return designationRepo.findByOrganisationId(orgId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DesignationDTO> getDesignationsByDeptId(Long deptId) {
    return designationRepo.findByDepartmentId(deptId)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
}

    @Override
    public DesignationDTO patchUpdateDesignation(Long id, DesignationDTO dto) {
        Designation existing = designationRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Designation", "id", id));

        log.debug("Updating Designation ID {} with DTO: {}", id, dto);

        if (dto.getTitle() != null)
            existing.setTitle(dto.getTitle());

        if (dto.getDescription() != null)
            existing.setDescription(dto.getDescription());

        if (dto.getDepartmentId() != null) {
            Department dept = departmentRepo.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department", "id", dto.getDepartmentId()));
            existing.setDepartment(dept);
        }

        if (dto.getRoleId() != null) {
            Role role = roleRepo.findById(dto.getRoleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "id", dto.getRoleId()));

            existing.setRole(role);
            role.setDesignation(existing); // keep both sides consistent
        }

        Designation updated = designationRepo.save(existing);
        return mapToDTO(updated);
    }

    @Override
    public void deleteDesignation(Long id) {
        Designation designation = designationRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Designation", "id", id));

        // Break relation before delete (avoid constraint violation)
        if (designation.getRole() != null) {
            Role linkedRole = designation.getRole();
            linkedRole.setDesignation(null);
            designation.setRole(null);
            roleRepo.save(linkedRole);
        }

        designationRepo.delete(designation);
        log.info("Deleted designation with ID {}", id);
    }

    // Utility mappers
    private DesignationDTO mapToDTO(Designation designation) {
        DesignationDTO dto = new DesignationDTO();
        dto.setId(designation.getId());
        dto.setTitle(designation.getTitle());
        dto.setDescription(designation.getDescription());
        dto.setOrgId(designation.getOrganisation().getId());
        dto.setDepartmentId(designation.getDepartment().getId());
        dto.setDepartmentName(designation.getDepartment().getName());

        if (designation.getRole() != null) {
            dto.setRoleId(designation.getRole().getId());
            dto.setRoleName(designation.getRole().getName());
        }

        return dto;
    }
}