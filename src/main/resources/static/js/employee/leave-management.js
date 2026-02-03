$(document).ready(function() {
  let allLeaves = [];

  loadVisibleLeaves();

  // Filters
  $('#btnFilter').on('click', filterLeaves);
  $('#searchEmployee').on('keyup', filterLeaves);
  $('#filterStatus, #filterStartDate, #filterEndDate').on('change', filterLeaves);
  $('#btnExport').on('click', exportToExcel);

  // Load only visible (subordinate) leaves
  function loadVisibleLeaves() {
    $('#leavesTableBody').html(`
      <tr><td colspan="8" class="text-center">
        <div class="spinner-border text-primary"></div>
        <p class="mt-2 text-muted">Loading leaves...</p></td></tr>
    `);

    $.ajax({
      url: '/api/leaves/visible',
      method: 'GET',
      success: function(resp) {
        // controller returns { success: true, data: [...] }
        const data = resp && resp.data ? resp.data : [];
        allLeaves = data;
        updateStatistics(data);
        filterLeaves();
      },
      error: function() {
        $('#leavesTableBody').html(`<tr><td colspan="8" class="text-center text-danger">Failed to load leave data</td></tr>`);
        showToast('error', 'Failed to load leaves');
      }
    });
  }

  function updateStatistics(data) {
    $('#statPending').text(data.filter(l => l.status === 'PENDING').length);
    $('#statApproved').text(data.filter(l => l.status === 'APPROVE').length);
    $('#statRejected').text(data.filter(l => l.status === 'REJECTED').length);
    $('#statCanceled').text(data.filter(l => l.status === 'CANCELED').length);
  }

  function filterLeaves() {
    const search = $('#searchEmployee').val().toLowerCase();
    const statusFilter = $('#filterStatus').val();
    const startDateFilter = $('#filterStartDate').val();
    const endDateFilter = $('#filterEndDate').val();

    let filtered = allLeaves.filter(leave => {
      const matchesSearch = !search ||
        (leave.employeeName && leave.employeeName.toLowerCase().includes(search)) ||
        (leave.employeeCode && leave.employeeCode.toLowerCase().includes(search));

      const matchesStatus = !statusFilter || leave.leaveStatus === statusFilter;
      const matchesStartDate = !startDateFilter || (leave.startDate && leave.startDate >= startDateFilter);
      const matchesEndDate = !endDateFilter || (leave.endDate && leave.endDate <= endDateFilter);

      return matchesSearch && matchesStatus && matchesStartDate && matchesEndDate;
    });

    filtered.sort((a, b) => new Date(b.startDate) - new Date(a.startDate));
    renderLeavesTable(filtered);
  }

    function renderLeavesTable(data) {
      
        console.log("Rendering leaves table with data:", data);

    if (!data || data.length === 0) {
      $('#leavesTableBody').html(`<tr><td colspan="8" class="text-center text-muted">No leave records found</td></tr>`);
      return;
    }

    let html = '';
    data.forEach(leave => {
  html += `
    <tr>
      <td><strong>${escapeHtml(leave.employeeCode || '-')}</strong></td>
      <td>${escapeHtml(leave.employeeName)}</td>
      <td><span class="badge bg-info">${escapeHtml(leave.leaveTypeName)}</span></td>
      <td>${formatDate(leave.startDate)}</td>
      <td>${formatDate(leave.endDate)}</td>
      <td><span class="badge bg-secondary">${leave.leaveDays} days</span></td>
      <td>${getStatusBadge(leave.status)}</td>
      <td>
        <button class="btn btn-sm btn-outline-primary btn-view" data-id="${leave.id}">
          <i class="fas fa-eye"></i> View
        </button>
        ${leave.canApprove && leave.status === 'PENDING' ? `
          <button class="btn btn-sm btn-success btn-approve" data-id="${leave.id}">
            <i class="fas fa-check"></i> Approve
          </button>
          <button class="btn btn-sm btn-danger btn-reject" data-id="${leave.id}">
            <i class="fas fa-times"></i> Reject
          </button>
        ` : ''}
      </td>
    </tr>
  `;
});


    $('#leavesTableBody').html(html);

    // Attach handlers
    $('.btn-view').on('click', function() { viewLeaveDetails($(this).data('id')); });
    $('.btn-approve').on('click', function() { approveLeave($(this).data('id')); });
    $('.btn-reject').on('click', function() { rejectLeave($(this).data('id')); });
  }

  // Fetch leave details (unwrap response wrapper if present)
  function viewLeaveDetails(id) {
    $.ajax({
      url: `/api/leaves/${id}`,
      method: 'GET',
      success: function(resp) {
        const leave = resp && resp.data ? resp.data : resp; // tolerate both shapes
       console.log("Leave details:", leave);
          const html = `
          <div class="row g-3">
            <div class="col-12">
              <h6 class="text-primary"><i class="fas fa-user me-2"></i>Employee Information</h6><hr>
            </div>
            <div class="col-md-6"><strong>Name:</strong> ${escapeHtml(leave.employeeName)}</div>
            <div class="col-md-6"><strong>Code:</strong> ${escapeHtml(leave.employeeCode)}</div>

            <div class="col-12 mt-3"><h6 class="text-primary"><i class="fas fa-calendar me-2"></i>Leave Information</h6><hr></div>
            <div class="col-md-6"><strong>Leave Type:</strong> ${escapeHtml(leave.leaveTypeName)}</div>
            <div class="col-md-6"><strong>Days:</strong> ${leave.leaveDays}</div>
            <div class="col-md-6"><strong>Start:</strong> ${formatDate(leave.startDate)}</div>
            <div class="col-md-6"><strong>End:</strong> ${formatDate(leave.endDate)}</div>
            <div class="col-12 mt-3"><strong>Reason:</strong><p class="mt-2 p-3 bg-light rounded">${escapeHtml(leave.reason)}</p></div>

            ${leave.approvedByName ? `<div class="col-md-6"><strong>Approved By:</strong> ${escapeHtml(leave.approvedByName)}</div>` : ''}
            ${leave.approvedOn ? `<div class="col-md-6"><strong>Approved On:</strong> ${formatDate(leave.approvedOn)}</div>` : ''}
          </div>
        `;
        $('#leaveDetailBody').html(html);
        const modalEl = document.getElementById('leaveDetailModal');
        const modal = new bootstrap.Modal(modalEl);
        modal.show();
      },
      error: function() {
        showToast('error', 'Failed to load leave details');
      }
    });
  }

  // Approve: prompt remarks then POST JSON body { remarks: "..." }.
  function approveLeave(id) {
    const remarks = prompt("Add approval remarks (optional):", "");
    const payload = { remarks: remarks || "" };

    $.ajax({
      url: `/api/leaves/${id}/approve`,
      method: 'POST',
      contentType: 'application/json',
      data: JSON.stringify(payload),
      success: function(resp) {
        showToast('success', (resp && resp.message) ? resp.message : 'Leave approved');
        // reload visible leaves to reflect updated statuses
        loadVisibleLeaves();
      },
      error: function(xhr) {
        const msg = xhr && xhr.responseJSON && xhr.responseJSON.message ? xhr.responseJSON.message : 'Failed to approve leave';
        showToast('error', msg);
      }
    });
  }

  // Reject: prompt remarks then POST JSON body { remarks: "..." }.
  function rejectLeave(id) {
    const remarks = prompt("Add rejection remarks (required):", "");
    if (remarks === null) return; // user cancelled
    if (!remarks.trim()) {
      if (!confirm("You didn't enter remarks. Proceed to reject without remarks?")) return;
    }

    const payload = { remarks: remarks || "" };

    $.ajax({
      url: `/api/leaves/${id}/reject`,
      method: 'POST',
      contentType: 'application/json',
      data: JSON.stringify(payload),
      success: function(resp) {
        showToast('success', (resp && resp.message) ? resp.message : 'Leave rejected');
        loadVisibleLeaves();
      },
      error: function(xhr) {
        const msg = xhr && xhr.responseJSON && xhr.responseJSON.message ? xhr.responseJSON.message : 'Failed to reject leave';
        showToast('error', msg);
      }
    });
    }
    
    function getStatusBadge(status) {
  const map = {
    'PENDING': '<span class="badge bg-warning text-dark">Pending</span>',
    'APPROVE': '<span class="badge bg-success">Approved</span>',
    'APPROVED': '<span class="badge bg-success">Approved</span>', // just in case
    'REJECTED': '<span class="badge bg-danger">Rejected</span>',
    'CANCELED': '<span class="badge bg-secondary">Canceled</span>'
  };
  return map[status] || `<span class="badge bg-secondary">${escapeHtml(status || '--')}</span>`;
}


  // Export CSV uses current cached allLeaves
  function exportToExcel() {
    if (!allLeaves.length) return showToast('error', 'No data to export');
    let csv = 'Employee Code,Employee Name,Leave Type,Start Date,End Date,Days,Status,Reason\n';
    allLeaves.forEach(l => {
      // Make sure to escape quotes inside fields
      const esc = s => `"${String(s || '').replace(/"/g, '""')}"`;
      csv += `${esc(l.employeeCode)},${esc(l.employeeName)},${esc(l.leaveTypeName)},${esc(l.startDate)},${esc(l.endDate)},${esc(l.totalDays)},${esc(l.leaveStatus)},${esc(l.reason)}\n`;
    });

    const blob = new Blob([csv], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `subordinate_leaves_${new Date().toISOString().split('T')[0]}.csv`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
    showToast('success', 'Leaves exported successfully');
  }

  // Helpers
  function getStatusBadge(status) {
    const map = {
      'PENDING': '<span class="badge bg-warning text-dark">Pending</span>',
      'APPROVE': '<span class="badge bg-success">Approved</span>',
      'REJECTED': '<span class="badge bg-danger">Rejected</span>',
      'CANCELED': '<span class="badge bg-secondary">Canceled</span>'
    };
    return map[status] || `<span class="badge bg-secondary">${escapeHtml(status)}</span>`;
  }

  function formatDate(dateStr) {
    if (!dateStr) return '--';
    // handle local date strings (yyyy-MM-dd) and ISO strings
    const d = new Date(dateStr);
    if (isNaN(d)) return dateStr;
    return d.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
  }

  function escapeHtml(text) {
    if (text === null || text === undefined) return '';
    return String(text).replace(/[&<>"']/g, m => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#039;'}[m]));
  }



});

