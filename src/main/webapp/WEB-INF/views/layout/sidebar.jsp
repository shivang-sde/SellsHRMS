<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<aside id="hrmsSidebar" class="hrms-sidebar" aria-label="Sidebar">
  <div class="sidebar-inner d-flex flex-column">
    
    <!-- Logo -->
    <div class="sidebar-top text-center py-3">
      <!-- <img src="/img/sellsparkLogo.png" alt="logo" height="36"> -->

    </div>

    <!-- Navigation -->
    <nav class="nav flex-column nav-pills sidebar-nav mt-2">

      <!-- SUPER ADMIN -->
      <sec:authorize access="hasAuthority('SUPER_ADMIN')">
        <a class="nav-link" href="${pageContext.request.contextPath}/superadmin/dashboard" data-key="dashboard">
          <i class="fa fa-chart-line"></i>
          <span class="nav-text">Dashboard</span>
        </a>

        <a class="nav-link" href="${pageContext.request.contextPath}/superadmin/organisations">
          <i class="fa fa-building"></i>
          <span class="nav-text">Organisations</span>
        </a>

        <a class="nav-link" href="${pageContext.request.contextPath}/superadmin/orgadmins">
          <i class="fa fa-users"></i>
          <span class="nav-text">Org Admins</span>
        </a>

        <a class="nav-link" href="${pageContext.request.contextPath}/superadmin/create-organisation">
          <i class="fa fa-plus-circle"></i>
          <span class="nav-text">Create Organisation</span>
        </a>
      </sec:authorize>

      <!-- ORG ADMIN -->
      <sec:authorize access="hasAuthority('ORG_ADMIN')">
        <!-- ORG ADMIN Dashboard -->
 
      <a  class="nav-link toggle-link" href="#" data-key="dashboard">
        <i class="fa fa-tachometer-alt"></i>
        <span class="nav-text">Dashboard</span>
         <i class="fa fa-plus toggle-icon"></i>
      </a>

      <ul class="sub-menu">
        <li class="nav-item">
      <a class="nav-link" href="${pageContext.request.contextPath}/org/dashboard">
        <i class="fa fa-home"></i>
        <span class="nav-text">Home</span>
        
      </a>
    </li>
    <li class="nav-item">
      <a class="nav-link" href="${pageContext.request.contextPath}/org/attendance-analytics-dashboard">
        <i class="fa fa-user-check"></i>
        <span class="nav-text">Attendance & Absenteeism</span>
      </a>
    </li>
    <li class="nav-item">
      <a class="nav-link" href="${pageContext.request.contextPath}/org/employee-performance">
        <i class="fa fa-chart-bar"></i>
        <span class="nav-text">Employee Performance</span>
      </a>
    </li>
    <!-- Future feature -->
    <!--
    <li class="nav-item">
      <a class="nav-link" href="${pageContext.request.contextPath}/org/compensation-benefits">
        <i class="fa fa-hand-holding-usd"></i>
        <span class="nav-text">Compensation & Benefits</span>
      </a>
    </li>
    -->
      </ul>


        <!-- Departments / Designations -->
        <a class="nav-link toggle-link" href="#">
          <i class="fa fa-sitemap"></i>
          <span class="nav-text">Departments</span>
          <i class="fa fa-plus toggle-icon"></i>
        </a>
        <ul class="sub-menu">
          <li><a href="${pageContext.request.contextPath}/org/departments">Departments</a></li>
          <li><a href="${pageContext.request.contextPath}/org/designations">Designations</a></li>
        </ul>

        <!-- Roles & Permissions -->
    <a class="nav-link toggle-link" href="#">
      <i class="fa fa-shield-alt"></i>
      <span class="nav-text">Roles & Permissions</span>
      <i class="fa fa-plus toggle-icon"></i>
    </a>
    <ul class="sub-menu">
      <li><a href="${pageContext.request.contextPath}/org/roles">All Roles</a></li>
      <li><a href="${pageContext.request.contextPath}/org/create-role">Create Role</a></li>
    </ul>


        <!-- Employees -->
        <a class="nav-link toggle-link" href="#">
          <i class="fa fa-users"></i>
          <span class="nav-text">Employees</span>
          <i class="fa fa-plus toggle-icon"></i>
        </a>
        <ul class="sub-menu">
          <li><a href="${pageContext.request.contextPath}/org/create-employee">Add Employee</a></li>
          <li><a href="${pageContext.request.contextPath}/org/employees">All Employees</a></li>
        </ul>

        <!-- Attendance -->
        <a class="nav-link toggle-link" href="#">
          <i class="fa fa-clock"></i>
          <span class="nav-text">Attendance</span>
          <i class="fa fa-plus toggle-icon"></i>
        </a>
        <ul class="sub-menu">
          <li><a href="${pageContext.request.contextPath}/org/attendance">Today's Attendance</a></li>
          <li><a href="${pageContext.request.contextPath}/org/attendance/reports">Attendance Reports</a></li>
          <li><a href="${pageContext.request.contextPath}/org/holidays">Holidays</a></li>
        </ul>

        <!-- Leaves -->
        <!-- <a class="nav-link toggle-link" href="#">
          <i class="fa fa-calendar-alt"></i>
          <span class="nav-text">Leaves</span>
          <i class="fa fa-plus toggle-icon"></i>
        </a>
        <ul class="sub-menu">
          <li><a href="${pageContext.request.contextPath}/org/leaves/pending">Pending Approvals</a></li>
          <li><a href="${pageContext.request.contextPath}/org/leaves">All Leaves</a></li>
          <li><a href="${pageContext.request.contextPath}/org/leaves/types">Leave Types</a></li>
          <li><a href="${pageContext.request.contextPath}/org/leaves/reports">Leave Reports</a></li>
        </ul> -->

        <!-- Leave Management -->
        <a class="nav-link toggle-link" href="#">
          <i class="fa fa-calendar-alt"></i>
          <span class="nav-text">Leave</span>
          <i class="fa fa-plus toggle-icon"></i>
        </a>
        <ul class="sub-menu">
          <li><a href="${pageContext.request.contextPath}/org/leaves">Leave Requests</a></li>
          <li><a href="${pageContext.request.contextPath}/org/leave-types">Leave Types</a></li>
          <li><a href="${pageContext.request.contextPath}/org/leave-balances">Leave Balances</a></li>
        </ul>


        

         <!-- Organization Info -->
        <a class="nav-link toggle-link" href="#">
          <i class="fa fa-info-circle"></i>
          <span class="nav-text">Organization Info</span>
          <i class="fa fa-plus toggle-icon"></i>
        </a>
        <ul class="sub-menu">
          <li><a href="${pageContext.request.contextPath}/org/knowledge-base">Knowledge Base</a></li>
          <li><a href="${pageContext.request.contextPath}/org/announcements">Announcements</a></li>
          <li><a href="${pageContext.request.contextPath}/org/events">Events</a></li>
        </ul>

        <!-- Organisation Policy -->
        <a class="nav-link" href="${pageContext.request.contextPath}/org/organisation-policy">
          <i class="fa fa-file-contract"></i>
          <span class="nav-text">Organisation Policy</span>
        </a>


        <!-- PAYROLL MANAGEMENT -->
    <a class="nav-link toggle-link" href="#">
    <i class="fa fa-wallet"></i>
    <span class="nav-text">Payroll Management</span>
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
      <i class="fa fa-layer-group me-2 text-success"></i> <span class="nav-text">Salary Structures</span>
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
      <i class="fa fa-balance-scale me-2 text-warning"></i> <span class="nav-text">Statutory & Tax Setup</span>
    </a>
  </li>  


  <!-- Template Management (NEW SECTION) -->
  <li class="sub-header text-muted small mt-2">Templates</li>
  <li>
    <a href="${pageContext.request.contextPath}/salary-slip-template/list">
      <i class="fa fa-file-alt me-2 text-primary"></i> 
      <span class="nav-text">Salary Slip Templates</span>
    </a>
  </li>
  <li>
    <a href="${pageContext.request.contextPath}/salary-slip-template/design">
      <i class="fa fa-drafting-compass me-2 text-info"></i> 
      <span class="nav-text">Template Designer</span>
    </a>
  </li>


  <!-- Payroll Operations -->
    <li class="sub-header text-muted small mt-2">Operations</li>
    <li>
      <a href="${pageContext.request.contextPath}/payroll/payruns">
        <i class="fa fa-play-circle me-2 text-danger"></i><span class="nav-text">PayRun Dashboard</span>
      </a>
    </li>
    <li>
      <a href="${pageContext.request.contextPath}/payroll/payslips">
        <i class="fa fa-file-invoice-dollar me-2 text-secondary"></i><span class="nav-text">Salary Slips</span>
      </a>
   </li>

  <!-- Reports -->
  <li class="sub-header text-muted small mt-2">Reports</li>
  <li>
    <a href="${pageContext.request.contextPath}/payroll/reports">
      <i class="fa fa-table me-2 text-dark"></i> <span class="nav-text">Payroll Reports</span>
    </a>
  </li>
