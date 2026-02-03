// project-details.js
// Full, production-ready file for Project Details page (attachments, tickets, members, drag-drop, etc.)

let projectId = window.projectId;
let projectData = null;
let allSubordinates = [];
let editingTicketId = null;

// Global org/emp IDs (from apiClient.js globals)


$(document).ready(async function () {
  await Promise.all([loadProjectDetails(), loadSubordinates()]);
});

$(document).on('change', '#attachmentType', function () {
  const type = $(this).val();
  if (type === 'FILE') {
    $('#fileInputContainer').show();
    $('#linkInputContainer').hide();
  } else {
    $('#fileInputContainer').hide();
    $('#linkInputContainer').show();
  }
});

$('#attachmentModal').on('shown.bs.modal', function () {
  $('#attachmentType').trigger('change');
});


// ============================================================
// PROJECT DETAILS LOADING
// ============================================================
async function loadProjectDetails() {
  try {
    loadingUtils.show();
    const res = await projectAPI.getById(projectId);
    projectData = res;
    const tickets = await ticketAPI.getByProject(projectId);
    projectData.tickets = tickets || [];

    // Render Project Info
    $('#projectName').text(projectData.name || '-');
    $('#projectDescription').text(projectData.description || '-');
    $('#projectMethodology').text(projectData.methodology || '-');
    $('#projectType').text(projectData.projectType || '-');
    $('#projectPriority').text(projectData.priority || '-');
    $('#projectStartDate').text(formatDate(projectData.startDate));
    $('#projectEndDate').text(formatDate(projectData.endDate));
    $('#projectManager').text(projectData.projectManagerName || '-');
    $('#projectTeamLead').text(projectData.projectTeamLeadName || '-');
    $('#projectStatusBadge').html(getStatusBadge(projectData.status));

    renderMembers(projectData.members || []);
    renderTickets(projectData.tickets || []);
    await loadAttachments(window.projectId);

    const isManagerOrLead =
      String(employeeId) === String(projectData.createdById) ||
      String(employeeId) === String(projectData.projectManagerId) ||
      String(employeeId) === String(projectData.projectTeamLeadId);

    if (isManagerOrLead) {
      $('#createTicketBtn').show();
      $('#addMemberBtn').show();
    } else {
      $('#createTicketBtn').hide();
      $('#addMemberBtn').hide();
    }

    $('#createTicketBtn').off('click').on('click', openTicketModal);
  } catch (err) {
    console.error('Error loading project:', err);
    showToast('error', err.message || 'Failed to load project details');
  } finally {
    loadingUtils.hide();
  }
}

// ============================================================
// MEMBERS RENDERING & MANAGEMENT
// ============================================================
function renderMembers(members) {
  const tbody = $('#membersTable tbody');
  if (!members || !members.length) {
    tbody.html('<tr><td colspan="4" class="text-center text-muted py-3">No members added</td></tr>');
    return;
  }

  const isManagerOrLead =
    String(employeeId) === String(projectData.createdById) ||
    String(employeeId) === String(projectData.projectManagerId) ||
    String(employeeId) === String(projectData.projectTeamLeadId);

  const html = members
    .map(m => {
      const isProtected =
        m.employeeId === projectData.createdById ||
        m.employeeId === projectData.projectManagerId ||
        m.employeeId === projectData.projectTeamLeadId;

      return `
        <tr>
          <td>${escapeHtml(m.employeeName)}</td>
          <td>${escapeHtml(m.departmentName || '-')}</td>
          <td>${escapeHtml(m.email || '-')}</td>
          <td class="text-end">
            ${isManagerOrLead && !isProtected
              ? `<button class="btn btn-sm btn-outline-danger" onclick="removeMember(${m.employeeId})"><i class="fas fa-trash"></i></button>`
              : ''}
          </td>
        </tr>`;
    })
    .join('');

  tbody.html(html);
  $('#addMemberBtn').toggle(isManagerOrLead).off('click').on('click', openAddMemberModal);
}

