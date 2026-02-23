package com.sellspark.SellsHRMS.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sellspark.SellsHRMS.dto.common.DropdownOption;
import com.sellspark.SellsHRMS.entity.Department;

import java.util.List;
import com.sellspark.SellsHRMS.entity.Organisation;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    List<Department> findByOrganisationId(Long orgId);

    void deleteById(Long id);

    Long countByOrganisation(Organisation organisation);

    @Query("""
                        SELECT new com.sellspark.SellsHRMS.dto.common.DropdownOption(
                            d.id, d.name
                        )
                             FROM Department d
                             WHERE d.organisation.id = :orgId
                             ORDER BY d.name ASC
            """)
    List<DropdownOption> findDepartmentDropdown(@Param("orgId") Long orgId);

}
