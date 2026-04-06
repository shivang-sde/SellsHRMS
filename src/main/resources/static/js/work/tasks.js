let selfTasks = [];
let editingTaskId = null;
let removedAttachmentIds = [];
let existingAttachments = [];

// Subordinate tasks state
let subordinateTasks = [];
let filteredSubordinateTasks = [];

// --------------------------------------------
// INITIALIZE
// --------------------------------------------
$(document).ready(function () {
  loadSelfTasks();
  loadSubordinateTasks();

  // Toggle Reminder field dynamically
  $("#taskStatusSelect").on("change", function () {
    const status = $(this).val();
    if (status === "REMINDER") {
      $("#reminderGroup").show();
      $('[name="reminderAt"]').attr("required", true);
    } else {
      $("#reminderGroup").hide();
      $('[name="reminderAt"]').removeAttr("required").val("");
    }
  });

  // Show new attachments preview
  $('[name="attachments"]').on("change", function () {
    const files = Array.from(this.files);
    const preview = $("#newAttachmentsPreview");
    if (files.length) {
      preview.html(
        files
          .map((f) => `<div><i class="fas fa-file me-2"></i>${f.name}</div>`)
          .join(""),
      );
    } else {
      preview.empty();
    }
  });
});

// --------------------------------------------
// LOAD SELF TASKS
// --------------------------------------------
async function loadSelfTasks() {
  try {
    loadingUtils.show("#selfTasksTable tbody");
    const res = await taskAPI.getMyTasks(window.APP.EMPLOYEE_ID);
    const data = res?.data || res;
    selfTasks = Array.isArray(data) ? data : [];
    renderSelfTasks(selfTasks);
  } catch (error) {
    console.error("Failed to load self tasks", error);
    showToast("error", "Failed to load tasks");
  } finally {
    loadingUtils.hide();
  }
}

// --------------------------------------------
// RENDER TASKS TABLE
// --------------------------------------------
function renderSelfTasks(tasks) {
  const tbody = $("#selfTasksTable tbody");
  if (!tasks.length) {
    tbody.html(`
      <tr>
        <td colspan="6" class="text-center text-muted py-5">
          <i class="fas fa-tasks fa-3x mb-3 opacity-50"></i>
          <p>No personal tasks found</p>
        </td>
      </tr>
    `);
    return;
  }

  const html = tasks
    .map(
      (task) => `
    <tr>
      <td>${task.title}</td>
      <td>${getStatusBadge(task.status)}</td>
      <td>${task.reminderAt ? formatDateTime(task.reminderAt) : '<span class="text-muted">-</span>'}</td>
      <td>${formatDate(task.createdAt)}</td>
      <td>
        <div class="btn-group btn-group-sm">
          <button class="btn btn-outline-warning" onclick="editSelfTask(${task.id})"><i class="fas fa-edit"></i></button>
          <button class="btn btn-outline-danger" onclick="deleteSelfTask(${task.id})"><i class="fas fa-trash"></i></button>
        </div>
      </td>
    </tr>
  `,
    )
    .join("");

  tbody.html(html);
}

// --------------------------------------------
// OPEN MODAL FOR NEW TASK
// --------------------------------------------
function openSelfTaskModal() {
  editingTaskId = null;
  removedAttachmentIds = [];
  existingAttachments = [];

  $("#selfTaskModalTitle").text("Add Task / Reminder");
  modalUtils.resetForm("selfTaskModal");
  $("#existingAttachments").empty();
  $("#newAttachmentsPreview").empty();
  $('[name="status"]').val("TO_DO");
  modalUtils.open("selfTaskModal");
}

