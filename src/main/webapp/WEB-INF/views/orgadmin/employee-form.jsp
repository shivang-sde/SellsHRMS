<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<input type="hidden" id="employeeId" value="${employeeId}">

<div class="row mb-3">
    <div class="col-12">
        <div class="d-flex justify-content-between align-items-center">
            <h2 class="mb-0">
                <c:choose>
                    <c:when test="${not empty employeeId}">Edit Employee</c:when>
                    <c:otherwise>Create New Employee</c:otherwise>
                </c:choose>
            </h2>
            <a href="/org/employees" class="btn btn-outline-secondary">
                <i class="fas fa-arrow-left me-2"></i>Back to List
            </a>
        </div>
    </div>
</div>

<form id="employeeForm" enctype="multipart/form-data">
    
    <!-- Personal Information -->
    <div class="card mb-3">
        <div class="card-header bg-primary text-white">
            <h5 class="mb-0"><i class="fas fa-user me-2"></i>Personal Information</h5>
        </div>
        <div class="card-body">
            <div class="row g-3">
                <div class="col-md-4">
                    <label class="form-label" for="firstName">First Name <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" id="firstName" name="firstName" required>
                </div>
                <div class="col-md-4">
                    <label class="form-label" for="lastName">Last Name <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" id="lastName" name="lastName" required>
                </div>
                <div class="col-md-4">
                    <label class="form-label" for="dob">Date of Birth</label>
                    <input type="date" class="form-control" id="dob" name="dob">
                </div>
                <div class="col-md-4">
                    <label class="form-label" for="genderSelect">Gender</label>
                    <select class="form-select" id="genderSelect" name="gender">
                        <option value="">Select</option>
                        <option value="MALE">Male</option>
                        <option value="FEMALE">Female</option>
                        <option value="OTHER">Other</option>
                    </select>
                </div>
                <div class="col-md-4">
                    <label class="form-label" for="personalEmail">Personal Email</label>
                    <input type="email" class="form-control" id="personalEmail" name="personalEmail">
                </div>
                <div class="col-md-4">
                    <label class="form-label" for="phone">Phone <span class="text-danger">*</span></label>
                    <input type="tel" class="form-control" id="phone" name="phone" required>
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
                    <input type="text" class="form-control" id="nationality" name="nationality">
                </div>
                <div class="col-md-4">
                    <label class="form-label" for="maritalStatus">Marital Status</label>
                    <select class="form-select" id="maritalStatus" name="maritalStatus">
                        <option value="">Select</option>
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
                    <small class="text-muted">Max 5MB (JPG, PNG)</small>
                </div>
            </div>
        </div>
    </div>

    <!-- Address Information -->
    <div class="card mb-3">
        <div class="card-header bg-primary text-white">
            <h5 class="mb-0"><i class="fas fa-map-marker-alt me-2"></i>Address Information</h5>
        </div>
        <div class="card-body">
            <!-- Local Address -->
            <h6 class="mb-3">Local Address</h6>
            <div class="row g-3 mb-4">
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

            <!-- Permanent Address -->
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h6 class="mb-0">Permanent Address</h6>
                <button type="button" class="btn btn-sm btn-outline-primary" id="copyLocalAddress">
                    <i class="fas fa-copy me-1"></i>Same as Local
                </button>
            </div>
            <div class="row g-3">
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

    <!-- Company Information -->
    <div class="card mb-3">
        <div class="card-header bg-primary text-white">
            <h5 class="mb-0"><i class="fas fa-building me-2"></i>Company Information</h5>
        </div>
        <div class="card-body">
            <div class="row g-3">
                <div class="col-md-4">
                    <label class="form-label" for="employeeCode">Employee Code <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" id="employeeCode" name="employeeCode" required>
                </div>
                <div class="col-md-4">
                    <label class="form-label" for="dateOfJoining">Date of Joining <span class="text-danger">*</span></label>
                    <input type="date" class="form-control" id="dateOfJoining" name="dateOfJoining" required>
                </div>
                <div class="col-md-4">
                    <label class="form-label" for="dateOfExit">Date of Exit</label>
                    <input type="date" class="form-control" id="dateOfExit" name="dateOfExit">
                </div>
                <div class="col-md-4">
                    <label class="form-label" for="employmentType">Employment Type <span class="text-danger">*</span></label>
                    <select class="form-select" id="employmentType" name="employmentType" required>
                        <option value="">Select</option>
                        <option value="FULLTIME">Full Time</option>
                        <option value="PARTTIME">Part Time</option>
                        <option value="CONSULTANT">Consultant</option>
                        <option value="CONTRACT">Contract</option>
                        <option value="INTERN">Intern</option>

                    </select>
                </div>
<div class="col-md-4">
    <label class="form-label" for="status">Status <span class="text-danger">*</span></label>
    <select class="form-select" id="status" name="status" required>
        <option value="">Select</option>
        <option value="ACTIVE">Active</option>
        <option value="INACTIVE">Inactive</option>
        <option value="ONLEAVE">On Leave</option>
        <option value="SUSPENDED">Suspended</option>
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

    <!-- Account Information -->
    <div class="card mb-3">
        <div class="card-header bg-primary text-white">
            <h5 class="mb-0"><i class="fas fa-key me-2"></i>Account Information</h5>
        </div>
        <div class="card-body">
            <div class="row g-3">
                <div class="col-md-6">
                    <label class="form-label" for="workEmail">Work Email <span class="text-danger">*</span></label>
                    <input type="email" class="form-control" id="workEmail" name="workEmail" required>
                </div>
                <div class="col-md-6">
                    <label class="form-label" for="password">Password <span class="text-danger">*</span></label>
                    <input type="password" class="form-control" id="password" name="password" required>
                    <small class="text-muted">Min 8 characters</small>
                </div>
            </div>
        </div>
    </div>


    <!-- Form Actions -->
    <div class="card">
        <div class="card-body">
            <div class="d-flex justify-content-end gap-2">
                <a href="/org/employees" class="btn btn-secondary">Cancel</a>
                <button type="submit" class="btn btn-primary" id="submitBtn">
                    <i class="fas fa-save me-2"></i>
                    <c:choose>
                        <c:when test="${not empty employeeId}">Update Employee</c:when>
                        <c:otherwise>Create Employee</c:otherwise>
                    </c:choose>
                </button>
            </div>
        </div>
    </div>

</form>