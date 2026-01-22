package com.sellspark.SellsHRMS.service;

import java.util.List;

import com.sellspark.SellsHRMS.entity.Project;
import com.sellspark.SellsHRMS.entity.Task;
import com.sellspark.SellsHRMS.entity.Task.TaskStatus;
import com.sellspark.SellsHRMS.entity.TaskComment;

public interface WorkService {
    // Project CRUD
    Project saveProject(Project project, Long orgId);
    List<Project> getProjectsByOrg(Long orgId);
    
    // Task CRUD & Logic
    Task createTask(Task task, Long projectId, Long orgId);
    Task updateTaskStatus(Long taskId, TaskStatus status);
    
    // Comment System
    // TaskComment addComment(Long taskId, Long authorId, String content);
    // List<TaskComment> getTaskComments(Long taskId);
}