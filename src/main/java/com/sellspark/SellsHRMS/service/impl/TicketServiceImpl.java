package com.sellspark.SellsHRMS.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sellspark.SellsHRMS.dto.project.TaskAttachmentDTO;
import com.sellspark.SellsHRMS.dto.project.TaskDTO;
import com.sellspark.SellsHRMS.dto.project.TicketActivityDTO;
import com.sellspark.SellsHRMS.dto.project.TicketAttachmentDTO;
import com.sellspark.SellsHRMS.dto.project.TicketDTO;
import com.sellspark.SellsHRMS.entity.Employee;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.Project;
import com.sellspark.SellsHRMS.entity.Task;
import com.sellspark.SellsHRMS.entity.TaskAttachment;
import com.sellspark.SellsHRMS.entity.Ticket;
import com.sellspark.SellsHRMS.entity.Ticket.TicketStatus;
import com.sellspark.SellsHRMS.entity.TicketActivity;
import com.sellspark.SellsHRMS.entity.TicketAttachment;
import com.sellspark.SellsHRMS.exception.EmployeeNotFoundException;
import com.sellspark.SellsHRMS.exception.InvalidOperationException;
import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
import com.sellspark.SellsHRMS.exception.UnauthorizedActionException;
import com.sellspark.SellsHRMS.repository.EmployeeRepository;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;
import com.sellspark.SellsHRMS.repository.ProjectMemberRepository;
import com.sellspark.SellsHRMS.repository.ProjectRepository;
import com.sellspark.SellsHRMS.repository.TaskActivityRepository;
import com.sellspark.SellsHRMS.repository.TicketActivityRepository;
import com.sellspark.SellsHRMS.repository.TicketAttachmentRepository;
import com.sellspark.SellsHRMS.repository.TicketRepository;
import com.sellspark.SellsHRMS.service.TicketService;
import com.sellspark.SellsHRMS.utils.EmployeeHierarchyUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepo;
    private final EmployeeRepository empRepo;
    private final ProjectRepository projectRepo;
    private final OrganisationRepository orgRepo;
    private final TicketAttachmentRepository attachmentRepo;
    private final TicketActivityRepository activityRepo;
    private final EmployeeHierarchyUtil hierarchyUtil;
    private final ProjectMemberRepository projectMemberRepo;

    // ---------------- CREATE ----------------
    @Override
    public TicketDTO createTicket(TicketDTO dto, Long organisationId, Long createdById) {
        Employee creator = empRepo.findById(createdById)
                .orElseThrow(() -> new EmployeeNotFoundException(createdById));
        Organisation org = creator.getOrganisation();

        Project project = null;
        if (dto.getProjectId() != null) {
            project = projectRepo.findById(dto.getProjectId())
                    .orElseThrow(() -> new RuntimeException("Project not found"));
        }

        Ticket ticket = Ticket.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .status(dto.getStatus() != null
                        ? TicketStatus.valueOf(dto.getStatus())
                        : TicketStatus.OPEN)
                .organisation(org)
                .project(project)
                .createdBy(creator)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .isActive(true)
                .build();

        // Assign employees
        if (dto.getAssigneeIds() != null && !dto.getAssigneeIds().isEmpty()) {
            List<Employee> assignees = empRepo.findAllById(dto.getAssigneeIds());
            ticket.setAssignees(assignees);
            ticket.setStatus(TicketStatus.ASSIGNED);
        }

       Ticket saved = ticketRepo.save(ticket);

        // Log activity
        logActivity(saved, creator, TicketActivity.ActivityType.TICKET_CREATED, "Ticketc " + ticket.getTitle() + "created by " + creator.getFirstName());

        return mapToDTO(saved);
    }

    // ---------------- UPDATE ----------------
    @Override
    public TicketDTO updateTicket(Long ticketId, TicketDTO dto, Long organisationId, Long employeeId) {
        Ticket ticket = ticketRepo.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        Employee employee = empRepo.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        
        // Permission check (either creator or project member)
        validateTicketAccess(ticket, employeeId);


        if (dto.getTitle() != null) ticket.setTitle(dto.getTitle());
        if (dto.getDescription() != null) ticket.setDescription(dto.getDescription());
        if (dto.getStatus() != null) {
            Ticket.TicketStatus old = ticket.getStatus();
            ticket.setStatus(Ticket.TicketStatus.valueOf(dto.getStatus()));
            logActivity(ticket, employee, TicketActivity.ActivityType.STATUS_CHANGED, "Status changed from " + old + " to " + ticket.getStatus());
        }

        if (dto.getStartDate() != null) ticket.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) ticket.setEndDate(dto.getEndDate());
        if (dto.getActualCompletionDate() != null) ticket.setActualCompletionDate(dto.getActualCompletionDate());

        if (dto.getAssigneeIds() != null) {
            List<Employee> newAssignees = empRepo.findAllById(dto.getAssigneeIds());
            ticket.setAssignees(newAssignees);
            logActivity(ticket, employee, TicketActivity.ActivityType.ASSIGNEES_UPDATED, 
                    "Assigned to " + newAssignees.stream().map(Employee::getFirstName).collect(Collectors.joining(", ")));
        }

        ticketRepo.save(ticket);
        return mapToDTO(ticket);
    }

    // ---------------- DELETE ----------------
    @Override
    public void deleteTicket(Long ticketId, Long organisationId, Long employeeId) {
        Ticket ticket = ticketRepo.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        validateTicketAccess(ticket, employeeId);
        ticket.setIsActive(false);
        ticket.setUpdatedAt(LocalDateTime.now());
        ticketRepo.save(ticket);

        Employee emp = empRepo.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
        logActivity(ticket, emp, TicketActivity.ActivityType.TICKET_UPDATED, "Ticket deleted");
    }


