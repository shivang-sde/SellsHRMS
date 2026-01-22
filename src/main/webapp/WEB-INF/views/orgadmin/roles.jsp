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

<style>
  .perms-badge {
    font-size: 0.875rem;
    font-style: italic;
    background-color: #f1f1f1;
    padding: 2px 6px;
    border: 1px solid #ccc;
  }
</style>
