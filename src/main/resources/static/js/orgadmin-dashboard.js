document.addEventListener("DOMContentLoaded", () => {
    const orgId = window.APP.ORG_ID || document.getElementById("globalOrgId")?.value;
    if (!orgId) return;

    console.log("Initializing premium dashboard for orgId:", orgId);
    
    // Initial data load
    loadDashboardStats(orgId);
    
    // Optional: Add auto-refresh every 5 minutes
    // setInterval(() => loadDashboardStats(orgId), 300000);
});

/* ---------------------
   DASHBOARD STATS
---------------------- */
async function loadDashboardStats(orgId) {
    try {
        const org = await orgApi.getDetails();
        
        // Populate KPI cards with a simple "count up" feel if needed, 
        // but for now, direct update is cleaner.
        updateKpiValue("countEmployees", org.totalEmployees);
        updateKpiValue("countDepartments", org.totalDepartments);
        updateKpiValue("maxEmpLimit", org.maxEmployees ?? "--");
        
        console.log("Dashboard stats synchronized successfully.");
    } catch (err) {
        showToast("error", "Failed to sync dashboard metrics");
        console.error("Dashboard stats sync failed:", err);
    }
}

function updateKpiValue(id, value) {
    const el = document.getElementById(id);
    if (el) {
        el.style.opacity = 0;
        setTimeout(() => {
            el.innerText = value;
            el.style.transition = "opacity 0.3s ease";
            el.style.opacity = 1;
        }, 100);
    }
}

/* ---------------------
   RECENT EMPLOYEES (Archived)
---------------------- */
// async function loadRecentEmployees(orgId) {
//     try {
//         const res = await fetch(`/api/employees/recent/${orgId}`);
//         const list = await res.json();
//         const tbody = document.getElementById("recentEmployeesBody");
//         if (!tbody) return;
//         tbody.innerHTML = "";
//         if (!list.length) {
//             tbody.innerHTML = `<tr><td colspan="5" class="text-center">No recent employees found</td></tr>`;
//             return;
//         }
//         list.forEach(emp => {
//             tbody.innerHTML += `
//                 <tr>
//                     <td>${emp.fullName}</td>
//                     <td>${emp.email}</td>
//                     <td>${emp.designationTitle ?? '-'}</td>
//                     <td>${emp.departmentName ?? '-'}</td>
//                     <td><a href="/orgadmin/employee/${emp.id}" class="btn btn-sm btn-primary">View</a></td>
//                 </tr>`;
//         });
//     } catch (e) {
//         console.error("Recent employees load failed", e);
//     }
// }
