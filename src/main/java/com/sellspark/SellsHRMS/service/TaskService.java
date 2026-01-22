
package com.sellspark.SellsHRMS.service;


import com.sellspark.SellsHRMS.dto.project.*;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface TaskService {

    TaskDTO createTask(TaskDTO dto, Long organisationId, Long reporterId, List<MultipartFile> files,  List<String> descriptions);

    TaskDTO updateTask(Long taskId, TaskDTO dto, Long organisationId, Long employeeId, List<MultipartFile> newFiles, List<Long> removeAttachmentIds,  List<String> descriptions);

    TaskDTO getTaskById(Long taskId, Long organisationId, Long employeeId);

    List<TaskDTO> getSelfTasks(Long organisationId, Long employeeId);

    List<TaskDTO> getTasksByProject(Long projectId, Long organisationId, Long employeeId);
    List<TaskAttachmentDTO> getAttachments(Long taskId, Long organisationId);
     TaskAttachmentDTO addAttachment(Long taskId, TaskAttachmentDTO attachmentDTO, Long employeeId);

    List<TaskAttachmentDTO> addAttachments(Long taskId, List<TaskAttachmentDTO> dtos, Long employeeId);

    void deleteTask(Long taskId, Long organisationId, Long employeeId);


     List<TaskDTO> getUpcomingReminders(Long organisationId, Long employeeId, int daysAhead);

    // TaskCommentDTO addComment(Long taskId, TaskCommentDTO commentDTO, Long employeeId);
    // List<TaskCommentDTO> getComments(Long taskId, Long organisationId);

   
}
