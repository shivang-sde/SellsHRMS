package com.sellspark.SellsHRMS.service;

import java.util.List;

import com.sellspark.SellsHRMS.dto.organisation.DepartmentDTO;

public interface DepartmentService {

    DepartmentDTO createDepartment(DepartmentDTO dto);

    DepartmentDTO getDepartmentById(Long id);

    List<DepartmentDTO> getAllDepartmentsByOrgId(Long orgId);

    DepartmentDTO patchUpdateDepartment(Long id, DepartmentDTO partialUpdate);

    void deleteDepartment(Long id);
}
