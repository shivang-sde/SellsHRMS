package com.sellspark.SellsHRMS.service.common;

import java.util.List;

import com.sellspark.SellsHRMS.dto.common.DropdownOption;

public interface DropdownService {

    List<DropdownOption> getDepartmentDropdown(Long orgId);

    List<DropdownOption> getDesignationDropdown(Long orgId);

    List<DropdownOption> getEmployeeDropdown(Long orgId);

    // List<DropdownOption> getRoleDropdown(Long orgId);
}