async function openAddMemberModal() {
  try {
    loadingUtils.show();
    const res = await employeeAPI.getSubordinates(employeeId, organisationId);
    const employees = res || [];
    const existingIds = (projectData.members || []).map(m => m.employeeId);
    const available = employees.filter(e => !existingIds.includes(e.id));

    if (!available.length) {
      showToast('info', 'No new subordinates available to add.');
      return;
    }

    const options = available
      .map(e => `<option value="${e.id}">${escapeHtml(e.fullName)} ${escapeHtml(e.employeeCode)} (${escapeHtml(e.department || 'N/A')})</option>`)
      .join('');
    $('#addMemberSelect').html(options);
    modalUtils.open('addMemberModal');
  } catch (err) {
    console.error('Failed to load subordinates:', err);
    showToast('error', 'Failed to load employees list');
  } finally {
    loadingUtils.hide();
  }
}

async function saveMembers() {
  const selected = $('#addMemberSelect').val() || [];
  if (!selected.length) return showToast('warning', 'Please select at least one employee.');

  try {
    loadingUtils.show();
    await apiClient.post(
      `/projects/${projectId}/members?organisationId=${organisationId}&employeeId=${employeeId}`,
      { employeeIds: selected.map(Number) }
    );
    showToast('success', 'Members added successfully');
    modalUtils.close('addMemberModal');
    await loadProjectDetails();
  } catch (err) {
    console.error('Error adding members:', err);
    showToast('error', 'Failed to add members');
  } finally {
    loadingUtils.hide();
  }
}

async function removeMember(empId) {
  modalUtils.confirm('Remove Member', 'Are you sure you want to remove this member?', async () => {
    try {
      loadingUtils.show();
      await apiClient.delete(
        `/projects/${projectId}/members/${empId}?organisationId=${organisationId}&employeeId=${employeeId}`
      );
      showToast('success', 'Member removed successfully');
      await loadProjectDetails();
    } catch (err) {
      console.error('Error removing member:', err);
      showToast('error', 'Failed to remove member');
    } finally {
      loadingUtils.hide();
    }
  });
}

// ============================================================
// TICKETS
// ============================================================
function renderTickets(tickets) {
  const tbody = $('#ticketsTable tbody');
  if (!tickets || !tickets.length) {
    tbody.html('<tr><td colspan="6" class="text-center text-muted py-3">No tickets found</td></tr>');
    return;
  }

  const isManagerOrLead =
    String(employeeId) === String(projectData.createdById) ||
    String(employeeId) === String(projectData.projectManagerId) ||
    String(employeeId) === String(projectData.projectTeamLeadId);
  


  const html = tickets
    .map(t => {
      console.log("isManagerOrLead:", isManagerOrLead);
      console.log("t.assigneeIds:", t.assigneeIds);
      console.log("projectData.members:", projectData.members);
      console.log("employeeId:", employeeId);
      console.log("is employee a member?",  projectData.members.some(
  m => String(m.employeeId || m.id) === String(window.APP.EMPLOYEE_ID)
));
      const condition1 = !isManagerOrLead;
      const condition2 = !t.assigneeIds || t.assigneeIds.length === 0;
      const condition3 =  projectData.members.some(
                                                    m => String(m.employeeId || m.id) === String(window.APP.EMPLOYEE_ID));
      console.log({ condition1, condition2, condition3 }); const canPick = condition1 && condition2 && condition3; console.log("canPick:", canPick);
      return `
        <tr>
          <td><a href="${window.APP.CONTEXT_PATH}/work/tickets/${t.id}" class="fw-semibold">${escapeHtml(t.title)}</a></td>
          <td>${getStatusBadge(t.status)}</td>
          <td>${escapeHtml((t.assigneeNames || []).join(', ') || '-')}</td>
          <td>${formatDate(t.startDate)}</td>
          <td>${formatDate(t.endDate)}</td>
          <td>
            ${isManagerOrLead
              ? `
                <button class="btn btn-sm btn-outline-warning" onclick="editTicket(${t.id})"><i class="fas fa-edit"></i></button>
                <button class="btn btn-sm btn-outline-danger" onclick="deleteTicket(${t.id})"><i class="fas fa-trash"></i></button>`
              : canPick
              ? `<button class="btn btn-sm btn-outline-success" onclick="pickTicket(${t.id})">Pick</button>`
              : `<button class="btn btn-sm btn-outline-primary" onclick="viewTicket(${t.id})"><i class="fas fa-eye"></i></button>`}
          </td>
        </tr>`;
    })
    .join('');

  tbody.html(html);
}