</ul>


      </sec:authorize>

      <!-- EMPLOYEE -->
      <sec:authorize access="hasAuthority('EMPLOYEE')">
        <!-- EMPLOYEE Dashboard -->
        <a class="nav-link" href="${pageContext.request.contextPath}/employee/dashboard" data-key="dashboard">
          <i class="fa fa-chart-line"></i>
          <span class="nav-text">Dashboard</span>
        </a>

        <a class="nav-link" href="${pageContext.request.contextPath}/employee/profile">
          <i class="fa fa-user"></i>
          <span class="nav-text">My Profile</span>
        </a>

        <a class="nav-link" href="${pageContext.request.contextPath}/employee/attendance">
          <i class="fa fa-clock"></i>
          <span class="nav-text">My Attendance</span>
        </a>

        <a class="nav-link" href="${pageContext.request.contextPath}/employee/leave">
          <i class="fa fa-calendar-check"></i>
          <span class="nav-text">My Leaves</span>
        </a>

        <a class="nav-link" href="${pageContext.request.contextPath}/employee/salaries">
          <i class="fa fa-calendar-check"></i>
          <span class="nav-text">My Salaries</span>
        </a>
        
         <a class="nav-link" href="${pageContext.request.contextPath}/org/leaves">
          <i class="fa fa-calendar-check"></i>
          <span class="nav-text">Leave Requests</span>
         </a>
          <!-- Organization Info -->
        <a class="nav-link toggle-link" href="#">
          <i class="fa fa-info-circle"></i>
          <span class="nav-text">Organization</span>
          <i class="fa fa-plus toggle-icon"></i>
        </a>
        <ul class="sub-menu">
          <li><a href="${pageContext.request.contextPath}/employee/knowledge-base">Knowledge Base</a></li>
          <li><a href="${pageContext.request.contextPath}/employee/announcements">Announcements</a></li>
          <li><a href="${pageContext.request.contextPath}/employee/events">Events</a></li>
        </ul>
        <!-- <a class="nav-link" href="${pageContext.request.contextPath}/employee/leave">
          <i class="fa fa-calendar-check"></i>
          <span class="nav-text">My Leaves</span>
        </a>

        <a class="nav-link" href="${pageContext.request.contextPath}/employee/leave/apply">
          <i class="fa fa-paper-plane"></i>
          <span class="nav-text">Apply Leave</span>
        </a> -->
      </sec:authorize>

      <!-- ========================= -->
