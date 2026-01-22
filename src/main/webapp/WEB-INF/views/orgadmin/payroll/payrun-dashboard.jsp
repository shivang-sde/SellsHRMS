<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageScript" value="payroll/payrun-dashboard" />

<div class="container-fluid py-4">
    <!-- Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="mb-1"><i class="fas fa-calendar-check me-2 text-primary"></i>Payroll Runs</h2>
            <p class="text-muted mb-0">View completed and upcoming payroll cycles</p>
        </div>
        <button class="btn btn-outline-secondary btn-lg" id="btnRefreshRuns">
            <i class="fas fa-sync-alt me-2"></i>Refresh
        </button>
    </div>

    <!-- Stats Cards -->
    <div class="row g-4 mb-4">
        <div class="col-md-3">
            <div class="card shadow-sm">
                <div class="card-body text-center">
                    <p class="text-muted mb-1">Total Payruns</p>
                    <h3 id="statTotal">0</h3>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card shadow-sm">
                <div class="card-body text-center">
                    <p class="text-muted mb-1">Completed</p>
                    <h3 class="text-success" id="statCompleted">0</h3>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card shadow-sm">
                <div class="card-body text-center">
                    <p class="text-muted mb-1">Processing</p>
                    <h3 class="text-warning" id="statProcessing">0</h3>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card shadow-sm">
                <div class="card-body text-center">
                    <p class="text-muted mb-1">Next Payroll Date</p>
                    <h5 id="nextRunDate" class="text-primary">-</h5>
                </div>
            </div>
        </div>
    </div>

    <!-- Payruns List -->
    <div class="card shadow-sm">
        <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <h5 class="mb-0"><i class="fas fa-list me-2 text-primary"></i>Payroll History</h5>
            <button id="btnExportRuns" class="btn btn-outline-primary btn-sm">
                <i class="fas fa-file-excel me-1"></i>Export All
            </button>
        </div>
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead class="table-light">
                        <tr>
                            <th>Pay Period</th>
                            <th>Status</th>
                            <th>Total Employees</th>
                            <th class="text-end">Total Net</th>
                            <th class="text-end">Run Date</th>
                            <th class="text-end">Actions</th>
                        </tr>
                    </thead>
                    <tbody id="payrunsTableBody">
                        <tr>
                            <td colspan="6" class="text-center text-muted py-4">Loading payruns...</td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <div id="emptyState" class="text-center py-5 d-none">
                <i class="fas fa-calendar-times fa-3x text-muted mb-3"></i>
                <h5 class="text-muted">No pay runs yet</h5>
                <p class="text-muted">Payroll cycle will run automatically as per organisation policy</p>
            </div>
        </div>
    </div>
</div>
