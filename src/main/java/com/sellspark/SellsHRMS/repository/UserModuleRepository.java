package com.sellspark.SellsHRMS.repository;

import com.sellspark.SellsHRMS.entity.UserModule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserModuleRepository extends JpaRepository<UserModule, Long> {
    List<UserModule> findByUserId(Long userId);
}
