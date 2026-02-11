<%@ page contentType="text/html;charset=UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

        <div class="container-fluid">

            <div class="card shadow-sm mt-3">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h5 class="mb-0"><i class="fa-solid fa-screwdriver-wrench me-2"></i>Maintenance Logs</h5>
                    <button class="btn btn-primary btn-sm" onclick="openMaintenanceModal()">
                        <i class="fa-solid fa-plus me-1"></i> Add Log
                    </button>
                </div>

                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-striped table-hover mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th>Asset Code</th>
                                    <th>Asset Name</th>
                                    <th>Date</th>
                                    <th>Description</th>
                                    <th>Cost</th>
                                    <th>Performed By</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody id="maintenanceTableBody">
                                <tr>
                                    <td colspan="7" class="text-center">Loading...</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>

        <!-- MODAL: MAINTENANCE LOG -->
        <div class="modal fade" id="maintenanceModal">
            <div class="modal-dialog">
                <form id="maintenanceModalForm" class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Add Maintenance Log</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <label class="form-label">Asset</label>
                            <select id="maintenanceAssetSelect" class="form-select" required></select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Date</label>
                            <input id="maintenanceDateInput" type="date" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Description</label>
                            <textarea id="maintenanceDescInput" class="form-control" rows="2"></textarea>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Cost</label>
                            <input id="maintenanceCostInput" type="number" step="0.01" class="form-control">
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Performed By</label>
                            <input id="maintenancePerformedByInput" class="form-control">
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-secondary" type="button" data-bs-dismiss="modal">Cancel</button>
                        <button class="btn btn-primary" type="submit">Save</button>
                    </div>
                </form>
            </div>
        </div>