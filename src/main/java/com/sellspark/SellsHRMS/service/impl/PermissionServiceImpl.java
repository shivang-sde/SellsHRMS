package com.sellspark.SellsHRMS.service.impl;

import com.sellspark.SellsHRMS.entity.Permission;
import com.sellspark.SellsHRMS.repository.PermissionRepository;
import com.sellspark.SellsHRMS.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepo;

    @Override
    public List<Permission> getAllPermissions() {
        return permissionRepo.findAll();
    }

    @Override
    public Permission createPermission(Permission permission) {
        if (permissionRepo.findByCode(permission.getCode()).isPresent()) {
            throw new IllegalArgumentException("Permission with code already exists: " + permission.getCode());
        }
        return permissionRepo.save(permission);
    }
}
