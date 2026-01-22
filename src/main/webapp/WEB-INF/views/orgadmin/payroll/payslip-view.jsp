<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageScript" value="payroll/payslip-view" />

<div class="container-fluid py-4" id="payslipContainer">
    <!-- Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="mb-1"><i class="fas fa-file-invoice me-2 text-primary"></i>Payslip</h2>
            <p class="text-muted mb-0">Employee monthly salary breakdown</p>
        </div>
        <div>
            <a href="${pageContext.request.contextPath}/payroll/payruns" class="btn btn-outline-secondary me-2">
                <i class="fas fa-arrow-left"></i> Back
            </a>
            <button id="btnPrint" class="btn btn-primary">
                <i class="fas fa-print"></i> Print / Download
            </button>
            <button id="btnPdf" class="btn btn-outline-success">
             <i class="fas fa-file-pdf"></i> Download PDF
            </button>
        </div>
    </div>

    <!-- Employee & Pay Details -->
    <div class="card shadow-sm border-0 mb-4">
        <div class="card-body">
            <div class="row g-3">
                <div class="col-md-6">
                    <h5 class="fw-bold mb-2" id="empName">Employee Name</h5>
                    <p class="mb-1 text-muted" id="empDesignation">Designation: -</p>
                    <p class="mb-0 text-muted" id="empDepartment">Department: -</p>
                </div>
                <div class="col-md-6 text-md-end">
                    <p class="mb-1"><strong>Pay Period:</strong> <span id="payPeriod">-</span></p>
                    <p class="mb-1"><strong>Pay Run:</strong> <span id="payRunLabel">-</span></p>
                    <p class="mb-0"><strong>Generated On:</strong> <span id="generatedDate">-</span></p>
                </div>
            </div>
        </div>
    </div>

    <!-- Earnings & Deductions -->
    <div class="row g-4">
        <!-- Earnings -->
        <div class="col-md-6">
            <div class="card border-success shadow-sm h-100">
                <div class="card-header bg-success bg-opacity-10">
                    <h6 class="mb-0 fw-bold text-success"><i class="fas fa-arrow-up me-2"></i>Earnings</h6>
                </div>
                <div class="card-body p-0">
                    <table class="table mb-0 table-sm align-middle">
                        <tbody id="earningsTable"></tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- Deductions -->
        <div class="col-md-6">
            <div class="card border-danger shadow-sm h-100">
                <div class="card-header bg-danger bg-opacity-10">
                    <h6 class="mb-0 fw-bold text-danger"><i class="fas fa-arrow-down me-2"></i>Deductions</h6>
                </div>
                <div class="card-body p-0">
                    <table class="table mb-0 table-sm align-middle">
                        <tbody id="deductionsTable"></tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <!-- Summary Footer -->
    <div class="card mt-4 shadow-sm border-0">
        <div class="card-body text-center">
            <h5 class="fw-bold text-primary">Net Pay: <span id="netPay">₹0.00</span></h5>
            <p class="text-muted mb-0" id="netPayWords">In words: -</p>
        </div>
    </div>
</div>
