package com.sellspark.SellsHRMS.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sellspark.SellsHRMS.entity.Department;
import com.sellspark.SellsHRMS.entity.Designation;

import java.util.List;

public interface DesignationRepository extends JpaRepository<Designation, Long> {

    List<Designation> findByOrganisationId(Long orgId);

    void deleteById(Long id);

    List<Designation> findByDepartment_Id(Long departmentId);

    List<Designation> findByDepartmentId(Long departmentId);

}
