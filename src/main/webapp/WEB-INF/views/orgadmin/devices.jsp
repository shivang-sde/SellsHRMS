<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <div class="row mb-4">
            <div class="col-12 d-flex justify-content-between align-items-center">
                <h2 class="mb-0">Attendance Devices</h2>
                <button class="btn btn-primary" onclick="openAddDeviceModal()">
                    <i class="fas fa-plus me-2"></i> Add Device
                </button>
            </div>
        </div>

        <div class="card shadow-sm">
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-hover align-middle">
                        <thead class="table-light">
                            <tr>
                                <th>Device Name</th>
                                <th>Device Code</th>
                                <th>Status</th>
                                <th>Created At</th>
                            </tr>
                        </thead>
                        <tbody id="deviceTableBody">
                            <tr>
                                <td colspan="4" class="text-center text-muted">Loading devices...</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- Add Device Modal -->
        <div class="modal fade" id="addDeviceModal" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Add Attendance Device</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <form id="addDeviceForm">
                            <div class="mb-3">
                                <label class="form-label">Device Name</label>
                                <input type="text" class="form-control" name="name" required
                                    placeholder="e.g. Front Desk Kiosk">
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Device Code (Unique)</label>
                                <input type="text" class="form-control" name="deviceCode" required
                                    placeholder="e.g. KIOSK-01">
                            </div>
                        </form>

                        <!-- API Key Display Section (Hidden initially) -->
                        <div id="apiKeySection" class="d-none mt-4 text-center">
                            <div class="alert alert-success">
                                <i class="fas fa-check-circle me-2"></i> Device Created Successfully!
                            </div>
                            <p class="mb-1 fw-bold">API Key (Copy this now, it won't be shown again)</p>
                            <div class="input-group">
                                <input type="text" id="generatedApiKey" class="form-control text-center font-monospace"
                                    readonly>
                                <button class="btn btn-outline-secondary" onclick="copyApiKey()">
                                    <i class="fas fa-copy"></i>
                                </button>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                        <button type="button" class="btn btn-primary" id="saveDeviceBtn" onclick="saveDevice()">Create
                            Device</button>
                    </div>
                </div>
            </div>
        </div>