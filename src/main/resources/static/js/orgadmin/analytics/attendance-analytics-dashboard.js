/**
 * Attendance Dashboard JavaScript
 * Handles data fetching and chart rendering for the attendance dashboard
 */

(function () {
    'use strict';

    // Dashboard Configuration
    const DASHBOARD_CONFIG = {
        API_BASE: window.APP.CONTEXT_PATH + '/api/dashboard',
        ORG_ID: window.APP.ORG_ID,
        REFRESH_INTERVAL: 300000, // 5 minutes
        CHART_COLORS: {
            primary: '#667eea',
            success: '#4ade80',
            danger: '#ef4444',
            warning: '#facc15',
            info: '#60a5fa',
            purple: '#a78bfa',
            pink: '#f472b6'
        }
    };

    // Chart instances
    let charts = {
        attendance: null,
        absence: null,
        deptMissed: null,
        weeklyHours: null
    };

    /**
     * Initialize Dashboard
     */
    function initDashboard() {
        console.log('🚀 Initializing Attendance Dashboard...');
        console.log('Organisation ID:', DASHBOARD_CONFIG.ORG_ID);

        if (!DASHBOARD_CONFIG.ORG_ID) {
            showError('Organisation ID not found. Please login again.');
            return;
        }

        // Load all dashboard data
        loadDashboardData();

        // Setup auto-refresh
        setInterval(loadDashboardData, DASHBOARD_CONFIG.REFRESH_INTERVAL);
    }

    /**
     * Load All Dashboard Data
     */
    async function loadDashboardData() {
        try {
            showLoading(true);

            const orgId = DASHBOARD_CONFIG.ORG_ID;
            const baseUrl = DASHBOARD_CONFIG.API_BASE;

            // Fetch all data in parallel
            const [summary, trend, reasons, deptMissed, weeklyHours, lateArrivals, dailyLateArrivals] = await Promise.all([
                fetchData(`${baseUrl}/summary?orgId=${orgId}`),
                fetchData(`${baseUrl}/attendance-trend?orgId=${orgId}`),
                fetchData(`${baseUrl}/absence-reasons?orgId=${orgId}`),
                fetchData(`${baseUrl}/days-missed-department?orgId=${orgId}`),
                fetchData(`${baseUrl}/weekly-hours?orgId=${orgId}`),
                fetchData(`${baseUrl}/late-arrivals-trend?orgId=${orgId}`),
                fetchData(`${baseUrl}/late-arrivals-daily?orgId=${orgId}&days=30`)
            ]);

            console.log('Late Arrivals Daily:', dailyLateArrivals);
            console.log('Late Arrivals Trend:', lateArrivals);
            console.log('Summary:', summary);
            console.log('Trend:', trend);
            console.log('Reasons:', reasons);
            console.log('Dept Missed:', deptMissed);
            console.log('Weekly Hours:', weeklyHours);

            // renderLateArrivalsTrendChart(lateArrivals);
            renderLateArrivalsDailyChart(dailyLateArrivals);

            // function renderLateArrivalsTrendChart(data) {
            //     const ctx = document.getElementById('lateArrivalsTrendChart');
            //     if (!ctx) return;

            //     if (charts.lateArrivals) charts.lateArrivals.destroy();

            //     charts.lateArrivals = new Chart(ctx, {
            //         type: 'bar',
            //         data: {
            //             labels: data.map(d => d.monthLabel),
            //             datasets: [{
            //                 label: 'Late Arrivals',
            //                 data: data.map(d => parseFloat(d.attendanceRate || 0)),
            //                 backgroundColor: '#3b82f6',
            //                 borderRadius: 6
            //             }]
            //         },
            //         options: {
            //             responsive: true,
            //             maintainAspectRatio: false,
            //             scales: { y: { beginAtZero: true } },
            //             plugins: {
            //                 legend: { display: true, position: 'top' }
            //             }
            //         }
            //     });
            // }


            // Update UI with fetched data
            updateSummaryCards(summary);
            renderAttendanceTrendChart(trend);
            renderAbsenceReasonsChart(reasons);
            renderDeptMissedChart(deptMissed);
            renderWeeklyHoursChart(weeklyHours);

            showLoading(false);
            console.log('✅ Dashboard data loaded successfully');

        } catch (error) {
            console.error('❌ Error loading dashboard:', error);
            showError('Failed to load dashboard data. Please refresh the page.');
            showLoading(false);
        }
    }

    /**
     * Fetch Data from API
     */
    async function fetchData(url) {
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'same-origin'
        });

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }

        return await response.json();
    }

    /**
     * Update Summary Cards
     */
    function updateSummaryCards(summary) {
        const avgAttendance = parseFloat(summary.averageAttendance) || 0;
        const prevAttendance = parseFloat(summary.previousAttendance) || 0;
        const daysMissed = summary.totalDaysMissed || 0;
        const prevDaysMissed = summary.previousDaysMissed || 0;
        const activeEmp = summary.activeEmployees || 0;
        const lateArrivals = summary.todayLateArrivals || 0;

        const attendanceChange = avgAttendance - prevAttendance;
        const daysMissedChange = daysMissed - prevDaysMissed;

        // Update Average Attendance
        $('#avgAttendance').text(avgAttendance.toFixed(1) + '%');

        const attendanceChangeEl = $('#attendanceChange');
        const changeText = `${attendanceChange >= 0 ? '+' : ''}${attendanceChange.toFixed(1)}% vs Previous (${prevAttendance.toFixed(1)}%)`;
        attendanceChangeEl.text(changeText);
        attendanceChangeEl.removeClass('positive negative').addClass(attendanceChange >= 0 ? 'positive' : 'negative');

        // Update Days Missed
        $('#daysMissed').text(daysMissed);
        $('#activeEmployees').text(activeEmp);
        $('#lateArrivals').text(lateArrivals);

        const daysMissedChangeEl = $('#daysMissedChange');
        const missedChangeText = `${daysMissedChange >= 0 ? '+' : ''}${daysMissedChange} vs Previous (${prevDaysMissed})`;
        daysMissedChangeEl.text(missedChangeText);
        daysMissedChangeEl.removeClass('positive negative').addClass(daysMissedChange <= 0 ? 'positive' : 'negative');
    }

    /**
     * Render Attendance Trend Chart
     */
    function renderAttendanceTrendChart(data) {
        const ctx = document.getElementById('attendanceChart');

        // Destroy existing chart
        if (charts.attendance) {
            charts.attendance.destroy();
        }

        charts.attendance = new Chart(ctx, {
            type: 'line',
            data: {
                labels: data.map(d => d.monthLabel),
                datasets: [{
                    label: 'Attendance Rate (%)',
                    data: data.map(d => parseFloat(d.attendanceRate)),
                    borderColor: DASHBOARD_CONFIG.CHART_COLORS.primary,
                    backgroundColor: 'rgba(102, 126, 234, 0.1)',
                    borderWidth: 3,
                    fill: true,
                    tension: 0.4,
                    pointRadius: 5,
                    pointHoverRadius: 7,
                    pointBackgroundColor: '#ffffff',
                    pointBorderColor: DASHBOARD_CONFIG.CHART_COLORS.primary,
                    pointBorderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: true,
                        position: 'top',
                        labels: {
                            font: { size: 12 },
                            padding: 15
                        }
                    },
                    tooltip: {
                        backgroundColor: 'rgba(0, 0, 0, 0.8)',
                        padding: 12,
                        titleFont: { size: 14 },
                        bodyFont: { size: 13 },
                        callbacks: {
                            label: function (context) {
                                return 'Attendance: ' + context.parsed.y.toFixed(1) + '%';
                            }
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        max: 100,
                        ticks: {
                            callback: function (value) {
                                return value + '%';
                            }
                        },
                        grid: {
                            color: 'rgba(0, 0, 0, 0.05)'
                        }
                    },
                    x: {
                        grid: {
                            display: false
                        }
                    }
                }
            }
        });
    }

    /**
     * Render Absence Reasons Pie Chart
     */
    function renderAbsenceReasonsChart(data) {
        const ctx = document.getElementById('absencePieChart');

        if (charts.absence) {
            charts.absence.destroy();
        }

        const colors = [
            DASHBOARD_CONFIG.CHART_COLORS.primary,
            DASHBOARD_CONFIG.CHART_COLORS.info,
            DASHBOARD_CONFIG.CHART_COLORS.warning,
            DASHBOARD_CONFIG.CHART_COLORS.success,
            DASHBOARD_CONFIG.CHART_COLORS.purple,
            DASHBOARD_CONFIG.CHART_COLORS.pink
        ];

        charts.absence = new Chart(ctx, {
            type: 'pie',
            data: {
                labels: data.map(d => d.reason),
                datasets: [{
                    data: data.map(d => parseFloat(d.percentage)),
                    backgroundColor: colors,
                    borderWidth: 2,
                    borderColor: '#ffffff'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: {
                            font: { size: 11 },
                            padding: 10,
                            generateLabels: function (chart) {
                                const data = chart.data;
                                return data.labels.map((label, i) => ({
                                    text: `${label}: ${data.datasets[0].data[i].toFixed(1)}%`,
                                    fillStyle: data.datasets[0].backgroundColor[i],
                                    hidden: false,
                                    index: i
                                }));
                            }
                        }
                    },
                    tooltip: {
                        callbacks: {
                            label: function (context) {
                                return context.label + ': ' + context.parsed.toFixed(1) + '%';
                            }
                        }
                    }
                }
            }
        });
    }



    /**
 * Render Daily Late Arrivals Chart
 * Shows last 15–30 days trend from /late-arrivals-daily
 */
    function renderLateArrivalsDailyChart(data) {
        const ctx = document.getElementById('lateArrivalsDailyChart');
        if (!ctx) return;

        // Destroy existing instance if any
        if (charts.lateArrivalsDaily) {
            charts.lateArrivalsDaily.destroy();
        }

        charts.lateArrivalsDaily = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: data.map(d => d.monthLabel), // here monthLabel is dayLabel ("Jan 05", etc.)
                datasets: [{
                    label: 'Late Arrivals',
                    data: data.map(d => parseFloat(d.attendanceRate || 0)), // DTO uses same field
                    backgroundColor: '#60a5fa',
                    borderRadius: 6,
                    borderSkipped: false
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: true,
                        position: 'top'
                    },
                    tooltip: {
                        callbacks: {
                            label: function (context) {
                                return 'Late Arrivals: ' + context.parsed.y;
                            }
                        }
                    }
                },
                scales: {
                    x: {
                        grid: { display: false },
                        ticks: {
                            autoSkip: true,
                            maxTicksLimit: 10,
                            font: { size: 11 }
                        }
                    },
                    y: {
                        beginAtZero: true,
                        grid: { color: 'rgba(0, 0, 0, 0.05)' },
                        title: { display: true, text: 'Employees', font: { size: 12 } }
                    }
                }
            }
        });
    }


    /**
     * Render Department Missed Days Chart
     */
    function renderDeptMissedChart(data) {
        const ctx = document.getElementById('deptMissedChart');

        if (charts.deptMissed) {
            charts.deptMissed.destroy();
        }

        charts.deptMissed = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: data.map(d => d.departmentName),
                datasets: [{
                    label: 'Days Missed',
                    data: data.map(d => d.daysMissed),
                    backgroundColor: DASHBOARD_CONFIG.CHART_COLORS.danger,
                    borderRadius: 10,
                    borderSkipped: false

                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                indexAxis: 'y',
                plugins: {
                    legend: {
                        display: false
                    },
                    tooltip: {
                        callbacks: {
                            label: function (context) {
                                return 'Days Missed: ' + context.parsed.x;
                            }
                        }
                    }
                },
                scales: {
                    x: {
                        beginAtZero: true,
                        grid: {
                            color: 'rgba(0, 0, 0, 0.05)'
                        }
                    },
                    y: {
                        grid: {
                            display: false
                        }
                    }
                }
            }
        });
    }

    /**
     * Render Weekly Hours Chart
     */
    function renderWeeklyHoursChart(data) {
        const ctx = document.getElementById('weeklyHoursChart');

        console.log('Weekly Hours Data:', data);

        if (charts.weeklyHours) {
            charts.weeklyHours.destroy();
        }

        charts.weeklyHours = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: data.map(d => d.departmentName),
                datasets: [{
                    label: 'Average Hours',
                    data: data.map(d => parseFloat(d.averageHours)),
                    backgroundColor: DASHBOARD_CONFIG.CHART_COLORS.success,
                    borderRadius: 8,
                    borderSkipped: false
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                indexAxis: 'y',
                plugins: {
                    legend: {
                        display: false
                    },
                    tooltip: {
                        callbacks: {
                            label: function (context) {
                                return 'Hours: ' + context.parsed.x.toFixed(1);
                            }
                        }
                    }
                },
                scales: {
                    x: {
                        beginAtZero: true,
                        ticks: {
                            callback: function (value) {
                                return value + ' hrs';
                            }
                        },
                        grid: {
                            color: 'rgba(0, 0, 0, 0.05)'
                        }
                    },
                    y: {
                        grid: {
                            display: false
                        }
                    }
                }
            }
        });
    }

    /**
     * Show/Hide Loading Overlay
     */
    function showLoading(show) {
        const loadingEl = $('#dashboardLoading');
        if (show) {
            loadingEl.fadeIn(200);
        } else {
            loadingEl.fadeOut(200);
        }
    }

    /**
     * Show Error Message
     */
    function showError(message) {
        if (typeof Swal !== 'undefined') {
            Swal.fire({
                icon: 'error',
                title: 'Dashboard Error',
                text: message,
                confirmButtonColor: DASHBOARD_CONFIG.CHART_COLORS.primary
            });
        } else {
            alert(message);
        }
    }

    // Initialize when DOM is ready
    $(document).ready(function () {
        initDashboard();
    });

})();