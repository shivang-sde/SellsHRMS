<%-- Sidebar content for Organisation Admin --%>

    <div class="list-group list-group-flush mx-3 mt-4">
        <a href="/orgadmin/dashboard" class="list-group-item list-group-item-action py-2 ripple">
            <i class="bi bi-speedometer2 me-3"></i><span>Dashboard</span>
        </a>

        <h6 class="sidebar-heading px-3 mt-3 mb-1 text-muted">EMPLOYEE MANAGEMENT</h6>
        <a href="/orgadmin/employees" class="list-group-item list-group-item-action py-2 ripple">
            <i class="bi bi-person-lines-fill me-3"></i><span>All Employees</span>
        </a>
        <a href="/orgadmin/create-employee" class="list-group-item list-group-item-action py-2 ripple">
            <i class="bi bi-person-plus me-3"></i><span>Add Employee</span>
        </a>

        <h6 class="sidebar-heading px-3 mt-3 mb-1 text-muted">ORG SETUP (Optional)</h6>
        <a href="/orgadmin/departments" class="list-group-item list-group-item-action py-2 ripple">
            <i class="bi bi-diagram-2 me-3"></i><span>Departments</span>
        </a>
        <a href="/orgadmin/settings" class="list-group-item list-group-item-action py-2 ripple">
            <i class="bi bi-gear me-3"></i><span>Organisation Settings</span>
        </a>
    </div>