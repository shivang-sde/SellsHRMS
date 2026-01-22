<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Page Header -->
<div class="row mb-4">
    <div class="col-12">
        <div class="d-flex justify-content-between align-items-center">
            <div>
                <h3 class="mb-1">Salary Slip Preview</h3>
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb mb-0">
                        <li class="breadcrumb-item"><a href="/dashboard">Home</a></li>
                        <li class="breadcrumb-item"><a href="/payroll">Payroll</a></li>
                        <li class="breadcrumb-item active">Salary Slip</li>
                    </ol>
                </nav>
            </div>
            <div>
                <button type="button" class="btn btn-outline-primary me-2" onclick="printSlip()">
                    <i class="bi bi-printer"></i> Print
                </button>
                <button type="button" class="btn btn-danger me-2" onclick="downloadSlipPDF()">
                    <i class="bi bi-file-pdf"></i> Download PDF
                </button>
                <a href="/payroll/salary-slips" class="btn btn-secondary">
                    <i class="bi bi-arrow-left"></i> Back
                </a>
            </div>
        </div>
    </div>
</div>

<!-- Error Alert (if any) -->
<c:if test="${not empty error}">
    <div class="row mb-3">
        <div class="col-12">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="bi bi-exclamation-triangle-fill"></i> ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </div>
    </div>
</c:if>

<!-- Salary Slip Content -->
<div class="row">
    <div class="col-12">
        <div class="card shadow-sm">
            <div class="card-body p-0">
                <!-- Slip Container -->
                <div id="salarySlipContent" class="p-4">
                    ${slipHtml}
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Hidden input for salary slip ID -->
<input type="hidden" id="salarySlipId" value="${salarySlipId}">

<!-- Print Styles -->
<style>
    @media print {
        /* Hide everything except the slip */
        body * {
            visibility: hidden;
        }
        
        #salarySlipContent,
        #salarySlipContent * {
            visibility: visible;
        }
        
        #salarySlipContent {
            position: absolute;
            left: 0;
            top: 0;
            width: 100%;
            padding: 20px !important;
        }
        
        /* Remove card styling for print */
        .card {
            border: none !important;
            box-shadow: none !important;
        }
    }

    /* Slip Container Styling */
    #salarySlipContent {
        background: white;
        min-height: 800px;
    }

    /* Ensure tables are properly formatted */
    #salarySlipContent table {
        page-break-inside: avoid;
    }
</style>