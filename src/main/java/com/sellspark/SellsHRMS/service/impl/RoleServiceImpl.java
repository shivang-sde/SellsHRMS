package com.sellspark.SellsHRMS.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sellspark.SellsHRMS.dto.permission.PermissionDTO;
import com.sellspark.SellsHRMS.dto.role.RoleResponse;
import com.sellspark.SellsHRMS.entity.Designation;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.Role;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;
import com.sellspark.SellsHRMS.repository.PermissionRepository;
import com.sellspark.SellsHRMS.repository.RoleRepository;
import com.sellspark.SellsHRMS.service.RoleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {
    
    private final RoleRepository roleRepo;
    private final OrganisationRepository orgRepo;
    private final PermissionRepository permissionRepo;

    @Override
    public RoleResponse createRole(Role role, Long organisationId) {
        Organisation org = orgRepo.findById(organisationId)
                .orElseThrow(() -> new RuntimeException("Organisation not found"));

        role.setOrganisation(org);
        return mapToRoleResponse(roleRepo.save(role));
    }

    @Override
    public RoleResponse updateRole(Long id, Role updatedRole) {
        Role existingRole = roleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        existingRole.setName(updatedRole.getName());
        existingRole.setDescription(updatedRole.getDescription());
        existingRole.setPermissions(updatedRole.getPermissions());

        return mapToRoleResponse(roleRepo.save(existingRole));
    }

    @Override
    public void deleteRole(Long id) {
        roleRepo.deleteById(id);
    }

    @Override
public List<RoleResponse> getRolesByOrganisation(Long organisationId) {
    return roleRepo.findByOrganisationId(organisationId).stream()
        .map(role -> mapToRoleResponse(role))
        .collect(Collectors.toList());
}

    @Override
    public RoleResponse getRoleResponseById(Long id) {
    Role role = roleRepo.findById(id)
        .orElseThrow(() -> new RuntimeException("Role not found"));

    Designation desg = role.getDesignation();

    log.info("desg {}, desig title {}, desig id {}", desg, desg.getTitle(), desg.getId());
    
    return mapToRoleResponse(role);
}

private RoleResponse mapToRoleResponse(Role role) {
    if (role == null) return null;

    RoleResponse.RoleResponseBuilder builder = RoleResponse.builder()
            .id(role.getId())
            .name(role.getName())
            .description(role.getDescription())
            .organisationId(
                    role.getOrganisation() != null ? role.getOrganisation().getId() : null
            )
            .permissions(
                    role.getPermissions() != null
                            ? role.getPermissions().stream()
                                    .map(permission -> PermissionDTO.builder()
                                            .id(permission.getId())
                                            .module(permission.getModule())
                                            .action(permission.getAction())
                                            .code(permission.getCode())
                                            .build())
                                    .toList()
                            : List.of()
            );
            log.info("role desg {}", role.getDesignation().getTitle());
            if (role.getDesignation() != null) {
                log.info("role -> desg {}", role.getName() );
            builder.designationId(role.getDesignation().getId());
            builder.designationTitle(role.getDesignation().getTitle());
        }
         return builder.build();
}


}
