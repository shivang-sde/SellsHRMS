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
              <button class="btn btn-sm btn-outline-primary me-1" onclick="editRole(${role.id})"><i class="fa fa-edit"></i></button>
              <button class="btn btn-sm btn-outline-danger" onclick="deleteRole(${role.id})"><i class="fa fa-trash"></i></button>
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
});
