package com.sellspark.SellsHRMS.repository;



import com.sellspark.SellsHRMS.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // ----------------------- BASIC FILTERS -----------------------

    /**
     * All tickets under a specific project.
     */
    List<Ticket> findByProjectId(Long projectId);

    /**
     * Tickets assigned to a given employee.
     */
    List<Ticket> findByAssignees_Id(Long employeeId);

    /**
     * Independent tickets not linked to any project.
     */
    List<Ticket> findByProjectIsNullAndAssignees_Id(Long employeeId);


   @Query("""
       SELECT DISTINCT t FROM Ticket t
       LEFT JOIN t.assignees a
       WHERE t.project IS NULL
         AND (a.id = :employeeId OR t.createdBy.id = :employeeId)
         AND t.isActive = true
       """)
List<Ticket> findIndependentTicketsByAssigneeOrCreator(@Param("employeeId") Long employeeId);


    /**
     * All tickets created by an employee (for My Tickets tab).
     */
    List<Ticket> findByCreatedBy_Id(Long createdById);

    /**
     * Tickets by project and status.
     */
    List<Ticket> findByProjectIdAndStatus(Long projectId, Ticket.TicketStatus status);

    // ----------------------- SEARCH / FILTER -----------------------


    @Query("""
       SELECT DISTINCT t FROM Ticket t
       LEFT JOIN t.assignees a
       WHERE t.project.organisation.id = :orgId
         AND (a.id = :empId OR t.createdBy.id = :empId)
       """)
       
       
       List<Ticket> findByOrganisationIdAndAssignees_IdOrCreatedBy_Id(@Param("orgId") Long orgId,
                                                               @Param("empId") Long empId);



    /**
 * Keyword search across title and description.
 * Used in UI filters and dashboards.
 */
@Query("""
       SELECT t FROM Ticket t
       WHERE t.project.organisation.id = :organisationId
         AND (LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
              OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
       """)
List<Ticket> searchByOrganisationAndKeyword(@Param("organisationId") Long organisationId,
                                            @Param("keyword") String keyword);

// ----------------------- STATUS FILTER -----------------------

/**
 * All tickets for an organisation by status (cross-project).
 */
@Query("""
       SELECT t FROM Ticket t
       WHERE t.project.organisation.id = :organisationId
         AND t.status = :status
       """)
List<Ticket> findByOrganisationAndStatus(@Param("organisationId") Long organisationId,
                                         @Param("status") Ticket.TicketStatus status);

// ----------------------- DASHBOARD -----------------------

/**
 * All tickets where an employee is assignee OR creator.
 */
@Query("""
       SELECT DISTINCT t FROM Ticket t
       LEFT JOIN t.assignees a
       WHERE a.id = :employeeId OR t.createdBy.id = :employeeId
       """)
List<Ticket> findAllVisibleToEmployee(@Param("employeeId") Long employeeId);

/**
 * Ticket progress report for organisation dashboard.
 */
@Query("""
       SELECT t.id, t.title,
              COUNT(ts) AS totalTasks,
              SUM(CASE WHEN ts.status = 'DONE' THEN 1 ELSE 0 END) AS completedTasks,
              t.startDate, t.endDate, t.actualCompletionDate
       FROM Ticket t
       LEFT JOIN t.tasks ts
       WHERE t.project.organisation.id = :organisationId
       GROUP BY t.id, t.title, t.startDate, t.endDate, t.actualCompletionDate
       """)
List<Object[]> getTicketProgressReport(@Param("organisationId") Long organisationId);



// All tickets by status for org
List<Ticket> findByOrganisationIdAndStatus(Long organisationId, Ticket.TicketStatus status);

// All delayed tickets (endDate passed but not completed)
@Query("""
    SELECT t FROM Ticket t
    WHERE t.organisation.id = :organisationId
      AND t.endDate < CURRENT_DATE
      AND t.status NOT IN ('COMPLETED', 'CANCELLED')
""")
List<Ticket> findDelayedTickets(@Param("organisationId") Long organisationId);

// All visible tickets for employee
@Query("""
    SELECT DISTINCT t FROM Ticket t
    LEFT JOIN t.assignees a
    WHERE t.organisation.id = :organisationId
      AND (a.id = :employeeId OR t.createdBy.id = :employeeId)
""")
List<Ticket> findAllVisibleToEmployee(@Param("organisationId") Long organisationId, @Param("employeeId") Long employeeId);



}
