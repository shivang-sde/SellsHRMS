document.addEventListener("DOMContentLoaded", () => {
    const orgId = localStorage.getItem("orgId");
    if (!orgId) return;

    loadDashboardStats(orgId);
    loadRecentEmployees(orgId);
});

/* ---------------------
   DASHBOARD STATS
---------------------- */
async function loadDashboardStats(orgId) {
    try {
        const empRes = await fetch(`/api/employees/count/${orgId}`);
        const depRes = await fetch(`/api/departments/count/${orgId}`);
        const desRes = await fetch(`/api/designations/count/${orgId}`);
        const orgRes = await fetch(`/api/organisations/${orgId}`);

        document.getElementById("countEmployees").innerText = await empRes.text();
        document.getElementById("countDepartments").innerText = await depRes.text();
        document.getElementById("countDesignations").innerText = await desRes.text();

        const org = await orgRes.json();
        document.getElementById("maxEmpLimit").innerText = org.maxEmployees ?? "--";
    } catch (err) {
        console.error("Dashboard stats failed", err);
    }
}

/* ---------------------
   RECENT EMPLOYEES
---------------------- */
async function loadRecentEmployees(orgId) {
    try {
        const res = await fetch(`/api/employees/recent/${orgId}`);
        const list = await res.json();

        const tbody = document.getElementById("recentEmployeesBody");
        if (!tbody) return;

        tbody.innerHTML = "";

        if (!list.length) {
            tbody.innerHTML = `<tr><td colspan="5" class="text-center">No recent employees found</td></tr>`;
            return;
        }

        list.forEach(emp => {
            tbody.innerHTML += `
                <tr>
                    <td>${emp.fullName}</td>
                    <td>${emp.email}</td>
                    <td>${emp.designationTitle ?? '-'}</td>
                    <td>${emp.departmentName ?? '-'}</td>
                    <td>
                        <a href="/orgadmin/employee/${emp.id}" class="btn btn-sm btn-primary">View</a>
                    </td>
                </tr>`;
        });

    } catch (e) {
        console.error("Recent employees load failed", e);
    }
}
