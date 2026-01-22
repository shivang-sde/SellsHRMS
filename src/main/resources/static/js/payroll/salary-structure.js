/**
 * Salary Structures Module
 * CRUD and component assignments for salary structures
 */

const SalaryStructures = (() => {
    let structuresData = [];
    let componentsData = [];
    let currentStructureId = null;
    let structureModal = null;

    const init = () => {
        structureModal = new bootstrap.Modal(document.getElementById('structureModal'));

        attachEventListeners();
        loadInitialData();
    };

    const attachEventListeners = () => {
        $('#btnAddStructure, #btnAddStructureEmpty').on('click', () => openStructureModal());
        $('#btnSaveStructure').on('click', saveStructure);
        $('#searchStructure').on('input', debounce(applyFilters, 300));
        $('#filterStatus').on('change', applyFilters);
        $('#btnResetFilters').on('click', resetFilters);
        $('#structureModal').on('hidden.bs.modal', resetForm);
    };

    const loadInitialData = async () => {
        showLoading(true);
        try {
            await Promise.all([loadStructures(), loadComponents()]);
        } finally {
            showLoading(false);
        }
    };

    const loadStructures = async () => {
        try {
            const response = await $.ajax({
                url: `${window.APP.CONTEXT_PATH}/api/payroll/salary-structures/organisation/${window.APP.ORG_ID}`,
                method: 'GET',
                data: { orgId: window.APP.ORG_ID }
            });
            console.log("struct...", response)
            structuresData = response.data || response || [];
            renderStructures(structuresData);
        } catch (error) {
            console.error('Error loading structures:', error);
            showToast('error', 'Failed to load salary structures');
        }
    };

    const loadComponents = async () => {
        try {
            const response = await $.ajax({
                url: `${window.APP.CONTEXT_PATH}/api/payroll/salary-components/organisation/${window.APP.ORG_ID}`,
                method: 'GET',
                data: { orgId: window.APP.ORG_ID, active: true }
            });
            console.log("load components", response)
            componentsData = response || [];
            populateComponentSelect();
        } catch (error) {
            console.error('Error loading components:', error);
        }
    };

    const renderStructures = (structures) => {
        const tbody = $('#structuresTableBody');
        tbody.empty();

        if (!structures || structures.length === 0) {
            showEmptyState(true);
            return;
        }

        showEmptyState(false);
        structures.forEach(structure => {
            tbody.append(createStructureRow(structure));
        });
    };

    const createStructureRow = (structure) => {
        const statusClass = structure.active ? 'success' : 'secondary';
        const componentsList = structure.components?.map(c => c.abbreviation).join(', ') || '-';
        const ctcTotal = structure.components?.reduce((sum, c) => sum + (c.includeInCTC ? c.amount || 0 : 0), 0);

        return `
            <tr data-id="${structure.id}">
                <td>${structure.name}</td>
                <td>${componentsList}</td>
                <td class="text-end">₹${formatCurrency(ctcTotal)}</td>
                <td><span class="badge bg-${statusClass}">${structure.active ? 'Active' : 'Inactive'}</span></td>
                <td class="text-end">
                    <div class="btn-group btn-group-sm">
                        <button class="btn btn-outline-primary" onclick="SalaryStructures.editStructure(${structure.id})" title="Edit">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-outline-danger" onclick="SalaryStructures.deactivateStructure(${structure.id})"
                                title="Deactivate" ${!structure.active ? 'disabled' : ''}>
                            <i class="fas fa-ban"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `;
    };

    const populateComponentSelect = () => {
        const select = $('#componentSelect');
        select.empty();
        componentsData.forEach(c => {
            select.append(`<option class="m-2" value="${c.id}">${c.name} (${c.abbreviation})</option>`);
        });
    };

    const openStructureModal = (structure = null) => {
        console.log("Struc", structure)
        resetForm();
        if (structure) {
            currentStructureId = structure.id;
            $('#structureModalTitle').html('<i class="fas fa-edit me-2"></i>Edit Salary Structure');
            populateForm(structure);
        } else {
            currentStructureId = null;
            $('#structureModalTitle').html('<i class="fas fa-plus me-2"></i>New Salary Structure');
        }
        structureModal.show();
    };

    const populateForm = (structure) => {
        $('#structureId').val(structure.id);
        $('#structureName').val(structure.name);
        $('#leaveEncashmentRate').val(structure.leaveEncashmentRate)
        $('#structureDescription').val(structure.description),
        $('#structureFrequency').val(structure.payrollFrequency),
        $('#structureCurrency').val(structure.currency)
        $('#structureIsActive').prop('checked', structure.active);
        if (structure.components) {
            console.log("struc comp", structure.components)
            const selectedIds = structure.components.map(c => c.id.toString());
            $('#componentSelect').val(selectedIds);
        }
    };

    const resetForm = () => {
        $('#structureForm')[0].reset();
        $('#structureId').val('');
        $('#componentSelect').val([]);
        currentStructureId = null;
    };

    const saveStructure = async () => {
        if (!$('#structureForm')[0].checkValidity()) {
            $('#structureForm')[0].reportValidity();
            return;
        }

        const structureData = {
            id: currentStructureId,
            organisationId: window.APP.ORG_ID,
            name: $('#structureName').val().trim(),
            description: $('#structureDescription').val().trim(),
            payrollFrequency: $('#structureFrequency').val(),
            currency: $('#structureCurrency').val().trim(),
            leaveEncashmentRate: parseFloat($('#leaveEncashmentRate').val()) || 0,
            componentIds: $('#componentSelect').val() || [],
            active: $('#structureIsActive').is(':checked')
        };

        const btnSave = $('#btnSaveStructure');
        btnSave.prop('disabled', true).html('<i class="fas fa-spinner fa-spin me-2"></i>Saving...');

        try {
            const method = currentStructureId ? 'PUT' : 'POST';
            const url = `${window.APP.CONTEXT_PATH}/api/payroll/salary-structures${currentStructureId ? '/' + currentStructureId : ''}`;
            await $.ajax({
                url,
                method,
                contentType: 'application/json',
                data: JSON.stringify(structureData)
            });
            showToast('success', `Structure ${currentStructureId ? 'updated' : 'created'} successfully`);
            structureModal.hide();
            loadStructures();
        } catch (error) {
            console.error('Error saving structure:', error);
            showToast('error', error.responseJSON?.message || 'Failed to save structure');
        } finally {
            btnSave.prop('disabled', false).html('<i class="fas fa-save me-2"></i>Save Structure');
        }
    };

    const editStructure = (structureId) => {
        const structure = structuresData.find(s => s.id === structureId);
        if (structure) openStructureModal(structure);
    };

    const deactivateStructure = async (structureId) => {
        console.log("deactivating struc" )
        const structure = structuresData.find(s => s.id === structureId);
        if (!structure) return;
        const confirmed = await window.showConfirmation({
            title: 'Deactivate Structure',
            message: `Are you sure you want to deactivate "${structure.name}"?`,
            confirmText: 'Deactivate',
            confirmClass: 'btn-danger'
        });
        console.log("Confirmation result:", confirmed); // Debug log
    if (!confirmed) return;

        try {
            await $.ajax({
                url: `${window.APP.CONTEXT_PATH}/api/payroll/salary-structures/${structureId}/deactivate`,
                method: 'PATCH'
            });
            window.showToast('success', 'Structure deactivated successfully');
            loadStructures();
        } catch (error) {
            console.error('Error deactivating structure:', error);
            showToast('error', 'Failed to deactivate structure');
        }
    };

    const applyFilters = () => {
        const search = $('#searchStructure').val().toLowerCase();
        const statusFilter = $('#filterStatus').val();

        let filtered = [...structuresData];

        if (search) filtered = filtered.filter(s => s.name.toLowerCase().includes(search));
        if (statusFilter) filtered = filtered.filter(s => (s.active ? 'ACTIVE' : 'INACTIVE') === statusFilter);

        renderStructures(filtered);
    };

    const resetFilters = () => {
        $('#searchStructure').val('');
        $('#filterStatus').val('');
        renderStructures(structuresData);
    };

    const showLoading = (show) => {
        if (show) {
            $('#loadingState').removeClass('d-none');
            $('#structuresTable').closest('.table-responsive').addClass('d-none');
            $('#emptyState').addClass('d-none');
        } else {
            $('#loadingState').addClass('d-none');
            $('#structuresTable').closest('.table-responsive').removeClass('d-none');
        }
    };

    const showEmptyState = (show) => {
        if (show) {
            $('#emptyState').removeClass('d-none');
            $('#structuresTable').closest('.table-responsive').addClass('d-none');
        } else {
            $('#emptyState').addClass('d-none');
            $('#structuresTable').closest('.table-responsive').removeClass('d-none');
        }
    };

    const debounce = (func, wait) => {
        let timeout;
        return function (...args) {
            clearTimeout(timeout);
            timeout = setTimeout(() => func(...args), wait);
        };
    };

    const formatCurrency = (amount) => {
        return new Intl.NumberFormat('en-IN', {minimumFractionDigits: 2, maximumFractionDigits: 2}).format(amount);
    };

    return { init, editStructure, deactivateStructure };
})();

document.addEventListener('DOMContentLoaded', () => SalaryStructures.init());
window.SalaryStructures = SalaryStructures;
