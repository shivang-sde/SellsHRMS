<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="row mb-3">
    <div class="col-12">
        <div class="d-flex justify-content-between align-items-center">
            <h2 class="mb-0">Attendance Reports</h2>
            <a href="/org/attendance" class="btn btn-outline-secondary">
                <i class="fas fa-arrow-left me-2"></i>Back to Today's Attendance
            </a>
        </div>
    </div>
</div>

<!-- Date Range Selector -->
<div class="card mb-4">
    <div class="card-header">
        <h5 class="mb-0">Select Date Range</h5>
    </div>
    <div class="card-body">
        <div class="row g-3">
            <div class="col-md-3">
                <label class="form-label">From Date</label>
                <input type="date" id="startDate" class="form-control">
            </div>
            <div class="col-md-3">
                <label class="form-label">To Date</label>
                <input type="date" id="endDate" class="form-control">
            </div>
            <div class="col-md-3">
                <label class="form-label">Select Employee (Optional)</label>
                <select id="filterEmployee" class="form-select">
                    <option value="">All Employees</option>
                </select>
            </div>
            <div class="col-md-3">
                <label class="form-label">&nbsp;</label>
                <button id="btnGenerateReport" class="btn btn-primary w-100">
                    <i class="fas fa-chart-line me-2"></i>Generate Report
                </button>
            </div>
        </div>
    </div>
</div>

<!-- Summary Statistics -->
<div class="row mb-4" id="statsSection" style="display:none;">
    <div class="col-md-3">
        <div class="card bg-success text-white">
            <div class="card-body">
                <h6>Total Present Days</h6>
                <h3 id="statPresent">0</h3>
            </div>
        </div>
    </div>
    <div class="col-md-3">
        <div class="card bg-danger text-white">
            <div class="card-body">
                <h6>Total Absent Days</h6>
                <h3 id="statAbsent">0</h3>
            </div>
        </div>
    </div>
    <div class="col-md-3">
        <div class="card bg-warning text-white">
            <div class="card-body">
                <h6>Total Leave Days</h6>
                <h3 id="statLeaves">0</h3>
            </div>
        </div>
    </div>
    <div class="col-md-3">
        <div class="card bg-info text-white">
            <div class="card-body">
                <h6>Total Work Hours</h6>
                <h3 id="statHours">0h</h3>
            </div>
        </div>
    </div>
</div>

<!-- Detailed Report Table -->
<div class="card" id="reportSection" style="display:none;">
    <div class="card-header d-flex justify-content-between align-items-center">
        <h5 class="mb-0">Attendance Details</h5>
        <button id="btnExportReport" class="btn btn-success btn-sm">
            <i class="fas fa-download me-2"></i>Export Excel
        </button>
    </div>
    <div class="card-body">
        <div class="table-responsive">
            <table class="table table-striped table-hover">
                <thead class="table-light">
                    <tr>
                        <th>Date</th>
                        <th>Employee</th>
                        <th>Department</th>
                        <th>Punch In</th>
                        <th>Punch Out</th>
                        <th>Work Hours</th>
                        <th>Status</th>
                        <th>Late/Early</th>
                    </tr>
                </thead>
                <tbody id="reportTableBody">
                    <!-- Populated by JS -->
                </tbody>
            </table>
        </div>
    </div>
</div>

<!-- Empty State -->
<div class="card" id="emptyState">
    <div class="card-body text-center py-5">
        <i class="fas fa-chart-bar fa-4x text-muted mb-3"></i>
        <h5>Select Date Range to Generate Report</h5>
        <p class="text-muted">Choose the date range and optionally select an employee to view detailed attendance reports.</p>
    </div>
</div>