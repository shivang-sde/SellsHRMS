package com.sellspark.SellsHRMS.repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sellspark.SellsHRMS.entity.Employee;
import com.sellspark.SellsHRMS.entity.Leave;
import com.sellspark.SellsHRMS.entity.LeaveType;
import com.sellspark.SellsHRMS.entity.Organisation;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {

    List<Leave> findByEmployeeId(Long employeeId);

    List<Leave> findByEmployeeAndLeaveYear(Employee employee, String leaveYear);

    List<Leave> findByOrganisationAndLeaveStatus(Organisation org, Leave.LeaveStatus status);

    List<Leave> findByOrganisationIdAndEmployeeIdIn(Long orgId, Collection<Long> employeeIds);

    @Query("SELECT COALESCE(SUM(l.leaveDays), 0) FROM Leave l WHERE l.employee.id = :employeeId " +
            "AND l.leaveType.id = :leaveTypeId " +
            "AND l.leaveYear = :leaveYear " +
            "AND l.leaveStatus = 'APPROVE'")
    Double sumApprovedLeaveDays(
            @Param("employeeId") Long employeeId,
            @Param("leaveTypeId") Long leaveTypeId,
            @Param("leaveYear") String leaveYear);

    List<Leave> findByOrganisationId(Long organisationId);

    Optional<LeaveType> findByIdAndOrganisation(Long id, Organisation org);

    @Query("""
                SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END
                FROM Leave l
                WHERE l.employee.id = :employeeId
                  AND (
                      (l.startDate BETWEEN :startDate AND :endDate)
                      OR (l.endDate BETWEEN :startDate AND :endDate)
                      OR (l.startDate <= :startDate AND l.endDate >= :endDate)
                  )
                  AND l.leaveStatus IN :statuses
            """)
    boolean existsOverlappingLeave(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("statuses") List<Leave.LeaveStatus> statuses);

    @Query("""
                SELECT l FROM Leave l
                WHERE l.employee.id = :employeeId
                  AND (
                      (l.startDate BETWEEN :startDate AND :endDate)
                      OR (l.endDate BETWEEN :startDate AND :endDate)
                      OR (l.startDate <= :startDate AND l.endDate >= :endDate)
                  )
                  AND l.leaveStatus IN ('PENDING', 'APPROVE')
            """)
    List<Leave> findOverlappingLeaves(Long employeeId, LocalDate startDate, LocalDate endDate);

    @Query("""
            SELECT l FROM Leave l
            WHERE l.employee.id = :employeeId
            AND l.startDate BETWEEN :start AND :end
            """)
    List<Leave> findEmployeeLeavesForLeaveYear(
            @Param("employeeId") Long employeeId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    // Count leaves by status
    Long countByOrganisationIdAndLeaveStatus(Long orgId, Leave.LeaveStatus status);

    // Get leaves between dates
    List<Leave> findByOrganisationIdAndStartDateBetween(Long orgId, LocalDate from, LocalDate to);

    // Get employee's leaves in a date range
    List<Leave> findByEmployeeIdAndStartDateBetweenOrEndDateBetween(
            Long empId, LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2);

    // Get approved leaves in date range (for calendar)
    List<Leave> findByOrganisationIdAndLeaveStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long orgId, Leave.LeaveStatus status, LocalDate endDate, LocalDate startDate);

    @Query("SELECT l FROM Leave l WHERE l.organisation.id = :orgId " +
            "AND l.leaveStatus = 'PENDING' ORDER BY l.startDate ASC")
    List<Leave> findPendingLeavesByOrg(@Param("orgId") Long orgId);

    @Query("SELECT l FROM Leave l WHERE l.organisation = :org AND l.startDate BETWEEN :from AND :to")
    List<Leave> findLeavesBetweenDates(@Param("org") Organisation org,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);

    boolean existsByOrganisationAndLeaveStatusIn(
            Organisation organisation,
            List<Leave.LeaveStatus> statuses);

    @Query("SELECT l FROM Leave l WHERE l.employee.id = :employeeId " +
            "AND l.leaveType.id = :leaveTypeId")
    List<Leave> findApprovedLeavesByEmployeeAndTypeThisYear(@Param("employeeId") Long employeeId,
            @Param("leaveTypeId") Long leaveTypeId);

    // Check if employee has approved leave on a specific date
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END " +
            "FROM Leave l WHERE l.employee.id = :employeeId " +
            "AND :date BETWEEN l.startDate AND l.endDate " +
            "AND l.leaveStatus = 'APPROVE'")
    boolean existsByEmployeeAndDateAndApproved(
            @Param("employeeId") Long employeeId,
            @Param("date") LocalDate date);

    // Get all leaves by employee and date range
    @Query("SELECT l FROM Leave l WHERE l.employee.id = :employeeId " +
            "AND ((l.startDate BETWEEN :startDate AND :endDate) " +
            "OR (l.endDate BETWEEN :startDate AND :endDate) " +
            "OR (l.startDate <= :startDate AND l.endDate >= :endDate)) " +
            "ORDER BY l.startDate DESC")
    List<Leave> findByEmployeeAndDateRange(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

}
