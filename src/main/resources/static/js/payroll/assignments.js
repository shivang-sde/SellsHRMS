/**
 * Employee Salary Assignments Module
 */
const SalaryAssignments = (() => {
    let assignmentsData = [];
    let employeesData = [];
    let structuresData = [];
    let currentAssignmentId = null;
    let assignmentModal = null;
    let viewModal = null;

    const init = () => {
        assignmentModal = new bootstrap.Modal(document.getElementById('assignmentModal'));
        viewModal = new bootstrap.Modal(document.getElementById('viewAssignmentModal'));
        
        attachEventListeners();
        loadInitialData();
    };

    const attachEventListeners = () => {
        $('#btnAddAssignment, #btnAddAssignmentEmpty').on('click', () => openAssignmentModal());
        $('#btnSaveAssignment').on('click', saveAssignment);
        $('#employeeSelect').on('change', handleEmployeeChange);
        $('#structureSelect').on('change', handleStructureChange);
        $('#baseSalary, #variablePay').on('input', calculateTotal);
        $('#filterDepartment, #filterStructure').on('change', applyFilters);
        $('#searchEmployee').on('input', debounce(applyFilters, 300));
        $('#btnResetFilters').on('click', resetFilters);
        $('#assignmentModal').on('hidden.bs.modal', resetForm);
    };

    const loadInitialData = async () => {
        showLoading(true);
        try {
            await Promise.all([
                loadAssignments(),
                loadEmployees(),
                loadStructures()
            ]);
        } finally {
            showLoading(false);
        }
    };

    const loadAssignments = async () => {
        try {
            const response = await $.ajax({
                url: `${window.APP.CONTEXT_PATH}/api/payroll/assignments/organisation/${window.APP.ORG_ID}`,
                method: 'GET',
                data: { orgId: window.APP.ORG_ID }
            });
            assignmentsData = response.data || response || [];
            renderAssignments(assignmentsData);
        } catch (error) {
            console.error('Error loading assignments:', error);
            showToast('error', 'Failed to load salary assignments');
        }
    };

    const loadEmployees = async () => {
        try {
            const response = await $.ajax({
                url: `${window.APP.CONTEXT_PATH}/api/employees/org/${window.APP.ORG_ID}`,
                method: 'GET',
                data: { orgId: window.APP.ORG_ID, status: 'ACTIVE' }
            });
            employeesData = response.data || response || [];
            console.log("Emp data", response)
            populateEmployeeDropdown();
        } catch (error) {
            console.error('Error loading employees:', error);
        }
    };

    const loadStructures = async () => {
        try {
            const response = await $.ajax({
                url: `${window.APP.CONTEXT_PATH}/api/payroll/salary-structures/organisation/${window.APP.ORG_ID}`,
                method: 'GET',
                data: { orgId: window.APP.ORG_ID, active: true }
            });
            structuresData = response.data || response || [];
            populateStructureDropdown();
            populateFilterDropdowns();
        } catch (error) {
            console.error('Error loading structures:', error);
        }
    };

    const renderAssignments = (assignments) => {
        const tbody = $('#assignmentsTableBody');
        tbody.empty();

        if (!assignments || assignments.length === 0) {
            showEmptyState(true);
            return;
        }

        showEmptyState(false);
        assignments.forEach(assignment => {
            tbody.append(createAssignmentRow(assignment));
        });
    };

    const createAssignmentRow = (assignment) => {
        const statusClass = assignment.active ? 'success' : 'secondary';
        return `
            <tr>
                <td>
                    <strong>${assignment.employeeName}</strong>
                    <br><small class="text-muted">${assignment.employeeCode}</small>
                </td>
                <td>${assignment.employeeDepartmentName || '-'}</td>
                <td>${assignment.salaryStructureName}</td>
                <td class="text-end">₹${formatCurrency(assignment.basePay)}</td>
                <td class="text-end">₹${formatCurrency(assignment.variablePay || 0)}</td>
                <td class="text-end"><strong>₹${formatCurrency(assignment.totalCTC)}</strong></td>
                <td>${formatDate(assignment.effectiveFrom)}</td>
                <td><span class="badge bg-${statusClass}">${assignment.active ? 'Active' : 'Inactive'}</span></td>
                <td class="text-end">
                    <div class="btn-group btn-group-sm">
                        <button class="btn btn-outline-primary" onclick="SalaryAssignments.viewAssignment(${assignment.id})" title="View">
                            <i class="fas fa-eye"></i>
                        </button>
                        <button class="btn btn-outline-primary" onclick="SalaryAssignments.editAssignment(${assignment.id})" title="Edit">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-outline-danger" onclick="SalaryAssignments.deactivateAssignment(${assignment.id})" 
                                title="Deactivate" ${!assignment.active ? 'disabled' : ''}>
                            <i class="fas fa-ban"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `;
    };

    // --- New/Missing Functionality ---

    const populateEmployeeDropdown = () => {
        const select = $('#employeeSelect');
        select.find('option:not(:first)').remove();
        employeesData.forEach(emp => {
            select.append(`<option value="${emp.id}">${emp.fullName} (${emp.employeeCode})</option>`);
        });
    };

    const populateStructureDropdown = () => {
        const select = $('#structureSelect, #filterStructure');
        select.find('option:not(:first)').remove();
        structuresData.forEach(s => {
            select.append(`<option value="${s.id}">${s.name}</option>`);
        });
    };

    const populateFilterDropdowns = () => {
        const departments = [...new Set(employeesData.map(e => e.department).filter(Boolean))];
        const deptFilter = $('#filterDepartment');
        deptFilter.find('option:not(:first)').remove();
        departments.forEach(dept => {
            deptFilter.append(`<option value="${dept}">${dept}</option>`);
        });
    };

    const handleEmployeeChange = () => {
        const empId = $('#employeeSelect').val();
        const emp = employeesData.find(e => e.id == empId);
        if (emp) {
            $('#empId').text(emp.employeeCode);
            $('#empDept').text(emp.department || 'N/A');
            $('#empDesig').text(emp.designation || 'N/A');
            $('#employeeInfo').removeClass('d-none');
        } else {
            $('#employeeInfo').addClass('d-none');
        }
    };

    const handleStructureChange = () => {
        const structId = $('#structureSelect').val();
        const struct = structuresData.find(s => s.id == structId);
        if (struct) {
            const names = struct.components?.map(c => c.name).join(', ') || 'No components defined';
            $('#structureComponents').text(names);
            $('#structureInfo').removeClass('d-none');
        } else {
            $('#structureInfo').addClass('d-none');
        }
    };

    const openAssignmentModal = (assignment = null) => {
        resetForm();
        if (assignment) {
            currentAssignmentId = assignment.id;
            $('#assignmentModalTitle').html('<i class="fas fa-edit me-2"></i>Edit Salary Assignment');
            $('#employeeSelect').val(assignment.employeeId).trigger('change').prop('disabled', true);
            $('#structureSelect').val(assignment.salaryStructureId).trigger('change');
            $('#baseSalary').val(assignment.basePay);
            $('#variablePay').val(assignment.variablePay);
            $('#effectiveDate').val(assignment.effectiveFrom);
            $('#effectiveUntil').val(assignment.effectiveTo);
            $('#remarks').val(assignment.remarks);
            $('#assignmentIsActive').prop('checked', assignment.active);
            calculateTotal();
        } else {
            currentAssignmentId = null;
            $('#assignmentModalTitle').html('<i class="fas fa-plus me-2"></i>New Salary Assignment');
            $('#employeeSelect').prop('disabled', false);
        }
        assignmentModal.show();
    };

    const saveAssignment = async () => {
        const form = $('#assignmentForm')[0];
        if (!form.checkValidity()) return form.reportValidity();

        const data = {
            id: currentAssignmentId,
            organisationId: window.APP.ORG_ID,
            employeeId: $('#employeeSelect').val(),
            salaryStructureId: $('#structureSelect').val(),
            basePay: parseFloat($('#baseSalary').val()),
            variablePay: parseFloat($('#variablePay').val()) || 0,
            effectiveFrom: $('#effectiveDate').val(),
            effectiveTo: $('#effectiveUntil').val() || null,
            remarks: $('#remarks').val(),
            active: $('#assignmentIsActive').is(':checked')
        };

        console.log("salary assign data", data)

        try {
            await $.ajax({
                url: `${window.APP.CONTEXT_PATH}/api/payroll/assignments${currentAssignmentId ? '/' + currentAssignmentId : ''}`,
                method: currentAssignmentId ? 'PUT' : 'POST',
                contentType: 'application/json',
                data: JSON.stringify(data)
            });
            showToast('success', 'Assignment saved successfully');
            assignmentModal.hide();
            loadAssignments();
        } catch (error) {
            console.error("error, repsonse", error)
            showToast('error', error.meesage || "error saving salary assignment");
        }
    };

    const viewAssignment = (id) => {
        const data = assignmentsData.find(a => a.id === id);
        if (!data) return;

        const content = `
            <div class="col-md-6"><strong>Employee:</strong><p>${data.employeeName}</p></div>
            <div class="col-md-6"><strong>Structure:</strong><p>${data.salaryStructureName}</p></div>
            <div class="col-md-4"><strong>Base:</strong><p>₹${formatCurrency(data.baseSalary)}</p></div>
            <div class="col-md-4"><strong>Variable:</strong><p>₹${formatCurrency(data.variablePay)}</p></div>
            <div class="col-md-4"><strong>Total CTC:</strong><p class="text-primary fw-bold">₹${formatCurrency(data.totalCTC)}</p></div>
            <div class="col-md-6"><strong>Effective From:</strong><p>${formatDate(data.effectiveFrom)}</p></div>
            <div class="col-md-6"><strong>Status:</strong><p>${data.active ? 'Active' : 'Inactive'}</p></div>
            <div class="col-12"><strong>Remarks:</strong><p>${data.remarks || 'N/A'}</p></div>
        `;
        $('#assignmentDetailsContent').html(content);
        viewModal.show();
    };

    const editAssignment = (id) => {
        const assignment = assignmentsData.find(a => a.id === id);
        if (assignment) openAssignmentModal(assignment);
    };

    const deactivateAssignment = async (id) => {
        const assignment = assignmentsData.find(a => a.id = id)
        if(!assignment) return;

        const confirmed = await window.showConfirmation({
            title: 'Deactivate Salary Assignments',
            message: `Are you sure you want to remove salary structure assigned to the employee?`,
            confirmText: 'Deactivate',
            confirmClass: 'btn-danger'
            }
        )
        console.log("cinfirmation result ", confirmed)
        if(!confirmed) return;
        try {
            await $.ajax({
                url: `${window.APP.CONTEXT_PATH}/api/payroll/assignments/${id}/deactivate`,
                method: 'PATCH'
            });
            showToast('success', 'Assignment deactivated');
            loadAssignments();
        } catch (error) {
            showToast('error', 'Failed to deactivate');
        }
    };

    // --- Logic & Helpers ---

    const calculateTotal = () => {

        // in ctc we have to implement logic in frontend also to calculate same as we are doing in backed such as based on 
        // all those component and their type or if they depend upon base pay.
        //then we'll show the ctc,

        const base = parseFloat($('#baseSalary').val()) || 0;
        const variable = parseFloat($('#variablePay').val()) || 0;
        const total = base + variable;

        $('#displayBase').text('₹' + formatCurrency(base));
        $('#displayVariable').text('₹' + formatCurrency(variable));
        $('#displayTotal').text('₹' + formatCurrency(total));
    };

    const applyFilters = () => {
        const search = $('#searchEmployee').val().toLowerCase();
        const dept = $('#filterDepartment').val();
        const struct = $('#filterStructure').val();

        const filtered = assignmentsData.filter(a => {
            const matchesSearch = a.employeeName.toLowerCase().includes(search) || a.employeeCode.toLowerCase().includes(search);
            const matchesDept = !dept || a.department === dept;
            const matchesStruct = !struct || a.structureId == struct;
            return matchesSearch && matchesDept && matchesStruct;
        });
        renderAssignments(filtered);
    };

    const resetFilters = () => {
        $('#searchEmployee').val('');
        $('#filterDepartment').val('');
        $('#filterStructure').val('');
        renderAssignments(assignmentsData);
    };

    const resetForm = () => {
        $('#assignmentForm')[0].reset();
        $('#assignmentId').val('');
        $('#employeeInfo, #structureInfo').addClass('d-none');
        calculateTotal();
    };

    const formatCurrency = (amount) => {
        return new Intl.NumberFormat('en-IN', {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        }).format(amount);
    };

    const formatDate = (dateString) => {
        if (!dateString) return '-';
        return new Date(dateString).toLocaleDateString('en-IN');
    };

    const showLoading = (show) => {
        $('#loadingState').toggleClass('d-none', !show);
        $('#assignmentsTable').toggleClass('d-none', show);
    };

    const showEmptyState = (show) => {
        $('#emptyState').toggleClass('d-none', !show);
        $('#assignmentsTable').toggleClass('d-none', show);
    };

    const debounce = (func, wait) => {
        let timeout;
        return (...args) => {
            clearTimeout(timeout);
            timeout = setTimeout(() => func(...args), wait);
        };
    };

    // const showToast = (type, message) => {
    //     // Assuming a global showToast exists, or use console as fallback
    //     if (window.showToast) window.showToast(type, message);
    //     else console.log(`${type.toUpperCase()}: ${message}`);
    // };

    return {
        init,
        viewAssignment,
        editAssignment,
        deactivateAssignment
    };
})();

document.addEventListener('DOMContentLoaded', () => {
    SalaryAssignments.init();
});

window.SalaryAssignments = SalaryAssignments;