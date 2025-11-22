<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
    <%-- This single file contains all dynamic sidebar links for all roles --%>

        <div class="list-group list-group-flush mx-3 mt-4">

            <a href="/dashboard" class="list-group-item list-group-item-action py-2 ripple">
                <i class="bi bi-speedometer2 me-3"></i><span>Dashboard</span>
            </a>

            <hr />

            <sec:authorize access="hasAuthority('SUPER_ADMIN')">
                <h6 class="sidebar-heading px-3 mt-3 mb-1 text-muted">GLOBAL ADMINISTRATION</h6>
                <a href="/superadmin/organisations" class="list-group-item list-group-item-action py-2 ripple">
                    <i class="bi bi-building me-3"></i><span>Organisations</span>
                </a>
                <a href="/superadmin/orgadmins" class="list-group-item list-group-item-action py-2 ripple">
                    <i class="bi bi-people me-3"></i><span>Org Admins</span>
                </a>
                <a href="/superadmin/create-organisation" class="list-group-item list-group-item-action py-2 ripple">
                    <i class="bi bi-plus-circle me-3"></i><span>New Organisation</span>
                </a>
                <a href="/superadmin/create-orgadmin" class="list-group-item list-group-item-action py-2 ripple">
                    <i class="bi bi-person-plus me-3"></i><span>New Org Admin</span>
                </a>
            </sec:authorize>

            <sec:authorize access="hasAuthority('ORG_ADMIN')">
                <h6 class="sidebar-heading px-3 mt-3 mb-1 text-muted">EMPLOYEE MANAGEMENT</h6>
                <a href="/orgadmin/employees" class="list-group-item list-group-item-action py-2 ripple">
                    <i class="bi bi-person-lines-fill me-3"></i><span>All Employees</span>
                </a>
                <a href="/orgadmin/create-employee" class="list-group-item list-group-item-action py-2 ripple">
                    <i class="bi bi-person-plus me-3"></i><span>Add Employee</span>
                </a>

                <h6 class="sidebar-heading px-3 mt-3 mb-1 text-muted">ORG SETUP</h6>
                <a href="/orgadmin/departments" class="list-group-item list-group-item-action py-2 ripple">
                    <i class="bi bi-diagram-2 me-3"></i><span>Departments</span>
                </a>
                <a href="/orgadmin/settings" class="list-group-item list-group-item-action py-2 ripple">
                    <i class="bi bi-gear me-3"></i><span>Organisation Settings</span>
                </a>
            </sec:authorize>

            <sec:authorize access="hasAuthority('EMPLOYEE')">
                <h6 class="sidebar-heading px-3 mt-3 mb-1 text-muted">EMPLOYEE PORTAL</h6>
                <a href="/employee/profile" class="list-group-item list-group-item-action py-2 ripple">
                    <i class="bi bi-person-circle me-3"></i><span>My Profile</span>
                </a>
                <a href="/employee/leaves" class="list-group-item list-group-item-action py-2 ripple">
                    <i class="bi bi-calendar-minus me-3"></i><span>My Leaves</span>
                </a>
            </sec:authorize>

        </div>