<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Project Details - HRMS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
    <nav class="navbar navbar-dark bg-dark">
        <div class="container-fluid">
            <a href="${pageContext.request.contextPath}/work/projects" class="navbar-brand">← Back to Projects</a>
        </div>
    </nav>

    <div class="container-fluid py-4">
        <div class="row mb-4">
            <div class="col-12">
                <h2 id="projectName">Project Details</h2>
                <p class="text-muted" id="projectCode"></p>
            </div>
        </div>

        <div class="row">
            <!-- Project Info -->
            <div class="col-md-8">
                <div class="card mb-4">
                    <div class="card-header">
                        <h5>Project Information</h5>
                    </div>
                    <div class="card-body">
                        <div id="projectInfo"></div>
                    </div>
                </div>

                <!-- Tasks Tab -->
                <div class="card mb-4">
                    <div class="card-header">
                        <h5>Project Tasks</h5>
                    </div>
                    <div class="card-body">
                        <div id="tasksContainer"></div>
                    </div>
                </div>

                <!-- Tickets Tab -->
                <div class="card">
                    <div class="card-header">
                        <h5>Project Tickets</h5>
                    </div>
                    <div class="card-body">
                        <div id="ticketsContainer"></div>
                    </div>
                </div>
            </div>

            <!-- Sidebar -->
            <div class="col-md-4">
                <!-- Project Members -->
                <div class="card mb-4">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <h5 class="mb-0">Team Members</h5>
                        <button class="btn btn-sm btn-primary" onclick="openMemberModal()">+ Add</button>
                    </div>
                    <div class="card-body">
                        <div id="membersContainer"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Member Modal -->
    <div id="memberModal" class="modal">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Add Team Member</h5>
                    <button type="button" class="close" data-dismiss="modal">
                        <span>&times;</span>
                    </button>
                </div>
                <form id="memberForm">
                    <div class="modal-body">
                        <input type="hidden" name="id" id="memberId">
                        <input type="hidden" name="projectId" id="modalProjectId" value="${projectId}">
                        
                        <div class="form-group mb-3">
                            <label>Employee ID *</label>
                            <input type="number" name="employeeId" class="form-control" required>
                        </div>

                        <div class="form-group mb-3">
                            <label>Role *</label>
                            <select name="role" class="form-select" required>
                                <option value="">Select Role</option>
                                <option value="PROJECT_MANAGER">Project Manager</option>
                                <option value="TEAM_LEAD">Team Lead</option>
                                <option value="DEVELOPER">Developer</option>
                                <option value="DESIGNER">Designer</option>
                                <option value="TESTER">Tester</option>
                                <option value="ANALYST">Analyst</option>
                            </select>
                        </div>

                        <div class="form-group mb-3">
                            <label>
                                <input type="checkbox" name="isActive"> Active Member
                            </label>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary">Save Member</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/resources/js/apiClient.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/modalUtils.js"></script>
    <script>
        let project = null;
        let members = [];
        let memberModal;

        document.addEventListener('DOMContentLoaded', () => {
            memberModal = new ModalManager('memberModal');
            loadProjectDetails();
            
            document.getElementById('memberForm').addEventListener('submit', handleMemberSubmit);
        });

        async function loadProjectDetails() {
            try {
                project = await ProjectAPI.getById(projectId);
                
                document.getElementById('projectName').textContent = project.name;
                document.getElementById('projectCode').textContent = `Code: ${project.code}`;
                
                renderProjectInfo();
                loadMembers();
                loadTasks();
                loadTickets();
            } catch (error) {
                showToast('Failed to load project details', 'error');
            }
        }

        function renderProjectInfo() {
            const html = `
                <div class="row mb-3">
                    <div class="col-md-6">
                        <strong>Type:</strong> <span class="badge badge-info">${project.type}</span>
                    </div>
                    <div class="col-md-6">
                        <strong>Status:</strong> ${getStatusBadge(project.status)}
                    </div>
                </div>
                <div class="row mb-3">
                    <div class="col-md-6">
                        <strong>Priority:</strong> ${getPriorityBadge(project.priority)}
                    </div>
                    <div class="col-md-6">
                        <strong>Budget:</strong> $${project.budget || 'N/A'}
                    </div>
                </div>
                <div class="row mb-3">
                    <div class="col-md-6">
                        <strong>Start Date:</strong> ${formatDate(project.startDate)}
                    </div>
                    <div class="col-md-6">
                        <strong>End Date:</strong> ${formatDate(project.endDate)}
                    </div>
                </div>
                <div class="row">
                    <div class="col-12">
                        <strong>Description:</strong>
                        <p>${project.description || 'No description provided'}</p>
                    </div>
                </div>
            `;
            
            document.getElementById('projectInfo').innerHTML = html;
        }

        async function loadMembers() {
            try {
                members = await MemberAPI.byProject(projectId);
                renderMembers();
            } catch (error) {
                console.error('Failed to load members:', error);
            }
        }

        function renderMembers() {
            const container = document.getElementById('membersContainer');
            
            if (members.length === 0) {
                container.innerHTML = '<p class="text-muted">No members yet</p>';
                return;
            }
            
            const html = members.map(m => `
                <div class="member-item d-flex justify-content-between align-items-center mb-2 p-2 border rounded">
                    <div>
                        <strong>Employee #${m.employeeId}</strong><br>
                        <small class="text-muted">${m.role}</small>
                    </div>
                    <button onclick="removeMember(${m.id})" class="btn btn-sm btn-outline-danger">Remove</button>
                </div>
            `).join('');
            
            container.innerHTML = html;
        }

        function openMemberModal() {
            memberModal.setTitle('Add Team Member');
            memberModal.reset();
            memberModal.show();
        }

        async function handleMemberSubmit(e) {
            e.preventDefault();
            
            const formData = memberModal.getFormData();
            formData.projectId = projectId;
            
            memberModal.setLoading(true);
            
            try {
                await MemberAPI.add(formData);
                showToast('Member added successfully', 'success');
                memberModal.hide();
                loadMembers();
            } catch (error) {
                memberModal.setLoading(false);
                showToast('Failed to add member: ' + error.message, 'error');
            }
        }

        function removeMember(id) {
            showConfirmDialog(
                'Remove Member',
                'Are you sure you want to remove this member from the project?',
                async () => {
                    try {
                        await MemberAPI.remove(id);
                        showToast('Member removed successfully', 'success');
                        loadMembers();
                    } catch (error) {
                        showToast('Failed to remove member: ' + error.message, 'error');
                    }
                }
            );
        }

        async function loadTasks() {
            const container = document.getElementById('tasksContainer');
            try {
                const tasks = await TaskAPI.byProject(projectId);
                
                if (tasks.length === 0) {
                    container.innerHTML = '<p class="text-muted">No tasks yet</p>';
                    return;
                }
                
                const columns = [
                    { label: 'Title', field: 'title' },
                    { label: 'Status', field: 'status', render: (row) => getStatusBadge(row.status) },
                    { label: 'Priority', field: 'priority', render: (row) => getPriorityBadge(row.priority) },
                    { label: 'Due Date', field: 'dueDate', render: (row) => formatDate(row.dueDate) }
                ];
                
                renderTable(tasks, columns, container);
            } catch (error) {
                container.innerHTML = '<p class="text-danger">Failed to load tasks</p>';
            }
        }

        async function loadTickets() {
            const container = document.getElementById('ticketsContainer');
            try {
                const tickets = await TicketAPI.byProject(projectId);
                
                if (tickets.length === 0) {
                    container.innerHTML = '<p class="text-muted">No tickets yet</p>';
                    return;
                }
                
                const columns = [
                    { label: 'Title', field: 'title' },
                    { label: 'Category', field: 'category' },
                    { label: 'Status', field: 'status', render: (row) => getStatusBadge(row.status) },
                    { label: 'Priority', field: 'priority', render: (row) => getPriorityBadge(row.priority) }
                ];
                
                renderTable(tickets, columns, container);
            } catch (error) {
                container.innerHTML = '<p class="text-danger">Failed to load tickets</p>';
            }
        }
    </script>
</body>
</html>