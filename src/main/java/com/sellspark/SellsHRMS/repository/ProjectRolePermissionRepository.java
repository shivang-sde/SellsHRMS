package com.sellspark.SellsHRMS.repository;

import com.sellspark.SellsHRMS.entity.ProjectRolePermission;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRolePermissionRepository extends JpaRepository<ProjectRolePermission, Long> {

    List<ProjectRolePermission> findByRoleId(Long roleId);

}
