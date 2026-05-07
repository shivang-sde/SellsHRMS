<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
  <%@ taglib prefix="app" tagdir="/WEB-INF/tags" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
      <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

        <aside id="hrmsSidebar" class="hrms-sidebar">
          <div class="sidebar-header">
            <c:choose>
              <c:when test="${not empty sessionScope.LOGO_URL}">
                <img src="${sessionScope.LOGO_URL}" alt="Logo" class="sidebar-logo">
              </c:when>
              <c:otherwise>
                <div class="sidebar-brand">
                  <span class="brand-text">Sells<span class="text-slate-400">HRMS</span></span>
                </div>
              </c:otherwise>
            </c:choose>
          </div>
          <div class="sidebar-inner">
            <nav class="sidebar-nav">

              <!-- ================= SUPER ADMIN ================= -->
              <sec:authorize access="hasAuthority('SUPER_ADMIN')">
                <div class="nav-group-label">Administration</div>
                <a class="nav-link" href="${pageContext.request.contextPath}/superadmin/dashboard">
                  <i class="fa-solid fa-gauge"></i> <span class="nav-text">Dashboard</span>
                </a>
                <a class="nav-link" href="${pageContext.request.contextPath}/superadmin/organisations">
                  <i class="fa-solid fa-building-columns"></i> <span class="nav-text">Organisations</span>
                </a>
                <a class="nav-link" href="${pageContext.request.contextPath}/superadmin/orgadmins">
                  <i class="fa-solid fa-users-gear"></i> <span class="nav-text">Org Admins</span>
                </a>
                <a class="nav-link" href="${pageContext.request.contextPath}/superadmin/create-organisation">
                  <i class="fa-solid fa-plus"></i> <span class="nav-text">Create Organisation</span>
                </a>
                <a class="nav-link" href="${pageContext.request.contextPath}/superadmin/permissions">
                  <i class="fa-solid fa-shield-check"></i> <span class="nav-text">Permission</span>
                </a>
                <div class="nav-group">
                  <a class="nav-link toggle-link" href="javascript:void(0)">
                    <i class="fa-solid fa-bell"></i> <span class="nav-text">Notification</span>
                    <i class="fa-solid fa-chevron-right ms-auto small toggle-chevron"></i>
                  </a>
                  <ul class="sub-menu">
                    <li><a href="${pageContext.request.contextPath}/superadmin/notifications/templates">Templates</a>
                    </li>
                    <li><a href="${pageContext.request.contextPath}/superadmin/notifications/events">Events</a></li>
                  </ul>
                </div>

                <!-- In sidebar.jsp - Under SUPER ADMIN section -->

                <div class="nav-group">
                  <a class="nav-link toggle-link" href="javascript:void(0)">
                    <i class="fa-solid fa-globe"></i> <span class="nav-text">URL Monitor (All)</span>
                    <i class="fa-solid fa-chevron-right ms-auto small toggle-chevron"></i>
                  </a>
                  <ul class="sub-menu">
                    <li><a href="${pageContext.request.contextPath}/superadmin/monitor/dashboard">Global Dashboard</a>
                    </li>
                    <li><a href="${pageContext.request.contextPath}/superadmin/monitor/urls">All URLs</a></li>
                    <li><a href="${pageContext.request.contextPath}/superadmin/monitor/incidents">All Incidents</a></li>
                  </ul>
                </div>
              </sec:authorize>

              <!-- ================= ACCOUNTANT ================= -->
              <sec:authorize access="hasAuthority('ACCOUNTANT')">
                <div class="nav-group-label">Accounting</div>
                <a class="nav-link" href="${pageContext.request.contextPath}/accountant-panel">
                  <i class="fa-solid fa-calculator"></i> <span class="nav-text">Accountant Panel</span>
                </a>
              </sec:authorize>

              <!-- ================= ORG ADMIN ================= -->
              <sec:authorize access="hasAuthority('ORG_ADMIN')">
                <div class="nav-group-label">Organization</div>
                <div class="nav-group">
                  <a class="nav-link toggle-link" href="javascript:void(0)">
                    <i class="fa-solid fa-chart-line"></i> <span class="nav-text">Dashboards</span>
                    <i class="fa-solid fa-chevron-right ms-auto small toggle-chevron"></i>
                  </a>
                  <ul class="sub-menu">
                    <li><a href="${pageContext.request.contextPath}/org/dashboard">Home</a></li>
                    <li><a href="${pageContext.request.contextPath}/org/attendance-analytics-dashboard">Attendance</a>
                    </li>
                  </ul>
                </div>

                <app:ifModule code="ORG_STRUCTURE">
                  <div class="nav-group">
                    <a class="nav-link toggle-link" href="javascript:void(0)">
                      <i class="fa-solid fa-sitemap"></i> <span class="nav-text">Organisation</span>
                      <i class="fa-solid fa-chevron-right ms-auto small toggle-chevron"></i>
                    </a>
                    <ul class="sub-menu">
                      <li><a href="${pageContext.request.contextPath}/org/roles">Roles</a></li>
                      <li><a href="${pageContext.request.contextPath}/org/departments">Departments</a></li>
                      <li><a href="${pageContext.request.contextPath}/org/designations">Designations</a></li>
                      <app:ifModule code="ORG_POLICY">
                        <li><a href="${pageContext.request.contextPath}/org/organisation-policy">Settings</a></li>
                      </app:ifModule>
                      <li><a href="${pageContext.request.contextPath}/org/notifications/smtp-settings">SMTP Settings</a>
                      </li>
                      <li><a href="${pageContext.request.contextPath}/org/notifications/preferences">Notification
                          Prefs</a></li>
                    </ul>
                  </div>
                </app:ifModule>

                <div class="nav-group-label">People</div>

                <app:ifModule code="EMPLOYEE">
                  <sec:authorize
                    access="hasAnyAuthority('ORG_ADMIN', 'EMPLOYEE_VIEW_ALL', 'EMPLOYEE_VIEW_TEAM', 'EMPLOYEE_CREATE')">
                    <div class="nav-group">
                      <a class="nav-link toggle-link" href="javascript:void(0)">
                        <i class="fa-solid fa-users"></i> <span class="nav-text">Employees</span>
                        <sec:authorize access="hasAnyAuthority('ORG_ADMIN', 'EMPLOYEE_CREATE')">
                          <span class="ms-auto"
                            onclick="window.location.href='${pageContext.request.contextPath}/org/create-employee'; event.stopPropagation();">
                            <i class="fa-solid fa-plus small text-zinc-400 hover:text-zinc-900"></i>
                          </span>
                        </sec:authorize>
                        <i class="fa-solid fa-chevron-right ms-2 small toggle-chevron"></i>
                      </a>
                      <ul class="sub-menu">
                        <sec:authorize access="hasAnyAuthority('ORG_ADMIN', 'EMPLOYEE_CREATE')">
                          <li><a href="${pageContext.request.contextPath}/org/create-employee">Add Employee</a></li>
                        </sec:authorize>
                        <sec:authorize access="hasAnyAuthority('ORG_ADMIN', 'EMPLOYEE_VIEW_ALL', 'EMPLOYEE_VIEW_TEAM')">
                          <li><a href="${pageContext.request.contextPath}/org/employees">All Employees</a></li>
                        </sec:authorize>
                      </ul>
                    </div>
                  </sec:authorize>
                </app:ifModule>

                <div class="nav-group-label">Operations</div>

                <app:ifModule code="ATTENDANCE">
                  <div class="nav-group">
                    <a class="nav-link toggle-link" href="javascript:void(0)">
                      <i class="fa-solid fa-calendar-check"></i> <span class="nav-text">Attendance</span>
                      <i class="fa-solid fa-chevron-right ms-auto small toggle-chevron"></i>
                    </a>
                    <ul class="sub-menu">
                      <li><a href="${pageContext.request.contextPath}/org/attendance">Today's Attendance</a></li>
                      <li><a href="${pageContext.request.contextPath}/org/attendance/reports">Reports</a></li>
                      <li><a href="${pageContext.request.contextPath}/org/devices">Devices</a></li>
                    </ul>
                  </div>
                </app:ifModule>

                <app:ifModule code="LEAVE">
                  <div class="nav-group">
                    <a class="nav-link toggle-link" href="javascript:void(0)">
                      <i class="fa-solid fa-calendar-days"></i> <span class="nav-text">Leave</span>
                      <i class="fa-solid fa-chevron-right ms-auto small toggle-chevron"></i>
                    </a>
                    <ul class="sub-menu">
                      <li><a href="${pageContext.request.contextPath}/org/leaves">Leave Requests</a></li>
                      <li><a href="${pageContext.request.contextPath}/org/leave-types">Leave Types</a></li>
                      <li><a href="${pageContext.request.contextPath}/org/leave-balances">Leave Balances</a></li>
                    </ul>
                  </div>
                </app:ifModule>

                <app:ifModule code="PAYROLL">
                  <div class="nav-group">
                    <a class="nav-link toggle-link" href="javascript:void(0)">
                      <i class="fa-solid fa-wallet"></i> <span class="nav-text">Payroll</span>
                      <i class="fa-solid fa-chevron-right ms-auto small toggle-chevron"></i>
                    </a>
                    <ul class="sub-menu">
                      <li class="nav-group-label" style="padding-left:0; color:#52525b;">Setup</li>
                      <li><a href="${pageContext.request.contextPath}/payroll/salary-components">Salary Components</a>
                      </li>
                      <li><a href="${pageContext.request.contextPath}/payroll/salary-structures">Salary Structures</a>
                      </li>
                      <li><a href="${pageContext.request.contextPath}/payroll/salary-assignments">Employee
                          Assignments</a></li>
                      <li><a href="${pageContext.request.contextPath}/payroll/statutory-tax">Statutory & Tax</a></li>
                      <li class="nav-group-label" style="padding-left:0; color:#52525b;">Templates</li>
                      <li><a href="${pageContext.request.contextPath}/salary-slip-template/list">Salary Slip
                          Templates</a></li>
                      <li><a href="${pageContext.request.contextPath}/salary-slip-template/design">Template Designer</a>
                      </li>
                      <li class="nav-group-label" style="padding-left:0; color:#52525b;">Operations</li>
                      <li><a href="${pageContext.request.contextPath}/payroll/payruns">PayRun Dashboard</a></li>
                    </ul>
                  </div>
                </app:ifModule>




                <app:ifModule code="ASSET_MANAGEMENT">
                  <div class="nav-group">
                    <a class="nav-link toggle-link" href="javascript:void(0)">
                      <i class="fa-solid fa-box-open"></i> <span class="nav-text">Assets</span>
                      <i class="fa-solid fa-chevron-right ms-auto small toggle-chevron"></i>
                    </a>
                    <ul class="sub-menu">
                      <li><a href="${pageContext.request.contextPath}/org/assets">All Assets</a></li>
                      <li><a href="${pageContext.request.contextPath}/org/assets/categories">Categories</a></li>
                      <li><a href="${pageContext.request.contextPath}/org/assets/vendors">Vendors</a></li>
                      <li><a href="${pageContext.request.contextPath}/org/assets/assignments">Assignments</a></li>
                      <li><a href="${pageContext.request.contextPath}/org/assets/maintenance">Maintenance Logs</a></li>
                    </ul>
                  </div>
                </app:ifModule>


                <!-- In sidebar.jsp - Under Operations section, after Payroll or before Resources -->

                <app:ifModule code="URL_MONITOR">
                  <div class="nav-group">
                    <a class="nav-link toggle-link" href="javascript:void(0)">
                      <i class="fa-solid fa-globe"></i> <span class="nav-text">URL Monitor</span>
                      <i class="fa-solid fa-chevron-right ms-auto small toggle-chevron"></i>
                    </a>
                    <ul class="sub-menu">
                      <li><a href="${pageContext.request.contextPath}/org/monitor/dashboard">Dashboard</a></li>
                      <li><a href="${pageContext.request.contextPath}/org/monitor/urls">URLs</a></li>
                      <li><a href="${pageContext.request.contextPath}/org/monitor/groups">Groups</a></li>
                      <li><a href="${pageContext.request.contextPath}/org/monitor/incidents">Incidents</a></li>
                    </ul>
                  </div>
                </app:ifModule>

                <app:ifModule code="ORG_HUB">
                  <div class="nav-group-label">Resources</div>
                  <div class="nav-group">
                    <a class="nav-link toggle-link" href="javascript:void(0)">
                      <i class="fa-solid fa-hubspot"></i> <span class="nav-text">Organisation Hub</span>
                      <i class="fa-solid fa-chevron-right ms-auto small toggle-chevron"></i>
                    </a>
                    <ul class="sub-menu">
                      <li><a href="${pageContext.request.contextPath}/org/knowledge-base">Knowledge Base</a></li>
                      <li><a href="${pageContext.request.contextPath}/org/announcements">Announcements</a></li>
                      <li><a href="${pageContext.request.contextPath}/org/events">Events</a></li>
                      <li><a href="${pageContext.request.contextPath}/org/holidays">Holidays</a></li>
                    </ul>
                  </div>
                </app:ifModule>
              </sec:authorize>

              <!-- ================= EMPLOYEE ================= -->
              <sec:authorize access="hasAuthority('EMPLOYEE')">
                <div class="nav-group-label">Activity</div>
                <a class="nav-link" href="${pageContext.request.contextPath}/employee/dashboard">
                  <i class="fa-solid fa-house-chimney-window"></i> <span class="nav-text">Dashboard</span>
                </a>
                <a class="nav-link" href="${pageContext.request.contextPath}/employee/attendance">
                  <i class="fa-solid fa-user-clock"></i> <span class="nav-text">My Attendance</span>
                </a>
                <a class="nav-link" href="${pageContext.request.contextPath}/employee/leave">
                  <i class="fa-solid fa-calendar-check"></i> <span class="nav-text">My Leaves</span>
                </a>
                <a class="nav-link" href="${pageContext.request.contextPath}/employee/salaries">
                  <i class="fa-solid fa-file-invoice-dollar"></i> <span class="nav-text">My Salaries</span>
                </a>

                <sec:authorize access="hasAnyAuthority('EMPLOYEE_VIEW_ALL', 'EMPLOYEE_VIEW_TEAM')">
                  <a class="nav-link" href="${pageContext.request.contextPath}/org/employees">
                    <i class="fa-solid fa-users"></i> <span class="nav-text">Employees</span>
                  </a>
                  <a class="nav-link" href="${pageContext.request.contextPath}/org/attendance">
                    <i class="fa-solid fa-clock"></i> <span class="nav-text">Employee Attendance</span>
                  </a>
                  <a class="nav-link" href="${pageContext.request.contextPath}/employee/leaves">
                    <i class="fa-solid fa-calendar-day"></i> <span class="nav-text">Leave Requests</span>
                  </a>
                </sec:authorize>

                <app:ifModule code="PRODUCTIVITY_MANAGEMENT">
                  <div class="nav-group">
                    <a class="nav-link toggle-link" href="javascript:void(0)">
                      <i class="fa-solid fa-briefcase"></i> <span class="nav-text">My Work</span>
                      <i class="fa-solid fa-chevron-right ms-auto small toggle-chevron"></i>
                    </a>
                    <ul class="sub-menu">
                      <li><a href="${pageContext.request.contextPath}/work/dashboard">Dashboard</a></li>
                      <li><a href="${pageContext.request.contextPath}/work/projects">Projects</a></li>
                      <li><a href="${pageContext.request.contextPath}/work/tasks">Tasks</a></li>
                      <li><a href="${pageContext.request.contextPath}/work/tickets">Tickets</a></li>
                    </ul>
                  </div>
                </app:ifModule>
              </sec:authorize>

            </nav>
          </div>
        </aside>