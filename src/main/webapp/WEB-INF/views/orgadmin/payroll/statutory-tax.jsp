<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="container-fluid mt-3">

    <!-- ------------------ TABS ------------------ -->
    <ul class="nav nav-tabs nav-text" id="payrollTab" role="tablist">
        <li class="nav-item">
            <a class="nav-link nav-text active" id="statutory-tab" data-bs-toggle="tab" href="#statutory" role="tab">Statutory Components</a>
        </li>
        <li class="nav-item">
            <a class="nav-link nav-text" id="mapping-tab" data-bs-toggle="tab" href="#mapping" role="tab">Statutory Mappings</a>
        </li>
        <li class="nav-item">
            <a class="nav-link nav-text" id="tax-tab" data-bs-toggle="tab" href="#tax" role="tab">Income Tax</a>
        </li>
    </ul>

    <div class="tab-content mt-3" id="payrollTabContent">

        <!-- ================== STATUTORY COMPONENTS ================== -->
        <div class="tab-pane fade show active" id="statutory" role="tabpanel">
            <div class="mb-2">
                <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#statutoryModal">+ Add Component</button>
            </div>
            <table class="table table-bordered table-striped" id="statutoryTable">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Code</th>
                    <th>Country</th>
                    <th>State</th>
                    <th>Active</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody></tbody>
            </table>
        </div>

        <!-- ================== STATUTORY MAPPING ================== -->
        <div class="tab-pane fade" id="mapping" role="tabpanel">
            <div class="mb-2">
                <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#mappingModal">+ Add Mapping</button>
            </div>
            <div class="alert alert-info mt-2 py-2 small">
            <strong>💡 Notice :</strong>
                <ul class="mb-1 ps-3">
                    <li>By default, every <code>Statutory Component Rule</code> is applied to the employee’s <code>Base Pay</code>.</li>
                    <li>If you want the rule to apply to other salary components (e.g., allowances, bonuses), you need to explicitly map those components here.</li>
                    <li>This ensures statutory calculations (like PF/ESI) are correctly applied across all relevant parts of the salary.</li>
                </ul>
                <small class="text-muted">
                    ⚠️ Make sure the components you select are already defined in the salary structure, otherwise the mapping will not work.
                </small>
            </div>
            <table class="table table-bordered table-striped" id="mappingTable">
                <thead>
                <tr>
                    <th>Statutory Component</th>
                    <th>Salary Component</th>
                    <th>Employee %</th>
                    <th>Employer %</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody></tbody>
            </table>
        </div>

        <!-- ================== TAX COMPONENT ================== -->
        <div class="tab-pane fade" id="tax" role="tabpanel">
            <div class="mb-2">
                <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#taxModal">+ Add Tax Slab</button>
            </div>
            <table class="table table-bordered table-striped" id="taxTable">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Effective From</th>
                    <th>Effective To</th>
                    <th>Allow Exemption</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody></tbody>
            </table>
        </div>

    </div>
</div>

