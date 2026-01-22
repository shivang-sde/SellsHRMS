<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="leave-balances-management">
  <!-- Header -->
  <div class="d-flex justify-content-between align-items-center mb-4">
    <div>
      <h2 class="mb-1">Employee Leave Balances</h2>
      <p class="text-muted mb-0">View and manage employee leave balances across organization</p>
    </div>
    <div class="d-flex gap-2">
      <button class="btn btn-outline-secondary" onclick="window.location.href='${pageContext.request.contextPath}/org/leaves'">
        <i class="fa fa-arrow-left me-2"></i>Back to Leaves
      </button>
      <button class="btn btn-success" onclick="exportToExcel()">
        <i class="fa fa-file-excel me-2"></i>Export to Excel
      </button>
    </div>
  </div>

  <!-- Filter Section -->
  <div class="card border-0 shadow-sm mb-4">
    <div class="card-body">
      <div class="row g-3">
        <div class="col-md-3">
          <label class="form-label small">Search Employee</label>
          <input type="search" class="form-control" id="employeeSearch" placeholder="Name or Code...">
        </div>
        <div class="col-md-3">
          <label class="form-label small">Department</label>
          <select class="form-select" id="departmentFilter">
            <option value="">All Departments</option>
          </select>
        </div>
        <div class="col-md-3">
          <label class="form-label small">Leave Type</label>
          <select class="form-select" id="leaveTypeFilter">
            <option value="">All Leave Types</option>
          </select>
        </div>
        <div class="col-md-3">
          <label class="form-label small">Leave Year</label>
          <select class="form-select" id="financialYearFilter">
            <option value="">Current LY</option>
          </select>
        </div>
      </div>
    </div>
  </div>

  <!-- Summary Cards -->
  <div class="row mb-4" id="summaryCards">
    <div class="col-md-3">
      <div class="card border-0 shadow-sm">
        <div class="card-body">
          <small class="text-muted">Total Employees</small>
          <h3 class="mb-0 mt-1" id="totalEmployees">0</h3>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card border-0 shadow-sm">
        <div class="card-body">
          <small class="text-muted">Total Leaves Allocated</small>
          <h3 class="mb-0 mt-1 text-primary" id="totalAllocated">0</h3>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card border-0 shadow-sm">
        <div class="card-body">
          <small class="text-muted">Total Leaves Availed</small>
          <h3 class="mb-0 mt-1 text-danger" id="totalAvailed">0</h3>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card border-0 shadow-sm">
        <div class="card-body">
          <small class="text-muted">Total Balance Remaining</small>
          <h3 class="mb-0 mt-1 text-success" id="totalRemaining">0</h3>
        </div>
      </div>
    </div>
  </div>

  <!-- Leave Balances Table -->
  <div class="card border-0 shadow-sm">
    <div class="card-header bg-white border-bottom">
      <div class="d-flex justify-content-between align-items-center">
        <h5 class="mb-0">Leave Balance Details</h5>
        <span class="text-muted small" id="recordCount">0 records</span>
      </div>
    </div>
    <div class="card-body p-0">
      <div class="table-responsive">
        <table class="table table-hover mb-0" id="balancesTable">
          <thead class="bg-light">
            <tr>
              <th>Employee</th>
              <th>Code</th>
              <th>Department</th>
              <th>Leave Type</th>
              <th>Type</th>
              <th>Opening</th>
              <th>Accrued</th>
              <th>Availed</th>
              <th>Balance</th>
              <th>C/F</th>
              <th>Encashed</th>
              <th>FY</th>
            </tr>
          </thead>
          <tbody id="balancesTableBody">
            <tr>
              <td colspan="12" class="text-center py-5">
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

<!-- View Balance Details Modal -->
<div class="modal fade" id="balanceDetailsModal" tabindex="-1">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Leave Balance Details</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        <div class="row mb-3">
          <div class="col-md-6">
            <label class="text-muted small">Employee</label>
            <p class="fw-semibold mb-0" id="detailEmployeeName"></p>
          </div>
          <div class="col-md-6">
            <label class="text-muted small">Employee Code</label>
            <p class="mb-0" id="detailEmployeeCode"></p>
          </div>
        </div>
        <div class="row mb-3">
          <div class="col-md-6">
            <label class="text-muted small">Leave Type</label>
            <p class="mb-0" id="detailLeaveType"></p>
          </div>
          <div class="col-md-6">
            <label class="text-muted small">Financial Year</label>
            <p class="mb-0" id="detailFY"></p>
          </div>
        </div>
        <hr>
        <div class="row g-3">
          <div class="col-md-3">
            <div class="text-center p-3 bg-light rounded">
              <small class="text-muted d-block">Opening Balance</small>
              <h4 class="mb-0 mt-2" id="detailOpening">0</h4>
            </div>
          </div>
          <div class="col-md-3">
            <div class="text-center p-3 bg-info bg-opacity-10 rounded">
              <small class="text-muted d-block">Accrued</small>
              <h4 class="mb-0 mt-2 text-info" id="detailAccrued">0</h4>
            </div>
          </div>
          <div class="col-md-3">
            <div class="text-center p-3 bg-danger bg-opacity-10 rounded">
              <small class="text-muted d-block">Availed</small>
              <h4 class="mb-0 mt-2 text-danger" id="detailAvailed">0</h4>
            </div>
          </div>
          <div class="col-md-3">
            <div class="text-center p-3 bg-success bg-opacity-10 rounded">
              <small class="text-muted d-block">Balance</small>
              <h4 class="mb-0 mt-2 text-success" id="detailBalance">0</h4>
            </div>
          </div>
        </div>
        <div class="row g-3 mt-2">
          <div class="col-md-6">
            <div class="text-center p-3 bg-light rounded">
              <small class="text-muted d-block">Carried Forward</small>
              <h5 class="mb-0 mt-2" id="detailCarriedForward">0</h5>
            </div>
          </div>
          <div class="col-md-6">
            <div class="text-center p-3 bg-light rounded">
              <small class="text-muted d-block">Encashed</small>
              <h5 class="mb-0 mt-2" id="detailEncashed">0</h5>
            </div>
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
.table thead th {
  font-weight: 600;
  text-transform: uppercase;
  font-size: 11px;
  color: #6c757d;
  white-space: nowrap;
}

.table tbody tr {
  cursor: pointer;
}

.table tbody tr:hover {
  background-color: rgba(0, 123, 255, 0.05);
}

.balance-positive {
  color: #28a745;
  font-weight: 600;
}

.balance-low {
  color: #ffc107;
  font-weight: 600;
}

.balance-negative {
  color: #dc3545;
  font-weight: 600;
}
</style>