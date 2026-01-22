$(document).ready(function() {
  let leaveTypes = [];
  let allLeaves = [];

  // Initialize page
  init();

  function init() {
    loadLeaveBalances();
    loadLeaveTypes();
    loadMyLeaves();
    loadLeaveStats();
    setupEventListeners();
  }

  function setupEventListeners() {
    // Apply leave form submission
    $('#applyLeaveForm').on('submit', handleApplyLeave);
    
    // Edit leave form submission
    $('#editLeaveForm').on('submit', handleEditLeave);
    
    // Date change listeners for calculating days
    $('#startDate, #endDate, #startDayBreakdown, #endDayBreakdown').on('change', calculateLeaveDays);
    
    // Status filter
    $('#statusFilter').on('change', filterLeavesByStatus);
    
    // Leave type selection
    $('#leaveTypeSelect').on('change', showLeaveTypeInfo);

    // Set minimum date to today
    const today = new Date().toISOString().split('T')[0];
    $('#startDate, #endDate, #editStartDate, #editEndDate').attr('min', today);
  }

  // Load leave balances
  function loadLeaveBalances() {
    $.ajax({
      url: '/api/leaves/balances',
      method: 'GET',
      success: function(response) {
        if (response.success) {
          displayLeaveBalances(response.data);
        }
      },
      error: function(xhr) {
        console.error('Error loading leave balances:', xhr);
      }
    });
  }

  function displayLeaveBalances(balances) {
  const container = $('#leaveBalanceCards');
  container.empty();

  if (balances.length === 0) {
    container.append(`
      <div class="col-12">
        <div class="alert alert-info">
          No leave balances found. Please contact HR.
        </div>
      </div>
    `);
    return;
  }

  balances.forEach(balance => {
    const remaining = balance.closingBalance - balance.availed;

    const card = $(`
      <div class="col-md-3 mb-4 ">
        <div class="card border-0 shadow-sm leave-balance-card h-100 selectable-leave-card" 
             data-leave-type-id="${balance.leaveTypeId}"
             data-leave-type-name="${balance.leaveTypeName}">
          <div class="card-body">
            <div class="d-flex justify-content-between align-items-start mb-2">
              <h6 class="mb-0">${balance.leaveTypeName}</h6>
              <span class="badge ${balance.isPaid ? 'bg-success' : 'bg-secondary'}">
                ${balance.isPaid ? 'Paid' : 'Unpaid'}
              </span>
            </div>
            <div class="d-flex justify-content-between align-items-end">
              <div>
                <small class="text-muted">Available</small>
                <h3 class="mb-0 text-primary">${remaining}</h3>
              </div>
              <div class="text-end">
                <small class="text-muted">Used: ${balance.availed}</small><br>
                <small class="text-muted">Total: ${balance.closingBalance}</small>
              </div>
            </div>
          </div>
        </div>
      </div>
    `);

    // 💥 click to open Apply Leave Modal
    card.find('.selectable-leave-card').on('click', function() {
      const leaveTypeId = $(this).data('leave-type-id');
      const leaveTypeName = $(this).data('leave-type-name');

      // Open the modal
      $('#applyLeaveModal').modal('show');

      // Preselect the leave type in dropdown
      $('#leaveTypeSelect').val(leaveTypeId).trigger('change');

      // Optionally show a toast or info text
      showToast('info', `Applying for ${leaveTypeName}`);
    });

    container.append(card);
  });
}


  // Load leave types for dropdown
  function loadLeaveTypes() {
    const orgId = window.APP.ORG_ID;
    $.ajax({
      url: `/api/leave-type/org/${orgId}`,
      method: 'GET',
      success: function(response) {
        leaveTypes = response;
        populateLeaveTypeDropdown();
      },
      error: function(xhr) {
        console.error('Error loading leave types:', xhr);
      }
    });
  }

  function populateLeaveTypeDropdown() {
    const select = $('#leaveTypeSelect');
    select.empty().append('<option value="">Select Leave Type</option>');
    
    leaveTypes.forEach(type => {
      if (type.visibleToEmployees) {
        select.append(`<option value="${type.id}" 
          data-annual-limit="${type.annualLimit}"
          data-allow-half-day="${type.allowHalfDay}"
          data-max-consecutive="${type.maxConsecutiveDays || 'N/A'}">
          ${type.name}
        </option>`);
      }
    });
  }

  function showLeaveTypeInfo() {
    const selected = $('#leaveTypeSelect option:selected');
    const annualLimit = selected.data('annual-limit');
    const maxConsecutive = selected.data('max-consecutive');
    const allowHalfDay = selected.data('allow-half-day');
    
    if (selected.val()) {
      let info = `Annual Limit: ${annualLimit} days`;
      if (maxConsecutive !== 'N/A') {
        info += ` | Max Consecutive: ${maxConsecutive} days`;
      }
      if (allowHalfDay) {
        info += ` | Half day allowed`;
      }
      $('#leaveTypeInfo').text(info);
    } else {
      $('#leaveTypeInfo').text('');
    }
  }

  // Calculate leave days
  function calculateLeaveDays() {
    const startDate = $('#startDate').val();
    const endDate = $('#endDate').val();
    const startBreakdown = $('#startDayBreakdown').val();
    const endBreakdown = $('#endDayBreakdown').val();

    if (startDate && endDate) {
      const start = new Date(startDate);
      const end = new Date(endDate);
      
      if (end < start) {
        $('#calculatedDays').val('Invalid date range');
        return;
      }

      let days = Math.floor((end - start) / (1000 * 60 * 60 * 24)) + 1;
      
      // Adjust for half days
      if (startBreakdown !== 'FULL_DAY') {
        days -= 0.5;
      }
      if (endBreakdown !== 'FULL_DAY' && start.getTime() !== end.getTime()) {
        days -= 0.5;
      }

      $('#calculatedDays').val(days + (days === 1 ? ' day' : ' days'));
    }
  }

  // Load employee's leaves
  function loadMyLeaves() {
    $.ajax({
      url: '/api/leaves/my',
      method: 'GET',
      success: function(response) {
        console.log('My Leaves:', response);
        if (response.success) {
          allLeaves = response.data;
          displayLeaves(allLeaves);
        }
      },
      error: function(xhr) {
        $('#leavesTableBody').html(`
          <tr>
            <td colspan="7" class="text-center text-danger">
              Error loading leaves. Please try again.
            </td>
          </tr>
        `);
      }
    });
  }

  function displayLeaves(leaves) {
    const tbody = $('#leavesTableBody');
    tbody.empty();

    if (leaves.length === 0) {
      tbody.append(`
        <tr>
          <td colspan="7" class="text-center py-4">
            <i class="fa fa-inbox fa-3x text-muted mb-3"></i>
            <p class="text-muted">No leave applications found</p>
          </td>
        </tr>
      `);
      return;
    }

    leaves.forEach(leave => {
      const statusBadge = getStatusBadge(leave.status);
      const actions = getActionButtons(leave);
      
      const row = `
        <tr>
          <td>
            <strong>${leave.leaveTypeName}</strong>
          </td>
          <td>
            <small class="text-muted">
              ${formatDate(leave.startDate)} 
              ${leave.startDayBreakdown ? `(${formatDayBreakdown(leave.startDayBreakdown)})` : ''}
              <br>to<br>
              ${formatDate(leave.endDate)}
              ${leave.endDayBreakdown ? `(${formatDayBreakdown(leave.endDayBreakdown)})` : ''}
            </small>
          </td>
          <td><strong>${leave.leaveDays}</strong></td>
          <td>${formatDate(leave.appliedOn)}</td>
          <td>${statusBadge}</td>
          <td>
            ${leave.approverName || '-'}
            ${leave.approverRemarks ? `<br><small class="text-muted">${leave.approverRemarks}</small>` : ''}
          </td>
          <td>${actions}</td>
        </tr>
      `;
      tbody.append(row);
    });
  }

  function getStatusBadge(status) {
    const badges = {
      'PENDING': '<span class="badge badge-status bg-warning text-dark">Pending</span>',
      'APPROVE': '<span class="badge badge-status bg-success">Approved</span>',
      'REJECTED': '<span class="badge badge-status bg-danger">Rejected</span>',
      'CANCELED': '<span class="badge badge-status bg-secondary">Cancelled</span>'
    };
    return badges[status] || status;
  }

  function getActionButtons(leave) {
    let buttons = `
      <button class="btn btn-sm btn-outline-primary action-btn" onclick="viewLeaveDetails(${leave.id})">
        <i class="fa fa-eye"></i>
      </button>
    `;

    if (leave.status === 'PENDING') {
      buttons += `
        <button class="btn btn-sm btn-outline-info action-btn ms-1" onclick="editLeave(${leave.id})">
          <i class="fa fa-edit"></i>
        </button>
        <button class="btn btn-sm btn-outline-danger action-btn ms-1" onclick="cancelLeave(${leave.id})">
          <i class="fa fa-times"></i>
        </button>
      `;
    }

    return buttons;
  }

  // Apply leave
  function handleApplyLeave(e) {
    e.preventDefault();
    
    const formData = {
      leaveTypeId: parseInt($('#leaveTypeSelect').val()),
      startDate: $('#startDate').val(),
      startDayBreakdown: $('#startDayBreakdown').val(),
      endDate: $('#endDate').val(),
      endDayBreakdown: $('#endDayBreakdown').val(),
      reason: $('textarea[name="reason"]').val()
    };

    if (new Date(formData.endDate) < new Date(formData.startDate)) {
      showToast('error', 'End date must be after start date');
      return;
    }

    $.ajax({
      url: '/api/leaves/apply',
      method: 'POST',
      contentType: 'application/json',
      data: JSON.stringify(formData),
      success: function(response) {
        if (response.success) {
          showToast('success', response.message);
          $('#applyLeaveModal').modal('hide');
          $('#applyLeaveForm')[0].reset();
          loadMyLeaves();
          loadLeaveBalances();
          loadLeaveStats();
        }
      },
      error: function(xhr) {
        const error = xhr.responseJSON?.message || 'Failed to apply leave';
        showToast('error', error);
      }
    });
  }

  // Edit leave
  window.editLeave = function(leaveId) {
    const leave = allLeaves.find(l => l.id === leaveId);
    if (!leave) return;

    $('#editLeaveId').val(leave.id);
    $('#editLeaveType').val(leave.leaveTypeName);
    $('#editStartDate').val(leave.startDate);
    $('#editStartDayBreakdown').val(leave.startDayBreakdown || 'FULL_DAY');
    $('#editEndDate').val(leave.endDate);
    $('#editEndDayBreakdown').val(leave.endDayBreakdown || 'FULL_DAY');
    $('#editReason').val(leave.reason);

    $('#editLeaveModal').modal('show');
  };

  function handleEditLeave(e) {
    e.preventDefault();
    
    const leaveId = $('#editLeaveId').val();
    const formData = {
      leaveTypeId: allLeaves.find(l => l.id == leaveId).leaveTypeId,
      startDate: $('#editStartDate').val(),
      startDayBreakdown: $('#editStartDayBreakdown').val(),
      endDate: $('#editEndDate').val(),
      endDayBreakdown: $('#editEndDayBreakdown').val(),
      reason: $('#editReason').val()
    };

    if (new Date(formData.endDate) < new Date(formData.startDate)) {
      showToast('error', 'End date must be after start date');
      return;
    }

    $.ajax({
      url: `/api/leaves/${leaveId}/update`,
      method: 'PATCH',
      contentType: 'application/json',
      data: JSON.stringify(formData),
      success: function(response) {
        if (response.success) {
          showToast('success', response.message);
          $('#editLeaveModal').modal('hide');
          loadMyLeaves();
        }
      },
      error: function(xhr) {
        const error = xhr.responseJSON?.message || 'Failed to update leave';
        showToast('error', error);
      }
    });
  }

  // Cancel leave
  window.cancelLeave = function(leaveId) {
    if (!confirm('Are you sure you want to cancel this leave application?')) {
      return;
    }

    $.ajax({
      url: `/api/leaves/${leaveId}/cancel`,
      method: 'DELETE',
      success: function(response) {
        if (response.success) {
          showToast('success', response.message);
          loadMyLeaves();
          loadLeaveStats();
        }
      },
      error: function(xhr) {
        const error = xhr.responseJSON?.message || 'Failed to cancel leave';
        showToast('error', error);
      }
    });
  };

  // View leave details
  window.viewLeaveDetails = function(leaveId) {
    const leave = allLeaves.find(l => l.id === leaveId);
    if (!leave) return;

    $('#viewLeaveType').text(leave.leaveTypeName);
    $('#viewStatus').html(getStatusBadge(leave.status));
    $('#viewStartDate').text(formatDate(leave.startDate) + (leave.startDayBreakdown ? ` (${formatDayBreakdown(leave.startDayBreakdown)})` : ''));
    $('#viewEndDate').text(formatDate(leave.endDate) + (leave.endDayBreakdown ? ` (${formatDayBreakdown(leave.endDayBreakdown)})` : ''));
    $('#viewLeaveDays').text(leave.leaveDays);
    $('#viewAppliedOn').text(formatDate(leave.appliedOn));
    $('#viewReason').text(leave.reason || '-');

    if (leave.approverName) {
      $('#viewApproverName').text(leave.approverName);
      $('#viewApprovedOn').text(leave.approvedOn ? formatDate(leave.approvedOn) : '-');
      $('#viewApproverRemarks').text(leave.approverRemarks || '-');
      $('#approvalSection').show();
    } else {
      $('#approvalSection').hide();
    }

    $('#viewLeaveModal').modal('show');
  };

  // Filter leaves by status
  function filterLeavesByStatus() {
    const status = $('#statusFilter').val();
    if (status === '') {
      displayLeaves(allLeaves);
    } else {
      const filtered = allLeaves.filter(l => l.status === status);
      displayLeaves(filtered);
    }
  }

  // Load leave statistics
  function loadLeaveStats() {
    $.ajax({
      url: '/api/leaves/my/stats',
      method: 'GET',
      success: function(response) {
        console.log('Leave Stats:', response);
        if (response.success) {
          const stats = response.data.statusStats;
          $('#pendingCount').text(stats.PENDING || 0);
          $('#approvedCount').text(stats.APPROVE || 0);
          $('#rejectedCount').text(stats.REJECTED || 0);
          $('#cancelledCount').text(stats.CANCELED || 0);
        }
      },
      error: function(xhr) {
        console.error('Error loading stats:', xhr);
      }
    });
  }

  // Helper functions
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
});