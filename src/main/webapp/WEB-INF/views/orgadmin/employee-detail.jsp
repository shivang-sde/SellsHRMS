<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<input type="hidden" id="employeeId" value="${employeeId}">

<div class="row mb-3">
    <div class="col-12">
        <div class="d-flex justify-content-between align-items-center">
            <h2 class="mb-0">Employee Details</h2>
            <div class="btn-group">
                <a href="/org/employees" class="btn btn-outline-secondary">
                    <i class="fas fa-arrow-left me-2"></i>Back
                </a>
                <a href="/org/employee/edit/${employeeId}" class="btn btn-primary">
                    <i class="fas fa-edit me-2"></i>Edit
                </a>
            </div>
        </div>
    </div>
</div>

<!-- Loading State -->
<div id="loadingState" class="text-center py-5">
    <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading...</span>
    </div>
    <p class="mt-2 text-muted">Loading employee details...</p>
</div>

<!-- Employee Details Content -->
<div id="employeeContent" style="display:none;">
    
    <!-- Basic Info Card -->
    <div class="card mb-3">
        <div class="card-body">
            <div class="row">
                <div class="col-md-2 text-center">
                    <img id="employeePhoto" src="/images/default-avatar.png" 
                         class="img-fluid rounded-circle" 
                         style="width: 120px; height: 120px; object-fit: cover;" 
                         alt="Employee Photo">
                </div>
                <div class="col-md-10">
                    <div class="row">
                        <div class="col-md-6">
                            <h3 id="employeeName" class="mb-2">--</h3>
                            <p class="text-muted mb-1">
                                <i class="fas fa-id-badge me-2"></i>
                                <span id="employeeCode">--</span>
                            </p>
                            <p class="text-muted mb-1">
                                <i class="fas fa-envelope me-2"></i>
                                <span id="workEmail">--</span>
                            </p>
                            <p class="text-muted mb-1">
                                <i class="fas fa-phone me-2"></i>
                                <span id="phone">--</span>
                            </p>
                        </div>
                        <div class="col-md-6 text-end">
                            <span id="statusBadge" class="badge bg-success mb-2">Active</span>
                            <p class="mb-1"><strong>Department:</strong> <span id="department">--</span></p>
                            <p class="mb-1"><strong>Designation:</strong> <span id="designation">--</span></p>
                            <p class="mb-1"><strong>Type:</strong> <span id="employmentType">--</span></p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Tab Navigation -->
    <ul class="nav nav-tabs mb-3" id="employeeTabs" role="tablist">
        <li class="nav-item" role="presentation">
            <button class="nav-link active" id="personal-tab" data-bs-toggle="tab" 
                    data-bs-target="#personal" type="button" role="tab">
                <i class="fas fa-user me-2"></i>Personal Info
            </button>
        </li>
        <li class="nav-item" role="presentation">
            <button class="nav-link" id="address-tab" data-bs-toggle="tab" 
                    data-bs-target="#address" type="button" role="tab">
                <i class="fas fa-map-marker-alt me-2"></i>Address
            </button>
        </li>
        <li class="nav-item" role="presentation">
            <button class="nav-link" id="company-tab" data-bs-toggle="tab" 
                    data-bs-target="#company" type="button" role="tab">
                <i class="fas fa-building me-2"></i>Company Info
            </button>
        </li>
        <li class="nav-item" role="presentation">
            <button class="nav-link" id="documents-tab" data-bs-toggle="tab" 
                    data-bs-target="#documents" type="button" role="tab">
                <i class="fas fa-file-alt me-2"></i>Documents
            </button>
        </li>
        <li class="nav-item" role="presentation">
            <button class="nav-link" id="bank-tab" data-bs-toggle="tab" 
                    data-bs-target="#bank" type="button" role="tab">
                <i class="fas fa-university me-2"></i>Bank Details
            </button>
        </li>
    </ul>

    <!-- Tab Content -->
    <div class="tab-content" id="employeeTabContent">
        
        <!-- Personal Info Tab -->
        <div class="tab-pane fade show active" id="personal" role="tabpanel">
            <div class="card">
                <div class="card-body">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <strong>First Name:</strong>
                            <p id="firstName">--</p>
                        </div>
                        <div class="col-md-6">
                            <strong>Last Name:</strong>
                            <p id="lastName">--</p>
                        </div>
                        <div class="col-md-6">
                            <strong>Date of Birth:</strong>
                            <p id="dob">--</p>
                        </div>
                        <div class="col-md-6">
                            <strong>Gender:</strong>
                            <p id="gender">--</p>
                        </div>
                        <div class="col-md-6">
                            <strong>Personal Email:</strong>
                            <p id="personalEmail">--</p>
                        </div>
                        <div class="col-md-6">
                            <strong>Alternate Phone:</strong>
                            <p id="alternatePhone">--</p>
                        </div>
                        <div class="col-md-6">
                            <strong>Father's Name:</strong>
                            <p id="fatherName">--</p>
                        </div>
                        <div class="col-md-6">
                            <strong>Nationality:</strong>
                            <p id="nationality">--</p>
                        </div>
                        <div class="col-md-6">
                            <strong>Marital Status:</strong>
                            <p id="maritalStatus">--</p>
                        </div>
                        <div class="col-md-6">
                            <strong>Reference Name:</strong>
                            <p id="referenceName">--</p>
                        </div>
                        <div class="col-md-6">
                            <strong>Reference Phone:</strong>
                            <p id="referencePhone">--</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Address Tab -->
        <div class="tab-pane fade" id="address" role="tabpanel">
            <div class="row">
                <div class="col-md-6 mb-3">
                    <div class="card">
                        <div class="card-header bg-primary text-white">
                            <h6 class="mb-0">Local Address</h6>
                        </div>
                        <div class="card-body" id="localAddressContent">
                            <p class="text-muted">No address available</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 mb-3">
                    <div class="card">
                        <div class="card-header bg-primary text-white">
                            <h6 class="mb-0">Permanent Address</h6>
                        </div>
                        <div class="card-body" id="permanentAddressContent">
                            <p class="text-muted">No address available</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Company Info Tab -->
        <div class="tab-pane fade" id="company" role="tabpanel">
            <div class="card">
                <div class="card-body">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <strong>Employee Code:</strong>
                            <p id="empCode">--</p>
                        </div>
                        <div class="col-md-6">
                            <strong>Date of Joining:</strong>
                            <p id="dateOfJoining">--</p>
                        </div>
                        <div class="col-md-6">
                            <strong>Date of Exit:</strong>
                            <p id="dateOfExit">--</p>
                        </div>
                        <div class="col-md-6">
                            <strong>Employment Type:</strong>
                            <p id="empType">--</p>
                        </div>
                        <div class="col-md-6">
                            <strong>Status:</strong>
                            <p id="empStatus">--</p>
                        </div>
                        <div class="col-md-6">
                            <strong>Organisation:</strong>
                            <p id="organisation">--</p>
                        </div>
                        <div class="col-md-6">
                            <strong>Department:</strong>
                            <p id="dept">--</p>
                        </div>
                        <div class="col-md-6">
                            <strong>Designation:</strong>
                            <p id="desg">--</p>
                        </div>
                        <div class="col-md-6">
                            <strong>Reporting To:</strong>
                            <p id="reportingTo">--</p>
                        </div>
                        <div class="col-md-6">
                            <strong>Shift:</strong>
                            <p id="shift">--</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Documents Tab -->
        <div class="tab-pane fade" id="documents" role="tabpanel">
            <div class="card">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h6 class="mb-0">Document Management</h6>
                    <button class="btn btn-sm btn-primary" data-bs-toggle="modal" data-bs-target="#uploadDocModal">
                        <i class="fas fa-upload me-1"></i>Upload Document
                    </button>
                </div>
                <div class="card-body">
                    <div id="documentsList">
                        <p class="text-center text-muted">Loading documents...</p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Bank Details Tab -->
        <div class="tab-pane fade" id="bank" role="tabpanel">
            <div class="card">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h6 class="mb-0">Bank Account Details</h6>
                    <button class="btn btn-sm btn-primary" data-bs-toggle="modal" data-bs-target="#addBankModal">
                        <i class="fas fa-plus me-1"></i>Add Bank Account
                    </button>
                </div>
                <div class="card-body">
                    <div id="bankDetailsList">
                        <p class="text-center text-muted">Loading bank details...</p>
                    </div>
                </div>
            </div>
        </div>

    </div>