function openTicketModal() {
  editingTicketId = null;
  $('#ticketModalTitle').text('Create Ticket');
  $('#ticketForm')[0].reset();
  $('#ticketProjectId').val(projectId);
  modalUtils.open('ticketModal');
}

async function saveTicket() {
  const form = document.getElementById('ticketForm');
  if (!form.checkValidity()) {
    form.reportValidity();
    return;
  }

  const formData = new FormData(form);
  // remove file inputs from plain JSON payload
  formData.delete('attachments');

  const data = Object.fromEntries(formData.entries());
  data.assigneeIds = $('#ticketAssigneesSelect').val()?.map(Number) || [];
  data.organisationId = organisationId;
  data.createdById = employeeId;
  data.projectId = projectId;

  try {
    loadingUtils.show();
    let created;
    if (editingTicketId) {
      await ticketAPI.update(editingTicketId, data);
      showToast('success', 'Ticket updated successfully');
    } else {
      created = await ticketAPI.create(data);
      showToast('success', 'Ticket created successfully');

      // Upload attachments if any
      const files = $('#ticketAttachments')[0].files;
      if (files && files.length > 0) await uploadTicketAttachments(created.id);
    }
    modalUtils.close('ticketModal');
    await loadProjectDetails();
  } catch (err) {
    console.error(err);
    showToast('error', err.message || 'Failed to save ticket');
  } finally {
    loadingUtils.hide();
  }
}

async function editTicket(id) {
  try {
    const t = await ticketAPI.getById(id);
    editingTicketId = id;

    $('#ticketModalTitle').text('Edit Ticket');
    $('#ticketForm [name="title"]').val(t.title);
    $('#ticketForm [name="description"]').val(t.description);
    $('#ticketForm [name="status"]').val(t.status);
    $('#ticketForm [name="startDate"]').val(t.startDate);
    $('#ticketForm [name="endDate"]').val(t.endDate);
    $('#ticketAssigneesSelect').val((t.assigneeIds || []).map(String));

    modalUtils.open('ticketModal');
  } catch (err) {
    console.error(err);
    showToast('error', 'Failed to load ticket');
  }
}

function viewTicket(id) {
  window.location.href = `${window.APP.CONTEXT_PATH}/work/tickets/${id}`;
}

async function deleteTicket(id) {
  modalUtils.confirm('Delete Ticket', 'Are you sure you want to delete this ticket?', async () => {
    try {
      loadingUtils.show();
      await ticketAPI.delete(id);
      showToast('success', 'Ticket deleted successfully');
      await loadProjectDetails();
    } catch (err) {
      console.error(err);
      showToast('error', 'Failed to delete ticket');
    } finally {
      loadingUtils.hide();
    }
  });
}

async function pickTicket(id) {
  try {
    const ticket = await ticketAPI.getById(id);
    const assigneeIds = ticket.assigneeIds || [];
    if (!assigneeIds.includes(employeeId)) assigneeIds.push(employeeId);

    await ticketAPI.update(id, { ...ticket, assigneeIds, organisationId, employeeId });
    showToast('success', 'You have picked this ticket');
    await loadProjectDetails();
  } catch (err) {
    console.error('Error picking ticket:', err);
    showToast('error', 'Failed to pick this ticket');
  }
}

// ============================================================
// TICKET ATTACHMENTS (reusable function)
// ============================================================
async function uploadTicketAttachments(ticketId) {
  const files = $('#ticketAttachments')[0].files;
  if (!files || files.length === 0) {
    showToast('warning', 'Please select at least one file.');
    return;
  }

  const formData = new FormData();
  for (let i = 0; i < files.length; i++) {
    formData.append('files', files[i]);
  }

  try {
    loadingUtils.show();
    await ticketAPI.addAttachments(ticketId, formData);
    showToast('success', 'Attachments uploaded successfully');
    // Optionally reload ticket attachments if you display them somewhere
  } catch (err) {
    console.error(err);
    showToast('error', 'Failed to upload ticket attachments');
  } finally {
    loadingUtils.hide();
  }
}

// ============================================================
// ATTACHMENTS (Files + Links)
// ============================================================
$('#attachmentType').on('change', function () {
  const type = $(this).val();
  if (type === 'FILE') {
    $('#fileInputContainer').show();
    $('#linkInputContainer').hide();
  } else {
    $('#fileInputContainer').hide();
    $('#linkInputContainer').show();
  }
});

