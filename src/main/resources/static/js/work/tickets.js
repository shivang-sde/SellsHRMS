let allTickets = [];
let allProjects = [];
let allSubordinates = [];
let editingTicketId = null;

// Subordinate tickets state
let subordinateTickets = [];
let filteredSubordinateTickets = [];

$(document).ready(async function () {
  await Promise.all([loadProjects(), loadTickets(), loadSubordinates()]);
  loadSubordinateTickets();

  $("#statusFilter, #projectFilter").on("change", filterTickets);
  $("#searchInput").on("input", debounce(handleSearch, 400));
});

// ============================================================
// LOAD PROJECTS ACCESSIBLE TO CURRENT USER (MANAGER / CREATOR)
// ============================================================
async function loadProjects() {
  try {
    allProjects = await projectAPI.getByEmployee(window.APP.EMPLOYEE_ID);
    const options = allProjects
      .map((p) => `<option value="${p.id}">${p.name}</option>`)
      .join("");
    $('[name="projectId"]').append(options);
  } catch (err) {
    console.error("Failed to load projects", err);
    showToast("warning", "Could not load projects");
  }
}

// ============================================================
// LOAD SUBORDINATES (FOR ASSIGNEES)
// ============================================================
async function loadSubordinates() {
  try {
    const subs = await employeeAPI.getSubordinates(window.APP.EMPLOYEE_ID);
    allSubordinates = subs || [];
    const options = allSubordinates
      .map(
        (e) =>
          `<option value="${e.id}">${e.fullName} ${e.employeeCode} (${e.department || "N/A"} - ${e.designation})</option>`,
      )
      .join("");
    $("#ticketAssigneesSelect").html(options);
  } catch (err) {
    console.error("Failed to load subordinates", err);
    showToast("warning", "Could not load subordinates");
  }
}

// ============================================================
// LOAD & RENDER TICKETS
// ============================================================
async function loadTickets() {
  try {
    loadingUtils.show("#ticketsTable tbody");
    allTickets = await ticketAPI.getIndependent(); // default view
    console.log(" tickets, ", allTickets);
    renderTickets(allTickets);
  } catch (error) {
    console.error("Failed to load tickets:", error);
    showToast("error", "Failed to load tickets");
  } finally {
    loadingUtils.hide();
  }
}

function renderTickets(tickets) {
  const tbody = $("#ticketsTable tbody");

  if (!tickets || tickets.length === 0) {
    tbody.html(
      `<tr><td colspan="8" class="text-center py-5 text-muted">No tickets found</td></tr>`,
    );
    return;
  }

  const html = tickets
    .map(
      (t) => `
    <tr class="ticket-row hover-shadow">
      <td>
        <a href="${window.APP.CONTEXT_PATH}/work/tickets/${t.id}" class="fw-semibold text-decoration-none">
          ${t.title}
        </a>
        <div class="small text-muted mt-1">${t.description?.substring(0, 60) || ""}${t.description?.length > 60 ? "..." : ""}</div>
      </td>

      <td>${getStatusBadge(t.status)}</td>
      <td>${t.createdByName || "-"}</td>
      <td>${formatDate(t.startDate)}</td>
       <td>${formatDate(t.endDate)}</td>
      <td>
        <div class="btn-group btn-group-sm">
          ${t.status === "OPEN" ? `<button class="btn btn-outline-success" onclick="startTicket(${t.id})">Start</button>` : ""}
          ${t.status === "IN_PROGRESS" ? `<button class="btn btn-outline-primary" onclick="completeTicket(${t.id})">Complete</button>` : ""}
          <button class="btn btn-outline-primary" onclick="viewTicket(${t.id})" title="View Details"><i class="fas fa-eye"></i></button>
          <button class="btn btn-outline-warning" onclick="editTicket(${t.id})" title="Edit"><i class="fas fa-edit"></i></button>
          <button class="btn btn-outline-danger" onclick="deleteTicket(${t.id})" title="Delete"><i class="fas fa-trash"></i></button>
        </div>
      </td>
    </tr>
  `,
    )
    .join("");

  tbody.html(html);
}

