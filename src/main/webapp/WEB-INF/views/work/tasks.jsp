<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
  <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

    <div class="tasks-page">
      <!-- Page Header -->
      <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 class="mb-1"><i class="fas fa-user-check text-warning me-2"></i>Tasks & Reminders</h2>
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

      <!-- ============================================ -->
      <!-- SUBORDINATE TASKS SECTION (shown if manager) -->
      <!-- ============================================ -->
      <div id="subordinateTasksSection" class="mt-5" style="display: none;">
        <div class="d-flex justify-content-between align-items-center mb-3">
          <div>
            <h3 class="mb-1">
              <i class="fas fa-users text-info me-2"></i>Team Tasks
            </h3>
            <p class="text-muted mb-0">Tasks created or worked on by your subordinates</p>
          </div>
          <!-- Filter controls -->
          <div class="d-flex gap-2 align-items-center">
            <div class="input-group input-group-sm" style="width: 240px;">
              <span class="input-group-text"><i class="fas fa-search"></i></span>
              <input type="text" class="form-control" id="subTaskSearch" placeholder="Search tasks..."
                oninput="filterSubordinateTasks()">
            </div>
            <select class="form-select form-select-sm" id="subTaskStatusFilter" style="width: 160px;"
              onchange="filterSubordinateTasks()">
              <option value="">All Statuses</option>
              <option value="TO_DO">To Do</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="REVIEW">Review</option>
              <option value="DONE">Done</option>
              <option value="BACKLOG">Backlog</option>
              <option value="REMINDER">Reminder</option>
            </select>
            <select class="form-select form-select-sm" id="subTaskEmployeeFilter" style="width: 180px;"
              onchange="filterSubordinateTasks()">
              <option value="">All Employees</option>
            </select>
          </div>
        </div>

        <div class="card border-0 shadow-sm">
          <div class="card-body p-0">
            <div class="table-responsive">
              <table class="table table-hover align-middle mb-0" id="subordinateTasksTable">
                <thead class="table-light">
                  <tr>
                    <th>Task Title</th>
                    <th>Employee</th>
                    <th>Status</th>
                    <th>Type</th>
                    <th>Reminder</th>
                    <th>Created On</th>
                    <th class="text-center">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td colspan="7" class="text-center py-5">
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
    </div>

    <!-- =============================== -->
    <!-- SELF TASK CREATE/EDIT MODAL      -->
    <!-- =============================== -->
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
                      <span id="currentAttachment"></span>
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

    <!-- =============================== -->
    <!-- SUBORDINATE TASK DETAIL MODAL   -->
    <!-- =============================== -->
    <div class="modal fade" id="subTaskDetailModal" tabindex="-1">
      <div class="modal-dialog modal-lg modal-dialog-scrollable">
        <div class="modal-content">
          <div class="modal-header bg-light">
            <h5 class="modal-title">
              <i class="fas fa-clipboard-list text-info me-2"></i>
              <span id="subTaskDetailTitle">Task Details</span>
            </h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body" id="subTaskDetailBody">
            <div class="text-center py-5">
              <div class="spinner-border text-primary" role="status"></div>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
            <a href="#" id="subTaskDetailLink" class="btn btn-primary">
              <i class="fas fa-external-link-alt me-1"></i>Open Full Page
            </a>
          </div>
        </div>
      </div>
    </div>