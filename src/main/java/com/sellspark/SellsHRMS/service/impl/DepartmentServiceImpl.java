package com.sellspark.SellsHRMS.service.impl;

import com.sellspark.SellsHRMS.dto.organisation.DepartmentDTO;
import com.sellspark.SellsHRMS.entity.Department;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.repository.DepartmentRepository;
import com.sellspark.SellsHRMS.service.DepartmentService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepo;

    @Override
    public DepartmentDTO createDepartment(DepartmentDTO dto) {
        Department dept = new Department();
        dept.setName(dto.getName());
        dept.setDescription(dto.getDescription());

        // attach organisation by id
        Organisation org = new Organisation();
        org.setId(dto.getOrgId());
        dept.setOrganisation(org);

        Department saved = departmentRepo.save(dept);
        return mapToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentDTO getDepartmentById(Long id) {
        Department dept = departmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        return mapToDTO(dept);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentDTO> getAllDepartmentsByOrgId(Long orgId) {
        return departmentRepo.findByOrganisationId(orgId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public DepartmentDTO patchUpdateDepartment(Long id, DepartmentDTO dto) {
        Department existing = departmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getDescription() != null) existing.setDescription(dto.getDescription());

        Department updated = departmentRepo.save(existing);
        return mapToDTO(updated);
    }

    @Override
    public void deleteDepartment(Long id) {
        departmentRepo.deleteById(id);
    }

    private DepartmentDTO mapToDTO(Department dept) {
        return DepartmentDTO.builder()
                .id(dept.getId())
                .name(dept.getName())
                .description(dept.getDescription())
                .orgId(dept.getOrganisation().getId())
                .orgName(dept.getOrganisation().getName())
                .build();
    }
}
