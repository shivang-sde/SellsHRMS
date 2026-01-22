<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!-- <c:set var="pageScript" value="payroll/salary-components" /> -->

<div class="container-fluid py-4">
    <!-- Page Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="mb-1"><i class="fas fa-coins me-2 text-primary"></i>Salary Components</h2>
            <p class="text-muted mb-0">Define and manage salary components for your organization</p>
        </div>
        <button class="btn btn-primary" id="btnAddComponent">
            <i class="fas fa-plus me-2"></i>Add Component
        </button>
    </div>

    <!-- Filter Section -->
    <div class="card shadow-sm mb-4">
        <div class="card-body">
            <div class="row g-3">
                <div class="col-md-3">
                    <label class="form-label small text-muted">Component Type</label>
                    <select class="form-select" id="filterType">
                        <option value="">All Types</option>
                        <option value="EARNING">Earnings</option>
                        <option value="DEDUCTION">Deductions</option>
                    </select>
                </div>
                <div class="col-md-3">
                    <label class="form-label small text-muted">Calculation Type</label>
                    <select class="form-select" id="filterCalcType">
                        <option value="">All Calculation Types</option>
                        <option value="FIXED">Fixed</option>
                        <option value="PERCENTAGE">Percentage</option>
                        <option value="FORMULA">Formula</option>
                    </select>
                </div>
                <div class="col-md-4">
                    <label class="form-label small text-muted">Search</label>
                    <input type="text" class="form-control" id="searchComponent" placeholder="Search by name or abbreviation...">
                </div>
                <div class="col-md-2 d-flex align-items-end">
                    <button class="btn btn-outline-secondary w-100" id="btnResetFilters">
                        <i class="fas fa-redo me-2"></i>Reset
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- Components Table -->
    <div class="card shadow-sm">
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-hover align-middle" id="componentsTable">
                    <thead class="table-light">
                        <tr>
                            <th>Name</th>
                            <th>Abbr</th>
                            <th>Type</th>
                            <th>Calc Type</th>
                            <th class="text-center">Taxable</th>
                            <th class="text-center">CTC</th>
                            <th class="text-center">DOP</th>
                            <th>Status</th>
                            <th class="text-end">Actions</th>
                        </tr>
                    </thead>
                    <tbody id="componentsTableBody">
                        <!-- Dynamic content -->
                    </tbody>
                </table>
            </div>
            
            <!-- Empty State -->
            <div id="emptyState" class="text-center py-5 d-none">
                <i class="fas fa-inbox fa-3x text-muted mb-3"></i>
                <h5 class="text-muted">No salary components found</h5>
                <p class="text-muted">Create your first salary component to get started</p>
                <button class="btn btn-primary mt-3" id="btnAddComponentEmpty">
                    <i class="fas fa-plus me-2"></i>Add Component
                </button>
            </div>

            <!-- Loading State -->
            <div id="loadingState" class="text-center py-5">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
                <p class="text-muted mt-3">Loading components...</p>
            </div>
        </div>
    </div>
</div>

