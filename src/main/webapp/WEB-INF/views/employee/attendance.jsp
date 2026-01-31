<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8" %> <%@ taglib prefix="c"
uri="http://java.sun.com/jsp/jstl/core" %>

<div class="row mb-4">
  <!-- <div class="col-12">
    <h2 class="mb-0">My Attendance</h2>
  </div> -->
   <div class="alert alert-light border-start border-4 border-primary shadow-sm mt-3">
  <i class="fas fa-info-circle text-primary me-2"></i>
  <span id="attendanceSummaryText">
    You haven't punched in yet. Your shift starts at <strong>09:30 AM</strong>.
  </span>
</div>
</div>

<c:import url="/WEB-INF/views/employee/punch-in-out-section.jsp"/>


<!-- Today's Summary -->
<div class="row mb-4">
  <div class="col-md-3">
    <div class="card text-center">
      <div class="card-body">
        <i class="fas fa-clock fa-2x text-primary mb-2"></i>
        <h6 class="text-muted">Punch In Time</h6>
        <h4 id="todayPunchIn">--:--</h4>
      </div>
    </div>
  </div>
  <div class="col-md-3">
    <div class="card text-center">
      <div class="card-body">
        <i class="fas fa-sign-out-alt fa-2x text-danger mb-2"></i>
        <h6 class="text-muted">Punch Out Time</h6>
        <h4 id="todayPunchOut">--:--</h4>
      </div>
    </div>
  </div>
  <div class="col-md-3">
    <div class="card text-center">
      <div class="card-body">
        <i class="fas fa-hourglass-half fa-2x text-success mb-2"></i>
        <h6 class="text-muted">Total Hours</h6>
        <h4 id="todayTotalHours">0h</h4>
      </div>
    </div>
  </div>
  <div class="col-md-3">
    <div class="card text-center">
      <div class="card-body">
        <i class="fas fa-calendar-check fa-2x text-info mb-2"></i>
        <h6 class="text-muted">This Month</h6>
        <h4 id="monthDaysPresent">0 days</h4>
      </div>
    </div>
  </div>
</div>

<!-- Attendance History -->
<div class="card">
  <div class="card-header d-flex justify-content-between align-items-center">
    <h5 class="mb-0">Attendance History</h5>
    <div>
      <input
        type="date"
        id="startDate"
        class="form-control form-control-sm d-inline-block"
        style="width: 150px"
      />
      <span class="mx-2">to</span>
      <input
        type="date"
        id="endDate"
        class="form-control form-control-sm d-inline-block"
        style="width: 150px"
      />
      <button id="btnFilter" class="btn btn-sm btn-primary ms-2">
        <i class="fas fa-filter"></i> Filter
      </button>
    </div>
  </div>
  <div class="card-body">
    <div class="table-responsive">
      <table class="table table-hover">
        <thead class="table-light">
          <tr>
            <th>Date</th>
            <th>Punch In</th>
            <th>Punch Out</th>
            <th>Work Hours</th>
            <th>Status</th>
            <th>Source</th>
          </tr>
        </thead>
        <tbody id="attendanceTableBody">
          <tr>
            <td colspan="6" class="text-center text-muted">
              <div class="spinner-border spinner-border-sm"></div>
              Loading...
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</div>
