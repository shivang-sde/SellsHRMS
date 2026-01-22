let projectId = window.projectId;
let projectData = null;
let allSubordinates = [];
let editingTicketId = null;

$(document).ready(async function () {
  await Promise.all([loadProjectDetails(), loadSubordinates()]);
});

async function loadProjectDetails() {
  try {
    loadingUtils.show();
    const res = await projectAPI.getById(projectId);
    projectData = res;

    const tickets = await ticketAPI.getByProject(projectId);
    projectData.tickets = tickets;
    console.log("project data", projectData)


    console.log("project data", projectData)



    // Render Project Info
    $('#projectName').text(projectData.name);
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

    console.log("project ", projectData)

    const employeeId = String(window.APP.EMPLOYEE_ID);

    const isManagerOrLead =
    employeeId === String(projectData.createdById) ||
    employeeId === String(projectData.projectManagerId) ||
    employeeId === String(projectData.projectTeamLeadId);


    if (isManagerOrLead) {
      $('#createTicketBtn').show();
      $('#addMemberBtn').show();
    }

    // Bind event
    $('#createTicketBtn').off('click').on('click', openTicketModal);

  } catch (err) {
    console.error('Error loading project:', err);
    showToast('Failed to load project details', 'error');
  } finally {
    loadingUtils.hide();
  }
}

function renderMembers(members) {
  const tbody = $('#membersTable tbody');
  if (!members.length) {
    tbody.html('<tr><td colspan="4" class="text-center text-muted py-3">No members added</td></tr>');
    return;
  }

  const isManagerOrLead =
    window.APP.EMPLOYEE_ID === projectData.createdById ||
    window.APP.EMPLOYEE_ID === projectData.projectManagerId ||
    window.APP.EMPLOYEE_ID === projectData.projectTeamLeadId;

  const html = members.map(m => {
    const isCreator = m.employeeId === projectData.createdById;
    const isManager = m.employeeId === projectData.projectManagerId;
    const isLead = m.employeeId === projectData.projectTeamLeadId;

    // Determine badge label and color
    let roleBadge = '<span class="badge bg-secondary">Member</span>';
    if (isCreator) roleBadge = '<span class="badge bg-dark">Creator</span>';
    else if (isManager) roleBadge = '<span class="badge bg-primary">Manager</span>';
    else if (isLead) roleBadge = '<span class="badge bg-info text-dark">Team Lead</span>';

    const isProtected = isCreator || isManager || isLead;

    return `
      <tr>
        <td>
          ${m.employeeName}
          <span class="ms-2">${roleBadge}</span>
        </td>
        <td>${m.departmentName || '-'}</td>
        <td>${m.email || '-'}</td>
        <td class="text-end">
          ${isManagerOrLead && !isProtected ? `
            <button class="btn btn-sm btn-outline-danger" title="Remove Member"
              onclick="removeMember(${m.employeeId})">
              <i class="fas fa-trash"></i>
            </button>` : ''}
        </td>
      </tr>
    `;
  }).join('');

  tbody.html(html);

  // Enable Add Member button only for authorized roles
  const canEdit = isManagerOrLead;
  $('#addMemberBtn').toggle(canEdit).off('click').on('click', openAddMemberModal);
}


function renderTickets(tickets) {
  const tbody = $('#ticketsTable tbody');
  if (!tickets.length) {
    tbody.html('<tr><td colspan="6" class="text-center text-muted py-3">No tickets found</td></tr>');
    return;
  }

  const isManagerOrLead =
    window.APP.EMPLOYEE_ID === projectData.createdById ||
    window.APP.EMPLOYEE_ID === projectData.projectManagerId ||
    window.APP.EMPLOYEE_ID === projectData.projectTeamLeadId;

  const html = tickets.map(t => {
    const canPick =
      !isManagerOrLead &&
      (!t.assigneeIds || t.assigneeIds.length === 0) &&
      projectData.members.some(m => m.employeeId === window.APP.EMPLOYEE_ID);

    return `
      <tr>
        <td>
          <a href="${window.APP.CONTEXT_PATH}/work/tickets/${t.id}" class="fw-semibold">${t.title}</a>
        </td>
        <td>${getStatusBadge(t.status)}</td>
        <td>${(t.assigneeNames || []).join(', ') || '-'}</td>
        <td>${formatDate(t.startDate)}</td>
        <td>${formatDate(t.endDate)}</td>
        <td>
          ${isManagerOrLead ? `
            <button class="btn btn-sm btn-outline-warning" onclick="editTicket(${t.id})"><i class="fas fa-edit"></i></button>
            <button class="btn btn-sm btn-outline-danger" onclick="deleteTicket(${t.id})"><i class="fas fa-trash"></i></button>
          ` : canPick ? `
            <button class="btn btn-sm btn-outline-success" onclick="pickTicket(${t.id})">Pick this Ticket</button>
          ` : `
            <button class="btn btn-sm btn-outline-primary" onclick="viewTicket(${t.id})"><i class="fas fa-eye"></i></button>
          `}
        </td>
      </tr>
    `;
  }).join('');

  tbody.html(html);
}

