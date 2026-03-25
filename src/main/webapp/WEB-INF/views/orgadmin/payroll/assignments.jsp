<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <c:set var="pageScript" value="payroll/assignments" />

        <div class="container-fluid py-4">
            <div class="row align-items-center mb-4">
                <div class="col">
                    <h2 class="fw-bold mb-1"><i class="fas fa-file-invoice-dollar me-2 text-primary"></i>Salary
                        Assignments</h2>
                </div>
                <div class="col-auto">
                    <button class="btn btn-primary rounded-pill px-4 shadow-sm" id="btnAddAssignment">
                        <i class="fas fa-plus me-2"></i>New Assignment
                    </button>
                </div>
            </div>


            <div class="card border-0 shadow-sm mb-4">
                <div class="card-body p-3">
                    <div class="row g-2 align-items-center">
                        <div class="col-md-5">
                            <div class="input-group input-group-merge">
                                <span class="input-group-text bg-light border-0"><i
                                        class="fa fa-search text-muted"></i></span>
                                <input type="text" class="form-control border-0 bg-light" id="searchEmployee"
                                    placeholder="Search by name, code or department...">
                            </div>
                        </div>
                        <div class="col-md-3">
                            <select class="form-select border-0 bg-light" id="filterStructure">
                                <option value="">All Salary Structures</option>
                            </select>
                        </div>
                        <div class="col-md-2">
                            <button class="btn btn-light w-100 fw-bold" id="btnResetFilters">Reset</button>
                        </div>
                    </div>
                </div>
            </div>

            <div class="card border-0 shadow-sm overflow-hidden">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0" id="assignmentsTable">
                        <thead class="bg-light text-muted small">
                            <tr>
                                <th class="ps-4">EMPLOYEE</th>
                                <th>STRUCTURE</th>
                                <th class="text-end">MONTHLY NET</th>
                                <th class="text-end">ANNUAL CTC</th>
                                <th>EFFECTIVE DATE</th>
                                <th>STATUS</th>
                                <th class="text-end pe-4">ACTIONS</th>
                            </tr>
                        </thead>
                        <tbody id="assignmentsTableBody" class="border-top-0">
                        </tbody>
                    </table>
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
                                    <label class="form-label">Salary Structure <span
                                            class="text-danger">*</span></label>
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
                                        <input type="number" class="form-control" id="variablePay" placeholder="0.00"
                                            step="0.01" min="0">
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
        <div class="modal fade" id="viewAssignmentModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog modal-lg modal-dialog-centered">
                <div class="modal-content shadow">
                    <div class="modal-header border-bottom-0 pb-0">
                        <h5 class="modal-title fw-bold text-dark">
                            <i class="fas fa-file-invoice-dollar text-primary me-2"></i>Salary Assignment Details
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body bg-light mt-3 py-4 border-top">
                        <div class="row g-3" id="assignmentDetailsContent">
                            <!-- Dynamic content -->
                        </div>
                    </div>
                    <div class="modal-footer border-top-0 bg-light rounded-bottom">
                        <button type="button" class="btn btn-secondary px-4 fw-bold rounded-pill" data-bs-dismiss="modal">Close</button>
                    </div>
                </div>
            </div>
        </div>



        <style>
            /* Table & UI Enhancements */
            .bg-soft-success {
                background-color: #e6fffa;
                color: #047857;
            }

            .bg-soft-secondary {
                background-color: #f3f4f6;
                color: #4b5563;
            }

            .avatar-sm {
                width: 38px;
                height: 38px;
                font-size: 14px;
            }

            .x-small {
                font-size: 0.7rem;
            }

            #assignmentsTable thead th {
                letter-spacing: 0.05em;
                padding-top: 15px;
                padding-bottom: 15px;
                border-bottom: 1px solid #f1f5f9;
            }

            #assignmentsTable tbody tr {
                transition: background 0.2s;
            }

            .input-group-merge .form-control {
                border-top-left-radius: 0;
                border-bottom-left-radius: 0;
            }

            /* Modal Styling */
            .modal-content {
                border: none;
                border-radius: 1rem;
            }

            .modal-header {
                border-bottom: 1px solid #f1f5f9;
                padding: 1.5rem;
            }

            .modal-body {
                padding: 1.5rem;
            }

            .card.bg-light {
                border: 1px dashed #cbd5e1;
            }
        </style>