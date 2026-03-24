<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
  <%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
      <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

        <nav class="hrms-header">
          <div class="container-fluid d-flex align-items-center justify-content-between px-3">
            <!-- Left: Sidebar Toggle + Brand -->
            <div class="d-flex align-items-center gap-2">
              <button id="sidebarToggle" class="sidebar-toggle-btn" aria-label="Toggle sidebar">
                <i class="fa fa-bars-staggered"></i>
              </button>

              <c:choose>
                <c:when test="${not empty sessionScope.LOGO_URL}">
                  <a href="${pageContext.request.contextPath}/org/dashboard" class="brand-link text-decoration-none">
                    <div class="brand-logo-wrap" title="Company Logo">
                      <img src="${sessionScope.LOGO_URL}" alt="Company Logo"
                        onerror="this.src='${pageContext.request.contextPath}/img/logo-placeholder.png'">
                    </div>
                  </a>
                </c:when>
                <c:otherwise>
                  <a href="${pageContext.request.contextPath}/org/dashboard" class="brand-link text-decoration-none">
                    <div class="brand-logo-wrap">
                      <i class="fa fa-building fa-xl text-primary"></i>
                    </div>
                    <span class="ms-2 fw-bold text-dark d-none d-lg-inline"
                      style="letter-spacing: -0.5px; font-size: 1.1rem;">Sells<span
                        class="text-primary">HRMS</span></span>
                  </a>
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
                        onerror="this.src=''; this.parentElement.innerHTML='${fn:substring(sessionScope.USER_NAME, 0, 1)}'"
                        alt="Profile">
                    </c:when>
                    <c:otherwise>
                      ${fn:substring(sessionScope.USER_NAME, 0, 1)}
                    </c:otherwise>
                  </c:choose>
                </div>
                <div class="user-info">
                  <span class="user-name">${sessionScope.USER_NAME}</span>
                  <span class="user-role" style="text-transform: capitalize;">
                    <c:out value="${fn:replace(sessionScope.SYSTEM_ROLE, '_', ' ')}" />
                  </span>

                </div>
              </div>

              <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userMenu">
                <li class="dropdown-header">Manage Account</li>
                <sec:authorize access="hasAuthority('EMPLOYEE')">
                  <li><a class="dropdown-item" href="${pageContext.request.contextPath}/employee/profile">
                      <i class="fa-regular fa-user text-primary"></i> My Profile</a></li>
                </sec:authorize>
                <!-- <li><a class="dropdown-item" href="#">
            <i class="fa-regular fa-circle-question text-secondary"></i> Knowledge Base</a></li>
         -->
                <div class="dropdown-divider"></div>


                <li><a class="dropdown-item" href="#" data-bs-toggle="modal" data-bs-target="#changePasswordModal">
                    <i class="fa fa-key text-warning"></i> Change Password</a></li>


                <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/logout">
                    <i class="fa fa-arrow-right-from-bracket"></i> Sign Out</a></li>
              </ul>
            </div>
          </div>
        </nav>

        <!-- Change Password Modal -->
        <div class="modal fade" id="changePasswordModal" tabindex="-1" aria-hidden="true">
          <div class="modal-dialog modal-dialog-centered">
            <form id="changePasswordForm" class="modal-content overflow-hidden border-0 shadow"
              style="border-radius: 16px;">
              <div class="modal-header border-0 pb-0">
                <h5 class="modal-title fw-bold">Update Password</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
              </div>
              <div class="modal-body p-4">
                <p class="text-muted small mb-4">Ensure your new password contains at least 8 characters with a mix of
                  letters and numbers.</p>

                <div class="mb-3">
                  <label class="form-label fw-600 small">Current Password</label>
                  <input type="password" class="form-control" id="currentPassword" placeholder="Enter current password"
                    required style="border-radius: 10px; padding: 10px 14px;">
                </div>
                <div class="mb-3">
                  <label class="form-label fw-600 small">New Password</label>
                  <input type="password" class="form-control" id="newPassword" placeholder="Enter new password" required
                    style="border-radius: 10px; padding: 10px 14px;">
                </div>
                <div class="mb-4">
                  <label class="form-label fw-600 small">Confirm New Password</label>
                  <input type="password" class="form-control" id="confirmPassword" placeholder="Repeat new password"
                    required style="border-radius: 10px; padding: 10px 14px;">
                </div>

                <div id="changePasswordMessage"></div>
              </div>
              <div class="modal-footer border-0 pt-0 p-4">
                <button type="submit" class="btn btn-primary w-100"
                  style="border-radius: 10px; padding: 12px; font-weight: 600;">Securely Update Password</button>
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