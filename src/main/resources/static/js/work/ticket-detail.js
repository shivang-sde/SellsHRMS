let ticketId = window.ticketId || document.getElementById("taskTicketId")?.value || null;

let ticketData = null;
let allEmployees = [];
let editingTaskId = null;
let existingAttachments = [];
let removedAttachmentIds = [];

$(document).ready(async function() {
  await loadTicketDetails();
  await loadEmployees();
});

async function loadTicketDetails() {
  try {
    const res = await ticketAPI.getById(ticketId, window.APP.ORG_ID, window.APP.EMPLOYEE_ID);
    ticketData = res;

    console.log(res)
    
    $('#ticketTitle').text(ticketData.title);
    $('#ticketDescription').text(ticketData.description || '-');
    $('#ticketProject').text(ticketData.projectName || '-');
    $('#ticketStartDate').text(ticketData.startDate || '-');
    $('#ticketEndDate').text(ticketData.endDate || '-');
    $('#ticketCreatedBy').text(ticketData.createdByName || '-');
    $('#ticketAssignees').text((ticketData.assigneeNames || []).join(', ') || '-');
    $('#ticketStatusBadge').html(getStatusBadge(ticketData.status));

    renderTasks(ticketData.tasks || []);
    await loadTicketAttachments(ticketId);
    await loadTicketActivity(ticketId);

  } catch (err) {
    console.error(err);
    showToast('Failed to load ticket details', 'error');
  }
}

// ============================================================
// TASKS
// ============================================================
async function loadEmployees() {
  try {
    allEmployees = await employeeAPI.getSubordinates(window.APP.EMPLOYEE_ID);
    const options = allEmployees.map(e => `<option value="${e.id}">${e.firstName} ${e.lastName}</option>`).join('');
    $('#taskForm [name="assigneeId"]').html(options);
  } catch (err) {
    console.error(err);
  }
}

function renderTasks(tasks) {
  const tbody = $('#tasksTable tbody');
  if (!tasks.length) {
    tbody.html('<tr><td colspan="6" class="text-center text-muted">No tasks yet</td></tr>');
    return;
  }

  const html = tasks.map(t => `
    <tr>
      <td>${t.title}</td>
      <td>${getTaskStatusBadge(t.status)}</td>
      <td>${t.priority}</td>
      <td>${t.assigneeName || '-'}</td>
      <td>${t.reminderEnabled ? formatDateTime(t.reminderAt) : '-'}</td>
      <td>
        <div class="btn-group btn-group-sm">
          <button class="btn btn-outline-primary" onclick="editTask(${t.id})"><i class="fas fa-edit"></i></button>
          <button class="btn btn-outline-danger" onclick="deleteTask(${t.id})"><i class="fas fa-trash"></i></button>
        </div>
      </td>
    </tr>
  `).join('');
  tbody.html(html);
}

function openTaskModal() {
  editingTaskId = null;
  $('#taskModalTitle').text('Add Task');
  $('#taskForm')[0].reset();
  $('#taskTicketId').val(ticketId);
  modalUtils.open('taskModal');
}

async function editTask(id) {
  try {
    const res = await taskAPI.getById(id);
    const t = res.data;
    editingTaskId = id;

    $('#taskModalTitle').text('Edit Task');
    Object.entries(t).forEach(([k, v]) => {
      const el = $(`#taskForm [name="${k}"]`);
      if (el.length) {
        if (el.attr('type') === 'datetime-local' && v) el.val(v.replace(' ', 'T'));
        else el.val(v);
      }
    });
    modalUtils.open('taskModal');
  } catch (err) {
    console.error(err);
    showToast('Failed to load task', 'error');
  }
}