// --------------------------------------------
// SAVE OR UPDATE TASK
// --------------------------------------------
// --------------------------------------------
// SAVE OR UPDATE SELF TASK / REMINDER
// --------------------------------------------
async function saveSelfTask() {
  const form = document.getElementById("selfTaskForm");
  if (!form.checkValidity()) {
    form.reportValidity();
    return;
  }

  // 1️⃣ Base Task Object
  const formFields = Object.fromEntries(new FormData(form).entries());

  const taskData = {
    createdById: window.APP.EMPLOYEE_ID,
    reporterId: window.APP.EMPLOYEE_ID,
    isSelfTask: true,
    title: formFields.title || "",
    description: formFields.description || "",
    status: formFields.status || "TO_DO",
    reminderAt: formFields.reminderAt || null,
    reminderEnabled: formFields.status === "REMINDER",
  };

  // 2️⃣ Build FormData
  const formData = new FormData();
  formData.append(
    "task",
    new Blob([JSON.stringify(taskData)], { type: "application/json" }),
  );

  // 3️⃣ Append new attachments with their descriptions
  const attachmentRows = document.querySelectorAll(
    "#attachmentRows .attachment-row",
  );
  attachmentRows.forEach((row, i) => {
    const fileInput = row.querySelector('input[name="attachments"]');
    const descInput = row.querySelector('input[name="attachmentDescriptions"]');

    if (fileInput?.files.length) {
      formData.append("attachments", fileInput.files[0]); // file
      formData.append("descriptions", descInput?.value?.trim() || ""); // description
    }
  });

  // 4️⃣ Append removed attachment IDs (edit mode)
  if (Array.isArray(removedAttachmentIds) && removedAttachmentIds.length) {
    removedAttachmentIds.forEach((id) =>
      formData.append("removeAttachmentIds", id),
    );
  }

  try {
    loadingUtils.show();

    // 5️⃣ Determine endpoint
    const url = editingTaskId
      ? `${window.APP.CONTEXT_PATH}/api/tasks/${editingTaskId}?organisationId=${organisationId}&employeeId=${employeeId}`
      : `${window.APP.CONTEXT_PATH}/api/tasks?organisationId=${organisationId}&reporterId=${employeeId}`;

    const method = editingTaskId ? "PUT" : "POST";

    // 6️⃣ Send multipart request
    const response = await fetch(url, {
      method,
      body: formData,
    });

    const result = await response.json();

    if (!response.ok) throw new Error(result?.message || "Failed to save task");

    showToast("success", "Task saved successfully");
    modalUtils.close("selfTaskModal");

    // Refresh tasks table
    loadSelfTasks();

    // Reset form state
    editingTaskId = null;
    removedAttachmentIds = [];
    existingAttachments = [];
    $("#attachmentRows").empty();
    addAttachmentRow(); // initial blank row
    $("#newAttachmentsPreview").empty();
  } catch (error) {
    console.error("Error saving task:", error);
    showToast("error", error.message || "Failed to save task");
  } finally {
    loadingUtils.hide();
  }
}

// --------------------------------------------
// EDIT EXISTING TASK
// --------------------------------------------
async function editSelfTask(taskId) {
  try {
    editingTaskId = taskId;
    removedAttachmentIds = [];

    // 1️⃣ Fetch task details
    const res = await taskAPI.getById(taskId);
    const task = res?.data?.data || res?.data || res;

    // 2️⃣ Reset form
    modalUtils.resetForm("selfTaskModal");

    // 3️⃣ Populate basic fields
    Object.keys(task).forEach((key) => {
      const input = $(`[name="${key}"]`);
      if (input.length) {
        const type = input.attr("type");

        if (type === "file") {
          // ❌ Do not set .val() on file inputs
          // Instead, show the filename elsewhere if needed
          return;
        }

        if (type === "datetime-local" && task[key]) {
          input.val(task[key].slice(0, 16));
        } else if (input.is("select")) {
          input.val(task[key]);
        } else {
          input.val(task[key] ?? "");
        }
      }
    });

    $("#currentAttachment").html(
      task.attachments?.map((a) => `<div>${a.filename}</div>`).join("") ||
        "No files uploaded",
    );

    // 4️⃣ Fetch existing attachments
    const attachRes = await taskAPI.getAttachments(taskId);
    existingAttachments = attachRes?.data || attachRes || [];
    renderExistingAttachments(existingAttachments);

    // 5️⃣ Reset new attachment rows
    $("#attachmentRows").empty();
    addAttachmentRow(); // Add initial empty row

    // 6️⃣ Show modal
    $("#selfTaskModalTitle").text("Edit Task / Reminder");
    modalUtils.open("selfTaskModal");
  } catch (error) {
    console.error("Edit task error:", error);
    showToast("error", "Failed to load task details");
  }
}

