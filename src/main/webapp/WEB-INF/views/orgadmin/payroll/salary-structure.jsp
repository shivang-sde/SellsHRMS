<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageScript" value="payroll/salary-structures" />

<div class="container-fluid py-4">
    <!-- Page Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="mb-1"><i class="fas fa-layer-group me-2 text-primary"></i>Salary Structures</h2>
            <p class="text-muted mb-0">Manage salary structures and assign components to them</p>
        </div>
        <button class="btn btn-primary" id="btnAddStructure">
            <i class="fas fa-plus me-2"></i>New Structure
        </button>
    </div>

    <!-- Filter Section -->
    <div class="card shadow-sm mb-4">
        <div class="card-body">
            <div class="row g-3">
                <div class="col-md-4">
                    <label class="form-label small text-muted">Search Structure</label>
                    <input type="text" class="form-control" id="searchStructure" placeholder="Search by name...">
                </div>
                <div class="col-md-3">
                    <label class="form-label small text-muted">Status</label>
                    <select class="form-select" id="filterStatus">
                        <option value="">All</option>
                        <option value="ACTIVE">Active</option>
                        <option value="INACTIVE">Inactive</option>
                    </select>
                </div>
                <div class="col-md-2 d-flex align-items-end">
                    <button class="btn btn-outline-secondary w-100" id="btnResetFilters">
                        <i class="fas fa-redo me-2"></i>Reset
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- Structures Table -->
    <div class="card shadow-sm">
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-hover align-middle" id="structuresTable">
                    <thead class="table-light">
                        <tr>
                            <th>Name</th>
                            <th>Components</th>
                            <th class="text-end">CTC Contribution</th>
                            <th>Status</th>
                            <th class="text-end">Actions</th>
                        </tr>
                    </thead>
                    <tbody id="structuresTableBody">
                        <!-- Dynamic content -->
                    </tbody>
                </table>
            </div>

            <!-- Empty State -->
            <div id="emptyState" class="text-center py-5 d-none">
                <i class="fas fa-layer-group fa-3x text-muted mb-3"></i>
                <h5 class="text-muted">No salary structures found</h5>
                <p class="text-muted">Create a salary structure to start assigning components</p>
                <button class="btn btn-primary mt-3" id="btnAddStructureEmpty">
                    <i class="fas fa-plus me-2"></i>New Structure
                </button>
            </div>

            <!-- Loading State -->
            <div id="loadingState" class="text-center py-5">
                <div class="spinner-border text-primary" role="status"></div>
                <p class="text-muted mt-3">Loading salary structures...</p>
            </div>
        </div>
    </div>
</div>

<!-- Add/Edit Structure Modal -->
<div class="modal fade" id="structureModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="structureModalTitle">
                    <i class="fas fa-plus me-2"></i>New Salary Structure
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <form id="structureForm">
                    <input type="hidden" id="structureId">

                    <div class="row g-3">
                        <!-- Structure Name -->
                        <div class="col-md-12">
                            <label class="form-label">Structure Name <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="structureName" required placeholder="e.g., Executive Package">
                        </div>

                        <!-- Description -->
                        <div class="col-md-12">
                            <label class="form-label">Description</label>
                            <textarea class="form-control" id="structureDescription" rows="2" placeholder="Brief description of structure"></textarea>
                        </div>

                        <!-- Payroll Frequency -->
                        <div class="col-md-6">
                            <label class="form-label">Payroll Frequency</label>
                            <select class="form-select" id="structureFrequency">
                                <option value="MONTHLY">Monthly</option>
                                <option value="BIWEEKLY">Bi-Weekly</option>
                                <option value="WEEKLY">Weekly</option>
                            </select>
                        </div>

                        <!-- Currency -->
                        <div class="col-md-3">
                            <label class="form-label">Currency</label>
                            <input type="text" class="form-control" id="structureCurrency" placeholder="e.g., INR, USD">
                        </div>

                        <!-- Leave Encashment Rate -->
                        <div class="col-md-3">
                            <label class="form-label">Leave Encashment Rate (%)</label>
                            <input type="number" step="0.01" class="form-control" id="leaveEncashmentRate" placeholder="e.g., 8.33">
                        </div>

                        <!-- Components -->
                        <div class="col-md-12 mt-3">
                            <label class="form-label">Select Components</label>
                            <select class="form-select" id="componentSelect" multiple required>
                                <!-- Components dynamically populated -->
                            </select>
                            <small class="form-text text-muted">Hold Ctrl/Cmd to select multiple components</small>
                        </div>

                        <!-- Active Switch -->
                        <div class="col-md-6 mt-3">
                            <div class="form-check form-switch">
                                <input class="form-check-input" type="checkbox" id="structureIsActive" checked>
                                <label class="form-check-label" for="structureIsActive">
                                    <i class="fas fa-toggle-on me-1"></i>Active Structure
                                </label>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                    <i class="fas fa-times me-2"></i>Cancel
                </button>
                <button type="button" class="btn btn-primary" id="btnSaveStructure">
                    <i class="fas fa-save me-2"></i>Save Structure
                </button>
            </div>
        </div>
    </div>
</div>