// ============================================================
// LOAD SUBORDINATES (for ticket assignee dropdown)
// ============================================================
async function loadSubordinates() {
  try {
    const res = await employeeAPI.getSubordinates(window.APP.EMPLOYEE_ID);
    allSubordinates = res;
    const options = allSubordinates
      .map(e => `<option value="${e.id}">${e.fullName} ${e.employeeCode} (${e.department || 'N/A'})</option>`)
      .join('');
    $('#ticketAssigneesSelect').html(options);
  } catch (err) {
    console.error('Failed to load subordinates:', err);
  }
}

// ============================================================
// TICKET CREATE / EDIT / DELETE
// ============================================================
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
  formData.delete('attachments'); // ✅ prevent parse error

  const data = {};
  formData.forEach((v, k) => (data[k] = v || null));

  data.assigneeIds = $('#ticketAssigneesSelect').val()?.map(Number) || [];
  data.organisationId = window.APP.ORG_ID;
  data.createdById = window.APP.EMPLOYEE_ID;
  data.projectId = projectId;

  console.log("Ticket Payload:", data);

  try {
    loadingUtils.show();

    if (editingTicketId) {
      await ticketAPI.update(editingTicketId, data);
      showToast('Ticket updated successfully', 'success');
    } else {
      const created = await ticketAPI.create(data);
      showToast('Ticket created successfully', 'success');

      // Upload attachments only if chosen
      const files = $('#ticketAttachments')[0].files;
      if (files.length > 0) await uploadTicketAttachments(created.id);
    }
    modalUtils.close('ticketModal');
    await loadProjectDetails();
  } catch (error) {
    console.error(error);
    showToast(error.message || 'Failed to save ticket', 'error');
  } finally {
    loadingUtils.hide();
  }
}


async function editTicket(id) {
  try {
    const res = await ticketAPI.getById(id, window.APP.ORG_ID, window.APP.EMPLOYEE_ID);
    const t = res.data;
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
    showToast('Failed to load ticket', 'error');
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
      showToast('Ticket deleted successfully', 'success');
      await loadProjectDetails();
    } catch (err) {
      showToast('Failed to delete ticket', 'error');
    } finally {
      loadingUtils.hide();
    }
  });
}

// ============================================================
// PICK TICKET (for project members)
// ============================================================
async function pickTicket(id) {
  try {
    const res = await ticketAPI.getById(id, window.APP.ORG_ID, window.APP.EMPLOYEE_ID);
    const ticket = res.data;
    const assigneeIds = ticket.assigneeIds || [];
    assigneeIds.push(window.APP.EMPLOYEE_ID);

    await ticketAPI.update(id, {
      ...ticket,
      assigneeIds,
      organisationId: window.APP.ORG_ID,
      employeeId: window.APP.EMPLOYEE_ID
    });

    showToast('You have picked this ticket', 'success');
    await loadProjectDetails();
  } catch (err) {
    console.error('Error picking ticket:', err);
    showToast('Failed to pick this ticket', 'error');
  }
}


// ============================================================
// PROJECT MEMBERS MANAGEMENT
// ============================================================

