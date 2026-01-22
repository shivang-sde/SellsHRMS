$(document).ready(function() {
    const orgId = window.APP.ORG_ID || $('#globalOrgId').val();
    let allLeaves = [];

    // Load data
    loadAllLeaves();
    loadLeaveTypes();

    // Event handlers
    $('#btnFilter').on('click', filterLeaves);
    $('#searchEmployee').on('keyup', filterLeaves);
    $('#filterStatus, #filterLeaveType, #filterStartDate, #filterEndDate').on('change', filterLeaves);
    $('#btnExport').on('click', exportToExcel);

    // Load all leaves
    function loadAllLeaves() {
        $('#leavesTableBody').html(`
            <tr>
                <td colspan="8" class="text-center">
                    <div class="spinner-border text-primary"></div>
                    <p class="mt-2 text-muted">Loading leaves...</p>
                </td>
            </tr>
        `);

        $.ajax({
            url: `/api/leaves/org/${orgId}`,
            method: 'GET',
            success: function(data) {
                allLeaves = data;
                updateStatistics(data);
                filterLeaves();
            },
            error: function() {
                showToast('error', 'Failed to load leaves');
                $('#leavesTableBody').html(`
                    <tr>
                        <td colspan="8" class="text-center text-danger">
                            Failed to load leave data
                        </td>
                    </tr>
                `);
            }
        });
    }

    // Load leave types for filter
    function loadLeaveTypes() {
        $.ajax({
            url: `/api/leave-types/org/${orgId}`,
            method: 'GET',
            success: function(data) {
                let options = '<option value="">All Types</option>';
                data.forEach(type => {
                    options += `<option value="${type.id}">${type.name}</option>`;
                });
                $('#filterLeaveType').html(options);
            },
            error: function() {
                console.error('Failed to load leave types');
            }
        });
    }

    // Update statistics
    function updateStatistics(data) {
        const pending = data.filter(l => l.leaveStatus === 'PENDING').length;
        const approved = data.filter(l => l.leaveStatus === 'APPROVE').length;
        const rejected = data.filter(l => l.leaveStatus === 'REJECTED').length;
        const canceled = data.filter(l => l.leaveStatus === 'CANCELED').length;

        $('#statPending').text(pending);
        $('#statApproved').text(approved);
        $('#statRejected').text(rejected);
        $('#statCanceled').text(canceled);
    }

    // Filter leaves
    function filterLeaves() {
        const search = $('#searchEmployee').val().toLowerCase();
        const statusFilter = $('#filterStatus').val();
        const leaveTypeFilter = $('#filterLeaveType').val();
        const startDateFilter = $('#filterStartDate').val();
        const endDateFilter = $('#filterEndDate').val();

        let filtered = allLeaves.filter(leave => {
            const matchesSearch = !search || 
                (leave.employeeName && leave.employeeName.toLowerCase().includes(search)) ||
                (leave.employeeCode && leave.employeeCode.toLowerCase().includes(search));
            
            const matchesStatus = !statusFilter || leave.leaveStatus === statusFilter;
            const matchesType = !leaveTypeFilter || leave.leaveTypeId == leaveTypeFilter;
            
            const matchesStartDate = !startDateFilter || leave.startDate >= startDateFilter;
            const matchesEndDate = !endDateFilter || leave.endDate <= endDateFilter;

            return matchesSearch && matchesStatus && matchesType && matchesStartDate && matchesEndDate;
        });

        // Sort by start date (newest first)
        filtered.sort((a, b) => new Date(b.startDate) - new Date(a.startDate));

        renderLeavesTable(filtered);
    }

    // Render leaves table
    function renderLeavesTable(data) {
        if (!data || data.length === 0) {
            $('#leavesTableBody').html(`
                <tr>
                    <td colspan="8" class="text-center text-muted">
                        <i class="fas fa-inbox me-2"></i>No leave records found
                    </td>
                </tr>
            `);
            return;
        }

        let html = '';
        data.forEach(leave => {
            html += `
                <tr>
                    <td><strong>${escapeHtml(leave.employeeCode)}</strong></td>
                    <td>${escapeHtml(leave.employeeName)}</td>
                    <td><span class="badge bg-info">${escapeHtml(leave.leaveTypeName)}</span></td>
                    <td>${formatDate(leave.startDate)}</td>
                    <td>${formatDate(leave.endDate)}</td>
                    <td><span class="badge bg-secondary">${leave.totalDays} days</span></td>
                    <td>${getStatusBadge(leave.leaveStatus)}</td>
                    <td>
                        <button class="btn btn-sm btn-outline-primary btn-view" data-id="${leave.id}">
                            <i class="fas fa-eye"></i> View
                        </button>
                    </td>
                </tr>
            `;
        });

        $('#leavesTableBody').html(html);

        // Attach event handlers
        $('.btn-view').on('click', function() {
            const leaveId = $(this).data('id');
            viewLeaveDetails(leaveId);
        });
    }

    // View leave details
    function viewLeaveDetails(leaveId) {
        $.ajax({
            url: `/api/leaves/${leaveId}`,
            method: 'GET',
            success: function(leave) {
                const html = `
                    <div class="row g-3">
                        <div class="col-12">
                            <h6 class="text-primary"><i class="fas fa-user me-2"></i>Employee Information</h6>
                            <hr>
                        </div>
                        <div class="col-md-6">
                            <strong>Name:</strong> ${escapeHtml(leave.employeeName)}
                        </div>
                        <div class="col-md-6">
                            <strong>Employee Code:</strong> ${escapeHtml(leave.employeeCode)}
                        </div>
                        
                        <div class="col-12 mt-3">
                            <h6 class="text-primary"><i class="fas fa-calendar me-2"></i>Leave Information</h6>
                            <hr>
                        </div>
                        <div class="col-md-6">
                            <strong>Leave Type:</strong> 
                            <span class="badge bg-info">${escapeHtml(leave.leaveTypeName)}</span>
                        </div>
                        <div class="col-md-6">
                            <strong>Duration:</strong> 
                            <span class="badge bg-secondary">${leave.totalDays} day(s)</span>
                        </div>
                        <div class="col-md-6">
                            <strong>Start Date:</strong> ${formatDate(leave.startDate)}
                        </div>
                        <div class="col-md-6">
                            <strong>End Date:</strong> ${formatDate(leave.endDate)}
                        </div>
                        <div class="col-12 mt-3">
                            <strong>Reason:</strong>
                            <p class="mt-2 p-3 bg-light rounded">${escapeHtml(leave.reason)}</p>
                        </div>
                        <div class="col-md-6">
                            <strong>Status:</strong> ${getStatusBadge(leave.leaveStatus)}
                        </div>
                        ${leave.approvedByName ? `
                        <div class="col-md-6">
                            <strong>Approved By:</strong> ${escapeHtml(leave.approvedByName)}
                        </div>
                        ` : ''}
                    </div>
                `;
                
                $('#leaveDetailBody').html(html);
                const modal = new bootstrap.Modal(document.getElementById('leaveDetailModal'));
                modal.show();
            },
            error: function() {
                showToast('error', 'Failed to load leave details');
            }
        });
    }

    // Export to Excel
    function exportToExcel() {
        const search = $('#searchEmployee').val().toLowerCase();
        const statusFilter = $('#filterStatus').val();

        let filtered = allLeaves.filter(leave => {
            const matchesSearch = !search || 
                (leave.employeeName && leave.employeeName.toLowerCase().includes(search)) ||
                (leave.employeeCode && leave.employeeCode.toLowerCase().includes(search));
            const matchesStatus = !statusFilter || leave.leaveStatus === statusFilter;
            return matchesSearch && matchesStatus;
        });

        if (filtered.length === 0) {
            showToast('error', 'No data to export');
            return;
        }

        // Create CSV
        let csv = 'Employee Code,Employee Name,Leave Type,Start Date,End Date,Days,Status,Reason\n';
        filtered.forEach(leave => {
            csv += `"${leave.employeeCode}",`;
            csv += `"${leave.employeeName}",`;
            csv += `"${leave.leaveTypeName}",`;
            csv += `"${leave.startDate}",`;
            csv += `"${leave.endDate}",`;
            csv += `"${leave.totalDays}",`;
            csv += `"${leave.leaveStatus}",`;
            csv += `"${leave.reason}"\n`;
        });

        // Download
        const blob = new Blob([csv], { type: 'text/csv' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `all_leaves_${new Date().toISOString().split('T')[0]}.csv`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);

        showToast('success', 'Leaves exported successfully');
    }

    // Utility functions
    function getStatusBadge(status) {
        const badges = {
            'PENDING': '<span class="badge bg-warning text-dark">Pending</span>',
            'APPROVE': '<span class="badge bg-success">Approved</span>',
            'REJECTED': '<span class="badge bg-danger">Rejected</span>',
            'CANCELED': '<span class="badge bg-secondary">Canceled</span>'
        };
        return badges[status] || '<span class="badge bg-secondary">' + status + '</span>';
    }

    function formatDate(dateStr) {
        if (!dateStr) return '--';
        return new Date(dateStr).toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        });
    }

    function escapeHtml(text) {
        if (!text) return '';
        const map = { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;' };
        return String(text).replace(/[&<>"']/g, m => map[m]);
    }
});