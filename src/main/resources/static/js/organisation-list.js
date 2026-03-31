document.addEventListener("DOMContentLoaded", () => {
  let selectedOrgId = null;
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
        // Determine status badge color
        const statusBadge = org.isActive
          ? `<span class="badge rounded-pill bg-soft-success text-success px-3">Active</span>`
          : `<span class="badge rounded-pill bg-soft-danger text-danger px-3">Inactive</span>`;

        const row = `
        <tr>
            <td class="ps-4 text-muted small">${i + 1}</td>
            <td>
                <div class="fw-bold text-dark">${org.name}</div>
                <div class="text-muted x-small">${org.domain}</div>
            </td>
            <td>
                <div class="small fw-medium">${org.adminEmail ?? "N/A"}</div>
            </td>
            <td>${statusBadge}</td>
            <td>
                <div class="small fw-bold text-dark">${org.maxEmployees} Employees</div>
                <div class="progress mt-1" style="height: 4px; width: 80px;">
                    <div class="progress-bar bg-primary" style="width: 70%"></div>
                </div>
            </td>
            <td>
                <div class="small">${org.validity ?? "-"}</div>
            </td>
            <td class="text-end pe-4">
                <div class="dropdown">
                    <button class="btn btn-light btn-sm rounded-circle border shadow-sm" type="button" data-bs-toggle="dropdown">
                        <i class="fas fa-ellipsis-v"></i>
                    </button>
                    <ul class="dropdown-menu dropdown-menu-end shadow border-0 rounded-3">
                        <li><a class="dropdown-item py-2" href="javascript:void(0)" onclick="editOrganisation(${org.id})">
                            <i class="fa fa-edit text-primary me-2"></i> Edit Details</a>
                        </li>
                        <li><a class="dropdown-item py-2" href="javascript:void(0)" onclick="openManageModulesModal(${org.id}, '${org.name}')">
                            <i class="fa fa-cog text-info me-2"></i> Manage Modules</a>
                        </li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item py-2" href="javascript:void(0)" onclick="extendValidity(${org.id}, '${org.validity || ""}')">
                            <i class="fa fa-calendar-plus text-secondary me-2"></i> Extend Validity</a>
                        </li>
                        <li><a class="dropdown-item py-2" href="javascript:void(0)" onclick="increaseMaxEmployees(${org.id}, ${org.maxEmployees || 0})">
                            <i class="fa fa-users text-secondary me-2"></i> Increase Capacity</a>
                        </li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item py-2 ${org.isActive ? "text-danger" : "text-success"}" href="javascript:void(0)" 
                               onclick="toggleOrganisationStatus(${org.id}, ${org.isActive})">
                            <i class="fa ${org.isActive ? "fa-ban" : "fa-check"} me-2"></i> 
                            ${org.isActive ? "Deactivate Organisation" : "Activate Organisation"}</a>
                        </li>
                    </ul>
                </div>
            </td>
        </tr>`;
        tbody.insertAdjacentHTML("beforeend", row);
      });
    } catch (err) {
      console.error("Error loading orgs", err);
      showToast("error", err.message);
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
      showToast("error", err.message);
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
      showToast("error", err.message);
    }
  };

  // ---------------------------
  // OPEN MODAL & LOAD MODULES
  // ---------------------------
  window.openManageModulesModal = async function (orgId, orgName) {
    selectedOrgId = orgId;
    document.getElementById("manageModulesLabel").innerText =
      `Manage Modules for ${orgName}`;

    const form = document.getElementById("manageModulesForm");
    form.innerHTML = `<div class="text-center p-4">Loading modules...</div>`;

    try {
      const [allRes, activeRes] = await Promise.all([
        fetch(`/api/modules/all`),
        fetch(`/api/modules/codes/org/${orgId}/active`)
      ]);
      const [allData, activeData] = await Promise.all([
        allRes.json(), activeRes.json()
      ]);

      const allModules = allData.data || allData;
      const activeCodes = new Set(activeData.data || activeData);

      form.innerHTML = "";

      allModules.forEach(m => {
        const isChecked = activeCodes.has(m.code);
        form.insertAdjacentHTML("beforeend", `
          <div class="col-md-4 module-item ${isChecked ? '' : 'disabled-module'}">
            <div class="form-check">
              <input class="form-check-input module-toggle" type="checkbox"
                     id="mod_${m.code}" value="${m.code}"
                     ${isChecked ? "checked" : ""}>
              <label class="form-check-label" for="mod_${m.code}">
                ${m.name}
                ${isChecked ? "" : `<span class="badge bg-light text-muted border ms-2">🔒 Not Available</span>`}
              </label>
            </div>
          </div>
        `);
      });

      // Bind dynamic toggle handlers
      document.querySelectorAll(".module-toggle").forEach(cb => {
        cb.addEventListener("change", e => {
          const container = e.target.closest(".module-item");
          const label = container.querySelector("label");
          if (e.target.checked) {
            container.classList.remove("disabled-module");
            const badge = label.querySelector(".badge");
            if (badge) badge.remove();
          } else {
            container.classList.add("disabled-module");
            if (!label.querySelector(".badge")) {
              label.insertAdjacentHTML("beforeend",
                `<span class="badge bg-light text-muted border ms-2">🔒 Not Available</span>`);
            }
          }
        });
      });

      new bootstrap.Modal(document.getElementById("manageModulesModal")).show();
    } catch (err) {
      console.error("Failed to load modules", err);
      showToast("error",);
    }
  };

  // ---------------------------
  // SAVE CHANGES
  // ---------------------------
  document.getElementById("saveModulesBtn").addEventListener("click", async () => {
    if (!selectedOrgId) return;

    const codes = Array.from(
      document.querySelectorAll("#manageModulesForm input[type='checkbox']:checked")
    ).map(el => el.value);

    try {
      const res = await fetch(`/api/modules/org/${selectedOrgId}/assign`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(codes)
      });

      if (!res.ok) throw new Error();
      showToast("success", "Modules updated successfully!");
      bootstrap.Modal.getInstance(document.getElementById("manageModulesModal")).hide();
    } catch (err) {
      console.error(err);
      showToast("error", err.message);
    }
  });


});