<!-- Add/Edit Component Modal -->
<div class="modal fade" id="componentModal" tabindex="-1" aria-hidden="true" style="z-index:2000;">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="componentModalTitle">
                    <i class="fas fa-coins me-2"></i>Add Salary Component
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body ">
                <form id="componentForm">
                    <input type="hidden" id="componentId">
                    
                    <div class="row g-3">
                        <!-- Basic Information -->
                        <div class="col-12">
                            <h6 class="text-primary border-bottom pb-2 mb-3">Basic Information</h6>
                        </div>
                        
                        <div class="col-md-8">
                            <label class="form-label">Component Name <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="componentName" required placeholder="e.g., Basic Salary">
                        </div>
                        
                        <div class="col-md-4">
                            <label class="form-label">Abbreviation <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="componentAbbr" required placeholder="e.g., BASIC">
                        </div>
                        
                        <div class="col-md-6">
                            <label class="form-label">Component Type <span class="text-danger">*</span></label>
                            <select class="form-select" id="componentType" required>
                                <option value="">Select Type</option>
                                <option value="EARNING">Earning</option>
                                <option value="DEDUCTION">Deduction</option>
                            </select>
                        </div>
                        
                        <div class="col-md-6">
                            <label class="form-label">Calculation Type <span class="text-danger">*</span></label>
                             <select class="form-select" id="calculationType" required>
                                    <option value="">Select Calculation Type</option>
                                    <option value="FIXED">Fixed Amount</option>
                                    <!-- <option value="PERCENTAGE">Percentage of Base Pay</option> -->
                                    <option value="FORMULA">Formula Based</option>
                            </select>
                        </div>

                        <div class="col-md-4 d-none" id="fixedAmountSection">
                            <label class="form-label">Amount<span class="text-danger">*</span></label>
                            <input type="number" class="form-control" id="fixedAmount"  placeholder="e.g., 15000.00">
                        </div>

                         <div class="col-12" id="conditionSection">
                <label class="form-label">Condition <span class="text-danger">*</span></label>
                <textarea class="form-control font-monospace" id="condition" rows="3"
                    placeholder="e.g., BASE > 15000 or BASIC < 15000"></textarea>

            <!-- Custom Formula Section -->
            <div class="col-12 d-none" id="formulaSection">
                <label class="form-label">Custom Formula <span class="text-danger">*</span></label>
                <textarea class="form-control font-monospace" id="componentFormula" rows="3"
                    placeholder="e.g., (BASE + DA) * 0.12 or BASIC * 0.1"></textarea>
    
    <!-- Formula Help / Instructions -->
            <div class="alert alert-info mt-2 py-2 small">
                <strong>💡 Formula Instructions:</strong>
                    <ul class="mb-1 ps-3">
                    <li>Use <code>BASE</code> to reference the employee’s Base Pay (from salary structure).</li>
                    <li>Use abbreviations of other salary components (e.g., <code>BASIC</code>, <code>HRA</code>, <code>DA</code>) to reference their calculated values.</li>
                    <li>Supported math operators: <code>+</code>, <code>-</code>, <code>*</code>, <code>/</code>, parentheses <code>()</code>.</li>
                    <li>Conditional logic can be added using ternary expressions:<br>
                        <code>(BASE &gt; 50000) ? BASE * 0.1 : BASE * 0.05</code>
                    </li>
                    <li>You can also reference constants or percentages, e.g. <code>(BASE * 0.12)</code> for 12% of base pay.</li>
                    </ul>
                <small class="text-muted">Make sure all referenced components exist in the salary structure.</small>
            </div>
        </div>


                        
                        <div class="col-12">
                            <label class="form-label">Description</label>
                            <textarea class="form-control" id="componentDescription" rows="2" 
                                      placeholder="Brief description of this component"></textarea>
                        </div>
                        
                        <!-- Flags & Settings -->
                        <div class="col-12 mt-4">
                            <h6 class="text-primary border-bottom pb-2 mb-3">Component Settings</h6>
                        </div>
                        
                        <div class="col-md-6">
                            <div class="form-check form-switch">
                                <input class="form-check-input" type="checkbox" id="isTaxable">
                                <label class="form-check-label" for="isTaxable">
                                    <i class="fas fa-receipt me-1"></i>Taxable Component
                                </label>
                            </div>
                            <small class="form-text text-muted">Include in taxable income calculation</small>
                        </div>
                        
                        <div class="col-md-6">
                            <div class="form-check form-switch">
                                <input class="form-check-input" type="checkbox" id="includeInCTC">
                                <label class="form-check-label" for="includeInCTC">
                                    <i class="fas fa-chart-line me-1"></i>Include in CTC
                                </label>
                            </div>
                            <small class="form-text text-muted">Count towards Cost to Company</small>
                        </div>

                        <div class="col-md-6">
                            <div class="form-check form-switch">
                                <input class="form-check-input" type="checkbox" id="dependsOnPaymentDays">
                                <label class="form-check-label" for="dependsOnPaymentDays">
                                    <i class="fas fa-chart-line me-1"></i>Depends upon payment days.
                                </label>
                            </div>
                            <small class="form-text text-muted">Depending upon Attendance</small>
                        </div>

                        <div class="col-md-6">
                            <div class="form-check form-switch">
                                <input class="form-check-input" type="checkbox" id="roundToNearest">
                                <label class="form-check-label" for="roundToNearest">
                                    <i class="fas fa-chart-line me-1"></i>Round to Nearest Value 
                                </label>
                            </div>
                            <small class="form-text text-muted">Round to nearest for easy calaulation</small>
                        </div>
                        
                        <div class="col-md-6">
                            <div class="form-check form-switch">
                                <input class="form-check-input" type="checkbox" id="isActive" checked>
                                <label class="form-check-label" for="isActive">
                                    <i class="fas fa-toggle-on me-1"></i>Active
                                </label>
                            </div>
                            <small class="form-text text-muted">Enable this component</small>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                    <i class="fas fa-times me-2"></i>Cancel
                </button>
                <button type="button" class="btn btn-primary" id="btnSaveComponent">
                    <i class="fas fa-save me-2"></i>Save Component
                </button>
            </div>
        </div>
    </div>
</div>