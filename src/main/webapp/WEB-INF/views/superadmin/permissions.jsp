<div class="card shadow-sm">
    <div class="card-header d-flex justify-content-between align-items-center">
        <h5 class="mb-0"><i class="fa-solid fa-key text-primary me-2"></i> Permission Management</h5>
        <button class="btn btn-sm btn-primary" data-bs-toggle="modal" data-bs-target="#createPermissionModal">
            <i class="fa-solid fa-plus"></i> Add Permission
        </button>
    </div>

    <div class="card-body">
        <table class="table table-hover align-middle" id="permissionTable">
            <thead class="table-light">
                <tr>
                    <th>ID</th>
                    <th>Module</th>
                    <th>Action</th>
                    <th>Code</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody id="permissionTableBody"></tbody>
        </table>
    </div>
</div>

<!-- Create Permission Modal -->
<div class="modal fade" id="createPermissionModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Create Permission</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <form id="permissionForm">
                    <div class="mb-3">
                        <label class="form-label">Module <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="module" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Action <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="action" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Code <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="code" required>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <button class="btn btn-primary" id="savePermissionBtn">Save</button>
            </div>
        </div>
    </div>
</div>