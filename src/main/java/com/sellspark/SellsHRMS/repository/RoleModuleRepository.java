package com.sellspark.SellsHRMS.repository;

import com.sellspark.SellsHRMS.entity.RoleModule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoleModuleRepository extends JpaRepository<RoleModule, Long> {
    List<RoleModule> findByRoleId(Long roleId);
}
