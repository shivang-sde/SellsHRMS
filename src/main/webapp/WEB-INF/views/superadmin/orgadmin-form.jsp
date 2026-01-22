<%@ page contentType="text/html;charset=UTF-8" %>
<c:set var="pageTitle" value="Create Organisation Admin" />
<c:set var="pageScript" value="orgadmin-form" />

<div class="card">
  <div class="card-header"><strong>Create Org Admin</strong></div>
  <div class="card-body">
    <form id="createAdminForm" class="row g-3">
      <div class="col-md-6"><label class="form-label">Full Name</label><input id="adminFullName" class="form-control" required></div>
      <div class="col-md-6"><label class="form-label">Email</label><input id="adminEmail" type="email" class="form-control" required></div>
      <div class="col-md-6"><label class="form-label">Password</label><input id="adminPassword" type="password" class="form-control" required></div>
      <div class="col-md-6"><label class="form-label">Organisation</label><select id="organisationId" class="form-select"><option>Loading...</option></select></div>
      <div class="col-12"><button class="btn btn-success">Create</button></div>
      <div class="col-12"><div id="createAdminMessage" class="mt-3"></div></div>
    </form>
  </div>
</div>
