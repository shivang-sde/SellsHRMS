package com.sellspark.SellsHRMS.controller.api;

import com.sellspark.SellsHRMS.dto.files.FileUploadResponseDTO;
import com.sellspark.SellsHRMS.dto.project.*;
import com.sellspark.SellsHRMS.entity.Employee;
import com.sellspark.SellsHRMS.entity.Ticket;
import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
import com.sellspark.SellsHRMS.exception.employee.EmployeeNotFoundException;
import com.sellspark.SellsHRMS.payload.ApiResponse;
import com.sellspark.SellsHRMS.repository.EmployeeRepository;
import com.sellspark.SellsHRMS.repository.TicketActivityRepository;
import com.sellspark.SellsHRMS.repository.TicketRepository;
import com.sellspark.SellsHRMS.service.TicketService;
import com.sellspark.SellsHRMS.service.files.FileUploadService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for managing Tickets, their attachments, and activity logs.
 */
@Slf4j
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketRestController {

    private final TicketService ticketService;
    private final TicketActivityRepository ticketActivityRepo;
    private final TicketRepository ticketRepo;
    private final EmployeeRepository employeeRepo;

    private final FileUploadService fileUploadService;

    // ----------------------------------------------------------------
    // CREATE
    // ----------------------------------------------------------------
    @PostMapping
    public ResponseEntity<ApiResponse<TicketDTO>> createTicket(
            @RequestBody TicketDTO dto,
            @RequestParam Long organisationId,
            @RequestParam Long createdById) {

        TicketDTO created = ticketService.createTicket(dto, organisationId, createdById);
        return ResponseEntity.ok(ApiResponse.ok("Ticket created successfully", created));
    }

    // ----------------------------------------------------------------
    // UPDATE
    // ----------------------------------------------------------------
    @PutMapping("/{ticketId}")
    public ResponseEntity<ApiResponse<TicketDTO>> updateTicket(
            @PathVariable Long ticketId,
            @RequestBody TicketDTO dto,
            @RequestParam Long organisationId,
            @RequestParam Long employeeId) {

        TicketDTO updated = ticketService.updateTicket(ticketId, dto, organisationId, employeeId);
        return ResponseEntity.ok(ApiResponse.ok("Ticket updated successfully", updated));
    }

    @PutMapping("/{ticketId}/status")
    public ResponseEntity<ApiResponse<TicketDTO>> updateTicketStatus(
            @PathVariable Long ticketId,
            @RequestParam String status,
            @RequestParam Long employeeId) {

        TicketDTO updated = ticketService.updateTicketStatus(ticketId, status, employeeId);
        return ResponseEntity.ok(ApiResponse.ok("Ticket status updated", updated));
    }

    // ----------------------------------------------------------------
    // DELETE
    // ----------------------------------------------------------------
    @DeleteMapping("/{ticketId}")
    public ResponseEntity<ApiResponse<Void>> deleteTicket(
            @PathVariable Long ticketId,
            @RequestParam Long organisationId,
            @RequestParam Long employeeId) {

        ticketService.deleteTicket(ticketId, organisationId, employeeId);
        return ResponseEntity.ok(ApiResponse.ok("Ticket deleted successfully", null));
    }

    // ----------------------------------------------------------------
    // GET SINGLE
    // ----------------------------------------------------------------
    @GetMapping("/{ticketId}")
    public ResponseEntity<ApiResponse<TicketDTO>> getTicketById(
            @PathVariable Long ticketId,
            @RequestParam Long organisationId,
            @RequestParam Long employeeId) {

        TicketDTO ticket = ticketService.getTicketById(ticketId, organisationId, employeeId);
        return ResponseEntity.ok(ApiResponse.ok("Ticket retrieved successfully", ticket));
    }

    // ----------------------------------------------------------------
    // LIST BY PROJECT
    // ----------------------------------------------------------------
    @GetMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<List<TicketDTO>>> getTicketsByProject(
            @PathVariable Long projectId,
            @RequestParam Long organisationId,
            @RequestParam Long employeeId) {

        List<TicketDTO> tickets = ticketService.getTicketsByProject(projectId, organisationId, employeeId);
        return ResponseEntity.ok(ApiResponse.ok("Tickets fetched successfully", tickets));
    }

    // ----------------------------------------------------------------
    // LIST BY ASSIGNEE
    // ----------------------------------------------------------------
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<List<TicketDTO>>> getTicketsByAssignee(
            @PathVariable Long employeeId,
            @RequestParam Long organisationId) {

        List<TicketDTO> tickets = ticketService.getTicketsByAssignee(employeeId, organisationId);
        return ResponseEntity.ok(ApiResponse.ok("Tickets assigned to employee fetched", tickets));
    }

    // ----------------------------------------------------------------
    // LIST INDEPENDENT (non-project tickets)
    // ----------------------------------------------------------------
    @GetMapping("/independent")
    public ResponseEntity<ApiResponse<List<TicketDTO>>> getIndependentTickets(
            @RequestParam Long organisationId,
            @RequestParam Long employeeId) {

        List<TicketDTO> tickets = ticketService.getIndependentTickets(organisationId, employeeId);
        log.info("tickets {}", tickets);
        return ResponseEntity.ok(ApiResponse.ok("Independent tickets fetched successfully", tickets));
    }

    // ----------------------------------------------------------------
    // SUBORDINATE INDEPENDENT TICKETS
    // ----------------------------------------------------------------
    @GetMapping("/subordinates")
    public ResponseEntity<ApiResponse<List<TicketDTO>>> getSubordinateIndependentTickets(
            @RequestParam Long organisationId,
            @RequestParam Long managerId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        List<TicketDTO> tickets = ticketService.getSubordinateIndependentTickets(organisationId, managerId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.ok("Subordinate independent tickets fetched", tickets));
    }

    @PutMapping("/{ticketId}/assign")
    public ResponseEntity<ApiResponse<TicketDTO>> assignTicket(
            @PathVariable Long ticketId,
            @RequestParam List<Long> assigneeIds,
            @RequestParam Long managerId) {

        TicketDTO updated = ticketService.assignTicket(ticketId, assigneeIds, managerId);
        return ResponseEntity.ok(ApiResponse.ok("Ticket assigned successfully", updated));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<TicketDTO>>> getTicketsByStatus(
            @PathVariable String status,
            @RequestParam Long organisationId) {

        List<TicketDTO> tickets = ticketService.getTicketsByStatus(organisationId, status);
        return ResponseEntity.ok(ApiResponse.ok("Tickets fetched by status", tickets));
    }

    @GetMapping("/visible")
    public ResponseEntity<ApiResponse<List<TicketDTO>>> getVisibleTickets(
            @RequestParam Long organisationId,
            @RequestParam Long employeeId) {

        List<TicketDTO> tickets = ticketService.getAllVisibleToEmployee(employeeId, organisationId);
        return ResponseEntity.ok(ApiResponse.ok("Visible tickets fetched successfully", tickets));
    }

    @GetMapping("/{ticketId}/history")
    public ResponseEntity<ApiResponse<List<TicketActivityDTO>>> getTicketHistory(
            @PathVariable Long ticketId) {

        List<TicketActivityDTO> history = ticketService.getTicketHistory(ticketId);
        return ResponseEntity.ok(ApiResponse.ok("Ticket history fetched successfully", history));
    }

    @GetMapping("/delayed")
    public ResponseEntity<ApiResponse<List<TicketDTO>>> getDelayedTickets(
            @RequestParam Long organisationId) {

        List<TicketDTO> tickets = ticketService.getDelayedTickets(organisationId);
        return ResponseEntity.ok(ApiResponse.ok("Delayed tickets fetched successfully", tickets));
    }

    // ----------------------------------------------------------------
    // SEARCH
    // ----------------------------------------------------------------
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<TicketDTO>>> searchTickets(
            @RequestParam Long organisationId,
            @RequestParam String keyword) {

        List<TicketDTO> tickets = ticketService.searchTickets(organisationId, keyword);
        return ResponseEntity.ok(ApiResponse.ok("Ticket search results", tickets));
    }

    // ----------------------------------------------------------------
    // ADD ATTACHMENT
    // ----------------------------------------------------------------
    // @PostMapping("/{ticketId}/attachments")
    // public ResponseEntity<ApiResponse<TicketAttachmentDTO>> addAttachment(
    // @PathVariable Long ticketId,
    // @RequestBody TicketAttachmentDTO dto,
    // @RequestParam Long employeeId) {

    // TicketAttachmentDTO saved = ticketService.addAttachment(ticketId, dto,
    // employeeId);
    // return ResponseEntity.ok(ApiResponse.ok("Attachment added successfully",
    // saved));
    // }

    @PostMapping(value = "/{ticketId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<List<TicketAttachmentDTO>>> addAttachments(
            @PathVariable Long ticketId,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "descriptions", required = false) List<String> descriptions,
            @RequestParam Long employeeId) {

        // -------------------------
        // 1️⃣ Validate entities
        // -------------------------
        Employee uploader = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        Ticket ticket = ticketRepo.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        Long orgId = uploader.getOrganisation().getId();

        // -------------------------
        // 2️⃣ Upload all files centrally (no manual Files.copy)
        // -------------------------
        List<FileUploadResponseDTO> uploadedFiles = fileUploadService.uploadMultipartFiles(
                files,
                "tickets",
                "org-" + orgId + "/tickets/ticket-" + ticket.getId());

        // -------------------------
        // 3️⃣ Build DTOs and persist each attachment
        // -------------------------
        List<TicketAttachmentDTO> savedAttachments = new ArrayList<>();

        for (int i = 0; i < uploadedFiles.size(); i++) {
            FileUploadResponseDTO fileResp = uploadedFiles.get(i);
            String desc = (descriptions != null && descriptions.size() > i) ? descriptions.get(i) : null;

            TicketAttachmentDTO dto = TicketAttachmentDTO.builder()
                    .fileName(fileResp.getFileName())
                    .fileUrl(fileResp.getFileUrl())
                    .fileType(fileResp.getFileType())
                    .fileSizeKB(fileResp.getFileSizeKB())
                    .description(desc)
                    .build();

            // ✅ Each saved in REQUIRES_NEW transaction → no deadlocks
            TicketAttachmentDTO savedDto = ticketService.saveAttachmentIsolated(ticketId, dto, employeeId);
            savedAttachments.add(savedDto);
        }

        // -------------------------
        // 4️⃣ Return unified response
        // -------------------------
        return ResponseEntity.ok(ApiResponse.ok("Attachments uploaded successfully", savedAttachments));
    }

    // ----------------------------------------------------------------
    // GET ATTACHMENTS
    // ----------------------------------------------------------------
    @GetMapping("/{ticketId}/attachments")
    public ResponseEntity<ApiResponse<List<TicketAttachmentDTO>>> getAttachments(
            @PathVariable Long ticketId,
            @RequestParam Long organisationId) {

        List<TicketAttachmentDTO> attachments = ticketService.getAttachments(ticketId, organisationId);
        return ResponseEntity.ok(ApiResponse.ok("Attachments fetched successfully", attachments));
    }

    // ----------------------------------------------------------------
    // GET ACTIVITY LOG
    // ----------------------------------------------------------------
    @GetMapping("/{ticketId}/activities")
    public ResponseEntity<ApiResponse<List<TicketActivityDTO>>> getTicketActivities(
            @PathVariable Long ticketId) {

        List<TicketActivityDTO> activities = ticketActivityRepo.findByTicketIdOrderByCreatedAtAsc(ticketId)
                .stream()
                .map(a -> TicketActivityDTO.builder()
                        .id(a.getId())
                        .ticketId(a.getTicket().getId())
                        .employeeId(a.getEmployee().getId())
                        .employeeName(a.getEmployee().getFirstName() + " " + a.getEmployee().getLastName())
                        .activityType(a.getType().name())
                        .description(a.getDescription())
                        .createdAt(a.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.ok("Ticket activities fetched successfully", activities));
    }
}
