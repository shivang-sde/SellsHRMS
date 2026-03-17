package com.sellspark.SellsHRMS.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sellspark.SellsHRMS.entity.Role;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.Permission;
import com.sellspark.SellsHRMS.repository.RoleRepository;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;
import com.sellspark.SellsHRMS.repository.PermissionRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(3) // Run after permissions
public class RoleSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final OrganisationRepository organisationRepository;
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Get root organisation (ID 1)
        Organisation rootOrg = organisationRepository.findById(1L)
                .orElseGet(() -> {
                    Organisation org = new Organisation();
                    org.setId(1L);
                    org.setName("Root Organisation");
                    org.setEmpPrefix("ROOT");
                    return organisationRepository.save(org);
                });

        // Check if roles already exist for root org
        if (roleRepository.countByOrganisationId(1L) == 0) {
            log.info("🌱 Seeding roles...");

            // 1. SUPER_ADMIN Role (all permissions)
            createRole("SUPER_ADMIN", "Super Administrator - Full system access", rootOrg,
                    permissionRepository.findAll());

            // 2. ORG_ADMIN Role (organisation management permissions)
            createRole("ORG_ADMIN", "Organisation Administrator - Manage org settings", rootOrg,
                    permissionRepository.findByModules(
                            Arrays.asList("EMPLOYEE", "LEAVE", "ATTENDANCE", "PAYROLL", "ORG_HUB", "ROLE_PERMISSION")));

            // 3. ACCOUNTANT Role (financial permissions)
            createRole("ACCOUNTANT", "Accountant - Financial operations", rootOrg,
                    permissionRepository.findByModules(
                            Arrays.asList("PAYROLL", "ACCOUNTING")));

            // 4. EMPLOYEE Role (basic permissions)
            createRole("EMPLOYEE", "Employee - Basic self-service", rootOrg,
                    permissionRepository.findByActionIn(
                            Arrays.asList("VIEW_SELF", "APPLY", "MARK_MANUAL")));

            log.info("✅ Seeded 4 roles: SUPER_ADMIN, ORG_ADMIN, ACCOUNTANT, EMPLOYEE");
        } else {
            log.info("Roles already exist, skipping...");
        }
    }

    private void createRole(String name, String description, Organisation org, List<Permission> permissions) {
        Role role = new Role();
        role.setName(name);
        role.setDescription(description);
        role.setOrganisation(org);
        role.setPermissions(new HashSet<>(permissions));
        roleRepository.save(role);
        log.debug("Created role: {}", name);
    }
}