package com.sellspark.SellsHRMS.service;

import com.sellspark.SellsHRMS.dto.project.TaskCommentDTO;
import java.util.List;

public interface TaskCommentService {
    
    TaskCommentDTO addComment(TaskCommentDTO commentDTO);
    
    TaskCommentDTO updateComment(Long commentId, TaskCommentDTO commentDTO);
    
    List<TaskCommentDTO> getCommentsByTask(Long taskId);
    
    void deleteComment(Long commentId);
}