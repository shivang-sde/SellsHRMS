<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="row mb-3">
    <div class="col-12">
        <div class="d-flex justify-content-between align-items-center">
            <div>
                <h2 class="mb-0">Today's Attendance</h2>
                <p class="text-muted mb-0" id="currentDate">Loading...</p>
            </div>
            <div>
                <button class="btn btn-outline-primary" onclick="location.href='/org/attendance/reports'">
                    <i class="fas fa-chart-bar me-2"></i>View Reports
                </button>
                <button class="btn btn-primary" id="btnRefresh">
                    <i class="fas fa-sync-alt me-2"></i>Refresh
                </button>
            </div>
        </div>
    </div>
</div>

<!-- Summary Cards -->
<div class="row mb-4">
    <div class="col-md-3">
        <div class="card text-center border-success">
            <div class="card-body">
                <i class="fas fa-check-circle fa-2x text-success mb-2"></i>
                <h3 id="countPresent" class="mb-0">0</h3>
                <small class="text-muted">Present</small>
            </div>
        </div>
    </div>
    <div class="col-md-3">
        <div class="card text-center border-danger">
            <div class="card-body">
                <i class="fas fa-times-circle fa-2x text-danger mb-2"></i>
                <h3 id="countAbsent" class="mb-0">0</h3>
                <small class="text-muted">Absent</small>
            </div>
        </div>
    </div>
    <div class="col-md-3">
        <div class="card text-center border-warning">
            <div class="card-body">
                <i class="fas fa-calendar-times fa-2x text-warning mb-2"></i>
                <h3 id="countOnLeave" class="mb-0">0</h3>
                <small class="text-muted">On Leave</small>
            </div>
        </div>
    </div>
    <div class="col-md-3">
        <div class="card text-center border-info">
            <div class="card-body">
                <i class="fas fa-hourglass-half fa-2x text-info mb-2"></i>
                <h3 id="countPending" class="mb-0">0</h3>
                <small class="text-muted">Not Punched In</small>
            </div>
        </div>
    </div>
</div>

<!-- Filter Section -->
<div class="card mb-3">
    <div class="card-body">
        <div class="row g-3">
            <div class="col-md-3">
                <input type="text" id="searchEmployee" class="form-control" placeholder="Search by name or code...">
            </div>
            <div class="col-md-2">
                <select id="filterStatus" class="form-select">
                    <option value="">All Status</option>
                    <option value="PRESENT">Present</option>
                    <option value="ABSENT">Absent</option>
                    <option value="ON_LEAVE">On Leave</option>
                    <option value="HALF_DAY">Half Day</option>
                    <option value="HOLIDAY">Holiday</option>
                </select>
            </div>
            <div class="col-md-2">
                <select id="filterDepartment" class="form-select">
                    <option value="">All Departments</option>
                </select>
            </div>
            <div class="col-md-2">
                <button id="btnFilter" class="btn btn-secondary w-100">
                    <i class="fas fa-filter me-2"></i>Filter
                </button>
            </div>
            <div class="col-md-3 text-end">
                <button id="btnExport" class="btn btn-success">
                    <i class="fas fa-file-excel me-2"></i>Export to Excel
                </button>
            </div>
        </div>
    </div>
</div>

<!-- Attendance Table -->
<div class="card">
    <div class="card-body">
        <div class="table-responsive">
            <table class="table table-hover">
                <thead class="table-light">
                    <tr>
                        <th>Employee Code</th>
                        <th>Name</th>
                        <th>Department</th>
                        <th>Punch In</th>
                        <th>Punch Out</th>
                        <th>Work Hours</th>
                        <th>Status</th>
                        <th>Remarks</th>
                    </tr>
                </thead>
                <tbody id="attendanceTableBody">
                    <tr>
                        <td colspan="8" class="text-center">
                            <div class="spinner-border text-primary"></div>
                            <p class="mt-2 text-muted">Loading attendance...</p>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>