<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="container py-4">

  <!-- Ticket Header -->
  <div class="card mb-3">
    <div class="card-header d-flex justify-content-between align-items-center">
      <h4 id="ticketTitle"></h4>
      <span id="ticketStatusBadge"></span>
    </div>
    <div class="card-body">
      <p id="ticketDescription"></p>
      <p>
        <strong>Project:</strong> <span id="ticketProject"></span> |
        <strong>Start:</strong> <span id="ticketStartDate"></span> |
        <strong>End:</strong> <span id="ticketEndDate"></span> |
        <strong>Created by:</strong> <span id="ticketCreatedBy"></span>
      </p>
      <p>
        <strong>Assignees:</strong> <span id="ticketAssignees"></span>
      </p>
    </div>
  </div>

  <!-- TASKS SECTION -->
  <div class="card mb-3">
    <div class="card-header d-flex justify-content-between align-items-center">
      <h5>Tasks</h5>
      <button class="btn btn-sm btn-primary" onclick="openTaskModal()">Add Task</button>
    </div>
    <div class="card-body p-0">
      <table class="table table-sm table-striped align-middle mb-0" id="tasksTable">
  <thead>
    <tr>
      <th>Update Title</th>
      <th>Description</th>
      <th>By</th>
      <th>On</th>
      <th>Attachments</th>
      <th>Actions</th>
    </tr>
  </thead>
  <tbody>
    <!-- Rendered via JS -->
  </tbody>
</table>

    </div>
  </div>

  <!-- ATTACHMENTS -->
  <div class="card mb-3">
    <div class="card-header">Attachments</div>
    <div class="card-body" id="ticketAttachmentsList"></div>
  </div>

  <!-- ACTIVITY LOG -->
  <div class="card mb-3">
    <div class="card-header">Activity Log</div>
    <ul class="list-group list-group-flush" id="ticketActivityList"></ul>
  </div>

</div>

<!-- Task Modal -->
<!-- Add Work Update Modal -->
<div class="modal fade" id="taskModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog">
    <form id="taskForm" class="modal-content">
      <div class="modal-header">
        <h5 id="taskModalTitle">Add Work Update</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
            <c:if test="${not empty ticketId}">
                <input type="hidden" name="ticketId" id="taskTicketId" value="${ticketId}">
            </c:if>

        <div class="mb-3">
          <label class="form-label">Title</label>
          <input type="text" class="form-control" name="title" placeholder="E.g., Fixed API issue" required>
        </div>

        <div class="mb-3">
          <label class="form-label">Description</label>
          <textarea class="form-control" name="description" placeholder="Describe what you worked on..." required></textarea>
        </div>

        <!-- Optional attachments -->
        <div id="attachmentRows" class="mb-3">
          <label class="form-label">Attachments</label>
          <button type="button" class="btn btn-sm btn-outline-secondary mb-2" onclick="addAttachmentRow()">
            <i class="fas fa-paperclip me-1"></i> Add Attachment
          </button>
          <!-- dynamically added attachment rows -->
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
        <button type="button" class="btn btn-primary" onclick="saveTicketWorkUpdate()">Save</button>
      </div>
    </form>
  </div>
</div>


<script>
    // Inject directly from JSP model
    window.ticketId = '${ticketId}';

    // Normalize: if EL injected empty or "null", set to actual null
    if (!window.ticketId || window.ticketId === 'null') {
        window.ticketId = null;
    }
</script>


