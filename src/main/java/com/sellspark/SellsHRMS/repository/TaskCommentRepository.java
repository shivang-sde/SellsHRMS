// package com.sellspark.SellsHRMS.repository;

// import java.util.List;

// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;

// import com.sellspark.SellsHRMS.entity.TaskComment;

// public interface TaskCommentRepository extends JpaRepository<TaskComment, Long> {
//     // List<TaskComment> findByTaskIdOrderByCreatedAtDesc(Long taskId);

//     // @Query("SELECT tc FROM TaskComment tc WHERE tc.task.id = :taskId ORDER BY tc.createdAt ASC")
//     // List<TaskComment> findByTaskIdOrderByCreatedAtAsc(@Param("taskId") Long taskId);

//     Long countByTaskId(Long taskId);
// }