async function saveAttachment() {
  const title = $('#attachmentTitle').val().trim();
  const description = $('#attachmentDescription').val().trim();
  const type = $('#attachmentType').val();
  const files = $('#attachmentFiles')[0].files;
  const link = $('#attachmentUrl').val().trim();

  if (!title) return showToast('warning', 'Please enter a title');

  const saveBtn = $('#attachmentModal button.btn-primary');
  saveBtn.prop('disabled', true).text('Saving...');

  try {
    if (type === 'FILE') {
      if (!files || files.length === 0) return showToast('warning', 'Please select at least one file');
      await uploadAndSaveFiles(files, title, description, type);
    } else {
      if (!link) return showToast('warning', 'Please provide a valid link');
      const dto = { title, description, type, url: link };
      await projectAPI.addAttachment(projectId, dto);
      showToast('success', 'Link added successfully');
    }

    $('#attachmentModal').modal('hide');
    await loadAttachments(projectId);
  } catch (err) {
    console.error(err);
    showToast('error', err.message || 'Failed to save attachment');
  } finally {
    saveBtn.prop('disabled', false).text('Save');
  }
}

async function uploadAndSaveFiles(files, title, description, type) {
  try {
    // 1️⃣ Upload files to the universal file API
    const uploadedFiles = await fileAPI.upload(files, 'projects', `project-${projectId}`);

    // 2️⃣ For each uploaded file, register it as a project attachment
    for (const file of uploadedFiles) {
      const dto = {
        title,
        description,
        type,
        url: file.fileUrl // ✅ match backend DTO property
      };
      await apiClient.post(`/projects/${projectId}/attachments?employeeId=${employeeId}`, dto);
    }

    // 3️⃣ Refresh UI
    showToast('success', 'File(s) uploaded successfully');
    $('#attachmentModal').modal('hide');
    await loadAttachments(projectId);
  } catch (err) {
    console.error('Upload failed', err);
    showToast('error', err.message || 'Upload failed');
  }
}



async function loadAttachments(projectId) {
  try {
    const res = await projectAPI.getAttachments(projectId);
    const list = $('#attachmentList');
    list.empty();

    if (!res || !res.length) {
      list.append('<div class="text-muted p-2">No attachments yet.</div>');
      return;
    }

    res.forEach(a => {
      const isFile = String(a.type).toUpperCase() === 'FILE';
      const icon = getFileIcon(a.fileType || a.type || '');
      list.append(`
        <div class="list-group-item d-flex justify-content-between align-items-center">
          <div class="d-flex align-items-center gap-2">
            <i class="fas ${icon} attachment-icon"></i>
            <div>
              <strong>${escapeHtml(a.title || a.fileName || '(Untitled)')}</strong><br>
              <a href="${a.url}" target="_blank" rel="noopener noreferrer">${a.url}</a>
              <p class="text-muted small mb-0">${escapeHtml(a.description || '')}</p>
            </div>
          </div>
          <div class="btn-group">
            ${isFile ? `<button class="btn btn-sm btn-outline-secondary" onclick="downloadAttachment('${encodeURI(a.url)}')">Download</button>` : ''}
            
          </div>
        </div>
      `);
    });
    //  <button class="btn btn-sm btn-danger" onclick="deleteAttachment(${a.id})">Delete</button>
  } catch (err) {
    console.error(err);
    showToast('error', err.message || 'Failed to load attachments');
  }
}

async function deleteAttachment(id) {
  try {
    await apiClient.delete(`/projects/${projectId}/attachments/${id}?employeeId=${employeeId}`);
    showToast('success', 'Attachment deleted');
    await loadAttachments(projectId);
  } catch (err) {
    console.error(err);
    showToast('error', 'Failed to delete attachment');
  }
}

function downloadAttachment(fileUrl) {
  // fileUrl may already be encoded; fileAPI.download will encode again safely
  fileAPI.download(fileUrl);
}

// ============================================================
// DRAG-AND-DROP UPLOAD (project-level)
// ============================================================
const uploadArea = document.getElementById('uploadArea');
if (uploadArea) {
  uploadArea.addEventListener('dragover', e => {
    e.preventDefault();
    uploadArea.classList.add('dragover');
  });
  uploadArea.addEventListener('dragleave', () => uploadArea.classList.remove('dragover'));
  uploadArea.addEventListener('drop', e => {
    e.preventDefault();
    uploadArea.classList.remove('dragover');
    const files = e.dataTransfer.files;
    uploadFiles(files);
  });
}
$('#attachmentFile').on('change', e => uploadFiles(e.target.files));

