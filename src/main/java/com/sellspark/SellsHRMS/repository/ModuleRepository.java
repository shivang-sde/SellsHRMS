package com.sellspark.SellsHRMS.repository;

import com.sellspark.SellsHRMS.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ModuleRepository extends JpaRepository<Module, Long> {
    Optional<Module> findByCode(String code);
}
