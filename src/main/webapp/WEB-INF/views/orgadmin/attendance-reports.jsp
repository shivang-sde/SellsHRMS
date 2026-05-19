<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <div class="row mb-3">
            <div class="col-12">
                <div class="d-flex justify-content-between align-items-center">
                    <h2 class="mb-0">Attendance Reports</h2>
                    <a href="#" onclick="history.back();" class="btn btn-outline-secondary">
                        <i class="fas fa-arrow-left me-2"></i>Back
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
                        <h6>Total Present</h6>
                        <h3 id="statPresent">0</h3>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card bg-danger text-white">
                    <div class="card-body">
                        <h6>Total Absent</h6>
                        <h3 id="statAbsent">0</h3>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card bg-warning text-white">
                    <div class="card-body">
                        <h6>Total Leaves</h6>
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
                                <th>Remarks</th>
                                <th>Actions</th>
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
                <p class="text-muted">Choose the date range and optionally select an employee to view detailed
                    attendance reports.</p>
            </div>
        </div>


        <!-- Edit Attendance Modal -->
        <div class="modal fade" id="editAttendanceModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header bg-primary text-white">
                        <h5 class="modal-title">Edit Attendance Record</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"
                            aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form id="editAttendanceForm">
                            <input type="hidden" id="editAttendanceId">

                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label class="form-label">Employee</label>
                                    <input type="text" id="editEmployeeName" class="form-control" readonly>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label">Date</label>
                                    <input type="date" id="editAttendanceDate" class="form-control" readonly>
                                </div>
                            </div>

                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label class="form-label">Punch In</label>
                                    <input type="time" id="editPunchIn" class="form-control">
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label">Punch Out</label>
                                    <input type="time" id="editPunchOut" class="form-control">
                                </div>
                            </div>

                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label class="form-label">Status</label>
                                    <select id="editStatus" class="form-select">
                                        <option value="PRESENT">Present</option>
                                        <option value="ABSENT">Absent</option>
                                        <option value="ON_LEAVE">On Leave</option>
                                        <option value="HALF_DAY">Half Day</option>
                                        <option value="SHORT_DAY">Short Day</option>
                                        <option value="HOLIDAY">Holiday</option>
                                        <option value="WEEK_OFF">Week Off</option>
                                        <option value="WFH">WFH</option>
                                    </select>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label">Late/Early</label>
                                    <input type="text" id="editLateEarly" class="form-control" readonly>
                                </div>
                            </div>

                            <!-- Attendance -> Leave Settlement Fields -->
                            <div class="row mb-3 d-none" id="leaveFieldsContainer">
                                <div class="col-md-6">
                                    <label class="form-label">Leave Type <span class="text-danger">*</span></label>
                                    <select id="editLeaveType" class="form-select">
                                        <option value="">Select Leave Type</option>
                                    </select>
                                    <small id="leaveBalanceInfo" class="text-info d-none"></small>
                                </div>
                                <div class="col-md-6 d-none" id="halfDayTypeContainer">
                                    <label class="form-label">Half Day Type <span class="text-danger">*</span></label>
                                    <select id="editHalfDayType" class="form-select">
                                        <option value="FIRST_HALF">First Half</option>
                                        <option value="SECOND_HALF">Second Half</option>
                                    </select>
                                </div>
                                <div class="col-md-12 mt-3">
                                    <label class="form-label">Leave Reason</label>
                                    <input type="text" id="editLeaveReason" class="form-control" placeholder="Optional leave reason">
                                </div>
                                <div class="col-md-12 mt-2">
                                    <div class="form-check">
                                        <input class="form-check-input" type="checkbox" id="editAutoApproveLeave" checked>
                                        <label class="form-check-label" for="editAutoApproveLeave">
                                            Auto Approve Leave
                                        </label>
                                    </div>
                                </div>
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Remarks</label>
                                <textarea id="editRemarks" class="form-control" rows="3"></textarea>
                            </div>

                            <div class="alert alert-info">
                                <strong>Note:</strong> Changes will be saved immediately and will reflect in
                                reports.
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="button" class="btn btn-primary" id="btnSaveAttendance">
                            <i class="fas fa-save me-2"></i>Save Changes
                        </button>
                    </div>
                </div>
            </div>
        </div>