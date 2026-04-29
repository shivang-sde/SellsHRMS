<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="d-flex flex-wrap justify-content-between align-items-center mb-3 gap-2">
    <h4 class="mb-0"><i class="fa fa-envelope-open-text me-2 text-primary"></i>Notification Template Management</h4>
    <div class="d-flex gap-2">
        <button class="btn btn-outline-secondary" onclick="loadTemplates()"><i class="fa fa-rotate me-1"></i>Refresh</button>
        <button class="btn btn-outline-info" onclick="seedDefaultTemplates()"><i class="fa fa-seedling me-1"></i>Seed Default Templates</button>
        <button class="btn btn-primary-hrms" onclick="openCreateModal()"><i class="fa fa-plus me-1"></i>Create Template</button>
    </div>
</div>

<div class="row g-3 mb-3">
    <div class="col-sm-6 col-xl-3"><div class="card hrms-card shadow-sm"><div class="card-body"><div class="text-muted small">Total Templates</div><h4 id="totalCount" class="mb-0">0</h4></div></div></div>
    <div class="col-sm-6 col-xl-3"><div class="card hrms-card shadow-sm"><div class="card-body"><div class="text-muted small">Active</div><h4 id="activeCount" class="mb-0 text-success">0</h4></div></div></div>
    <div class="col-sm-6 col-xl-3"><div class="card hrms-card shadow-sm"><div class="card-body"><div class="text-muted small">Disabled</div><h4 id="disabledCount" class="mb-0 text-secondary">0</h4></div></div></div>
    <div class="col-sm-6 col-xl-3"><div class="card hrms-card shadow-sm"><div class="card-body"><div class="text-muted small">Roles Count</div><h4 id="rolesCount" class="mb-0 text-info">0</h4></div></div></div>
</div>

<div class="card hrms-card shadow-sm mb-3 sticky-top" style="top:72px; z-index:1000;">
    <div class="card-body">
        <div class="row g-2 align-items-end">
            <div class="col-md-4">
                <label class="form-label small fw-semibold mb-1">Search</label>
                <input type="text" class="form-control" id="searchInput" placeholder="Search event code, subject, body" onkeyup="filterTemplates()">
            </div>
            <div class="col-md-2">
                <label class="form-label small fw-semibold mb-1">Role</label>
                <select class="form-select" id="roleFilter" onchange="filterTemplates()">
                    <option value="">All Roles</option>
                    <option value="ADMIN">ADMIN</option><option value="MANAGER">MANAGER</option><option value="EMPLOYEE">EMPLOYEE</option><option value="HR">HR</option><option value="SUPERADMIN">SUPERADMIN</option>
                </select>
            </div>
            <div class="col-md-3">
                <label class="form-label small fw-semibold mb-1">Event</label>
                <select class="form-select" id="eventFilter" onchange="filterTemplates()"><option value="">All Events</option></select>
            </div>
            <div class="col-md-2">
                <label class="form-label small fw-semibold mb-1">Status</label>
                <select class="form-select" id="statusFilter" onchange="filterTemplates()">
                    <option value="">All Status</option><option value="true">Active</option><option value="false">Disabled</option>
                </select>
            </div>
            <div class="col-md-1"><button class="btn btn-outline-dark w-100" onclick="resetFilters()">Reset</button></div>
        </div>
    </div>
</div>

<div class="card hrms-card shadow-sm">
    <div class="card-body">
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th>ID</th><th>Event Code</th><th>Target Role</th><th>Subject</th><th>Status</th><th>Updated Time</th><th class="text-end">Actions</th>
                    </tr>
                </thead>
                <tbody id="templatesTableBody"></tbody>
            </table>
        </div>
        <div id="emptyState" class="text-center text-muted py-5 d-none">
            <i class="fa fa-inbox fa-2x mb-2"></i>
            <div>No templates found for current filters.</div>
        </div>
    </div>
</div>