async function saveTicketWorkUpdate() {
  const form = document.getElementById('taskForm');
  if (!form.checkValidity()) {
    form.reportValidity();
    return;
  }

  const formFields = Object.fromEntries(new FormData(form).entries());

  // 1️⃣ Build base task object
  const taskData = {
    ticketId: ticketId,
    title: formFields.title || '',
    description: formFields.description || '',
    status: 'DONE', // work update is inherently done
    isSelfTask: false,
    priority: 'MEDIUM',
    reporterId: ticketData.createdById, // ticket creator (manager/team lead)
    createdById: window.APP.EMPLOYEE_ID, // current logged-in employee
  };

  // 2️⃣ Prepare multipart form
  const formData = new FormData();
  formData.append('task', new Blob([JSON.stringify(taskData)], { type: 'application/json' }));

  // 3️⃣ Add attachments
  const attachmentRows = document.querySelectorAll('#attachmentRows .attachment-row');
  attachmentRows.forEach((row) => {
    const fileInput = row.querySelector('input[name="attachments"]');
    const descInput = row.querySelector('input[name="attachmentDescriptions"]');
    if (fileInput?.files.length) {
      formData.append('attachments', fileInput.files[0]);
      formData.append('descriptions', descInput?.value?.trim() || '');
    }
  });

  try {
    loadingUtils.show();

    const url = editingTaskId
      ? `${window.APP.CONTEXT_PATH}/api/tasks/${editingTaskId}?organisationId=${window.APP.ORG_ID}&employeeId=${window.APP.EMPLOYEE_ID}`
      : `${window.APP.CONTEXT_PATH}/api/tasks?organisationId=${window.APP.ORG_ID}&reporterId=${ticketData.createdById}`;

    const method = editingTaskId ? 'PUT' : 'POST';

    const response = await fetch(url, { method, body: formData });
    const result = await response.json();

    if (!response.ok) throw new Error(result?.message || 'Failed to save work update');

    showToast('Work update saved successfully', 'success');
    modalUtils.close('taskModal');

    // Refresh ticket detail to show latest updates
    await loadTicketDetails();

    // Reset
    editingTaskId = null;
    $('#attachmentRows').empty();
    addAttachmentRow();

  } catch (error) {
    console.error('Error saving work update:', error);
    showToast(error.message || 'Failed to save update', 'error');
  } finally {
    loadingUtils.hide();
  }
}



// ============================================================
// EDIT WORK UPDATE
// ============================================================
async function editTicketWorkUpdate(id) {
  try {
    loadingUtils.show();
    editingTaskId = id;

    const res = await taskAPI.getById(id);
    const t = res.data;

    // Fill modal fields
    $('#taskModalTitle').text('Edit Work Update');
    $('#taskForm [name="title"]').val(t.title || '');
    $('#taskForm [name="description"]').val(t.description || '');
    $('#taskTicketId').val(t.ticketId);

    // Load existing attachments
    await loadExistingAttachments(t.attachments || []);

    modalUtils.open('taskModal');

  } catch (err) {
    console.error('Error loading task for edit:', err);
    showToast('Failed to load work update', 'error');
  } finally {
    loadingUtils.hide();
  }
}

// ============================================================
// LOAD EXISTING ATTACHMENTS IN EDIT MODE
// ============================================================
async function loadExistingAttachments(attachments) {
  existingAttachments = attachments || [];
  removedAttachmentIds = [];
  const container = $('#attachmentRows');
  container.empty();

  if (!attachments.length) {
    addAttachmentRow();
    return;
  }

  attachments.forEach(att => {
    container.append(`
      <div class="attachment-row border p-2 rounded mb-2 d-flex justify-content-between align-items-center bg-light">
        <div>
          <i class="fas fa-paperclip text-primary me-2"></i>
          <a href="${att.fileUrl}" target="_blank">${att.fileName}</a>
          <small class="text-muted">(${att.fileSizeKB?.toFixed(1)} KB)</small>
          ${att.description ? `<div class="text-muted small">${att.description}</div>` : ''}
        </div>
        <button type="button" class="btn btn-sm btn-outline-danger" onclick="removeExistingAttachment(${att.id}, this)">
          <i class="fas fa-times"></i>
        </button>
      </div>
    `);
  });

  // Add new attachment option
  container.append(`
    <div class="text-end mt-2">
      <button type="button" class="btn btn-sm btn-outline-secondary" onclick="addAttachmentRow()">
        <i class="fas fa-plus me-1"></i> Add New Attachment
      </button>
    </div>
  `);
}