<!-- ================== STATUTORY MODAL ================== -->
<div class="modal fade" id="statutoryModal" tabindex="-1">
    <div class="modal-dialog modal-xl">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Add / Edit Statutory Component</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>

            <div class="modal-body">
                <form id="statutoryForm">
                    <input type="hidden" id="statutoryId">
                    <div class="row mb-2">
                        <div class="col-md-3">
                            <label>Name</label>
                            <input type="text" class="form-control" id="statutoryName" required>
                        </div>
                        <div class="col-md-3">
                            <label>Code</label>
                            <input type="text" class="form-control" id="statutoryCode">
                        </div>
                        <div class="col-md-3">
                            <label>Country</label>
                            <input type="text" class="form-control" id="statutoryCountry">
                        </div>
                        <div class="col-md-3">
                            <label>State</label>
                            <input type="text" class="form-control" id="statutoryState">
                        </div>
                    </div>

                    <div class="mb-2">
                        <label>Rules</label>
                       <div class="table-responsive  payroll-rule-table-wrapper">
                         <table class="table table-sm payroll-rule-table align-middle" id="statutoryRuleTable">
                            <thead>
                            <tr>
                                <th>Effective From</th>
                                <th>Effective To</th>
                                <th>Employee %</th>
                                <th>Employer %</th>
                                <th>Min Salary</th>
                                <th>Max Salary</th>
                                <th>Deduction Cycle</th>
                                <th>✖</th>
                            </tr>
                            </thead>
                            <tbody></tbody>
                        </table>
                       </div>
                        <button type="button" class="btn btn-secondary btn-sm" id="addStatutoryRule">+ Add Rule</button>
                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-success" id="saveStatutory">Save</button>
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<!-- ================== MAPPING MODAL ================== -->
<div class="modal fade" id="mappingModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Add / Edit Mapping</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>

             <div class="alert alert-info mt-2 py-2 small">
            <strong>💡 Notice :</strong>
            <ul class="mb-1 ps-3">
                <li>By default, every <code>Statutory Component Rule</code> is applied to the employee’s <code>Base Pay</code>.</li>
                <li>If you want the rule to apply to other salary components (e.g., allowances, bonuses), you need to explicitly map those components here.</li>
                 <li>This ensures statutory calculations (like PF/ESI) are correctly applied across all relevant parts of the salary.</li>
             </ul>
            <small class="text-muted">
                ⚠️ Make sure the components you select are already defined in the salary structure, otherwise the mapping will not work.
            </small>
        </div>

            <div class="modal-body">
                <form id="mappingForm">
                    <input type="hidden" id="mappingId">
                    <div class="row mb-2">
                        <div class="col-md-6">
                            <label>Statutory Component</label>
                            <select class="form-select" id="mappingStatutoryComponent"></select>
                        </div>
                        <div class="col-md-6">
                            <label>Salary Component</label>
                            <select class="form-select" id="mappingSalaryComponent"></select>
                        </div>
                    </div>
                    <div class="row mb-2">
                        <!-- <div class="col-md-3">
                            <label>Employee %</label>
                            <input type="number" class="form-control" id="mappingEmployeePercent">
                        </div>
                        <div class="col-md-3">
                            <label>Employer %</label>
                            <input type="number" class="form-control" id="mappingEmployerPercent">
                        </div>
                        <div class="col-md-3">
                            <label>Country</label>
                            <input type="text" class="form-control" id="mappingCountry">
                        </div>
                        <div class="col-md-3">
                            <label>State</label>
                            <input type="text" class="form-control" id="mappingState">
                        </div> -->
                    </div>
                    <div class="form-check mb-2">
                        <input type="checkbox" class="form-check-input" id="mappingInclude">
                        <label class="form-check-label">Include In Calculation</label>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-success" id="saveMapping">Save</button>
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<!-- ================== TAX MODAL ================== -->
<div class="modal fade" id="taxModal" tabindex="-1">
    <div class="modal-dialog modal-xl">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Add / Edit Tax Slab</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <form id="taxForm">
                    <input type="hidden" id="taxId">
                    <div class="row mb-2">
                        <div class="col-md-4">
                            <label>Name</label>
                            <input type="text" class="form-control" id="taxName" required>
                        </div>
                        <div class="col-md-4">
                            <label>Effective From</label>
                            <input type="date" class="form-control" id="taxFrom" required>
                        </div>
                        <div class="col-md-4">
                            <label>Effective To</label>
                            <input type="date" class="form-control" id="taxTo">
                        </div>
                    </div>
                    <div class="form-check mb-2">
                        <input type="checkbox" class="form-check-input" id="taxExemption">
                        <label class="form-check-label">Allow Exemption</label>
                    </div>

                    <div class="mb-2">
                        <label>Rules</label>
                        <div class="table-responsive payroll-rule-table-wrapper">
                            <table class="table table-sm payroll-rule-table align-middle" id="taxRuleTable">
                            <thead>
                            <tr>
                                <th>Min Income</th>
                                <th>Max Income</th>
                                <th>Deduction %</th>
                                <th>Condition</th>
                                <th>✖</th>
                            </tr>
                            </thead>
                            <tbody></tbody>
                        </table>
                        </div>
                        <button type="button" class="btn btn-secondary btn-sm" id="addTaxRule">+ Add Rule</button>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-success" id="saveTax">Save</button>
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

