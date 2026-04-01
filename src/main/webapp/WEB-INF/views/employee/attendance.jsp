<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
  <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

    <div class="row mb-4">
      <div class="col-12">
        <h2 class="mb-0">Attendance</h2>
      </div>
      <!-- <div class="alert m-4 alert-light border-start border-4 border-primary shadow-sm ">
  <i class="fas fa-info-circle text-primary me-2"></i>
  <span id="attendanceSummaryText">
    You haven't punched in yet. Your shift starts at <strong>09:30 AM</strong>.
  </span>
</div> -->
    </div>

    <c:import url="/WEB-INF/views/employee/punch-in-out-section.jsp" />


    <!-- Attendance History -->
    <div class="card">
      <div class="card-header d-flex justify-content-between align-items-center">
        <h5 class="mb-0">Attendance History</h5>
        <div>
          <input type="date" id="startDate" class="form-control form-control-sm d-inline-block" style="width: 150px" />
          <span class="mx-2">to</span>
          <input type="date" id="endDate" class="form-control form-control-sm d-inline-block" style="width: 150px" />
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
                <th>Status</thp>
                <th>Remarks</th>

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