$(document).ready(function() {
    const orgId = window.APP.ORG_ID || $('#globalOrgId').val();
    let allAttendance = [];

    // Update current date
    $('#currentDate').text(new Date().toLocaleDateString('en-US', {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    }));

    // Load initial data
    loadTodayAttendance();
    loadDepartments();

    // Event handlers
    $('#btnRefresh').on('click', loadTodayAttendance);
    $('#btnFilter').on('click', filterAttendance);
    $('#searchEmployee').on('keyup', filterAttendance);
    $('#btnExport').on('click', exportToExcel);

    // Load today's attendance
    function loadTodayAttendance() {
        $('#attendanceTableBody').html(`
            <tr>
                <td colspan="8" class="text-center">
                    <div class="spinner-border text-primary"></div>
                    <p class="mt-2 text-muted">Loading attendance...</p>
                </td>
            </tr>
        `);

        $.ajax({
            url: `/api/attendance/today/org/${orgId}`,
            method: 'GET',
            success: function(data) {
                allAttendance = data;
                updateSummaryCards(data);
                filterAttendance();
            },
            error: function(xhr) {
                showToast('error', 'Failed to load attendance');
                $('#attendanceTableBody').html(`
                    <tr>
                        <td colspan="8" class="text-center text-danger">
                            <i class="fas fa-exclamation-triangle me-2"></i>
                            Failed to load attendance data
                        </td>
                    </tr>
                `);
            }
        });
    }

    // Load departments for filter
    function loadDepartments() {
        $.ajax({
            url: `/api/departments/org/${orgId}`,
            method: 'GET',
            success: function(data) {
                let options = '<option value="">All Departments</option>';
                data.forEach(dept => {
                    options += `<option value="${dept.id}">${dept.name}</option>`;
                });
                $('#filterDepartment').html(options);
            },
            error: function() {
                console.error('Failed to load departments');
            }
        });
    }

    // Update summary cards
    function updateSummaryCards(data) {
        const present = data.filter(d => 
            d.status === 'PRESENT' || d.status === 'HALF_DAY' || d.status === 'WFH'
        ).length;
        
        const absent = data.filter(d => d.status === 'ABSENT').length;
        const onLeave = data.filter(d => d.status === 'ON_LEAVE').length;
        const pending = data.filter(d => d.status === 'ABSENT' && !d.punchIn).length;

        $('#countPresent').text(present);
        $('#countAbsent').text(absent);
        $('#countOnLeave').text(onLeave);
        $('#countPending').text(pending);
    }

    // Filter attendance
    function filterAttendance() {
        const search = $('#searchEmployee').val().toLowerCase();
        const statusFilter = $('#filterStatus').val();
        const deptFilter = $('#filterDepartment').val();

        let filtered = allAttendance.filter(record => {
            const matchesSearch = !search || 
                (record.employeeName && record.employeeName.toLowerCase().includes(search)) ||
                (record.employeeCode && record.employeeCode.toLowerCase().includes(search));
            
            const matchesStatus = !statusFilter || record.status === statusFilter;
            
            // Department filter would need department info in response
            const matchesDept = !deptFilter; // TODO: Add dept filtering when available
            
            return matchesSearch && matchesStatus && matchesDept;
        });

        renderAttendanceTable(filtered);
    }

    // Render attendance table
    function renderAttendanceTable(data) {
        if (!data || data.length === 0) {
            $('#attendanceTableBody').html(`
                <tr>
                    <td colspan="8" class="text-center text-muted">
                        <i class="fas fa-inbox me-2"></i>No attendance records found
                    </td>
                </tr>
            `);
            return;
        }

        let html = '';
        data.forEach(record => {
            console.log(record);    
            const statusBadge = getStatusBadge(record.status);
            const punchIn = record.punchIn ? formatTime(record.punchIn) : '<span class="text-muted">--:--</span>';
            const punchOut = record.punchOut ? formatTime(record.punchOut) : '<span class="text-muted">--:--</span>';
            const workHours = record.workHours ? record.workHours.toFixed(2) + 'h' : '<span class="text-muted">--</span>';
            
            html += `
                <tr>
                    <td><strong>${escapeHtml(record.employeeCode || 'N/A')}</strong></td>
                    <td>${escapeHtml(record.employeeName || 'N/A')}</td>
                    <td>${escapeHtml(record.department || 'N/A')}</td>
                    <td>${punchIn}</td>
                    <td>${punchOut}</td>
                    <td>${workHours}</td>
                    <td>${statusBadge}</td>
                    <td><small class="text-muted">${escapeHtml(record.remarks || '--')}</small></td>
                </tr>
            `;
        });

        $('#attendanceTableBody').html(html);
    }

    // Export to Excel
    function exportToExcel() {
        // Get filtered data
        const search = $('#searchEmployee').val().toLowerCase();
        const statusFilter = $('#filterStatus').val();

        let filtered = allAttendance.filter(record => {
            const matchesSearch = !search || 
                (record.employeeName && record.employeeName.toLowerCase().includes(search)) ||
                (record.employeeCode && record.employeeCode.toLowerCase().includes(search));
            const matchesStatus = !statusFilter || record.status === statusFilter;
            return matchesSearch && matchesStatus;
        });

        if (filtered.length === 0) {
            showToast('error', 'No data to export');
            return;
        }

        // Create CSV content
        let csv = 'Employee Code,Name,Department,Punch In,Punch Out,Work Hours,Status,Remarks\n';
        filtered.forEach(record => {
            csv += `"${record.employeeCode || 'N/A'}",`;
            csv += `"${record.employeeName || 'N/A'}",`;
            csv += `"${record.department || 'N/A'}",`;
            csv += `"${record.punchIn ? formatTime(record.punchIn) : '--'}",`;
            csv += `"${record.punchOut ? formatTime(record.punchOut) : '--'}",`;
            csv += `"${record.workHours ? record.workHours.toFixed(2) : '--'}",`;
            csv += `"${record.status || '--'}",`;
            csv += `"${record.remarks || '--'}"\n`;
        });

        // Download CSV
        const blob = new Blob([csv], { type: 'text/csv' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `attendance_${new Date().toISOString().split('T')[0]}.csv`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);

        showToast('success', 'Attendance exported successfully');
    }

    // Utility functions
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
        return date.toLocaleTimeString('en-IN', { hour12: true }); 
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