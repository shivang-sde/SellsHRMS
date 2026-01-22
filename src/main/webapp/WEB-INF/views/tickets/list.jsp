<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ticket Management - HRMS</title>
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
                <h2>Ticket Management</h2>
            </div>
            <div class="col-md-6 text-end">
                <button class="btn btn-primary" onclick="openTicketModal()">+ New Ticket</button>
            </div>
        </div>

        <!-- Filters -->
        <div class="card mb-4">
            <div class="card-body">
                <div class="row g-3">
                    <div class="col-md-3">
                        <input type="text" id="searchInput" class="form-control" placeholder="Search tickets...">
                    </div>
                    <div class="col-md-2">
                        <select id="statusFilter" class="form-select">
                            <option value="">All Status</option>
                            <option value="OPEN">Open</option>
                            <option value="IN_PROGRESS">In Progress</option>
                            <option value="RESOLVED">Resolved</option>
                            <option value="CLOSED">Closed</option>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <select id="priorityFilter" class="form-select">
                            <option value="">All Priority</option>
                            <option value="LOW">Low</option>
                            <option value="MEDIUM">Medium</option>
                            <option value="HIGH">High</option>
                            <option value="CRITICAL">Critical</option>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <select id="categoryFilter" class="form-select">
                            <option value="">All Categories</option>
                            <option value="BUG">Bug</option>
                            <option value="FEATURE">Feature</option>
                            <option value="SUPPORT">Support</option>
                            <option value="ENHANCEMENT">Enhancement</option>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <button class="btn btn-secondary w-100" onclick="clearFilters()">Clear</button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Tickets Table -->
        <div class="card">
            <div class="card-body">
                <div id="ticketsTableContainer"></div>
            </div>
        </div>
    </div>

    <!-- Ticket Modal -->
    <div id="ticketModal" class="modal">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Add Ticket</h5>
                    <button type="button" class="close" data-dismiss="modal"><span>&times;</span></button>
                </div>
                <form id="ticketForm">
                    <div class="modal-body">
                        <input type="hidden" name="id" id="ticketId">
                        
                        <div class="row">
                            <div class="col-md-8">
                                <div class="form-group mb-3">
                                    <label>Ticket Title *</label>
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
                                    <label>Category *</label>
                                    <select name="category" class="form-select" required>
                                        <option value="">Select</option>
                                        <option value="BUG">Bug</option>
                                        <option value="FEATURE">Feature</option>
                                        <option value="SUPPORT">Support</option>
                                        <option value="ENHANCEMENT">Enhancement</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="form-group mb-3">
                                    <label>Status *</label>
                                    <select name="status" class="form-select" required>
                                        <option value="OPEN">Open</option>
                                        <option value="IN_PROGRESS">In Progress</option>
                                        <option value="RESOLVED">Resolved</option>
                                        <option value="CLOSED">Closed</option>
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
                        </div>

                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group mb-3">
                                    <label>Reporter ID *</label>
                                    <input type="number" name="reporterId" class="form-control" required>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group mb-3">
                                    <label>Due Date</label>
                                    <input type="date" name="dueDate" class="form-control">
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary">Save Ticket</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/resources/js/apiClient.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/modalUtils.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/tickets.js"></script>
</body>
</html>