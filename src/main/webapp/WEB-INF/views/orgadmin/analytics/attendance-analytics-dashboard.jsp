<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

            <!-- Attendance Dashboard Content -->
            <div class="attendance-dashboard-container">
                <div class="row mb-4">
                    <div class="col-12">
                        <h2 class="mb-0 text-primary fw-bold">
                            <i class="fas fa-chart-line me-2"></i>
                            Attendance & Absenteeism Dashboard
                        </h2>
                        <p class="text-muted">Real-time analytics for employee attendance</p>
                    </div>
                </div>

                <!-- Summary Cards -->
                <div class="row mb-4" id="summaryCards">
                    <div class="col-md-6 col-lg-3 mb-3">
                        <div class="card dashboard-metric-card h-100 shadow-sm">
                            <div class="card-body text-center">
                                <div class="metric-icon mb-3">
                                    <i class="fas fa-user-check fa-3x text-primary"></i>
                                </div>
                                <h6 class="text-muted text-uppercase mb-2">Average Attendance</h6>
                                <h2 class="metric-value text-primary mb-2" id="avgAttendance">--%</h2>
                                <p class="metric-change mb-0" id="attendanceChange">
                                    <span class="spinner-border spinner-border-sm" role="status"></span>
                                    <span class="ms-2">Loading...</span>
                                </p>
                            </div>
                        </div>
                    </div>

                    <div class="col-md-6 col-lg-3 mb-3">
                        <div class="card dashboard-metric-card h-100 shadow-sm">
                            <div class="card-body text-center">
                                <div class="metric-icon mb-3">
                                    <i class="fas fa-calendar-times fa-3x text-danger"></i>
                                </div>
                                <h6 class="text-muted text-uppercase mb-2">Days Missed</h6>
                                <h2 class="metric-value text-danger mb-2" id="daysMissed">--</h2>
                                <p class="metric-change mb-0" id="daysMissedChange">
                                    <span class="spinner-border spinner-border-sm" role="status"></span>
                                    <span class="ms-2">Loading...</span>
                                </p>
                            </div>
                        </div>
                    </div>

                    <div class="col-md-6 col-lg-3 mb-3">
                        <div class="card dashboard-metric-card h-100 shadow-sm">
                            <div class="card-body text-center">
                                <div class="metric-icon mb-3">
                                    <i class="fas fa-clock fa-3x text-warning"></i>
                                </div>
                                <h6 class="text-muted text-uppercase mb-2">Late Arrivals</h6>
                                <h2 class="metric-value text-warning mb-2" id="lateArrivals">--</h2>
                                <p class="metric-change mb-0 text-muted">Today</p>
                            </div>
                        </div>
                    </div>

                    <div class="col-md-6 col-lg-3 mb-3">
                        <div class="card dashboard-metric-card h-100 shadow-sm">
                            <div class="card-body text-center">
                                <div class="metric-icon mb-3">
                                    <i class="fas fa-users fa-3x text-success"></i>
                                </div>
                                <h6 class="text-muted text-uppercase mb-2">Active Employees</h6>
                                <h2 class="metric-value text-success mb-2" id="activeEmployees">--</h2>
                                <p class="metric-change mb-0 text-muted">Current Period</p>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Charts Row 1 -->
                <div class="row mb-4">
                    <div class="col-lg-8 mb-3">
                        <div class="card shadow-sm h-100">
                            <div class="card-header bg-white border-0">
                                <h5 class="mb-0">
                                    <i class="fas fa-chart-line text-primary me-2"></i>
                                    Attendance Over Time
                                </h5>
                            </div>
                            <div class="card-body">
                                <div class="chart-container" style="position: relative; height: 300px;">
                                    <canvas id="attendanceChart"></canvas>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- <div class="col-lg-4 mb-3">
                        <div class="card shadow-sm h-100">
                            <div class="card-header bg-white border-0">
                                <h5 class="mb-0">
                                    <i class="fas fa-chart-line text-primary me-2"></i>
                                    Late Arrivals Over Time
                                </h5>
                            </div>
                            <div class="card-body">
                                <div class="chart-container" style="position: relative; height: 300px;">
                                    <canvas id="lateArrivalsChart"></canvas>
                                </div>
                            </div>
                        </div>
                    </div> -->


                    <div class="col-lg-6 mb-3">
                        <div class="card shadow-sm h-100">
                            <div class="card-header bg-white border-0">
                                <h5 class="mb-0"><i class="fas fa-calendar-day text-primary me-2"></i>Daily Late
                                    Arrivals</h5>
                            </div>
                            <div class="card-body">
                                <div class="chart-container" style="position: relative; height: 300px;">
                                    <canvas id="lateArrivalsDailyChart"></canvas>
                                </div>
                            </div>
                        </div>
                    </div>


                    <div class="col-lg-4 mb-3">
                        <div class="card shadow-sm h-100">
                            <div class="card-header bg-white border-0">
                                <h5 class="mb-0">
                                    <i class="fas fa-chart-pie text-primary me-2"></i>
                                    Absence Reasons
                                </h5>
                            </div>
                            <div class="card-body">
                                <div class="chart-container" style="position: relative; height: 300px;">
                                    <canvas id="absencePieChart"></canvas>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Charts Row 2 -->
                <div class="row">
                    <div class="col-lg-6 mb-3">
                        <div class="card shadow-sm h-100">
                            <div class="card-header bg-white border-0">
                                <h5 class="mb-0">
                                    <i class="fas fa-building text-primary me-2"></i>
                                    Days Missed by Department
                                </h5>
                            </div>
                            <div class="card-body">
                                <div class="chart-container" style="position: relative; height: 200px;">
                                    <canvas id="deptMissedChart"></canvas>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="col-lg-6 mb-3">
                        <div class="card shadow-sm h-100">
                            <div class="card-header bg-white border-0">
                                <h5 class="mb-0">
                                    <i class="fas fa-business-time text-primary me-2"></i>
                                    Average Weekly Hours by Department
                                </h5>
                            </div>
                            <div class="card-body">
                                <div class="chart-container" style="position: relative; height: 300px;">
                                    <canvas id="weeklyHoursChart"></canvas>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Loading Overlay -->
            <div id="dashboardLoading" class="dashboard-loading" style="display: none;">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
                <p class="mt-3">Loading dashboard data...</p>
            </div>