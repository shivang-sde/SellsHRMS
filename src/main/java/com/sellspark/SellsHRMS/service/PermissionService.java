package com.sellspark.SellsHRMS.service;

import com.sellspark.SellsHRMS.entity.Permission;
import java.util.List;

public interface PermissionService {
    List<Permission> getAllPermissions();
    Permission createPermission(Permission permission);
}
