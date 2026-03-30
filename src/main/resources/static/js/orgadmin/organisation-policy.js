$(document).ready(function () {
  const orgId = window.APP?.ORG_ID || $('#globalOrgId').val();
  let policyData = null;
  const $form = $('#organisationPolicyForm');
  const $saveBtn = $('#savePolicyBtn');
  const $editBtn = $('#editPolicyBtn');
  const $createBtn = $('#createPolicyBtn');
  const $alert = $('#noPolicyAlert');

  loadOrganisationPolicy();

  function loadOrganisationPolicy() {
    $.ajax({
      url: `/api/organisation/${orgId}/policy`,
      method: 'GET',
      success: function (data) {
        if (!data || !data.id) return showNoPolicyState();
        console.log('Organisation Policy Data:', data);
        policyData = data;
        fillForm(data);
        disableForm(true);
        $alert.addClass('d-none');
        $editBtn.removeClass('d-none');
      },
      error: showNoPolicyState
    });
  }

  function fillForm(d) {
    $('#policyId').val(d.id);
    $('#financialYearStartMonth').val(d.financialYearStartMonth);
    $('#financialYearStartDay').val(d.financialYearStartDay);
    $('#leaveYearStartMonth').val(d.leaveYearStartMonth);
    $('#leaveYearStartDay').val(d.leaveYearStartDay);
    $('#standardDailyHours').val(d.standardDailyHours);
    $('#weeklyHours').val(d.weeklyHours);
    $('#minMonthlyHours').val(d.minMonthlyHours);
    $('#autoPunchOutTime').val(d.autoPunchOutTime);
    setWeekOffDays(d.weekOffDays);
    $('#salaryCycleStartDay').val(d.salaryCycleStartDay);
    // $('#cycleDuration').val(d.cycleDuration);
    $('#payslipGenerationOffsetDays').val(d.payslipGenerationOffsetDays);
    $('#officeStartTime').val(d.officeStart);
    $('#officeClosedTime').val(d.officeClosed);
    $('#lateGraceMinutes').val(d.lateGraceMinutes);
    $('#earlyOutGraceMinutes').val(d.earlyOutGraceMinutes);
    $('#overtimeAllowed').prop('checked', d.overtimeAllowed);
    $('#overtimeMultiplier').val(d.overtimeMultiplier);
    $('#carryForwardEnabled').prop('checked', d.carryForwardEnabled);
    $('#encashmentEnabled').prop('checked', d.encashmentEnabled);
    $('#additionalNotes').val(d.additionalNotes);

    // last updated info
    const time = d.updatedAt || d.createdAt;
    if (time) {
      const formatted = new Date(time).toLocaleString();
      $('#lastUpdatedTime').text(formatted);
      $('#lastUpdatedInfo').show();
    } else {
      $('#lastUpdatedInfo').hide();
    }
  }

  function disableForm(state) {
    if (state) {
      $form.addClass('form-disabled-mode');
    } else {
      $form.removeClass('form-disabled-mode');
    }

    $form.find('input, select, textarea').prop('disabled', state);
    state ? $saveBtn.addClass('d-none') : $saveBtn.removeClass('d-none');
    state ? $editBtn.removeClass('d-none') : $editBtn.addClass('d-none');

    // conditional re-disable
    if (!state) {
      handleOvertimeChange();
    }
  }

  $('#overtimeAllowed').on('change', handleOvertimeChange);

  function handleOvertimeChange() {
    const isChecked = $('#overtimeAllowed').is(':checked');
    $('#overtimeMultiplier').prop('disabled', !isChecked);
    if (!isChecked) {
      $('#overtimeMultiplier').val('');
    }
  }

  function showNoPolicyState() {
    $alert.removeClass('d-none');
    disableForm(true);
    $editBtn.addClass('d-none');
    $saveBtn.addClass('d-none');
  }

  $editBtn.on('click', () => disableForm(false));
  $createBtn.on('click', () => {
    $alert.addClass('d-none');
    disableForm(false);
    $form[0].reset();
    policyData = {};
  });

  $form.on('submit', function (e) {
    e.preventDefault();

    if (!validatePolicy()) {
      return;
    }

    Swal.fire({
      title: 'Update Policy?',
      text: 'Updating this policy will affect payroll, attendance, and leave calculations. Proceed?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#0284c7',
      cancelButtonColor: '#94a3b8',
      confirmButtonText: 'Yes, update policy',
      cancelButtonText: 'Cancel'
    }).then((result) => {
      if (result.isConfirmed) {
        savePolicy();
      }
    });
  });

  function savePolicy() {
    const updated = {
      ...policyData,

      financialYearStartMonth: parseInt($('#financialYearStartMonth').val()),
      financialYearStartDay: parseInt($('#financialYearStartDay').val()),

      leaveYearStartMonth: parseInt($('#leaveYearStartMonth').val()),
      leaveYearStartDay: parseInt($('#leaveYearStartDay').val()),

      officeStart: $('#officeStartTime').val(),
      officeClosed: $('#officeClosedTime').val(),

      standardDailyHours: parseFloat($('#standardDailyHours').val()),
      weeklyHours: parseFloat($('#weeklyHours').val()),
      monthlyHours: parseFloat($('#minMonthlyHours').val()),

      autoPunchOutTime: $('#autoPunchOutTime').val(),

      weekOffDays: getWeekOffDays(),

      lateGraceMinutes: parseInt($('#lateGraceMinutes').val()),
      earlyOutGraceMinutes: parseInt($('#earlyOutGraceMinutes').val()),

      overtimeAllowed: $('#overtimeAllowed').is(':checked'),
      overtimeMultiplier: parseFloat($('#overtimeMultiplier').val()),

      carryForwardEnabled: $('#carryForwardEnabled').is(':checked'),
      encashmentEnabled: $('#encashmentEnabled').is(':checked'),

      salaryCycleStartDay: parseInt($('#salaryCycleStartDay').val()),
      // cycleDuration: parseInt($('#cycleDuration').val()),
      payslipGenerationOffsetDays: parseInt($('#payslipGenerationOffsetDays').val()),

      additionalNotes: $('#additionalNotes').val()
    };

    const isNew = !policyData || !policyData.id;
    const method = isNew ? 'POST' : 'PUT';
    const url = isNew
      ? `/api/organisation/${orgId}/policy/create`
      : `/api/organisation/${orgId}/policy/${policyData.id}/update`;

    $.ajax({
      url,
      method,
      contentType: 'application/json',
      data: JSON.stringify(updated),
      success: function () {
        showToast('success', isNew ? 'Policy created successfully!' : 'Policy updated successfully!');
        loadOrganisationPolicy();
      },
      error: function (xhr) {
        showToast('error', xhr?.responseJSON?.message || 'Failed to save organisation policy.');
      }
    });
  }

  function setWeekOffDays(days) {
    $('.week-off-checkbox').prop('checked', false);

    if (!Array.isArray(days)) return;

    days.forEach(day => {
      const val = day?.toUpperCase();
      $(`.week-off-checkbox[value="${val}"]`).prop('checked', true);
    });
  }

  function getWeekOffDays() {
    return Array.from($('.week-off-checkbox:checked')).map(cb => cb.value.toUpperCase());
  }


  function validatePolicy() {

    const start = $('#officeStartTime').val();
    const end = $('#officeClosedTime').val();
    const autoOut = $('#autoPunchOutTime').val();

    if (!start || !end) {
      showToast('error', "Office timings are required");
      return false;
    }

    if (start >= end) {
      showToast('error', "Office close must be after start time");
      return false;
    }

    if (autoOut && autoOut < end) {
      showToast('error', "Auto punch-out must be after office closing");
      return false;
    }

    const weekOff = getWeekOffDays();
    if (!weekOff || weekOff.length === 0) {
      showToast('error', "Select at least one week off day");
      return false;
    }

    return true;
  }
});
