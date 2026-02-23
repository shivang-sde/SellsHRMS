package com.sellspark.SellsHRMS.service.impl.common;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sellspark.SellsHRMS.dto.common.DropdownOption;
import com.sellspark.SellsHRMS.repository.DepartmentRepository;
import com.sellspark.SellsHRMS.repository.DesignationRepository;
import com.sellspark.SellsHRMS.repository.EmployeeRepository;
import com.sellspark.SellsHRMS.service.common.DropdownService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DropdownServiceImpl implements DropdownService {

    private final DepartmentRepository deptRepo;
    private final DesignationRepository desgRepo;
    private final EmployeeRepository empRepo;

    @Override
    public List<DropdownOption> getDepartmentDropdown(Long orgId) {
        return deptRepo.findDepartmentDropdown(orgId);
    }

    @Override
    public List<DropdownOption> getDesignationDropdown(Long orgId) {
        return desgRepo.findDesignationDropdown(orgId);
    }

    @Override
    public List<DropdownOption> getEmployeeDropdown(Long orgId) {
        return empRepo.findEmployeeDropdown(orgId);
    }
}