function renderMembers(members) {
  const tbody = $('#membersTable tbody');
  if (!members.length) {
    tbody.html('<tr><td colspan="4" class="text-center text-muted py-3">No members added</td></tr>');
    return;
  }

  const isManagerOrLead =
    window.APP.EMPLOYEE_ID === projectData.createdById ||
    window.APP.EMPLOYEE_ID === projectData.projectManagerId ||
    window.APP.EMPLOYEE_ID === projectData.projectTeamLeadId;

  const html = members.map(m => {
    const isProtected =
      m.employeeId === projectData.createdById ||
      m.employeeId === projectData.projectManagerId ||
      m.employeeId === projectData.projectTeamLeadId;

    return `
      <tr>
        <td>${m.employeeName}</td>
        <td>${m.departmentName || '-'}</td>
        <td>${m.email || '-'}</td>
        <td class="text-end">
          ${isManagerOrLead && !isProtected ? `
            <button class="btn btn-sm btn-outline-danger" title="Remove Member"
              onclick="removeMember(${m.employeeId})">
              <i class="fas fa-trash"></i>
            </button>` : ''}
        </td>
      </tr>
    `;
  }).join('');

  tbody.html(html);

  // Enable Add Member button only for authorized roles
  const canEdit = isManagerOrLead;
  $('#addMemberBtn').toggle(canEdit).off('click').on('click', openAddMemberModal);
}

// ============================================================
// OPEN ADD MEMBER MODAL
// ============================================================
async function openAddMemberModal() {
  try {
    loadingUtils.show();
    const res = await employeeAPI.getSubordinates(window.APP.EMPLOYEE_ID, window.APP.ORG_ID);
    const employees = res.data || [];

    // Exclude already existing members
    const existingIds = (projectData.members || []).map(m => m.employeeId);
    const available = employees.filter(e => !existingIds.includes(e.id));

    if (!available.length) {
      showToast('No new subordinates available to add.', 'info');
      return;
    }

    const options = available
      .map(e => `<option value="${e.id}">${e.firstName} ${e.lastName} (${e.departmentName || 'N/A'})</option>`)
      .join('');
    $('#addMemberSelect').html(options);

    modalUtils.open('addMemberModal');
  } catch (err) {
    console.error('Failed to load subordinates:', err);
    showToast('Failed to load employees list', 'error');
  } finally {
    loadingUtils.hide();
  }
}

// ============================================================
// SAVE MEMBERS (POST /api/projects/{id}/members)
// ============================================================
async function saveMembers() {
  const selected = $('#addMemberSelect').val() || [];
  if (!selected.length) {
    showToast('Please select at least one employee.', 'warning');
    return;
  }

  try {
    loadingUtils.show();
    await apiClient.post(
      `/projects/${projectId}/members?organisationId=${window.APP.ORG_ID}&employeeId=${window.APP.EMPLOYEE_ID}`,
      { employeeIds: selected.map(Number) }
    );
    showToast('Members added successfully', 'success');
    modalUtils.close('addMemberModal');
    await loadProjectDetails(); // refresh
  } catch (err) {
    console.error('Error adding members:', err);
    showToast('Failed to add members', 'error');
  } finally {
    loadingUtils.hide();
  }
}

// ============================================================
// REMOVE MEMBER (DELETE /api/projects/{id}/members/{empId})
// ============================================================
async function removeMember(empId) {
  modalUtils.confirm('Remove Member', 'Are you sure you want to remove this member?', async () => {
    try {
      loadingUtils.show();
      await apiClient.delete(
        `/projects/${projectId}/members/${empId}?organisationId=${window.APP.ORG_ID}&employeeId=${window.APP.EMPLOYEE_ID}`
      );
      showToast('Member removed successfully', 'success');
      await loadProjectDetails(); // refresh list
    } catch (err) {
      console.error('Error removing member:', err);
      showToast('Failed to remove member', 'error');
    } finally {
      loadingUtils.hide();
    }
  });
}



async function uploadTicketAttachments(ticketId) {
  const rows = $('#attachmentRows .attachment-row');
  if (!rows.length) {
    showToast('Please select at least one file.', 'warning');
    return;
  }

  const formData = new FormData();
  rows.each(function () {
    const fileInput = $(this).find('input[name="attachments"]')[0];
    const descInput = $(this).find('input[name="attachmentDescriptions"]').val();
    if (fileInput.files.length > 0) {
      formData.append('files', fileInput.files[0]);
      formData.append('descriptions', descInput || '');
    }
  });

  try {
    loadingUtils.show();
    await ticketAPI.addAttachments(ticketId, formData);
    showToast('Attachments uploaded successfully', 'success');
    await loadTicketAttachments(ticketId);
  } catch (err) {
    console.error(err);
    showToast('Failed to upload attachments', 'error');
  } finally {
    loadingUtils.hide();
  }
}

