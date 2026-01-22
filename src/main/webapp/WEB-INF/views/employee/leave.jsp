<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="leave-management-container">
  <!-- Header Section -->
  <div class="d-flex justify-content-between align-items-center mb-4">
    <div>
      <h2 class="mb-1">My Leaves</h2>
      <p class="text-muted mb-0">Manage your leave applications and view balances</p>
    </div>
    <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#applyLeaveModal">
      <i class="fa fa-plus me-2"></i>Apply Leave
    </button>
  </div>

  <!-- Leave Balance Cards -->
  <div class="row mb-4" id="leaveBalanceCards">
    <!-- Dynamic cards will be inserted here -->
  </div>

  <!-- Leave Statistics -->
  <div class="row mb-4">
    <div class="col-md-3">
      <div class="card border-0 shadow-sm">
        <div class="card-body">
          <div class="d-flex align-items-center">
            <div class="flex-shrink-0">
              <div class="stat-icon bg-warning bg-opacity-10 text-warning">
                <i class="fa fa-clock"></i>
              </div>
            </div>
            <div class="flex-grow-1 ms-3">
              <h6 class="mb-0 text-muted">Pending</h6>
              <h3 class="mb-0" id="pendingCount">0</h3>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card border-0 shadow-sm">
        <div class="card-body">
          <div class="d-flex align-items-center">
            <div class="flex-shrink-0">
              <div class="stat-icon bg-success bg-opacity-10 text-success">
                <i class="fa fa-check-circle"></i>
              </div>
            </div>
            <div class="flex-grow-1 ms-3">
              <h6 class="mb-0 text-muted">Approved</h6>
              <h3 class="mb-0" id="approvedCount">0</h3>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card border-0 shadow-sm">
        <div class="card-body">
          <div class="d-flex align-items-center">
            <div class="flex-shrink-0">
              <div class="stat-icon bg-danger bg-opacity-10 text-danger">
                <i class="fa fa-times-circle"></i>
              </div>
            </div>
            <div class="flex-grow-1 ms-3">
              <h6 class="mb-0 text-muted">Rejected</h6>
              <h3 class="mb-0" id="rejectedCount">0</h3>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card border-0 shadow-sm">
        <div class="card-body">
          <div class="d-flex align-items-center">
            <div class="flex-shrink-0">
              <div class="stat-icon bg-secondary bg-opacity-10 text-secondary">
                <i class="fa fa-ban"></i>
              </div>
            </div>
            <div class="flex-grow-1 ms-3">
              <h6 class="mb-0 text-muted">Cancelled</h6>
              <h3 class="mb-0" id="cancelledCount">0</h3>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- Leave Applications Table -->
  <div class="card border-0 shadow-sm">
    <div class="card-header bg-white border-bottom">
      <div class="d-flex justify-content-between align-items-center">
        <h5 class="mb-0">My Leave Applications</h5>
        <div class="d-flex gap-2">
          <select class="form-select form-select-sm" id="statusFilter" style="width: auto;">
            <option value="">All Status</option>
            <option value="PENDING">Pending</option>
            <option value="APPROVE">Approved</option>
            <option value="REJECTED">Rejected</option>
            <option value="CANCELED">Cancelled</option>
          </select>
        </div>
      </div>
    </div>
    <div class="card-body">
      <div class="table-responsive">
        <table class="table table-hover" id="leavesTable">
          <thead>
            <tr>
              <th>Leave Type</th>
              <th>Duration</th>
              <th>Days</th>
              <th>Applied On</th>
              <th>Status</th>
              <th>Approver</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody id="leavesTableBody">
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

<!-- Apply Leave Modal -->
<div class="modal fade" id="applyLeaveModal" tabindex="-1">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Apply for Leave</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <form id="applyLeaveForm">
        <div class="modal-body">
          <div class="mb-3">
            <label class="form-label">Leave Type <span class="text-danger">*</span></label>
            <select class="form-select" id="leaveTypeSelect" name="leaveTypeId" required>
              <option value="">Select Leave Type</option>
            </select>
            <small class="text-muted" id="leaveTypeInfo"></small>
          </div>
          
          <div class="row">
            <div class="col-md-6 mb-3">
              <label class="form-label">Start Date <span class="text-danger">*</span></label>
              <input type="date" class="form-control" name="startDate" id="startDate" required>
            </div>
            <div class="col-md-6 mb-3">
              <label class="form-label">Start Day Type</label>
              <select class="form-select" name="startDayBreakdown" id="startDayBreakdown">
                <option value="FULL_DAY">Full Day</option>
                <option value="FIRST_HALF">First Half</option>
                <option value="SECOND_HALF">Second Half</option>
              </select>
            </div>
          </div>

          <div class="row">
            <div class="col-md-6 mb-3">
              <label class="form-label">End Date <span class="text-danger">*</span></label>
              <input type="date" class="form-control" name="endDate" id="endDate" required>
            </div>
            <div class="col-md-6 mb-3">
              <label class="form-label">End Day Type</label>
              <select class="form-select" name="endDayBreakdown" id="endDayBreakdown">
                <option value="FULL_DAY">Full Day</option>
                <option value="FIRST_HALF">First Half</option>
                <option value="SECOND_HALF">Second Half</option>
              </select>
            </div>
          </div>

          <div class="mb-3">
            <label class="form-label">Total Days</label>
            <input type="text" class="form-control" id="calculatedDays" readonly>
          </div>

          <div class="mb-3">
            <label class="form-label">Reason <span class="text-danger">*</span></label>
            <textarea class="form-control" name="reason" rows="3" required></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
          <button type="submit" class="btn btn-primary">Submit Application</button>
        </div>
      </form>
    </div>
  </div>
