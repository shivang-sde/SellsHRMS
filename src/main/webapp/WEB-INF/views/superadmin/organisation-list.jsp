<%@ page contentType="text/html;charset=UTF-8" %>
<c:set var="pageTitle" value="Organisations" />
<c:set var="pageScript" value="organisation-list" />

<div class="d-flex justify-content-between align-items-center mb-3">
  <h4 class="mb-0">Organisations</h4>
  <a class="btn btn-primary" href="${pageContext.request.contextPath}/superadmin/create-organisation">
    Add Organisation
  </a>
</div>

<div class="card">
  <div class="card-body">
    <div class="table-responsive">
      <table id="orgTable" class="table table-hover">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Domain</th>
            <th>Email</th>
            <th>Active</th>
            <th>Max Employees</th>
            <th>Validity</th>
            <th style="width:160px">Actions</th>
          </tr>
        </thead>
        <tbody id="orgTableBody">
          <tr><td colspan="6" class="text-center">Loading...</td></tr>
        </tbody>
      </table>
    </div>
  </div>
</div>


<!-- Edit Organisation Modal -->
<div class="modal fade" id="editOrgModal" tabindex="-1" aria-labelledby="editOrgModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg modal-dialog-scrollable">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="editOrgModalLabel">Edit Organisation</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>

      <div class="modal-body">
        <!-- Reused Form -->
        <form id="editOrgForm" class="row g-3">

          <h5 class="mt-3">Organisation Details</h5>

          <input type="hidden" id="editOrgId">

          <div class="col-md-6">
            <label for="editOrgName" class="form-label">Organisation Name</label>
            <input id="editOrgName" class="form-control" required>
          </div>

          <div class="col-md-6">
            <label for="editOrgDomain" class="form-label">Domain</label>
            <input id="editOrgDomain" class="form-control" required>
          </div>

          <div class="col-md-6">
            <label for="editOrgEmail" class="form-label">Contact Email</label>
            <input id="editOrgEmail" type="email" class="form-control" required>
          </div>

          <div class="col-md-6">
            <label for="editOrgPhone" class="form-label">Contact Phone</label>
            <input id="editOrgPhone" class="form-control">
          </div>

          <div class="col-md-4">
            <label for="editOrgAddress" class="form-label">Address</label>
            <input id="editOrgAddress" class="form-control">
          </div>

          <div class="col-md-4">
            <label for="editOrgCountry" class="form-label">Country</label>
            <input id="editOrgCountry" class="form-control">
          </div>

          <div class="col-md-4">
            <label for="editLogoUrl" class="form-label">Logo URL</label>
            <input id="editLogoUrl" class="form-control">
          </div>

          <div class="col-md-4">
            <label for="editOrgPan" class="form-label">PAN</label>
            <input id="editOrgPan" class="form-control" required>
          </div>

          <div class="col-md-4">
            <label for="editOrgTan" class="form-label">TAN</label>
            <input id="editOrgTan" class="form-control" required>
          </div>

          <div class="col-md-4">
            <label for="editMaxEmployees" class="form-label">Max Employees</label>
            <input id="editMaxEmployees" type="number" class="form-control" required>
          </div>

          <div class="col-md-4">
            <label for="editValidity" class="form-label">Validity</label>
            <input id="editValidity" type="date" class="form-control" required>
          </div>

          <!-- ADMIN SECTION (Hidden / Disabled for edit) -->
          <h5 class="mt-4 text-muted">Admin Details (Not Editable)</h5>

          <div class="col-md-6">
            <label for="editAdminFullName" class="form-label">Full Name</label>
            <input id="editAdminFullName" class="form-control" disabled>
          </div>

          <div class="col-md-6">
            <label for="editAdminEmail" class="form-label">Email</label>
            <input id="editAdminEmail" type="email" class="form-control" disabled>
          </div>

        </form>
      </div>

      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
        <button id="saveOrgBtn" type="button" class="btn btn-success">Save Changes</button>
      </div>
    </div>
  </div>
</div>

