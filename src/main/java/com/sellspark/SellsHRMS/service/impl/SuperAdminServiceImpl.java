package com.sellspark.SellsHRMS.service.impl;

import com.sellspark.SellsHRMS.entity.SuperAdmin;
import com.sellspark.SellsHRMS.repository.SuperAdminRepository;
import com.sellspark.SellsHRMS.service.SuperAdminService;
import com.sellspark.SellsHRMS.service.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SuperAdminServiceImpl implements SuperAdminService {

    private final SuperAdminRepository superAdminRepository;
    private final UserService userService;

    @Override
    public Optional<SuperAdmin> findByEmail(String email) {

        return superAdminRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public SuperAdmin create(String email, String rawPassword) {
        userService.createUser(
                email,
                rawPassword,
                "SUPER_ADMIN",
                "SUPER_ADMIN",
                1L // root organisation
        );
        SuperAdmin superAdmin = new SuperAdmin();
        superAdmin.setEmail(email);
        superAdmin.setIsActive(true);

        return superAdminRepository.save(superAdmin);
    }

    @Override
    public List<SuperAdmin> getAll() {
        return superAdminRepository.findAll();
    }

    // @Override
    // public SuperAdmin update(Long id, String newEmail) {

    // return superAdminRepo.findById(id)
    // .map(sa -> {
    // sa.setEmail(newEmail);
    // return superAdminRepo.save(sa);
    // })
    // .orElseThrow(() -> new RuntimeException("SuperAdmin not found"));
    // }

    @Override
    public void delete(Long id) {
        superAdminRepository.deleteById(id);
    }
}
