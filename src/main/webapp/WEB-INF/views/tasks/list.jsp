<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Task Management - HRMS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
    <nav class="navbar navbar-dark bg-dark">
        <div class="container-fluid">
            <a href="${pageContext.request.contextPath}/work/dashboard" class="navbar-brand">HRMS - Work Management</a>
        </div>
    </nav>

    <div class="container-fluid py-4">
        <div class="row mb-4">
            <div class="col-md-6">
                <h2>Task Management</h2>
            </div>
            <div class="col-md-6 text-end">
                <button class="btn btn-primary" onclick="openTaskModal()">+ New Task</button>
            </div>
        </div>

        <div class="card">
            <div class="card-body">
                <div id="tasksTableContainer"></div>
            </div>
        </div>
    </div>

    <!-- Task Modal -->
    <div id="taskModal" class="modal">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Add Task</h5>
                    <button type="button" class="close" data-dismiss="modal"><span>&times;</span></button>
                </div>
                <form id="taskForm">
                    <div class="modal-body">
                        <input type="hidden" name="id" id="taskId">
                        
                        <div class="row">
                            <div class="col-md-8">
                                <div class="form-group mb-3">
                                    <label>Task Title *</label>
                                    <input type="text" name="title" class="form-control" required>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="form-group mb-3">
                                    <label>Project ID</label>
                                    <input type="number" name="projectId" class="form-control" value="${projectId}">
                                </div>
                            </div>
                        </div>

                        <div class="form-group mb-3">
                            <label>Description</label>
                            <textarea name="description" class="form-control" rows="3"></textarea>
                        </div>

                        <div class="row">
                            <div class="col-md-3">
                                <div class="form-group mb-3">
                                    <label>Status *</label>
                                    <select name="status" class="form-select" required>
                                        <option value="OPEN">Open</option>
                                        <option value="IN_PROGRESS">In Progress</option>
                                        <option value="COMPLETED">Completed</option>
                                        <option value="ON_HOLD">On Hold</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="form-group mb-3">
                                    <label>Priority *</label>
                                    <select name="priority" class="form-select" required>
                                        <option value="LOW">Low</option>
                                        <option value="MEDIUM">Medium</option>
                                        <option value="HIGH">High</option>
                                        <option value="CRITICAL">Critical</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="form-group mb-3">
                                    <label>Assigned To</label>
                                    <input type="number" name="assignedTo" class="form-control">
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="form-group mb-3">
                                    <label>Due Date *</label>
                                    <input type="date" name="dueDate" class="form-control" required>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary">Save Task</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/resources/js/apiClient.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/modalUtils.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/tasks.js"></script>
</body>
</html>