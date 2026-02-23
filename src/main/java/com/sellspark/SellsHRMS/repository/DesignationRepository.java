package com.sellspark.SellsHRMS.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sellspark.SellsHRMS.dto.common.DropdownOption;
import com.sellspark.SellsHRMS.entity.Designation;

import java.util.List;

public interface DesignationRepository extends JpaRepository<Designation, Long> {

    List<Designation> findByOrganisationId(Long orgId);

    void deleteById(Long id);

    List<Designation> findByDepartment_Id(Long departmentId);

    List<Designation> findByDepartmentId(Long departmentId);

    @Query("""
             SELECT new com.sellspark.SellsHRMS.dto.common.DropdownOption(
             des.id, des.title
             )
             FROM Designation des
             WHERE des.organisation.id = :orgId
             ORDER BY des.title ASC
            """)
    List<DropdownOption> findDesignationDropdown(@Param("orgId") Long orgId);
}
