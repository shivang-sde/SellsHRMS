$(document).ready(function () {
  const orgId = window.APP.ORG_ID;
  const urlParams = new URLSearchParams(window.location.search);
  const roleId = urlParams.get('id');
  const isEditMode = !!roleId;

  // Load permissions first, then role (if editing)
  loadPermissions().then(() => {
    if (isEditMode) {
      loadRole(roleId);
    }
  });

  $('#roleForm').on('submit', function (e) {
    e.preventDefault();
    const formData = collectFormData();

    const endpoint = isEditMode ? `/api/roles/${roleId}` : `/api/roles/org/${orgId}`;
    const method = isEditMode ? 'PUT' : 'POST';

    $('#saveBtn').prop('disabled', true).html('<span class="spinner-border spinner-border-sm me-2"></span>Saving...');

    $.ajax({
      url: endpoint,
      method,
      contentType: 'application/json',
      data: JSON.stringify(formData),
      success: function () {
        showToast('success', 'Role saved successfully');
        setTimeout(() => (window.location.href = '/org/roles'), 1500);
      },
      error: function (xhr) {
        const msg = xhr.responseJSON?.message || 'Failed to save role';
        showToast('error', msg);
        $('#saveBtn').prop('disabled', false).html('<i class="fa fa-save me-2"></i>Save Role');
      }
    });
  });

  // ✅ Now returns a Promise
  function loadPermissions() {
    return $.get('/api/permissions', function (data) {
      const container = $('#permissionsList');
      container.empty();

      const grouped = groupBy(data, 'module');
      for (const module in grouped) {
        const group = $('<div class="col-12 mt-3"><h6 class="fw-bold text-primary">' + module + '</h6></div>');
        container.append(group);

        grouped[module].forEach(p => {
          container.append(`
            <div class="col-md-4 col-sm-6">
              <div class="form-check">
                <input class="form-check-input permission-checkbox" type="checkbox" id="perm_${p.id}" value="${p.id}">
                <label class="form-check-label" for="perm_${p.id}">${p.action} (${p.code})</label>
              </div>
            </div>
          `);
        });
      }
    });
  }

  function loadRole(id) {
    $.get(`/api/roles/${id}`, function (data) {
      $('input[name="name"]').val(data.name);
      $('input[name="description"]').val(data.description);

      // Permissions should already be loaded now
      if (data.permissions && data.permissions.length > 0) {
        data.permissions.forEach(p => {
          $(`#perm_${p.id}`).prop('checked', true);
        });
      }
    });
  }

  function collectFormData() {
    const selectedPermissions = [];
    $('.permission-checkbox:checked').each(function () {
      selectedPermissions.push({ id: parseInt($(this).val()) });
    });

    return {
      name: $('input[name="name"]').val().trim(),
      description: $('input[name="description"]').val().trim(),
      permissions: selectedPermissions
    };
  }

  function groupBy(arr, key) {
    return arr.reduce((acc, item) => {
      (acc[item[key]] = acc[item[key]] || []).push(item);
      return acc;
    }, {});
  }
});
