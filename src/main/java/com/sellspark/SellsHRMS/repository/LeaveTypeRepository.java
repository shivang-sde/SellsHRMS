package com.sellspark.SellsHRMS.repository;

import com.sellspark.SellsHRMS.entity.LeaveType;
import com.sellspark.SellsHRMS.entity.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {
    
    
    List<LeaveType> findByOrganisationAndIsActiveTrue(Organisation organisation);
    
    List<LeaveType> findByOrganisation(Organisation organisation);

    // Get visible leave types for an organization
List<LeaveType> findByOrganisationIdAndVisibleToEmployeesTrue(Long orgId);

// Get leave types by gender applicability
List<LeaveType> findByOrganisationIdAndApplicableGenderIn(Long orgId, List<String> genders);
    
    Optional<LeaveType> findByIdAndOrganisation(Long id, Organisation organisation);
    
    @Query("SELECT lt FROM LeaveType lt WHERE lt.organisation = :org AND lt.visibleToEmployees = true AND lt.isActive = true")
    List<LeaveType> findVisibleLeaveTypesForEmployees(@Param("org") Organisation organisation);
    
    @Query("SELECT lt FROM LeaveType lt WHERE lt.organisation = :org AND lt.isActive = true AND " +
           "(lt.applicableGender = 'ALL' OR lt.applicableGender = :gender)")
    List<LeaveType> findApplicableLeaveTypes(@Param("org") Organisation organisation, 
                                            @Param("gender") String gender);
    
    boolean existsByOrganisationAndName(Organisation organisation, String name);
}