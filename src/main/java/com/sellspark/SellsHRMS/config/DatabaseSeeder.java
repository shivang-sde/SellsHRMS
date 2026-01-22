// package com.sellspark.SellsHRMS.config;

// import com.sellspark.SellsHRMS.entity.Permission;
// import com.sellspark.SellsHRMS.entity.Role;
// import com.sellspark.SellsHRMS.entity.Organisation;
// import com.sellspark.SellsHRMS.repository.PermissionRepository;
// import com.sellspark.SellsHRMS.repository.RoleRepository;
// import com.sellspark.SellsHRMS.repository.OrganisationRepository;

// import jakarta.annotation.PostConstruct;
// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Component;

// import java.util.HashSet;
// import java.util.List;

// @Component
// @RequiredArgsConstructor
// public class DatabaseSeeder {

// private final PermissionRepository permissionRepo;
// private final RoleRepository roleRepo;
// private final OrganisationRepository organisationRepo;

// @PostConstruct
// public void seedData() {

// // -------------------------------
// // 1. Ensure DEFAULT Global Organisation for SuperAdmin
// // -------------------------------
// Organisation org = organisationRepo.findByName("SYSTEM").orElseGet(() -> {
// Organisation o = Organisation.builder()
// .name("SYSTEM")
// .country("IN")
// .domain("system")
// .contactEmail("system@sellspark.com")
// .build();
// return organisationRepo.save(o);
// });

// // -------------------------------
// // 2. Define all permissions
// // -------------------------------
// List<Permission> permissions = List.of(
// createPermission("EMPLOYEE", "VIEW", "EMPLOYEE_VIEW"),
// createPermission("EMPLOYEE", "EDIT", "EMPLOYEE_EDIT"),
// createPermission("EMPLOYEE", "DELETE", "EMPLOYEE_DELETE"),

// createPermission("LEAVE", "VIEW", "LEAVE_VIEW"),
// createPermission("LEAVE", "APPROVE", "LEAVE_APPROVE"),

// createPermission("PAYROLL", "VIEW", "PAYROLL_VIEW"),
// createPermission("PAYROLL", "PROCESS", "PAYROLL_PROCESS"));

// permissions.forEach(p -> {
// if (permissionRepo.findByCode(p.getCode()).isEmpty()) {
// permissionRepo.save(p);
// }
// });

// // -------------------------------
// // 3. Create SUPER_ADMIN Role (All permissions)
// // -------------------------------
// if (roleRepo.findByName("SUPER_ADMIN").isEmpty()) {
// roleRepo.save(Role.builder()
// .organisation(org)
// .name("SUPER_ADMIN")
// .permissions(new HashSet<>(permissionRepo.findAll()))
// .build());
// }

// // -------------------------------
// // 4. Create ADMIN Role
// // -------------------------------
// if (roleRepo.findByName("ORG_ADMIN").isEmpty()) {
// roleRepo.save(Role.builder()
// .organisation(org)
// .name("ORG_ADMIN")
// .permissions(new HashSet<>(List.of(
// permissionRepo.findByCode("EMPLOYEE_VIEW").get(),
// permissionRepo.findByCode("EMPLOYEE_EDIT").get(),
// permissionRepo.findByCode("LEAVE_VIEW").get())))
// .build());
// }

// // -------------------------------
// // 5. Create HR Role
// // -------------------------------
// if (roleRepo.findByName("HR").isEmpty()) {
// roleRepo.save(Role.builder()
// .organisation(org)
// .name("HR")
// .permissions(new HashSet<>(List.of(
// permissionRepo.findByCode("EMPLOYEE_VIEW").get(),
// permissionRepo.findByCode("EMPLOYEE_EDIT").get(),
// permissionRepo.findByCode("LEAVE_APPROVE").get())))
// .build());
// }

// // -------------------------------
// // 6. Create EMPLOYEE Role (self access)
// // -------------------------------
// if (roleRepo.findByName("EMPLOYEE").isEmpty()) {
// roleRepo.save(Role.builder()
// .organisation(org)
// .name("EMPLOYEE")
// .permissions(new HashSet<>(List.of(
// permissionRepo.findByCode("EMPLOYEE_VIEW").get())))
// .build());
// }

// System.out.println("✔ Roles + Permissions Seeded Successfully");
// }

// private Permission createPermission(String module, String action, String
// code) {
// return Permission.builder()
// .module(module)
// .action(action)
// .code(code)
// .build();
// }
// }


package com.sellspark.SellsHRMS.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sellspark.SellsHRMS.entity.Employee;
import com.sellspark.SellsHRMS.repository.EmployeeRepository;
import com.sellspark.SellsHRMS.service.LeaveService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseSeeder {

    // @Autowired
    // private EmployeeRepository employeeRepository;

    // @Autowired
    // private LeaveService leaveService;

    // @PostConstruct
    // public void seedLeavesForExistingEmployees() {
    //     log.info("Starting leave balance initialization for existing employees...");
    //     Iterable<Employee> employees = employeeRepository.findAll();
    //     for (Employee employee : employees) {
    //         try {
    //             String leaveYear = leaveService.getCurrentLeaveYear(employee.getOrganisation().getId());
    //             leaveService.initializeLeaveBalancesForEmployee(employee.getId(), employee.getOrganisation().getId(), leaveYear);
    //             log.info("Initialized leave balances for employee ID: " + employee.getId());
    //         } catch (Exception e) {
    //             log.error("Failed to initialize leave balances for employee ID: " + employee.getId(), e);
    //         }
    //     }
    // }
}
