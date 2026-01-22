package com.sellspark.SellsHRMS.controller.api;

import com.sellspark.SellsHRMS.dto.project.TaskDTO;
import com.sellspark.SellsHRMS.entity.Employee;
import com.sellspark.SellsHRMS.entity.Task;
import com.sellspark.SellsHRMS.exception.EmployeeNotFoundException;
import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
import com.sellspark.SellsHRMS.dto.project.TaskAttachmentDTO;
import com.sellspark.SellsHRMS.dto.project.TaskActivityDTO;
import com.sellspark.SellsHRMS.payload.ApiResponse;
import com.sellspark.SellsHRMS.service.TaskService;
import com.sellspark.SellsHRMS.repository.EmployeeRepository;
import com.sellspark.SellsHRMS.repository.TaskActivityRepository;
import com.sellspark.SellsHRMS.repository.TaskRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * REST controller for managing Tasks and their related activities/attachments.
 */
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskRestController {

    private final TaskService taskService;
    private final TaskActivityRepository taskActivityRepository;
    private final TaskRepository taskRepository;
    private final EmployeeRepository empRepo;

    // ---------------- CREATE TASK ----------------
@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<ApiResponse<TaskDTO>> createTask(
        @RequestPart("task") TaskDTO dto,
        @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments,
        @RequestParam Long organisationId,
        @RequestParam Long reporterId,
        @RequestParam(value = "descriptions", required = false) List<String> descriptions
    ) {

    TaskDTO created = taskService.createTask(dto, organisationId, reporterId, attachments, descriptions);
    return ResponseEntity.ok(ApiResponse.ok("Task created successfully", created));
}

// ---------------- UPDATE TASK ----------------
@PutMapping(value = "/{taskId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<ApiResponse<TaskDTO>> updateTask(
        @PathVariable Long taskId,
        @RequestPart("task") TaskDTO dto,
        @RequestPart(value = "attachments", required = false) List<MultipartFile> newFiles,
        @RequestParam(value = "removeAttachmentIds", required = false) List<Long> removeAttachmentIds,
        @RequestParam(value = "descriptions", required = false) List<String> descriptions,
        @RequestParam Long organisationId,
        @RequestParam Long employeeId) {

     TaskDTO updated = taskService.updateTask(taskId, dto, organisationId, employeeId, newFiles, removeAttachmentIds, descriptions);
    return ResponseEntity.ok(ApiResponse.ok("Task updated successfully", updated));
}



    // ----------------------------------------------------------------
    // DELETE TASK
    // ----------------------------------------------------------------
    @DeleteMapping("/{taskId}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(
            @PathVariable Long taskId,
            @RequestParam Long organisationId,
            @RequestParam Long employeeId) {

        taskService.deleteTask(taskId, organisationId, employeeId);
        return ResponseEntity.ok(ApiResponse.ok("Task deleted successfully", null));
    }

    // ----------------------------------------------------------------
    // GET SINGLE TASK
    // ----------------------------------------------------------------
    @GetMapping("/{taskId}")
    public ResponseEntity<ApiResponse<TaskDTO>> getTaskById(
            @PathVariable Long taskId,
            @RequestParam Long organisationId,
            @RequestParam Long employeeId) {

        TaskDTO task = taskService.getTaskById(taskId, organisationId, employeeId);
        return ResponseEntity.ok(ApiResponse.ok("Task retrieved successfully", task));
    }


    @GetMapping("/self")
    public ResponseEntity<ApiResponse<List<TaskDTO>>> getSelfTasks(
        @RequestParam Long organisationId,
        @RequestParam Long employeeId) {

    List<TaskDTO> tasks = taskService.getSelfTasks(organisationId, employeeId);
    return ResponseEntity.ok(ApiResponse.ok("Fetched self tasks", tasks));
}


    // ----------------------------------------------------------------
    // LIST TASKS BY PROJECT
    // ----------------------------------------------------------------
    @GetMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<List<TaskDTO>>> getTasksByProject(
            @PathVariable Long projectId,
            @RequestParam Long organisationId,
            @RequestParam Long employeeId) {

        List<TaskDTO> tasks = taskService.getTasksByProject(projectId, organisationId, employeeId);
        return ResponseEntity.ok(ApiResponse.ok("Tasks fetched successfully", tasks));
    }

    // ----------------------------------------------------------------
    // ADD ATTACHMENT
    // ----------------------------------------------------------------
    @PostMapping(value = "/{taskId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<ApiResponse<List<TaskAttachmentDTO>>> addAttachments(
        @PathVariable Long taskId,
        @RequestParam("files") List<MultipartFile> files,
        @RequestParam(value = "descriptions", required = false) List<String> descriptions,
        @RequestParam Long employeeId) {

    Employee uploader = empRepo.findById(employeeId)
            .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
    Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

    Long orgId = uploader.getOrganisation().getId();
    Path basePath = Paths.get("uploads", "org-" + orgId, "tasks", "task-" + taskId);

    try {
        Files.createDirectories(basePath);

        List<TaskAttachmentDTO> dtos = IntStream.range(0, files.size())
                .mapToObj(i -> {
                    MultipartFile file = files.get(i);
                    String desc = (descriptions != null && descriptions.size() > i) ? descriptions.get(i) : null;

                    String fileName = file.getOriginalFilename();
                    Path dest = basePath.resolve(fileName);

                    try (InputStream is = file.getInputStream()) {
                        Files.copy(is, dest, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to store file: " + fileName, e);
                    }

                    String fileUrl = "/uploads/org-" + orgId + "/tasks/task-" + taskId + "/" + fileName;

                    return TaskAttachmentDTO.builder()
                            .fileName(fileName)
                            .fileUrl(fileUrl)
                            .fileType(file.getContentType())
                            .fileSizeKB((double) file.getSize() / 1024)
                            .description(desc)
                            .build();
                }).collect(Collectors.toList());

        List<TaskAttachmentDTO> saved = taskService.addAttachments(taskId, dtos, employeeId);
        return ResponseEntity.ok(ApiResponse.ok("Attachments uploaded successfully", saved));

    } catch (IOException e) {
        throw new RuntimeException("Could not store attachments", e);
    }
}


    // ----------------------------------------------------------------
    // GET ATTACHMENTS
    // ----------------------------------------------------------------
    @GetMapping("/{taskId}/attachments")
    public ResponseEntity<ApiResponse<List<TaskAttachmentDTO>>> getAttachments(
            @PathVariable Long taskId,
            @RequestParam Long organisationId) {

        List<TaskAttachmentDTO> attachments = taskService.getAttachments(taskId, organisationId);
        return ResponseEntity.ok(ApiResponse.ok("Attachments fetched successfully", attachments));
    }

    // ----------------------------------------------------------------
    // GET TASK ACTIVITY LOG
    // ----------------------------------------------------------------
    @GetMapping("/{taskId}/activities")
    public ResponseEntity<ApiResponse<List<TaskActivityDTO>>> getTaskActivities(
            @PathVariable Long taskId) {

        List<TaskActivityDTO> activities = taskActivityRepository.findByTask_Id(taskId)
                .stream()
                .map(a -> TaskActivityDTO.builder()
                        .id(a.getId())
                        .activityType(a.getActivityType().name())
                        .employeeId(a.getEmployee().getId())
                        .employeeName(a.getEmployee().getFirstName() + " " + a.getEmployee().getLastName())
                        .oldValue(a.getOldValue())
                        .newValue(a.getNewValue())
                        .createdAt(a.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.ok("Task activities fetched successfully", activities));
    }



    @GetMapping("/reminders/upcoming")
        public ResponseEntity<ApiResponse<List<TaskDTO>>> getUpcomingReminders(
        @RequestParam Long organisationId,
        @RequestParam Long employeeId,
        @RequestParam(defaultValue = "3") int daysAhead) {

    List<TaskDTO> reminders = taskService.getUpcomingReminders(organisationId, employeeId, daysAhead);
    return ResponseEntity.ok(ApiResponse.ok("Upcoming reminders fetched successfully", reminders));
}

}
