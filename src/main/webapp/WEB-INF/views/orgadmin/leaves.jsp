<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="org-leave-management">
  <!-- Header -->
  <div class="d-flex justify-content-between align-items-center mb-4">
    <div>
      <h2 class="mb-1">Leave Management</h2>
      <p class="text-muted mb-0">Manage employee leave requests and configurations</p>
    </div>
    <div class="d-flex gap-2">
      <button class="btn btn-outline-primary" onclick="window.location.href='${pageContext.request.contextPath}/org/leave-types'">
        <i class="fa fa-cog me-2"></i>Leave Types
      </button>
      <button class="btn btn-outline-secondary" onclick="window.location.href='${pageContext.request.contextPath}/org/leave-balances'">
        <i class="fa fa-wallet me-2"></i>Leave Balances
      </button>
    </div>
  </div>

  <!-- Tabs -->
  <ul class="nav nav-tabs mb-4" id="leaveTabs" role="tablist">
    <li class="nav-item" role="presentation">
      <button class="nav-link active" id="pending-tab" data-bs-toggle="tab" data-bs-target="#pending" type="button">
        <i class="fa fa-clock me-2"></i>Pending <span class="badge bg-warning text-dark ms-2" id="pendingBadge">0</span>
      </button>
    </li>
    <li class="nav-item" role="presentation">
      <button class="nav-link" id="approved-tab" data-bs-toggle="tab" data-bs-target="#approved" type="button">
        <i class="fa fa-check-circle me-2"></i>Approved
      </button>
    </li>
    <li class="nav-item" role="presentation">
      <button class="nav-link" id="rejected-tab" data-bs-toggle="tab" data-bs-target="#rejected" type="button">
        <i class="fa fa-times-circle me-2"></i>Rejected
      </button>
    </li>
    <li class="nav-item" role="presentation">
      <button class="nav-link" id="all-tab" data-bs-toggle="tab" data-bs-target="#all" type="button">
        <i class="fa fa-list me-2"></i>All Leaves
      </button>
    </li>
  </ul>

  <!-- Tab Content -->
  <div class="tab-content" id="leaveTabContent">
    <!-- Pending Leaves -->
    <div class="tab-pane fade show active" id="pending" role="tabpanel">
      <div class="card border-0 shadow-sm">
        <div class="card-header bg-white border-bottom">
          <div class="d-flex justify-content-between align-items-center">
            <h5 class="mb-0">Pending Approvals</h5>
            <input type="search" class="form-control form-control-sm" style="width: 250px;" placeholder="Search employee..." id="pendingSearch">
          </div>
        </div>
        <div class="card-body">
          <div class="table-responsive">
            <table class="table table-hover">
              <thead>
                <tr>
                  <th>Employee</th>
                  <th>Leave Type</th>
                  <th>Duration</th>
                  <th>Days</th>
                  <th>Applied On</th>
                  <th>Reason</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody id="pendingTableBody">
                <tr>
                  <td colspan="7" class="text-center py-4">
                    <div class="spinner-border text-primary" role="status">
                      <span class="visually-hidden">Loading...</span>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>

    <!-- Approved Leaves -->
    <div class="tab-pane fade" id="approved" role="tabpanel">
      <div class="card border-0 shadow-sm">
        <div class="card-header bg-white border-bottom">
          <div class="d-flex justify-content-between align-items-center">
            <h5 class="mb-0">Approved Leaves</h5>
            <div class="d-flex gap-2">
              <input type="search" class="form-control form-control-sm" style="width: 200px;" placeholder="Search..." id="approvedSearch">
              <input type="date" class="form-control form-control-sm" style="width: 150px;" id="approvedDateFilter">
            </div>
          </div>
        </div>
        <div class="card-body">
          <div class="table-responsive">
            <table class="table table-hover">
              <thead>
                <tr>
                  <th>Employee</th>
                  <th>Leave Type</th>
                  <th>Duration</th>
                  <th>Days</th>
                  <th>Approved By</th>
                  <th>Approved On</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody id="approvedTableBody">
                <tr>
                  <td colspan="7" class="text-center py-4">
                    <div class="spinner-border text-primary" role="status">
                      <span class="visually-hidden">Loading...</span>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>

    <!-- Rejected Leaves -->
    <div class="tab-pane fade" id="rejected" role="tabpanel">
      <div class="card border-0 shadow-sm">
        <div class="card-header bg-white border-bottom">
          <h5 class="mb-0">Rejected Leaves</h5>
        </div>
        <div class="card-body">
          <div class="table-responsive">
            <table class="table table-hover">
              <thead>
                <tr>
                  <th>Employee</th>
                  <th>Leave Type</th>
                  <th>Duration</th>
                  <th>Days</th>
                  <th>Rejected By</th>
                  <th>Rejection Reason</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody id="rejectedTableBody">
                <tr>
                  <td colspan="7" class="text-center py-4">
                    <div class="spinner-border text-primary" role="status">
                      <span class="visually-hidden">Loading...</span>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>

    <!-- All Leaves -->
    <div class="tab-pane fade" id="all" role="tabpanel">
      <div class="card border-0 shadow-sm">
        <div class="card-header bg-white border-bottom">
          <div class="d-flex justify-content-between align-items-center">
            <h5 class="mb-0">All Leave Applications</h5>
            <div class="d-flex gap-2">
              <select class="form-select form-select-sm" style="width: 150px;" id="allStatusFilter">
                <option value="">All Status</option>
                <option value="PENDING">Pending</option>
                <option value="APPROVE">Approved</option>
                <option value="REJECTED">Rejected</option>
                <option value="CANCELED">Cancelled</option>
              </select>
              <input type="search" class="form-control form-control-sm" style="width: 200px;" placeholder="Search employee..." id="allSearch">
            </div>
          </div>
        </div>
        <div class="card-body">
          <div class="table-responsive">
            <table class="table table-hover">
              <thead>
                <tr>
                  <th>Employee</th>
                  <th>Leave Type</th>
                  <th>Duration</th>
                  <th>Days</th>
                  <th>Status</th>
                  <th>Applied On</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody id="allTableBody">
                <tr>
                  <td colspan="7" class="text-center py-4">
                    <div class="spinner-border text-primary" role="status">
                      <span class="visually-hidden">Loading...</span>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<!-- Approve Leave Modal -->
