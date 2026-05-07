<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet" href="/css/employee/employee-form.css">

<div class="employee-form-container">
    <input type="hidden" id="employeeId" value="${employeeId}">

    <!-- Page Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="mb-1">
                <c:choose>
                    <c:when test="${not empty employeeId}">Edit Employee</c:when>
                    <c:otherwise>Create New Employee</c:otherwise>
                </c:choose>
            </h2>
            <p class="text-muted mb-0">Complete the details below to <c:choose><c:when test="${not empty employeeId}">update the</c:when><c:otherwise>onboard a new</c:otherwise></c:choose> team member.</p>
        </div>
        <a href="#" onclick="history.back();" class="btn btn-outline-secondary btn-sm">
            <i class="fas fa-arrow-left me-2"></i>Back to List
        </a>
    </div>

    <form id="employeeForm" enctype="multipart/form-data">

        <!-- Personal Information Section -->
        <div class="card">
            <div class="card-header">
                <div class="header-title">
                    <i class="fas fa-user"></i>
                    <h5>Personal Information</h5>
                </div>
            </div>
            <div class="card-body">
                <div class="row g-compact">
                    <div class="col-md-4">
                        <label class="form-label" for="firstName">First Name <span class="required-dot">*</span></label>
                        <input type="text" class="form-control" id="firstName" name="firstName" required placeholder="John">
                    </div>
                    <div class="col-md-4">
                        <label class="form-label" for="lastName">Last Name <span class="required-dot">*</span></label>
                        <input type="text" class="form-control" id="lastName" name="lastName" required placeholder="Doe">
                    </div>
                    <div class="col-md-4">
                        <label class="form-label" for="dob">Date of Birth</label>
                        <input type="date" class="form-control" id="dob" name="dob">
                    </div>
                    <div class="col-md-4">
                        <label class="form-label" for="genderSelect">Gender <span class="required-dot">*</span></label>
                        <select class="form-select" id="genderSelect" name="gender" required>
                            <option value="">Select Gender</option>
                            <option value="MALE">Male</option>
                            <option value="FEMALE">Female</option>
                            <option value="OTHER">Other</option>
                        </select>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label" for="personalEmail">Personal Email</label>
                        <input type="email" class="form-control" id="personalEmail" name="personalEmail" placeholder="john.doe@example.com">
                    </div>
                    <div class="col-md-4">
                        <label class="form-label" for="phone">Phone <span class="required-dot">*</span></label>
                        <input type="tel" class="form-control" id="phone" name="phone" required placeholder="+91 00000 00000">
                    </div>
                    <div class="col-md-4">
                        <label class="form-label" for="alternatePhone">Alternate Phone</label>
                        <input type="tel" class="form-control" id="alternatePhone" name="alternatePhone">
                    </div>
                    <div class="col-md-4">
                        <label class="form-label" for="fatherName">Father's Name</label>
                        <input type="text" class="form-control" id="fatherName" name="fatherName">
                    </div>
                    <div class="col-md-4">
                        <label class="form-label" for="nationality">Nationality</label>
                        <input type="text" class="form-control" id="nationality" name="nationality" placeholder="Indian">
                    </div>
                    <div class="col-md-4">
                        <label class="form-label" for="maritalStatus">Marital Status <span class="required-dot">*</span></label>
                        <select class="form-select" id="maritalStatus" name="maritalStatus" required>
                            <option value="">Select Status</option>
                            <option value="SINGLE">Single</option>
                            <option value="MARRIED">Married</option>
                            <option value="DIVORCED">Divorced</option>
                            <option value="WIDOWED">Widowed</option>
                        </select>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label" for="referenceName">Reference Name</label>
                        <input type="text" class="form-control" id="referenceName" name="referenceName">
                    </div>
                    <div class="col-md-4">
                        <label class="form-label" for="referencePhone">Reference Phone</label>
                        <input type="tel" class="form-control" id="referencePhone" name="referencePhone">
                    </div>
                    <div class="col-md-4">
                        <label class="form-label" for="photoInput">Profile Photo</label>
                        <input type="file" class="form-control" id="photoInput" name="photo" accept="image/*">
                        <div class="form-text text-muted">Max 5MB (JPG, PNG)</div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Identity Section -->
        <div class="card">
            <div class="card-header">
                <div class="header-title">
                    <i class="fas fa-id-card"></i>
                    <h5>Identity Documents</h5>
                </div>
            </div>
            <div class="card-body">
                <div class="row g-compact">
                    <div class="col-md-4">
                        <label class="form-label" for="aadharNumber">Aadhar Number</label>
                        <input type="text" class="form-control" id="aadharNumber" name="aadharNumber" required placeholder="0000 0000 0000">
                    </div>
                    <div class="col-md-4">
                        <label class="form-label" for="panNumber">PAN Number</label>
                        <input type="text" class="form-control" id="panNumber" name="panNumber" placeholder="ABCDE1234F">
                    </div>
                    <div class="col-md-4">
                        <label class="form-label" for="uanNumber">UAN Number</label>
                        <input type="text" class="form-control" id="uanNumber" name="uanNumber" placeholder="100000000000">
                    </div>
                </div>
            </div>
        </div>

        <!-- Address Section -->
        <div class="card">
            <div class="card-header">
                <div class="header-title">
                    <i class="fas fa-map-marker-alt"></i>
                    <h5>Address Details</h5>
                </div>
            </div>
            <div class="card-body">
                <div class="form-group-title">Local Address</div>
                <div class="row g-compact mb-3">
                    <div class="col-md-6">
                        <label class="form-label" for="localAddressLine1">Address Line 1</label>
                        <input type="text" class="form-control" id="localAddressLine1" name="localAddress.addressLine1">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label" for="localAddressLine2">Address Line 2</label>
                        <input type="text" class="form-control" id="localAddressLine2" name="localAddress.addressLine2">
                    </div>
                    <div class="col-md-3">
                        <label class="form-label" for="localCity">City</label>
                        <input type="text" class="form-control" id="localCity" name="localAddress.city">
                    </div>
                    <div class="col-md-3">
                        <label class="form-label" for="localState">State</label>
                        <input type="text" class="form-control" id="localState" name="localAddress.state">
                    </div>
                    <div class="col-md-3">
                        <label class="form-label" for="localCountry">Country</label>
                        <input type="text" class="form-control" id="localCountry" name="localAddress.country">
                    </div>
                    <div class="col-md-3">
                        <label class="form-label" for="localPincode">Pincode</label>
                        <input type="text" class="form-control" id="localPincode" name="localAddress.pincode">
                    </div>
                </div>

                <div class="d-flex justify-content-between align-items-center mb-2">
                    <div class="form-group-title mb-0 border-0">Permanent Address</div>
                    <button type="button" class="btn btn-sm btn-outline-secondary py-0" id="copyLocalAddress" style="font-size: 0.7rem;">
                        <i class="fas fa-copy me-1"></i>Same as Local
                    </button>
                </div>
                <div class="row g-compact">
                    <div class="col-md-6">
                        <label class="form-label" for="permAddressLine1">Address Line 1</label>
                        <input type="text" class="form-control" id="permAddressLine1" name="permanentAddress.addressLine1">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label" for="permAddressLine2">Address Line 2</label>
                        <input type="text" class="form-control" id="permAddressLine2" name="permanentAddress.addressLine2">
                    </div>
                    <div class="col-md-3">
                        <label class="form-label" for="permCity">City</label>
                        <input type="text" class="form-control" id="permCity" name="permanentAddress.city">
                    </div>
                    <div class="col-md-3">
                        <label class="form-label" for="permState">State</label>
                        <input type="text" class="form-control" id="permState" name="permanentAddress.state">
                    </div>
                    <div class="col-md-3">
                        <label class="form-label" for="permCountry">Country</label>
                        <input type="text" class="form-control" id="permCountry" name="permanentAddress.country">
                    </div>
                    <div class="col-md-3">
                        <label class="form-label" for="permPincode">Pincode</label>
                        <input type="text" class="form-control" id="permPincode" name="permanentAddress.pincode">
                    </div>
                </div>
            </div>
        </div>

        <!-- Employment Section -->
        <div class="card">
            <div class="card-header">
                <div class="header-title">
                    <i class="fas fa-building"></i>
                    <h5>Employment Details</h5>
                </div>
            </div>
            <div class="card-body">
                <div class="row g-compact">
                    <div class="col-md-4">
                        <label class="form-label" for="dateOfJoining">Date of Joining <span class="required-dot">*</span></label>
                        <input type="date" class="form-control" id="dateOfJoining" name="dateOfJoining" required>
                    </div>
                    <div class="col-md-4" id="doeId">
                        <label class="form-label" for="dateOfExit">Date of Exit</label>
                        <input type="date" class="form-control" id="dateOfExit" name="dateOfExit">
                    </div>
                    <div class="col-md-4">
                        <label class="form-label" for="employmentType">Employment Type <span class="required-dot">*</span></label>
                        <select class="form-select" id="employmentType" name="employmentType" required>
                            <option value="">Select Type</option>
                            <option value="FULLTIME">Full Time</option>
                            <option value="PARTTIME">Part Time</option>
                            <option value="CONSULTANT">Consultant</option>
                            <option value="CONTRACT">Contract</option>
                            <option value="INTERN">Intern</option>
                        </select>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label" for="status">Status <span class="required-dot">*</span></label>
                        <select class="form-select" id="status" name="status" required>
                            <option value="">Select Status</option>
                            <option value="ACTIVE">Active</option>
                            <option value="INACTIVE">Inactive</option>
                            <option id="exitStatusVal" value="EXIT">Exit</option>
                            <option value="TERMINATED">Terminated</option>
                        </select>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label" for="departmentSelect">Department</label>
                        <select class="form-select" name="departmentId" id="departmentSelect">
                            <option value="">Select Department</option>
                        </select>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label" for="designationSelect">Designation</label>
                        <select class="form-select" name="designationId" id="designationSelect">
                            <option value="">Select Designation</option>
                        </select>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label" for="reportingToSelect">Reporting To</label>
                        <select class="form-select" name="reportingToId" id="reportingToSelect">
                            <option value="">Select Manager</option>
                        </select>
                    </div>
                </div>
            </div>
        </div>

        <!-- Account Section -->
        <div class="card">
            <div class="card-header">
                <div class="header-title">
                    <i class="fas fa-key"></i>
                    <h5>Account Access</h5>
                </div>
            </div>
            <div class="card-body">
                <div class="row g-compact">
                    <div class="col-md-6">
                        <label class="form-label" for="workEmail">Work Email <span class="required-dot">*</span></label>
                        <input type="email" class="form-control" id="workEmail" name="workEmail" required placeholder="john.doe@company.com">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label" for="password">Password <span class="required-dot">*</span></label>
                        <input type="password" class="form-control" id="password" name="password" required placeholder="••••••••">
                    </div>
                </div>
            </div>
        </div>

        <!-- Sticky Form Actions -->
        <div class="form-actions-bar">
            <a href="/org/employees" class="btn btn-outline-secondary btn-sm">Discard Changes</a>
            <button type="submit" class="btn btn-primary btn-sm" id="submitBtn">
                <i class="fas fa-save me-2"></i>
                <c:choose>
                    <c:when test="${not empty employeeId}">Update Profile</c:when>
                    <c:otherwise>Confirm & Create</c:otherwise>
                </c:choose>
            </button>
        </div>

    </form>
</div>
