<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="tasks-page">
    <!-- Page Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="mb-1"><i class="fas fa-user-check text-warning me-2"></i>My Tasks & Reminders</h2>
            <p class="text-muted mb-0">Create, track, and manage your own tasks or reminders</p>
        </div>
        <button class="btn btn-primary" onclick="openSelfTaskModal()">
            <i class="fas fa-plus me-2"></i>Add Task / Reminder
        </button>
    </div>

    <!-- My Self Tasks Table -->
    <div class="card border-0 shadow-sm">
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-hover align-middle" id="selfTasksTable">
                    <thead class="table-light">
                        <tr>
                            <th>Task Title</th>
                            <th>Status</th>
                            <th>Reminder</th>
                            <th>Created On</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
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

<!-- Task Modal -->
<div class="modal fade" id="selfTaskModal" tabindex="-1">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">
          <i class="fas fa-user-plus me-2"></i>
          <span id="selfTaskModalTitle">Add Task / Reminder</span>
        </h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>

      <div class="modal-body">
        <form id="selfTaskForm">
          <div class="row g-3">

            <div class="col-md-12">
              <label class="form-label">Title <span class="text-danger">*</span></label>
              <input type="text" class="form-control" name="title" required>
            </div>

            <div class="col-12">
              <label class="form-label">Description</label>
              <textarea class="form-control" name="description" rows="3"></textarea>
            </div>

            <div class="col-md-4">
              <label class="form-label">Status</label>
              <select class="form-select" name="status" id="taskStatusSelect">
                <option value="TO_DO">To Do</option>
                <option value="REMINDER">Reminder</option>
                <option value="IN_PROGRESS">In Progress</option>
                <option value="REVIEW">Review</option>
                <option value="DONE">Done</option>
              </select>
            </div>

            <!-- Reminder date (shown only if REMINDER) -->
            <div class="col-md-4" id="reminderGroup" style="display:none;">
              <label class="form-label">Reminder Date & Time</label>
              <input type="datetime-local" class="form-control" name="reminderAt">
            </div>

            <!-- Attachments -->
          <!-- Attachments -->
<div class="col-12">
  <label class="form-label">Attachments</label>

  <!-- Dynamic upload area -->
  <div id="attachmentRows" class="p-3 border rounded">
    <div class="attachment-row d-flex align-items-center mb-2">
      <input type="file" class="form-control me-2" name="attachments">
      <input type="text" class="form-control" name="attachmentDescriptions"
             placeholder="Add note or description (optional)">
      <button type="button" class="btn btn-outline-danger ms-2"
              onclick="$(this).closest('.attachment-row').remove()">
        <i class="fas fa-times"></i>
      </button>
    </div>
  </div>

  <!-- Buttons -->
  <button type="button" class="btn btn-sm btn-outline-primary mt-2" onclick="addAttachmentRow()">
    + Add another file
  </button>
  <small class="text-muted d-block mt-1">
    You can upload multiple files and add optional notes.
  </small>

     <!-- New attachments preview -->
              <div id="newAttachmentsPreview" class="mt-2 text-muted small"></div>

              <!-- Existing attachments (for edit mode) -->
              <div id="existingAttachments" class="mt-3"></div>
</div>           

        </form>
      </div>

      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
        <button type="button" class="btn btn-primary" onclick="saveSelfTask()">Save</button>
      </div>
    </div>
  </div>
</div>
