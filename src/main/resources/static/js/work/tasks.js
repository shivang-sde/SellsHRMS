let selfTasks = [];
let editingTaskId = null;
let removedAttachmentIds = [];
let existingAttachments = [];

// --------------------------------------------
// INITIALIZE
// --------------------------------------------
$(document).ready(function () {
  loadSelfTasks();

  // Toggle Reminder field dynamically
  $('#taskStatusSelect').on('change', function () {
    const status = $(this).val();
    if (status === 'REMINDER') {
      $('#reminderGroup').show();
      $('[name="reminderAt"]').attr('required', true);
    } else {
      $('#reminderGroup').hide();
      $('[name="reminderAt"]').removeAttr('required').val('');
    }
  });

  // Show new attachments preview
  $('[name="attachments"]').on('change', function () {
    const files = Array.from(this.files);
    const preview = $('#newAttachmentsPreview');
    if (files.length) {
      preview.html(files.map(f => `<div><i class="fas fa-file me-2"></i>${f.name}</div>`).join(''));
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
    loadingUtils.show('#selfTasksTable tbody');
    const res = await taskAPI.getMyTasks(window.APP.EMPLOYEE_ID);
    const data = res?.data || res;
    selfTasks = Array.isArray(data) ? data : [];
    renderSelfTasks(selfTasks);
  } catch (error) {
    console.error('Failed to load self tasks', error);
    showToast('Failed to load tasks', 'error');
  } finally {
    loadingUtils.hide();
  }
}

// --------------------------------------------
// RENDER TASKS TABLE
// --------------------------------------------
function renderSelfTasks(tasks) {
  const tbody = $('#selfTasksTable tbody');
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

  const html = tasks.map(task => `
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
  `).join('');

  tbody.html(html);
}

// --------------------------------------------
// OPEN MODAL FOR NEW TASK
// --------------------------------------------
function openSelfTaskModal() {
  editingTaskId = null;
  removedAttachmentIds = [];
  existingAttachments = [];

  $('#selfTaskModalTitle').text('Add Task / Reminder');
  modalUtils.resetForm('selfTaskModal');
  $('#existingAttachments').empty();
  $('#newAttachmentsPreview').empty();
  $('[name="status"]').val('TO_DO');
  modalUtils.open('selfTaskModal');
}

// --------------------------------------------
// SAVE OR UPDATE TASK
// --------------------------------------------
// --------------------------------------------
// SAVE OR UPDATE SELF TASK / REMINDER
// --------------------------------------------
async function saveSelfTask() {
  const form = document.getElementById('selfTaskForm');
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
    title: formFields.title || '',
    description: formFields.description || '',
    status: formFields.status || 'TO_DO',
    reminderAt: formFields.reminderAt || null,
    reminderEnabled: formFields.status === 'REMINDER'
  };

  // 2️⃣ Build FormData
  const formData = new FormData();
  formData.append('task', new Blob([JSON.stringify(taskData)], { type: 'application/json' }));

  // 3️⃣ Append new attachments with their descriptions
  const attachmentRows = document.querySelectorAll('#attachmentRows .attachment-row');
  attachmentRows.forEach((row, i) => {
    const fileInput = row.querySelector('input[name="attachments"]');
    const descInput = row.querySelector('input[name="attachmentDescriptions"]');

    if (fileInput?.files.length) {
      formData.append('attachments', fileInput.files[0]); // file
      formData.append('descriptions', descInput?.value?.trim() || ''); // description
    }
  });

  // 4️⃣ Append removed attachment IDs (edit mode)
  if (Array.isArray(removedAttachmentIds) && removedAttachmentIds.length) {
    removedAttachmentIds.forEach(id => formData.append('removeAttachmentIds', id));
  }

  try {
    loadingUtils.show();

    // 5️⃣ Determine endpoint
    const url = editingTaskId
      ? `${window.APP.CONTEXT_PATH}/api/tasks/${editingTaskId}?organisationId=${organisationId}&employeeId=${employeeId}`
      : `${window.APP.CONTEXT_PATH}/api/tasks?organisationId=${organisationId}&reporterId=${employeeId}`;

    const method = editingTaskId ? 'PUT' : 'POST';

    // 6️⃣ Send multipart request
    const response = await fetch(url, {
      method,
      body: formData
    });

    const result = await response.json();

    if (!response.ok) throw new Error(result?.message || 'Failed to save task');

    showToast('Task saved successfully', 'success');
    modalUtils.close('selfTaskModal');

    // Refresh tasks table
    loadSelfTasks();

    // Reset form state
    editingTaskId = null;
    removedAttachmentIds = [];
    existingAttachments = [];
    $('#attachmentRows').empty();
    addAttachmentRow(); // initial blank row
    $('#newAttachmentsPreview').empty();

  } catch (error) {
    console.error('Error saving task:', error);
    showToast(error.message || 'Failed to save task', 'error');
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
    modalUtils.resetForm('selfTaskModal');

    // 3️⃣ Populate basic fields
    Object.keys(task).forEach(key => {
      const input = $(`[name="${key}"]`);
      if (input.length) {
        if (input.attr('type') === 'datetime-local' && task[key]) {
          // Convert to 'YYYY-MM-DDTHH:mm' format for input
          input.val(task[key].slice(0, 16));
        } else if (input.is('select')) {
          input.val(task[key]);
        } else {
          input.val(task[key] ?? '');
        }
      }
    });

    // 4️⃣ Fetch existing attachments
    const attachRes = await taskAPI.getAttachments(taskId);
    existingAttachments = attachRes?.data || attachRes || [];
    renderExistingAttachments(existingAttachments);

    // 5️⃣ Reset new attachment rows
    $('#attachmentRows').empty();
    addAttachmentRow(); // Add initial empty row

    // 6️⃣ Show modal
    $('#selfTaskModalTitle').text('Edit Task / Reminder');
    modalUtils.open('selfTaskModal');

  } catch (error) {
    console.error('Edit task error:', error);
    showToast('Failed to load task details', 'error');
  }
}


// --------------------------------------------
// RENDER EXISTING ATTACHMENTS
// --------------------------------------------
function renderExistingAttachments(attachments) {
  const container = $('#existingAttachments');
  container.empty();

  if (!attachments.length) {
    container.html('<p class="text-muted">No attachments yet.</p>');
    return;
  }

  const html = attachments.map(a => `
    <div class="attachment-item d-flex justify-content-between align-items-center border rounded p-2 mb-2">
      <a href="${window.APP.CONTEXT_PATH}${a.fileUrl}" target="_blank">
        <i class="fas fa-paperclip me-2"></i>${a.fileName}
      </a>
      <button type="button" class="btn btn-sm btn-outline-danger" onclick="removeAttachment(${a.id})">
        <i class="fas fa-times"></i>
      </button>
    </div>
  `).join('');

  container.html(html);
}

function removeAttachment(id) {
  // Mark attachment as removed
  removedAttachmentIds.push(id);

  // Remove from DOM
  $(`button[onclick="removeAttachment(${id})"]`).closest('.attachment-item').remove();
}


// --------------------------------------------
// DELETE TASK
// --------------------------------------------
async function deleteSelfTask(id) {
  modalUtils.confirm('Delete Task', 'Are you sure you want to delete this task?', async () => {
    try {
      await taskAPI.delete(id);
      showToast('Task deleted successfully', 'success');
      loadSelfTasks();
    } catch (error) {
      showToast('Failed to delete task', 'error');
    }
  });
}


function addAttachmentRow() {
  $('#attachmentRows').append(`
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

