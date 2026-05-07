(function () {
    "use strict";

    const DASHBOARD_CONFIG = {
        CHART_COLORS: {
            primary: '#18181b',    // Zinc 900
            secondary: '#71717a',  // Zinc 500
            emerald: '#10b981',    // Emerald 500
            rose: '#ef4444',       // Red 500
            amber: '#f59e0b',      // Amber 500
            zinc100: '#f4f4f5'
        }
    };

    let charts = {
        attendance: null,
        absence: null,
        lateArrivals: null,
        deptMissed: null,
        weeklyHours: null
    };

    /**
     * Get start and end dates based on filter selection
     */
    function getFilterRange() {
        const rangeType = $('#dateRangeSelect').val();
        let startDate, endDate = new Date().toISOString().split('T')[0];
        const now = new Date();

        if (rangeType === 'custom') {
            startDate = $('#startDateInput').val();
            endDate = $('#endDateInput').val();
        } else {
            const start = new Date();
            switch (rangeType) {
                case '7days': start.setDate(now.getDate() - 7); break;
                case '30days': start.setDate(now.getDate() - 30); break;
                case 'currentMonth': start.setDate(1); break;
                case 'last3months': start.setMonth(now.getMonth() - 3); break;
                default: start.setDate(now.getDate() - 30);
            }
            startDate = start.toISOString().split('T')[0];
        }
        return { startDate, endDate };
    }

    /**
     * Fetch all data with current filters
     */
    async function loadDashboardData() {
        const orgId = window.APP.ORG_ID;
        const { startDate, endDate } = getFilterRange();
        const baseUrl = '/api/dashboard';

        // Use ISO format for dates as expected by @DateTimeFormat
        const queryParams = `orgId=${orgId}&startDate=${startDate}&endDate=${endDate}`;

        showLoading(true);
        try {
            await Promise.all([
                fetchData(`${baseUrl}/summary?${queryParams}`).then(updateSummaryCards),
                fetchData(`${baseUrl}/attendance-trend?${queryParams}`).then(renderAttendanceChart),
                fetchData(`${baseUrl}/absence-reasons?${queryParams}`).then(renderAbsenceReasonsChart),
                fetchData(`${baseUrl}/days-missed-department?${queryParams}`).then(renderDeptMissedChart),
                fetchData(`${baseUrl}/late-arrivals-daily?${queryParams}`).then(renderLateArrivalsDailyChart),
                fetchData(`${baseUrl}/weekly-hours?${queryParams}`).then(renderWeeklyHoursChart)
            ]);
        } catch (error) {
            console.error('Failed to load dashboard data:', error);
            showError('Error syncing analytics. Please check your network or try again.');
        } finally {
            showLoading(false);
        }
    }

    function fetchData(url) {
        return $.ajax({
            url: url,
            method: 'GET',
            dataType: 'json'
        });
    }

    function showLoading(show) {
        const loadingEl = $('#dashboardLoading');
        show ? loadingEl.fadeIn(200) : loadingEl.fadeOut(200);
    }

    function showError(message) {
        console.error(message);
    }

    /**
     * UPDATE KPI CARDS
     */
    function updateSummaryCards(data) {
        $('#avgAttendance').text(data.averageAttendance.toFixed(1) + '%');
        $('#daysMissed').text(data.totalDaysMissed);
        $('#lateArrivals').text(data.todayLateArrivals);
        $('#activeEmployees').text(data.activeEmployees);

        renderTrendIndicator($('#attendanceChange'), data.averageAttendance, data.previousAttendance, true);
        renderTrendIndicator($('#daysMissedChange'), data.totalDaysMissed, data.previousDaysMissed, false);
    }

    function renderTrendIndicator($el, current, previous, higherIsBetter) {
        const diff = current - previous;
        const isPositive = diff >= 0;
        const colorClass = (isPositive === higherIsBetter) ? 'text-emerald-600' : 'text-rose-600';
        const icon = isPositive ? 'fa-arrow-up' : 'fa-arrow-down';

        $el.html(`<i class="fas ${icon} me-1"></i> ${Math.abs(diff).toFixed(1)} vs prev period`)
            .removeClass('text-emerald-600 text-rose-600')
            .addClass(colorClass);
    }

    /**
     * CHART RENDERING FUNCTIONS
     */
    function renderAttendanceChart(data) {
        const ctx = document.getElementById('attendanceChart');
        if (charts.attendance) charts.attendance.destroy();

        charts.attendance = new Chart(ctx, {
            type: 'line',
            data: {
                labels: data.map(d => `${d.month}/${d.year}`),
                datasets: [{
                    label: 'Attendance Rate (%)',
                    data: data.map(d => parseFloat(d.attendanceRate)),
                    borderColor: DASHBOARD_CONFIG.CHART_COLORS.primary,
                    backgroundColor: 'rgba(16, 185, 129, 0.09)',
                    borderWidth: 2,
                    fill: true,
                    tension: 0.3,
                    pointRadius: 4,
                    pointBackgroundColor: '#fff'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { display: false } },
                scales: {
                    y: {
                        beginAtZero: true,
                        max: 100,
                        grid: { color: DASHBOARD_CONFIG.CHART_COLORS.zinc100 },
                        ticks: { callback: val => val + '%' }
                    },
                    x: { grid: { display: false } }
                }
            }
        });
    }

    function renderAbsenceReasonsChart(data) {
        const ctx = document.getElementById('absencePieChart');
        if (charts.absence) charts.absence.destroy();

        const colors = ['#18181b', '#4f46e5', '#0d9488', '#e11d48', '#d97706', '#7c3aed'];

        charts.absence = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: data.map(d => d.reason),
                datasets: [{
                    data: data.map(d => parseFloat(d.percentage)),
                    backgroundColor: colors,
                    borderWidth: 0,
                    hoverOffset: 15
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                cutout: '75%',
                plugins: {
                    legend: { position: 'bottom', labels: { usePointStyle: true, boxWidth: 8, padding: 15 } }
                }
            }
        });
    }

    function renderLateArrivalsDailyChart(data) {
        const ctx = document.getElementById('lateArrivalsDailyChart');
        if (charts.lateArrivals) charts.lateArrivals.destroy();

        charts.lateArrivals = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: data.map(d => d.label),
                datasets: [{
                    label: 'Late Count',
                    data: data.map(d => d.attendanceRate),
                    backgroundColor: DASHBOARD_CONFIG.CHART_COLORS.rose,
                    borderRadius: 4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { display: false } },
                scales: {
                    y: { beginAtZero: true, grid: { color: DASHBOARD_CONFIG.CHART_COLORS.zinc100 } },
                    x: { grid: { display: false } }
                }
            }
        });
    }

    function renderDeptMissedChart(data) {
        const ctx = document.getElementById('deptMissedChart');
        if (charts.deptMissed) charts.deptMissed.destroy();

        charts.deptMissed = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: data.map(d => d.departmentName),
                datasets: [{
                    label: 'Days Missed',
                    data: data.map(d => d.missedCount),
                    backgroundColor: DASHBOARD_CONFIG.CHART_COLORS.amber,
                    borderRadius: 4
                }]
            },
            options: {
                indexAxis: 'y',
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { display: false } },
                scales: {
                    x: { beginAtZero: true, grid: { color: DASHBOARD_CONFIG.CHART_COLORS.zinc100 } },
                    y: { grid: { display: false } }
                }
            }
        });
    }

    function renderWeeklyHoursChart(data) {
        const ctx = document.getElementById('weeklyHoursChart');
        if (charts.weeklyHours) charts.weeklyHours.destroy();

        charts.weeklyHours = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: data.map(d => d.departmentName),
                datasets: [{
                    label: 'Avg Hours',
                    data: data.map(d => parseFloat(d.averageHours)),
                    backgroundColor: DASHBOARD_CONFIG.CHART_COLORS.primary,
                    borderRadius: 4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { display: false } },
                scales: {
                    x: { grid: { display: false } },
                    y: {
                        beginAtZero: true,
                        grid: { color: DASHBOARD_CONFIG.CHART_COLORS.zinc100 },
                        ticks: { callback: val => val + 'h' }
                    }
                }
            }
        });
    }

    /**
     * INITIALIZE
     */
    function initDashboard() {
        // Event Listeners
        $('#dateRangeSelect').on('change', function () {
            const isCustom = $(this).val() === 'custom';
            $('#customDateInputs').toggleClass('d-none', !isCustom);
            if (!isCustom) loadDashboardData();
        });

        $('#applyFiltersBtn').on('click', loadDashboardData);

        // Initial Load
        loadDashboardData();
    }

    $(document).ready(initDashboard);
})();
