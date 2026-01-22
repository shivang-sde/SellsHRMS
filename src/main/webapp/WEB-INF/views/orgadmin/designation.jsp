<%@ page contentType="text/html;charset=UTF-8" %>

<div class="container-fluid">

    <!-- DESIGNATIONS CARD -->
    <div class="card shadow-sm mt-3">
        <div class="card-header d-flex justify-content-between align-items-center">
            <h5 class="mb-0">Designations</h5>
            <button class="btn btn-primary btn-sm" data-open-desig-create>
                + Add Designation
            </button>
        </div>

        <div class="card-body p-0">
            <table class="table table-striped align-middle mb-0">
                <thead class="table-light">
                <tr>
                    <th scope="col" style="width: 15%">Department</th>
                    <th scope="col" style="width: 15%">Role</th>
                    <th scope="col" style="width: 20%">Title</th>
                    <th scope="col" style="width: 35%">Description</th>
                    <th scope="col" style="width: 15%">Actions</th>
                </tr>
                </thead>
                <tbody id="designationTableBody">
                <tr><td colspan="5" class="text-center py-3">Loading...</td></tr>
                </tbody>
            </table>
        </div>
    </div>
</div>

<!-- DESIGNATION MODAL -->
<div class="modal fade" id="desigModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <form id="desigModalForm" class="modal-content">
            <div class="modal-header">
                <h5 id="desigModalTitle" class="modal-title">Add Designation</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>

            <div class="modal-body">
                <input type="hidden" id="desigId" />

                <!-- Department -->
                <div class="mb-3">
                    <label for="departmentSelect" class="form-label d-flex align-items-center justify-content-between">
                        <span>Department</span>
                        <button type="button" id="refreshDeptBtn" class="btn btn-sm btn-outline-secondary">
                            <i class="bi bi-arrow-repeat"></i> Refresh
                        </button>
                    </label>
                    <select id="departmentSelect" class="form-select" required>
                        <option value="">Select Department...</option>
                    </select>
                </div>

                <!-- Role -->
                <div class="mb-3">
                    <label for="roleSelect" class="form-label d-flex align-items-center justify-content-between">
                        <span>Role</span>
                        <button type="button" id="refreshRoleBtn" class="btn btn-sm btn-outline-secondary">
                            <i class="bi bi-arrow-repeat"></i> Refresh
                        </button>
                    </label>
                    <select id="roleSelect" class="form-select" required>
                        <option value="">Select Role...</option>
                    </select>
                </div>

                <!-- Title -->
                <div class="mb-3">
                    <label for="desigTitleInput" class="form-label">Title</label>
                    <input id="desigTitleInput" class="form-control" required placeholder="Enter designation title" />
                </div>

                <!-- Description -->
                <div class="mb-3">
                    <label for="desigDescInput" class="form-label">Description</label>
                    <textarea id="desigDescInput" class="form-control" rows="3" placeholder="Enter description..."></textarea>
                </div>
            </div>

            <div class="modal-footer">
                <button class="btn btn-secondary" type="button" data-bs-dismiss="modal">Cancel</button>
                <button class="btn btn-primary" type="submit">Save</button>
            </div>
        </form>
    </div>
</div>


<style>
    .spin {
        animation: spin 1s linear infinite;
    }

    @keyframes spin {
        100% { transform: rotate(360deg); }
    }
</style>
