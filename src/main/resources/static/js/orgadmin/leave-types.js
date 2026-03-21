$(document).ready(function () {
  let leaveTypes = [];
  const orgId = window.APP.ORG_ID;

  const methodSelect = $('[name="accrualMethod"]');
  const cfAllowed = $('[name="carryForwardAllowed"]');
  const cfLimit = $('[name="carryForwardLimit"]');
  const accrualRate = $('[name="accrualRate"]');
  const validityDays = $('[name="validityDays"]');

  // Initialize
  init();

  function init() {
    loadLeaveTypes();
    setupEventListeners();
  }

  function setupEventListeners() {

    $('#createLeaveTypeForm').on('submit', handleCreateLeaveType);
    $('#editLeaveTypeForm').on('submit', handleEditLeaveType);

    // For create form
    $('#createLeaveTypeForm [name="accrualMethod"]').on('change', () => updateFormState(false));
    $('#createLeaveTypeForm [name="carryForwardAllowed"]').on('change', () => updateFormState(false));

    // For edit form
    $('#editLeaveTypeForm [name="accrualMethod"]').on('change', () => updateFormState(true));
    $('#editLeaveTypeForm [name="carryForwardAllowed"]').on('change', () => updateFormState(true));

  }


  function updateFormState(isEdit = false) {
    const prefix = isEdit ? '#edit' : '';
    const method = $(`${prefix}AccrualMethod`).val();
    const accrualRate = $(`${prefix}AccrualRate`);
    const annualLimit = $(`${prefix}AnnualLimit`);
    const cfAllowed = $(`${prefix}CarryForwardAllowed`);
    const cfLimit = $(`${prefix}CarryForwardLimit`);
    const validityDays = $(`${prefix}ValidityDays`);

    // ---- Reset validation hints ----
    cfLimit.removeClass('is-invalid');
    annualLimit.removeClass('is-invalid');

    // ---- Accrual Rate ----
    accrualRate.prop('disabled', method === 'NONE' || method === 'PRO_RATA');
    if (method === 'NONE' || method === 'PRO_RATA') accrualRate.val('');

    // ---- Annual Limit ----
    const annualRequired = (method === 'ANNUAL');
    annualLimit.prop('required', annualRequired);
    annualLimit.prop('disabled', method === 'NONE' || method === 'PRO_RATA');
    if (!annualRequired && !annualLimit.val()) annualLimit.val('');

    // ---- Carry Forward ----
    const cfEnabled = (method === 'MONTHLY');
    cfAllowed.prop('disabled', !cfEnabled);
    if (!cfEnabled) {
      cfAllowed.prop('checked', false);
      cfLimit.prop('disabled', true).val('');
    } else {
      cfLimit.prop('disabled', !cfAllowed.is(':checked'));
    }

    // ---- Validity Days ----
    const validEnabled = (method === 'NONE');
    validityDays.prop('disabled', !validEnabled);
    if (!validEnabled) validityDays.val('');

    // ---- Inline logical warnings ----
    const accrualVal = parseFloat(accrualRate.val() || 0);
    const cfVal = parseFloat(cfLimit.val() || 0);
    if (cfAllowed.is(':checked') && cfVal > accrualVal && cfEnabled) {
      cfLimit.addClass('is-invalid');
      showToast('warning', 'Carry forward limit cannot exceed monthly accrual rate.');
    }

    if (annualRequired && (!annualLimit.val() || annualLimit.val() <= 0)) {
      annualLimit.addClass('is-invalid');
    }
  }


  // Load all leave types
  function loadLeaveTypes() {
    $.ajax({
      url: `/api/leave-type/org/${orgId}`,
      method: 'GET',
      success: function (response) {
        leaveTypes = response;
        displayLeaveTypes(leaveTypes);
      },
      error: function (xhr) {
        $('#leaveTypesGrid').html(`
          <div class="col-12">
            <div class="alert alert-danger">
              Error loading leave types. Please try again.
            </div>
          </div>
        `);
      }
    });
  }

  function displayLeaveTypes(types) {
    const grid = $('#leaveTypesGrid');
    grid.empty();

    if (types.length === 0) {
      grid.append(`
        <div class="col-12">
          <div class="card border-0 shadow-sm text-center py-5">
            <div class="card-body">
              <i class="fa fa-folder-open fa-4x text-muted mb-3"></i>
              <h5>No Leave Types Found</h5>
              <p class="text-muted">Create your first leave type to get started</p>
              <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#createLeaveTypeModal">
                <i class="fa fa-plus me-2"></i>Create Leave Type
              </button>
            </div>
          </div>
        </div>
      `);
      return;
    }

    types.forEach(type => {
      const card = createLeaveTypeCard(type);
      grid.append(card);
    });
  }

  function createLeaveTypeCard(type) {
    const features = [];

    console.log("type ", type);
    if (type.isPaid) features.push('<span class="feature-badge">Paid</span>');
    if (type.carryForwardAllowed) features.push('<span class="feature-badge">Carry Forward</span>');
    if (type.encashable) features.push('<span class="feature-badge">Encashable</span>');
    if (type.allowHalfDay) features.push('<span class="feature-badge">Half Day</span>');
    if (type.requiresApproval) features.push('<span class="feature-badge">Requires Approval</span>');

    if (type.isActive === false) {
      features.push('<span class="feature-badge text-lg bg-secondary">Inactive</span>');
    }


    const isOrgAdmin = window.APP.ROLE === 'ORG_ADMIN'
    const canEdit = isOrgAdmin || (window.APP.hasAnyPermission && window.APP.hasAnyPermission('LEAVE_EDIT'));
    const canDelete = isOrgAdmin;

    let actionButtons = '';
    if (canEdit || canDelete) {
      actionButtons = '<div class="d-flex gap-2">';
      if (canEdit) {
        actionButtons += `
          <button class="btn btn-sm btn-outline-primary flex-fill" onclick="editLeaveType(${type.id})">
            <i class="fa fa-edit me-1"></i>Edit
          </button>
        `;
      }
      if (canDelete) {
        const safeName = type.name ? type.name.replace(/'/g, "\\'") : '';
        actionButtons += `
          <button class="btn btn-sm btn-outline-danger" onclick="deleteLeaveType(${type.id}, '${safeName}')">
            <i class="fa fa-trash"></i>
          </button>
        `;
      }
      actionButtons += '</div>';
    }

    return `
      <div class="col-md-4 mb-4">
        <div class="card leave-type-card border-0 shadow-sm">
          <div class="leave-type-header">
            <div class="d-flex justify-content-between align-items-start">
              <div>
                <h5 class="mb-1">${type.name}</h5>
                <small class="opacity-75">${type.accrualMethod} Accrual</small>
              </div>
              <div class="text-end">
                <h3 class="mb-0">${type.annualLimit || '∞'}</h3>
                <small class="opacity-75">days/year</small>
              </div>
            </div>
          </div>
          <div class="card-body">
            <p class="text-muted small mb-3">${type.description || 'No description provided'}</p>
            
            <div class="mb-3">
              ${features.join(' ')}
            </div>

            <div class="row g-2 mb-3">
              ${type.accrualRate ? `
                <div class="col-6">
                  <small class="text-muted d-block">Accrual Rate</small>
                  <strong>${type.accrualRate} days/month</strong>
                </div>
              ` : ''}
              ${type.maxConsecutiveDays ? `
                <div class="col-6">
                  <small class="text-muted d-block">Max Consecutive</small>
                  <strong>${type.maxConsecutiveDays} days</strong>
                </div>
              ` : ''}
              ${type.carryForwardLimit ? `
                <div class="col-6">
                  <small class="text-muted d-block">Carry Forward Limit</small>
                  <strong>${type.carryForwardLimit} days</strong>
                </div>
              ` : ''}
              <div class="col-6">
                <small class="text-muted d-block">Gender</small>
                <strong>${type.applicableGender}</strong>
              </div>
            </div>

            ${actionButtons}
          </div>
        </div>
      </div>
    `;
  }

  // Create leave type
  function handleCreateLeaveType(e) {
    e.preventDefault();

    if (!validateLeaveTypeForm('#createLeaveTypeForm')) return;

    const formData = getFormData('#createLeaveTypeForm');
    formData.orgId = parseInt(orgId);

    $.ajax({
      url: '/api/leave-type/create',
      method: 'POST',
      contentType: 'application/json',
      data: JSON.stringify(formData),
      success: function (response) {
        showToast('success', 'Leave type created successfully');
        $('#createLeaveTypeModal').modal('hide');
        $('#createLeaveTypeForm')[0].reset();
        loadLeaveTypes();
      },
      error: function (xhr) {
        const error = xhr.responseJSON?.message || 'Failed to create leave type';
        showToast('error', error);
      }
    });
  }

  // Edit leave type
  window.editLeaveType = function (leaveTypeId) {
    const type = leaveTypes.find(t => t.id === leaveTypeId);
    if (!type) return;

    $('#editLeaveTypeId').val(type.id);
    $('#editName').val(type.name);
    $('#editDescription').val(type.description);
    $('#editAnnualLimit').val(type.annualLimit);
    $('#editAccrualMethod').val(type.accrualMethod);
    $('#editAccrualRate').val(type.accrualRate);
    $('#editApplicableGender').val(type.applicableGender);
    $('#editCarryForwardLimit').val(type.carryForwardLimit);
    $('#editMaxConsecutiveDays').val(type.maxConsecutiveDays);
    $('#editValidityDays').val(type.validityDays);

    // Checkboxes
    $('#editIsPaid').prop('checked', type.isPaid);
    $('#editRequiresApproval').prop('checked', type.requiresApproval);
    $('#editCarryForwardAllowed').prop('checked', type.carryForwardAllowed);
    $('#editEncashable').prop('checked', type.encashable);
    $('#editAllowHalfDay').prop('checked', type.allowHalfDay);
    $('#editAvailableDuringProbation').prop('checked', type.availableDuringProbation);
    $('#editIncludeHolidaysInLeave').prop('checked', type.includeHolidaysInLeave);
    $('#editVisibleToEmployees').prop('checked', type.visibleToEmployees);

    $('#editLeaveTypeModal').modal('show');
    setTimeout(() => updateFormState(true), 200);
  };

  function handleEditLeaveType(e) {
    e.preventDefault();

    if (!validateLeaveTypeForm('#editLeaveTypeForm')) return;


    const leaveTypeId = $('#editLeaveTypeId').val();
    const formData = getFormData('#editLeaveTypeForm');
    formData.orgId = parseInt(orgId);

    $.ajax({
      url: `/api/leave-type/${leaveTypeId}/update`,
      method: 'PATCH',
      contentType: 'application/json',
      data: JSON.stringify(formData),
      success: function (response) {
        showToast('success', 'Leave type updated successfully');
        $('#editLeaveTypeModal').modal('hide');
        loadLeaveTypes();
      },
      error: function (xhr) {
        const error = xhr.responseJSON?.message || 'Failed to update leave type';
        showToast('error', error);
      }
    });
  }

  // Delete leave type
  window.deleteLeaveType = function (leaveTypeId, name) {
    if (!confirm(`Are you sure you want to delete "${name}"? This action may deactivate the leave type if it's in use.`)) {
      return;
    }

    $.ajax({
      url: `/api/leave-type/${leaveTypeId}/delete`,
      method: 'DELETE',
      success: function () {
        showToast('success', 'Leave type deleted successfully');
        loadLeaveTypes();
      },
      error: function (xhr) {
        const error = xhr.responseJSON?.message || 'Failed to delete leave type';
        showToast('error', error);
      }
    });
  };

  // Helper function to get form data
  function getFormData(formSelector) {
    const form = $(formSelector);
    const data = {};

    // Text inputs and selects
    form.find('input[type="text"], input[type="number"], textarea, select').each(function () {
      const name = $(this).attr('name');
      const value = $(this).val();

      if (name && value !== '') {
        if ($(this).attr('type') === 'number') {
          data[name] = value ? parseFloat(value) : null;
        } else {
          data[name] = value;
        }
      }
    });

    // Checkboxes
    form.find('input[type="checkbox"]').each(function () {
      const name = $(this).attr('name');
      if (name) {
        data[name] = $(this).is(':checked');
      }
    });

    return data;
  }
});


function validateLeaveTypeForm(formSelector) {
  const form = $(formSelector);
  const method = form.find('[name="accrualMethod"]').val();
  const accrualRate = parseFloat(form.find('[name="accrualRate"]').val() || 0);
  const carryForwardAllowed = form.find('[name="carryForwardAllowed"]').is(':checked');
  const carryForwardLimit = parseFloat(form.find('[name="carryForwardLimit"]').val() || 0);
  const annualLimit = parseFloat(form.find('[name="annualLimit"]').val() || 0);
  const validityDays = parseFloat(form.find('[name="validityDays"]').val() || 0);
  const maxConsecutive = parseFloat(form.find('[name="maxConsecutiveDays"]').val() || 0);

  if (method === 'ANNUAL' && annualLimit <= 0) {
    showToast('error', 'Annual limit is required for annual accrual method.');
    return false;
  }
  if (method !== 'MONTHLY' && carryForwardAllowed) {
    showToast('error', 'Carry forward only applies for monthly accrual.');
    return false;
  }
  if (annualLimit && maxConsecutive && maxConsecutive > annualLimit) {
    showToast('error', 'Max consecutive days cannot exceed annual limit.');
    return false;
  }
  if (carryForwardAllowed && carryForwardLimit > accrualRate) {
    showToast('error', 'Carry forward limit cannot exceed monthly accrual rate.');
    return false;
  }
  if (validityDays > 0 && method !== 'NONE') {
    showToast('error', 'Validity days only apply for special (NONE accrual) leaves.');
    return false;
  }
  return true;
}
