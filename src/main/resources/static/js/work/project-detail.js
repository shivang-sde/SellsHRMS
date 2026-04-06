// project-details.js
// Full, production-ready file for Project Details page (attachments, tickets, members, drag-drop, etc.)

let projectId = window.projectId;
let projectData = null;
let allSubordinates = [];
let editingTicketId = null;

// Global org/emp IDs (from apiClient.js globals)


$(document).ready(async function () {
  await loadProjectDetails(); // Wait for project data first
  await loadSubordinates();   // Then load subordinates based on that data
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

  // 1. Identify the role of the LOGGED-IN user
  const isCreator = String(employeeId) === String(projectData.createdById);
  const isPM = String(employeeId) === String(projectData.projectManagerId);
  const isTL = String(employeeId) === String(projectData.projectTeamLeadId);

  const html = members
    .map(m => {

      // 2. Identify the role of the MEMBER in this row
      const targetId = String(m.employeeId);
      const isTargetCreator = targetId === String(projectData.createdById);
      const isTargetPM = targetId === String(projectData.projectManagerId);
      const isTargetTL = targetId === String(projectData.projectTeamLeadId);

      // 3. Permission Logic:
      let canDelete = false;

      if (isCreator) {
        // Creator can remove anyone except themselves
        canDelete = !isTargetCreator;
      } else if (isPM) {
        // PM can remove TL and regular members, but NOT Creator or themselves
        canDelete = !isTargetCreator && !isTargetPM;
      } else if (isTL) {
        // TL can only remove regular members (not Creator, PM, or themselves)
        canDelete = !isTargetCreator && !isTargetPM && !isTargetTL;
      }

      return `
        <tr>
          <td class="px-3"><div class="fw-semibold text-wrap" style="min-width: 150px;">${escapeHtml(m.employeeName)}</div></td>
          <td><div class="text-wrap" style="min-width: 120px;">${escapeHtml(m.departmentName || '-')}</div></td>
         <td class="text-end px-3">
            ${canDelete
          ? `<button class="btn btn-sm btn-outline-danger" onclick="removeMember(${m.employeeId})"><i class="fas fa-trash"></i></button>`
          : ''}
          </td>
        </tr>`;
    })
    .join('');

  tbody.html(html);
  const isAnyLead = isCreator || isPM || isTL;
  $('#addMemberBtn').toggle(isAnyLead).off('click').on('click', openAddMemberModal);
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


  await modalUtils.confirm('Remove Member', 'Are you sure you want to remove this member?', async () => {
    try {
      loadingUtils.show();

      // Ensure global variables exist
      if (!projectId || !organisationId || !employeeId) {
        throw new Error(`Missing IDs: Project:${projectId}, Org:${organisationId}, Emp:${employeeId}`);
      }

      const url = `/projects/${projectId}/members/${empId}?organisationId=${organisationId}&employeeId=${employeeId}`;

      const response = await apiClient.delete(url);


      showToast('success', 'Member removed successfully');
      await loadProjectDetails();
    } catch (err) {
      console.error('Error removing member:', err);
      showToast(err.message || 'Failed to remove member', 'error');
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

  if (!projectData || !projectData.members) {
    console.warn("Attempted to render tickets before projectData was ready.");
    return;
  }

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
      // 1. Check if user is a member of this project
      const isUserMember = projectData.members.some(
        m => String(m.employeeId || m.id) === String(employeeId)
      );

      // 2. Check if ticket is unassigned
      const isUnassigned = !t.assigneeIds || t.assigneeIds.length === 0;

      // 3. Define canPick logic
      // User can pick if: They aren't already a Lead/Manager AND ticket is unassigned AND they are in the project
      const canPick = !isManagerOrLead && isUnassigned && isUserMember;
      return `
        <tr>
          <td class="px-3"><a href="${window.APP.CONTEXT_PATH}/work/tickets/${t.id}" class="fw-semibold text-wrap" style="min-width: 150px; display: inline-block;">${escapeHtml(t.title)}</a></td>
          <td>${getStatusBadge(t.status)}</td>
          <td><div class="text-wrap" style="min-width: 150px; max-width: 250px;">${escapeHtml((t.assigneeNames || []).join(', ') || '-')}</div></td>
          <td>${formatDate(t.startDate)}</td>
          <td>${formatDate(t.endDate)}</td>
          <td class="px-3">
            <div class="d-flex flex-nowrap gap-1">
            <button class="btn btn-sm btn-outline-secondary" onclick="ticketStatusModal(${t.id})"><i class="fas fa-check"></i></button>
            ${isManagerOrLead
          ? `
                <button class="btn btn-sm btn-outline-warning" onclick="editTicket(${t.id})"><i class="fas fa-edit"></i></button>
                <button class="btn btn-sm btn-outline-danger" onclick="deleteTicket(${t.id})"><i class="fas fa-trash"></i></button>`
          : canPick
            ? `<button class="btn btn-sm btn-outline-success" onclick="pickTicket(${t.id})">Pick</button>`
            : `<button class="btn btn-sm btn-outline-primary" onclick="viewTicket(${t.id})"><i class="fas fa-eye"></i></button>`}
            </div>
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

function ticketStatusModal(ticketId) {
  editingTicketId = ticketId;
  $('#ticketStatusModalTitle').text('Update Ticket Status');
  $('#ticketStatusForm')[0].reset();
  modalUtils.open('ticketStatusModal');
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
      const filesInput = document.getElementById('ticketAttachments');
      const files = filesInput ? filesInput.files : null;
      if (files && files.length > 0) {
        await uploadTicketAttachments(created.id);
      }
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

async function saveTicketStatus() {
  const status = $('#ticketStatusSelect').val();
  try {
    loadingUtils.show();
    await ticketAPI.updateStatus(editingTicketId, status);
    showToast('success', 'Ticket status updated successfully');
    modalUtils.close('ticketStatusModal');
    await loadProjectDetails();
  } catch (err) {
    console.error(err);
    showToast('error', err.message || 'Failed to update ticket status');
  } finally {
    loadingUtils.hide();
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

    modalUtils.close('attachmentModal');
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
    modalUtils.close('attachmentModal');
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

    // Filter only those who are in projectData.members
    // Use employeeId from projectData.members
    const memberIds = new Set((projectData.members || []).map(m => String(m.employeeId)));

    // Compare against subordinate.id
    const filteredSubordinates = allSubordinates.filter(e => memberIds.has(String(e.id)));

    const options = filteredSubordinates
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

