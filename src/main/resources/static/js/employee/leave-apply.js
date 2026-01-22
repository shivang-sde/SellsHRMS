$(function() {
  // Load leave types
  $.getJSON(`/api/leave-type/org/${APP.ORG_ID}`, function(types) {
    const select = $('#leaveTypeId');
    select.empty().append('<option value="">Select</option>');
    types.forEach(t => {
      select.append(`<option value="${t.id}">${t.name}</option>`);
    });
  });

  // Submit leave request
  $('#applyLeaveForm').submit(function(e) {
    e.preventDefault();
    const dto = {
      leaveTypeId: +$('#leaveTypeId').val(),
      startDate: $('#startDate').val(),
      startDayBreakdown: $('#startDayBreakdown').val(),
      endDate: $('#endDate').val(),
      endDayBreakdown: $('#endDayBreakdown').val(),
      reason: $('#reason').val(),
      isHalfDay: $('#isHalfDay').is(':checked')
    };

    $.ajax({
      url: '/api/leaves/apply',
      method: 'POST',
      contentType: 'application/json',
      data: JSON.stringify(dto),
      success: res => showToast('success', res.message),
      error: err => showToast('error', err.responseJSON?.message || 'Apply failed')
    });
  });
});
