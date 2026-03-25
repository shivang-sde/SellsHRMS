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
        console.log('Loading assignments for orgId:', window.APP.ORG_ID);
        console.log('API URL:', `${window.APP.CONTEXT_PATH}/api/payroll/assignments/organisation/${window.APP.ORG_ID}`);

        showLoading(true);
        try {
            const response = await $.ajax({
                url: `${window.APP.CONTEXT_PATH}/api/payroll/assignments/organisation/${window.APP.ORG_ID}`,
                method: 'GET'
            });
            assignmentsData = response.data || response || [];
            renderAssignments(assignmentsData);
        } catch (error) {
            console.error('Full error object:', error);
            console.error('Error status:', error.status);
            console.error('Error response:', error.responseJSON);
            showToast('error', error.responseJSON?.message || "Error loading salary assignments");
        } finally {
            showLoading(false);
        }
    };

    const loadEmployees = async () => {
        try {
            const response = await $.ajax({
                url: `${window.APP.CONTEXT_PATH}/api/employees/org/${window.APP.ORG_ID}`,
                method: 'GET'
            });
            employeesData = response.data || response || [];
            populateEmployeeDropdown();
        } catch (error) {
            showToast('error', error.responseJSON.message || "error loading employees" + error.status);
        }
    };

    const loadStructures = async () => {
        try {
            const response = await $.ajax({
                url: `${window.APP.CONTEXT_PATH}/api/payroll/salary-structures/organisation/${window.APP.ORG_ID}`,
                method: 'GET'
            });
            structuresData = response.data || response || [];
            populateStructureDropdown();
            populateFilterDropdowns();
        } catch (error) {
            showToast('error', error.responseJSON.message || "error loading structures" + error.status);
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

        console.log(assignment);
        const statusClass = assignment.active ? 'bg-soft-success text-success' : 'bg-soft-secondary text-secondary';

        return `
        <tr>
            <td class="ps-4">
                <div class="d-flex align-items-center">
                    <div class="avatar-sm me-3 bg-light rounded-circle d-flex align-items-center justify-content-center text-primary fw-bold">
                        ${assignment.employeeName ? assignment.employeeName.charAt(0) : '?'}
                    </div>
                    <div>
                        <div class="fw-bold text-dark mb-0">${assignment.employeeName}</div>
                        <div class="small text-muted">${assignment.employeeCode || 'No ID'} • ${assignment.employeeDepartmentName || 'General'}</div>
                    </div>
                </div>
            </td>
            <td>
                <span class="badge bg-light text-dark border-0 fw-medium p-2">${assignment.salaryStructureName}</span>
            </td>
            <td class="text-end fw-medium">
                ₹${formatCurrency(assignment.monthlyNetTarget)}
            </td>
            <td class="text-end">
                <div class="fw-bolder text-primary">₹${formatCurrency(assignment.annualCtc)}</div>
            </td>
            <td>
                <div class="small fw-medium">${formatDate(assignment.effectiveFrom)}</div>
            </td>
            <td>
                <span class="badge rounded-pill ${statusClass}">${assignment.active ? 'Active' : 'Inactive'}</span>
            </td>
            <td class="text-end pe-4">
                <div class="btn-group">
                    <button class="btn btn-link btn-sm text-muted" onclick="SalaryAssignments.viewAssignment(${assignment.id})">
                        <i class="fas fa-eye"></i>
                    </button>
                    <button class="btn btn-link btn-sm text-muted" onclick="SalaryAssignments.editAssignment(${assignment.id})">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="btn btn-link btn-sm text-danger" onclick="SalaryAssignments.deactivateAssignment(${assignment.id})" ${!assignment.active ? 'disabled' : ''}>
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
            console.log("assignment", assignment)
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
        if (!form.checkValidity()) {
            form.reportValidity();
            return;
        }

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

        console.log("Saving assignment data:", data);
        console.log("Current assignment ID:", currentAssignmentId);

        try {
            const url = currentAssignmentId
                ? `${window.APP.CONTEXT_PATH}/api/payroll/assignments/${currentAssignmentId}`
                : `${window.APP.CONTEXT_PATH}/api/payroll/assignments`;

            console.log("Saving to URL:", url);
            console.log("Using method:", currentAssignmentId ? 'PUT' : 'POST');

            await $.ajax({
                url: url,
                method: currentAssignmentId ? 'PUT' : 'POST',
                contentType: 'application/json',
                data: JSON.stringify(data)
            });

            showToast('success', 'Assignment saved successfully');
            assignmentModal.hide();
            await loadAssignments(); // Wait for reload to complete
        } catch (error) {
            console.error('Save error:', error);
            console.error('Error status:', error.status);
            console.error('Error response:', error.responseJSON);
            showToast('error', error.responseJSON?.message || "Error saving salary assignment");
        }
    };

    const viewAssignment = (id) => {
        const data = assignmentsData.find(a => a.id === id);
        if (!data) return;

        let breakdownHtml = '';
        if (data.targetBreakdownJson) {
            try {
                const breakdown = JSON.parse(data.targetBreakdownJson);
                breakdownHtml = `
                    <div class="col-12 mt-4">
                        <div class="card border-0 shadow-sm">
                            <div class="card-header bg-white border-bottom p-4">
                                <h6 class="text-primary fw-bold mb-0 text-uppercase letter-spacing-1"><i class="fas fa-chart-pie me-2"></i>Estimated CTC Breakdown</h6>
                            </div>
                            <div class="card-body p-0">
                                <div class="table-responsive">
                                    <table class="table table-hover align-middle mb-0">
                                        <thead class="bg-light text-muted small text-uppercase">
                                            <tr>
                                                <th class="border-0 ps-4 py-3">Component Name</th>
                                                <th class="text-end border-0 pe-4 py-3">Monthly Amount</th>
                                            </tr>
                                        </thead>
                                        <tbody class="border-top-0">
                                            ${Object.entries(breakdown).map(([key, value]) => `
                                                <tr>
                                                    <td class="ps-4 text-dark fw-medium py-3 border-light">${key}</td>
                                                    <td class="text-end pe-4 fw-bold text-dark py-3 border-light">₹${formatCurrency(value)}</td>
                                                </tr>
                                            `).join('')}
                                        </tbody>
                                        <tfoot class="bg-primary bg-opacity-10 border-top border-primary border-opacity-25">
                                            <tr>
                                                <td class="ps-4 py-4 fw-bolder text-dark text-uppercase small">Total Annual Cost To Company (CTC)</td>
                                                <td class="text-end pe-4 py-4 fw-bolder text-primary fs-5">₹${formatCurrency(data.annualCtc)}</td>
                                            </tr>
                                        </tfoot>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                `;
            } catch (e) {
                console.error("Error parsing breakdown json", e);
            }
        }

        const content = `
            <div class="col-12 mb-1">
                <div class="d-flex flex-column flex-sm-row align-items-sm-center justify-content-between mb-3 bg-white p-3 rounded shadow-sm border border-light">
                    <div class="d-flex align-items-center mb-3 mb-sm-0">
                        <div class="avatar-md me-3 bg-primary bg-opacity-10 text-primary rounded-circle d-flex align-items-center justify-content-center fw-bold fs-4" style="width: 50px; height: 50px;">
                            ${data.employeeName ? data.employeeName.charAt(0) : '?'}
                        </div>
                        <div>
                            <h5 class="mb-0 fw-bold text-dark">${data.employeeName}</h5>
                            <div class="text-muted small mt-1">
                                <i class="fas fa-id-badge me-1"></i>${data.employeeCode || 'N/A'} <span class="mx-1">&bull;</span> <i class="fas fa-sitemap me-1"></i>${data.employeeDepartmentName || 'General'}
                            </div>
                        </div>
                    </div>
                    <div>
                        ${data.active 
                            ? '<span class="badge bg-success bg-opacity-10 text-success border border-success border-opacity-25 px-3 py-2 rounded-pill"><i class="fas fa-check-circle me-1"></i>Active Assignment</span>' 
                            : '<span class="badge bg-secondary bg-opacity-10 text-secondary border border-secondary border-opacity-25 px-3 py-2 rounded-pill"><i class="fas fa-ban me-1"></i>Inactive</span>'}
                    </div>
                </div>
            </div>

            <div class="col-12">
                <div class="row g-3">
                    <div class="col-sm-6 col-md-3">
                        <div class="bg-light p-3 rounded h-100 border border-light">
                            <small class="text-muted text-uppercase fw-bold" style="font-size: 0.70rem;"><i class="fas fa-layer-group me-1"></i>Structure</small>
                            <div class="fw-bold text-dark mt-1 fs-6 text-truncate" title="${data.salaryStructureName}">${data.salaryStructureName}</div>
                        </div>
                    </div>
                    <div class="col-sm-6 col-md-3">
                        <div class="bg-light p-3 rounded h-100 border border-light">
                            <small class="text-muted text-uppercase fw-bold" style="font-size: 0.70rem;"><i class="fas fa-money-bill-wave me-1"></i>Base Pay</small>
                            <div class="fw-bold text-dark mt-1 fs-6">₹${formatCurrency(data.basePay)}</div>
                        </div>
                    </div>
                    <div class="col-sm-6 col-md-3">
                        <div class="bg-light p-3 rounded h-100 border border-light">
                            <small class="text-muted text-uppercase fw-bold" style="font-size: 0.70rem;"><i class="fas fa-chart-line me-1"></i>Variable Pay</small>
                            <div class="fw-bold text-dark mt-1 fs-6">₹${formatCurrency(data.variablePay || 0)}</div>
                        </div>
                    </div>
                    <div class="col-sm-6 col-md-3">
                        <div class="bg-light p-3 rounded h-100 border border-light">
                            <small class="text-muted text-uppercase fw-bold" style="font-size: 0.70rem;"><i class="fas fa-calendar-check me-1"></i>Effective</small>
                            <div class="fw-bold text-dark mt-1 fs-6">${formatDate(data.effectiveFrom)}</div>
                        </div>
                    </div>
                </div>
            </div>

            ${data.remarks ? `
            <div class="col-12 mt-3">
                <div class="bg-warning bg-opacity-10 border-start border-warning border-4 p-3 rounded">
                    <small class="text-warning text-uppercase fw-bold align-items-center d-flex" style="font-size: 0.75rem;"><i class="fas fa-comment-dots me-2"></i>Remarks</small>
                    <p class="mb-0 text-dark mt-1 small">${data.remarks}</p>
                </div>
            </div>
            ` : ''}

            ${breakdownHtml}
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
        if (!assignment) return;

        const confirmed = await window.showConfirmation({
            title: 'Deactivate Salary Assignments',
            message: `Are you sure you want to remove salary structure assigned to the employee?`,
            confirmText: 'Deactivate',
            confirmClass: 'btn-danger'
        }
        )
        console.log("cinfirmation result ", confirmed)
        if (!confirmed) return;
        try {
            await $.ajax({
                url: `${window.APP.CONTEXT_PATH}/api/payroll/assignments/${id}/deactivate`,
                method: 'PATCH'
            });
            showToast('success', 'Assignment deactivated');
            loadAssignments();
        } catch (error) {
            showToast('error', error.responseJSON.message || "error deactivating salary assignment");
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