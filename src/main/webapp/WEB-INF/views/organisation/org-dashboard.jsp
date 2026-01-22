<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="pageTitle" value="Organisation Admin Dashboard" />
<c:set var="pageScript" value="dashboard" />
<div class="container-fluid mt-4">

    <!-- HEADER -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h3 class="fw-bold">Organisation Admin Dashboard</h3>

        <button class="btn btn-primary" onclick="location.href='/orgadmin/employee/create'">
            <i class="fas fa-user-plus"></i> Add Employee
        </button>
    </div>

    <!-- STAT CARDS -->
    <div class="row g-3">

        <div class="col-md-3">
            <div class="card shadow-sm border-0">
                <div class="card-body">
                    <h6>Total Employees</h6>
                    <h3 id="countEmployees">0</h3>
                </div>
            </div>
        </div>

        <div class="col-md-3">
            <div class="card shadow-sm border-0">
                <div class="card-body">
                    <h6>Departments</h6>
                    <h3 id="countDepartments">0</h3>
                </div>
            </div>
        </div>

        <div class="col-md-3">
            <div class="card shadow-sm border-0">
                <div class="card-body">
                    <h6>Designations</h6>
                    <h3 id="countDesignations">0</h3>
                </div>
            </div>
        </div>

        <div class="col-md-3">
            <div class="card shadow-sm border-0">
                <div class="card-body">
                    <h6>Max Employee Limit</h6>
                    <h3 id="maxEmpLimit">--</h3>
                </div>
            </div>
        </div>
    </div>

    <!-- QUICK ACTIONS -->
    <div class="card mt-4 shadow-sm border-0">
        <div class="card-header bg-light">
            <h5 class="m-0">Quick Actions</h5>
        </div>
        <div class="card-body">
            <div class="d-flex flex-wrap gap-3">
                <a href="/orgadmin/department" class="btn btn-outline-primary"><i class="fas fa-building"></i> Manage Departments</a>
                <a href="/orgadmin/designation" class="btn btn-outline-primary"><i class="fas fa-id-badge"></i> Manage Designations</a>
                <a href="/orgadmin/employees" class="btn btn-outline-primary"><i class="fas fa-users"></i> Employee List</a>
            </div>
        </div>
    </div>

    <!-- RECENT EMPLOYEES -->
    <div class="card mt-4 shadow-sm border-0">
        <div class="card-header bg-light">
            <h5 class="m-0">Recently Added Employees</h5>
        </div>
        <div class="card-body p-0">
            <table class="table table-striped mb-0">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Designation</th>
                    <th>Department</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody id="recentEmployeesBody">
                <tr><td colspan="5" class="text-center">Loading...</td></tr>
                </tbody>
            </table>
        </div>
    </div>

</div>

<script>
document.addEventListener("DOMContentLoaded", () => {
    loadDashboardStats();
    loadRecentEmployees();
});

// Load statistics
async function loadDashboardStats() {
    const orgId = localStorage.getItem("orgId");

    try {
        const empRes = await fetch(`/api/employees/count/${orgId}`);
        const depRes = await fetch(`/api/departments/count/${orgId}`);
        const desRes = await fetch(`/api/designations/count/${orgId}`);
        const orgRes = await fetch(`/api/organisations/${orgId}`);

        document.getElementById("countEmployees").innerText = await empRes.text();
        document.getElementById("countDepartments").innerText = await depRes.text();
        document.getElementById("countDesignations").innerText = await desRes.text();

        const org = await orgRes.json();
        document.getElementById("maxEmpLimit").innerText = org.maxEmployees;

    } catch (err) {
        console.error("Dashboard stats failed", err);
    }
}

// Load recent employees
async function loadRecentEmployees() {
    const orgId = localStorage.getItem("orgId");

    try {
        const res = await fetch(`/api/employees/recent/${orgId}`);
        const list = await res.json();

        const tbody = document.getElementById("recentEmployeesBody");
        tbody.innerHTML = "";

        if (list.length === 0) {
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
</script>
