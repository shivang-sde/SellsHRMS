<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="container py-4">

  <!-- Store Project ID in global JS -->
  <script>
    window.projectId = `${projectId}`;
  </script>

  <!-- PROJECT HEADER -->
  <div class="card shadow-sm mb-4">
    <div class="card-header d-flex justify-content-between align-items-center">
      <h4 class="mb-0" id="projectName"></h4>
      <span id="projectStatusBadge"></span>
    </div>
    <div class="card-body">
      <p id="projectDescription" class="text-muted"></p>
      <div class="row">
        <div class="col-md-6">
          <p><strong>Methodology:</strong> <span id="projectMethodology"></span></p>
          <p><strong>Type:</strong> <span id="projectType"></span></p>
          <p><strong>Priority:</strong> <span id="projectPriority"></span></p>
        </div>
        <div class="col-md-6">
          <p><strong>Start Date:</strong> <span id="projectStartDate"></span></p>
          <p><strong>End Date:</strong> <span id="projectEndDate"></span></p>
          <p><strong>Manager:</strong> <span id="projectManager"></span></p>
          <p><strong>Team Lead:</strong> <span id="projectTeamLead"></span></p>
        </div>
      </div>
    </div>
  </div>

  <!-- PROJECT MEMBERS -->
  <div class="card shadow-sm mb-4">
    <div class="card-header d-flex justify-content-between align-items-center">
      <h5 class="mb-0">Project Members</h5>
      <button class="btn btn-sm btn-outline-primary" id="addMemberBtn" style="display:none;">
        <i class="fas fa-user-plus me-1"></i> Add Member
      </button>
    </div>
    <div class="card-body p-0">
      <table class="table table-striped mb-0" id="membersTable">
        <thead>
          <tr>
            <th>Name</th>
            <th>Department</th>
            <th>Email</th>
          </tr>
        </thead>
        <tbody></tbody>
      </table>
    </div>
  </div>

  <!-- PROJECT TICKETS -->
  <div class="card shadow-sm">
    <div class="card-header d-flex justify-content-between align-items-center">
      <h5 class="mb-0">Project Tickets</h5>
      <button class="btn btn-sm btn-success" id="createTicketBtn" style="display:none;">
        <i class="fas fa-plus me-1"></i> Create Ticket
      </button>
    </div>
    <div class="card-body p-0">
      <table class="table table-hover align-middle mb-0" id="ticketsTable">
        <thead class="table-light">
          <tr>
            <th>Title</th>
            <th>Status</th>
            <th>Assignees</th>
            <th>Start</th>
            <th>End</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody></tbody>
      </table>
    </div>
  </div>

</div>
<!-- CREATE / EDIT TICKET MODAL -->
<div class="modal fade" id="ticketModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-lg">
    <form id="ticketForm" class="modal-content" enctype="multipart/form-data">
      <div class="modal-header">
        <h5 id="ticketModalTitle">Create Ticket</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>

      <div class="modal-body">
        <input type="hidden" name="projectId" id="ticketProjectId">

        <div class="row g-3">
          <div class="col-md-6">
            <label class="form-label">Title</label>
            <input type="text" class="form-control" name="title" required>
          </div>

          <div class="col-md-6">
            <label class="form-label">Status</label>
            <select class="form-select" name="status" required>
              <option value="OPEN">Open</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="ON_HOLD">On Hold</option>
              <option value="COMPLETED">Completed</option>
            </select>
          </div>

          <div class="col-md-12">
            <label class="form-label">Description</label>
            <textarea class="form-control" name="description" rows="3"></textarea>
          </div>

          <div class="col-md-4">
            <label class="form-label">Start Date</label>
            <input type="date" class="form-control" name="startDate" required>
          </div>

          <div class="col-md-4">
            <label class="form-label">End Date</label>
            <input type="date" class="form-control" name="endDate" required>
          </div>

          <div class="col-md-4">
            <label class="form-label">Assignees</label>
            <select multiple class="form-select" id="ticketAssigneesSelect" name="assigneeIds"></select>
          </div>

          <div class="col-md-12">
            <label class="form-label">Attachments</label>
            <input type="file" class="form-control" id="ticketAttachments" name="attachments" multiple>
            <div class="form-text">You can upload multiple files.</div>
          </div>
        </div>
      </div>

      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
        <button type="button" class="btn btn-primary" onclick="saveTicket()">Save</button>
      </div>
    </form>
  </div>
</div>




<!-- ADD MEMBER MODAL -->
<div class="modal fade" id="addMemberModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog">
    <form id="addMemberForm" class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Add Project Members</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        <div class="mb-3">
          <label class="form-label">Select Employees</label>
          <select multiple class="form-select" id="addMemberSelect" required></select>
          <div class="form-text">Hold Ctrl / Cmd to select multiple employees.</div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
        <button type="button" class="btn btn-primary" onclick="saveMembers()">Add Selected</button>
      </div>
    </form>
  </div>
</div>

