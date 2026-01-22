(function () {

    const ctx = {
        orgId: window.APP?.ORG_ID || null,
        table: document.getElementById("designationTableBody"),
        modal: null,
        departments: [],
        roles: [],
        currentRoleId: null,

    };

    document.addEventListener("DOMContentLoaded", async () => {
        ctx.modal = new bootstrap.Modal(document.getElementById("desigModal"));

        // Load initial data
        await Promise.allSettled([loadDepartments(), loadRoles(), loadDesignations()]);

        // Button bindings
        document.querySelector("[data-open-desig-create]")
            ?.addEventListener("click", openCreate);

        document.getElementById("desigModalForm")
            .addEventListener("submit", saveDesignation);

        document.getElementById("refreshDeptBtn")
            ?.addEventListener("click", reloadDepartments);

        document.getElementById("refreshRoleBtn")
            ?.addEventListener("click", reloadRoles);
    });

    // --- Load Departments ---
    async function loadDepartments() {
        const res = await fetch(`/api/departments/org/${ctx.orgId}`);
        ctx.departments = await res.json();
        populateDeptDropdown();
    }

    function populateDeptDropdown() {
        const select = document.getElementById("departmentSelect");
        select.innerHTML = `<option value="">Select Department...</option>` +
            ctx.departments.map(d => `<option value="${d.id}">${escape(d.name)}</option>`).join("");
    }

    async function reloadDepartments() {
        const btn = document.getElementById("refreshDeptBtn");
        btn.disabled = true;
        btn.innerHTML = `<i class="bi bi-arrow-repeat spin"></i> Refreshing...`;
        await loadDepartments();
        btn.disabled = false;
        btn.innerHTML = `<i class="bi bi-arrow-repeat"></i> Refresh`;
    }

    // --- Load Roles ---
    async function loadRoles(currentRoleId = null) {
        const res = await fetch(`/api/roles/org/${ctx.orgId}`);
        let roles = await res.json();


        // Keep only unassigned roles + current role (when editing)
        roles = roles.filter(r => !r.designationId || r.id === currentRoleId);

        ctx.roles = roles;
        populateRoleDropdown(currentRoleId);
    }

    function populateRoleDropdown(currentRoleId = null) {
        const select = document.getElementById("roleSelect");
        select.innerHTML = `<option value="">Select Role...</option>` +
            ctx.roles.map(r => `
                <option value="${r.id}" ${r.id === currentRoleId ? "selected" : ""}>
                    ${escape(r.name)}${r.designationId && r.id !== currentRoleId ? " (Assigned)" : ""}
                </option>
            `).join("");
    }

    async function reloadRoles() {
        const btn = document.getElementById("refreshRoleBtn");
        btn.disabled = true;
        btn.innerHTML = `<i class="bi bi-arrow-repeat spin"></i> Refreshing...`;
        await loadRoles(ctx.currentRoleId);
        btn.disabled = false;
        btn.innerHTML = `<i class="bi bi-arrow-repeat"></i> Refresh`;
    }

    // --- Load Designations ---
    async function loadDesignations() {
        ctx.table.innerHTML = `<tr><td colspan="5" class="text-center">Loading...</td></tr>`;
        const res = await fetch(`/api/designations/org/${ctx.orgId}`);
        const list = await res.json();

        ctx.table.innerHTML = list.length
            ? list.map(row).join("")
            : `<tr><td colspan="5" class="text-center">No records</td></tr>`;
    }

    function row(d) {
        return `
        <tr>
           <td>${escape(d.departmentName || "-")}</td>
           <td>${escape(d.roleName || "-")}</td>
           <td>${escape(d.title)}</td>
           <td>${escape(d.description || "")}</td>
           <td>
             <button class="btn btn-sm btn-primary" onclick="openDesigEdit(${d.id})">Edit</button>
             <button class="btn btn-sm btn-danger" onclick="deleteDesignation(${d.id})">Delete</button>
           </td>
        </tr>`;
    }

    // --- Modal: Create/Edit ---
    async function openCreate() {
        await Promise.all([loadDepartments(), loadRoles()]);

        document.getElementById("desigId").value = "";
        document.getElementById("departmentSelect").value = "";
        document.getElementById("roleSelect").value = "";
        document.getElementById("desigTitleInput").value = "";
        document.getElementById("desigDescInput").value = "";

        document.getElementById("desigModalTitle").innerText = "Add Designation";
        ctx.modal.show();
    }

    window.openDesigEdit = async function (id) {
        const res = await fetch(`/api/designations/${id}`);
        const d = await res.json();

        ctx.currentRoleId = d.roleId || null;

        // reload lists, keeping current role visible
        await Promise.all([loadDepartments(), loadRoles(d.roleId)]);

        document.getElementById("desigId").value = d.id;
        document.getElementById("departmentSelect").value = d.departmentId || "";
        document.getElementById("roleSelect").value = d.roleId || "";
        document.getElementById("desigTitleInput").value = d.title;
        document.getElementById("desigDescInput").value = d.description || "";

        document.getElementById("desigModalTitle").innerText = "Edit Designation";
        ctx.modal.show();
    };

    // --- Save / Update ---
    async function saveDesignation(e) {
        e.preventDefault();

        const id = document.getElementById("desigId").value;
        const deptId = document.getElementById("departmentSelect").value;
        const roleId = document.getElementById("roleSelect").value;

        if (!deptId) {
            alert("Please select a Department");
            return;
        }
        if (!roleId) {
            alert("Please select a Role");
            return;
        }

        const payload = {
            id: id || null,
            title: document.getElementById("desigTitleInput").value.trim(),
            description: document.getElementById("desigDescInput").value.trim(),
            departmentId: deptId,
            roleId: roleId,
            orgId: ctx.orgId
        };

        const res = await fetch(
            id ? `/api/designations/${id}` : `/api/designations`,
            {
                method: id ? "PATCH" : "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            }
        );

        if (!res.ok) {
            showToast("error", "Failed to save designation");
            return;
        }

        showToast("success", "Designation saved successfully");
        ctx.modal.hide();

        // reload data to reflect latest changes
        await loadDesignations();
        await loadRoles(); // refresh available roles list
    }

    // --- Delete ---
    window.deleteDesignation = async function (id) {
        if (!confirm("Delete this designation?")) return;

        const res = await fetch(`/api/designations/${id}`, { method: "DELETE" });
        if (!res.ok) {
            showToast("error", "Delete failed");
            return;
        }

        showToast("success", "Designation deleted");
        await loadDesignations();
        await loadRoles(); // refresh roles availability
    };

    // --- Utility ---
    function escape(s) {
        return String(s || "").replace(/[&<>"]/g, c =>
            ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;" }[c])
        );
    }

})();