<div class="modal fade" id="approveModal" tabindex="-1">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header bg-success text-white">
        <h5 class="modal-title">Approve Leave</h5>
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
      </div>
      <form id="approveForm">
        <input type="hidden" id="approveLeaveId">
        <div class="modal-body">
          <div class="mb-3">
            <label class="form-label">Employee</label>
            <input type="text" class="form-control" id="approveEmployeeName" readonly>
          </div>
          <div class="row mb-3">
            <div class="col-6">
              <label class="form-label">Leave Type</label>
              <input type="text" class="form-control" id="approveLeaveType" readonly>
            </div>
            <div class="col-6">
              <label class="form-label">Duration</label>
              <input type="text" class="form-control" id="approveDuration" readonly>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-label">Employee Reason</label>
            <textarea class="form-control" id="approveEmployeeReason" rows="2" readonly></textarea>
          </div>
          <div class="mb-3">
            <label class="form-label">Remarks (Optional)</label>
            <textarea class="form-control" name="remarks" rows="3" placeholder="Add any approval remarks..."></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
          <button type="submit" class="btn btn-success">
            <i class="fa fa-check me-2"></i>Approve Leave
          </button>
        </div>
      </form>
    </div>
  </div>
</div>

<!-- Reject Leave Modal -->
<div class="modal fade" id="rejectModal" tabindex="-1">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header bg-danger text-white">
        <h5 class="modal-title">Reject Leave</h5>
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
      </div>
      <form id="rejectForm">
        <input type="hidden" id="rejectLeaveId">
        <div class="modal-body">
          <div class="mb-3">
            <label class="form-label">Employee</label>
            <input type="text" class="form-control" id="rejectEmployeeName" readonly>
          </div>
          <div class="row mb-3">
            <div class="col-6">
              <label class="form-label">Leave Type</label>
              <input type="text" class="form-control" id="rejectLeaveType" readonly>
            </div>
            <div class="col-6">
              <label class="form-label">Duration</label>
              <input type="text" class="form-control" id="rejectDuration" readonly>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-label">Employee Reason</label>
            <textarea class="form-control" id="rejectEmployeeReason" rows="2" readonly></textarea>
          </div>
          <div class="mb-3">
            <label class="form-label">Rejection Reason <span class="text-danger">*</span></label>
            <textarea class="form-control" name="remarks" rows="3" placeholder="Explain why this leave is being rejected..." required></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
          <button type="submit" class="btn btn-danger">
            <i class="fa fa-times me-2"></i>Reject Leave
          </button>
        </div>
      </form>
    </div>
  </div>
