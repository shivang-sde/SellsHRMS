package com.sellspark.SellsHRMS.service.impl;


import com.sellspark.SellsHRMS.dto.project.*;
import com.sellspark.SellsHRMS.entity.*;
import com.sellspark.SellsHRMS.exception.EmployeeNotFoundException;
import com.sellspark.SellsHRMS.exception.ProjectMemberNotFoundException;
import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
import com.sellspark.SellsHRMS.repository.*;
import com.sellspark.SellsHRMS.service.TaskService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final EmployeeRepository empRepo;
    private final TaskAttachmentRepository attachmentRepository;
    private final TaskActivityRepository activityRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TicketRepository ticketRepository;
    private final TicketActivityRepository ticketActivityRepository;


 // ---------------- CREATE TASK ----------------
@Override
@Transactional
public TaskDTO createTask(TaskDTO dto,
                          Long organisationId,
                          Long reporterId,
                          List<MultipartFile> files,  List<String> descriptions) {

    // 1️⃣ Identify reporter & creator
    Employee reporter = empRepo.findById(reporterId)
            .orElseThrow(() -> new EmployeeNotFoundException(reporterId));

    Employee createdBy = (dto.getCreatedById() != null)
            ? empRepo.findById(dto.getCreatedById())
                .orElseThrow(() -> new EmployeeNotFoundException(dto.getCreatedById()))
            : reporter;

    Organisation org = reporter.getOrganisation();

    // 2️⃣ Determine context (Project / Ticket / Self)
    Project project = null;
    if (dto.getProjectId() != null) {
        project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        org = project.getOrganisation();
    }

    Ticket ticket = null;
    if (dto.getTicketId() != null) {
        ticket = ticketRepository.findById(dto.getTicketId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        logTicketActivity(ticket, createdBy, TicketActivity.ActivityType.TASK_ADDED, "Task Added by " + createdBy.getFirstName());
        org = ticket.getOrganisation();
    }

    boolean isSelfTask = Boolean.TRUE.equals(dto.getIsSelfTask()) ||
            (dto.getProjectId() == null && dto.getTicketId() == null);

    // 3️⃣ Optional assignee
    Employee assignee = null;
    if (dto.getAssigneeId() != null) {
        assignee = empRepo.findById(dto.getAssigneeId())
                .orElseThrow(() -> new EmployeeNotFoundException(dto.getAssigneeId()));
        if (project != null) validateProjectMember(project.getId(), assignee.getId());
    }

    // 4️⃣ Build and save task
    Task task = Task.builder()
            .title(dto.getTitle())
            .description(dto.getDescription())
            .status(dto.getStatus() != null
                    ? Task.TaskStatus.valueOf(dto.getStatus())
                    : Task.TaskStatus.BACKLOG)
            .project(project)
            .ticket(ticket)
            .assignee(assignee)
            .reporter(reporter)
            .createdBy(createdBy)
            .organisation(org)
            .isSelfTask(isSelfTask)
            .reminderEnabled(dto.getReminderEnabled())
            .reminderAt(dto.getReminderAt())
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .build();

    taskRepository.save(task);

    // 5️⃣ Handle attachments (multiple uploads)
    if (files != null && !files.isEmpty()) {
    handleTaskFileUpload(task, files, reporter, descriptions);
}


    // 6️⃣ Log activities
    logActivity(task, reporter.getId(), TaskActivity.ActivityType.TASK_CREATED, null, "Task created");

    if (Boolean.TRUE.equals(dto.getReminderEnabled()) && dto.getReminderAt() != null) {
        logActivity(task, reporter.getId(), TaskActivity.ActivityType.REMINDER_SET,
                null, "Reminder set for " + dto.getReminderAt());
    }

    return mapToDTO(task);
}



// ---------------- UPDATE TASK ----------------
@Override
@Transactional
public TaskDTO updateTask(Long taskId,
                          TaskDTO dto,
                          Long organisationId,
                          Long employeeId,
                          List<MultipartFile> newFiles,
                          List<Long> removeAttachmentIds,  List<String> descriptions) {

    Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

    // 🔒 Optional: Validate access for project tasks
    if (task.getProject() != null) {
        validateProjectMember(task.getProject().getId(), employeeId);
    }

    boolean updated = false;

    // 🔹 Update fields safely
    if (dto.getTitle() != null && !dto.getTitle().equals(task.getTitle())) {
        task.setTitle(dto.getTitle());
        updated = true;
    }

    if (dto.getDescription() != null && !dto.getDescription().equals(task.getDescription())) {
        task.setDescription(dto.getDescription());
        updated = true;
    }

    if (dto.getStatus() != null && !dto.getStatus().equals(task.getStatus().name())) {
        String oldValue = task.getStatus().name();
        task.setStatus(Task.TaskStatus.valueOf(dto.getStatus()));
        logActivity(task, employeeId, TaskActivity.ActivityType.STATUS_CHANGED, oldValue, dto.getStatus());
        updated = true;
    }

    if (dto.getReminderAt() != null && !dto.getReminderAt().equals(task.getReminderAt())) {
        LocalDateTime old = task.getReminderAt();
        task.setReminderAt(dto.getReminderAt());
        logActivity(task, employeeId, TaskActivity.ActivityType.REMINDER_SET,
                String.valueOf(old), String.valueOf(dto.getReminderAt()));
        updated = true;
    }

    if (dto.getReminderEnabled() != null && !dto.getReminderEnabled().equals(task.getReminderEnabled())) {
        Boolean old = task.getReminderEnabled();
        task.setReminderEnabled(dto.getReminderEnabled());
        logActivity(task, employeeId, TaskActivity.ActivityType.REMINDER_TOGGLE,
                String.valueOf(old), String.valueOf(dto.getReminderEnabled()));
        updated = true;
    }

    if (updated) {
        task.setUpdatedAt(LocalDateTime.now());
        taskRepository.save(task);
    }

    // 🔹 Remove attachments
    if (removeAttachmentIds != null && !removeAttachmentIds.isEmpty()) {
        List<TaskAttachment> toDelete = attachmentRepository.findAllById(removeAttachmentIds);

        for (TaskAttachment a : toDelete) {
            try {
                Path path = Paths.get("uploads", "org-" + task.getOrganisation().getId(),
                        "tasks", "task-" + task.getId(), a.getFileName());
                Files.deleteIfExists(path);
            } catch (IOException e) {
                log.warn("Failed to delete attachment file: {}", a.getFileName(), e);
            }
        }

        attachmentRepository.deleteAll(toDelete);
        toDelete.forEach(a ->
                logActivity(task, employeeId, TaskActivity.ActivityType.ATTACHMENT_REMOVED, a.getFileName(), null)
        );
    }

    // 🔹 Add new attachments
    if (newFiles != null && !newFiles.isEmpty()) {
    handleTaskFileUpload(task, newFiles,
        empRepo.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId)),
        descriptions);
}

    return mapToDTO(task);
}

    @Override
        public List<TaskDTO> getSelfTasks(Long organisationId, Long employeeId) {
    List<Task> tasks = taskRepository
            .findByOrganisation_IdAndCreatedBy_IdAndIsSelfTaskTrue(organisationId, employeeId);
    return tasks.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
}



    // ---------------- GET TASK ----------------
    @Override
    public TaskDTO getTaskById(Long taskId, Long organisationId, Long employeeId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (task.getProject() != null) validateProjectMember(task.getProject().getId(), employeeId);

        return mapToDTO(task);
    }

  @Override
    public List<TaskDTO> getTasksByProject(Long projectId, Long organisationId, Long employeeId) {
        validateProjectMember(projectId, employeeId);

        return taskRepository.findByProjectId(projectId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }



    // ---------------- DELETE ----------------
   @Override
    public void deleteTask(Long taskId, Long organisationId, Long employeeId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (task.getProject() != null) validateProjectMember(task.getProject().getId(), employeeId);

        task.setIsActive(false);
        taskRepository.save(task);

        logActivity(task, employeeId, TaskActivity.ActivityType.TASK_UPDATED, "ACTIVE", "DELETED");
    }

    // ---------------- COMMENTS ----------------



  
    // ---------------- ATTACHMENTS ----------------

    @Override
public List<TaskAttachmentDTO> addAttachments(Long taskId, List<TaskAttachmentDTO> dtos, Long employeeId) {
    Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

    Employee uploader = empRepo.findById(employeeId)
            .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

    List<TaskAttachment> attachments = dtos.stream().map(dto -> TaskAttachment.builder()
            .task(task)
            .fileName(dto.getFileName())
            .fileUrl(dto.getFileUrl())
            .fileType(dto.getFileType())
            .fileSizeKB(dto.getFileSizeKB())
            .description(dto.getDescription())
            .uploadedBy(uploader)
            .uploadedAt(LocalDateTime.now())
            .build()).collect(Collectors.toList());

    attachmentRepository.saveAll(attachments);
    attachments.forEach(a -> logActivity(task, employeeId, TaskActivity.ActivityType.ATTACHMENT_UPLOADED, null, a.getFileName()));

    return attachments.stream().map(this::mapToAttachmentDTO).collect(Collectors.toList());
}




    @Override
    public TaskAttachmentDTO addAttachment(Long taskId, TaskAttachmentDTO attachmentDTO, Long employeeId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (task.getProject() != null) validateProjectMember(task.getProject().getId(), employeeId);

        Employee emp = empRepo.findById(employeeId).orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        TaskAttachment attachment = TaskAttachment.builder()
                .task(task)
                .fileName(attachmentDTO.getFileName())
                .fileUrl(attachmentDTO.getFileUrl())
                .fileType(attachmentDTO.getFileType())
                .fileSizeKB(attachmentDTO.getFileSizeKB())
                .uploadedBy(emp)
                .uploadedAt(LocalDateTime.now())
                .build();

        attachmentRepository.save(attachment);
        logActivity(task, employeeId, TaskActivity.ActivityType.ATTACHMENT_UPLOADED, null, attachment.getFileName());

        return mapToAttachmentDTO(attachment);
    }

    @Override
    public List<TaskAttachmentDTO> getAttachments(Long taskId, Long organisationId) {
        return attachmentRepository.findByTaskId(taskId)
                .stream().map(this::mapToAttachmentDTO).collect(Collectors.toList());
    }

    // ---------------- HELPER METHODS ----------------

    private ProjectMember getProjectMember(Long projectId, Long employeeId) {
        return projectMemberRepository.findByProjectIdAndEmployeeIdAndIsActiveTrue(projectId, employeeId)
                .orElseThrow(() -> new ProjectMemberNotFoundException(employeeId, projectId));
    }

   private void validateProjectMember(Long projectId, Long employeeId) {
        if (!projectMemberRepository.existsByProjectIdAndEmployeeIdAndIsActiveTrue(projectId, employeeId)) {
            throw new ProjectMemberNotFoundException(employeeId, projectId);
        }
    }

    private void logActivity(Task task, Long employeeId, TaskActivity.ActivityType type, String oldValue, String newValue) {

        Employee emp = empRepo.findById(employeeId).orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        TaskActivity activity = TaskActivity.builder()
                .task(task)
                .employee(emp)
                .activityType(type)
                .oldValue(oldValue)
                .newValue(newValue)
                .createdAt(LocalDateTime.now())
                .build();

        activityRepository.save(activity);
    }

    // ---------------- DTO MAPPERS ----------------
   private TaskDTO mapToDTO(Task task) {
    if (task == null) return null;

    return TaskDTO.builder()
            .id(task.getId())
            .title(task.getTitle())
            .description(task.getDescription())

            // Priority & Status
        //     .priority(task.getPriority() != null ? task.getPriority().name() : null)
            .status(task.getStatus() != null ? task.getStatus().name() : null)

            // Associations
            .projectId(task.getProject() != null ? task.getProject().getId() : null)
            .projectName(task.getProject() != null ? task.getProject().getName() : null)
            .ticketId(task.getTicket() != null ? task.getTicket().getId() : null)
            .ticketTitle(task.getTicket() != null ? task.getTicket().getTitle() : null)

            // People
            .assigneeId(task.getAssignee() != null ? task.getAssignee().getId() : null)
            .assigneeName(task.getAssignee() != null
                    ? task.getAssignee().getFirstName() + " " + task.getAssignee().getLastName()
                    : null)
            .reporterId(task.getReporter() != null ? task.getReporter().getId() : null)
            .reporterName(task.getReporter() != null
                    ? task.getReporter().getFirstName() + " " + task.getReporter().getLastName()
                    : null)
            .createdById(task.getCreatedBy() != null ? task.getCreatedBy().getId() : null)
            .createdByName(task.getCreatedBy() != null
                    ? task.getCreatedBy().getFirstName() + " " + task.getCreatedBy().getLastName()
                    : null)

            // Task Type and Reminder
            .isSelfTask(task.getIsSelfTask())
            .reminderAt(task.getReminderAt())
            .reminderEnabled(task.getReminderEnabled())

            // Meta
            .isActive(task.getIsActive())
            .createdAt(task.getCreatedAt())
            .updatedAt(task.getUpdatedAt())

            // Attachments
            .attachments(task.getAttachments() != null
                    ? task.getAttachments().stream()
                        .map(this::mapToAttachmentDTO)
                        .collect(Collectors.toList())
                    : null)
            .build();
}



    private TaskAttachmentDTO mapToAttachmentDTO(TaskAttachment attachment) {
        return TaskAttachmentDTO.builder()
                .id(attachment.getId())
                .taskId(attachment.getTask() != null ? attachment.getTask().getId() : null)
                .fileName(attachment.getFileName())
                .fileUrl(attachment.getFileUrl())
                .fileType(attachment.getFileType())
                .fileSizeKB(attachment.getFileSizeKB())
                .uploadedById(attachment.getUploadedBy() != null ? attachment.getUploadedBy().getId() : null)
                .uploadedByName(attachment.getUploadedBy() != null ? attachment.getUploadedBy().getFirstName() : null)
                .uploadedAt(attachment.getUploadedAt())
                .build();
    }


    @Override
@Transactional(readOnly = true)
public List<TaskDTO> getUpcomingReminders(Long organisationId, Long employeeId, int daysAhead) {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime end = now.plusDays(daysAhead);

    List<Task> reminders = taskRepository.findUpcomingReminders(now, end);

    // Filter by employee and organisation if needed
    reminders = reminders.stream()
            .filter(t -> t.getCreatedBy() != null && t.getCreatedBy().getId().equals(employeeId))
            .filter(t -> t.getOrganisation() != null && t.getOrganisation().getId().equals(organisationId))
            .collect(Collectors.toList());

    return reminders.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
}



    // ---------------- FILE UPLOAD HELPER ----------------
private void handleTaskFileUpload(Task task,
                                  List<MultipartFile> files,
                                  Employee uploader,
                                  @Nullable List<String> descriptions) {

    Long orgId = task.getOrganisation().getId();
    Path basePath = Paths.get("uploads", "org-" + orgId, "tasks", "task-" + task.getId());

    try {
        Files.createDirectories(basePath);
    } catch (IOException e) {
        throw new RuntimeException("Could not create directory for task uploads", e);
    }

    List<TaskAttachment> attachments = new ArrayList<>();

    for (int i = 0; i < files.size(); i++) {
        MultipartFile file = files.get(i);
        String description = (descriptions != null && descriptions.size() > i)
                ? descriptions.get(i)
                : null;

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path dest = basePath.resolve(fileName);

        try (InputStream is = file.getInputStream()) {
            Files.copy(is, dest, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + fileName, e);
        }

        TaskAttachment attachment = TaskAttachment.builder()
                .task(task)
                .fileName(fileName)
                .fileUrl("/uploads/org-" + orgId + "/tasks/task-" + task.getId() + "/" + fileName)
                .fileType(file.getContentType())
                .fileSizeKB((double) file.getSize() / 1024)
                .description(description)
                .uploadedBy(uploader)
                .uploadedAt(LocalDateTime.now())
                .build();

        attachments.add(attachment);
    }

    attachmentRepository.saveAll(attachments);

    // Log each upload as activity
    attachments.forEach(a ->
            logActivity(task, uploader.getId(),
                    TaskActivity.ActivityType.ATTACHMENT_UPLOADED,
                    null, a.getFileName())
    );
}

 private void logTicketActivity(Ticket ticket, Employee emp, TicketActivity.ActivityType type, String desc) {
        TicketActivity activity = TicketActivity.builder()
                .ticket(ticket)
                .employee(emp)
                .type(type)
                .description(desc)
                .createdAt(LocalDateTime.now())
                .build();
        ticketActivityRepository.save(activity);
    }

}
