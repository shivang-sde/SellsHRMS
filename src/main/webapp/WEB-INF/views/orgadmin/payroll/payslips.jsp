<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageScript" value="payroll/payslip-view" />

<div class="container-fluid py-4">
    <!-- Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="mb-1">
                <i class="fas fa-file-alt me-2 text-primary"></i>Salary Slip
            </h2>
            <p class="text-muted mb-0">Detailed salary breakup for the selected pay period</p>
        </div>
        <div>
            <a href="${pageContext.request.contextPath}/payroll/payruns" class="btn btn-outline-secondary me-2">
                <i class="fas fa-arrow-left me-2"></i>Back
            </a>
            <button id="btnDownloadPdf" class="btn btn-primary">
                <i class="fas fa-download me-2"></i>Download PDF
            </button>
        </div>
    </div>

    <!-- Employee Info -->
    <div class="card shadow-sm border-0 mb-4">
        <div class="card-body">
            <div class="row g-3">
                <div class="col-md-4">
                    <p class="text-muted mb-1">Employee</p>
                    <h6 id="empName">-</h6>
                </div>
                <div class="col-md-4">
                    <p class="text-muted mb-1">Pay Period</p>
                    <h6 id="payPeriod">-</h6>
                </div>
                <div class="col-md-4">
                    <p class="text-muted mb-1">Working / Payment / LOP Days</p>
                    <h6><span id="workingDays">0</span> / <span id="paymentDays">0</span> / <span id="lopDays">0</span></h6>
                </div>
            </div>
        </div>
    </div>

    <!-- Earnings / Deductions -->
    <div class="row g-4 mb-4">
        <div class="col-md-6">
            <div class="card shadow-sm border-0">
                <div class="card-header bg-light">
                    <h6 class="mb-0"><i class="fas fa-arrow-up text-success me-2"></i>Earnings</h6>
                </div>
                <div class="card-body p-0">
                    <table class="table mb-0">
                        <tbody id="earningsBody">
                            <tr><td class="text-muted text-center py-3">No earnings</td></tr>
                        </tbody>
                        <tfoot class="table-light">
                            <tr>
                                <th>Total Earnings</th>
                                <th class="text-end text-success" id="grossPay">₹0.00</th>
                            </tr>
                        </tfoot>
                    </table>
                </div>
            </div>
        </div>

        <div class="col-md-6">
            <div class="card shadow-sm border-0">
                <div class="card-header bg-light">
                    <h6 class="mb-0"><i class="fas fa-arrow-down text-danger me-2"></i>Deductions</h6>
                </div>
                <div class="card-body p-0">
                    <table class="table mb-0">
                        <tbody id="deductionsBody">
                            <tr><td class="text-muted text-center py-3">No deductions</td></tr>
                        </tbody>
                        <tfoot class="table-light">
                            <tr>
                                <th>Total Deductions</th>
                                <th class="text-end text-danger" id="totalDeductions">₹0.00</th>
                            </tr>
                        </tfoot>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <!-- Net Pay Summary -->
    <div class="card shadow-sm border-0">
        <div class="card-body text-center py-4">
            <h5 class="mb-1 text-muted">Net Pay</h5>
            <h2 class="fw-bold text-primary mb-2" id="netPay">₹0.00</h2>
            <p class="text-muted fst-italic" id="netPayWords">In words: -</p>
        </div>
    </div>
</div>
