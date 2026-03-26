$(document).ready(function () {
  const orgId = window.APP.ORG_ID;
  loadRoles();

  function loadRoles() {
    $.get(`/api/roles/org/${orgId}`, function (data) {
      const tbody = $('#rolesTable tbody');
      tbody.empty();

      data.forEach(role => {
        const perms = (role.permissions && role.permissions.length > 0)
            ? role.permissions.map(p => `<span class="perms-badge mb-1 d-inline-block">${p.code}</span>`).join(' ')
            : '<span class="text-muted">No Permissions</span>';

        tbody.append(`
          <tr>
            <td>${role.name}</td>
            <td>${role.description || '-'}</td>
            <td>${perms}</td>
            <td>
              <button class="btn btn-sm btn-outline-primary me-1" onclick="editRole(${role.id})" title="Edit Role"><i class="fa fa-edit"></i></button>
              <button class="btn btn-sm btn-outline-danger" onclick="deleteRole(${role.id})" title="Delete Role"><i class="fa fa-trash"></i></button>
            </td>
          </tr>
        `);
      });
    });
  }

  window.editRole = function (id) {
    window.location.href = `/org/create-role?id=${id}`;
  };

  window.deleteRole = function (id) {
    if (!confirm('Are you sure you want to delete this role?')) return;
    $.ajax({
      url: `/api/roles/${id}`,
      method: 'DELETE',
      success: function () {
        showToast('success', 'Role deleted successfully');
        loadRoles();
      },
      error: function () {
        showToast('error', 'Failed to delete role');
      }
    });
  };

  // Load purely descriptive module permissions for viewing
  loadModuleWisePermissions();

  function loadModuleWisePermissions() {
    $.get('/api/permissions', function (data) {
      const container = $('#permissionsList');
      container.empty();

      const grouped = groupBy(data, 'module');
      for (const module in grouped) {
        const groupContainer = $('<div class="col-12 mt-3 mb-1"><h6 class="fw-bold text-primary border-bottom pb-2">' + module + '</h6><div class="row g-2"></div></div>');
        container.append(groupContainer);

        const modContainer = groupContainer.find('.row');

        grouped[module].forEach(p => {
          modContainer.append(`
            <div class="col-md-3 col-sm-4">
              <div class="bg-white border rounded p-2 shadow-sm h-100">
                <span class="d-block fw-semibold text-dark" style="font-size: 0.85rem; line-height: 1.2;">${p.action}</span>
                <span class="d-block text-muted mt-1" style="font-size: 0.70rem; font-family: monospace;">${p.code}</span>
              </div>
            </div>
          `);
        });
      }
    });
  }

  function groupBy(arr, key) {
    return arr.reduce((acc, item) => {
      (acc[item[key]] = acc[item[key]] || []).push(item);
      return acc;
    }, {});
  }
});
