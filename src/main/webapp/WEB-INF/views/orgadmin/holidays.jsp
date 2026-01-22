<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="row mb-3">
    <div class="col-12">
        <div class="d-flex justify-content-between align-items-center">
            <h2 class="mb-0">Holiday Management</h2>
            <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addHolidayModal">
                <i class="fas fa-plus me-2"></i>Add Holiday
            </button>
        </div>
    </div>
</div>

<!-- Year Selector -->
<div class="card mb-3">
    <div class="card-body">
        <div class="row g-3">
            <div class="col-md-3">
                <label class="form-label">Select Year</label>
                <select id="yearSelect" class="form-select">
                    <option value="2024">2024</option>
                    <option value="2025" selected>2025</option>
                    <option value="2026">2026</option>
                </select>
            </div>
            <div class="col-md-3">
                <label class="form-label">Filter by Type</label>
                <select id="filterType" class="form-select">
                    <option value="">All Types</option>
                    <option value="PUBLIC">Public Holiday</option>
                    <option value="COMPANY_SPECIFIC">Company Specific</option>
                    <option value="OPTIONAL">Optional</option>
                </select>
            </div>
            <div class="col-md-2">
                <label class="form-label">&nbsp;</label>
                <button id="btnFilter" class="btn btn-secondary w-100">
                    <i class="fas fa-filter me-2"></i>Filter
                </button>
            </div>
        </div>
    </div>
</div>

<!-- Holidays List -->
<div class="card">
    <div class="card-body">
        <div class="table-responsive">
            <table class="table table-hover">
                <thead class="table-light">
                    <tr>
                        <th>Date</th>
                        <th>Holiday Name</th>
                        <th>Type</th>
                        <th>Mandatory</th>
                        <th>Description</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody id="holidaysTableBody">
                    <tr>
                        <td colspan="6" class="text-center">
                            <div class="spinner-border text-primary"></div>
                            <p class="mt-2 text-muted">Loading holidays...</p>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>

<!-- Add Holiday Modal -->
<div class="modal fade" id="addHolidayModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <form id="addHolidayForm">
                <div class="modal-header">
                    <h5 class="modal-title">Add Holiday</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label">Holiday Name <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" name="holidayName" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Date <span class="text-danger">*</span></label>
                        <input type="date" class="form-control" name="holidayDate" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Type</label>
                        <select class="form-select" name="holidayType">
                            <option value="PUBLIC">Public Holiday</option>
                            <option value="COMPANY_SPECIFIC">Company Specific</option>
                            <option value="OPTIONAL">Optional</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Description</label>
                        <textarea class="form-control" name="description" rows="3"></textarea>
                    </div>
                    <div class="mb-3 form-check">
                        <input type="checkbox" class="form-check-input" name="isMandatory" id="isMandatory" checked>
                        <label class="form-check-label" for="isMandatory">
                            Mandatory Holiday
                        </label>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Save Holiday</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Delete Confirmation Modal -->
<div class="modal fade" id="deleteHolidayModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Confirm Delete</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                Are you sure you want to delete this holiday?
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-danger" id="confirmDeleteHoliday">Delete</button>
            </div>
        </div>
    </div>
</div>