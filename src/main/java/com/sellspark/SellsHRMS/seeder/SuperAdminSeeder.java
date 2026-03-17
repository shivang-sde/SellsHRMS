package com.sellspark.SellsHRMS.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sellspark.SellsHRMS.service.SuperAdminService;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(4) // Run last
public class SuperAdminSeeder implements CommandLineRunner {

    private final SuperAdminService superAdminService;

    @Value("${app.super-admin.email:admin@sellspark.com}")
    private String superAdminEmail;

    @Value("${app.super-admin.password:}")
    private String superAdminPassword;

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Check if super admin exists
        if (superAdminService.findByEmail(superAdminEmail).isPresent()) {
            log.info("Super admin already exists, skipping...");
            return;
        }

        log.info("🌱 Creating super admin account...");

        // Different behavior based on environment
        if (isProduction()) {
            // Production: password is REQUIRED
            if (superAdminPassword == null || superAdminPassword.isEmpty()) {
                String errorMsg = "SUPER_ADMIN_PASSWORD environment variable must be set in production!";
                log.error(errorMsg);
                throw new IllegalStateException(errorMsg);
            }
        } else {
            // Test/Development: use default password if not set
            if (superAdminPassword == null || superAdminPassword.isEmpty()) {
                superAdminPassword = "test@123";
                log.warn(
                        "⚠️ Using default test password for super admin. Set SUPER_ADMIN_PASSWORD in .env to override.");
            }
        }

        // Create super admin
        superAdminService.create(superAdminEmail, superAdminPassword);
        log.info("✅ Super admin created with email: {} and role: SUPER_ADMIN", superAdminEmail);
    }

    private boolean isProduction() {
        return activeProfile != null &&
                (activeProfile.contains("prod") ||
                        activeProfile.contains("production"));
    }
}