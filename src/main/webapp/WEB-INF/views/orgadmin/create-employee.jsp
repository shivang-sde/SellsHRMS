<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%-- Create Employee Content for Org Admin --%>

        <div class="row">
            <div class="col-lg-10">
                <div class="card shadow-sm">
                    <div class="card-header">
                        Employee Details
                    </div>
                    <div class="card-body">
                        <form id="createEmployeeForm">
                            <h5>1. Basic Information & Login</h5>
                            <div class="row">
                                <div class="col-md-4 mb-3">
                                    <label for="firstName" class="form-label">First Name</label>
                                    <input type="text" class="form-control" id="firstName" required>
                                </div>
                                <div class="col-md-4 mb-3">
                                    <label for="lastName" class="form-label">Last Name</label>
                                    <input type="text" class="form-control" id="lastName" required>
                                </div>
                                <div class="col-md-4 mb-3">
                                    <label for="gender" class="form-label">Gender</label>
                                    <select class="form-select" id="gender" required>
                                        <option value="MALE">Male</option>
                                        <option value="FEMALE">Female</option>
                                        <option value="OTHER">Other</option>
                                    </select>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-md-4 mb-3">
                                    <label for="email" class="form-label">Email (Login)</label>
                                    <input type="email" class="form-control" id="email" required>
                                </div>
                                <div class="col-md-4 mb-3">
                                    <label for="password" class="form-label">Password</label>
                                    <input type="password" class="form-control" id="password" required>
                                </div>
                                <div class="col-md-4 mb-3">
                                    <label for="phone" class="form-label">Phone</label>
                                    <input type="tel" class="form-control" id="phone">
                                </div>
                            </div>

                            <hr />

                            <h5>2. Employment & Salary</h5>
                            <div class="row">
                                <div class="col-md-4 mb-3">
                                    <label for="dateOfJoining" class="form-label">Date of Joining</label>
                                    <input type="date" class="form-control" id="dateOfJoining" required>
                                </div>
                                <div class="col-md-4 mb-3">
                                    <label for="employmentType" class="form-label">Employment Type</label>
                                    <select class="form-select" id="employmentType" required>
                                        <option value="FULL_TIME">Full Time</option>
                                        <option value="PART_TIME">Part Time</option>
                                        <option value="CONTRACT">Contract</option>
                                    </select>
                                </div>
                                <div class="col-md-4 mb-3">
                                    <label for="status" class="form-label">Status</label>
                                    <select class="form-select" id="status" required>
                                        <option value="ACTIVE">Active</option>
                                        <option value="ON_LEAVE">On Leave</option>
                                        <option value="TERMINATED">Terminated</option>
                                    </select>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-md-4 mb-3">
                                    <label for="salary" class="form-label">Annual Salary (Double)</label>
                                    <input type="number" step="0.01" class="form-control" id="salary">
                                </div>
                                <div class="col-md-4 mb-3">
                                    <label for="departmentId" class="form-label">Department ID</label>
                                    <input type="number" class="form-control" id="departmentId" placeholder="e.g., 1">
                                    <div class="form-text">Needs a valid Department ID.</div>
                                </div>
                                <div class="col-md-4 mb-3">
                                    <label for="designationId" class="form-label">Designation ID</label>
                                    <input type="number" class="form-control" id="designationId" placeholder="e.g., 1">
                                    <div class="form-text">Needs a valid Designation ID.</div>
                                </div>
                                <div class="col-md-4 mb-3">
                                    <label for="managerId" class="form-label">Manager ID (Optional)</label>
                                    <input type="number" class="form-control" id="managerId" placeholder="e.g., 1">
                                </div>
                            </div>

                            <hr />

                            <h5>3. Personal & Address Details</h5>
                            <div class="row">
                                <div class="col-md-4 mb-3">
                                    <label for="dob" class="form-label">Date of Birth</label>
                                    <input type="date" class="form-control" id="dob">
                                </div>
                                <div class="col-md-8 mb-3">
                                    <label for="address" class="form-label">Street Address</label>
                                    <input type="text" class="form-control" id="address">
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-md-3 mb-3">
                                    <label for="city" class="form-label">City</label>
                                    <input type="text" class="form-control" id="city">
                                </div>
                                <div class="col-md-3 mb-3">
                                    <label for="state" class="form-label">State</label>
                                    <input type="text" class="form-control" id="state">
                                </div>
                                <div class="col-md-3 mb-3">
                                    <label for="country" class="form-label">Country</label>
                                    <input type="text" class="form-control" id="country">
                                </div>
                                <div class="col-md-3 mb-3">
                                    <label for="pincode" class="form-label">Pincode</label>
                                    <input type="text" class="form-control" id="pincode">
                                </div>
                            </div>

                            <div class="d-grid mt-4">
                                <button type="submit" class="btn btn-primary btn-lg">Create Employee</button>
                            </div>
                            <div id="createEmployeeMessage" class="mt-3"></div>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <script>
            $(document).ready(function () {
                // 1. Get current Org ID (MOCKING for testing)
                const currentOrgId = 1;

                $("#createEmployeeForm").submit(function (e) {
                    e.preventDefault();
                    const messageDiv = $("#createEmployeeMessage");
                    messageDiv.empty();

                    // Populate DTO fields
                    const employeeData = {
                        // Credentials & Basic
                        firstName: $("#firstName").val(),
                        lastName: $("#lastName").val(),
                        email: $("#email").val(),
                        password: $("#password").val(),
                        phone: $("#phone").val(),
                        gender: $("#gender").val(),
                        dob: $("#dob").val() || null,
                        dateOfJoining: $("#dateOfJoining").val(),

                        // Employment
                        salary: parseFloat($("#salary").val()) || 0.0,
                        employmentType: $("#employmentType").val(),
                        status: $("#status").val(),

                        // Address
                        address: $("#address").val(),
                        city: $("#city").val(),
                        state: $("#state").val(),
                        country: $("#country").val(),
                        pincode: $("#pincode").val(),

                        // FKs (Set null if empty)
                        departmentId: $("#departmentId").val() ? parseInt($("#departmentId").val()) : null,
                        designationId: $("#designationId").val() ? parseInt($("#designationId").val()) : null,
                        managerId: $("#managerId").val() ? parseInt($("#managerId").val()) : null,
                        organisationId: currentOrgId // Auto-assign to the Org Admin's organisation
                    };

                    // API endpoint from OrgAdminRestController: POST /api/org-admin/employee
                    $.ajax({
                        type: "POST",
                        url: "/api/org-admin/employee",
                        contentType: "application/json",
                        data: JSON.stringify(employeeData),
                        success: function (response) {
                            messageDiv.html('<div class="alert alert-success">Employee created successfully! ID: ' + response.id + '</div>');
                            $("#createEmployeeForm")[0].reset();
                            // Optionally, redirect to the list page
                            // window.location.href = "/orgadmin/employees";
                        },
                        error: function (jqXHR, textStatus, errorThrown) {
                            const errorMsg = jqXHR.responseJSON ? (jqXHR.responseJSON.error || JSON.stringify(jqXHR.responseJSON)) : "An unknown error occurred.";
                            messageDiv.html('<div class="alert alert-danger">Creation Failed: ' + errorMsg + '</div>');
                        }
                    });
                });
            });
        </script>