</div>

<!-- Edit Leave Modal -->
<div class="modal fade" id="editLeaveModal" tabindex="-1">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Edit Leave Application</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <form id="editLeaveForm">
        <input type="hidden" id="editLeaveId">
        <div class="modal-body">
          <div class="mb-3">
            <label class="form-label">Leave Type</label>
            <input type="text" class="form-control" id="editLeaveType" readonly>
          </div>
          
          <div class="row">
            <div class="col-md-6 mb-3">
              <label class="form-label">Start Date</label>
              <input type="date" class="form-control" name="startDate" id="editStartDate" required>
            </div>
            <div class="col-md-6 mb-3">
              <label class="form-label">Start Day Type</label>
              <select class="form-select" name="startDayBreakdown" id="editStartDayBreakdown">
                <option value="FULL_DAY">Full Day</option>
                <option value="FIRST_HALF">First Half</option>
                <option value="SECOND_HALF">Second Half</option>
              </select>
            </div>
          </div>

          <div class="row">
            <div class="col-md-6 mb-3">
              <label class="form-label">End Date</label>
              <input type="date" class="form-control" name="endDate" id="editEndDate" required>
            </div>
            <div class="col-md-6 mb-3">
              <label class="form-label">End Day Type</label>
              <select class="form-select" name="endDayBreakdown" id="editEndDayBreakdown">
                <option value="FULL_DAY">Full Day</option>
                <option value="FIRST_HALF">First Half</option>
                <option value="SECOND_HALF">Second Half</option>
              </select>
            </div>
          </div>

          <div class="mb-3">
            <label class="form-label">Reason</label>
            <textarea class="form-control" name="reason" id="editReason" rows="3" required></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
          <button type="submit" class="btn btn-primary">Update Application</button>
        </div>
      </form>
    </div>
  </div>
</div>

<!-- View Leave Details Modal -->
<div class="modal fade" id="viewLeaveModal" tabindex="-1">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Leave Details</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        <div class="row mb-3">
          <div class="col-6">
            <small class="text-muted">Leave Type</small>
            <p class="mb-0 fw-semibold" id="viewLeaveType"></p>
          </div>
          <div class="col-6">
            <small class="text-muted">Status</small>
            <p class="mb-0" id="viewStatus"></p>
          </div>
        </div>
        <div class="row mb-3">
          <div class="col-6">
            <small class="text-muted">Start Date</small>
            <p class="mb-0" id="viewStartDate"></p>
          </div>
          <div class="col-6">
            <small class="text-muted">End Date</small>
            <p class="mb-0" id="viewEndDate"></p>
          </div>
        </div>
        <div class="row mb-3">
          <div class="col-6">
            <small class="text-muted">Total Days</small>
            <p class="mb-0" id="viewLeaveDays"></p>
          </div>
          <div class="col-6">
            <small class="text-muted">Applied On</small>
            <p class="mb-0" id="viewAppliedOn"></p>
          </div>
        </div>
        <div class="mb-3">
          <small class="text-muted">Reason</small>
          <p class="mb-0" id="viewReason"></p>
        </div>
        <div id="approvalSection" style="display: none;">
          <hr>
          <div class="row mb-3">
            <div class="col-6">
              <small class="text-muted">Approved By</small>
              <p class="mb-0" id="viewApproverName"></p>
            </div>
            <div class="col-6">
              <small class="text-muted">Approved On</small>
              <p class="mb-0" id="viewApprovedOn"></p>
            </div>
          </div>
          <div class="mb-3">
            <small class="text-muted">Approver Remarks</small>
            <p class="mb-0" id="viewApproverRemarks"></p>
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
.stat-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  font-size: 20px;
}

.leave-balance-card {
  transition: transform 0.2s;
}

.leave-balance-card:hover {
  transform: translateY(-2px);
}

.badge-status {
  padding: 6px 12px;
  border-radius: 6px;
  font-weight: 500;
  font-size: 12px;
}

.table th {
  font-weight: 600;
  text-transform: uppercase;
  font-size: 12px;
  color: #6c757d;
}

.action-btn {
  padding: 4px 8px;
  font-size: 12px;
  border-radius: 4px;
}

.modal {
   
    margin-bottom: 10vh;
    z-index: 1100;
}

.leave-balance-card {
  cursor: pointer;
  transition: all 0.2s ease-in-out;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
}
.leave-balance-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
}

</style>