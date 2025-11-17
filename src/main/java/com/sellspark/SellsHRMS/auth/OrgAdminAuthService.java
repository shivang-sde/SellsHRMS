package com.sellspark.SellsHRMS.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sellspark.SellsHRMS.entity.OrganisationAdmin;
import com.sellspark.SellsHRMS.repository.OrganisationAdminRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrgAdminAuthService {

    private final OrganisationAdminRepository repository;
    private final PasswordEncoder encoder;

    public OrganisationAdmin login(String email, String password) {
        OrganisationAdmin admin = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!encoder.matches(password, admin.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        return admin;
    }
}
