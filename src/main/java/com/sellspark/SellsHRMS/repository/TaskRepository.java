package com.sellspark.SellsHRMS.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sellspark.SellsHRMS.entity.Task;
import com.sellspark.SellsHRMS.entity.Task.TaskStatus;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProjectId(Long projectId);
    List<Task> findByAssigneeId(Long employeeId);
    List<Task> findByAssigneeIdAndStatusNot(Long empId, TaskStatus status);

   List<Task> findByReporter_IdAndOrganisation_IdAndProjectIsNullOrderByCreatedAtDesc(Long empId, Long orgId);

   List<Task> findByOrganisation_IdAndCreatedBy_IdAndIsSelfTaskTrue(Long orgId, Long empId);


    long countByProjectId(Long projectId); // Used for Task Key generation

   @Query("""
       SELECT t FROM Task t
       WHERE t.organisation.id = :orgId
         AND (t.assignee.id = :empId OR t.reporter.id = :empId)
       """)
List<Task> findByOrganisationIdAndAssignee_IdOrReporter_Id(
        @Param("orgId") Long orgId,
        @Param("empId") Long empId);


    @Query("SELECT t FROM Task t " +
           "WHERE t.isSelfTask = true " +
           "AND t.reminderEnabled = true " +
           "AND t.reminderAt BETWEEN :start AND :end " +
           "AND t.isActive = true " +
           "ORDER BY t.reminderAt ASC")
    List<Task> findUpcomingReminders(@Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end);

    @Query("SELECT t.status, COUNT(t) FROM Task t WHERE t.project.id = :projectId GROUP BY t.status")
    List<Object[]> getStatusCountsByProject(Long projectId);

//     @Query("SELECT t FROM Task t WHERE t.dueDate < CURRENT_TIMESTAMP AND t.status != :status AND t.organisation.id = :orgId")
//     List<Task> findOverdueTasks(@Param("orgId") Long orgId, @Param("status") TaskStatus status );
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignee.id = :empId AND t.status = 'DONE'")
    Long countCompletedTasksByEmployee(Long empId);


    List<Task> findByProjectIdAndIsActiveTrue(Long projectId);

//     List<Task> findByProjectIdAndParentTaskIsNullAndIsActiveTrue(Long projectId);

//     List<Task> findByParentTaskIdAndIsActiveTrue(Long parentTaskId);

    Optional<Task> findByIdAndProjectOrganisationId(Long id, Long organisationId);
    List<Task> findByAssigneeIdAndIsActiveTrue(Long assigneeId);

//     List<Task> findByEpicIdAndIsActiveTrue(Long epicId);

//     List<Task> findBySprintIdAndIsActiveTrue(Long sprintId);

    Optional<Task> findByIdAndOrganisationId(Long id, Long organisationId);

    List<Task> findByStatusAndOrganisationId(TaskStatus status, Long organisationId);

//     List<Task> findByAssigneeIdAndIsActiveTrue(Long employeeId);

    List<Task> findByAssigneeIdAndStatusAndIsActiveTrue(Long employeeId, TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId " +
           "AND t.status = :status AND t.isActive = true")
    List<Task> findByProjectIdAndStatus(@Param("projectId") Long projectId, @Param("status") TaskStatus status);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.id = :projectId " +
           "AND t.status = :status AND t.isActive = true")
    Long countByProjectIdAndStatus(@Param("projectId") Long projectId, @Param("status") TaskStatus status);

//     @Query("SELECT t FROM Task t WHERE t.assignee.id = :empId " +
//            "AND t.dueDate BETWEEN :startDate AND :endDate " +
//            "AND t.status NOT IN ('COMPLETED', 'CANCELLED') " +
//            "AND t.isActive = true")
//     List<Task> findUpcomingTasksByEmployee(@Param("empId") Long empId, 
//                                            @Param("startDate") LocalDate startDate,
//                                            @Param("endDate") LocalDate endDate);

//     @Query("SELECT t FROM Task t WHERE t.assignee.id = :empId " +
//            "AND t.dueDate < :today " +
//            "AND t.status NOT IN ('COMPLETED', 'CANCELLED') " +
//            "AND t.isActive = true")
//     List<Task> findOverdueTasksByEmployee(@Param("empId") Long empId, @Param("today") LocalDate today);

//     // In TaskRepository    
//        @Query("SELECT DISTINCT t.epic FROM Task t WHERE t.sprint.id = :sprintId")
//        List<Epic> findEpicsBySprint(@Param("sprintId") Long sprintId);

    // In TaskRepository
       // @Query("SELECT DISTINCT t.sprint FROM Task t WHERE t.epic.id = :epicId")
       // List<Sprint> findSprintsByEpic(@Param("epicId") Long epicId);


     @Query("SELECT COUNT(t) FROM Task t WHERE t.organisation.id = :orgId AND t.status = :status AND t.isActive = true")
    Long countByStatus(@Param("orgId") Long orgId, @Param("status") TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.organisation.id = :orgId AND LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) AND t.isActive = true")
    List<Task> searchTasks(@Param("orgId") Long orgId, @Param("keyword") String keyword);




}