$(function() {
  $.getJSON('/api/leaves/my', function(resp) {
    const tbody = $('#leaveTable tbody').empty();
    resp.data.forEach(l => {
      const badge = l.status === 'APPROVE' ? 'success'
                   : l.status === 'REJECTED' ? 'danger'
                   : l.status === 'PENDING' ? 'warning' : 'secondary';
      tbody.append(`
        <tr>
          <td>${l.leaveTypeName}</td>
          <td>${l.startDate}</td>
          <td>${l.endDate}</td>
          <td>${l.leaveDays}</td>
          <td><span class="badge bg-${badge}">${l.status}</span></td>
          <td>${l.approverName || '-'}</td>
          <td>${l.approverRemarks || '-'}</td>
          <td>${l.status === 'PENDING'
            ? `<button class="btn btn-sm btn-outline-danger cancel-btn" data-id="${l.id}">Cancel</button>`
            : ''}</td>
        </tr>`);
    });

    $('.cancel-btn').click(function() {
      const id = $(this).data('id');
      $.ajax({ url: `/api/leaves/${id}/cancel`, type: 'DELETE' })
        .done(() => { showToast('success', 'Cancelled'); location.reload(); });
    });
  });
});
