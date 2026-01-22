package com.sellspark.SellsHRMS.repository;

import com.sellspark.SellsHRMS.entity.TaskActivity;
import com.sellspark.SellsHRMS.entity.TaskActivity.ActivityType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskActivityRepository extends JpaRepository<TaskActivity, Long> {

    List<TaskActivity> findByTaskIdOrderByCreatedAtDesc(Long taskId);

    @Query("SELECT a FROM TaskActivity a WHERE a.task.organisation.id = :orgId AND a.createdAt BETWEEN :start AND :end ORDER BY a.createdAt DESC")
    List<TaskActivity> findRecentActivities(@Param("orgId") Long orgId,
                                            @Param("start") java.time.LocalDateTime start,
                                            @Param("end") java.time.LocalDateTime end);


    
    List<TaskActivity> findByEmployeeId(Long employeeId);
    List<TaskActivity> findByTaskIdOrderByCreatedAtAsc(Long taskId);


     List<TaskActivity> findByTask_Id(Long id);

    List<TaskActivity> findByTaskIdAndActivityType(Long taskId, ActivityType type);

    
    @Query("SELECT ta FROM TaskActivity ta WHERE ta.task.id = :taskId AND ta.employee.id = :empId")
    List<TaskActivity> findByTaskAndEmployee(@Param("taskId") Long taskId, @Param("empId") Long empId);

    @Query("SELECT ta FROM TaskActivity ta WHERE ta.task.project.organisation.id = :orgId ORDER BY ta.createdAt DESC")
    List<TaskActivity> findByOrganisation(@Param("orgId") Long orgId);
}
