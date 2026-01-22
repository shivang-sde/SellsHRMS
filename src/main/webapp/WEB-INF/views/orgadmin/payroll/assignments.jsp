<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageScript" value="payroll/assignments" />

<div class="container-fluid py-4">
    <!-- Page Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="mb-1"><i class="fas fa-user-tie me-2 text-primary"></i>Salary Assignments</h2>
            <p class="text-muted mb-0">Assign salary structures and compensation to employees</p>
        </div>
        <button class="btn btn-primary" id="btnAddAssignment">
            <i class="fas fa-plus me-2"></i>New Assignment
        </button>
    </div>

    <!-- Filter Section -->
    <div class="card shadow-sm mb-4">
        <div class="card-body">
            <div class="row g-3">
                <div class="col-md-3">
                    <label class="form-label small text-muted">Department</label>
                    <select class="form-select" id="filterDepartment">
                        <option value="">All Departments</option>
                    </select>
                </div>
                <div class="col-md-3">
                    <label class="form-label small text-muted">Salary Structure</label>
                    <select class="form-select" id="filterStructure">
                        <option value="">All Structures</option>
                    </select>
                </div>
                <div class="col-md-4">
                    <label class="form-label small text-muted">Search Employee</label>
                    <input type="text" class="form-control" id="searchEmployee" placeholder="Search by name or employee ID...">
                </div>
                <div class="col-md-2 d-flex align-items-end">
                    <button class="btn btn-outline-secondary w-100" id="btnResetFilters">
                        <i class="fas fa-redo me-2"></i>Reset
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- Assignments Table -->
    <div class="card shadow-sm">
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-hover align-middle" id="assignmentsTable">
                    <thead class="table-light">
                        <tr>
                            <th>Employee</th>
                            <th>Department</th>
                            <th>Salary Structure</th>
                            <th class="text-end">Base Salary</th>
                            <th class="text-end">Variable Pay</th>
                            <th class="text-end">Total CTC</th>
                            <th>Effective Date</th>
                            <th>Status</th>
                            <th class="text-end">Actions</th>
                        </tr>
                    </thead>
                    <tbody id="assignmentsTableBody">
                        <!-- Dynamic content -->
                    </tbody>
                </table>
            </div>
            
            <!-- Empty State -->
            <div id="emptyState" class="text-center py-5 d-none">
                <i class="fas fa-user-slash fa-3x text-muted mb-3"></i>
                <h5 class="text-muted">No salary assignments found</h5>
                <p class="text-muted">Create the first salary assignment for an employee</p>
                <button class="btn btn-primary mt-3" id="btnAddAssignmentEmpty">
                    <i class="fas fa-plus me-2"></i>New Assignment
                </button>
            </div>

            <!-- Loading State -->
            <div id="loadingState" class="text-center py-5">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
                <p class="text-muted mt-3">Loading assignments...</p>
            </div>
        </div>
    </div>
</div>

<!-- Add/Edit Assignment Modal -->
<div class="modal fade" id="assignmentModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="assignmentModalTitle">
                    <i class="fas fa-user-tie me-2"></i>New Salary Assignment
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <form id="assignmentForm">
                    <input type="hidden" id="assignmentId">
                    
                    <div class="row g-3">
                        <!-- Employee Selection -->
                        <div class="col-12">
                            <h6 class="text-primary border-bottom pb-2 mb-3">Employee Information</h6>
                        </div>
                        
                        <div class="col-md-12">
                            <label class="form-label">Select Employee <span class="text-danger">*</span></label>
                            <select class="form-select" id="employeeSelect" required>
                                <option value="">Choose an employee...</option>
                            </select>
                            <div id="employeeInfo" class="mt-2 p-3 bg-light rounded d-none">
                                <div class="row g-2 small">
                                    <div class="col-md-4">
                                        <strong>Employee ID:</strong> <span id="empId"></span>
                                    </div>
                                    <div class="col-md-4">
                                        <strong>Department:</strong> <span id="empDept"></span>
                                    </div>
                                    <div class="col-md-4">
                                        <strong>Designation:</strong> <span id="empDesig"></span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        
                        <!-- Structure Selection -->
                        <div class="col-12 mt-4">
                            <h6 class="text-primary border-bottom pb-2 mb-3">Salary Structure</h6>
                        </div>
                        
                        <div class="col-md-12">
                            <label class="form-label">Salary Structure <span class="text-danger">*</span></label>
                            <select class="form-select" id="structureSelect" required>
                                <option value="">Choose a structure...</option>
                            </select>
                            <div id="structureInfo" class="mt-2 d-none">
                                <div class="alert alert-info small mb-0">
                                    <strong>Components:</strong> <span id="structureComponents"></span>
                                </div>
                            </div>
                        </div>
                        
                        <!-- Compensation Details -->
                        <div class="col-12 mt-4">
                            <h6 class="text-primary border-bottom pb-2 mb-3">Compensation Details</h6>
                        </div>
                        
                        <div class="col-md-6">
                            <label class="form-label">Base Salary <span class="text-danger">*</span></label>
                            <div class="input-group">
                                <span class="input-group-text">₹</span>
                                <input type="number" class="form-control" id="baseSalary" required 
                                       placeholder="0.00" step="0.01" min="0">
                            </div>
                        </div>
                        
                        <div class="col-md-6">
                            <label class="form-label">Variable Pay</label>
                            <div class="input-group">
                                <span class="input-group-text">₹</span>
                                <input type="number" class="form-control" id="variablePay" 
                                       placeholder="0.00" step="0.01" min="0">
                            </div>
                        </div>
                        
                        <div class="col-md-12">
                            <div class="card bg-light">
                                <div class="card-body">
                                    <div class="row">
                                        <div class="col-md-4">
                                            <small class="text-muted">Base Salary</small>
                                            <h5 class="mb-0" id="displayBase">₹0.00</h5>
                                        </div>
                                        <div class="col-md-4">
                                            <small class="text-muted">Variable Pay</small>
                                            <h5 class="mb-0" id="displayVariable">₹0.00</h5>
                                        </div>
                                        <div class="col-md-4">
                                            <small class="text-muted">Total CTC</small>
                                            <h4 class="mb-0 text-primary" id="displayTotal">₹0.00</h4>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        
                        <!-- Effective Date -->
                        <div class="col-md-6">
                            <label class="form-label">Effective From <span class="text-danger">*</span></label>
                            <input type="date" class="form-control" id="effectiveDate" required>
                        </div>
                        
                        <div class="col-md-6">
                            <label class="form-label">Effective Until</label>
                            <input type="date" class="form-control" id="effectiveUntil">
                            <small class="form-text text-muted">Leave blank for indefinite</small>
                        </div>
                        
                        <div class="col-12">
                            <label class="form-label">Remarks</label>
                            <textarea class="form-control" id="remarks" rows="2" 
                                      placeholder="Any additional notes..."></textarea>
                        </div>
                        
                        <div class="col-md-6">
                            <div class="form-check form-switch">
                                <input class="form-check-input" type="checkbox" id="assignmentIsActive" checked>
                                <label class="form-check-label" for="assignmentIsActive">
                                    <i class="fas fa-toggle-on me-1"></i>Active Assignment
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
                <button type="button" class="btn btn-primary" id="btnSaveAssignment">
                    <i class="fas fa-save me-2"></i>Save Assignment
                </button>
            </div>
        </div>
    </div>
</div>

<!-- View Assignment Details Modal -->
<div clasas="modal fade" id="viewAssignmentModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="fas fa-eye me-2"></i>Assignment Details
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <div class="row g-3" id="assignmentDetailsContent">
                    <!-- Dynamic content -->
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>