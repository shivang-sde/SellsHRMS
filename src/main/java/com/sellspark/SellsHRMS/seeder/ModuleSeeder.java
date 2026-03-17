package com.sellspark.SellsHRMS.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sellspark.SellsHRMS.entity.Module;
import com.sellspark.SellsHRMS.repository.ModuleRepository;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(1) // Run first
public class ModuleSeeder implements CommandLineRunner {

    private final ModuleRepository moduleRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (moduleRepository.count() == 0) {
            log.info("🌱 Seeding modules...");

            List<Module> modules = Arrays.asList(
                    createModule("ORG_STRUCTURE", "Departments & Designations"),
                    createModule("ROLE_PERMISSION", "Roles & Permissions"),
                    createModule("EMPLOYEE", "Employee Management"),
                    createModule("ATTENDANCE", "Attendance Management"),
                    createModule("LEAVE", "Leave Management"),
                    createModule("HOLIDAY", "Holiday Management"),
                    createModule("ORG_POLICY", "Organisation Policy"),
                    createModule("PAYROLL", "Payroll Management"),
                    createModule("ASSET_MANAGEMENT", "Asset Management"),
                    createModule("PRODUCTIVITY_MANAGEMENT", "Productivity Management"),
                    createModule("ACCOUNTING", "Accountant Panel"),
                    createModule("ORG_HUB", "Organisation Hub"));

            moduleRepository.saveAll(modules);
            log.info("✅ Seeded {} modules", modules.size());
        } else {
            log.info("Modules already exist, skipping...");
        }
    }

    private Module createModule(String code, String name) {
        Module module = new Module();
        module.setCode(code);
        module.setName(name);
        return module;
    }
}