async function uploadFiles(files) {
  if (!files || !files.length) return;

  // if modal is open, don't double-upload (avoid race)
  if ($('#attachmentModal').hasClass('show')) return;

  const formData = new FormData();
  for (const f of files) formData.append('files', f);
  formData.append('module', 'projects');
  formData.append('entityFolder', `project-${projectId}`);

  $('#uploadProgressContainer').show();
  const progressBar = $('#uploadProgressBar');
  progressBar.css('width', '0%').text('0%');

  try {
    const xhr = new XMLHttpRequest();
    xhr.open('POST', `${apiClient.baseURL}/files/upload`);
    xhr.upload.onprogress = e => {
      if (e.lengthComputable) {
        const percent = Math.round((e.loaded / e.total) * 100);
        progressBar.css('width', percent + '%').text(percent + '%');
      }
    };

    xhr.onload = async () => {
      $('#uploadProgressContainer').hide();
      if (xhr.status === 200) {
        try {
          const data = JSON.parse(xhr.responseText);
          const uploaded = data.data || [];
          for (const file of uploaded) {
            const dto = { title: file.fileName, description: '', type: 'FILE', url: file.fileUrl };
            await apiClient.post(`/projects/${projectId}?employeeId=${employeeId}`, dto);
          }
          showToast('success', 'Files uploaded successfully');
          await loadAttachments(projectId);
        } catch (parseErr) {
          console.error('Upload parse error', parseErr);
          showToast('error', 'Upload succeeded but response parsing failed');
        }
      } else {
        showToast('error', 'Upload failed');
      }
    };

    xhr.onerror = () => {
      $('#uploadProgressContainer').hide();
      showToast('error', 'Upload error');
    };

    xhr.send(formData);
  } catch (err) {
    console.error(err);
    showToast('error', 'Upload error');
    $('#uploadProgressContainer').hide();
  }
}

// ============================================================
// SUBORDINATES (for assignee dropdown)
// ============================================================
async function loadSubordinates() {
  try {
    const res = await employeeAPI.getSubordinates(employeeId, organisationId);
    allSubordinates = res || [];
    const options = allSubordinates
      .map(e => `<option value="${e.id}">${escapeHtml(e.fullName)} ${escapeHtml(e.employeeCode)} (${escapeHtml(e.department || 'N/A')})</option>`)
      .join('');
    $('#ticketAssigneesSelect').html(options);
  } catch (err) {
    console.error(err);
    showToast('error', err.message || 'Failed to load subordinates');
  }
}

// ============================================================
// HELPERS
// ============================================================
function getFileIcon(fileType = '') {
  const ft = String(fileType).toLowerCase();
  if (ft.includes('image')) return 'fa-file-image text-primary';
  if (ft.includes('pdf')) return 'fa-file-pdf text-danger';
  if (ft.includes('video')) return 'fa-file-video text-warning';
  if (ft.includes('word') || ft.includes('doc')) return 'fa-file-word text-info';
  if (ft.includes('excel') || ft.includes('sheet')) return 'fa-file-excel text-success';
  return 'fa-file text-secondary';
}

function escapeHtml(unsafe) {
  if (unsafe === null || unsafe === undefined) return '';
  return String(unsafe)
    .replace(/&/g, '&amp;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;');
}

function formatDate(dateStr) {
  if (!dateStr) return '-';
  try {
    const d = new Date(dateStr);
    return d.toLocaleDateString();
  } catch {
    return dateStr;
  }
}

// status badge helper (simple)
function getStatusBadge(status) {
  const s = String(status || '').toUpperCase();
  switch (s) {
    case 'IN_PROGRESS':
      return '<span class="badge bg-primary">In Progress</span>';
    case 'COMPLETED':
      return '<span class="badge bg-success">Completed</span>';
    case 'ON_HOLD':
      return '<span class="badge bg-warning text-dark">On Hold</span>';
    case 'CANCELLED':
      return '<span class="badge bg-danger">Cancelled</span>';
    default:
      return '<span class="badge bg-secondary">Planning</span>';
  }
}
