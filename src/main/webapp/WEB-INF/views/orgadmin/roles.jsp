<div class="card shadow-sm p-4">
  <div class="d-flex justify-content-between align-items-center mb-3">
    <h4><i class="fa fa-shield-alt me-2"></i>All Roles</h4>
    <a href="/org/create-role" class="btn btn-primary btn-sm">
      <i class="fa fa-plus me-1"></i>Create Role
    </a>
  </div>

  <div class="table-responsive">
    <table class="table table-bordered table-hover align-middle" id="rolesTable">
      <thead class="table-light">
        <tr>
          <th>Role Name</th>
          <th>Description</th>
          <th>Permissions</th>
          <th width="150">Actions</th>
        </tr>
      </thead>
      <tbody></tbody>
    </table>
  </div>
</div>

<!-- System Permissions List -->
<div class="card shadow-sm p-4 mt-4">
  <div class="d-flex justify-content-between align-items-center mb-3">
    <h5 class="mb-0"><i class="fa fa-list-alt me-2 text-primary"></i>Available Module Permissions</h5>
  </div>
  <div class="alert alert-info py-2 small mb-0">
    <i class="fa fa-info-circle me-1"></i> Below is the list of all system permissions grouped by their respective modules. These can be attached to any role.
  </div>
  <div id="permissionsList" class="row">
      <div class="col-12 text-center py-4">
          <div class="spinner-border text-primary"></div>
          <p class="mt-2 text-muted">Loading available permissions...</p>
      </div>
  </div>
</div>

<style>
  .perms-badge {
    font-size: 0.875rem;
    font-style: italic;
    background-color: #f1f1f1;
    padding: 2px 6px;
    border: 1px solid #ccc;
    border-radius: 4px;
  }
</style>
