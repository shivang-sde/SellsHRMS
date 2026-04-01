<%@ page contentType="text/html;charset=UTF-8" %>
<c:set var="pageTitle" value="Organisation Admins" />
<c:set var="pageScript" value="orgadmin-list" />

<div class="d-flex justify-content-between align-items-center mb-3">
  <h4 class="mb-0">Organisation Admins</h4>
  <!-- <a class="btn btn-primary" href="${pageContext.request.contextPath}/superadmin/create-orgadmin">
    Add Org Admin
  </a> -->
</div>

<div class="card shadow-sm">
  <div class="card-body p-0">
    <div class="table-responsive">
      <table class="table table-hover align-middle text-nowrap mb-0">
        <thead class="table-light">
          <tr>
            <th class="px-3">ID</th>
            <th>Name</th>
            <th>Email</th>
            <th>Last Login</th>
            <th>Active</th>
            <th class="px-3">Actions</th>
          </tr>
        </thead>
        <tbody id="orgAdminsBody">
          <tr><td colspan="6" class="text-center py-4">Loading...</td></tr>
        </tbody>
      </table>
    </div>
  </div>
</div>
