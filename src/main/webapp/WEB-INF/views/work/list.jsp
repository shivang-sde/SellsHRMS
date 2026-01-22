<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Project Management - HRMS</title>
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
                <h2>Project Management</h2>
            </div>
            <div class="col-md-6 text-end">
                <button class="btn btn-primary" onclick="openProjectModal()">
                    + New Project
                </button>
            </div>
        </div>

        <!-- Filters -->
        <div class="card mb-4">
            <div class="card-body">
                <div class="row g-3">
                    <div class="col-md-4">
                        <input type="text" id="searchInput" class="form-control" placeholder="Search projects...">
                    </div>
                    <div class="col-md-3">
                        <select id="statusFilter" class="form-select">
                            <option value="">All Status</option>
                            <option value="ACTIVE">Active</option>
                            <option value="COMPLETED">Completed</option>
                            <option value="ON_HOLD">On Hold</option>
                            <option value="CANCELLED">Cancelled</option>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <select id="typeFilter" class="form-select">
                            <option value="">All Types</option>
                            <option value="INTERNAL">Internal</option>
                            <option value="CLIENT">Client</option>
                            <option value="RESEARCH">Research</option>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <button class="btn btn-secondary w-100" onclick="clearFilters()">Clear</button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Projects Table -->
        <div class="card">
            <div class="card-body">
                <div id="projectsTableContainer"></div>
            </div>
        </div>
    </div>

    <!-- Project Modal -->
    <div id="projectModal" class="modal">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Add Project</h5>
                    <button type="button" class="close" data-dismiss="modal">
                        <span>&times;</span>
                    </button>
                </div>
                <form id="projectForm">
                    <div class="modal-body">
                        <input type="hidden" name="id" id="projectId">
                        <input type="hidden" name="organisationId" value="${organisationId != null ? organisationId : 1}">
                        
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group mb-3">
                                    <label>Project Name *</label>
                                    <input type="text" name="name" class="form-control" required>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group mb-3">
                                    <label>Project Code *</label>
                                    <input type="text" name="code" class="form-control" required>
                                </div>
                            </div>
                        </div>

                        <div class="form-group mb-3">
                            <label>Description</label>
                            <textarea name="description" class="form-control" rows="3"></textarea>
                        </div>

                        <div class="row">
                            <div class="col-md-4">
                                <div class="form-group mb-3">
                                    <label>Type *</label>
                                    <select name="type" class="form-select" required>
                                        <option value="">Select Type</option>
                                        <option value="INTERNAL">Internal</option>
                                        <option value="CLIENT">Client</option>
                                        <option value="RESEARCH">Research</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="form-group mb-3">
                                    <label>Status *</label>
                                    <select name="status" class="form-select" required>
                                        <option value="ACTIVE">Active</option>
                                        <option value="ON_HOLD">On Hold</option>
                                        <option value="COMPLETED">Completed</option>
                                        <option value="CANCELLED">Cancelled</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-md-4">
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
                        </div>

                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group mb-3">
                                    <label>Start Date *</label>
                                    <input type="date" name="startDate" class="form-control" required>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group mb-3">
                                    <label>End Date *</label>
                                    <input type="date" name="endDate" class="form-control" required>
                                </div>
                            </div>
                        </div>

                        <div class="form-group mb-3">
                            <label>Budget</label>
                            <input type="number" name="budget" class="form-control" step="0.01">
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary">Save Project</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/resources/js/apiClient.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/modalUtils.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/projects.js"></script>
</body>
</html>