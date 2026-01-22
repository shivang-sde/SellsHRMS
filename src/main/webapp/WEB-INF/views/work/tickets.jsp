<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="tickets-page">
    <!-- Page Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="mb-1"><i class="fas fa-ticket-alt text-info me-2"></i>Tickets</h2>
            <p class="text-muted mb-0">Track and resolve support tickets</p>
        </div>
        <button class="btn btn-primary" onclick="openTicketModal()">
            <i class="fas fa-plus me-2"></i>New Ticket
        </button>
    </div>

    <!-- Filters -->
    <div class="card border-0 shadow-sm mb-3">
        <div class="card-body">
            <div class="row g-3">
                <div class="col-md-3">
                    <input type="text" class="form-control" id="ticketSearchInput" 
                           placeholder="Search tickets...">
                </div>
                <div class="col-md-2">
                    <select class="form-select" id="ticketStatusFilter">
                        <option value="">All Status</option>
                        <option value="OPEN">Open</option>
                        <option value="IN_PROGRESS">In Progress</option>
                        <option value="RESOLVED">Resolved</option>
                        <option value="CLOSED">Closed</option>
                    </select>
                </div>
                <div class="col-md-2">
                    <select class="form-select" id="ticketPriorityFilter">
                        <option value="">All Priorities</option>
                        <option value="HIGH">High</option>
                        <option value="MEDIUM">Medium</option>
                        <option value="LOW">Low</option>
                    </select>
                </div>
                <div class="col-md-2">
                    <select class="form-select" id="ticketTypeFilter">
                        <option value="">All Types</option>
                        <option value="BUG">Bug</option>
                        <option value="FEATURE">Feature</option>
                        <option value="SUPPORT">Support</option>
                        <option value="INQUIRY">Inquiry</option>
                    </select>
                </div>
                <div class="col-md-3">
                    <div class="btn-group w-100">
                        <button class="btn btn-outline-secondary" onclick="loadMyTickets()">
                            <i class="fas fa-user me-2"></i>My Tickets
                        </button>
                        <button class="btn btn-outline-secondary" onclick="resetTicketFilters()">
                            <i class="fas fa-redo"></i>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Tickets Table -->
    <div class="card border-0 shadow-sm">
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-hover align-middle" id="ticketsTable">
                    <thead class="table-light text-uppercase small">
    <tr>
      <th>Title</th>
      <th>Status</th>
      <th>Reporter</th>
      <th>Start Date</th>
      <th>Expiry Date</th>
      <th>Actions</th>
    </tr>
  </thead>
                    <tbody class="table-border-bottom-0">
                        <tr>
                            <td colspan="6" class="text-center py-5">
                                <div class="spinner-border text-primary" role="status">
                                    <span class="visually-hidden">Loading...</span>
                                </div>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div> 
    </div>
</div>

<!-- Ticket Modal -->
<div class="modal fade" id="ticketModal" tabindex="-1">
  <div class="modal-dialog modal-xl">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">
          <i class="fas fa-ticket-alt me-2"></i>
          <span id="ticketModalTitle">Create / Edit Ticket</span>
        </h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>

      <div class="modal-body">
        <!-- Nav Tabs -->
        <ul class="nav nav-tabs mb-3" id="ticketTabs" role="tablist">
          <li class="nav-item" role="presentation">
            <button class="nav-link active" id="ticketDetailsTab" data-bs-toggle="tab" data-bs-target="#ticketDetails" type="button" role="tab">Details</button>
          </li>
          <li class="nav-item" role="presentation">
            <button class="nav-link" id="ticketAttachmentsTab" data-bs-toggle="tab" data-bs-target="#ticketAttachments" type="button" role="tab">Attachments</button>
          </li>
          <li class="nav-item" role="presentation">
            <button class="nav-link" id="ticketActivityTab" data-bs-toggle="tab" data-bs-target="#ticketActivity" type="button" role="tab">Activity Log</button>
          </li>
        </ul>

        <!-- Tab Contents -->
        <div class="tab-content">
          <!-- DETAILS TAB -->
          <div class="tab-pane fade show active" id="ticketDetails" role="tabpanel">
            <form id="ticketForm">
              <div class="row g-3">
                <div class="col-md-12">
                  <label class="form-label">Title <span class="text-danger">*</span></label>
                  <input type="text" class="form-control" name="title" required>
                </div>

                <div class="col-md-6">
                  <label class="form-label">Status <span class="text-danger">*</span></label>
                  <select class="form-select" name="status" required>
                    <option value="OPEN">Open</option>
                    <option value="IN_PROGRESS">In Progress</option>
                    <option value="ON_HOLD">On Hold</option>
                    <option value="COMPLETED">Completed</option>
                    <option value="CANCELLED">Cancelled</option>
                  </select>
                </div>

                <div class="col-md-6">
                  <label class="form-label">Project (optional)</label>
                  <select class="form-select" name="projectId">
                    <option value="">Independent Ticket</option>
                  </select>
                </div>

                <div class="col-12">
                  <label class="form-label">Description</label>
                  <textarea class="form-control" name="description" rows="4"></textarea>
                </div>

                <div class="col-md-6">
                  <label class="form-label">Start Date</label>
                  <input type="date" class="form-control" name="startDate">
                </div>
                <div class="col-md-6">
                  <label class="form-label">End Date</label>
                  <input type="date" class="form-control" name="endDate">
                </div>

                <div class="col-12">
                  <label class="form-label">Assign To (Multiple)</label>
                  <select class="form-select" id="ticketAssigneesSelect" name="assigneeIds" multiple></select>
                  <small class="text-muted">Only your subordinates can be assigned.</small>
                </div>
              </div>
            </form>
          </div>

         <!-- ATTACHMENTS TAB -->
<div class="tab-pane fade" id="ticketAttachments" role="tabpanel">
  <div id="attachmentsUploadArea" class="p-3 border rounded">
    <div id="attachmentRows">
      <div class="attachment-row d-flex align-items-center mb-2">
        <input type="file" class="form-control me-2" name="attachments" required>
        <input type="text" class="form-control" name="attachmentDescriptions" placeholder="Add note or description (optional)">
      </div>
    </div>
    <button type="button" class="btn btn-sm btn-outline-primary" onclick="addAttachmentRow()">+ Add another file</button>
    <button type="button" class="btn btn-sm btn-primary ms-2" onclick="uploadTicketAttachments(editingTicketId)">Upload All</button>
  </div>

  <hr>

  <h6 class="fw-semibold">Uploaded Attachments</h6>
  <div id="ticketAttachmentsList" class="mt-3"></div>
</div>


          <!-- ACTIVITY TAB -->
          <div class="tab-pane fade" id="ticketActivity" role="tabpanel">
            <ul id="ticketActivityList" class="list-group"></ul>
          </div>
        </div>
      </div>

      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
        <button type="button" class="btn btn-primary" onclick="saveTicket()">Save Ticket</button>
      </div>
    </div>
  </div>
</div>
