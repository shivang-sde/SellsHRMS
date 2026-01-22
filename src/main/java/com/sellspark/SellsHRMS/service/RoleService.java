package com.sellspark.SellsHRMS.service;

import com.sellspark.SellsHRMS.dto.role.RoleResponse;
import com.sellspark.SellsHRMS.entity.Role;


import java.util.List;


public interface RoleService {
    RoleResponse createRole(Role role, Long organisationId);
    RoleResponse updateRole(Long id, Role updatedRole);
    void deleteRole(Long id);
    List<RoleResponse> getRolesByOrganisation(Long organisationId);
    RoleResponse getRoleResponseById(Long id);
}
 