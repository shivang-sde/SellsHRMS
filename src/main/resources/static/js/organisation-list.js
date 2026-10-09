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

        const buildDoc = (name, val, url, isVerified, orgId, docType) => {
          let valHtml = val ? val : '<span class="text-muted text-decoration-underline" style="font-style: italic;">Not Provided</span>';
          let linkHtml = url ? `<a href="${url}" target="_blank" class="ms-1 text-primary text-decoration-none" style="font-size: 0.9rem;" title="Download Document"><i class="fas fa-cloud-download-alt"></i></a>` : '';
          let iconHtml = isVerified ? `<i class="fas fa-check-circle text-success ms-1" title="Verified"></i>` : `<i class="fas fa-times-circle text-danger ms-1" title="Not Verified"></i>`;
          
          // Add Verify button for unverified documents that have values
          let verifyBtn = '';
          if (!isVerified && val && val.trim() !== '') {
            verifyBtn = `<button class="btn btn-xs btn-outline-success ms-1" 
                         onclick="manuallyVerifyDocument(${orgId}, '${docType}')" 
                         title="Manually Verify ${name}">
                         <i class="fas fa-check"></i> Verify</button>`;
          }
          
          return `<div class="x-small mb-1"><span class="fw-bold text-secondary" style="display:inline-block; width: 50px;">${name}:</span> ${valHtml} ${linkHtml} ${iconHtml} ${verifyBtn}</div>`;
        };

        const docsHtml = `
            ${buildDoc('PAN', org.pan, org.panUrl, org.isPanVerified, org.id, 'PAN')}
            ${buildDoc('Aadhaar', org.aadhar, org.aadharUrl, org.isAadharVerified, org.id, 'AADHAAR')}
            ${buildDoc('GST', org.gst, org.gstUrl, org.isGstVerified, org.id, 'GST')}
            ${buildDoc('TAN', org.tan, org.tanUrl, org.isTanVerified, org.id, 'TAN')}
        `;

        // Check if all documents are verified
        const allVerified = org.isPanVerified && 
                           org.isAadharVerified && 
                           org.isGstVerified && 
                           org.isTanVerified;

        // Check if all documents have values (required for verify-all)
        const hasAllDocs = org.pan && org.aadhar && org.gst && org.tan;

        const verificationBadgesHtml = `
            <div class="mt-2 d-flex flex-wrap gap-1">
                <span class="badge ${org.isPanVerified ? 'bg-success' : 'bg-danger'}" style="font-size: 0.65rem;" title="PAN Verification">PAN</span>
                <span class="badge ${org.isAadharVerified ? 'bg-success' : 'bg-danger'}" style="font-size: 0.65rem;" title="Aadhaar Verification">UIDAI</span>
                <span class="badge ${org.isGstVerified ? 'bg-success' : 'bg-danger'}" style="font-size: 0.65rem;" title="GST Verification">GST</span>
                <span class="badge ${org.isTanVerified ? 'bg-success' : 'bg-danger'}" style="font-size: 0.65rem;" title="TAN Verification">TAN</span>
            </div>
            ${!allVerified && hasAllDocs ? `
                <button class="btn btn-xs btn-outline-primary mt-2" 
                        onclick="manuallyVerifyAllDocuments(${org.id})" 
                        title="Verify All Documents">
                        <i class="fas fa-check-double"></i> Verify All
                </button>
            ` : ''}
        `;

        const row = `
        <tr>
            <td class="ps-4 text-muted small">${i + 1}</td>
            <td>
                <div class="fw-bold text-dark">${org.name}</div>
                <div class="text-muted x-small">${org.domain}</div>
            </td>
            <td>
                ${docsHtml}
            </td>
            <td>
                <div class="mb-1">${statusBadge}</div>
                ${verificationBadgesHtml}
            </td>
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
  // MANUAL VERIFICATION FUNCTIONS
  // ------------------------

  /**
   * Manually verify a single document
   */
  window.manuallyVerifyDocument = async function (orgId, docType) {
    if (!confirm(`Are you sure you want to manually verify the ${docType} document for this organisation?`)) {
      return;
    }

    try {
      const endpoint = `/api/superadmin/organisation/${orgId}/documents/${docType}/verify`;
      const res = await fetch(endpoint, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({ verificationNote: "Manual verification by Super Admin" })
      });

      if (!res.ok) {
        const errorData = await res.json().catch(() => ({}));
        throw new Error(errorData.message || "Failed to verify document");
      }

      const data = await res.json();
      showToast("success", data.message || "Document verified successfully");
      loadOrganisations(); // Refresh the list
    } catch (err) {
      console.error("Manual verification failed", err);
      showToast("error", err.message || "Failed to verify document");
    }
  };

  /**
   * Manually verify all documents for an organisation
   */
  window.manuallyVerifyAllDocuments = async function (orgId) {
    if (!confirm(`Are you sure you want to manually verify ALL documents for this organisation?`)) {
      return;
    }

    try {
      const endpoint = `/api/superadmin/organisation/${orgId}/documents/verify-all`;
      const res = await fetch(endpoint, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({ verificationNote: "Bulk manual verification by Super Admin" })
      });

      if (!res.ok) {
        const errorData = await res.json().catch(() => ({}));
        throw new Error(errorData.message || "Failed to verify all documents");
      }

      const data = await res.json();
      showToast("success", data.message || "All documents verified successfully");
      loadOrganisations(); // Refresh the list
    } catch (err) {
      console.error("Manual verify-all failed", err);
      showToast("error", err.message || "Failed to verify all documents");
    }
  };

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
      showToast("error", "Failed to load modules");
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
