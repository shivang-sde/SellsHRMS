<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<style>
    :root {
        --zinc-50: #fafafa;
        --zinc-100: #f4f4f5;
        --zinc-200: #e4e4e7;
        --zinc-300: #d4d4d8;
        --zinc-400: #a1a1aa;
        --zinc-500: #71717a;
        --zinc-600: #52525b;
        --zinc-700: #3f3f46;
        --zinc-800: #27272a;
        --zinc-900: #18181b;
        --zinc-950: #09090b;
    }

    .attendance-dashboard-wrapper {
        padding: 1.5rem;
        background-color: var(--zinc-50);
        min-height: calc(100vh - 64px);
    }

    /* ── Header ── */
    .dashboard-header {
        margin-bottom: 2rem;
    }

    .dashboard-header h2 {
        font-size: 1.75rem;
        font-weight: 800;
        color: var(--zinc-900);
        letter-spacing: -0.03em;
        margin-bottom: 0.25rem;
    }

    .dashboard-header p {
        color: var(--zinc-500);
        font-size: 0.9375rem;
    }

    /* ── KPI Grid ── */
    .kpi-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
        gap: 1.25rem;
        margin-bottom: 2.5rem;
    }

    .kpi-card {
        background: #ffffff;
        border: 1px solid var(--zinc-200);
        border-radius: 1rem;
        padding: 1.5rem;
        display: flex;
        align-items: center;
        gap: 1.25rem;
        transition: all 0.2s ease;
    }

    .kpi-card:hover {
        border-color: var(--zinc-300);
        transform: translateY(-2px);
        box-shadow: 0 10px 25px rgba(0, 0, 0, 0.04);
    }

    .kpi-icon {
        width: 48px;
        height: 48px;
        background: var(--zinc-100);
        color: var(--zinc-900);
        border-radius: 0.75rem;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 1.25rem;
    }

    .kpi-info h6 {
        font-size: 0.75rem;
        font-weight: 600;
        color: var(--zinc-500);
        text-transform: uppercase;
        letter-spacing: 0.05em;
        margin: 0 0 0.25rem;
    }

    .kpi-info h3 {
        font-size: 1.375rem;
        font-weight: 700;
        color: var(--zinc-900);
        margin: 0;
    }

    .metric-change {
        font-size: 0.75rem;
        margin-top: 0.25rem;
    }

    .metric-change.positive { color: #10b981; }
    .metric-change.negative { color: #ef4444; }

    /* ── Chart Cards ── */
    .chart-card {
        background: #ffffff;
        border: 1px solid var(--zinc-200);
        border-radius: 1rem;
        height: 100%;
        display: flex;
        flex-direction: column;
        overflow: hidden;
    }

    .chart-card .card-header {
        background: #ffffff;
        border-bottom: 1px solid var(--zinc-100);
        padding: 1.25rem;
        display: flex;
        align-items: center;
        gap: 0.75rem;
        font-weight: 700;
        font-size: 0.9375rem;
        color: var(--zinc-900);
    }

    .chart-card .card-header i {
        color: var(--zinc-400);
    }

    .chart-card .card-body {
        padding: 1.5rem;
        flex: 1;
    }

    .chart-container {
        position: relative;
        width: 100%;
    }

    /* ── Loading ── */
    .dashboard-loading {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(255, 255, 255, 0.8);
        backdrop-filter: blur(4px);
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        z-index: 2000;
    }

    .spinner-zinc {
        width: 3rem;
        height: 3rem;
        border: 4px solid var(--zinc-200);
        border-top-color: var(--zinc-900);
        border-radius: 50%;
        animation: spin 0.8s linear infinite;
    }

    @keyframes spin {
        to { transform: rotate(360deg); }
    }
</style>

<div class="attendance-dashboard-wrapper">
    <!-- Header with Filters -->
    <div class="dashboard-header d-flex flex-wrap align-items-center justify-content-between gap-3">
        <div>
            <h2>Attendance Analytics</h2>
            <p>Strategic insights into workforce presence and engagement patterns.</p>
        </div>
        
        <div class="analytics-filters d-flex align-items-center gap-2">
            <div class="filter-group">
                <select id="dateRangeSelect" class="form-select form-select-sm">
                    <option value="7days">Last 7 Days</option>
                    <option value="30days" selected>Last 30 Days</option>
                    <option value="currentMonth">Current Month</option>
                    <option value="last3months">Last 3 Months</option>
                    <option value="custom">Custom Range</option>
                </select>
            </div>
            
            <div id="customDateInputs" class="d-none d-flex align-items-center gap-2">
                <input type="date" id="startDateInput" class="form-control form-control-sm">
                <span class="text-zinc-400">to</span>
                <input type="date" id="endDateInput" class="form-control form-control-sm">
            </div>
            
            <button id="applyFiltersBtn" class="btn btn-sm btn-zinc">
                <i class="fas fa-sync-alt me-1"></i> Sync
            </button>
        </div>
    </div>

    <!-- KPI Grid -->
    <div class="kpi-grid" id="summaryCards">
        <div class="kpi-card">
            <div class="kpi-icon"><i class="fas fa-user-check"></i></div>
            <div class="kpi-info">
                <h6>Avg Attendance</h6>
                <h3 id="avgAttendance">--%</h3>
                <div class="metric-change" id="attendanceChange">Loading...</div>
            </div>
        </div>

        <div class="kpi-card">
            <div class="kpi-icon"><i class="fas fa-calendar-times"></i></div>
            <div class="kpi-info">
                <h6>Days Missed</h6>
                <h3 id="daysMissed">--</h3>
                <div class="metric-change" id="daysMissedChange">Loading...</div>
            </div>
        </div>

        <div class="kpi-card">
            <div class="kpi-icon"><i class="fas fa-clock"></i></div>
            <div class="kpi-info">
                <h6>Late Arrivals</h6>
                <h3 id="lateArrivals">--</h3>
                <div class="metric-change text-muted">Today's count</div>
            </div>
        </div>

        <div class="kpi-card">
            <div class="kpi-icon"><i class="fas fa-users"></i></div>
            <div class="kpi-info">
                <h6>Active Staff</h6>
                <h3 id="activeEmployees">--</h3>
                <div class="metric-change text-muted">Total headcount</div>
            </div>
        </div>
    </div>

    <!-- Charts Row 1 -->
    <div class="row g-4 mb-4">
        <div class="col-lg-8">
            <div class="chart-card">
                <div class="card-header">
                    <i class="fas fa-chart-line"></i>
                    <span>Attendance Over Time</span>
                </div>
                <div class="card-body">
                    <div class="chart-container" style="height: 320px;">
                        <canvas id="attendanceChart"></canvas>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-lg-4">
            <div class="chart-card">
                <div class="card-header">
                    <i class="fas fa-chart-pie"></i>
                    <span>Absence Reasons</span>
                </div>
                <div class="card-body">
                    <div class="chart-container" style="height: 320px;">
                        <canvas id="absencePieChart"></canvas>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Charts Row 2 -->
    <div class="row g-4">
        <div class="col-lg-6">
            <div class="chart-card">
                <div class="card-header">
                    <i class="fas fa-calendar-day"></i>
                    <span>Daily Late Arrivals</span>
                </div>
                <div class="card-body">
                    <div class="chart-container" style="height: 300px;">
                        <canvas id="lateArrivalsDailyChart"></canvas>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-lg-6">
            <div class="chart-card">
                <div class="card-header">
                    <i class="fas fa-building"></i>
                    <span>Days Missed by Department</span>
                </div>
                <div class="card-body">
                    <div class="chart-container" style="height: 300px;">
                        <canvas id="deptMissedChart"></canvas>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Weekly Hours (Full Width) -->
    <div class="row g-4 mt-1">
        <div class="col-12">
            <div class="chart-card">
                <div class="card-header">
                    <i class="fas fa-business-time"></i>
                    <span>Average Weekly Hours by Department</span>
                </div>
                <div class="card-body">
                    <div class="chart-container" style="height: 350px;">
                        <canvas id="weeklyHoursChart"></canvas>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Loading Overlay -->
<div id="dashboardLoading" class="dashboard-loading" style="display: none;">
    <div class="spinner-zinc"></div>
    <p class="mt-4 font-monospace small text-uppercase tracking-widest text-zinc-500">Syncing Analytics...</p>
</div>