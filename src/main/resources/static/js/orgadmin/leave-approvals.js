$(document).ready(function() {
  const orgId = window.APP.ORG_ID || $('#globalOrgId').val();
  const approverId = window.APP.EMPLOYEE_ID || $('#globalEmployeeId').val();

  function loadPending() {
    $.getJSON(`/api/leaves/pending`, function(resp) {
      if (!resp.success) return showToast('error', 'Failed to load pending leaves');
      const tbody = $('#pendingLeavesTable tbody').empty();
      resp.data.forEach(l => {
        tbody.append(`
          <tr>
            <td>${l.employeeName}</td>
            <td>${l.leaveTypeName}</td>
            <td>${l.startDate}</td>
            <td>${l.endDate}</td>
            <td>${l.leaveDays}</td>
            <td>${l.reason}</td>
            <td>${l.appliedOn || '-'}</td>
            <td>
              <button class="btn btn-sm btn-success approve-btn" data-id="${l.id}">Approve</button>
              <button class="btn btn-sm btn-danger reject-btn" data-id="${l.id}">Reject</button>
            </td>
          </tr>`);
      });
    });
  }

  loadPending();

  $(document).on('click', '.approve-btn', function() {
    const id = $(this).data('id');
    const remarks = prompt('Add approval remarks (optional):') || '';
    $.ajax({
      url: `/api/leaves/${id}/approve`,
      method: 'POST',
      contentType: 'application/json',
      data: JSON.stringify({ remarks }),
      success: res => { showToast('success', 'Leave approved'); loadPending(); },
      error: () => showToast('error', 'Approval failed')
    });
  });

  $(document).on('click', '.reject-btn', function() {
    const id = $(this).data('id');
    const remarks = prompt('Reason for rejection:') || '';
    $.ajax({
      url: `/api/leaves/${id}/reject`,
      method: 'POST',
      contentType: 'application/json',
      data: JSON.stringify({ remarks }),
      success: res => { showToast('info', 'Leave rejected'); loadPending(); },
      error: () => showToast('error', 'Rejection failed')
    });
  });
});
