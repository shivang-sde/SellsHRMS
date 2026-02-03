<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<nav class="hrms-header">
  <div class="container-fluid d-flex align-items-center justify-content-between px-3">
    <!-- Left: Sidebar Toggle + Brand -->
    <div class="d-flex align-items-center gap-2">
      <button id="sidebarToggle" class="sidebar-toggle-btn" aria-label="Toggle sidebar">
        <i class="fa fa-bars"></i>
      </button>

      <c:if test="${not empty LOGO_URL}">
      <a href="${pageContext.request.contextPath}/dashboard"
        class="d-flex align-items-center gap-2 text-decoration-none brand-link">
        <div class="brand-logo-wrap">
          <img src="${sessionScope.LOGO_URL}" class="" alt="Company Logo">
        </div>
        <!-- <span class="brand-name">SellsPark HRMS</span> -->
      </a>
      </c:if>

     
    </div>

    <!-- Right: User Dropdown -->
    <div class="dropdown d-flex align-items-center">
      <a href="#" class="d-flex align-items-center text-dark text-decoration-none dropdown-toggle" id="userMenu"
        data-bs-toggle="dropdown" aria-expanded="false">
        <!-- <div class="avatar-wrap me-2">
          <img src="${sessionScope.PROFILE_IMAGE_URL}"
            onerror="this.src='${pageContext.request.contextPath}/img/avatar-placeholder.png'" alt="User Avatar"
            class="profile-avatar">
        </div> -->
        <span class="user-name d-none d-md-inline">${sessionScope.USER_NAME}</span>
      </a>

      <ul class="dropdown-menu dropdown-menu-end shadow-sm border-0 rounded-3 mt-2" aria-labelledby="userMenu">
        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/employee/profile">
            <i class="fa fa-user me-2 text-primary"></i> Profile</a></li>
        <li><a class="dropdown-item" href="#"><i class="fa fa-gear me-2 text-secondary"></i> Settings</a></li>
        <li>
          <hr class="dropdown-divider">
        </li>
        <sec:authorize access="hasAnyAuthority('ORG_ADMIN', 'EMPLOYEE')">
          <li><a class="dropdown-item" href="#" data-bs-toggle="modal" data-bs-target="#changePasswordModal">
              <i class="fa fa-lock me-2 text-warning"></i> Change Password</a></li>
        </sec:authorize>
        <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/logout">
            <i class="fa fa-sign-out-alt me-2"></i> Sign Out</a></li>
      </ul>
    </div>
  </div>
</nav>



<!-- Change Password Modal -->
<div class="modal fade" id="changePasswordModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog">
    <form id="changePasswordForm" class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Change Password</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        <div class="mb-3">
          <label class="form-label">Current Password</label>
          <input type="password" class="form-control" id="currentPassword" required>
        </div>
        <div class="mb-3">
          <label class="form-label">New Password</label>
          <input type="password" class="form-control" id="newPassword" required>
        </div>
        <div class="mb-3">
          <label class="form-label">Confirm Password</label>
          <input type="password" class="form-control" id="confirmPassword" required>
        </div>

        <div id="changePasswordMessage"></div>
      </div>
      <div class="modal-footer">
        <button type="submit" class="btn btn-primary">Change Password</button>
      </div>
    </form>
  </div>
</div>

<script>
  $("#changePasswordForm").on("submit", function (e) {
    e.preventDefault();

    const data = {
      email: "${sessionScope.EMAIL}",
      currentPassword: $("#currentPassword").val(),
      newPassword: $("#newPassword").val(),
      confirmPassword: $("#confirmPassword").val(),
    };

    // 🔹 Basic client-side checks
    if (data.currentPassword === data.newPassword) {
      showToast("error", "New password cannot be the same as the current password!");
      $("#changePasswordMessage").html('<div class="alert alert-danger">New password cannot be the same as current password!</div>');
      return;
    }

    if (data.newPassword !== data.confirmPassword) {
      showToast("error", "New password and confirmation do not match!");
      $("#changePasswordMessage").html('<div class="alert alert-danger">New password and confirmation do not match!</div>');
      return;
    }

    // 🔹 AJAX request
    $.ajax({
      url: "${pageContext.request.contextPath}/api/users/change-password",
      method: "POST",
      contentType: "application/json",
      data: JSON.stringify(data),

      success: function (res) {
        showToast("success", res.message || "Password changed successfully.");
        $("#changePasswordModal").modal("hide");
        $("#changePasswordForm")[0].reset();
        $("#changePasswordMessage").empty();
        window.location.href = "${pageContext.request.contextPath}/logout";
        
      },

      error: function (xhr) {
        console.error("Password change failed:", xhr);
        const msg = xhr.responseJSON?.message || "Failed to change password. Please try again.";
        showToast("error", "wrong current password");
        $("#changePasswordMessage").html('<div class="alert alert-danger">' + "wrong credential" + '</div>');
      }
    });
  });
</script>