//    --------- Ticket Life Cycle  ----------------

@Override
public TicketDTO assignTicket(Long ticketId, List<Long> assigneeIds, Long managerId) {
    Ticket ticket = ticketRepo.findById(ticketId)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

    Employee manager = empRepo.findById(managerId)
            .orElseThrow(() -> new EmployeeNotFoundException(managerId));

    List<Employee> assignees = empRepo.findAllById(assigneeIds);
    ticket.setAssignees(assignees);
    ticket.setStatus(Ticket.TicketStatus.ASSIGNED);
    ticket.setAssignedAt(LocalDate.now());
    ticketRepo.save(ticket);

    String assigneeNames = assignees.stream()
            .map(Employee::getFirstName)
            .collect(Collectors.joining(", "));

    logActivity(ticket, manager, TicketActivity.ActivityType.ASSIGNEES_UPDATED,
            "Assigned to: " + assigneeNames);

    return mapToDTO(ticket);
}


@Override
public TicketDTO updateTicketStatus(Long ticketId, String status, Long employeeId) {
    Ticket ticket = ticketRepo.findById(ticketId)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

    Employee emp = empRepo.findById(employeeId)
            .orElseThrow(() -> new EmployeeNotFoundException(employeeId));


    validateTicketAccess(ticket, employeeId);

    Ticket.TicketStatus oldStatus = ticket.getStatus();
    Ticket.TicketStatus newStatus = Ticket.TicketStatus.valueOf(status.toUpperCase());

    // Lifecycle timestamps
    if (newStatus == TicketStatus.IN_PROGRESS && ticket.getActualStartDate() == null) {
        ticket.setActualStartDate(LocalDate.now());
    } else if (newStatus == TicketStatus.COMPLETED && ticket.getActualCompletionDate() == null) {
        ticket.setActualCompletionDate(LocalDate.now());
    }

    ticket.setStatus(newStatus);

    // Automatically track lifecycle timestamps
    switch (newStatus) {
        case ASSIGNED -> {
            if (ticket.getAssignedAt() == null) ticket.setAssignedAt(LocalDate.now());
        }
        case IN_PROGRESS -> {
            if (ticket.getActualStartDate() == null) ticket.setActualStartDate(LocalDate.now());
        }
        case COMPLETED -> {
            if (ticket.getActualCompletionDate() == null) ticket.setActualCompletionDate(LocalDate.now());
        }
        default -> {}
    }

    ticketRepo.save(ticket);

    logActivity(ticket, emp, TicketActivity.ActivityType.STATUS_CHANGED,
            "Status changed from " + oldStatus + " to " + newStatus);

    return mapToDTO(ticket);
}



    // ---------------- FETCH ----------------
    @Override
    public TicketDTO getTicketById(Long ticketId, Long organisationId, Long employeeId) {
        Ticket ticket = ticketRepo.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        validateTicketAccess(ticket, employeeId);
        return mapToDTO(ticketRepo.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found")));
    }

        @Override
        public List<TicketDTO> getTicketsByStatus(Long organisationId, String status) {
        Ticket.TicketStatus ticketStatus = Ticket.TicketStatus.valueOf(status.toUpperCase());
        List<Ticket> tickets = ticketRepo.findByOrganisationAndStatus(organisationId, ticketStatus);
        return tickets.stream().map(this::mapToDTO).collect(Collectors.toList());
        }


        @Override
        public List<TicketDTO> getAllVisibleToEmployee(Long employeeId, Long organisationId) {
        List<Ticket> tickets = ticketRepo.findAllVisibleToEmployee(organisationId, employeeId);
        return tickets.stream().map(this::mapToDTO).collect(Collectors.toList());
        }


        @Override
        public List<TicketDTO> getDelayedTickets(Long organisationId) {
        List<Ticket> tickets = ticketRepo.findDelayedTickets(organisationId);
        return tickets.stream().map(this::mapToDTO).collect(Collectors.toList());
        }


        @Override
        public List<TicketActivityDTO> getTicketHistory(Long ticketId) {
        return activityRepo.findByTicketIdOrderByCreatedAtAsc(ticketId).stream()
            .map(this::mapToActivityDTO)
            .collect(Collectors.toList());
        }


    @Override
    public List<TicketDTO> getTicketsByProject(Long projectId, Long organisationId, Long employeeId) {
         validateProjectMember(projectId, employeeId);
        return ticketRepo.findByProjectId(projectId).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<TicketDTO> getTicketsByAssignee(Long employeeId, Long organisationId) {
        return ticketRepo.findByAssignees_Id(employeeId).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<TicketDTO> getIndependentTickets(Long organisationId, Long employeeId) {
        return ticketRepo.findIndependentTicketsByAssigneeOrCreator(employeeId).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<TicketDTO> searchTickets(Long organisationId, String keyword) {
        return ticketRepo.searchByOrganisationAndKeyword(organisationId, keyword).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    // ---------------- ATTACHMENTS ----------------
    @Override
    public TicketAttachmentDTO addAttachment(Long ticketId, TicketAttachmentDTO dto, Long employeeId) {
        Ticket ticket = ticketRepo.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        validateTicketAccess(ticket, employeeId);
                Employee emp = empRepo.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        TicketAttachment att = TicketAttachment.builder()
                .ticket(ticket)
                .fileName(dto.getFileName())
                .fileUrl(dto.getFileUrl())
                .fileType(dto.getFileType())
                .fileSizeKB(dto.getFileSizeKB())
                .uploadedBy(emp)
                .uploadedAt(LocalDateTime.now())
                .build();

        attachmentRepo.save(att);
        logActivity(ticket, emp, TicketActivity.ActivityType.ATTACHMENT_UPLOADED, "Uploaded " + dto.getFileName());
        return mapToTicketAttachmentDTO(att);
    }

    @Override
    public List<TicketAttachmentDTO> addAttachments(Long ticketId, List<TicketAttachmentDTO> dtos, Long employeeId) {
    Ticket ticket = ticketRepo.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));
    validateTicketAccess(ticket, employeeId);

    Employee uploader = empRepo.findById(employeeId)
            .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

    List<TicketAttachment> attachments = dtos.stream().map(dto -> TicketAttachment.builder()
            .ticket(ticket)
            .fileName(dto.getFileName())
            .fileUrl(dto.getFileUrl())
            .fileType(dto.getFileType())
            .fileSizeKB(dto.getFileSizeKB())
            .description(dto.getDescription())
            .uploadedBy(uploader)
            .uploadedAt(LocalDateTime.now())
            .build()).collect(Collectors.toList());

    attachmentRepo.saveAll(attachments);

    for (TicketAttachment att : attachments) {
        logActivity(ticket, uploader, TicketActivity.ActivityType.ATTACHMENT_UPLOADED,
                "Uploaded: " + att.getFileName() + (att.getDescription() != null ? " (" + att.getDescription() + ")" : ""));
    }

    return attachments.stream().map(this::mapToTicketAttachmentDTO).collect(Collectors.toList());
}


    @Override
    public List<TicketAttachmentDTO> getAttachments(Long ticketId, Long organisationId) {
        return attachmentRepo.findByTicket_Id(ticketId).stream()
                .map(this::mapToTicketAttachmentDTO).collect(Collectors.toList());
    }

    // ---------------- HISTORY ----------------
    // @Override
    // public List<TicketActivityDTO> getTicketHistory(Long ticketId) {
    //     return activityRepo.findByTicketIdOrderByCreatedAtAsc(ticketId).stream()
    //             .map(this::mapToActivityDTO).collect(Collectors.toList());
    // }

    // ---------------- HELPERS ----------------
    private void logActivity(Ticket ticket, Employee emp, TicketActivity.ActivityType type, String desc) {
        TicketActivity activity = TicketActivity.builder()
                .ticket(ticket)
                .employee(emp)
                .type(type)
                .description(desc)
                .createdAt(LocalDateTime.now())
                .build();
        activityRepo.save(activity);
    }

     // 🧾 HELPER METHODS
    // ---------------------------------------------------------------
    private void validateProjectMember(Long projectId, Long employeeId) {
        if (!projectMemberRepo.existsByProjectIdAndEmployeeIdAndIsActiveTrue(projectId, employeeId)) {
            throw new ResourceNotFoundException("Employee not a member of this project");
        }
    }

    private void validateTicketAccess(Ticket ticket, Long employeeId) {
    Long creatorId = ticket.getCreatedBy().getId();
    boolean isAssignee = ticket.getAssignees().stream().anyMatch(e -> e.getId().equals(employeeId));
    boolean isProjectMember = ticket.getProject() != null &&
            projectMemberRepo.existsByProjectIdAndEmployeeIdAndIsActiveTrue(ticket.getProject().getId(), employeeId);

    if (!employeeId.equals(creatorId) && !isAssignee && !isProjectMember) {
        throw new UnauthorizedActionException(
            "Employee " + employeeId + " is not authorized to access Ticket " + ticket.getId()
        );
    }
}



    private TicketDTO mapToDTO(Ticket t) {
        return TicketDTO.builder()
                .id(t.getId())
                .title(t.getTitle())
                .description(t.getDescription())
                .status(t.getStatus().name())
                .projectId(t.getProject() != null ? t.getProject().getId() : null)
                .projectName(t.getProject() != null ? t.getProject().getName() : null)
                .assigneeIds(t.getAssignees().stream().map(Employee::getId).collect(Collectors.toList()))
                .assigneeNames(t.getAssignees().stream().map(e -> e.getFirstName() + " " + e.getLastName()).collect(Collectors.toList()))
                .startDate(t.getStartDate())
                .endDate(t.getEndDate())
                .actualCompletionDate(t.getActualCompletionDate())
                .createdById(t.getCreatedBy() != null ? t.getCreatedBy().getId() : null)
                .createdByName(t.getCreatedBy() != null 
                    ? t.getCreatedBy().getFirstName() + " " + t.getCreatedBy().getLastName() 
                    : null)
                .tasks(t.getTasks() != null ? t.getTasks().stream().map(this::mapToTaskDTO).collect(Collectors.toList()): null)
                .build();
    }

    private TicketAttachmentDTO mapToTicketAttachmentDTO(TicketAttachment att) {
        return TicketAttachmentDTO.builder()
                .id(att.getId())
                .ticketId(att.getTicket().getId())
                .fileName(att.getFileName())
                .fileUrl(att.getFileUrl())
                .fileType(att.getFileType())
                .fileSizeKB(att.getFileSizeKB())
                .uploadedById(att.getUploadedBy().getId())
                .uploadedByName(att.getUploadedBy().getFirstName())
                .uploadedAt(att.getUploadedAt())
                .build();
    }

    private TicketActivityDTO mapToActivityDTO(TicketActivity act) {
        return TicketActivityDTO.builder()
                .id(act.getId())
                .ticketId(act.getTicket().getId())
                .employeeId(act.getEmployee().getId())
                .employeeName(act.getEmployee().getFirstName())
                .activityType(act.getType().name())
                .description(act.getDescription())
                .createdAt(act.getCreatedAt())
                .build();
    }


    private TaskDTO mapToTaskDTO(Task task) {
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
                        .map(this::mapToTaskAttachmentDTO)
                        .collect(Collectors.toList())
                    : null)
            .build();
}

private TaskAttachmentDTO mapToTaskAttachmentDTO(TaskAttachment attachment) {
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
}