// ============================================================
// OPEN MODAL FOR CREATE
// ============================================================
function openTicketModal() {
  editingTicketId = null;
  $("#ticketModalTitle").text("Create New Ticket");
  modalUtils.resetForm("ticketModal");
  $('[name="status"]').val("OPEN");
  $("#ticketAttachmentsList").empty();
  modalUtils.open("ticketModal");
}

// ============================================================
// EDIT TICKET
// ============================================================
async function editTicket(id) {
  try {
    loadingUtils.show();
    const ticket = await ticketAPI.getById(id);
    editingTicketId = id;
    $("#ticketModalTitle").text("Edit Ticket");

    await loadTicketAttachments(id);
    await loadTicketActivity(id);

    // populate form
    Object.entries(ticket).forEach(([key, value]) => {
      const el = $(`[name="${key}"]`);
      if (el.length) {
        if (el.attr("type") === "date" && value) el.val(value.split("T")[0]);
        else el.val(value);
      }
    });

    // Populate project
    $('[name="projectId"]').val(ticket.projectId || "");

    // Populate assignees (multi-select)
    await loadSubordinates();
    if (ticket.assigneeIds?.length) {
      $("#ticketAssigneesSelect").val(ticket.assigneeIds.map(String));
    }

    modalUtils.open("ticketModal");
  } catch (err) {
    console.error(err);
    showToast("error", "Failed to load ticket details");
  } finally {
    loadingUtils.hide();
  }
}

// ============================================================
// SAVE (CREATE / UPDATE)
// ============================================================
async function saveTicket() {
  const form = document.getElementById("ticketForm");
  if (!form.checkValidity()) {
    form.reportValidity();
    return;
  }

  const formData = new FormData(form);
  const data = {};
  formData.forEach((v, k) => (data[k] = v || null));

  // collect assignees (multi)
  const assigneeIds = $("#ticketAssigneesSelect").val() || [];
  data.assigneeIds = assigneeIds.map((id) => parseInt(id));

  // add org and creator
  data.organisationId = window.APP.ORG_ID;
  data.createdById = window.APP.EMPLOYEE_ID;

  try {
    loadingUtils.show();

    if (editingTicketId) {
      await ticketAPI.update(editingTicketId, data);
      showToast("success", "Ticket updated successfully");
    } else {
      const created = await ticketAPI.create(data);
      showToast("success", "Ticket created successfully");

      // Handle attachments if any
      await uploadTicketAttachments(created.id);
    }

    modalUtils.close("ticketModal");
    await loadTickets();
  } catch (error) {
    console.error(error);
    showToast("error", error.message || "Failed to save ticket");
  } finally {
    loadingUtils.hide();
  }
}

function addAttachmentRow() {
  $("#attachmentRows").append(`
    <div class="attachment-row d-flex align-items-center mb-2">
      <input type="file" class="form-control me-2" name="attachments">
      <input type="text" class="form-control" name="attachmentDescriptions" placeholder="Add note or description (optional)">
      <button type="button" class="btn btn-sm btn-outline-danger ms-2" onclick="$(this).parent().remove()">×</button>
    </div>
  `);
}

async function loadTicketAttachments(ticketId) {
  try {
    const attachments = await ticketAPI.getAttachments(ticketId);
    const list = $("#ticketAttachmentsList");

    if (!attachments.length) {
      list.html('<p class="text-muted">No attachments yet.</p>');
      return;
    }

    const html = attachments
      .map(
        (a) => `
        <div class="border rounded p-2 mb-2 d-flex justify-content-between align-items-center">
          <div>
            <i class="fas fa-paperclip me-2 text-primary"></i>
            <a href="${a.fileUrl}" target="_blank">${a.fileName}</a>
            <small class="text-muted">(${a.fileSizeKB?.toFixed(1)} KB)</small>
          </div>
          <span class="text-muted small">by ${a.uploadedByName || "N/A"} on ${formatDateTime(a.uploadedAt)}</span>
        </div>
      `,
      )
      .join("");
    list.html(html);
  } catch (err) {
    $("#ticketAttachmentsList").html(
      '<p class="text-danger">Failed to load attachments</p>',
    );
  }
}

