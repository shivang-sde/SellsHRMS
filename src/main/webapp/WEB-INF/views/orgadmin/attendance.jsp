<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

        <c:set var="pageTitle" value="Today's Attendance" scope="request" />
        <c:set var="pageScript" value="orgadmin/attendance" scope="request" />
        <c:set var="pageStyle" value="attendance" scope="request" />

        <div class="row attendance-header mb-4">
            <div class="col-12">
                <div class="d-flex justify-content-between align-items-end">
                    <div>
                        <h2 class="mb-1">Today's Attendance</h2>
                        <p class="mb-0" id="currentDate"><i class="far fa-calendar-alt me-2"></i>Loading...</p>
                    </div>
                    <div class="d-flex gap-2">
                        <sec:authorize access="hasAnyAuthority('EMPLOYEE_VIEW_ALL', 'ORG_ADMIN', 'SUPER_ADMIN')">
                            <button class="btn btn-saas-outline" onclick="location.href='${pageContext.request.contextPath}/org/attendance/reports'">
                                <i class="fas fa-chart-pie me-2"></i>Analytics
                            </button>
                        </sec:authorize>
                        <button class="btn btn-saas-primary" id="btnRefresh">
                            <i class="fas fa-sync-alt me-2"></i>Refresh
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Summary Statistics -->
        <div class="row g-4 mb-4">
            <div class="col-sm-6 col-xl-3">
                <div class="summary-card card-present">
                    <div class="card-body">
                        <div class="icon-label-wrap">
                            <div class="summary-icon-box">
                                <i class="fas fa-user-check"></i>
                            </div>
                            <span class="label">Present</span>
                        </div>
                        <h3 id="countPresent">0</h3>
                    </div>
                </div>
            </div>
            <div class="col-sm-6 col-xl-3">
                <div class="summary-card card-absent">
                    <div class="card-body">
                        <div class="icon-label-wrap">
                            <div class="summary-icon-box">
                                <i class="fas fa-user-times"></i>
                            </div>
                            <span class="label">Absent</span>
                        </div>
                        <h3 id="countAbsent">0</h3>
                    </div>
                </div>
            </div>
            <div class="col-sm-6 col-xl-3">
                <div class="summary-card card-leave">
                    <div class="card-body">
                        <div class="icon-label-wrap">
                            <div class="summary-icon-box">
                                <i class="fas fa-plane-departure"></i>
                            </div>
                            <span class="label">On Leave</span>
                        </div>
                        <h3 id="countOnLeave">0</h3>
                    </div>
                </div>
            </div>
            <div class="col-sm-6 col-xl-3">
                <div class="summary-card card-pending">
                    <div class="card-body">
                        <div class="icon-label-wrap">
                            <div class="summary-icon-box">
                                <i class="fas fa-clock"></i>
                            </div>
                            <span class="label">Pending</span>
                        </div>
                        <h3 id="countPending">0</h3>
                    </div>
                </div>
            </div>
        </div>

        <!-- Integrated Filter Bar -->
        <div class="filter-card card mb-4">
            <div class="card-body p-3">
                <div class="row g-3 align-items-center">
                    <div class="col-lg-3">
                        <div class="input-group">
                            <span class="input-group-text bg-white border-end-0 text-muted">
                                <i class="fas fa-search"></i>
                            </span>
                            <input type="text" id="searchEmployee" class="form-control border-start-0"
                                placeholder="Search employees...">
                        </div>
                    </div>
                    <div class="col-sm-6 col-lg-2">
                        <select id="filterStatus" class="form-select">
                            <option value="">All Statuses</option>
                            <option value="PRESENT">Present</option>
                            <option value="ABSENT">Absent</option>
                            <option value="ON_LEAVE">On Leave</option>
                            <option value="HALF_DAY">Half Day</option>
                            <option value="HOLIDAY">Holiday</option>
                        </select>
                    </div>
                    <div class="col-sm-6 col-lg-2">
                        <select id="filterDepartment" class="form-select">
                            <option value="">All Departments</option>
                        </select>
                    </div>
                    <div class="col-6 col-lg-2">
                        <button id="btnFilter" class="btn btn-saas-primary w-100">
                            Apply Filter
                        </button>
                    </div>
                    <div class="col-6 col-lg-3 text-end">
                        <sec:authorize access="hasAnyAuthority('EMPLOYEE_VIEW_ALL', 'ORG_ADMIN', 'SUPER_ADMIN')">
                            <button id="btnExport" class="btn btn-success d-inline-flex align-items-center gap-2" style="border-radius: 10px; padding: 0.625rem 1.25rem; font-weight: 600;">
                                <i class="fas fa-file-excel"></i> Export Excel
                            </button>
                        </sec:authorize>
                    </div>
                </div>
            </div>
        </div>

        <!-- Attendance Data Table -->
        <div class="attendance-table-card card">
            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead>
                        <tr>
                            <th>Employee Code</th>
                            <th>Name</th>
                            <th>Department</th>
                            <th>Punch In</th>
                            <th>Punch Out</th>
                            <th>Work Hours</th>
                            <th>Status</th>
                            <th>Alerts</th>
                            <th>Remarks</th>
                        </tr>
                    </thead>
                    <tbody id="attendanceTableBody">
                        <tr>
                            <td colspan="9" class="text-center py-5">
                                <div class="spinner-grow text-primary" role="status">
                                    <span class="visually-hidden">Loading...</span>
                                </div>
                                <p class="mt-2 text-muted fw-500">Retrieving today's records...</p>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>