// ============================================================
// REMOVE EXISTING ATTACHMENT (MARK FOR DELETE)
// ============================================================
function removeExistingAttachment(id, btn) {
  removedAttachmentIds.push(id);
  $(btn).closest('.attachment-row').remove();
}

// ============================================================
// ADD NEW ATTACHMENT ROW (FOR UPLOAD)
// ============================================================
function addAttachmentRow() {
  $('#attachmentRows').append(`
    <div class="attachment-row d-flex align-items-center mb-2">
      <input type="file" class="form-control me-2" name="attachments">
      <input type="text" class="form-control" name="attachmentDescriptions" placeholder="Add note or description (optional)">
      <button type="button" class="btn btn-sm btn-outline-danger ms-2" onclick="$(this).parent().remove()">×</button>
    </div>
  `);
}



function renderTasks(tasks) {
  const tbody = $('#tasksTable tbody');
  if (!tasks.length) {
    tbody.html('<tr><td colspan="6" class="text-center text-muted py-3">No work updates yet</td></tr>');
    return;
  }

  const html = tasks.map(t => `
    <tr>
      <td>${t.title}</td>
      <td>${t.description || '-'}</td>
      <td>${t.createdByName || '-'}</td>
      <td>${formatDateTime(t.createdAt)}</td>
      <td>${t.attachments?.length ? `${t.attachments.length} file(s)` : '-'}</td>
      <td>
        <div class="btn-group btn-group-sm">
          <button class="btn btn-outline-warning" onclick="editTicketWorkUpdate(${t.id})">
            <i class="fas fa-edit"></i>
          </button>
          <button class="btn btn-outline-danger" onclick="deleteTask(${t.id})">
            <i class="fas fa-trash"></i>
          </button>
        </div>
      </td>
    </tr>
  `).join('');

  tbody.html(html);
}


async function deleteTask(id) {
  modalUtils.confirm('Delete Task', 'Are you sure?', async () => {
    try {
      await taskAPI.delete(id);
      showToast('Task deleted', 'success');
      await loadTicketDetails();
    } catch (err) {
      showToast('Failed to delete task', 'error');
    }
  });
}

// ============================================================
// ATTACHMENTS & ACTIVITY
// ============================================================


async function loadTicketAttachments(ticketId) {
  try {
    const attachments = await ticketAPI.getAttachments(ticketId);
    const list = $('#ticketAttachmentsList');

    if (!attachments.length) {
      list.html('<p class="text-muted">No attachments yet.</p>');
      return;
    }

    const html = attachments
      .map(a => `
        <div class="border rounded p-2 mb-2 d-flex justify-content-between align-items-center">
          <div>
            <i class="fas fa-paperclip me-2 text-primary"></i>
            <a href="${a.fileUrl}" target="_blank">${a.fileName}</a>
            <small class="text-muted">(${a.fileSizeKB?.toFixed(1)} KB)</small>
          </div>
          <span class="text-muted small">by ${a.uploadedByName || 'N/A'} on ${formatDateTime(a.uploadedAt)}</span>
        </div>
      `)
      .join('');
    list.html(html);
  } catch (err) {
    $('#ticketAttachmentsList').html('<p class="text-danger">Failed to load attachments</p>');
  }
}


async function loadTicketActivity(ticketId) {
  try {
    const activities = await ticketAPI.getActivities(ticketId);
    const list = $('#ticketActivityList');

    if (!activities.length) {
      list.html('<li class="list-group-item text-muted text-center">No activity yet</li>');
      return;
    }

    const html = activities
      .map(a => `
        <li class="list-group-item">
          <strong>${a.employeeName}</strong> 
          <span class="text-muted small">(${formatDateTime(a.createdAt)})</span><br>
          <span>${a.activityType}: ${a.description || ''}</span>
        </li>
      `)
      .join('');
    list.html(html);
  } catch (err) {
    $('#ticketActivityList').html('<li class="list-group-item text-danger text-center">Failed to load activities</li>');
  }
}