</div>

<!-- Upload Document Modal -->
<div class="modal fade" id="uploadDocModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <form id="uploadDocForm">
                <div class="modal-header">
                    <h5 class="modal-title">Upload Document</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label">Document Type</label>
                        <select class="form-select" name="documentType" required>
                            <option value="">Select Type</option>
                            <option value="AADHAR">Aadhar Card</option>
                            <option value="PAN">PAN Card</option>
                            <option value="PASSPORT">Passport</option>
                            <option value="DRIVING_LICENSE">Driving License</option>
                            <option value="RESUME">Resume</option>
                            <option value="OFFER_LETTER">Offer Letter</option>
                            <option value="CERTIFICATE">Certificate</option>
                            <option value="OTHER">Other</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Choose File or Provide Link</label>
                        <input type="file" class="form-control mb-2" id="docFile">
                        <input type="url" class="form-control" id="docUrl" placeholder="Or paste external URL">
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Upload</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Add Bank Modal -->
<div class="modal fade" id="addBankModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <form id="addBankForm">
                <div class="modal-header">
                    <h5 class="modal-title">Add Bank Account</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <input type="hidden" name="employeeId" id="bankEmployeeId">
                    <div class="mb-3">
                        <label class="form-label">Bank Name</label>
                        <input type="text" class="form-control" name="bankName" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Account Number</label>
                        <input type="text" class="form-control" name="accountNumber" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">IFSC Code</label>
                        <input type="text" class="form-control" name="ifscCode" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Branch</label>
                        <input type="text" class="form-control" name="branch">
                    </div>
                    <div class="mb-3 form-check">
                        <input type="checkbox" class="form-check-input" name="isPrimaryAccount" id="isPrimary">
                        <label class="form-check-label" for="isPrimary">
                            Set as Primary Account
                        </label>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Save</button>
                </div>
            </form>
        </div>
    </div>
</div>