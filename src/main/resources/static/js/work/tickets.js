let allTickets = [];
let allProjects = [];
let allSubordinates = [];
let editingTicketId = null;

$(document).ready(async function () {
  await Promise.all([loadProjects(), loadTickets(), loadSubordinates()]);

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
