// /js/employee.js
// Handles listing + create/edit/delete for employees (no file uploads).
// Uses organisationId from hidden input #organisationId (populated from sessionScope.ORG_ID).

document.addEventListener("DOMContentLoaded", initEmployeePage);

function initEmployeePage() {
  const orgId = document.getElementById("organisationId")?.value;
  if (!orgId) {
    console.warn("organisationId missing from the page. Ensure sessionScope.ORG_ID is set.");
  }

  // bindings
  document.getElementById("btnNew")?.addEventListener("click", () => openCreateForm());
  document.getElementById("btnRefresh")?.addEventListener("click", () => loadEmployees(orgId));
  document.getElementById("resetBtn")?.addEventListener("click", resetForm);

  const form = document.getElementById("employeeForm");
  if (form) form.addEventListener("submit", (e) => handleSubmit(e, orgId));

  // load initial data
  loadDropdowns(orgId);
  loadEmployees(orgId);
}

/* -------------------------
   LOAD / RENDER EMPLOYEES
   ------------------------- */
async function loadEmployees(orgId) {
  const tbody = document.getElementById("employeeTableBody");
  if (!tbody) {
    console.error("employeeTableBody not found in DOM");
    return;
  }

  tbody.innerHTML = `<tr><td colspan="8" class="text-center">Loading...</td></tr>`;

  try {
    const url = orgId ? `/api/employees/org/${orgId}` : `/api/employees`; // fallback
    const res = await fetch(url);
    if (!res.ok) throw new Error("failed to fetch employees");
    const list = await res.json();

    if (!Array.isArray(list) || list.length === 0) {
      tbody.innerHTML = `<tr><td colspan="8" class="text-center">No employees found</td></tr>`;
      return;
    }

    tbody.innerHTML = list.map((e, idx) => {
      const name = escapeHtml((e.fullName) || `${e.firstName || ""} ${e.lastName || ""}`.trim());
      const dept = escapeHtml(e.department || "-");
      const desg = escapeHtml(e.designation || "-");
      const status = escapeHtml(e.status || "Unknown");
      return `
        <tr>
          <td>${idx + 1}</td>
          <td>${escapeHtml(e.employeeCode || "")}</td>
          <td><a href="/org/employee/${e.id}">${name}</a></td>
          <td>${escapeHtml(e.email || "")}</td>
          <td>${dept}</td>
          <td>${desg}</td>
          <td>${status}</td>
          <td>
            <button class="btn btn-sm btn-primary" onclick="editEmployee(${e.id})">Edit</button>
            <button class="btn btn-sm btn-danger" onclick="deleteEmployee(${e.id})">Delete</button>
            <button class="btn btn-sm btn-outline-secondary" onclick="toggleStatus(${e.id}, '${status}')">Toggle Status</button>
          </td>
        </tr>
      `;
    }).join("");

  } catch (err) {
    console.error(err);
    tbody.innerHTML = `<tr><td colspan="8" class="text-danger text-center">Failed to load employees</td></tr>`;
  }
}

/* -------------------------
   LOAD DROPDOWNS
   ------------------------- */
