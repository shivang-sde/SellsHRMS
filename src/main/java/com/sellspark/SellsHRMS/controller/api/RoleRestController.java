package com.sellspark.SellsHRMS.controller.api;

import com.sellspark.SellsHRMS.dto.role.RoleResponse;
import com.sellspark.SellsHRMS.entity.Role;
import com.sellspark.SellsHRMS.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleRestController {

    private final RoleService roleService;

    @PostMapping("/org/{orgId}")
    public ResponseEntity<RoleResponse> createRole(@PathVariable Long orgId, @RequestBody Role role) {
        RoleResponse created = roleService.createRole(role, orgId);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleResponse> updateRole(@PathVariable Long id, @RequestBody Role role) {
        RoleResponse updated = roleService.updateRole(id, role);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/org/{orgId}")
    public ResponseEntity<List<RoleResponse>> getRolesByOrg(@PathVariable Long orgId) {
        return ResponseEntity.ok(roleService.getRolesByOrganisation(orgId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> getRole(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getRoleResponseById(id));
    }
}
