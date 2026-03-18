$(document).ready(function () {
    const orgId = window.APP.ORG_ID || $('#globalOrgId').val();
    let employees = [];
    let deleteEmployeeId = null;
    let resetUserId = null;

    // Check permissions
    const isAdmin = window.APP.ROLE === 'ORG_ADMIN';
    const canEdit = isAdmin || window.APP.hasPermission('EMPLOYEE_EDIT');
    const canDelete = isAdmin || window.APP.hasPermission('EMPLOYEE_DELETE');
    const canResetPass = isAdmin || window.APP.hasPermission('USER_ADMIN');

    // Load employees on page load
    loadEmployees();

    // Search button click
    $('#btnSearch').on('click', function () {
        filterEmployees();
    });

    // Enter key in search box
    $('#searchEmployee').on('keypress', function (e) {
        if (e.which === 13) {
            filterEmployees();
        }
    });

    // Delete confirmation
    $('#confirmDelete').on('click', function () {
        if (deleteEmployeeId) {
            deleteEmployee(deleteEmployeeId);
        }
    });

    $(document).on('click', '.btn-reset', function () {
        resetUserId = $(this).data('id');
        $('#resetUserId').val(resetUserId);

        // Create or get modal instance
        const modalEl = document.getElementById('resetPasswordModal');
        const modal = new bootstrap.Modal(modalEl);
        modal.show();

        console.log("Opening Reset Modal for User:", resetUserId);
    });


    $('#confirmReset').on('click', function () {
        const modal = $('#resetPasswordModal');
        const userId = modal.find('#resetUserId').val();
        const newPassword = modal.find('#newPassword').val().trim();

        if (!newPassword) {
            showToast('warning', 'Please enter a new password');
            return;
        }

        $.ajax({
            url: '/api/users/admin-reset-password',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ userId, newPassword }),
            success: function () {
                showToast('success', 'Password reset successfully');
                const modalInstance = bootstrap.Modal.getInstance(document.getElementById('resetPasswordModal'));
                if (modalInstance) modalInstance.hide();
            },
            error: function (xhr) {
                showToast('error', xhr.responseJSON?.message || 'Failed to reset password');
            }
        });
    });


    // Load all employees using permission-aware endpoint
    function loadEmployees() {
        $.ajax({
            url: `/api/employees`,
            method: 'GET',
            success: function (response) {
                employees = response.data || [];
                renderEmployees(employees);
            },
            error: function (xhr) {
                showToast('error', 'Failed to load employees');
                $('#employeeTableBody').html(`
                    <tr>
                        <td colspan="9" class="text-center text-danger">
                            <i class="fas fa-exclamation-triangle me-2"></i>
                            Failed to load employees
                        </td>
                    </tr>
                `);
            }
        });
    }

    // Filter employees
    function filterEmployees() {
        const searchTerm = $('#searchEmployee').val().toLowerCase();
        const statusFilter = $('#filterStatus').val();
        const typeFilter = $('#filterEmploymentType').val();

        let filtered = employees.filter(emp => {
            const matchesSearch = !searchTerm ||
                emp.fullName.toLowerCase().includes(searchTerm) ||
                emp.employeeCode.toLowerCase().includes(searchTerm) ||
                (emp.email && emp.email.toLowerCase().includes(searchTerm));

            const matchesStatus = !statusFilter || emp.status === statusFilter;
            const matchesType = !typeFilter || emp.employmentType === typeFilter;

            return matchesSearch && matchesStatus && matchesType;
        });

        renderEmployees(filtered);
    }

    // Render employees table
    function renderEmployees(data) {
        if (!data || data.length === 0) {
            $('#employeeTableBody').html(`
                <tr>
                    <td colspan="9" class="text-center text-muted">
                        <i class="fas fa-inbox me-2"></i>
                        No employees found
                    </td>
                </tr>
            `);
            return;
        }

        let html = '';
        data.forEach(emp => {
            const statusClass = getStatusClass(emp.status);
            const typeClass = getTypeClass(emp.employmentType);

            html += `
                <tr>
                    <td><strong>${escapeHtml(emp.employeeCode)}</strong></td>
                    <td>${escapeHtml(emp.fullName)}</td>
                    <td>${escapeHtml(emp.email || 'N/A')}</td>
                    <td>${escapeHtml(emp.phone || 'N/A')}</td>
                    <td>${escapeHtml(emp.department || 'N/A')}</td>
                    <td>${escapeHtml(emp.designation || 'N/A')}</td>
                    <td><span class="badge ${typeClass}">${formatText(emp.employmentType)}</span></td>
                    <td><span class="badge ${statusClass}">${formatText(emp.status)}</span></td>
                    <td>
                        <div class="btn-group btn-group-sm">
                            <a href="/org/employee/${emp.id}" class="btn btn-outline-primary" title="View">
                                <i class="fas fa-eye"></i>
                            </a>
                            ${canEdit ? `
                            <a href="/org/employee/edit/${emp.id}" class="btn btn-outline-warning" title="Edit">
                                <i class="fas fa-edit"></i>
                            </a>` : ''}
                            
                            ${canResetPass ? `
                            <button class="btn btn-outline-secondary btn-reset" data-id="${emp.userId}"
                                title="Reset Password">
                                <i class="fas fa-key"></i>
                            </button>` : ''}

                            ${canDelete ? `
                            <button class="btn btn-outline-danger btn-delete" data-id="${emp.id}" title="Delete">
                                <i class="fas fa-trash"></i>
                            </button>` : ''}
                        </div>
                    </td>
                </tr>
            `;
        });

        $('#employeeTableBody').html(html);

        // Attach delete event handlers
        $('.btn-delete').on('click', function () {
            deleteEmployeeId = $(this).data('id');
            const modal = new bootstrap.Modal(document.getElementById('deleteModal'));
            modal.show();
        });
    }

    // Delete employee
    function deleteEmployee(id) {
        $.ajax({
            url: `/api/employees/${id}`,
            method: 'DELETE',
            success: function () {
                showToast('success', 'Employee deleted successfully');
                const modalInstance = bootstrap.Modal.getInstance(document.getElementById('deleteModal'));
                if (modalInstance) modalInstance.hide();
                loadEmployees(); // Reload list
            },
            error: function (xhr) {
                showToast('error', 'Failed to delete employee');
            }
        });
    }

    // Utility functions
    function getStatusClass(status) {
        const classes = {
            'ACTIVE': 'bg-success',
            'INACTIVE': 'bg-secondary',
            'ON_LEAVE': 'bg-warning text-dark',
            'TERMINATED': 'bg-danger'
        };
        return classes[status] || 'bg-secondary';
    }

    function getTypeClass(type) {
        const classes = {
            'FULL_TIME': 'bg-primary',
            'PART_TIME': 'bg-info',
            'CONTRACT': 'bg-warning text-dark',
            'INTERN': 'bg-secondary'
        };
        return classes[type] || 'bg-secondary';
    }

    function formatText(text) {
        if (!text) return 'N/A';
        return text.replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase());
    }

    function escapeHtml(text) {
        if (!text) return '';
        const map = {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#039;'
        };
        return text.replace(/[&<>"']/g, m => map[m]);
    }
});