async function loadDropdowns(orgId) {
  // departments
  try {
    const depRes = await fetch(`/api/departments/org/${orgId}`);
    const departments = depRes.ok ? await depRes.json() : [];
    const depSelect = document.getElementById("departmentId");
    if (depSelect) {
      depSelect.innerHTML = `<option value="">-- Select Department --</option>`;
      departments.forEach(d => depSelect.innerHTML += `<option value="${d.id}">${escapeHtml(d.name)}</option>`);
    }
  } catch (e) { console.warn("load departments failed", e); }

  // designations
  try {
    const desRes = await fetch(`/api/designations/org/${orgId}`);
    const desigs = desRes.ok ? await desRes.json() : [];
    const desSelect = document.getElementById("designationId");
    if (desSelect) {
      desSelect.innerHTML = `<option value="">-- Select Designation --</option>`;
      desigs.forEach(d => desSelect.innerHTML += `<option value="${d.id}">${escapeHtml(d.title || d.name)}</option>`);
    }
  } catch (e) { console.warn("load designations failed", e); }

  // reporting employees (same org)
  try {
    const repRes = await fetch(`/api/employees/org/${orgId}`);
    const reps = repRes.ok ? await repRes.json() : [];
    const repSelect = document.getElementById("reportingToId");
    if (repSelect) {
      repSelect.innerHTML = `<option value="">-- No Manager / Not Assigned --</option>`;
      reps.forEach(r => {
        const label = (r.fullName) ? r.fullName : `${r.firstName || ""} ${r.lastName || ""}`.trim();
        repSelect.innerHTML += `<option value="${r.id}">${escapeHtml(label)}</option>`;
      });
    }
  } catch (e) { console.warn("load reporting employees failed", e); }

  // shifts (if endpoint exists)
  try {
    const sres = await fetch(`/api/shifts`);
    const shifts = sres.ok ? await sres.json() : [];
    const shiftSelect = document.getElementById("shiftId");
    if (shiftSelect) {
      shiftSelect.innerHTML = `<option value="">-- Select Shift --</option>`;
      shifts.forEach(s => shiftSelect.innerHTML += `<option value="${s.id}">${escapeHtml(s.name)}</option>`);
    }
  } catch (e) { /* optional */ }
}

/* -------------------------
   CREATE / UPDATE HANDLER
   ------------------------- */
async function handleSubmit(event, orgId) {
  event.preventDefault();

  const empId = document.getElementById("empId")?.value;
  const payload = buildRequestPayload(orgId);
  // basic validation
  if (!payload.firstName || !payload.workEmail && !payload.phone) {
    showFormMsg("First name and either work email or phone are required.");
    return;
  }

  try {
    const url = empId ? `/api/employees/${empId}` : `/api/employees`;
    const method = empId ? "PUT" : "POST";

    const res = await fetch(url, {
      method,
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });

    if (!res.ok) {
      const txt = await res.text();
      throw new Error(`Server returned ${res.status}: ${txt}`);
    }

    resetForm();
    await loadEmployees(orgId);
    showToast("success", empId ? "Employee updated" : "Employee created");
  } catch (err) {
    console.error(err);
    showFormMsg("Save failed: " + (err.message || "unknown"));
  }
}

/* -------------------------
   FILL FORM FOR EDIT
   ------------------------- */
async function editEmployee(id) {
  try {
    const res = await fetch(`/api/employees/${id}`);
    if (!res.ok) throw new Error("employee not found");
    const e = await res.json();

    document.getElementById("empId").value = e.id;
    document.getElementById("firstName").value = e.firstName || "";
    document.getElementById("lastName").value = e.lastName || "";
    document.getElementById("personalEmail").value = e.personalEmail || e.email || "";
    document.getElementById("phone").value = e.phone || "";
    // addresses (we used single-field address inputs)
    document.getElementById("localAddress").value = (e.localAddress && (e.localAddress.line1 || "")) || "";
    document.getElementById("permanentAddress").value = (e.permanentAddress && (e.permanentAddress.line1 || "")) || "";
    document.getElementById("employeeCode").value = e.employeeCode || "";
    document.getElementById("dateOfJoining").value = e.dateOfJoining || "";
    document.getElementById("employmentType").value = e.employmentType || "";
    document.getElementById("status").value = e.status || "Active";
    document.getElementById("departmentId").value = e.departmentId || "";
    document.getElementById("designationId").value = e.designationId || "";
    document.getElementById("reportingToId").value = e.reportingToId || "";
    document.getElementById("shiftId").value = e.shiftId || "";
    document.getElementById("workEmail").value = e.email || "";
    document.getElementById("password").value = "";

    document.getElementById("formTitle").innerText = "Edit Employee";
    window.scrollTo({ top: 0, behavior: 'smooth' });

  } catch (err) {
    console.error(err);
    showToast("error", "Failed to load employee for edit");
  }
}

/* -------------------------
   DELETE (soft delete)
   ------------------------- */
