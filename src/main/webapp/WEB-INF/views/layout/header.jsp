<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
  <%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
      <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

        <nav class="hrms-header">
          <div class="container-fluid d-flex align-items-center justify-content-between p-0">
            <!-- Left: Sidebar Toggle + Brand -->
            <div class="d-flex align-items-center gap-3">
              <button id="sidebarToggle" class="sidebar-toggle-btn d-lg-none">
                <i class="fa-solid fa-bars-staggered"></i>
              </button>
              <c:choose>
                <c:when test="${not empty sessionScope.LOGO_URL}">
                  <div class="brand-logo-wrap">
                    <img src="${sessionScope.LOGO_URL}" alt="Logo" class="brand-logo-img">
                  </div>
                </c:when>
                <c:otherwise>
                  <span class="brand-text">Sells<span class="text-muted-foreground">HRMS</span></span>
                </c:otherwise>
              </c:choose>
            </div>

            <!-- Right: User Menu -->
            <div class="dropdown">
              <div class="user-dropdown-btn dropdown-toggle" id="userMenu" data-bs-toggle="dropdown"
                aria-expanded="false">
                <div class="user-avatar-wrap">
                  <c:choose>
                    <c:when test="${not empty sessionScope.PROFILE_IMAGE_URL}">
                      <img src="${sessionScope.PROFILE_IMAGE_URL}"
                        onerror="this.style.display='none'; this.nextElementSibling.style.display='block';" alt="P">
                      <span style="display:none;">${fn:substring(sessionScope.USER_NAME, 0, 1)}</span>
                    </c:when>
                    <c:otherwise>
                      ${fn:substring(sessionScope.USER_NAME, 0, 1)}
                    </c:otherwise>
                  </c:choose>
                </div>
                <div class="user-info d-none d-md-flex">
                  <span class="user-name">${sessionScope.USER_NAME}</span>
                  <span class="user-role">${fn:replace(sessionScope.SYSTEM_ROLE, '_', ' ')}</span>
                </div>
              </div>

              <ul class="dropdown-menu dropdown-menu-end border shadow-sm" aria-labelledby="userMenu">
                <li class="dropdown-header text-muted-foreground small fw-bold px-3 py-2">ACCOUNT</li>
                <sec:authorize access="hasAuthority('EMPLOYEE')">
                  <li><a class="dropdown-item" href="${pageContext.request.contextPath}/employee/profile">
                      <i class="fa-regular fa-user"></i> Profile</a></li>
                </sec:authorize>
                <div class="dropdown-divider"></div>
                <li><a class="dropdown-item" href="#" data-bs-toggle="modal" data-bs-target="#changePasswordModal">
                    <i class="fa-solid fa-key"></i> Password</a></li>
                <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/logout">
                    <i class="fa-solid fa-arrow-right-from-bracket"></i> Sign Out</a></li>
              </ul>
            </div>
          </div>
        </nav>

        <!-- Change Password Modal -->
        <div class="modal fade" id="changePasswordModal" tabindex="-1" aria-hidden="true">
          <div class="modal-dialog modal-dialog-centered">
            <form id="changePasswordForm" class="modal-content border-0 shadow-lg">
              <div class="modal-header border-bottom px-4">
                <h5 class="modal-title fw-semibold">Update Password</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
              </div>
              <div class="modal-body p-4">
                <p class="text-muted-foreground small mb-4">Ensure your new password contains at least 8 characters.</p>

                <div class="mb-3">
                  <label class="form-label">Current Password</label>
                  <input type="password" class="form-control" id="currentPassword" required>
                </div>
                <div class="mb-3">
                  <label class="form-label">New Password</label>
                  <input type="password" class="form-control" id="newPassword" required>
                </div>
                <div class="mb-4">
                  <label class="form-label">Confirm New Password</label>
                  <input type="password" class="form-control" id="confirmPassword" required>
                </div>

                <div id="changePasswordMessage"></div>
              </div>
              <div class="modal-footer border-top p-4">
                <button type="submit" class="btn btn-primary w-100">Update Password</button>
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

            if (data.currentPassword === data.newPassword) {
              showToast("error", "New password cannot be the same as the current password!");
              $("#changePasswordMessage").html('<div class="alert alert-danger py-2 small">New password cannot be the same as current password!</div>');
              return;
            }

            if (data.newPassword !== data.confirmPassword) {
              showToast("error", "New password and confirmation do not match!");
              $("#changePasswordMessage").html('<div class="alert alert-danger py-2 small">New password and confirmation do not match!</div>');
              return;
            }

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
                showToast("error", "Wrong current password or server error.");
                $("#changePasswordMessage").html('<div class="alert alert-danger py-2 small">Verification failed. Please check your current password.</div>');
              }
            });
          });
        </script>