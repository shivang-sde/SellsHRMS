$(document).ready(function() {
  let leaveTypes = [];
  const orgId = window.APP.ORG_ID;

  // Initialize
  init();

  function init() {
    loadLeaveTypes();
    setupEventListeners();
  }

  function setupEventListeners() {
    $('#createLeaveTypeForm').on('submit', handleCreateLeaveType);
    $('#editLeaveTypeForm').on('submit', handleEditLeaveType);
  }

  // Load all leave types
  function loadLeaveTypes() {
    $.ajax({
      url: `/api/leave-type/org/${orgId}`,
      method: 'GET',
      success: function(response) {
        leaveTypes = response;
        displayLeaveTypes(leaveTypes);
      },
      error: function(xhr) {
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
    
    if (type.isPaid) features.push('<span class="feature-badge">Paid</span>');
    if (type.carryForwardAllowed) features.push('<span class="feature-badge">Carry Forward</span>');
    if (type.encashable) features.push('<span class="feature-badge">Encashable</span>');
    if (type.allowHalfDay) features.push('<span class="feature-badge">Half Day</span>');
    if (type.requiresApproval) features.push('<span class="feature-badge">Requires Approval</span>');

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

            <div class="d-flex gap-2">
              <button class="btn btn-sm btn-outline-primary flex-fill" onclick="editLeaveType(${type.id})">
                <i class="fa fa-edit me-1"></i>Edit
              </button>
              <button class="btn btn-sm btn-outline-danger" onclick="deleteLeaveType(${type.id}, '${type.name}')">
                <i class="fa fa-trash"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
    `;
  }

  // Create leave type
  function handleCreateLeaveType(e) {
    e.preventDefault();
    
    const formData = getFormData('#createLeaveTypeForm');
    formData.orgId = parseInt(orgId);

    $.ajax({
      url: '/api/leave-type/create',
      method: 'POST',
      contentType: 'application/json',
      data: JSON.stringify(formData),
      success: function(response) {
        showToast('success', 'Leave type created successfully');
        $('#createLeaveTypeModal').modal('hide');
        $('#createLeaveTypeForm')[0].reset();
        loadLeaveTypes();
      },
      error: function(xhr) {
        const error = xhr.responseJSON?.message || 'Failed to create leave type';
        showToast('error', error);
      }
    });
  }

  // Edit leave type
  window.editLeaveType = function(leaveTypeId) {
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
  };

  function handleEditLeaveType(e) {
    e.preventDefault();
    
    const leaveTypeId = $('#editLeaveTypeId').val();
    const formData = getFormData('#editLeaveTypeForm');
    formData.orgId = parseInt(orgId);

    $.ajax({
      url: `/api/leave-type/${leaveTypeId}/update`,
      method: 'PATCH',
      contentType: 'application/json',
      data: JSON.stringify(formData),
      success: function(response) {
        showToast('success', 'Leave type updated successfully');
        $('#editLeaveTypeModal').modal('hide');
        loadLeaveTypes();
      },
      error: function(xhr) {
        const error = xhr.responseJSON?.message || 'Failed to update leave type';
        showToast('error', error);
      }
    });
  }

  // Delete leave type
  window.deleteLeaveType = function(leaveTypeId, name) {
    if (!confirm(`Are you sure you want to delete "${name}"? This action may deactivate the leave type if it's in use.`)) {
      return;
    }

    $.ajax({
      url: `/api/leave-type/${leaveTypeId}/delete`,
      method: 'DELETE',
      success: function() {
        showToast('success', 'Leave type deleted successfully');
        loadLeaveTypes();
      },
      error: function(xhr) {
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
    form.find('input[type="text"], input[type="number"], textarea, select').each(function() {
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
    form.find('input[type="checkbox"]').each(function() {
      const name = $(this).attr('name');
      if (name) {
        data[name] = $(this).is(':checked');
      }
    });

    return data;
  }
});