package com.sellspark.SellsHRMS.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sellspark.SellsHRMS.entity.PunchInOut;

import jakarta.transaction.Transactional;

public interface PunchInOutRepository extends JpaRepository<PunchInOut, Long> {
    
  
    @Query("SELECT p FROM PunchInOut p WHERE p.employee.id = :employeeId " + "AND DATE(p.punchIn) = :date ORDER BY p.punchIn DESC" )
    List<PunchInOut> findByEmployeeAndDate(@Param("employeeId") Long employeeId, @Param("date") LocalDate date);


    @Modifying
    @Query("DELETE FROM PunchInOut p WHERE p.organisation.id = :orgId AND p.punchIn BETWEEN :start AND :end")
    int deleteByOrganisationAndDateRange(@Param("orgId") Long orgId,
                                     @Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end);



    @Query("SELECT p FROM PunchInOut p WHERE  p.employee.id = :employeeId " + "AND p.punchOut IS NULL ORDER BY p.punchIn DESC ")
    Optional<PunchInOut> findActivePunchByEmployee(@Param("employeeId") Long employeeId );

    
    @Query("SELECT p FROM PunchInOut p WHERE p.organisation.id = :orgId " + "AND DATE(p.punchIn) = :date ORDER BY p.punchIn DESC ")
    List<PunchInOut> findByOrganisationAndDate(@Param("orgId") Long orgId,  @Param("date") LocalDate date);
  
    @Query("SELECT p FROM PunchInOut p WHERE p.employee.id = :employeeId " 
        + "AND DATE(p.punchIn) BETWEEN :startDate And :endDate " + "ORDER BY p.punchIn DESC"
    )
    List<PunchInOut> findByEmployeeBetweenDates(@Param("employeeId") Long employeeId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


   

    @Modifying
    @Transactional
    @Query("DELETE FROM PunchInOut p WHERE p.organisation.id = :orgId")
    void deleteByOrganisationId(@Param("orgId") Long orgId);

}
