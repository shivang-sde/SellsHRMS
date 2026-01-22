// task-detail.js
let currentTask = null;
const taskId = $('#currentTaskId').val();

$(document).ready(function() {
    loadTaskDetails();
    loadActivities();
    loadAttachments();
});

async function loadTaskDetails() {
    try {
        currentTask = await taskAPI.getById(taskId);
        renderTaskDetails(currentTask);
        renderQuickInfo(currentTask);
    } catch (error) {
        console.error('Failed to load task:', error);
        showToast('Failed to load task details', 'error');
    }
}

function renderTaskDetails(task) {
    $('#taskTitle').html(`
        <i class="fas fa-tasks text-warning me-2"></i>${task.title}
    `);

    const html = `
        <div class="row g-3">
            <div class="col-md-6">
                <label class="text-muted small">Status</label>
                <div>${getStatusBadge(task.status)}</div>
            </div>
            <div class="col-md-6">
                <label class="text-muted small">Priority</label>
                <div>${getPriorityBadge(task.priority)}</div>
            </div>
            <div class="col-md-6">
                <label class="text-muted small">Assigned To</label>
                <div>${task.assignedToName || 'Unassigned'}</div>
            </div>
            <div class="col-md-6">
                <label class="text-muted small">Due Date</label>
                <div>${formatDate(task.dueDate)}</div>
            </div>
            <div class="col-md-6">
                <label class="text-muted small">Estimated Hours</label>
                <div>${task.estimatedHours || 'N/A'}</div>
            </div>
            <div class="col-md-6">
                <label class="text-muted small">Actual Hours</label>
                <div>${task.actualHours || 'N/A'}</div>
            </div>
            <div class="col-12">
                <label class="text-muted small">Description</label>
                <div class="border rounded p-3 bg-light">
                    ${task.description || '<em class="text-muted">No description provided</em>'}
                </div>
            </div>
        </div>
    `;

    $('#taskDetails').html(html);
}

function renderQuickInfo(task) {
    const progress = task.actualHours && task.estimatedHours 
        ? Math.round((task.actualHours / task.estimatedHours) * 100)
        : 0;

    const html = `
        <div class="d-flex flex-column gap-3">
            <div>
                <div class="d-flex justify-content-between mb-1">
                    <small class="text-muted">Progress</small>
                    <small class="fw-semibold">${progress}%</small>
                </div>
                <div class="progress" style="height: 8px;">
                    <div class="progress-bar" role="progressbar" 
                         style="width: ${progress}%" 
                         aria-valuenow="${progress}" aria-valuemin="0" aria-valuemax="100">
                    </div>
                </div>
            </div>
            <div class="border-top pt-2">
                <small class="text-muted d-block">Created By</small>
                <div class="fw-semibold">${task.createdByName || 'Unknown'}</div>
            </div>
            <div class="border-top pt-2">
                <small class="text-muted d-block">Created Date</small>
                <div>${formatDateTime(task.createdDate)}</div>
            </div>
            ${task.updatedDate ? `
                <div class="border-top pt-2">
                    <small class="text-muted d-block">Last Updated</small>
                    <div>${formatDateTime(task.updatedDate)}</div>
                </div>
            ` : ''}
        </div>
    `;

    $('#quickInfo').html(html);
}

async function loadActivities() {
    try {
        const activities = await taskAPI.getActivities(taskId);
        renderActivities(activities);
    } catch (error) {
        console.error('Failed to load activities:', error);
        $('#activityLog').html(`
            <p class="text-muted text-center">Failed to load activity log</p>
        `);
    }
}

function renderActivities(activities) {
    if (activities.length === 0) {
        $('#activityLog').html(`
            <p class="text-muted text-center">No activity yet</p>
        `);
        return;
    }

    const html = activities.map(activity => `
        <div class="d-flex gap-3 mb-3">
            <div class="flex-shrink-0">
                <div class="bg-primary bg-opacity-10 rounded-circle p-2" style="width: 40px; height: 40px;">
                    <i class="fas fa-${getActivityIcon(activity.activityType)} text-primary"></i>
                </div>
            </div>
            <div class="flex-grow-1">
                <div class="fw-semibold">${activity.performedByName}</div>
                <div class="text-muted small">${activity.activityDescription}</div>
                <div class="text-muted small">
                    <i class="fas fa-clock me-1"></i>${formatDateTime(activity.activityDate)}
                </div>
            </div>
        </div>
    `).join('');

    $('#activityLog').html(html);
}

async function loadAttachments() {
    try {
        const attachments = await taskAPI.getAttachments(taskId);
        renderAttachments(attachments);
    } catch (error) {
        console.error('Failed to load attachments:', error);
        $('#attachmentsList').html(`
            <p class="text-muted text-center small">No attachments</p>
        `);
    }
}

function renderAttachments(attachments) {
    if (attachments.length === 0) {
        $('#attachmentsList').html(`
            <p class="text-muted text-center small">No attachments</p>
        `);
        return;
    }

    const html = attachments.map(att => `
        <div class="d-flex align-items-center gap-2 mb-2 p-2 border rounded">
            <i class="fas fa-file text-muted"></i>
            <div class="flex-grow-1 small">
                <a href="${att.fileUrl}" target="_blank" class="text-decoration-none">
                    ${att.fileName}
                </a>
                ${att.description ? `<div class="text-muted">${att.description}</div>` : ''}
            </div>
        </div>
    `).join('');

    $('#attachmentsList').html(html);
}

function openAttachmentModal() {
    modalUtils.resetForm('attachmentModal');
    modalUtils.open('attachmentModal');
}

async function uploadAttachment() {
    const form = document.getElementById('attachmentForm');
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }

    const formData = new FormData(form);

    try {
        loadingUtils.show();
        await taskAPI.addAttachment(taskId, formData);
        showToast('Attachment uploaded successfully', 'success');
        modalUtils.close('attachmentModal');
        loadAttachments();
    } catch (error) {
        showToast('Failed to upload attachment', 'error');
    } finally {
        loadingUtils.hide();
    }
}

function editCurrentTask() {
    window.location.href = `${window.APP.CONTEXT_PATH}/work/tasks?edit=${taskId}`;
}

function deleteCurrentTask() {
    modalUtils.confirm(
        'Delete Task',
        'Are you sure you want to delete this task? This action cannot be undone.',
        async () => {
            try {
                loadingUtils.show();
                await taskAPI.delete(taskId);
                showToast('Task deleted successfully', 'success');
                window.location.href = `${window.APP.CONTEXT_PATH}/work/tasks`;
            } catch (error) {
                showToast('Failed to delete task', 'error');
                loadingUtils.hide();
            }
        }
    );
}

function getActivityIcon(type) {
    const icons = {
        'CREATED': 'plus-circle',
        'UPDATED': 'edit',
        'STATUS_CHANGED': 'exchange-alt',
        'COMMENT': 'comment',
        'ATTACHMENT': 'paperclip'
    };
    return icons[type] || 'circle';
}