async function startTicket(ticketId) {
  try {
    await ticketAPI.updateStatus(ticketId, "IN_PROGRESS");
    showToast("success", "Ticket started");
    await loadTickets();
  } catch (err) {
    showToast("error", err.message || "Failed to start ticket");
  }
}

async function completeTicket(ticketId) {
  try {
    await ticketAPI.updateStatus(ticketId, "COMPLETED");
    showToast("success", "Ticket completed");
    await loadTickets();
  } catch (err) {
    showToast("error", err.message || "Failed to complete ticket");
  }
}

async function loadTicketActivity(ticketId) {
  try {
    const activities = await ticketAPI.getActivities(ticketId);
    const list = $("#ticketActivityList");

    if (!activities.length) {
      list.html(
        '<li class="list-group-item text-muted text-center">No activity yet</li>',
      );
      return;
    }

    const html = activities
      .map(
        (a) => `
        <li class="list-group-item">
          <strong>${a.employeeName}</strong> 
          <span class="text-muted small">(${formatDateTime(a.createdAt)})</span><br>
          <span class="badge bg-info text-dark">${a.activityType}: ${a.description || ""}</span>
        </li>
      `,
      )
      .join("");
    list.html(html);
  } catch (err) {
    $("#ticketActivityList").html(
      '<li class="list-group-item text-danger text-center">Failed to load activities</li>',
    );
  }
}

// ============================================================
// FILE ATTACHMENT HANDLER
// ============================================================
async function uploadTicketAttachments(ticketId) {

  if (!ticketId) {
    showToast("warning", "Please save the ticket first before uploading attachments.");
    return;
  }

  const employeeId = window.APP.EMPLOYEE_ID;
  const rows = document.querySelectorAll(".attachment-row");

  if (!rows.length) {
    showToast("warning", "No attachments selected.");
    return;
  }

  const formData = new FormData();

  rows.forEach((row) => {
    const fileInput = row.querySelector('[name="attachments"]');
    const descInput = row.querySelector('[name="attachmentDescriptions"]');
    const file = fileInput?.files?.[0];

    if (file) {
      formData.append("files", file);
      formData.append("descriptions", descInput?.value || "");
    }
  });

  formData.append("employeeId", employeeId);

  try {
    loadingUtils.show();

    const response = await fetch(`/api/tickets/${ticketId}/attachments`, {
      method: "POST",
      body: formData,
    });

    if (!response.ok) throw new Error("Failed to upload attachments");
    const result = await response.json();

    showToast("success", result.message || "Attachments uploaded successfully");

    // Refresh the list to show new uploads
    await loadTicketAttachments(ticketId);
  } catch (err) {
    console.error(err);
    showToast("error", "Error uploading attachments");
  } finally {
    loadingUtils.hide();
  }
}


// ============================================================
// DELETE
// ============================================================
function deleteTicket(id) {
  modalUtils.confirm(
    "Delete Ticket",
    "Are you sure you want to delete this ticket?",
    async () => {
      try {
        loadingUtils.show();
        await ticketAPI.delete(id);
        showToast("success", "Ticket deleted successfully");
        await loadTickets();
      } catch (err) {
        showToast("error", "Failed to delete ticket");
      } finally {
        loadingUtils.hide();
      }
    },
  );
}

function getStatusBadge(status) {
  const colors = {
    OPEN: "bg-info",
    IN_PROGRESS: "bg-primary",
    ON_HOLD: "bg-warning",
    COMPLETED: "bg-success",
    CANCELLED: "bg-danger",
    RESOLVED: "bg-success",
    CLOSED: "bg-secondary",
  };
  return `<span class="badge ${colors[status] || "bg-light"} text-white">${status.replace("_", " ")}</span>`;
}

// ============================================================
// SEARCH & FILTER
// ============================================================
async function handleSearch() {
  const keyword = $("#searchInput").val().trim();
  if (keyword.length < 2) return renderTickets(allTickets);
  try {
    const results = await ticketAPI.search(keyword);
    renderTickets(results);
  } catch (err) {
    console.error("Search failed:", err);
  }
}