<div class="modal fade" id="templateModal" tabindex="-1">
    <div class="modal-dialog modal-xl modal-dialog-centered modal-dialog-scrollable">
        <div class="modal-content">
            <form id="templateForm">
                <div class="modal-header"><h5 class="modal-title" id="templateModalTitle">Create Template</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
                <div class="modal-body">
                    <input type="hidden" id="templateId">
                    <div class="row g-3">
                        <div class="col-lg-8">
                            <div class="row g-3">
                                <div class="col-md-6"><label class="form-label">Event Code <span class="text-danger">*</span></label><input type="text" class="form-control" id="eventCode" required><div class="form-text">Use uppercase and underscore format (e.g., LEAVE_APPROVED)</div></div>
                                <div class="col-md-6"><label class="form-label">Target Role <span class="text-danger">*</span></label><select class="form-select" id="targetRole" required><option value="">Select Role</option><option value="ADMIN">ADMIN</option><option value="MANAGER">MANAGER</option><option value="EMPLOYEE">EMPLOYEE</option><option value="HR">HR</option><option value="SUPERADMIN">SUPERADMIN</option></select></div>
                                <div class="col-12"><label class="form-label">Subject <span class="text-danger">*</span></label><textarea class="form-control auto-resize" id="subject" rows="2" required></textarea><div class="d-flex justify-content-between"><small class="text-muted">Use Thymeleaf variables like [[${employeeName}]]</small><small class="text-muted"><span id="subjectCount">0</span> chars</small></div></div>
                                <div class="col-12"><label class="form-label">Body HTML <span class="text-danger">*</span></label><textarea class="form-control auto-resize" id="body" rows="8" required></textarea><div class="d-flex justify-content-between"><small class="text-muted">Use Thymeleaf variables: [[${employeeName}]]</small><small class="text-muted"><span id="bodyCount">0</span> chars</small></div></div>
                                <div class="col-12"><div class="form-check form-switch"><input class="form-check-input" type="checkbox" id="isActive" checked><label class="form-check-label" for="isActive">Template is active</label></div></div>
                            </div>
                        </div>
                        <div class="col-lg-4">
                            <div class="border rounded p-3 bg-light-subtle">
                                <h6 class="fw-semibold mb-2">Syntax Helper</h6>
                                <div class="small text-muted mb-2">Click to copy variable token</div>
                                <div class="d-flex flex-wrap gap-2" id="variableHelper"></div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer"><button type="button" class="btn btn-secondary-hrms" data-bs-dismiss="modal">Cancel</button><button type="submit" class="btn btn-primary-hrms"><i class="fa fa-save me-1"></i>Save</button></div>
            </form>
        </div>
    </div>
</div>

<div class="modal fade" id="previewModal" tabindex="-1"><div class="modal-dialog modal-lg modal-dialog-centered modal-dialog-scrollable"><div class="modal-content"><div class="modal-header"><h5 class="modal-title">Template Preview</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div><div class="modal-body"><div class="mb-2"><strong>Subject</strong><div class="border rounded p-2 bg-light" id="previewSubject"></div></div><div><strong>Body</strong><div class="border rounded p-3 bg-white" id="previewBody"></div></div></div><div class="modal-footer"><button type="button" class="btn btn-outline-primary" id="testSendBtn" disabled>Test Send Email (Coming Soon)</button><button type="button" class="btn btn-secondary-hrms" data-bs-dismiss="modal">Close</button></div></div></div></div>

<div class="modal fade" id="viewModal" tabindex="-1"><div class="modal-dialog modal-lg modal-dialog-centered modal-dialog-scrollable"><div class="modal-content"><div class="modal-header"><h5 class="modal-title">Template Details</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div><div class="modal-body" id="viewContent"></div></div></div></div>

<div class="card hrms-card shadow-sm mt-3">
    <div class="card-header bg-light"><h6 class="mb-0"><i class="fa fa-bug me-2"></i>Debug Panel</h6></div>
    <div class="card-body">
        <div class="small text-muted">Use this panel to inspect current list state and variables extracted from selected template.</div>
        <pre id="debugPanel" class="mt-2 p-2 bg-dark text-light rounded small" style="max-height:180px; overflow:auto;">No template selected.</pre>
    </div>
</div>
