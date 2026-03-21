// package com.sellspark.SellsHRMS.seeder;

// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.core.annotation.Order;
// import org.springframework.stereotype.Component;
// import org.springframework.transaction.annotation.Transactional;

// import com.sellspark.SellsHRMS.entity.Permission;
// import com.sellspark.SellsHRMS.repository.PermissionRepository;
// import com.sellspark.SellsHRMS.repository.ModuleRepository;
// import com.sellspark.SellsHRMS.entity.Module;

// import java.util.Arrays;
// import java.util.List;

// @Slf4j
// @Component
// @RequiredArgsConstructor
// @Order(2) // Run after modules
// public class PermissionSeeder implements CommandLineRunner {

// private final PermissionRepository permissionRepository;
// private final ModuleRepository moduleRepository;

// @Override
// @Transactional
// public void run(String... args) throws Exception {
// if (permissionRepository.count() == 0) {
// log.info("🌱 Seeding permissions...");

// // Employee Module Permissions
// createPermissions("EMPLOYEE", Arrays.asList(
// new PermissionData("VIEW_SELF", "EMPLOYEE_VIEW_SELF"),
// new PermissionData("VIEW_TEAM", "EMPLOYEE_VIEW_TEAM"),
// new PermissionData("VIEW_ALL", "EMPLOYEE_VIEW_ALL"),
// new PermissionData("CREATE", "EMPLOYEE_CREATE"),
// new PermissionData("EDIT", "EMPLOYEE_EDIT")));

// // Leave Module Permissions
// createPermissions("LEAVE", Arrays.asList(
// new PermissionData("APPLY", "LEAVE_APPLY"),
// new PermissionData("VIEW_SELF", "LEAVE_VIEW_SELF"),
// new PermissionData("VIEW_TEAM", "LEAVE_VIEW_TEAM"),
// new PermissionData("VIEW_ALL", "LEAVE_VIEW_ALL"),
// new PermissionData("APPROVE", "LEAVE_APPROVE"),
// new PermissionData("EDIT", "LEAVE_EDIT")));

// // Attendance Module Permissions
// createPermissions("ATTENDANCE", Arrays.asList(
// new PermissionData("VIEW_SELF", "ATTENDANCE_VIEW_SELF"),
// new PermissionData("VIEW_ALL", "ATTENDANCE_VIEW_ALL"),
// new PermissionData("EDIT", "ATTENDANCE_EDIT")));

// // Payroll Module Permissions
// createPermissions("PAYROLL", Arrays.asList(
// new PermissionData("VIEW", "PAYROLL_VIEW")));

// // ORG_HUB Module Permissions
// createPermissions("ORG_HUB", Arrays.asList(
// new PermissionData("VIEW", "ORGHUB_VIEW"),
// new PermissionData("CREATE", "ORGHUB_CREATE"),
// new PermissionData("DELETE", "ORGHUB_DELETE"),
// new PermissionData("UPDATE", "ORGHUB_UPDATE")));

// // Asset Management Permissions
// createPermissions("ASSET_MANAGEMENT", Arrays.asList(
// new PermissionData("MANAGEMENT", "ASSEST_MANAGEMENT")));

// log.info("✅ Seeded all permissions");
// } else {
// log.info("Permissions already exist, skipping...");
// }
// }

// private void createPermissions(String moduleCode, List<PermissionData>
// permissions) {
// Module module = moduleRepository.findByCode(moduleCode)
// .orElseThrow(() -> new RuntimeException("Module not found: " + moduleCode));

// for (PermissionData data : permissions) {
// Permission permission = new Permission();
// permission.setAction(data.action);
// permission.setCode(data.code);
// permission.setActive(true);
// permission.setModule(moduleCode);
// permissionRepository.save(permission);
// }
// }

// @lombok.AllArgsConstructor
// private static class PermissionData {
// String action;
// String code;
// }
// }