// --------------------------------------------
// RENDER EXISTING ATTACHMENTS
// --------------------------------------------
function renderExistingAttachments(attachments) {
  const container = $("#existingAttachments");
  container.empty();

  if (!attachments.length) {
    container.html('<p class="text-muted">No attachments yet.</p>');
    return;
  }

  const html = attachments
    .map(
      (a) => `
    <div class="attachment-item d-flex justify-content-between align-items-center border rounded p-2 mb-2">
      <a href="${window.APP.CONTEXT_PATH}${a.fileUrl}" target="_blank">
        <i class="fas fa-paperclip me-2"></i>${a.fileName}
      </a>
      <button type="button" class="btn btn-sm btn-outline-danger" onclick="removeAttachment(${a.id})">
        <i class="fas fa-times"></i>
      </button>
    </div>
  `,
    )
    .join("");

  container.html(html);
}

function removeAttachment(id) {
  // Mark attachment as removed
  removedAttachmentIds.push(id);

  // Remove from DOM
  $(`button[onclick="removeAttachment(${id})"]`)
    .closest(".attachment-item")
    .remove();
}

// --------------------------------------------
// DELETE TASK
// --------------------------------------------
async function deleteSelfTask(id) {
  modalUtils.confirm(
    "Delete Task",
    "Are you sure you want to delete this task?",
    async () => {
      try {
        await taskAPI.delete(id);
        showToast("success", "Task deleted successfully");
        loadSelfTasks();
      } catch (error) {
        showToast("error", "Failed to delete task");
      }
    },
  );
}

function addAttachmentRow() {
  $("#attachmentRows").append(`
    <div class="attachment-row d-flex align-items-center mb-2">
      <input type="file" class="form-control me-2" name="attachments">
      <input type="text" class="form-control" name="attachmentDescriptions"
             placeholder="Add note or description (optional)">
      <button type="button" class="btn btn-outline-danger ms-2"
              onclick="$(this).closest('.attachment-row').remove()">
        <i class="fas fa-times"></i>
      </button>
    </div>
  `);
}


// ============================================================
// SUBORDINATE TASKS SECTION
// ============================================================

/**
 * Load tasks from all subordinates of the current employee.
 * If there are no subordinates, the section stays hidden.
 */
async function loadSubordinateTasks() {
  try {
    const res = await taskAPI.getSubordinateTasks(window.APP.EMPLOYEE_ID);
    const data = res?.data || res;
    subordinateTasks = Array.isArray(data) ? data : [];

    if (subordinateTasks.length > 0) {
      $("#subordinateTasksSection").show();
      populateEmployeeFilter(subordinateTasks);
      filteredSubordinateTasks = [...subordinateTasks];
      renderSubordinateTasks(filteredSubordinateTasks);
    } else {
      // Check if the API returned empty because there are truly no subordinates
      // vs there are subordinates but no tasks — still show the section header
      try {
        const subCheck = await employeeAPI.getSubordinates(window.APP.EMPLOYEE_ID);
        const subs = subCheck?.data || subCheck;
        if (Array.isArray(subs) && subs.length > 0) {
          // Has subordinates but no tasks yet
          $("#subordinateTasksSection").show();
          renderSubordinateTasks([]);
        }
      } catch {
        // No subordinates — hide the section
      }
    }
  } catch (error) {
    console.error("Failed to load subordinate tasks", error);
  }
}

/**
 * Populate the employee dropdown filter with unique employee names from the task list.
 */
