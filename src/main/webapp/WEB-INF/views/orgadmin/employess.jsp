<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%-- Employee List Content for Org Admin --%>

        <div class="mb-3 d-flex justify-content-between">
            <a href="/orgadmin/create-employee" class="btn btn-success">
                <i class="bi bi-plus-lg"></i> Add New Employee
            </a>
            <h5 id="orgIdDisplay" class="text-secondary align-self-end">Organisation ID: Loading...</h5>
        </div>

        <div class="card shadow-sm">
            <div class="card-header">
                Employees in Your Organisation
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-striped table-hover">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Name</th>
                                <th>Email</th>
                                <th>Phone</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody id="employeesTableBody">
                            <tr>
                                <td colspan="6" class="text-center">Loading employees...</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <script>
            $(document).ready(function () {
                // 1. Get current Org ID (MOCKING for testing - Replace with actual session retrieval)
                // You should use the /api/me endpoint or session attribute for this in production.
                const currentOrgId = 1;

                $("#orgIdDisplay").text("Organisation ID: " + currentOrgId);
                fetchEmployees(currentOrgId);

                function fetchEmployees(orgId) {
                    // API endpoint from EmployeeRestController: GET /api/employees/organisation/{orgId}
                    $.get(`/api/employees/organisation/${orgId}`)
                        .done(function (employees) {
                            let rows = '';
                            if (employees.length === 0) {
                                rows = '<tr><td colspan="6" class="text-center">No employees found.</td></tr>';
                            } else {
                                employees.forEach(function (emp) {
                                    rows += `
                                <tr>
                                    <td>${emp.id}</td>
                                    <td>${emp.firstName} ${emp.lastName}</td>
                                    <td>${emp.email}</td>
                                    <td>${emp.phone || 'N/A'}</td>
                                    <td><span class="badge bg-info">${emp.status}</span></td>
                                    <td>
                                        <button class="btn btn-sm btn-warning edit-employee me-2" data-id="${emp.id}">Edit</button>
                                        <button class="btn btn-sm btn-danger delete-employee" data-id="${emp.id}">Delete</button>
                                    </td>
                                </tr>
                            `;
                                });
                            }
                            $("#employeesTableBody").html(rows);
                        })
                        .fail(function () {
                            $("#employeesTableBody").html('<tr><td colspan="6" class="text-center text-danger">Failed to load employees.</td></tr>');
                        });
                }

                // Delete Handler (Uses OrgAdminRestController /api/org-admin/employee/{id})
                $(document).on('click', '.delete-employee', function () {
                    const empId = $(this).data('id');
                    if (confirm(`Are you sure you want to delete Employee ID: ${empId}?`)) {
                        $.ajax({
                            url: `/api/org-admin/employee/${empId}`,
                            type: 'DELETE',
                            success: function () {
                                alert('Employee deleted successfully!');
                                fetchEmployees(currentOrgId); // Refresh the list
                            },
                            error: function (jqXHR) {
                                alert('Failed to delete employee: ' + (jqXHR.responseJSON || jqXHR.responseText));
                            }
                        });
                    }
                });

                // Edit Handler (Will redirect to a generic form page, possibly with ID)
                $(document).on('click', '.edit-employee', function () {
                    const empId = $(this).data('id');
                    window.location.href = `/orgadmin/edit-employee/${empId}`; // Create this page next!
                });
            });
        </script>