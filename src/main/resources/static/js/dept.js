(function () {

    const ctx = {
        orgId: window.APP?.ORG_ID || null,
        table: document.getElementById("departmentTableBody"),
        modal: null
    };

    document.addEventListener("DOMContentLoaded", () => {

        ctx.modal = new bootstrap.Modal(document.getElementById("deptModal"));

        loadDepartments();

        document.querySelector("[data-open-dept-create]")
            ?.addEventListener("click", openCreate);

        document.getElementById("deptModalForm")
            .addEventListener("submit", saveDepartment);
    });

    async function loadDepartments() {
        ctx.table.innerHTML = `<tr><td colspan="4" class="text-center">Loading...</td></tr>`;
        const res = await fetch(`/api/departments/org/${ctx.orgId}`);
        const list = await res.json();

        ctx.table.innerHTML = list.length
            ? list.map(row).join("")
            : `<tr><td colspan="3" class="text-center">No records</td></tr>`;
    }

    function row(d) {
        return `
        <tr>
           <td>${escape(d.name)}</td>
           <td>${escape(d.description || "")}</td>
           <td>
             <button class="btn btn-sm btn-primary" onclick="openDeptEdit(${d.id})">Edit</button>
             <button class="btn btn-sm btn-danger" onclick="deleteDepartment(${d.id})">Delete</button>
           </td>
        </tr>`;
    }

    function openCreate() {
        document.getElementById("deptId").value = "";
        document.getElementById("deptNameInput").value = "";
        document.getElementById("deptDescInput").value = "";
        document.getElementById("deptModalTitle").innerText = "Add Department";

        ctx.modal.show();
    }

    window.openDeptEdit = async function (id) {
        const res = await fetch(`/api/departments/${id}`);
        const d = await res.json();

        document.getElementById("deptId").value = d.id;
        document.getElementById("deptNameInput").value = d.name;
        document.getElementById("deptDescInput").value = d.description || "";
        document.getElementById("deptModalTitle").innerText = "Edit Department";

        ctx.modal.show();
    };

    async function saveDepartment(e) {
        e.preventDefault();

        const id = document.getElementById("deptId").value;

        const payload = {
            name: document.getElementById("deptNameInput").value,
            description: document.getElementById("deptDescInput").value,
            orgId: ctx.orgId
        };

        const res = await fetch(
            id ? `/api/departments/${id}` : `/api/departments`,
            {
                method: id ? "PATCH" : "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            }
        );

        if (!res.ok) alert("Error saving");

        ctx.modal.hide();
        loadDepartments();
    }

    window.deleteDepartment = async function (id) {
        if (!confirm("Delete department?")) return;

        const res = await fetch(`/api/departments/${id}`, { method: "DELETE" });
        if (!res.ok) alert("Delete failed");

        loadDepartments();
    };

    function escape(s) {
        return String(s).replace(/[&<>"]/g, c =>
            ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;" }[c])
        );
    }

})();
