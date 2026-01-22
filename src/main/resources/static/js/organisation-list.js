document.addEventListener("DOMContentLoaded", () => {
  const tbody = document.getElementById("orgTableBody");
  if (!tbody) return;

  loadOrganisations();

  // -----------------------
  // LOAD ALL ORGANISATIONS
  // -----------------------
  async function loadOrganisations() {
    tbody.innerHTML = `<tr><td colspan="8" class="text-center">Loading...</td></tr>`;
    try {
      const res = await fetch("/api/superadmin/organisations");
    
      if (!res.ok) throw new Error("Failed to load organisations");
      const data = await res.json();
      console.log("org data", data)

      if (!Array.isArray(data) || !data.length) {
        tbody.innerHTML = `<tr><td colspan="8" class="text-center">No organisations found.</td></tr>`;
        return;
      }

      tbody.innerHTML = "";
      data.forEach((org, i) => {
        const row = `
          <tr>
            <td>${i + 1}</td>
            <td>${org.name}</td>
            <td>${org.domain}</td>
            <td>${org.adminEmail ?? "NA"}</td>
            <td>
              <span class="badge ${org.isActive ? "bg-success" : "bg-danger"}">
                ${org.isActive ? "Active" : "Inactive"}
              </span>
            </td>
            <td>${org.maxEmployees}</td>
            <td>${org.validity ?? "-"}</td>
            <td>
              <div class="btn-group btn-group-sm">
                <button class="btn btn-primary" onclick="editOrganisation(${org.id})">Edit</button>
                <button class="btn btn-${org.isActive ? "warning" : "success"}"
                        onclick="toggleOrganisationStatus(${org.id}, ${org.isActive})">
                  ${org.isActive ? "Deactivate" : "Activate"}
                </button>
                <button class="btn btn-info" onclick="extendValidity(${org.id}, '${org.validity || ""}')">Extend</button>
                <button class="btn btn-secondary" onclick="increaseMaxEmployees(${org.id}, ${org.maxEmployees || 0})">Increase</button>
              </div>
            </td>
          </tr>`;
        tbody.insertAdjacentHTML("beforeend", row);
      });
    } catch (err) {
      console.error("Error loading orgs", err);
      tbody.innerHTML = `<tr><td colspan="8" class="text-danger text-center">Error loading data.</td></tr>`;
    }
  }

  // ------------------------
  // ORGANISATION OPERATIONS
  // ------------------------

  window.editOrganisation = function (id) {
    window.location.href = `/superadmin/organisation/edit/${id}`;
  };

  window.toggleOrganisationStatus = async function (id, currentStatus) {
    const action = currentStatus ? "deactivate" : "activate";
    if (!confirm(`Are you sure you want to ${action} this organisation?`)) return;

    try {
      const res = await fetch(`/api/superadmin/organisation/${id}/${action}`, { method: "PUT" });
      if (!res.ok) throw new Error("Failed to toggle status");
      const msg = await res.text();
      showToast("success", msg);
      loadOrganisations();
    } catch (err) {
      console.error(err);
      showToast("error", "Error toggling organisation status");
    }
  };

  window.extendValidity = async function (id, currentDate) {
    const newDate = prompt(`Current validity: ${currentDate || "N/A"}\nEnter new validity date (YYYY-MM-DD):`);
    if (!newDate) return;
    try {
      const res = await fetch(`/api/superadmin/organisation/${id}/extend-validity?date=${newDate}`, { method: "PUT" });
      if (!res.ok) throw new Error("Failed to extend validity");
      showToast("success", "Validity extended successfully");
      loadOrganisations();
    } catch (err) {
      console.error(err);
      showToast("error", "Error extending validity");
    }
  };

  window.increaseMaxEmployees = async function (id, currentLimit) {
    const newLimit = prompt(`Current max employees: ${currentLimit}\nEnter new max limit:`);
    if (!newLimit || isNaN(newLimit)) return;
    try {
      const res = await fetch(`/api/superadmin/organisation/${id}/increase-max?limit=${newLimit}`, { method: "PUT" });
      if (!res.ok) throw new Error("Failed to update limit");
      showToast("success", "Max employees updated successfully");
      loadOrganisations();
    } catch (err) {
      console.error(err);
      showToast("error", "Error updating max employees");
    }
  };

  // Small toast helper
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
