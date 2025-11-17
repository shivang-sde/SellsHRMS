package com.sellspark.SellsHRMS.config;

import com.sellspark.SellsHRMS.entity.Permission;
import com.sellspark.SellsHRMS.entity.Role;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.repository.PermissionRepository;
import com.sellspark.SellsHRMS.repository.RoleRepository;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder {

    private final PermissionRepository permissionRepo;
    private final RoleRepository roleRepo;
    private final OrganisationRepository organisationRepo;

    @PostConstruct
    public void seedData() {

        // -------------------------------
        // 1. Ensure DEFAULT Global Organisation for SuperAdmin
        // -------------------------------
        Organisation org = organisationRepo.findByName("SYSTEM").orElseGet(() -> {
            Organisation o = Organisation.builder()
                    .name("SYSTEM")
                    .country("IN")
                    .domain("system")
                    .contactEmail("system@sellspark.com")
                    .build();
            return organisationRepo.save(o);
        });

        // -------------------------------
        // 2. Define all permissions
        // -------------------------------
        List<Permission> permissions = List.of(
                createPermission("EMPLOYEE", "VIEW", "EMPLOYEE_VIEW"),
                createPermission("EMPLOYEE", "EDIT", "EMPLOYEE_EDIT"),
                createPermission("EMPLOYEE", "DELETE", "EMPLOYEE_DELETE"),

                createPermission("LEAVE", "VIEW", "LEAVE_VIEW"),
                createPermission("LEAVE", "APPROVE", "LEAVE_APPROVE"),

                

                createPermission("PAYROLL", "VIEW", "PAYROLL_VIEW"),
                createPermission("PAYROLL", "PROCESS", "PAYROLL_PROCESS")
        );

        permissions.forEach(p -> {
            if (permissionRepo.findByCode(p.getCode()).isEmpty()) {
                permissionRepo.save(p);
            }
        });

        // -------------------------------
        // 3. Create SUPER_ADMIN Role (All permissions)
        // -------------------------------
        if (roleRepo.findByName("SUPER_ADMIN").isEmpty()) {
            roleRepo.save(Role.builder()
                    .organisation(org)
                    .name("SUPER_ADMIN")
                    .permissions(new HashSet<>(permissionRepo.findAll()))
                    .build());
        }

        // -------------------------------
        // 4. Create ADMIN Role
        // -------------------------------
        if (roleRepo.findByName("ADMIN").isEmpty()) {
            roleRepo.save(Role.builder()
                    .organisation(org)
                    .name("ADMIN")
                    .permissions(new HashSet<>(List.of(
                            permissionRepo.findByCode("EMPLOYEE_VIEW").get(),
                            permissionRepo.findByCode("EMPLOYEE_EDIT").get(),
                            permissionRepo.findByCode("LEAVE_VIEW").get()
                    )))
                    .build());
        }

        // -------------------------------
        // 5. Create HR Role
        // -------------------------------
        if (roleRepo.findByName("HR").isEmpty()) {
            roleRepo.save(Role.builder()
                    .organisation(org)
                    .name("HR")
                    .permissions(new HashSet<>(List.of(
                            permissionRepo.findByCode("EMPLOYEE_VIEW").get(),
                            permissionRepo.findByCode("EMPLOYEE_EDIT").get(),
                            permissionRepo.findByCode("LEAVE_APPROVE").get()
                    )))
                    .build());
        }

        // -------------------------------
        // 6. Create EMPLOYEE Role (self access)
        // -------------------------------
        if (roleRepo.findByName("EMPLOYEE").isEmpty()) {
            roleRepo.save(Role.builder()
                    .organisation(org)
                    .name("EMPLOYEE")
                    .permissions(new HashSet<>(List.of(
                            permissionRepo.findByCode("EMPLOYEE_VIEW").get()
                    )))
                    .build());
        }

        System.out.println("✔ Roles + Permissions Seeded Successfully");
    }

    private Permission createPermission(String module, String action, String code) {
        return Permission.builder()
                .module(module)
                .action(action)
                .code(code)
                .build();
    }
}
