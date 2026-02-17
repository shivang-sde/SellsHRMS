<%@ page contentType="text/html;charset=UTF-8" %>
  <c:set var="pageTitle" value="Organisations" />
  <c:set var="pageScript" value="organisation-list" />
  <div class="container-fluid py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div>
        <h4 class="fw-bold mb-0">Organisations Management</h4>
        <p class="text-muted small">Overview and control of all registered entities.</p>
      </div>
      <a class="btn btn-primary rounded-pill px-4 shadow-sm"
        href="${pageContext.request.contextPath}/superadmin/create-organisation">
        <i class="fas fa-plus me-2"></i>Add Organisation
      </a>
    </div>

    <div class="card border-0 shadow-sm mb-4">
      <div class="card-body">
        <div class="row g-3">
          <div class="col-md-4">
            <div class="input-group">
              <span class="input-group-text bg-white border-end-0"><i class="fa fa-search text-muted"></i></span>
              <input type="text" id="orgSearchInput" class="form-control border-start-0"
                placeholder="Search by name, domain, or email...">
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="card border-0 shadow-sm">
      <div class="card-body p-0">
        <div class="table-responsive">
          <table id="orgTable" class="table table-hover align-middle mb-0">
            <thead class="bg-light">
              <tr class="text-muted small fw-bold">
                <th class="ps-4">ID</th>
                <th>ORGANISATION</th>
                <th>ADMIN CONTACT</th>
                <th>STATUS</th>
                <th>CAPACITY</th>
                <th>VALIDITY</th>
                <th class="text-end pe-4">ACTIONS</th>
              </tr>
            </thead>
            <tbody id="orgTableBody">
            </tbody>
          </table>
        </div>
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

  <div class="modal fade" id="manageModulesModal" tabindex="-1" aria-labelledby="manageModulesLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="manageModulesLabel">Manage Modules for Organisation</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
        </div>

        <div class="modal-body">
          <form id="manageModulesForm" class="row g-3"></form>
        </div>

        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
          <button type="button" class="btn btn-success" id="saveModulesBtn">Save Changes</button>
        </div>
      </div>
    </div>
  </div>




  <style>
    /* Custom Styles for SuperAdmin Org List */
    .bg-soft-success {
      background-color: #dcfce7 !important;
      color: #15803d !important;
    }

    .bg-soft-danger {
      background-color: #fee2e2 !important;
      color: #b91c1c !important;
    }

    .x-small {
      font-size: 0.75rem;
    }

    .ls-1 {
      letter-spacing: 0.5px;
    }

    #orgTable thead th {
      border: none;
      text-transform: uppercase;
      letter-spacing: 0.05em;
    }

    #orgTable tbody tr {
      transition: all 0.2s;
    }

    #orgTable tbody tr:hover {
      background-color: #f8fafc;
    }

    /* Dropdown Menu Styling */
    .dropdown-menu .dropdown-item {
      font-size: 0.85rem;
      transition: background 0.2s;
    }

    .dropdown-menu .dropdown-item:hover {
      background-color: #f1f5f9;
    }

    .module-item.disabled-module {
      opacity: 0.7;
    }
  </style>