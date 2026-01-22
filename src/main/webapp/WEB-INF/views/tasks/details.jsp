<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Task Details - HRMS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
    <nav class="navbar navbar-dark bg-dark">
        <div class="container-fluid">
            <a href="${pageContext.request.contextPath}/work/tasks" class="navbar-brand">← Back to Tasks</a>
        </div>
    </nav>

    <div class="container-fluid py-4">
        <div class="row mb-4">
            <div class="col-12">
                <h2 id="taskTitle">Task Details</h2>
            </div>
        </div>

        <div class="row">
            <div class="col-md-8">
                <!-- Task Info -->
                <div class="card mb-4">
                    <div class="card-header">
                        <h5>Task Information</h5>
                    </div>
                    <div class="card-body">
                        <div id="taskInfo"></div>
                    </div>
                </div>

                <!-- Activity Log -->
                <div class="card mb-4">
                    <div class="card-header">
                        <h5>Activity Log</h5>
                    </div>
                    <div class="card-body">
                        <div id="activitiesContainer"></div>
                    </div>
                </div>
            </div>

            <div class="col-md-4">
                <!-- Attachments -->
                <div class="card">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <h5 class="mb-0">Attachments</h5>
                        <button class="btn btn-sm btn-primary" onclick="openAttachmentUpload()">+ Upload</button>
                    </div>
                    <div class="card-body">
                        <div id="attachmentsContainer"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Attachment Upload Modal -->
    <div id="attachmentModal" class="modal">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Upload Attachment</h5>
                    <button type="button" class="close" data-dismiss="modal"><span>&times;</span></button>
                </div>
                <form id="attachmentForm">
                    <div class="modal-body">
                        <div class="form-group mb-3">
                            <label>File *</label>
                            <input type="file" name="file" id="fileInput" class="form-control" required>
                        </div>
                        <div class="form-group mb-3">
                            <label>Description</label>
                            <input type="text" name="description" class="form-control">
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary">Upload</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/resources/js/apiClient.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/modalUtils.js"></script>
    <script>
        // const taskId = ${taskId};
        let task = null;
        let attachmentModal;

        document.addEventListener('DOMContentLoaded', () => {
            attachmentModal = new ModalManager('attachmentModal');
            loadTaskDetails();
            
            document.getElementById('attachmentForm').addEventListener('submit', handleAttachmentUpload);
        });

        async function loadTaskDetails() {
            try {
                task = await TaskAPI.getById(taskId);
                
                document.getElementById('taskTitle').textContent = task.title;
                
                renderTaskInfo();
                loadActivities();
                loadAttachments();
            } catch (error) {
                showToast('Failed to load task details', 'error');
            }
        }

        function renderTaskInfo() {
            const html = `
                <div class="row mb-3">
                    <div class="col-md-6">
                        <strong>Status:</strong> ${getStatusBadge(task.status)}
                    </div>
                    <div class="col-md-6">
                        <strong>Priority:</strong> ${getPriorityBadge(task.priority)}
                    </div>
                </div>
                <div class="row mb-3">
                    <div class="col-md-6">
                        <strong>Assigned To:</strong> ${task.assignedTo ? 'Employee #' + task.assignedTo : 'Unassigned'}
                    </div>
                    <div class="col-md-6">
                        <strong>Due Date:</strong> ${formatDate(task.dueDate)}
                    </div>
                </div>
                <div class="row mb-3">
                    <div class="col-md-6">
                        <strong>Project:</strong> ${task.projectId ? 'Project #' + task.projectId : 'No Project'}
                    </div>
                    <div class="col-md-6">
                        <strong>Created:</strong> ${formatDateTime(task.createdAt)}
                    </div>
                </div>
                <div class="row">
                    <div class="col-12">
                        <strong>Description:</strong>
                        <p>${task.description || 'No description provided'}</p>
                    </div>
                </div>
            `;
            
            document.getElementById('taskInfo').innerHTML = html;
        }

        async function loadActivities() {
            const container = document.getElementById('activitiesContainer');
            
            try {
                const activities = await TaskAPI.getActivities(taskId);
                
                if (activities.length === 0) {
                    container.innerHTML = '<p class="text-muted">No activity yet</p>';
                    return;
                }
                
                const html = activities.map(a => `
                    <div class="activity-item mb-3 pb-3 border-bottom">
                        <div class="d-flex justify-content-between">
                            <strong>${a.activityType}</strong>
                            <small class="text-muted">${formatDateTime(a.timestamp)}</small>
                        </div>
                        <p class="mb-1">${a.description}</p>
                        <small class="text-muted">By: Employee #${a.performedBy}</small>
                    </div>
                `).join('');
                
                container.innerHTML = html;
            } catch (error) {
                container.innerHTML = '<p class="text-danger">Failed to load activities</p>';
            }
        }

        async function loadAttachments() {
            const container = document.getElementById('attachmentsContainer');
            
            try {
                const attachments = await TaskAPI.getAttachments(taskId);
                
                if (attachments.length === 0) {
                    container.innerHTML = '<p class="text-muted">No attachments</p>';
                    return;
                }
                
                const html = attachments.map(a => `
                    <div class="attachment-item mb-2 p-2 border rounded">
                        <div class="d-flex justify-content-between align-items-center">
                            <div>
                                <strong>${a.fileName}</strong><br>
                                <small class="text-muted">${a.description || ''}</small><br>
                                <small class="text-muted">${formatDateTime(a.uploadedAt)}</small>
                            </div>
                            <a href="${a.fileUrl}" class="btn btn-sm btn-outline-primary" download>Download</a>
                        </div>
                    </div>
                `).join('');
                
                container.innerHTML = html;
            } catch (error) {
                container.innerHTML = '<p class="text-danger">Failed to load attachments</p>';
            }
        }

        function openAttachmentUpload() {
            attachmentModal.reset();
            attachmentModal.show();
        }

        async function handleAttachmentUpload(e) {
            e.preventDefault();
            
            const fileInput = document.getElementById('fileInput');
            const description = document.querySelector('[name="description"]').value;
            
            if (!fileInput.files || fileInput.files.length === 0) {
                showToast('Please select a file', 'error');
                return;
            }
            
            const formData = new FormData();
            formData.append('file', fileInput.files[0]);
            formData.append('description', description);
            
            attachmentModal.setLoading(true);
            
            try {
                await TaskAPI.addAttachment(taskId, formData);
                showToast('Attachment uploaded successfully', 'success');
                attachmentModal.hide();
                loadAttachments();
            } catch (error) {
                attachmentModal.setLoading(false);
                showToast('Failed to upload attachment: ' + error.message, 'error');
            }
        }
    </script>
</body>
</html>