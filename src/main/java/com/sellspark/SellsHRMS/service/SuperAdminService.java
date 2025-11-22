package com.sellspark.SellsHRMS.service;

import java.util.List;
import java.util.Optional;

import com.sellspark.SellsHRMS.entity.SuperAdmin;

public interface SuperAdminService {
    Optional<SuperAdmin> findByEmail(String email);

    SuperAdmin create(String email, String rawPassword);

    List<SuperAdmin> getAll();

    void delete(Long id);
}
