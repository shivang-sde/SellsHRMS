package com.sellspark.SellsHRMS.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sellspark.SellsHRMS.entity.Department;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    List<Department> findByOrganisationId(Long orgId);

    void deleteById(Long id);

    

}
