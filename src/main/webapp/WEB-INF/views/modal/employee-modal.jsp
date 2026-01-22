<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container-fluid">

  <!-- HEADER SECTION -->
  <div class="d-flex justify-content-between align-items-center mb-4">
    <div>
      <h3 class="fw-bold text-primary mb-1">Welcome, ${sessionScope.USER_NAME} 👋</h3>
      <p class="text-muted mb-0">Here’s an overview of your work activity.</p>
    </div>
    <div class="text-end">
      <div id="currentDate" class="fw-semibold"></div>
      <div id="currentTime" class="fs-6 text-muted"></div>
    </div>
  </div>

  <!-- TODAY’S ATTENDANCE SECTION -->
  <div class="row g-3 mb-4">
    <div class="col-lg-4 col-md-6">
      <div class="card shadow-sm border-0 p-3 text-center">
        <h6 class="fw-semibold mb-2"><i class="fa fa-clock text-primary me-2"></i>Today’s Attendance</h6>
        <div id="punchStatus" class="mb-3">
          <span class="badge bg-secondary fs-6">Loading...</span>
        </div>

        <div class="mb-3">
          <button id="btnPunchIn" class="btn btn-success rounded-pill px-4 py-2 fw-semibold">
            <i class="fa fa-fingerprint me-2"></i> Punch In
          </button>
          <button id="btnPunchOut" class="btn btn-danger rounded-pill px-4 py-2 fw-semibold" style="display:none;">
            <i class="fa fa-sign-out-alt me-2"></i> Punch Out
          </button>
        </div>

        <div id="workingHours" class="mt-3" style="display:none;">
          <div class="timer-pill mb-2" id="hoursWorked">0h 0m</div>
        </div>

        <div class="small text-muted">
          <strong>In:</strong> <span id="todayPunchIn">--:--</span> |
          <strong>Out:</strong> <span id="todayPunchOut">--:--</span>
        </div>
        <div class="small text-muted mt-1">
          <strong>Total:</strong> <span id="todayTotalHours">0h</span>
        </div>
      </div>
    </div>

    <!-- LEAVE BALANCE -->
    <div class="col-lg-8 col-md-6">
      <div class="card shadow-sm border-0 p-3">
        <h6 class="fw-semibold mb-3">
          <i class="fa fa-calendar-days text-primary me-2"></i>Leave Balances
        </h6>
        <div class="row" id="leaveBalanceCards">
          <div class="col-12 text-center text-muted">Loading...</div>
        </div>
      </div>
    </div>
  </div>

  <!-- ATTENDANCE HISTORY TABLE -->
  <div class="card shadow-sm border-0 mb-4">
    <div class="card-body">
      <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap">
        <h6 class="fw-semibold mb-0"><i class="fa fa-calendar-week text-primary me-2"></i>Attendance History</h6>

        <div class="d-flex align-items-center gap-2 flex-wrap">
          <input type="date" id="startDate" class="form-control form-control-sm">
          <input type="date" id="endDate" class="form-control form-control-sm">
          <button id="btnFilter" class="btn btn-primary btn-sm">
            <i class="fa fa-filter me-1"></i>Filter
          </button>
        </div>
      </div>

      <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
          <thead class="table-light">
            <tr>
              <th>Date</th>
              <th>Punch In</th>
              <th>Punch Out</th>
              <th>Hours</th>
              <th>Status</th>
              <th>Source</th>
            </tr>
          </thead>
          <tbody id="attendanceTableBody">
            <tr>
              <td colspan="6" class="text-center text-muted">
                Loading attendance...
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="mt-3 text-end small text-muted">
        <strong>Days Present:</strong> <span id="monthDaysPresent">0 days</span>
      </div>
    </div>
  </div>

  <!-- LEAVE REQUEST HISTORY -->
  <div class="card shadow-sm border-0 mb-4">
    <div class="card-body">
      <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap">
        <h6 class="fw-semibold mb-0"><i class="fa fa-plane-departure text-primary me-2"></i>My Leave Requests</h6>

        <div class="d-flex align-items-center gap-2 flex-wrap">
          <select id="filterStatus" class="form-select form-select-sm">
            <option value="">All</option>
            <option value="PENDING">Pending</option>
            <option value="APPROVE">Approved</option>
            <option value="REJECTED">Rejected</option>
          </select>
          <input type="date" id="filterStartDate" class="form-control form-control-sm">
          <input type="date" id="filterEndDate" class="form-control form-control-sm">
          <button id="btnFilterLeaves" class="btn btn-primary btn-sm">
            <i class="fa fa-filter me-1"></i>Filter
          </button>
        </div>
      </div>

      <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
          <thead class="table-light">
            <tr>
              <th>Type</th>
              <th>Start</th>
              <th>End</th>
              <th>Days</th>
              <th>Reason</th>
              <th>Status</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody id="leaveTableBody">
            <tr>
              <td colspan="7" class="text-center text-muted">
                Loading leaves...
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>

  <!-- LEAVE DETAIL MODAL -->
  <div class="modal fade" id="leaveDetailModal" tabindex="-1" aria-labelledby="leaveDetailModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title fw-semibold">Leave Details</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body" id="leaveDetailBody">
          <div class="text-center text-muted">
            Loading details...
          </div>
        </div>
      </div>
    </div>
  </div>

</div>

<!-- JS -->
<script src="/js/employee/employee-dashboard.js"></script>
