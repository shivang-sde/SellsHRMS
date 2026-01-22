<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<nav class="hrms-header navbar navbar-expand-lg align-items-center shadow-sm">
  <div class="container-fluid px-3">
    <!-- Left: Sidebar Toggle + Logo -->
    <div class="d-flex align-items-center">
      <button id="sidebarToggle" class="btn btn-ghost btn-sm me-2" aria-label="Toggle sidebar">
        <i class="fa fa-bars"></i>
      </button>

      <a class="navbar-brand d-flex align-items-center ms-1 text-decoration-none" href="${pageContext.request.contextPath}/org/dashboard">
        <img src="${pageContext.request.contextPath}/img/sellsparkLogo.png" alt="SellsHRMS" height="38" class="brand-logo me-2">
        <span class="brand-name fw-semibold d-none d-md-inline text-dark">SellsHRMS</span>
      </a>
    </div>

    <!-- Right: User Menu -->
    <div class="ms-auto d-flex align-items-center gap-3">
      <!-- Notification bell (optional future use)
      <div class="nav-item position-relative">
        <a href="#" class="nav-link text-muted position-relative">
          <i class="fa fa-bell"></i>
          <span class="badge rounded-pill notification-badge">3</span>
        </a>
      </div>
      -->

      <!-- User Dropdown -->
      <div class="nav-item dropdown">
        <a class="nav-link dropdown-toggle d-flex align-items-center text-muted" href="#" id="userMenu" data-bs-toggle="dropdown" aria-expanded="false">
          <img src="${pageContext.request.contextPath}/img/avatars/avatar-3.jpg" class="rounded-circle border" width="38" height="38" alt="user">
          <span id="name" class="ms-2 d-none d-md-inline fw-medium">${sessionScope.USER_NAME}</span>
        </a>
        <ul class="dropdown-menu dropdown-menu-end shadow-sm border-0 rounded-3">
          <li><a class="dropdown-item" href="${pageContext.request.contextPath}/employee/profile"><i class="fa fa-user me-2 text-primary"></i> Profile</a></li>
          <li><a class="dropdown-item" href="#"><i class="fa fa-gear me-2 text-secondary"></i> Settings</a></li>
          <li><hr class="dropdown-divider"></li>
          <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/logout"><i class="fa fa-sign-out-alt me-2"></i> Sign Out</a></li>
        </ul>
      </div>
    </div>
  </div>
</nav>