async function deleteEmployee(id) {
  if (!confirm("Are you sure you want to delete this employee? This will soft-delete (flag) the record.")) return;
  try {
    const res = await fetch(`/api/employees/${id}`, { method: "DELETE" });
    if (!res.ok) throw new Error("Delete failed");
    const orgId = document.getElementById("organisationId")?.value;
    await loadEmployees(orgId);
    showToast("success", "Employee deleted (soft)");
  } catch (err) {
    console.error(err);
    showToast("error", "Delete failed");
  }
}

/* -------------------------
   Toggle status (example: Active <-> Inactive)
   ------------------------- */
async function toggleStatus(id, currentStatus) {
  const orgId = document.getElementById("organisationId")?.value;
  // decide next status - simple cycle
  const states = ["Active", "Inactive", "Suspended", "Terminated"];
  let i = states.indexOf(currentStatus);
  i = i === -1 ? 0 : (i + 1) % states.length;
  const next = states[i];

  try {
    const res = await fetch(`/api/employees/${id}/status?status=${encodeURIComponent(next)}`, {
      method: "PATCH"
    });
    if (!res.ok) throw new Error("status update failed");
    await loadEmployees(orgId);
    showToast("success", `Status set to ${next}`);
  } catch (err) {
    console.error(err);
    showToast("error", "Status update failed");
  }
}

/* -------------------------
   HELPERS
   ------------------------- */
function buildRequestPayload(orgId) {
  // For embedded addresses we use a minimal DTO structure expected by EmployeeCreateRequest
  function makeAddress(singleLineId) {
    const v = document.getElementById(singleLineId)?.value || "";
    return {
      addressLine1: v,
      addressLine2: "",
      city: "",
      state: "",
      country: "",
      pincode: ""
    };
  }

  return {
    // PERSONAL
    firstName: document.getElementById("firstName")?.value || "",
    lastName: document.getElementById("lastName")?.value || "",
    dob: document.getElementById("dob")?.value || null,
    gender: document.getElementById("gender")?.value || null,
    personalEmail: document.getElementById("personalEmail")?.value || null,
    phone: document.getElementById("phone")?.value || null,
    alternatePhone: null,
    fatherName: null,
    nationality: null,
    maritalStatus: null,
    referenceName: null,
    referencePhone: null,

    // ADDRESS
    localAddress: makeAddress("localAddress"),
    permanentAddress: makeAddress("permanentAddress"),

    // COMPANY
    employeeCode: document.getElementById("employeeCode")?.value || null,
    dateOfJoining: document.getElementById("dateOfJoining")?.value || null,
    dateOfExit: null,
    employmentType: document.getElementById("employmentType")?.value || null,
    status: document.getElementById("status")?.value || "Active",

    organisationId: orgId || document.getElementById("organisationId")?.value || null,
    departmentId: parseNullableLong("departmentId"),
    designationId: parseNullableLong("designationId"),
    reportingToId: parseNullableLong("reportingToId"),
    shiftId: parseNullableLong("shiftId"),

    // ACCOUNT (optional)
    workEmail: document.getElementById("workEmail")?.value || null,
    password: document.getElementById("password")?.value || null
  };
}

function parseNullableLong(id) {
  const v = document.getElementById(id)?.value;
  if (!v || v === "") return null;
  const n = Number(v);
  return Number.isNaN(n) ? null : n;
}

function resetForm() {
  document.getElementById("employeeForm").reset();
  document.getElementById("empId").value = "";
  document.getElementById("formTitle").innerText = "Create Employee";
  hideFormMsg();
}

function openCreateForm() {
  resetForm();
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

function showFormMsg(msg) {
  const el = document.getElementById("formMsg");
  if (!el) return;
  el.style.display = "block";
  el.innerText = msg;
}

function hideFormMsg() {
  const el = document.getElementById("formMsg");
  if (!el) return;
  el.style.display = "none";
  el.innerText = "";
}

function showToast(type, msg) {
  // reuse the small toast helper in your layout if available, else minimal fallback
  if (window.showToast) {
    window.showToast(type, msg);
    return;
  }
  alert(msg);
}

function escapeHtml(s) {
  if (s == null) return "";
  return String(s).replace(/[&<>"']/g, c => ({
    '&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'
  }[c]));
}
