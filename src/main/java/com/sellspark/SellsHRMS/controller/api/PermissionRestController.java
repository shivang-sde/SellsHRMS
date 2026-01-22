package com.sellspark.SellsHRMS.controller.api;

import com.sellspark.SellsHRMS.entity.Permission;
import com.sellspark.SellsHRMS.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionRestController {

    private final PermissionService permissionService;

    @GetMapping
    public List<Permission> getAll() {
        return permissionService.getAllPermissions();
    }

    @PostMapping
    public Permission create(@RequestBody Permission permission) {
        return permissionService.createPermission(permission);
    }
}