<!-- 🧭 WORK MANAGEMENT SECTION -->
<!-- ========================= -->
<sec:authorize access="hasAuthority('EMPLOYEE')">

  <a class="nav-link toggle-link" href="#">
    <i class="fa fa-briefcase"></i>
    <span class="nav-text">My Work</span>
    <i class="fa fa-plus toggle-icon"></i>
  </a>

  <ul class="sub-menu">
    <!-- Work Dashboard -->
    <li>
      <a href="${pageContext.request.contextPath}/work/dashboard">
        <i class="fa fa-chart-line"></i> My Work Dashboard
      </a>
    </li>

    <!-- Projects -->
    <li>
      <a href="${pageContext.request.contextPath}/work/projects">
        <i class="fa fa-folder-open"></i> Projects
      </a>
    </li>

    <!-- Tickets -->
    <li>
      <a href="${pageContext.request.contextPath}/work/tickets">
        <i class="fa fa-ticket-alt"></i> Tickets
      </a>
    </li>

    <!-- Tasks -->
    <li>
      <a href="${pageContext.request.contextPath}/work/tasks">
        <i class="fa fa-tasks"></i> Tasks
      </a>
    </li>
  </ul>

</sec:authorize>



      <!-- Chat Box -->
      <!-- <a class="nav-link toggle-link" href="#">
        <i class="fa fa-comments"></i>
        <span class="nav-text">Chat Box</span>
        <i class="fa fa-plus toggle-icon"></i>
      </a>
      <ul class="sub-menu">
        <li><a href="#">Open Chat</a></li>
        <li><a href="#">Messages</a></li>
      </ul> -->

    </nav>

    <!-- Footer
    <div class="sidebar-footer mt-auto p-3">
      <button id="sidebarCollapseBtn" class="btn btn-outline-secondary btn-sm w-100">
        <i class="fa fa-angle-left"></i> <i class="fa fa-angle-right"></i> 
      </button>
    </div>
  </div> -->
</aside>

