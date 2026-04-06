$(document).ready(function () {
    const orgId = window.APP.ORG_ID || $('#globalOrgId').val();
    let allAttendance = [];

    const todayStr = new Date().toISOString().split('T')[0];
    $('#attendanceDate').val(todayStr);

    // Update current date display
    function updateDateDisplay(dateStr) {
        const dateObj = dateStr ? new Date(dateStr) : new Date();
        const isToday = !dateStr || dateStr === todayStr;

        $('#attendancePageTitle').text(isToday ? "Today's Attendance" : "Attendance");
        $('#currentDate').html(`<i class="far fa-calendar-alt me-2"></i>${dateObj.toLocaleDateString('en-US', {
            weekday: 'long',
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        })}`);
    }
    updateDateDisplay(todayStr);

    // Load initial data
    loadTodayAttendance();
    loadDepartments();

    // Event handlers
    $('#btnRefresh').on('click', loadTodayAttendance);
    $('#btnFilter').on('click', filterAttendance);
    $('#searchEmployee').on('keyup', filterAttendance);
    $('#btnExport').on('click', exportToExcel);
    $('#attendanceDate').on('change', function () {
        updateDateDisplay($(this).val());
        loadTodayAttendance();
    });

    // Load attendance
    function loadTodayAttendance() {
        const selectedDate = $('#attendanceDate').val();

        $('#attendanceTableBody').html(`
            <tr>
                <td colspan="10" class="text-center">
                    <div class="spinner-border text-primary"></div>
                    <p class="mt-2 text-muted">Loading attendance...</p>
                </td>
            </tr>
        `);

        let url = `/api/attendance/today/org/${orgId}`;
        if (selectedDate && selectedDate !== todayStr) {
            url = `/api/attendance/org/${orgId}?startDate=${selectedDate}&endDate=${selectedDate}`;
        }

        $.ajax({
            url: url,
            method: 'GET',
            success: function (data) {
                allAttendance = data;
                updateSummaryCards(data);
                filterAttendance();
            },
            error: function (xhr) {
                showToast('error', 'No attendance record found for today');
                $('#attendanceTableBody').html(`
                    <tr>
                        <td colspan="10" class="text-center text-danger">
                            <i class="fas fa-exclamation-triangle me-2"></i>
                            No attendance record found for today
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
            success: function (data) {
                let options = '<option value="">All Departments</option>';
                data.forEach(dept => {
                    options += `<option value="${dept.name}">${dept.name}</option>`;
                });
                $('#filterDepartment').html(options);
            },
            error: function (xhr) {
                console.error('Failed to load departments');
                showToast("error", xhr.responseJSON.message);
            }
        });
    }

    // Update summary cards
    function updateSummaryCards(data) {
        console.log(data);

        const present = data.filter(d =>
            d.status === 'PRESENT' ||
            d.status === 'HALF_DAY' ||
            d.status === 'WFH' ||
            d.status === 'WFO' ||
            d.status === 'SHORT_DAY'
        ).length;

        const absent = data.filter(d => d.status === 'ABSENT').length;
        const onLeave = data.filter(d => d.status === 'ON_LEAVE').length;

        $('#countPresent').text(present);
        $('#countAbsent').text(absent);
        $('#countOnLeave').text(onLeave);
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


            const matchesDept =
                !deptFilter || record.departmentId == deptFilter || record.department == deptFilter;

            return matchesSearch && matchesStatus && matchesDept;
        });

        renderAttendanceTable(filtered);
    }

    // Render attendance table
    function renderAttendanceTable(data) {
        if (!data || data.length === 0) {
            $('#attendanceTableBody').html(`
                <tr>
                    <td colspan="10" class="text-center text-muted">
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

            const lateEarly = [];
            if (record.isLate)
                lateEarly.push('<span class="badge bg-warning text-dark" title="Late In">Late</span>');
            if (record.isEarlyOut)
                lateEarly.push('<span class="badge bg-info" title="Early Out">Early</span>');
            const lateEarlyStr = lateEarly.length > 0 ? lateEarly.join(" ") : "--";

            const currentEmpId = parseInt(window.APP.EMPLOYEE_ID) || 0;
            const isOrgAdmin = window.APP.ROLE === 'ORG_ADMIN' || window.APP.ROLE === 'SUPER_ADMIN';

            let actionBtn = "";
            if (record.employeeId == currentEmpId && !isOrgAdmin) {
                actionBtn = `<button class="btn btn-sm btn-outline-secondary" disabled title="Cannot edit own record"><i class="fas fa-edit"></i></button>`;
            } else {
                actionBtn = `
                    <button class="btn btn-sm btn-outline-primary edit-attendance" onclick="editAttendance('${record.summaryId || record.id}')">
                        <i class="fas fa-edit"></i>
                    </button>
                `;
            }

            html += `
                <tr>
                    <td><strong>${escapeHtml(record.employeeCode || 'N/A')}</strong></td>
                    <td>${escapeHtml(record.employeeName || 'N/A')}</td>
                    <td>${escapeHtml(record.department || 'N/A')}</td>
                    <td>${punchIn}</td>
                    <td>${punchOut}</td>
                    <td>${workHours}</td>
                    <td>${statusBadge}</td>
                    <td>
                      ${lateEarlyStr}
                    </td>
                    <td><small class="text-muted text-wrap">${escapeHtml(record.remarks || '--')}</small></td>
                    <td>${actionBtn}</td>
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

        console.log(allAttendance[0]);
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
        const exportDate = $('#attendanceDate').val() || new Date().toISOString().split('T')[0];
        a.download = `attendance_${exportDate}.csv`;
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

    window.editAttendance = function (attendanceId) {
        const record = allAttendance.find(r => (r.summaryId || r.id) == attendanceId);

        if (!record) {
            showToast("error", "Attendance record not found");
            return;
        }

        $('#editAttendanceId').val(record.summaryId || record.id);
        $('#editEmployeeName').val(record.employeeName || '');

        const selectedDate = $('#attendanceDate').val() || new Date().toISOString().split('T')[0];
        const dateStr = record.punchIn
            ? record.punchIn.split('T')[0]
            : selectedDate;
        $('#editAttendanceDate').val(dateStr);

        function formatTimeForInput(dateTimeStr) {
            if (!dateTimeStr) return "";
            const date = new Date(dateTimeStr);
            const hr = String(date.getHours()).padStart(2, '0');
            const min = String(date.getMinutes()).padStart(2, '0');
            return `${hr}:${min}`;
        }

        $('#editPunchIn').val(record.punchIn ? formatTimeForInput(record.punchIn) : '');
        $('#editPunchOut').val(record.punchOut ? formatTimeForInput(record.punchOut) : '');
        $('#editStatus').val(record.status || '');
        $('#editRemarks').val(record.remarks || '');

        const lateEarly = [];
        if (record.isLate) lateEarly.push("Late");
        if (record.isEarlyOut) lateEarly.push("Early");
        $('#editLateEarly').val(lateEarly.join(", ") || 'No');

        $('#editAttendanceModal').modal('show');
    };

    $('#btnSaveAttendance').on('click', function (e) {
        e.preventDefault();
        updateAttendance();
    });

    function updateAttendance() {
        const attendanceId = $('#editAttendanceId').val();
        const punchInTime = $('#editPunchIn').val(); // HH:mm
        const punchOutTime = $('#editPunchOut').val(); // HH:mm

        const status = $('#editStatus').val();
        const remarks = $('#editRemarks').val();

        const oldRecord = allAttendance.find(r => (r.summaryId || r.id) == attendanceId);
        if (!oldRecord) return;

        let newPunchIn = null;
        let newPunchOut = null;
        const baseDateStr = $('#editAttendanceDate').val();

        if (punchInTime) {
            newPunchIn = `${baseDateStr}T${punchInTime}:00`;
        }

        if (punchOutTime) {
            newPunchOut = `${baseDateStr}T${punchOutTime}:00`;
        }

        const payload = { ...oldRecord };
        payload.punchIn = newPunchIn;
        payload.punchOut = newPunchOut;
        payload.status = status;
        payload.remarks = remarks;

        $.ajax({
            url: `/api/attendance/update`,
            method: "PUT",
            data: JSON.stringify(payload),
            contentType: "application/json",
            success: function (data) {
                showToast("success", "Attendance updated successfully");
                $('#editAttendanceModal').modal('hide');
                loadTodayAttendance(); // Reload data after update
            },
            error: function (xhr) {
                const msg = xhr.responseJSON?.message || "Failed to update attendance";
                showToast("error", msg);
            }
        });
    }
});