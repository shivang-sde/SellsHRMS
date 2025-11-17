package com.sellspark.SellsHRMS.security;

import com.sellspark.SellsHRMS.superadmin.SuperAdminRepositry;
import com.sellspark.SellsHRMS.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardModuleService {

    private final SuperAdminRepositry superAdminRepo;
    private final UserRepository userRepo;

    public List<String> getModulesForUser(String email) {

        if (superAdminRepo.findByEmail(email).isPresent()) {
            return List.of(
                    "superadmin",
                    "organisation",
                    "employees",
                    "attendance",
                    "leave",
                    "payroll");
        }

        var user = userRepo.findByEmail(email).orElseThrow();

        String role = user.getRole().getName();

        switch (role) {

            case "ADMIN":
                return List.of("organisation", "employees", "attendance", "leave", "payroll");

            case "HR":
                return List.of("employees", "attendance", "leave");

            case "EMPLOYEE":
                return List.of("profile", "attendance", "leave");

            default:
                return List.of("profile");
        }
    }
}
