<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="row mb-3">
    <div class="col-12">
        <div class="d-flex justify-content-between align-items-center">
            <div>
                <h2 class="mb-0">All Leaves</h2>
                <p class="text-muted mb-0">View and manage all leave requests</p>
            </div>
            <div>
                <a href="/org/leaves/pending" class="btn btn-warning">
                    <i class="fas fa-tasks me-2"></i>Pending Approvals
                </a>
                <a href="/org/leaves/reports" class="btn btn-primary">
                    <i class="fas fa-chart-bar me-2"></i>Reports
                </a>
            </div>
        </div>
    </div>
</div>

<!-- Statistics Cards -->
<div class="row mb-4">
    <div class="col-md-3">
        <div class="card text-center border-warning">
            <div class="card-body">
                <i class="fas fa-hourglass-half fa-2x text-warning mb-2"></i>
                <h3 id="statPending" class="mb-0">0</h3>
                <small class="text-muted">Pending</small>
            </div>
        </div>
    </div>
    <div class="col-md-3">
        <div class="card text-center border-success">
            <div class="card-body">
                <i class="fas fa-check-circle fa-2x text-success mb-2"></i>
                <h3 id="statApproved" class="mb-0">0</h3>
                <small class="text-muted">Approved</small>
            </div>
        </div>
    </div>
    <div class="col-md-3">
        <div class="card text-center border-danger">
            <div class="card-body">
                <i class="fas fa-times-circle fa-2x text-danger mb-2"></i>
                <h3 id="statRejected" class="mb-0">0</h3>
                <small class="text-muted">Rejected</small>
            </div>
        </div>
    </div>
    <div class="col-md-3">
        <div class="card text-center border-secondary">
            <div class="card-body">
                <i class="fas fa-ban fa-2x text-secondary mb-2"></i>
                <h3 id="statCanceled" class="mb-0">0</h3>
                <small class="text-muted">Canceled</small>
            </div>
        </div>
    </div>
</div>

<!-- Filters -->
<div class="card mb-3">
    <div class="card-body">
        <div class="row g-3">
            <div class="col-md-3">
                <label class="form-label">Search Employee</label>
                <input type="text" id="searchEmployee" class="form-control" placeholder="Name or code...">
            </div>
            <div class="col-md-2">
                <label class="form-label">Status</label>
                <select id="filterStatus" class="form-select">
                    <option value="">All Status</option>
                    <option value="PENDING">Pending</option>
                    <option value="APPROVE">Approved</option>
                    <option value="REJECTED">Rejected</option>
                    <option value="CANCELED">Canceled</option>
                </select>
            </div>
            <div class="col-md-2">
                <label class="form-label">Leave Type</label>
                <select id="filterLeaveType" class="form-select">
                    <option value="">All Types</option>
                </select>
            </div>
            <div class="col-md-2">
                <label class="form-label">From Date</label>
                <input type="date" id="filterStartDate" class="form-control">
            </div>
            <div class="col-md-2">
                <label class="form-label">To Date</label>
                <input type="date" id="filterEndDate" class="form-control">
            </div>
            <div class="col-md-1">
                <label class="form-label">&nbsp;</label>
                <button id="btnFilter" class="btn btn-secondary w-100">
                    <i class="fas fa-filter"></i>
                </button>
            </div>
        </div>
    </div>
</div>

<!-- Leaves Table -->
<div class="card">
    <div class="card-header d-flex justify-content-between align-items-center">
        <h5 class="mb-0">Leave Records</h5>
        <button id="btnExport" class="btn btn-success btn-sm">
            <i class="fas fa-download me-2"></i>Export Excel
        </button>
    </div>
    <div class="card-body">
        <div class="table-responsive">
            <table class="table table-hover">
                <thead class="table-light">
                    <tr>
                        <th>Employee Code</th>
                        <th>Employee Name</th>
                        <th>Leave Type</th>
                        <th>Start Date</th>
                        <th>End Date</th>
                        <th>Days</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody id="leavesTableBody">
                    <tr>
                        <td colspan="8" class="text-center">
                            <div class="spinner-border text-primary"></div>
                            <p class="mt-2 text-muted">Loading leaves...</p>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>

<!-- Leave Detail Modal -->
<div class="modal fade" id="leaveDetailModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Leave Request Details</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body" id="leaveDetailBody">
                <!-- Populated by JavaScript -->
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>