</div>

<!-- View Details Modal -->
<div class="modal fade" id="viewDetailsModal" tabindex="-1">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Leave Application Details</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        <div class="row mb-3">
          <div class="col-md-6">
            <label class="text-muted small">Employee Name</label>
            <p class="fw-semibold mb-0" id="detailEmployeeName"></p>
          </div>
          <div class="col-md-6">
            <label class="text-muted small">Status</label>
            <p id="detailStatus"></p>
          </div>
        </div>
        <div class="row mb-3">
          <div class="col-md-6">
            <label class="text-muted small">Leave Type</label>
            <p class="mb-0" id="detailLeaveType"></p>
          </div>
          <div class="col-md-6">
            <label class="text-muted small">Total Days</label>
            <p class="mb-0 fw-semibold" id="detailLeaveDays"></p>
          </div>
        </div>
        <div class="row mb-3">
          <div class="col-md-6">
            <label class="text-muted small">Start Date</label>
            <p class="mb-0" id="detailStartDate"></p>
          </div>
          <div class="col-md-6">
            <label class="text-muted small">End Date</label>
            <p class="mb-0" id="detailEndDate"></p>
          </div>
        </div>
        <div class="mb-3">
          <label class="text-muted small">Reason</label>
          <p class="mb-0" id="detailReason"></p>
        </div>
        <div class="mb-3">
          <label class="text-muted small">Applied On</label>
          <p class="mb-0" id="detailAppliedOn"></p>
        </div>
        <div id="approvalDetailsSection" style="display: none;">
          <hr>
          <h6 class="mb-3">Approval Details</h6>
          <div class="row mb-3">
            <div class="col-md-6">
              <label class="text-muted small">Actioned By</label>
              <p class="mb-0" id="detailApproverName"></p>
            </div>
            <div class="col-md-6">
              <label class="text-muted small">Actioned On</label>
              <p class="mb-0" id="detailApprovedOn"></p>
            </div>
          </div>
          <div class="mb-3">
            <label class="text-muted small">Remarks</label>
            <p class="mb-0" id="detailApproverRemarks"></p>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>

<style>
.nav-tabs .nav-link {
  color: #6c757d;
  border: none;
  border-bottom: 2px solid transparent;
}

.nav-tabs .nav-link:hover {
  border-color: transparent;
  border-bottom-color: #dee2e6;
}

.nav-tabs .nav-link.active {
  color: #0d6efd;
  border-color: transparent;
  border-bottom-color: #0d6efd;
  background: transparent;
}

.table th {
  font-weight: 600;
  text-transform: uppercase;
  font-size: 12px;
  color: #6c757d;
  border-bottom: 2px solid #dee2e6;
}

.action-btn {
  padding: 4px 8px;
  font-size: 12px;
}
</style>