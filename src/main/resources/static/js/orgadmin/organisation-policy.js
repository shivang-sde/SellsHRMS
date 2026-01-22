$(document).ready(function() {
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
      success: function(data) {
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
    $('#autoPunchTime').val(d.autoPunchTime);
    $('#lateGraceMinutes').val(d.lateGraceMinutes);
    $('#earlyOutGraceMinutes').val(d.earlyOutGraceMinutes);
    $('#overtimeAllowed').prop('checked', d.overtimeAllowed);
    $('#overtimeMultiplier').val(d.overtimeMultiplier);
    $('#flexibleHourModelEnabled').prop('checked', d.flexibleHourModelEnabled);
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
    $form.find('input, select, textarea').prop('disabled', state);
    state ? $saveBtn.addClass('d-none') : $saveBtn.removeClass('d-none');
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

  $form.on('submit', function(e) {
    e.preventDefault();

    if (!confirm("Updating this policy will affect payroll, attendance, and leave calculations. Proceed?")) {
      return;
    }

    const updated = {
      ...policyData,
      financialYearStartMonth: parseInt($('#financialYearStartMonth').val()),
      financialYearStartDay: parseInt($('#financialYearStartDay').val()),
      leaveYearStartMonth: parseInt($('#leaveYearStartMonth').val()),
      leaveYearStartDay: parseInt($('#leaveYearStartDay').val()),
      standardDailyHours: parseFloat($('#standardDailyHours').val()),
      weeklyHours: parseFloat($('#weeklyHours').val()),
      autoPunchTime: $('#autoPunchTime').val(),
      lateGraceMinutes: parseInt($('#lateGraceMinutes').val()),
      earlyOutGraceMinutes: parseInt($('#earlyOutGraceMinutes').val()),
      overtimeAllowed: $('#overtimeAllowed').is(':checked'),
      overtimeMultiplier: parseFloat($('#overtimeMultiplier').val()),
      minMonthlyHours: parseFloat($('#minMonthlyHours').val()),
      flexibleHourModelEnabled: $('#flexibleHourModelEnabled').is(':checked'),
      carryForwardEnabled: $('#carryForwardEnabled').is(':checked'),
      encashmentEnabled: $('#encashmentEnabled').is(':checked'),
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
      success: function() {
        alert(isNew ? 'Policy created successfully!' : 'Policy updated successfully!');
        loadOrganisationPolicy();
      },
      error: function() {
        alert('Failed to save organisation policy.');
      }
    });
  });
});
