document.addEventListener("DOMContentLoaded", () => {
  const lastLogin = window.APP.LAST_LOGIN;
  console.log("last login", lastLogin);
  const tbody = document.getElementById("orgAdminsBody");
  if (tbody) loadAdmins();

  async function loadAdmins() {
    tbody.innerHTML = `<tr><td colspan="6" class="text-center">Loading...</td></tr>`;
    try {
      const res = await fetch("/api/superadmin/orgadmins");
      if (!res.ok) throw new Error("Failed to load admins");
      const data = await res.json();

      console.log("data org admins", data)

      tbody.innerHTML = (data || []).length
        ? data.map(a => `
          <tr>
            <td>${a.id}</td>
            <td>${escapeHtml(a.fullName)}</td>
            <td>${escapeHtml(a.email)}</td>
            <td>${a.lastLogin ? new Date(lastLogin).toLocaleString() : '-'}</td>
            <td>
              <span class="badge ${a.isActive ? 'bg-success' : 'bg-danger'}">
                ${a.isActive ? 'Active' : 'Inactive'}
              </span>
            </td>
            <td>
              <div class="btn-group btn-group-sm">
                <button class="btn btn-${a.isActive ? 'warning' : 'success'}"
                        onclick="toggleAdminStatus(${a.id}, ${a.isActive})">
                  ${a.isActive ? 'Deactivate' : 'Activate'}
                </button>
              </div>
            </td>
          </tr>
        `).join("")
        : `<tr><td colspan="6" class="text-center">No admins found</td></tr>`;
    } catch (err) {
      console.error(err);
      tbody.innerHTML = `<tr><td colspan="6" class="text-danger text-center">Failed to load admins</td></tr>`;
    }
  }

  window.toggleAdminStatus = async function (id, currentStatus) {
    const action = currentStatus ? "deactivate" : "activate";
    if (!confirm(`Are you sure you want to ${action} this Org Admin?`)) return;
    try {
      const res = await fetch(`/api/superadmin/org-admin/${id}/${action}`, { method: "PUT" });
      if (!res.ok) throw new Error("Failed to toggle admin");
      const msg = await res.text();
      showToast("success", msg);
      loadAdmins();
    } catch (err) {
      console.error(err);
      showToast("error", "Error updating admin status");
    }
  };

  function escapeHtml(s) {
    return String(s || "").replace(/[&<>"']/g, c => ({
      "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
    }[c]));
  }

  window.showToast = function (type, msg) {
    const color = type === "success" ? "bg-success" : "bg-danger";
    const toast = document.createElement("div");
    toast.className = `toast align-items-center text-white ${color} border-0 position-fixed top-0 end-0 m-3`;
    toast.role = "alert";
    toast.innerHTML = `
      <div class="d-flex">
        <div class="toast-body">${msg}</div>
        <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
      </div>`;
    document.body.appendChild(toast);
    new bootstrap.Toast(toast, { delay: 2500 }).show();
  };
});
