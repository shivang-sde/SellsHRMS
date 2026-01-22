package com.sellspark.SellsHRMS.repository;

import com.sellspark.SellsHRMS.entity.Employee;
import com.sellspark.SellsHRMS.entity.Employee.EmployeeStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmployeeCode(String employeeCode);

    Optional<Employee> findByEmail(String email);

    List<Employee> findByOrganisationIdAndDeletedFalse(Long orgId);

    List<Employee> findByOrganisationIdAndDeletedFalseAndStatus(Long orgId, EmployeeStatus status );

    Optional<Employee> findByIdAndOrganisationId(Long id, Long organisationId);

    List<Employee> findByReportingTo(Employee reportingTo);


     // Find all by organisation (excluding deleted)
    List<Employee> findByOrganisationIdAndDeleted(Long organisationId, boolean deleted);

    // Find by organisation and status
    List<Employee> findByOrganisationIdAndStatus(
        Long organisationId, 
        Employee.EmployeeStatus status
    );

    // Find active employees by organisation
    List<Employee> findByOrganisationIdAndStatusAndDeleted(
        Long organisationId, 
        Employee.EmployeeStatus status,
        boolean deleted
    );

    List<Employee> findByStatusAndDeletedFalse(EmployeeStatus status);

    Optional<Employee> findByIdAndDeletedFalse(Long id);

    int countByOrganisationId(Long organisationId);
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.organisation.id = :orgId AND e.deleted = false")
long countByOrganisationIdAndDeletedFalse(@Param("orgId") Long orgId);


@Query("SELECT e FROM Employee e " +
       "WHERE e.organisation.id = :orgId " +
       "AND e.status = 'ACTIVE' " +
       "AND e.dob BETWEEN :startDate AND :endDate")
List<Employee> findUpcomingBirthdays(@Param("orgId") Long orgId,
                                     @Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);

@Query("SELECT e FROM Employee e " +
       "WHERE e.organisation.id = :orgId " +
       "AND e.status = 'ACTIVE' " +
       "AND e.dateOfJoining BETWEEN :startDate AND :endDate")
List<Employee> findUpcomingWorkAnniversaries(@Param("orgId") Long orgId,
                                             @Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);



}