function populateEmployeeFilter(tasks) {
  const select = $("#subTaskEmployeeFilter");
  const seen = new Map();

  tasks.forEach((t) => {
    // Collect unique employees who are createdBy, assignee, or reporter
    if (t.createdById && t.createdByName && !seen.has(t.createdById)) {
      seen.set(t.createdById, t.createdByName);
    }
    if (t.assigneeId && t.assigneeName && !seen.has(t.assigneeId)) {
      seen.set(t.assigneeId, t.assigneeName);
    }
  });

  // Keep existing "All Employees" option, add the rest
  select.find("option:not(:first)").remove();
  seen.forEach((name, id) => {
    select.append(`<option value="${id}">${name}</option>`);
  });
}

/**
 * Filter subordinate tasks based on search text, status, and employee dropdowns.
 */
function filterSubordinateTasks() {
  const searchTerm = ($("#subTaskSearch").val() || "").toLowerCase().trim();
  const statusFilter = $("#subTaskStatusFilter").val();
  const employeeFilter = $("#subTaskEmployeeFilter").val();

  filteredSubordinateTasks = subordinateTasks.filter((task) => {
    // Search filter: matches title, description, or employee name
    if (searchTerm) {
      const matchesSearch =
        (task.title || "").toLowerCase().includes(searchTerm) ||
        (task.description || "").toLowerCase().includes(searchTerm) ||
        (task.createdByName || "").toLowerCase().includes(searchTerm) ||
        (task.assigneeName || "").toLowerCase().includes(searchTerm) ||
        (task.reporterName || "").toLowerCase().includes(searchTerm);
      if (!matchesSearch) return false;
    }

    // Status filter
    if (statusFilter && task.status !== statusFilter) return false;

    // Employee filter: match against createdBy, assignee, or reporter
    if (employeeFilter) {
      const empId = parseInt(employeeFilter);
      if (task.createdById !== empId && task.assigneeId !== empId && task.reporterId !== empId) {
        return false;
      }
    }

    return true;
  });

  renderSubordinateTasks(filteredSubordinateTasks);
}

/**
 * Render the subordinate tasks table.
 */
function renderSubordinateTasks(tasks) {
  const tbody = $("#subordinateTasksTable tbody");

  if (!tasks || !tasks.length) {
    tbody.html(`
      <tr>
        <td colspan="7" class="text-center text-muted py-5">
          <i class="fas fa-clipboard-list fa-3x mb-3 opacity-50"></i>
          <p class="mb-0">No subordinate tasks found</p>
        </td>
      </tr>
    `);
    return;
  }

  const html = tasks
    .map((task) => {
      // Determine the employee name to show (who the task belongs to)
      const employeeName = task.createdByName || task.assigneeName || task.reporterName || "Unknown";

      // Task type indicator
      const typeLabel = task.isSelfTask
        ? '<span class="badge bg-secondary bg-opacity-25 text-secondary">Self Task</span>'
        : task.projectName
          ? `<span class="badge bg-primary bg-opacity-25 text-primary">${escapeHtml(task.projectName)}</span>`
          : '<span class="badge bg-info bg-opacity-25 text-info">General</span>';

      return `
        <tr>
          <td>
            <div class="fw-semibold">${escapeHtml(task.title)}</div>
            ${task.description ? `<small class="text-muted text-truncate d-block" style="max-width:300px;">${escapeHtml(task.description.substring(0, 80))}${task.description.length > 80 ? '...' : ''}</small>` : ''}
          </td>
          <td>
            <div class="d-flex align-items-center">
              <i class="fas fa-user-circle text-muted me-2"></i>
              <span>${escapeHtml(employeeName)}</span>
            </div>
          </td>
          <td>${getStatusBadge(task.status)}</td>
          <td>${typeLabel}</td>
          <td>${task.reminderAt ? formatDateTime(task.reminderAt) : '<span class="text-muted">-</span>'}</td>
          <td>${formatDate(task.createdAt)}</td>
          <td class="text-center">
            <button class="btn btn-sm btn-outline-info" onclick="viewSubordinateTaskDetail(${task.id})"
                    title="View Details">
              <i class="fas fa-eye"></i>
            </button>
          </td>
        </tr>
      `;
    })
    .join("");

  tbody.html(html);
}

