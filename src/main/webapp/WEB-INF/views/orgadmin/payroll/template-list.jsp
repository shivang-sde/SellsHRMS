<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Page Header -->
<div class="row mb-4">
    <div class="col-12">
        <div class="d-flex justify-content-between align-items-center">
            <div>
                <h3 class="mb-1">Salary Slip Templates</h3>
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb mb-0">
                        <li class="breadcrumb-item"><a href="/dashboard">Home</a></li>
                        <li class="breadcrumb-item"><a href="/payroll">Payroll</a></li>
                        <li class="breadcrumb-item active">Templates</li>
                    </ol>
                </nav>
            </div>
            <div>
                <a href="/salary-slip-template/design" class="btn btn-primary">
                    <i class="bi bi-plus-circle"></i> Create New Template
                </a>
            </div>
        </div>
    </div>
</div>

<!-- Templates Table -->
<div class="row">
    <div class="col-12">
        <div class="card shadow-sm">
            <div class="card-header bg-white">
                <div class="d-flex justify-content-between align-items-center">
                    <h5 class="mb-0"><i class="bi bi-collection"></i> All Templates</h5>
                    <div class="d-flex gap-2">
                        <input type="text" class="form-control form-control-sm" id="searchTemplate" 
                               placeholder="Search templates..." style="width: 250px;">
                        <button class="btn btn-sm btn-outline-secondary" onclick="refreshTemplates()">
                            <i class="bi bi-arrow-clockwise"></i> Refresh
                        </button>
                    </div>
                </div>
            </div>
            <div class="card-body">
                <!-- Loading State -->
                <div id="loadingState" class="text-center py-5">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                    <p class="text-muted mt-3">Loading templates...</p>
                </div>

                <!-- Table Content -->
                <div id="tableContent" style="display: none;">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle">
                            <thead class="table-light">
                                <tr>
                                    <th>Template Name</th>
                                    <th>Created By</th>
                                    <th>Created Date</th>
                                    <th>Last Updated</th>
                                    <th class="text-center">Status</th>
                                    <th class="text-center">Actions</th>
                                </tr>
                            </thead>
                            <tbody id="templateTableBody">
                                <!-- Rows will be loaded via JavaScript -->
                            </tbody>
                        </table>
                    </div>

                    <!-- Empty State -->
                    <div id="emptyState" class="text-center py-5" style="display: none;">
                        <i class="bi bi-inbox" style="font-size: 4rem; color: #dee2e6;"></i>
                        <h5 class="text-muted mt-3">No Templates Found</h5>
                        <p class="text-muted">Get started by creating your first salary slip template</p>
                        <a href="/salary-slip-template/design" class="btn btn-primary mt-3">
                            <i class="bi bi-plus-circle"></i> Create Template
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Delete Confirmation Modal -->
<div class="modal fade" id="deleteConfirmModal" tabindex="-1" aria-labelledby="deleteConfirmModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header bg-danger text-white">
                <h5 class="modal-title" id="deleteConfirmModalLabel">
                    <i class="bi bi-exclamation-triangle"></i> Confirm Delete
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <p>Are you sure you want to delete this template?</p>
                <p class="fw-bold mb-0" id="templateNameToDelete"></p>
                <input type="hidden" id="templateIdToDelete">
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-danger" onclick="confirmDelete()">
                    <i class="bi bi-trash"></i> Delete
                </button>
            </div>
        </div>
    </div>
</div>

<style>
    .table tbody tr {
        cursor: pointer;
        transition: background-color 0.2s;
    }

    .table tbody tr:hover {
        background-color: #f8f9fa;
    }

    .badge {
        padding: 0.35em 0.65em;
        font-weight: 500;
    }

    .btn-group-sm > .btn {
        padding: 0.25rem 0.5rem;
        font-size: 0.875rem;
    }
</style>