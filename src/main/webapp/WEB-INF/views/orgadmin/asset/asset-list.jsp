<%@ page contentType="text/html;charset=UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

        <div class="container-fluid">

            <div class="card shadow-sm mt-3">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h5 class="mb-0"><i class="fa-solid fa-boxes-stacked me-2"></i>Assets</h5>
                    <button class="btn btn-primary btn-sm" onclick="openAssetModal()">
                        <i class="fa-solid fa-plus me-1"></i> Add Asset
                    </button>
                </div>

                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-striped table-hover mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th>Code</th>
                                    <th>Name</th>
                                    <th>Category</th>
                                    <th>Status</th>
                                    <th>Condition</th>
                                    <th>Assigned To</th>
                                    <th>Cost</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody id="assetTableBody">
                                <tr>
                                    <td colspan="8" class="text-center">Loading...</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>

        <!-- MODAL: ASSET -->
        <div class="modal fade" id="assetModal">
            <div class="modal-dialog modal-lg">
                <form id="assetModalForm" class="modal-content">
                    <div class="modal-header">
                        <h5 id="assetModalTitle" class="modal-title">Add Asset</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>

                    <div class="modal-body">
                        <input type="hidden" id="assetId">

                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Asset Code <span class="text-danger">*</span></label>
                                <input id="assetCodeInput" class="form-control" required>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Name <span class="text-danger">*</span></label>
                                <input id="assetNameInput" class="form-control" required>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Description</label>
                            <textarea id="assetDescInput" class="form-control" rows="2"></textarea>
                        </div>

                        <div class="row">
                            <div class="col-md-4 mb-3">
                                <label class="form-label">Category</label>
                                <select id="assetCategorySelect" class="form-select">
                                    <option value="">-- None --</option>
                                </select>
                            </div>
                            <div class="col-md-4 mb-3">
                                <label class="form-label">Vendor</label>
                                <select id="assetVendorSelect" class="form-select">
                                    <option value="">-- None --</option>
                                </select>
                            </div>
                            <div class="col-md-4 mb-3">
                                <label class="form-label">Condition</label>
                                <select id="assetConditionSelect" class="form-select">
                                    <option value="NEW">New</option>
                                    <option value="GOOD">Good</option>
                                    <option value="FAIR">Fair</option>
                                    <option value="DAMAGED">Damaged</option>
                                </select>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Purchase Date</label>
                                <input id="assetPurchaseDateInput" type="date" class="form-control">
                            </div>
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Cost</label>
                                <input id="assetCostInput" type="number" step="0.01" class="form-control">
                            </div>
                        </div>
                    </div>

                    <div class="modal-footer">
                        <button class="btn btn-secondary" type="button" data-bs-dismiss="modal">Cancel</button>
                        <button class="btn btn-primary" type="submit">Save</button>
                    </div>
                </form>
            </div>
        </div>

        <!-- MODAL: ASSIGN ASSET -->
        <div class="modal fade" id="assignModal">
            <div class="modal-dialog">
                <form id="assignForm" class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Assign Asset</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <input type="hidden" id="assignAssetId">
                        <div class="mb-3">
                            <label class="form-label">Employee</label>
                            <select id="assignEmployeeSelect" class="form-select" required></select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Remarks</label>
                            <textarea id="assignRemarks" class="form-control" rows="2"></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-secondary" type="button" data-bs-dismiss="modal">Cancel</button>
                        <button class="btn btn-success" type="submit">Assign</button>
                    </div>
                </form>
            </div>
        </div>