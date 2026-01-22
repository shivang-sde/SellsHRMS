<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageScript" value="payroll/payrun-details" />

<div class="container-fluid py-4">

    <!-- Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="mb-1">
                <i class="fas fa-file-invoice-dollar me-2 text-primary"></i>Pay Run Summary
            </h2>
            <p class="text-muted mb-0">Salary slips generated for this payroll cycle</p>
        </div>
        <a href="${pageContext.request.contextPath}/payroll/payruns" class="btn btn-outline-secondary">
            <i class="fas fa-arrow-left me-2"></i>Back to Pay Runs
        </a>
    </div>

    <!-- Summary Cards -->
    <div class="row g-4 mb-4">
        <div class="col-md-4">
            <div class="card shadow-sm border-0 text-center">
                <div class="card-body">
                    <h6 class="text-muted">Total Gross</h6>
                    <h3 class="fw-bold text-success mb-0" id="totalGross">₹0.00</h3>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card shadow-sm border-0 text-center">
                <div class="card-body">
                    <h6 class="text-muted">Total Deductions</h6>
                    <h3 class="fw-bold text-danger mb-0" id="totalDeductions">₹0.00</h3>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card shadow-sm border-0 text-center">
                <div class="card-body">
                    <h6 class="text-muted">Net Pay</h6>
                    <h3 class="fw-bold text-primary mb-0" id="totalNet">₹0.00</h3>
                </div>
            </div>
        </div>
    </div>

    <!-- PayRun Info -->
    <div class="card shadow-sm border-0 mb-4">
        <div class="card-body">
            <div class="row g-3">
                <div class="col-md-4">
                    <p class="text-muted mb-1">Pay Period</p>
                    <h6 id="period">-</h6>
                </div>
                <div class="col-md-4">
                    <p class="text-muted mb-1">Status</p>
                    <span class="badge bg-info" id="runStatus">-</span>
                </div>
                <div class="col-md-4">
                    <p class="text-muted mb-1">Run Date</p>
                    <h6 id="runDate">-</h6>
                </div>
            </div>
        </div>
    </div>

    <!-- Salary Slip List -->
    <div class="card shadow-sm border-0">
        <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <h5 class="mb-0">
                <i class="fas fa-users me-2 text-primary"></i>Employee Salary Slips
            </h5>
            <button id="btnExport" class="btn btn-outline-primary btn-sm">
                <i class="fas fa-file-export me-1"></i>Export CSV
            </button>
        </div>

        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-striped align-middle">
                    <thead class="table-light">
                        <tr>
                            <th>Employee</th>
                            <th>Gross Pay</th>
                            <th>Deductions</th>
                            <th>Net Pay</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody id="slipTableBody">
                        <tr>
                            <td colspan="6" class="text-center text-muted py-4">Loading slips...</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
