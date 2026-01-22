package com.sellspark.SellsHRMS.repository;

import com.sellspark.SellsHRMS.entity.TaskAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskAttachmentRepository extends JpaRepository<TaskAttachment, Long> {

    List<TaskAttachment> findByTaskId(Long taskId);

    //  List<TaskAttachment> findByCommentId(Long commentId);

    void deleteByTaskId(Long taskId);
}
