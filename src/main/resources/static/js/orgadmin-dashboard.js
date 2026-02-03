document.addEventListener("DOMContentLoaded", () => {
    const orgId = window.APP.ORG_ID || document.getElementById("globalOrgId")?.value;
    if (!orgId) return;

    console.log("Loading org admin dashboard for orgId:", orgId);
    loadDashboardStats(orgId);
    loadRecentEmployees(orgId);
});

/* ---------------------
   DASHBOARD STATS
---------------------- */
async function loadDashboardStats(orgId) {
  console.log("Loading dashboard stats for orgId:", orgId);
  try {
    const org = await orgApi.getDetails(); // ✅ No .json()
    console.log("Org details:", org);

    document.getElementById("countEmployees").innerText = org.totalEmployees;
    document.getElementById("countDepartments").innerText = org.totalDepartments;
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
