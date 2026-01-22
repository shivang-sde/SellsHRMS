package com.sellspark.SellsHRMS.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sellspark.SellsHRMS.entity.Project;
import com.sellspark.SellsHRMS.entity.Task;
import com.sellspark.SellsHRMS.entity.Task.TaskStatus;
import com.sellspark.SellsHRMS.repository.EmployeeRepository;
import com.sellspark.SellsHRMS.repository.ProjectRepository;
import com.sellspark.SellsHRMS.repository.TaskRepository;
import com.sellspark.SellsHRMS.service.WorkService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class WorkServiceImpl implements WorkService {
    @Autowired private TaskRepository taskRepo;
    @Autowired private ProjectRepository projectRepo;
    private EmployeeRepository employeeRepo;

    @Override
    public Task createTask(Task task, Long projectId, Long orgId) {
        Project project = projectRepo.findById(projectId).orElseThrow();
        long count = taskRepo.countByProjectId(projectId);
        
        task.setProject(project);

        // Set Organisation from project to maintain integrity
        task.setOrganisation(project.getOrganisation()); 
        
        return taskRepo.save(task);
    }

    // @Override
    // public TaskComment addComment(Long taskId, Long authorId, String content) {
    //     Task task = taskRepo.findById(taskId).orElseThrow();
    //     // Employee author = employeeRepo.findById(authorId)...
    //     TaskComment comment = TaskComment.builder()
    //             .task(task)
    //             .comment(content)
    //             .employee(employeeRepo.findById(authorId).orElse(null)) // fix it later not null 
    //             .build();
    //     return commentRepo.save(comment);
    // }

    @Override
    public Project saveProject(Project project, Long orgId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'saveProject'");
    }

    @Override
    public List<Project> getProjectsByOrg(Long orgId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getProjectsByOrg'");
    }

    @Override
    public Task updateTaskStatus(Long taskId, TaskStatus status) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateTaskStatus'");
    }

    // @Override
    // public List<TaskComment> getTaskComments(Long taskId) {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'getTaskComments'");
    // }
}