document.addEventListener("DOMContentLoaded", () => {
  let selectedOrgId = null;
  const tbody = document.getElementById("orgTableBody");
  if (!tbody) return;

  loadOrganisations();

  // -----------------------
  // UTILITY FUNCTIONS
  // -----------------------

  /**
   * Safely escape text for HTML insertion
   */
  function escapeHtml(text) {
    if (!text) return "";
    const div = document.createElement("div");
    div.textContent = text;
    return div.innerHTML;
  }

  /**
   * Format document value for display with truncation
   */
  function formatDocumentValue(val) {
    if (!val || val.trim() === "") {
      return '<span class="text-muted" style="font-style: italic; font-size: 0.85rem;">Not Provided</span>';
    }
    // Truncate long values to prevent table expansion
    if (val.length > 20) {
      return `<span title="${escapeHtml(val)}">${escapeHtml(val.substring(0, 18))}...</span>`;
    }
    return escapeHtml(val);
  }

  /**
   * Create verification status badge
   */
  function getVerificationStatusBadge(isVerified) {
    if (isVerified) {
      return '<span class="badge bg-success-subtle text-success px-2 py-1" style="font-size: 0.75rem;"><i class="fas fa-check-circle me-1"></i>Verified</span>';
    }
    return '';
  }

  /**
   * Create verify button for unverified documents with values
   */
  function getVerifyButton(orgId, docType, docValue, isVerified) {
    // Only show verify button if: document has a value AND is not already verified
    if (!isVerified && docValue && docValue.trim() !== "") {
      return `<button class="btn btn-sm btn-outline-success px-2 py-0" 
                     onclick="manuallyVerifyDocument(${orgId}, '${docType}')" 
                     title="Verify ${docType}">
                     <i class="fas fa-check me-1"></i>Verify</button>`;
    }
    return '';
  }

  /**
   * Get document download link
   */
  function getDocumentLink(url, docType) {
    if (url && url.trim() !== "") {
      return `<a href="${escapeHtml(url)}" target="_blank" 
                class="text-primary text-decoration-none ms-1" 
                title="Download ${docType} document">
                <i class="fas fa-download" style="font-size: 0.85rem;"></i></a>`;
    }
    return '';
  }

  // -----------------------
  // LOAD ALL ORGANISATIONS
  // -----------------------
  async function loadOrganisations() {
    tbody.innerHTML = `<tr><td colspan="7" class="text-center py-4"><i class="fas fa-spinner fa-spin me-2"></i>Loading...</td></tr>`;
    
    try {
      const res = await fetch("/api/superadmin/organisations");

      if (!res.ok) throw new Error("Failed to load organisations");
      const data = await res.json();
      
      if (!Array.isArray(data) || !data.length) {
        tbody.innerHTML = `<tr><td colspan="7" class="text-center py-4 text-muted">No organisations found.</td></tr>`;
        return;
      }

      tbody.innerHTML = "";
      data.forEach((org, i) => {
        const row = buildOrganisationRow(org, i);
        tbody.insertAdjacentHTML("beforeend", row);
      });
    } catch (err) {
      console.error("Error loading organisations", err);
      showToast("error", err.message || "Failed to load organisations");
      tbody.innerHTML = `<tr><td colspan="7" class="text-danger text-center py-4">Error loading data.</td></tr>`;
    }
  }

  // -----------------------
  // ROW BUILDING
  // -----------------------

  function buildOrganisationRow(org, index) {
    // Calculate verification state
    const panVerified = org.panVerified || false;
    const aadharVerified = org.aadharVerified || false;
    const gstVerified = org.gstVerified || false;
    const tanVerified = org.tanVerified || false;
    
    const verifiedCount = [panVerified, aadharVerified, gstVerified, tanVerified].filter(Boolean).length;
    const allVerified = verifiedCount === 4;
    const hasAllDocValues = org.pan && org.aadhar && org.gst && org.tan;
    const canVerifyAll = !allVerified && hasAllDocValues;

    // Status badge
    const statusBadge = org.isActive
      ? '<span class="badge bg-success-subtle text-success px-3 py-1">Active</span>'
      : '<span class="badge bg-danger-subtle text-danger px-3 py-1">Inactive</span>';

    // Verification summary for Status & Verification column
    const verificationSummary = getVerificationSummary(verifiedCount, allVerified);

    // Build documents column
    const documentsHtml = buildDocumentsColumn(org, panVerified, aadharVerified, gstVerified, tanVerified);

    // Build verification actions (Verify All or All verified label)
    const verifyAllHtml = getVerifyAllHtml(org.id, allVerified, canVerifyAll);

    return `
    <tr class="align-middle">
      <td class="ps-4 text-muted small">${index + 1}</td>
      <td class="org-name-col">
        <div class="fw-bold text-dark mb-1">${escapeHtml(org.name || "")}</div>
        <div class="text-muted small">${escapeHtml(org.domain || "")}</div>
      </td>
      <td class="documents-col">
        ${documentsHtml}
      </td>
      <td class="status-col">
        <div class="mb-2">${statusBadge}</div>
        ${verificationSummary}
        ${verifyAllHtml}
      </td>
      <td class="capacity-col">
        <div class="small fw-bold text-dark">${org.maxEmployees || 0} Employees</div>
      </td>
      <td class="validity-col">
        <div class="small">${escapeHtml(org.validity ? org.validity : "-")}</div>
      </td>
      <td class="text-end pe-4 actions-col">
        <div class="dropdown">
          <button class="btn btn-light btn-sm rounded-circle border shadow-sm" type="button" 
                  data-bs-toggle="dropdown" aria-expanded="false">
            <i class="fas fa-ellipsis-v"></i>
          </button>
          <ul class="dropdown-menu dropdown-menu-end shadow border-0 rounded-3">
            <li><a class="dropdown-item py-2 small" href="javascript:void(0)" onclick="editOrganisation(${org.id})">
              <i class="fa fa-edit text-primary me-2"></i>Edit Details</a></li>
            <li><a class="dropdown-item py-2 small" href="javascript:void(0)" onclick="openManageModulesModal(${org.id}, '${escapeHtml(org.name || "")}')">
              <i class="fa fa-cog text-info me-2"></i>Manage Modules</a></li>
            <li><hr class="dropdown-divider my-1"></li>
            <li><a class="dropdown-item py-2 small" href="javascript:void(0)" onclick="extendValidity(${org.id}, '${org.validity || ""}')">
              <i class="fa fa-calendar-plus text-secondary me-2"></i>Extend Validity</a></li>
            <li><a class="dropdown-item py-2 small" href="javascript:void(0)" onclick="increaseMaxEmployees(${org.id}, ${org.maxEmployees || 0})">
              <i class="fa fa-users text-secondary me-2"></i>Increase Capacity</a></li>
            <li><hr class="dropdown-divider my-1"></li>
            <li><a class="dropdown-item py-2 small ${org.isActive ? "text-danger" : "text-success"}" 
                   href="javascript:void(0)" onclick="toggleOrganisationStatus(${org.id}, ${org.isActive})">
              <i class="fa ${org.isActive ? "fa-ban" : "fa-check"} me-2"></i>
              ${org.isActive ? "Deactivate" : "Activate"} Organisation</a></li>
          </ul>
        </div>
      </td>
    </tr>`;
  }

  
function buildDocumentsColumn(org, panVerified, aadharVerified, gstVerified, tanVerified) {
    const docs = [
        { name: "PAN", value: org.pan, url: org.panUrl, verified: panVerified, type: "PAN" },
        { name: "Aadhaar", value: org.aadhar, url: org.aadharUrl, verified: aadharVerified, type: "AADHAAR" },
        { name: "GST", value: org.gst, url: org.gstUrl, verified: gstVerified, type: "GST" },
        { name: "TAN", value: org.tan, url: org.tanUrl, verified: tanVerified, type: "TAN" }
    ];

    const docRows = docs.map(doc => {
        const valueHtml = formatDocumentValue(doc.value);
        const linkHtml = getDocumentLink(doc.url, doc.name);
        const statusHtml = doc.verified
            ? getVerificationStatusBadge(true)
            : getVerifyButton(org.id, doc.type, doc.value, doc.verified);

        return `
            <div class="doc-row">
                <span class="doc-label">${escapeHtml(doc.name)}:</span>
                <span class="doc-value small">${valueHtml}</span>
                <span class="doc-actions d-inline-flex align-items-center gap-1">
                    ${linkHtml}
                    ${statusHtml}
                </span>
            </div>`;
    }).join("");

    return `<div class="documents-container">${docRows}</div>`;
}

  function getVerificationSummary(verifiedCount, allVerified) {
    if (allVerified) {
      return '<div class="verification-summary text-success small mt-1"><i class="fas fa-check-circle me-1"></i>KYC Complete</div>';
    }
    return `<div class="verification-summary small mt-1">${verifiedCount} of 4 documents verified</div>`;
  }

  function getVerifyAllHtml(orgId, allVerified, canVerifyAll) {
    if (allVerified) {
      return '';
    }
    if (canVerifyAll) {
      return `<button class="btn btn-sm btn-outline-primary mt-2" 
                     onclick="manuallyVerifyAllDocuments(${orgId})" 
                     title="Verify All Documents">
                     <i class="fas fa-check-double me-1"></i>Verify All</button>`;
    }
    return '';
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

    // Disable the button during request to prevent duplicate clicks
    const buttons = document.querySelectorAll(`button[onclick="manuallyVerifyDocument(${orgId}, '${docType}')"]`);
    buttons.forEach(btn => {
      btn.disabled = true;
      btn.innerHTML = '<i class="fas fa-spinner fa-spin me-1"></i>Verifying...';
    });

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
      showToast("success", data.data ? data.data.message : data.message || "Document verified successfully");
      loadOrganisations(); // Refresh the list to show updated state
    } catch (err) {
      console.error("Manual verification failed", err);
      showToast("error", err.message || "Failed to verify document");
      // Re-enable buttons on error
      buttons.forEach(btn => {
        btn.disabled = false;
        btn.innerHTML = '<i class="fas fa-check me-1"></i>Verify';
      });
    }
  };

  /**
   * Manually verify all documents for an organisation
   */
  window.manuallyVerifyAllDocuments = async function (orgId) {
    if (!confirm(`Are you sure you want to manually verify ALL documents for this organisation?`)) {
      return;
    }

    // Disable the Verify All button during request
    const verifyAllBtn = document.querySelector(`button[onclick="manuallyVerifyAllDocuments(${orgId})"]`);
    if (verifyAllBtn) {
      verifyAllBtn.disabled = true;
      verifyAllBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-1"></i>Verifying...';
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
      showToast("success", data.data ? data.data.message : data.message || "All documents verified successfully");
      loadOrganisations(); // Refresh the list to show updated state
    } catch (err) {
      console.error("Manual verify-all failed", err);
      showToast("error", err.message || "Failed to verify all documents");
      // Re-enable button on error
      if (verifyAllBtn) {
        verifyAllBtn.disabled = false;
        verifyAllBtn.innerHTML = '<i class="fas fa-check-double me-1"></i>Verify All';
      }
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
    document.getElementById("manageModulesLabel").innerText = `Manage Modules for ${orgName}`;

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
                ${isChecked ? "" : `<span class="badge bg-light text-muted border ms-2 small">Not Available</span>`}
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
                `<span class="badge bg-light text-muted border ms-2 small">Not Available</span>`);
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
