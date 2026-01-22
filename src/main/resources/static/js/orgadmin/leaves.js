$(document).ready(function() {


  const orgId = window.APP?.ORG_ID || $('#globalOrgId').val();
  const empId = window.APP?.EMPLOYEE_ID || $('#globalEmpId').val();

  console.log("Organisation ID:", orgId);
  console.log("Employee ID:", empId);

  let allLeaves = [];
  let pendingLeaves = [];
  let approvedLeaves = [];
  let rejectedLeaves = [];

  // Initialize
  init();

  function init() {
    loadAllData();
    setupEventListeners();
  }

  function setupEventListeners() {
    // Form submissions
    $('#approveForm').on('submit', handleApproveLeave);
    $('#rejectForm').on('submit', handleRejectLeave);

    // Search filters
    $('#pendingSearch').on('keyup', function() {
      filterTable('pending', $(this).val());
    });
    $('#approvedSearch').on('keyup', function() {
      filterTable('approved', $(this).val());
    });
    $('#allSearch').on('keyup', function() {
      filterTable('all', $(this).val());
    });

    // Status filter for all leaves
    $('#allStatusFilter').on('change', function() {
      const status = $(this).val();
      if (status === '') {
        displayAllLeaves(allLeaves);
      } else {
        const filtered = allLeaves.filter(l => l.status === status);
        displayAllLeaves(filtered);
      }
    });

    // Date filter for approved leaves
    $('#approvedDateFilter').on('change', function() {
      const date = $(this).val();
      if (date) {
        const filtered = approvedLeaves.filter(l => l.startDate === date || l.endDate === date);
        displayApprovedLeaves(filtered);
      } else {
        displayApprovedLeaves(approvedLeaves);
      }
    });

    // Tab change events
    $('button[data-bs-toggle="tab"]').on('shown.bs.tab', function(e) {
      const target = $(e.target).data('bs-target');
      if (target === '#pending') loadPendingLeaves();
      if (target === '#approved') loadApprovedLeaves();
      if (target === '#rejected') loadRejectedLeaves();
      if (target === '#all') loadAllLeaves();
    });
  }

  function loadAllData() {
    loadPendingLeaves();
  }

  // Load pending leaves
  function loadPendingLeaves() {
    $.ajax({
      url: '/api/leaves/pending',
      method: 'GET',
      success: function(response) {
        if (response.success) {
          pendingLeaves = response.data;
          displayPendingLeaves(pendingLeaves);
          $('#pendingBadge').text(pendingLeaves.length);
        }
      },
      error: function(xhr) {
        $('#pendingTableBody').html(`
          <tr>
            <td colspan="7" class="text-center text-danger">Error loading pending leaves</td>
          </tr>
        `);
      }
    });
  }

  function displayPendingLeaves(leaves) {
    const tbody = $('#pendingTableBody');
    tbody.empty();

    if (leaves.length === 0) {
      tbody.append(`
        <tr>
          <td colspan="7" class="text-center py-4">
            <i class="fa fa-check-circle fa-3x text-success mb-3"></i>
            <p class="text-muted">No pending approvals</p>
          </td>
        </tr>
      `);
      return;
    }

    leaves.forEach(leave => {
      const row = `
        <tr data-employee="${leave.employeeName.toLowerCase()}">
          <td>
            <strong>${leave.employeeName}</strong>
          </td>
          <td>${leave.leaveTypeName}</td>
          <td>
            <small>
              ${formatDate(leave.startDate)} 
              ${leave.startDayBreakdown ? `(${formatDayBreakdown(leave.startDayBreakdown)})` : ''}<br>
              to<br>
              ${formatDate(leave.endDate)}
              ${leave.endDayBreakdown ? `(${formatDayBreakdown(leave.endDayBreakdown)})` : ''}
            </small>
          </td>
          <td><strong>${leave.leaveDays}</strong></td>
          <td>${formatDate(leave.appliedOn)}</td>
          <td>
            <small>${truncate(leave.reason, 50)}</small>
          </td>
          <td>
            <button class="btn btn-sm btn-success action-btn" onclick="openApproveModal(${leave.id})">
              <i class="fa fa-check"></i> Approve
            </button>
            <button class="btn btn-sm btn-danger action-btn ms-1" onclick="openRejectModal(${leave.id})">
              <i class="fa fa-times"></i> Reject
            </button>
            <button class="btn btn-sm btn-outline-primary action-btn ms-1" onclick="viewLeaveDetails(${leave.id})">
              <i class="fa fa-eye"></i>
            </button>
          </td>
        </tr>
      `;
      tbody.append(row);
    });
  }

  // Load approved leaves
  function loadApprovedLeaves() {
    $.ajax({
      url: '/api/leaves/status?status=APPROVE',
      method: 'GET',
      success: function(response) {
        if (response.success) {
          approvedLeaves = response.data;
          displayApprovedLeaves(approvedLeaves);
        }
      },
      error: function(xhr) {
        $('#approvedTableBody').html(`
          <tr>
            <td colspan="7" class="text-center text-danger">Error loading approved leaves</td>
          </tr>
        `);
      }
    });
  }

  function displayApprovedLeaves(leaves) {
    const tbody = $('#approvedTableBody');
    tbody.empty();

    if (leaves.length === 0) {
      tbody.append(`
        <tr>
          <td colspan="7" class="text-center py-4">
            <p class="text-muted">No approved leaves found</p>
          </td>
        </tr>
      `);
      return;
    }

    leaves.forEach(leave => {
      const row = `
        <tr data-employee="${leave.employeeName.toLowerCase()}">
          <td><strong>${leave.employeeName}</strong></td>
          <td>${leave.leaveTypeName}</td>
          <td>
            <small>
              ${formatDate(leave.startDate)} to ${formatDate(leave.endDate)}
            </small>
          </td>
          <td><strong>${leave.leaveDays}</strong></td>
          <td>${leave.approverName || '-'}</td>
          <td>${formatDate(leave.approvedOn)}</td>
          <td>
            <button class="btn btn-sm btn-outline-primary action-btn" onclick="viewLeaveDetails(${leave.id})">
              <i class="fa fa-eye"></i> View
            </button>
          </td>
        </tr>
      `;
      tbody.append(row);
    });
  }

  // Load rejected leaves
  function loadRejectedLeaves() {
    $.ajax({
      url: '/api/leaves/status?status=REJECTED',
      method: 'GET',
      success: function(response) {
        if (response.success) {
          rejectedLeaves = response.data;
          displayRejectedLeaves(rejectedLeaves);
        }
      },
      error: function(xhr) {
        $('#rejectedTableBody').html(`
          <tr>
            <td colspan="7" class="text-center text-danger">Error loading rejected leaves</td>
          </tr>
        `);
      }
    });
  }

  function displayRejectedLeaves(leaves) {
    const tbody = $('#rejectedTableBody');
    tbody.empty();

    if (leaves.length === 0) {
      tbody.append(`
        <tr>
          <td colspan="7" class="text-center py-4">
            <p class="text-muted">No rejected leaves found</p>
          </td>
        </tr>
      `);
      return;
    }

    leaves.forEach(leave => {
      const row = `
        <tr>
          <td><strong>${leave.employeeName}</strong></td>
          <td>${leave.leaveTypeName}</td>
          <td>
            <small>
              ${formatDate(leave.startDate)} to ${formatDate(leave.endDate)}
            </small>
          </td>
          <td><strong>${leave.leaveDays}</strong></td>
          <td>${leave.approverName || '-'}</td>
          <td><small>${leave.approverRemarks || '-'}</small></td>
          <td>
            <button class="btn btn-sm btn-outline-primary action-btn" onclick="viewLeaveDetails(${leave.id})">
              <i class="fa fa-eye"></i> View
            </button>
          </td>
        </tr>
      `;
      tbody.append(row);
    });
  }

  // Load all leaves
  function loadAllLeaves() {
    $.ajax({
      url: '/api/leaves/all',
      method: 'GET',
      success: function(response) {
        if (response.success) {
          allLeaves = response.data;
          displayAllLeaves(allLeaves);
        }
      },
      error: function(xhr) {
        $('#allTableBody').html(`
          <tr>
            <td colspan="7" class="text-center text-danger">Error loading leaves</td>
          </tr>
        `);
      }
    });
  }

  function displayAllLeaves(leaves) {
    const tbody = $('#allTableBody');
    tbody.empty();

    if (leaves.length === 0) {
      tbody.append(`
        <tr>
          <td colspan="7" class="text-center py-4">
            <p class="text-muted">No leave applications found</p>
          </td>
        </tr>
      `);
      return;
    }

    leaves.forEach(leave => {
      const statusBadge = getStatusBadge(leave.status);
      const row = `
        <tr data-employee="${leave.employeeName.toLowerCase()}">
          <td><strong>${leave.employeeName}</strong></td>
          <td>${leave.leaveTypeName}</td>
          <td>
            <small>
              ${formatDate(leave.startDate)} to ${formatDate(leave.endDate)}
            </small>
          </td>
          <td><strong>${leave.leaveDays}</strong></td>
          <td>${statusBadge}</td>
          <td>${formatDate(leave.appliedOn)}</td>
          <td>
            <button class="btn btn-sm btn-outline-primary action-btn" onclick="viewLeaveDetails(${leave.id})">
              <i class="fa fa-eye"></i>
            </button>
          </td>
        </tr>
      `;
      tbody.append(row);
    });
  }

  // Approve leave
  window.openApproveModal = function(leaveId) {


    if(!empId) {
     showToast('error', 'approver needs employee ID to approve leave, contact admin or support');
     return;
    }
    const leave = pendingLeaves.find(l => l.id === leaveId);
    if (!leave) return;

       if(leave.employeeId === empId) {
      showToast('error', 'You cannot approve your own leave request');
      return;
    }

    $('#approveLeaveId').val(leave.id);
    $('#approveEmployeeName').val(leave.employeeName);
    $('#approveLeaveType').val(leave.leaveTypeName);
    $('#approveDuration').val(`${formatDate(leave.startDate)} to ${formatDate(leave.endDate)} (${leave.leaveDays} days)`);
    $('#approveEmployeeReason').val(leave.reason);
    
    $('#approveModal').modal('show');
  };

  function handleApproveLeave(e) {
    e.preventDefault();
    
    const leaveId = $('#approveLeaveId').val();
    const remarks = $('#approveForm textarea[name="remarks"]').val();

    $.ajax({
      url: `/api/leaves/${leaveId}/approve`,
      method: 'POST',
      contentType: 'application/json',
      data: JSON.stringify({ remarks: remarks, approverId : empId, orgId : orgId }),
      success: function(response) {
        if (response.success) {
          showToast('success', response.message);
          $('#approveModal').modal('hide');
          $('#approveForm')[0].reset();
          loadPendingLeaves();
          
          // Reload current tab if it's not pending
          const activeTab = $('.nav-link.active').data('bs-target');
          if (activeTab === '#approved') loadApprovedLeaves();
          if (activeTab === '#all') loadAllLeaves();
        }
      },
      error: function(xhr) {
        const error = xhr.responseJSON?.message || 'Failed to approve leave';
        showToast('error', error);
      }
    });
  }

  // Reject leave
  window.openRejectModal = function(leaveId) {
       
    if(!empId) {
     showToast('error', 'approver needs employee ID to reject leave, contact admin or support');
     return;
    }
    const leave = pendingLeaves.find(l => l.id === leaveId);
    if (!leave) return;

     if(leave.employeeId === empId) {
      showToast('error', 'You cannot reject your own leave request');
      return;
    }


    $('#rejectLeaveId').val(leave.id);
    $('#rejectEmployeeName').val(leave.employeeName);
    $('#rejectLeaveType').val(leave.leaveTypeName);
    $('#rejectDuration').val(`${formatDate(leave.startDate)} to ${formatDate(leave.endDate)} (${leave.leaveDays} days)`);
    $('#rejectEmployeeReason').val(leave.reason);
    
    $('#rejectModal').modal('show');
  };

  function handleRejectLeave(e) {
    e.preventDefault();
    
    const leaveId = $('#rejectLeaveId').val();
    const remarks = $('#rejectForm textarea[name="remarks"]').val();

    if (!remarks.trim()) {
      showToast('error', 'Please provide a rejection reason');
      return;
    }

    $.ajax({
      url: `/api/leaves/${leaveId}/reject`,
      method: 'POST',
      contentType: 'application/json',
      data: JSON.stringify({ remarks: remarks, approverId : empId, orgId : orgId }),
      success: function(response) {
        if (response.success) {
          showToast('success', response.message);
          $('#rejectModal').modal('hide');
          $('#rejectForm')[0].reset();
          loadPendingLeaves();
          
          // Reload current tab if it's not pending
          const activeTab = $('.nav-link.active').data('bs-target');
          if (activeTab === '#rejected') loadRejectedLeaves();
          if (activeTab === '#all') loadAllLeaves();
        }
      },
      error: function(xhr) {
        const error = xhr.responseJSON?.message || 'Failed to reject leave';
        showToast('error', error);
      }
    });
  }

  // View leave details
  window.viewLeaveDetails = function(leaveId) {
    // Find leave in any of the arrays
    let leave = pendingLeaves.find(l => l.id === leaveId) ||
                approvedLeaves.find(l => l.id === leaveId) ||
                rejectedLeaves.find(l => l.id === leaveId) ||
                allLeaves.find(l => l.id === leaveId);
    
    if (!leave) return;

    $('#detailEmployeeName').text(leave.employeeName);
    $('#detailStatus').html(getStatusBadge(leave.status));
    $('#detailLeaveType').text(leave.leaveTypeName);
    $('#detailLeaveDays').text(leave.leaveDays);
    $('#detailStartDate').text(formatDate(leave.startDate) + (leave.startDayBreakdown ? ` (${formatDayBreakdown(leave.startDayBreakdown)})` : ''));
    $('#detailEndDate').text(formatDate(leave.endDate) + (leave.endDayBreakdown ? ` (${formatDayBreakdown(leave.endDayBreakdown)})` : ''));
    $('#detailReason').text(leave.reason || '-');
    $('#detailAppliedOn').text(formatDate(leave.appliedOn));

    if (leave.approverName) {
      $('#detailApproverName').text(leave.approverName);
      $('#detailApprovedOn').text(leave.approvedOn ? formatDate(leave.approvedOn) : '-');
      $('#detailApproverRemarks').text(leave.approverRemarks || 'No remarks');
      $('#approvalDetailsSection').show();
    } else {
      $('#approvalDetailsSection').hide();
    }

    $('#viewDetailsModal').modal('show');
  };

  // Filter table by search
  function filterTable(type, searchTerm) {
    const tbody = $(`#${type}TableBody`);
    const rows = tbody.find('tr[data-employee]');
    
    if (!searchTerm) {
      rows.show();
      return;
    }

    searchTerm = searchTerm.toLowerCase();
    rows.each(function() {
      const employeeName = $(this).data('employee');
      if (employeeName.includes(searchTerm)) {
        $(this).show();
      } else {
        $(this).hide();
      }
    });
  }

  // Helper functions
  function getStatusBadge(status) {
    const badges = {
      'PENDING': '<span class="badge bg-warning text-dark">Pending</span>',
      'APPROVE': '<span class="badge bg-success">Approved</span>',
      'REJECTED': '<span class="badge bg-danger">Rejected</span>',
      'CANCELED': '<span class="badge bg-secondary">Cancelled</span>'
    };
    return badges[status] || status;
  }

  function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-IN', { 
      day: '2-digit', 
      month: 'short', 
      year: 'numeric' 
    });
  }

  function formatDayBreakdown(breakdown) {
    const formats = {
      'FULL_DAY': 'Full Day',
      'FIRST_HALF': 'First Half',
      'SECOND_HALF': 'Second Half'
    };
    return formats[breakdown] || breakdown;
  }

  function truncate(str, length) {
    if (!str) return '-';
    return str.length > length ? str.substring(0, length) + '...' : str;
  }
});