/**
 * View a subordinate task's full details in a modal.
 */
async function viewSubordinateTaskDetail(taskId) {
  const modal = new bootstrap.Modal(document.getElementById("subTaskDetailModal"));
  const body = $("#subTaskDetailBody");

  // Show spinner
  body.html(`
    <div class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>
  `);
  modal.show();

  try {
    const res = await taskAPI.getById(taskId);
    const task = res?.data?.data || res?.data || res;

    // Set modal title & link
    $("#subTaskDetailTitle").text(task.title || "Task Details");
    $("#subTaskDetailLink").attr("href", `${window.APP.CONTEXT_PATH}/work/tasks/${taskId}`);

    // Build detail HTML
    const detailHtml = buildSubTaskDetailHtml(task);
    body.html(detailHtml);

    // Load attachments into the detail modal
    try {
      const attachRes = await taskAPI.getAttachments(taskId);
      const attachments = attachRes?.data || attachRes || [];
      renderSubTaskAttachments(attachments);
    } catch {
      $("#subTaskAttachmentsList").html('<p class="text-muted small">Could not load attachments.</p>');
    }

    // Load activity log
    try {
      const actRes = await taskAPI.getActivities(taskId);
      const activities = actRes?.data || actRes || [];
      renderSubTaskActivities(activities);
    } catch {
      $("#subTaskActivityLog").html('<p class="text-muted small">Could not load activity log.</p>');
    }

  } catch (error) {
    console.error("Failed to load task details:", error);
    body.html(`
      <div class="text-center text-danger py-5">
        <i class="fas fa-exclamation-triangle fa-3x mb-3"></i>
        <p>Failed to load task details</p>
      </div>
    `);
  }
}

/**
 * Build the HTML for a subordinate task detail view inside the modal.
 */
function buildSubTaskDetailHtml(task) {
  return `
    <div class="row g-4">
      <!-- Info Cards Row -->
      <div class="col-md-6">
        <div class="border rounded p-3 h-100">
          <h6 class="text-muted mb-2"><i class="fas fa-info-circle me-1"></i> Status & Type</h6>
          <div class="mb-2">${getStatusBadge(task.status)}</div>
          <div class="small text-muted">
            ${task.isSelfTask ? '<span class="badge bg-secondary">Self Task</span>' : ''}
            ${task.projectName ? `<span class="badge bg-primary">${escapeHtml(task.projectName)}</span>` : ''}
            ${task.ticketTitle ? `<span class="badge bg-warning text-dark">Ticket: ${escapeHtml(task.ticketTitle)}</span>` : ''}
          </div>
        </div>
      </div>

      <div class="col-md-6">
        <div class="border rounded p-3 h-100">
          <h6 class="text-muted mb-2"><i class="fas fa-users me-1"></i> People</h6>
          <div class="d-flex flex-column gap-1 small">
            <div><strong>Created By:</strong> ${escapeHtml(task.createdByName || 'N/A')}</div>
            <div><strong>Reporter:</strong> ${escapeHtml(task.reporterName || 'N/A')}</div>
            <div><strong>Assignee:</strong> ${escapeHtml(task.assigneeName || 'Unassigned')}</div>
          </div>
        </div>
      </div>

      <!-- Description -->
      <div class="col-12">
        <h6 class="text-muted mb-2"><i class="fas fa-align-left me-1"></i> Description</h6>
        <div class="border rounded p-3 bg-light" style="min-height: 60px;">
          ${task.description ? escapeHtml(task.description) : '<em class="text-muted">No description provided</em>'}
        </div>
      </div>

      <!-- Dates -->
      <div class="col-md-4">
        <div class="small">
          <strong><i class="fas fa-calendar-plus me-1"></i> Created:</strong><br>
          ${formatDateTime(task.createdAt)}
        </div>
      </div>
      <div class="col-md-4">
        <div class="small">
          <strong><i class="fas fa-calendar-check me-1"></i> Updated:</strong><br>
          ${task.updatedAt ? formatDateTime(task.updatedAt) : '<span class="text-muted">—</span>'}
        </div>
      </div>
      ${task.reminderAt ? `
        <div class="col-md-4">
          <div class="small">
            <strong><i class="fas fa-bell me-1"></i> Reminder:</strong><br>
            ${formatDateTime(task.reminderAt)}
          </div>
        </div>
      ` : ''}

      <!-- Attachments section -->
      <div class="col-12">
        <h6 class="text-muted mb-2"><i class="fas fa-paperclip me-1"></i> Attachments</h6>
        <div id="subTaskAttachmentsList">
          <div class="text-center py-2">
            <div class="spinner-border spinner-border-sm text-primary"></div>
          </div>
        </div>
      </div>

      <!-- Activity log -->
      <div class="col-12">
        <h6 class="text-muted mb-2"><i class="fas fa-history me-1"></i> Activity Log</h6>
        <div id="subTaskActivityLog" style="max-height: 200px; overflow-y: auto;">
          <div class="text-center py-2">
            <div class="spinner-border spinner-border-sm text-primary"></div>
          </div>
        </div>
      </div>
    </div>
  `;
}