function filterTickets() {
  const status = $("#statusFilter").val();
  const projectId = $("#projectFilter").val();

  let filtered = [...allTickets];
  if (status) filtered = filtered.filter((t) => t.status === status);
  if (projectId) filtered = filtered.filter((t) => t.projectId == projectId);

  renderTickets(filtered);
}

// ============================================================
// UTILS
// ============================================================
function debounce(func, wait) {
  let timeout;
  return function (...args) {
    clearTimeout(timeout);
    timeout = setTimeout(() => func.apply(this, args), wait);
  };
}

function viewTicket(id) {
  window.location.href = `${window.APP.CONTEXT_PATH}/work/tickets/${id}`;
}

// ============================================================
// SUBORDINATE TICKETS SECTION
// ============================================================

/**
 * Load independent tickets from all subordinates of the current employee.
 * Only tickets NOT under any project are shown.
 */
async function loadSubordinateTickets() {
  try {
    if (!$("#subTicketDateFilter").val()) {
      $("#subTicketDateFilter").val(new Date().toISOString().split('T')[0]);
    }
    if (!$("#subTicketDateFilterEnd").val()) {
      $("#subTicketDateFilterEnd").val(new Date().toISOString().split('T')[0]);
    }
    const startDate = $("#subTicketDateFilter").val();
    const endDate = $("#subTicketDateFilterEnd").val();

    const res = await ticketAPI.getSubordinateTickets(window.APP.EMPLOYEE_ID, startDate, endDate);
    const data = res?.data || res;
    subordinateTickets = Array.isArray(data) ? data : [];

    if (subordinateTickets.length > 0) {
      $("#subordinateTicketsSection").show();
      await populateSubTicketEmployeeFilter();
      filteredSubordinateTickets = [...subordinateTickets];
      renderSubordinateTickets(filteredSubordinateTickets);
    } else {
      // Check if there are subordinates but no tickets
      try {
        const subCheck = await employeeAPI.getSubordinates(window.APP.EMPLOYEE_ID);
        const subs = subCheck?.data || subCheck;
        if (Array.isArray(subs) && subs.length > 0) {
          $("#subordinateTicketsSection").show();
          renderSubordinateTickets([]);
        }
      } catch {
        // No subordinates — keep section hidden
      }
    }
  } catch (error) {
    console.error("Failed to load subordinate tickets", error);
  }
}

/**
 * Populate the employee dropdown filter with unique employee names from the subordinate ticket list.
 */
async function populateSubTicketEmployeeFilter() {
  const select = $("#subTicketEmployeeFilter");
  select.find("option:not(:first)").remove();

  try {
    const res = await employeeAPI.getSubordinates(window.APP.EMPLOYEE_ID);
    const subordinates = res?.data || res || [];

    subordinates.forEach((emp) => {
      select.append(`<option value="${emp.id}">${emp.fullName}</option>`);
    });
  } catch (err) {
    console.error("Failed to populate employee filter", err);
  }
}

/**
 * Filter subordinate tickets based on search text, status, and employee dropdowns.
 */
function filterSubordinateTickets() {
  const searchTerm = ($("#subTicketSearch").val() || "").toLowerCase().trim();
  const statusFilter = $("#subTicketStatusFilter").val();
  const employeeFilter = $("#subTicketEmployeeFilter").val();

  filteredSubordinateTickets = subordinateTickets.filter((ticket) => {
    // Search filter: matches title, description, or employee name
    if (searchTerm) {
      const matchesSearch =
        (ticket.title || "").toLowerCase().includes(searchTerm) ||
        (ticket.description || "").toLowerCase().includes(searchTerm) ||
        (ticket.createdByName || "").toLowerCase().includes(searchTerm) ||
        (ticket.assigneeNames || []).some(n => (n || "").toLowerCase().includes(searchTerm));
      if (!matchesSearch) return false;
    }

    // Status filter
    if (statusFilter && ticket.status !== statusFilter) return false;

    // Employee filter: match against createdBy or any assignee
    if (employeeFilter) {
      const empId = parseInt(employeeFilter);
      const isCreator = ticket.createdById === empId;
      const isAssignee = (ticket.assigneeIds || []).includes(empId);
      if (!isCreator && !isAssignee) return false;
    }

    return true;
  });

  renderSubordinateTickets(filteredSubordinateTickets);
}

