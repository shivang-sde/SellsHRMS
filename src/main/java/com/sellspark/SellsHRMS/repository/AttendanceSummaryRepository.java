package com.sellspark.SellsHRMS.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import com.sellspark.SellsHRMS.dto.attendance.MonthlySummaryDTO;
import com.sellspark.SellsHRMS.entity.AttendanceSummary;

import jakarta.transaction.Transactional;




@Repository
public interface AttendanceSummaryRepository 
    extends JpaRepository<AttendanceSummary, Long> {
    
    
    Optional<AttendanceSummary> findByEmployeeIdAndAttendanceDate(
        Long employeeId, 
        LocalDate date
    );
    
   
    List<AttendanceSummary> findByOrganisationIdAndAttendanceDate(
        Long organisationId, 
        LocalDate date
    );

    List<AttendanceSummary> findByAttendanceDate(LocalDate attendanceDate);
    

    List<AttendanceSummary> findByEmployeeIdAndAttendanceDateBetween(
        Long employeeId,
        LocalDate startDate,
        LocalDate endDate
    );
    
 
    List<AttendanceSummary> findByOrganisationIdAndAttendanceDateBetween(
        Long organisationId,
        LocalDate startDate,
        LocalDate endDate
    );

    Long countByOrganisationIdAndAttendanceDate(Long organisationId, LocalDate date);

    
 
    @Query("SELECT COUNT(a) FROM AttendanceSummary a " +
           "WHERE a.employee.id = :employeeId " +
           "AND a.status = 'ABSENT' " +
           "AND YEAR(a.attendanceDate) = :year " +
           "AND MONTH(a.attendanceDate) = :month")
    Long countAbsencesByEmployeeAndMonth(
        @Param("employeeId") Long employeeId,
        @Param("year") int year,
        @Param("month") int month
    );
    
    // ✅ Get monthly summary for organisation
    @Query("SELECT new com.sellspark.SellsHRMS.dto.attendance.MonthlySummaryDTO(" +
           "a.employee.id, " +
           "a.employee.firstName, " +
           "a.employee.lastName, " +
           "COUNT(CASE WHEN a.status = 'PRESENT' THEN 1 END), " +
           "COUNT(CASE WHEN a.status = 'ABSENT' THEN 1 END), " +
           "COUNT(CASE WHEN a.status = 'HALF_DAY' THEN 1 END), " +
           "COUNT(CASE WHEN a.status = 'ON_LEAVE' THEN 1 END), " +
           "SUM(a.workHours)) " +
           "FROM AttendanceSummary a " +
           "WHERE a.organisation.id = :orgId " +
           "AND a.attendanceDate BETWEEN :startDate AND :endDate " +
           "GROUP BY a.employee.id, a.employee.firstName, a.employee.lastName")
    List<MonthlySummaryDTO> getMonthlySummary(
        @Param("orgId") Long orgId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate

    );


    @Modifying
@Query("DELETE FROM AttendanceSummary a WHERE a.organisation.id = :orgId AND a.attendanceDate BETWEEN :start AND :end")
int deleteByOrganisationAndDateRange(@Param("orgId") Long orgId,
                                     @Param("start") LocalDate start,
                                     @Param("end") LocalDate end);


    
    // Count present days for employee in a month
    @Query("SELECT COUNT(a) FROM AttendanceSummary a " +
           "WHERE a.employee.id = :employeeId " +
           "AND a.status IN ('PRESENT', 'HALF_DAY') " +
           "AND YEAR(a.attendanceDate) = :year " +
           "AND MONTH(a.attendanceDate) = :month")
    Long countPresentDaysByEmployeeAndMonth(
        @Param("employeeId") Long employeeId,
        @Param("year") int year,
        @Param("month") int month
    );


    @Modifying
    @Transactional
    @Query("DELETE FROM AttendanceSummary a WHERE a.organisation.id = :orgId")
    void deleteByOrganisationId(@Param("orgId") Long orgId);


}