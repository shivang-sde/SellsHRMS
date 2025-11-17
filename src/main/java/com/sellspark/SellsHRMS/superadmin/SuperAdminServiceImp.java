package com.sellspark.SellsHRMS.superadmin;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SuperAdminServiceImp implements SuperAdminService {
    
    private final SuperAdminRepositry superAdminRepo;

    @Override
    public SuperAdmin findByEmail(String email) {
        return superAdminRepo.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Super Admin not Found"));
    }

    
}