/**
 * Render the subordinate tickets table.
 */
function renderSubordinateTickets(tickets) {
  const tbody = $("#subordinateTicketsTable tbody");

  if (!tickets || !tickets.length) {
    tbody.html(`
      <tr>
        <td colspan="7" class="text-center text-muted py-5">
          <i class="fas fa-ticket-alt fa-3x mb-3 opacity-50"></i>
          <p class="mb-0">No subordinate tickets found</p>
        </td>
      </tr>
    `);
    return;
  }

  const html = tickets
    .map((t) => {
      // Employee who created the ticket
      const employeeName = t.createdByName || "Unknown";

      // Assignees list
      const assigneesHtml = (t.assigneeNames && t.assigneeNames.length)
        ? t.assigneeNames.map(n => `<span class="badge bg-light text-dark me-1">${escapeHtml(n)}</span>`).join("")
        : '<span class="text-muted">Unassigned</span>';

      return `
        <tr>
          <td>
            <div class="fw-semibold">${escapeHtml(t.title)}</div>
            ${t.description ? `<small class="text-muted text-truncate d-block" style="max-width:300px;">${escapeHtml(t.description.substring(0, 80))}${t.description.length > 80 ? '...' : ''}</small>` : ''}
          </td>
          <td>
            <div class="d-flex align-items-center">
              <i class="fas fa-user-circle text-muted me-2"></i>
              <span>${escapeHtml(employeeName)}</span>
            </div>
          </td>
          <td>${getStatusBadge(t.status)}</td>
          <td>${assigneesHtml}</td>
          <td>${formatDate(t.startDate)}</td>
          <td>${formatDate(t.endDate)}</td>
          <td class="text-center">
            <button class="btn btn-sm btn-outline-info" onclick="viewSubordinateTicketDetail(${t.id})"
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
 * View a subordinate ticket's full details in a modal.
 */
async function viewSubordinateTicketDetail(ticketId) {
  const modal = new bootstrap.Modal(document.getElementById("subTicketDetailModal"));
  const body = $("#subTicketDetailBody");

  // Show spinner
  body.html(`
    <div class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>
  `);
  modal.show();

  try {
    const res = await ticketAPI.getById(ticketId);
    const ticket = res?.data?.data || res?.data || res;

    // Set modal title & link
    $("#subTicketDetailTitle").text(ticket.title || "Ticket Details");
    $("#subTicketDetailLink").attr("href", `${window.APP.CONTEXT_PATH}/work/tickets/${ticketId}`);

    // Build detail HTML
    const detailHtml = buildSubTicketDetailHtml(ticket);
    body.html(detailHtml);

    // Load attachments into the detail modal
    try {
      const attachRes = await ticketAPI.getAttachments(ticketId);
      const attachments = attachRes?.data || attachRes || [];
      renderSubTicketAttachments(attachments);
    } catch {
      $("#subTicketAttachmentsList").html('<p class="text-muted small">Could not load attachments.</p>');
    }

    // Load activity log
    try {
      const actRes = await ticketAPI.getActivities(ticketId);
      const activities = actRes?.data || actRes || [];
      renderSubTicketActivities(activities);
    } catch {
      $("#subTicketActivityLog").html('<p class="text-muted small">Could not load activity log.</p>');
    }

  } catch (error) {
    console.error("Failed to load ticket details:", error);
    body.html(`
      <div class="text-center text-danger py-5">
        <i class="fas fa-exclamation-triangle fa-3x mb-3"></i>
        <p>Failed to load ticket details</p>
      </div>
    `);
  }
}

/**
 * Build the HTML for a subordinate ticket detail view inside the modal.
 */
function buildSubTicketDetailHtml(ticket) {
  const assigneesHtml = (ticket.assigneeNames && ticket.assigneeNames.length)
    ? ticket.assigneeNames.map(n => `<span class="badge bg-primary bg-opacity-25 text-primary me-1">${escapeHtml(n)}</span>`).join("")
    : '<em class="text-muted">Unassigned</em>';

  return `
    <div class="row g-4">
      <!-- Info Cards Row -->
      <div class="col-md-6">
        <div class="border rounded p-3 h-100">
          <h6 class="text-muted mb-2"><i class="fas fa-info-circle me-1"></i> Status</h6>
          <div class="mb-2">${getStatusBadge(ticket.status)}</div>
          <div class="small text-muted">
            <span class="badge bg-secondary bg-opacity-25 text-secondary">Independent Ticket</span>
          </div>
        </div>
      </div>

      <div class="col-md-6">
        <div class="border rounded p-3 h-100">
          <h6 class="text-muted mb-2"><i class="fas fa-users me-1"></i> People</h6>
          <div class="d-flex flex-column gap-1 small">
            <div><strong>Created By:</strong> ${escapeHtml(ticket.createdByName || 'N/A')}</div>
            <div><strong>Assignees:</strong> ${assigneesHtml}</div>
          </div>
        </div>
      </div>

      <!-- Description -->
      <div class="col-12">
        <h6 class="text-muted mb-2"><i class="fas fa-align-left me-1"></i> Description</h6>
        <div class="border rounded p-3 bg-light" style="min-height: 60px;">
          ${ticket.description ? escapeHtml(ticket.description) : '<em class="text-muted">No description provided</em>'}
        </div>
      </div>

      <!-- Dates -->
      <div class="col-md-4">
        <div class="small">
          <strong><i class="fas fa-calendar-plus me-1"></i> Start Date:</strong><br>
          ${ticket.startDate ? formatDate(ticket.startDate) : '<span class="text-muted">—</span>'}
        </div>
      </div>
      <div class="col-md-4">
        <div class="small">
          <strong><i class="fas fa-calendar-check me-1"></i> End Date:</strong><br>
          ${ticket.endDate ? formatDate(ticket.endDate) : '<span class="text-muted">—</span>'}
        </div>
      </div>
      ${ticket.actualCompletionDate ? `
        <div class="col-md-4">
          <div class="small">
            <strong><i class="fas fa-flag-checkered me-1"></i> Completed:</strong><br>
            ${formatDate(ticket.actualCompletionDate)}
          </div>
        </div>
      ` : ''}

      <!-- Attachments section -->
      <div class="col-12">
        <h6 class="text-muted mb-2"><i class="fas fa-paperclip me-1"></i> Attachments</h6>
        <div id="subTicketAttachmentsList">
          <div class="text-center py-2">
            <div class="spinner-border spinner-border-sm text-primary"></div>
          </div>
        </div>
      </div>

      <!-- Activity log -->
      <div class="col-12">
        <h6 class="text-muted mb-2"><i class="fas fa-history me-1"></i> Activity Log</h6>
        <div id="subTicketActivityLog" style="max-height: 200px; overflow-y: auto;">
          <div class="text-center py-2">
            <div class="spinner-border spinner-border-sm text-primary"></div>
          </div>
        </div>
      </div>
    </div>
  `;
}

/**
 * Render attachments inside the subordinate ticket detail modal.
 */
function renderSubTicketAttachments(attachments) {
  const container = $("#subTicketAttachmentsList");

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
 * Render activity log inside the subordinate ticket detail modal.
 */
function renderSubTicketActivities(activities) {
  const container = $("#subTicketActivityLog");

  if (!activities || !activities.length) {
    container.html('<p class="text-muted small mb-0">No activity yet.</p>');
    return;
  }

  const icons = {
    TICKET_CREATED: "plus-circle",
    TICKET_UPDATED: "edit",
    STATUS_CHANGED: "exchange-alt",
    ASSIGNEES_UPDATED: "user-plus",
    ATTACHMENT_UPLOADED: "paperclip",
    ATTACHMENT_REMOVED: "times-circle",
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
          ${a.description ? `<div class="text-muted">${escapeHtml(a.description)}</div>` : ''}
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
