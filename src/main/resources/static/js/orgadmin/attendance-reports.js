$(document).ready(function() {
    const orgId = window.APP.ORG_ID || $('#globalOrgId').val();
    let reportData = [];

    // Set default dates (last 30 days)
    setDefaultDates();
    loadEmployees();

    $('#btnGenerateReport').on('click', generateReport);
    $('#btnExportReport').on('click', exportReport);

    function setDefaultDates() {
        const today = new Date();
        const thirtyDaysAgo = new Date();
        thirtyDaysAgo.setDate(today.getDate() - 30);

        $('#endDate').val(today.toISOString().split('T')[0]);
        $('#startDate').val(thirtyDaysAgo.toISOString().split('T')[0]);
    }

    function loadEmployees() {
        $.ajax({
            url: `/api/employees/org/${orgId}`,
            method: 'GET',
            success: function(data) {
                let options = '<option value="">All Employees</option>';
                data.forEach(emp => {
                    options += `<option value="${emp.id}">${emp.fullName} (${emp.employeeCode})</option>`;
                });
                $('#filterEmployee').html(options);
            },
            error: function() {
                console.error('Failed to load employees');
            }
        });
    }

    function generateReport() {
        const startDate = $('#startDate').val();
        const endDate = $('#endDate').val();
        const employeeId = $('#filterEmployee').val();

        if (!startDate || !endDate) {
            showToast('error', 'Please select start and end dates');
            return;
        }

        if (new Date(endDate) < new Date(startDate)) {
            showToast('error', 'End date must be after start date');
            return;
        }

        $('#emptyState').hide();
        $('#reportSection').show();
        $('#statsSection').show();

        $('#reportTableBody').html(`
            <tr>
                <td colspan="8" class="text-center">
                    <div class="spinner-border text-primary"></div>
                    <p class="mt-2">Generating report...</p>
                </td>
            </tr>
        `);

        if (employeeId) {
            // Single employee report
            generateEmployeeReport(employeeId, startDate, endDate);
        } else {
            // Organization-wide report
            generateOrgReport(startDate, endDate);
        }
    }

    function generateEmployeeReport(employeeId, startDate, endDate) {
        $.ajax({
            url: `/api/attendance/employee/${employeeId}`,
            method: 'GET',
            data: { startDate, endDate },
            success: function(data) {
                reportData = data;
                updateStatistics(data);
                renderReportTable(data);
            },
            error: function() {
                showToast('error', 'Failed to generate report');
                $('#reportTableBody').html(`
                    <tr>
                        <td colspan="8" class="text-center text-danger">
                            Failed to load report data
                        </td>
                    </tr>
                `);
            }
        });
    }

    function generateOrgReport(startDate, endDate) {
        // Get attendance for date range
        const start = new Date(startDate);
        const end = new Date(endDate);
        const promises = [];

        // Fetch data for each date
        for (let d = new Date(start); d <= end; d.setDate(d.getDate() + 1)) {
            const dateStr = d.toISOString().split('T')[0];
            promises.push(
                $.ajax({
                    url: `/api/attendance/org/${orgId}`,
                    method: 'GET',
                    data: { date: dateStr }
                })
            );
        }

        Promise.all(promises).then(results => {
            // Flatten all results
            reportData = results.flat();
            updateStatistics(reportData);
            renderReportTable(reportData);
        }).catch(() => {
            showToast('error', 'Failed to generate report');
        });
    }

    function updateStatistics(data) {
        const presentCount = data.filter(d => 
            d.status === 'PRESENT' || d.status === 'HALF_DAY' || d.status === 'WFH'
        ).length;
        
        const absentCount = data.filter(d => d.status === 'ABSENT').length;
        const leaveCount = data.filter(d => d.status === 'ON_LEAVE').length;
        
        const totalHours = data.reduce((sum, d) => sum + (d.workHours || 0), 0);

        $('#statPresent').text(presentCount);
        $('#statAbsent').text(absentCount);
        $('#statLeaves').text(leaveCount);
        $('#statHours').text(totalHours.toFixed(2) + 'h');
    }

    function renderReportTable(data) {
        if (!data || data.length === 0) {
            $('#reportTableBody').html(`
                <tr>
                    <td colspan="8" class="text-center text-muted">
                        No attendance records found for selected criteria
                    </td>
                </tr>
            `);
            return;
        }

        let html = '';
        data.forEach(record => {
            const date = record.punchIn ? 
                new Date(record.punchIn).toLocaleDateString() : 
                new Date().toLocaleDateString();

            const lateEarly = [];
            if (record.isLate) lateEarly.push('<span class="badge bg-warning text-dark">Late</span>');
            if (record.isEarlyOut) lateEarly.push('<span class="badge bg-info">Early</span>');
            const lateEarlyStr = lateEarly.length > 0 ? lateEarly.join(' ') : '--';

            html += `
                <tr>
                    <td>${date}</td>
                    <td>${escapeHtml(record.employeeName || 'N/A')}</td>
                    <td>${escapeHtml(record.department || 'N/A')}</td>
                    <td>${record.punchIn ? formatTime(record.punchIn) : '--'}</td>
                    <td>${record.punchOut ? formatTime(record.punchOut) : '--'}</td>
                    <td>${record.workHours ? record.workHours.toFixed(2) + 'h' : '--'}</td>
                    <td>${getStatusBadge(record.status)}</td>
                    <td>${lateEarlyStr}</td>
                </tr>
            `;
        });

        $('#reportTableBody').html(html);
    }

    function exportReport() {
        if (!reportData || reportData.length === 0) {
            showToast('error', 'No data to export');
            return;
        }

        let csv = 'Date,Employee,Department,Punch In,Punch Out,Work Hours,Status,Late/Early\n';
        
        reportData.forEach(record => {
            const date = record.punchIn ? 
                new Date(record.punchIn).toLocaleDateString() : 
                new Date().toLocaleDateString();

            const lateEarly = [];
            if (record.isLate) lateEarly.push('Late');
            if (record.isEarlyOut) lateEarly.push('Early');

            csv += `"${date}",`;
            csv += `"${record.employeeName || 'N/A'}",`;
            csv += `"${record.department || 'N/A'}",`;
            csv += `"${record.punchIn ? formatTime(record.punchIn) : '--'}",`;
            csv += `"${record.punchOut ? formatTime(record.punchOut) : '--'}",`;
            csv += `"${record.workHours ? record.workHours.toFixed(2) : '--'}",`;
            csv += `"${record.status || '--'}",`;
            csv += `"${lateEarly.join(', ') || '--'}"\n`;
        });

        const blob = new Blob([csv], { type: 'text/csv' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `attendance_report_${new Date().toISOString().split('T')[0]}.csv`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);

        showToast('success', 'Report exported successfully');
    }

    function getStatusBadge(status) {
        const badges = {
            'PRESENT': '<span class="badge bg-success">Present</span>',
            'ABSENT': '<span class="badge bg-danger">Absent</span>',
            'ON_LEAVE': '<span class="badge bg-warning text-dark">On Leave</span>',
            'HALF_DAY': '<span class="badge bg-info">Half Day</span>',
            'SHORT_DAY': '<span class="badge bg-warning">Short Day</span>',
            'HOLIDAY': '<span class="badge bg-secondary">Holiday</span>',
            'WEEK_OFF': '<span class="badge bg-secondary">Week Off</span>',
            'WFH': '<span class="badge bg-primary">WFH</span>'
        };
        return badges[status] || '<span class="badge bg-secondary">' + status + '</span>';
    }

    function formatTime(dateTimeStr) {
        if (!dateTimeStr) return '--';
        const date = new Date(dateTimeStr);
        return date.toLocaleTimeString('en-US', {
            hour: '2-digit',
            minute: '2-digit',
            hour12: false
        });
    }

    function escapeHtml(text) {
        if (!text) return '';
        const map = {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#039;'
        };
        return String(text).replace(/[&<>"']/g, m => map[m]);
    }
});