package com.sellspark.SellsHRMS.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sellspark.SellsHRMS.entity.SuperAdmin;
import com.sellspark.SellsHRMS.repository.SuperAdminRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SuperAdminAuthService {

    private final SuperAdminRepository repository;
    private final PasswordEncoder encoder;

    public SuperAdmin login(String email, String password) {
        SuperAdmin superAdmin = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!encoder.matches(password, superAdmin.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        return superAdmin;
    }
}
