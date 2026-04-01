<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
  <%@ taglib prefix="app" tagdir="/WEB-INF/tags" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
      <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

        <aside id="hrmsSidebar" class="hrms-sidebar" aria-label="Sidebar">
          <div class="sidebar-inner d-flex flex-column">

            <!-- Navigation -->
            <nav class="nav flex-column nav-pills sidebar-nav mt-2">

              <!-- ================= SUPER ADMIN ================= -->
              <sec:authorize access="hasAuthority('SUPER_ADMIN')">
                <a class="nav-link" href="${pageContext.request.contextPath}/superadmin/dashboard">
                  <i class="fa fa-chart-line"></i> <span>Dashboard</span>
                </a>
                <a class="nav-link" href="${pageContext.request.contextPath}/superadmin/organisations">
                  <i class="fa fa-building"></i> <span>Organisations</span>
                </a>
                <a class="nav-link" href="${pageContext.request.contextPath}/superadmin/orgadmins">
                  <i class="fa fa-users"></i> <span>Org Admins</span>
                </a>
                <a class="nav-link" href="${pageContext.request.contextPath}/superadmin/create-organisation">
                  <i class="fa fa-plus-circle"></i> <span>Create Organisation</span>
                </a>
                <a class="nav-link" href="${pageContext.request.contextPath}/superadmin/permissions">
                  <i class="fa fa-key"></i> <span>Permission</span>
                </a>
              </sec:authorize>

              <!-- ================= ACCOUNTANT ================= -->
              <sec:authorize access="hasAuthority('ACCOUNTANT')">
                <a class="nav-link" href="${pageContext.request.contextPath}/accountant-panel">
                  <i class="fa fa-wallet"></i> Accountant Panel
                </a>
              </sec:authorize>

              <!-- ================= ORG ADMIN ================= -->
              <sec:authorize access="hasAuthority('ORG_ADMIN')">

                <!-- Dashboard -->
                <a class="nav-link toggle-link" href="#">
                  <i class="fa fa-tachometer-alt"></i> Dashboards <i class="fa fa-plus toggle-icon"></i>
                </a>
                <ul class="sub-menu">
                  <li>
                    <a href="${pageContext.request.contextPath}/org/dashboard">
                      <i class="fa fa-tachometer-alt"></i> Dashboard
                    </a>
                  </li>
                  <li>
                    <a href="${pageContext.request.contextPath}/org/attendance-analytics-dashboard">
                      <i class="fa fa-user-check"></i> Attendance & Absenteeism
                    </a>
                  </li>
                </ul>

                <!-- Departments -->
                <app:ifModule code="ORG_STRUCTURE">
                  <a class="nav-link toggle-link" href="#">
                    <i class="fa fa-sitemap"></i> Departments <i class="fa fa-plus toggle-icon"></i>
                  </a>
                  <ul class="sub-menu">
                    <li><a href="${pageContext.request.contextPath}/org/departments">Departments</a></li>
                    <li><a href="${pageContext.request.contextPath}/org/designations">Designations</a></li>
                  </ul>
                </app:ifModule>

                <!-- Roles & Permissions -->
                <app:ifModule code="ROLE_PERMISSION">
                  <a class="nav-link toggle-link" href="#">
                    <i class="fa fa-shield-alt"></i> Roles & Permissions <i class="fa fa-plus toggle-icon"></i>
                  </a>
                  <ul class="sub-menu">
                    <li><a href="${pageContext.request.contextPath}/org/roles">All Roles</a></li>
                    <li><a href="${pageContext.request.contextPath}/org/create-role">Create Role</a></li>
                  </ul>
                </app:ifModule>

                <!-- Employees -->
                <app:ifModule code="EMPLOYEE">
                  <sec:authorize
                    access="hasAnyAuthority('ORG_ADMIN', 'EMPLOYEE_VIEW_ALL', 'EMPLOYEE_VIEW_TEAM', 'EMPLOYEE_CREATE')">
                    <a class="nav-link toggle-link" href="#">
                      <i class="fa fa-users"></i> Employees <i class="fa fa-plus toggle-icon"></i>
                    </a>
                    <ul class="sub-menu">
                      <sec:authorize access="hasAnyAuthority('ORG_ADMIN', 'EMPLOYEE_CREATE')">
                        <li><a href="${pageContext.request.contextPath}/org/create-employee">Add Employee</a></li>
                      </sec:authorize>
                      <sec:authorize access="hasAnyAuthority('ORG_ADMIN', 'EMPLOYEE_VIEW_ALL', 'EMPLOYEE_VIEW_TEAM')">
                        <li><a href="${pageContext.request.contextPath}/org/employees">All Employees</a></li>
                      </sec:authorize>
                    </ul>
                  </sec:authorize>
                </app:ifModule>

                <!-- Attendance -->
                <app:ifModule code="ATTENDANCE">
                  <a class="nav-link toggle-link" href="#">
                    <i class="fa fa-clock"></i> Attendance <i class="fa fa-plus toggle-icon"></i>
                  </a>
                  <ul class="sub-menu">
                    <li><a href="${pageContext.request.contextPath}/org/attendance">Today's Attendance</a></li>
                    <li><a href="${pageContext.request.contextPath}/org/attendance/reports">Reports</a></li>
                    <li><a href="${pageContext.request.contextPath}/org/devices">Devices</a></li>
                  </ul>
                </app:ifModule>

                <!-- Leave -->
                <app:ifModule code="LEAVE">
                  <a class="nav-link toggle-link" href="#">
                    <i class="fa fa-calendar-alt"></i> Leave <i class="fa fa-plus toggle-icon"></i>
                  </a>
                  <ul class="sub-menu">
                    <li><a href="${pageContext.request.contextPath}/org/leaves">Leave Requests</a></li>
                    <li><a href="${pageContext.request.contextPath}/org/leave-types">Leave Types</a></li>
                    <li><a href="${pageContext.request.contextPath}/org/leave-balances">Leave Balances</a></li>
                  </ul>
                </app:ifModule>



                <!-- Organisation Policy -->
                <app:ifModule code="ORG_POLICY">
                  <a class="nav-link" href="${pageContext.request.contextPath}/org/organisation-policy">
                    <i class="fa fa-file-contract"></i> Organisation Policy
                  </a>
                </app:ifModule>

                <!-- Payroll -->
                <app:ifModule code="PAYROLL">
                  <a class="nav-link toggle-link" href="#">
                    <i class="fa fa-wallet"></i> <span class="nav-text">Payroll Management</span>
                    <i class="fa fa-plus toggle-icon"></i>
                  </a>
                  <ul class="sub-menu">
                    <!-- Payroll Setup -->
                    <li class="sub-header text-muted small mt-2">Setup</li>
                    <li>
                      <a href="${pageContext.request.contextPath}/payroll/salary-components">
                        <i class="fa fa-coins me-2 text-primary"></i> <span class="nav-text">Salary Components</span>
                      </a>
                    </li>
                    <li>
                      <a href="${pageContext.request.contextPath}/payroll/salary-structures">
                        <i class="fa fa-layer-group me-2 text-success"></i> <span class="nav-text">Salary
                          Structures</span>
                      </a>
                    </li>
                    <li>
                      <a href="${pageContext.request.contextPath}/payroll/salary-assignments">
                        <i class="fa fa-user-tie me-2 text-info"></i> <span class="nav-text">Employee Assignments</span>
                      </a>
                    </li>

                    <!-- Statutory & Tax -->
                    <li class="sub-header text-muted small mt-2">Statutory & Tax</li>
                    <li>
                      <a href="${pageContext.request.contextPath}/payroll/statutory-tax">
                        <i class="fa fa-balance-scale me-2 text-warning"></i> <span class="nav-text">Statutory & Tax
                          Setup</span>
                      </a>
                    </li>

                    <!-- Template Management (NEW SECTION) -->
                    <li class="sub-header text-muted small mt-2">Templates</li>
                    <li>
                      <a href="${pageContext.request.contextPath}/salary-slip-template/list">
                        <i class="fa fa-file-alt me-2 text-primary"></i> <span class="nav-text">Salary Slip
                          Templates</span>
                      </a>
                    </li>
                    <li>
                      <a href="${pageContext.request.contextPath}/salary-slip-template/design">
                        <i class="fa fa-drafting-compass me-2 text-info"></i> <span class="nav-text">Template
                          Designer</span>
                      </a>
                    </li>

                    <!-- Payroll Operations -->
                    <li class="sub-header text-muted small mt-2">Operations</li>
                    <li>
                      <a href="${pageContext.request.contextPath}/payroll/payruns">
                        <i class="fa fa-play-circle me-2 text-danger"></i> <span class="nav-text">PayRun
                          Dashboard</span>
                      </a>
                    </li>
                  </ul>
                </app:ifModule>

                <!-- Asset Management -->
                <app:ifModule code="ASSET_MANAGEMENT">
                  <sec:authorize access="hasAnyAuthority('ORG_ADMIN','ASSEST_MANAGEMENT')">
                    <a class="nav-link toggle-link" href="#">
                      <i class="fa fa-boxes-stacked"></i> Assets <i class="fa fa-plus toggle-icon"></i>
                    </a>
                    <ul class="sub-menu">
                      <li><a href="${pageContext.request.contextPath}/org/assets">All Assets</a></li>
                      <li><a href="${pageContext.request.contextPath}/org/assets/categories">Categories</a></li>
                      <li><a href="${pageContext.request.contextPath}/org/assets/vendors">Vendors</a></li>
                      <li><a href="${pageContext.request.contextPath}/org/assets/assignments">Assignments</a></li>
                      <li><a href="${pageContext.request.contextPath}/org/assets/maintenance">Maintenance Logs</a></li>
                    </ul>
                  </sec:authorize>
                </app:ifModule>

                <!-- Organisation Hub -->
                <app:ifModule code="ORG_HUB">
                  <sec:authorize access="hasAnyAuthority('ORG_ADMIN','EMPLOYEE')">
                    <a class="nav-link toggle-link" href="#">
                      <i class="fa fa-info-circle"></i> Organisation Hub <i class="fa fa-plus toggle-icon"></i>
                    </a>
                    <ul class="sub-menu">
                      <li><a href="${pageContext.request.contextPath}/org/knowledge-base">Knowledge Base</a></li>
                      <li><a href="${pageContext.request.contextPath}/org/announcements">Announcements</a></li>
                      <li><a href="${pageContext.request.contextPath}/org/events">Events</a></li>
                      <li><a href="${pageContext.request.contextPath}/org/holidays">Holidays</a></li>
                    </ul>
                  </sec:authorize>
                </app:ifModule>

              </sec:authorize>

              <!-- ================= EMPLOYEE ================= -->
              <sec:authorize access="hasAuthority('EMPLOYEE')">
                <app:ifModule code="EMPLOYEE">
                  <a class="nav-link" href="${pageContext.request.contextPath}/employee/dashboard">
                    <i class="fa fa-chart-line"></i> Dashboard
                  </a>
                  <a class="nav-link" href="${pageContext.request.contextPath}/employee/attendance">
                    <i class="fa fa-clock"></i> My Attendance
                  </a>
                  <a class="nav-link" href="${pageContext.request.contextPath}/employee/leave">
                    <i class="fa fa-calendar-check"></i> My Leaves
                  </a>
                  <a class="nav-link" href="${pageContext.request.contextPath}/employee/salaries">
                    <i class="fa fa-wallet"></i> My Salaries
                  </a>
                </app:ifModule>


                <sec:authorize access="hasAnyAuthority('EMPLOYEE_VIEW_ALL', 'EMPLOYEE_VIEW_TEAM')">
                  <a class="nav-link" href="${pageContext.request.contextPath}/org/employees"><i
                      class="fa fa-users"></i> Employees</a>
                </sec:authorize>

                <sec:authorize access="hasAnyAuthority('EMPLOYEE_VIEW_ALL', 'EMPLOYEE_VIEW_TEAM')">
                  <a class="nav-link" href="${pageContext.request.contextPath}/org/attendance"><i
                      class="fa fa-clock"></i>Employee Attendance</a>
                </sec:authorize>

                <sec:authorize access="hasAnyAuthority('EMPLOYEE_VIEW_ALL', 'EMPLOYEE_VIEW_TEAM')">
                  <a class="nav-link" href="${pageContext.request.contextPath}/employee/leaves">
                    <i class="fa fa-calendar-check"></i> Leave Requests
                  </a>
                </sec:authorize>


                <app:ifModule code="PRODUCTIVITY_MANAGEMENT">
                  <a class="nav-link toggle-link" href="#">
                    <i class="fa fa-briefcase"></i> My Work <i class="fa fa-plus toggle-icon"></i>
                  </a>
                  <ul class="sub-menu">
                    <li><a href="${pageContext.request.contextPath}/work/dashboard">Dashboard</a></li>
                    <li><a href="${pageContext.request.contextPath}/work/projects">Projects</a></li>
                    <li><a href="${pageContext.request.contextPath}/work/tasks">Tasks</a></li>
                    <li><a href="${pageContext.request.contextPath}/work/tickets">Tickets</a></li>
                  </ul>
                </app:ifModule>
              </sec:authorize>

            </nav>
          </div>
        </aside>