/**
 * Render attachments inside the subordinate task detail modal.
 */
function renderSubTaskAttachments(attachments) {
  const container = $("#subTaskAttachmentsList");

  if (!attachments || !attachments.length) {
    container.html('<p class="text-muted small mb-0">No attachments.</p>');
    return;
  }

  const html = attachments
    .map(
      (a) => `
      <div class="d-flex align-items-center gap-2 mb-2 p-2 border rounded">
        <i class="fas fa-file text-muted"></i>
        <div class="flex-grow-1 small">
          <a href="${window.APP.CONTEXT_PATH}${a.fileUrl}" target="_blank" class="text-decoration-none">
            ${escapeHtml(a.fileName)}
          </a>
          ${a.description ? `<div class="text-muted">${escapeHtml(a.description)}</div>` : ''}
        </div>
        ${a.fileSizeKB ? `<span class="text-muted small">${Math.round(a.fileSizeKB)} KB</span>` : ''}
      </div>
    `,
    )
    .join("");

  container.html(html);
}

/**
 * Render activity log inside the subordinate task detail modal.
 */
function renderSubTaskActivities(activities) {
  const container = $("#subTaskActivityLog");

  if (!activities || !activities.length) {
    container.html('<p class="text-muted small mb-0">No activity yet.</p>');
    return;
  }

  const icons = {
    TASK_CREATED: "plus-circle",
    TASK_UPDATED: "edit",
    STATUS_CHANGED: "exchange-alt",
    ATTACHMENT_UPLOADED: "paperclip",
    ATTACHMENT_REMOVED: "times-circle",
    REMINDER_SET: "bell",
    REMINDER_TOGGLE: "bell-slash",
    COMMENT: "comment",
  };

  const html = activities
    .map(
      (a) => `
      <div class="d-flex gap-2 mb-2 small">
        <div class="flex-shrink-0">
          <i class="fas fa-${icons[a.activityType] || 'circle'} text-primary mt-1"></i>
        </div>
        <div class="flex-grow-1">
          <strong>${escapeHtml(a.employeeName || 'System')}</strong>
          <span class="text-muted ms-1">${escapeHtml(a.activityType || '').replace(/_/g, ' ')}</span>
          ${a.oldValue && a.newValue ? `<span class="text-muted">: ${escapeHtml(a.oldValue)} → ${escapeHtml(a.newValue)}</span>` : ''}
          ${!a.oldValue && a.newValue ? `<span class="text-muted">: ${escapeHtml(a.newValue)}</span>` : ''}
          <div class="text-muted" style="font-size: 0.75rem;">${formatDateTime(a.createdAt)}</div>
        </div>
      </div>
    `,
    )
    .join("");

  container.html(html);
}

/**
 * Utility: escape HTML to prevent XSS
 */
function escapeHtml(text) {
  if (!text) return '';
  const div = document.createElement('div');
  div.appendChild(document.createTextNode(text));
  return div.innerHTML;
}
