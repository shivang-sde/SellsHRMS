package com.sellspark.SellsHRMS.repository;

import com.sellspark.SellsHRMS.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByCode(String code);

    List<Permission> findAllByActiveTrue();

    List<Permission> findAllByActiveFalse();

    @Query("SELECT p FROM Permission p WHERE p.module IN :modules AND (p.active = true OR p.active IS NULL)")
    List<Permission> findByModules(@Param("modules") List<String> modules);

    List<Permission> findByActionIn(List<String> actions);
}
