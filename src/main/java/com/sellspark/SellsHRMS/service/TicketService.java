package com.sellspark.SellsHRMS.service;

import java.util.List;

import com.sellspark.SellsHRMS.dto.project.TaskActivityDTO;
import com.sellspark.SellsHRMS.dto.project.TicketActivityDTO;
import com.sellspark.SellsHRMS.dto.project.TicketAttachmentDTO;
import com.sellspark.SellsHRMS.dto.project.TicketDTO;

public interface TicketService {

    // -------------------- CORE CRUD --------------------
    TicketDTO createTicket(TicketDTO dto, Long organisationId, Long createdById);
    TicketDTO updateTicket(Long ticketId, TicketDTO dto, Long organisationId, Long employeeId);
    void deleteTicket(Long ticketId, Long organisationId, Long employeeId);

    // -------------------- ASSIGNMENT & STATUS --------------------
    TicketDTO assignTicket(Long ticketId, List<Long> assigneeIds, Long managerId);
    TicketDTO updateTicketStatus(Long ticketId, String status, Long employeeId);

    // -------------------- FETCH / DASHBOARD --------------------
    TicketDTO getTicketById(Long ticketId, Long organisationId, Long employeeId);
    List<TicketDTO> getTicketsByProject(Long projectId, Long organisationId, Long employeeId);
    List<TicketDTO> getTicketsByAssignee(Long employeeId, Long organisationId);
    List<TicketDTO> getIndependentTickets(Long organisationId, Long employeeId);
    List<TicketDTO> searchTickets(Long organisationId, String keyword);

    // Lifecycle/Dashboard filters
    List<TicketDTO> getTicketsByStatus(Long organisationId, String status);
    List<TicketDTO> getDelayedTickets(Long organisationId);
    List<TicketDTO> getAllVisibleToEmployee(Long employeeId, Long organisationId);

    // -------------------- ATTACHMENTS --------------------
    TicketAttachmentDTO addAttachment(Long ticketId, TicketAttachmentDTO dto, Long employeeId);
    List<TicketAttachmentDTO> addAttachments(Long ticketId, List<TicketAttachmentDTO> dtos, Long employeeId);
    List<TicketAttachmentDTO> getAttachments(Long ticketId, Long organisationId);

    // -------------------- ACTIVITY / HISTORY --------------------
    List<TicketActivityDTO> getTicketHistory(Long ticketId);
}
