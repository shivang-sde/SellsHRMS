package com.sellspark.SellsHRMS.repository;

import com.sellspark.SellsHRMS.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByCode(String code);

    List<Permission> findAllByActiveTrue();

    List<Permission> findAllByActiveFalse();
}
