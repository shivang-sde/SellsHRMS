/**
 * Salary Components Management Module
 * Handles CRUD operations for salary components
 */

const SalaryComponents = (() => {

    let componentsData = [];
    let currentComponentId = null;
    let componentModal = null;

    const init = () => {
        componentModal = new bootstrap.Modal(document.getElementById('componentModal'));
        
        attachEventListeners();
        loadComponents();
    };

    const attachEventListeners = () => {
        // Add component buttons
        $('#btnAddComponent, #btnAddComponentEmpty').on('click', () => openComponentModal());
        
        // Save component
        $('#btnSaveComponent').on('click', saveComponent);

        $('#componentName').on('input', debounce(function(){
             let name = $(this).val().trim();
             let words = name.split(/\s+/);
             let abbr = words.map(word => word.charAt(0).toUpperCase()).join('');
             $('#componentAbbr').val(abbr);
        }, 300))
        
        // Filters
        $('#filterType, #filterCalcType').on('change', applyFilters);
        $('#searchComponent').on('input', debounce(applyFilters, 300));
        $('#btnResetFilters').on('click', resetFilters);
        
        // Calculation type change
        $('#calculationType').on('change', handleCalculationTypeChange);
        
        // Form reset on modal close
        $('#componentModal').on('hidden.bs.modal', resetForm);
    };

    const loadComponents = async () => {
        showLoading(true);
        
        try {
            const response = await $.ajax({
                url: `${window.APP.CONTEXT_PATH}/api/payroll/salary-components/organisation/${window.APP.ORG_ID}`,
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                },
                data: { orgId: window.APP.ORG_ID }
            });

            componentsData = response.data || response || [];
            renderComponents(componentsData);
            window.showToast('success', 'Components loaded successfully');
        } catch (error) {
            console.error('Error loading components:', error);
            window.showToast('error', 'Failed to load salary components');
            showEmptyState(true);
        } finally {
            showLoading(false);
        }
    };

    const renderComponents = (components) => {
        const tbody = $('#componentsTableBody');
        tbody.empty();

        if (!components || components.length === 0) {
            showEmptyState(true);
            return;
        }

        showEmptyState(false);

        components.forEach(component => {
            const row = createComponentRow(component);
            tbody.append(row);
        });
    };

    const createComponentRow = (component) => {
        const typeClass = component.type === 'EARNING' ? 'success' : 'danger';
        const calcTypeLabel = getCalculationTypeLabel(component.calculationType);
        
        return `
            <tr data-component-id="${component.id}">  
                <td>
                    <strong>${component.name}</strong>
                    ${component.description ? `<br><small class="text-muted">${component.description}</small>` : ''}
                </td>
                <td><code class="text-primary">${component.abbreviation}</code></td>
                <td><span class="badge bg-${typeClass}">${component.type}</span></td>
                <td>${calcTypeLabel}</td>
                <td class="text-center">
                    ${component.taxable ? '<i class="fas fa-check-circle text-success"></i>' : '<i class="fas fa-times-circle text-muted"></i>'}
                </td>
                <td class="text-center">
                    ${component.includeInCTC ? '<i class="fas fa-check-circle text-success"></i>' : '<i class="fas fa-times-circle text-muted"></i>'}
                </td>
                <td class="text-center">
                    ${component.dependsOnPaymentDays ? '<i class="fas fa-check-circle text-success"></i>' : '<i class="fas fa-times-circle text-muted"></i>'}
                </td>
                <td>
                    ${component.active ? '<span class="badge bg-success">Active</span>' : '<span class="badge bg-secondary">Inactive</span>'}
                </td>
                <td class="text-end">
                    <div class="btn-group btn-group-sm">
                        <button class="btn btn-outline-primary" onclick="SalaryComponents.editComponent(${component.id})" 
                                title="Edit">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-outline-danger" onclick="SalaryComponents.deactivateComponent(${component.id})"
                                title="Deactivate" ${!component.active ? 'disabled' : ''}>
                            <i class="fas fa-ban"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `;
    };

    const getCalculationTypeLabel = (type) => {
        const labels = {
            'FIXED': '<span class="badge bg-info">Fixed</span>',
            'PERCENTAGE': '<span class="badge bg-warning">Percentage</span>',
            'FORMULA': '<span class="badge bg-primary">Formula</span>'
        };
        return labels[type] || type;
    };

    const openComponentModal = (component = null) => {
        resetForm();
        
        if (component) {
            currentComponentId = component.id;
            $('#componentModalTitle').html('<i class="fas fa-edit me-2"></i>Edit Salary Component');
            populateForm(component);
        } else {
            currentComponentId = null;
            $('#componentModalTitle').html('<i class="fas fa-plus me-2"></i>Add Salary Component');
        }
        
        componentModal.show();
    };

    const populateForm = (component) => {
        $('#componentId').val(component.id);
        $('#componentName').val(component.name);
        $('#componentAbbr').val(component.abbreviation);
        $('#componentType').val(component.type);
        $('#calculationType').val(component.calculationType);
        $('#condition').val(component.condition);
        $('#fixedAmount').val(component.fixedAmount);
        $('#componentFormula').val(component.formula || '');
        $('#componentDescription').val(component.description || '');
        $('#isTaxable').prop('checked', component.taxable);
        $('#includeInCTC').prop('checked', component.includeInCTC);
        $('#roundToNearest').prop('checked', component.roundToNearest);
        $('#dependsOnPaymentDays').prop('checked', component.dependsOnPaymentDays);
        $('#considerForPT').prop('checked', component.considerForPT);
        $('#isActive').prop('checked', component.active);
        
        handleCalculationTypeChange();
    };

    const resetForm = () => {
        $('#componentForm')[0].reset();
        $('#componentId').val('');
        currentComponentId = null;
        $('#formulaSection').addClass('d-none');
    };

    const handleCalculationTypeChange = () => {
        const calcType = $('#calculationType').val();
        if (calcType === 'FORMULA') {
            $('#formulaSection').removeClass('d-none');
            $('#fixedAmountSection').addClass('d-none')
        } else if(calcType === 'FIXED') {
            $('#fixedAmountSection').removeClass('d-none')
            $('#formulaSection').addClass('d-none');
        } else {
            $('#formulaSection').addClass('d-none');
        }
    };

    const saveComponent = async () => {
        if (!$('#componentForm')[0].checkValidity()) {
            $('#componentForm')[0].reportValidity();
            return;
        }

        const componentData = {
            id: currentComponentId,
            orgId: window.APP.ORG_ID,
            name: $('#componentName').val().trim(),
            abbreviation: $('#componentAbbr').val().trim().toUpperCase(),
            type: $('#componentType').val(),
            fixedAmount: $('#fixedAmount').val(),
            calculationType: $('#calculationType').val(),
            formula: $('#calculationType').val() === 'FORMULA' ? $('#componentFormula').val().trim() : null,
            condition: $('#condition').val(),
            description: $('#componentDescription').val().trim(),
            taxable: $('#isTaxable').is(':checked'),
            includeInCTC: $('#includeInCTC').is(':checked'),
            dependsOnPaymentDays: $('#dependsOnPaymentDays').is(':checked'),
            roundToNearest: $('#roundToNearest').is(':checked'),
            considerForPT: $('#considerForPT').is(':checked'),
            active: $('#isActive').is(':checked')
        };

        const btnSave = $('#btnSaveComponent');
        btnSave.prop('disabled', true).html('<i class="fas fa-spinner fa-spin me-2"></i>Saving...');

        try {
            const method = currentComponentId ? 'PUT' : 'POST';
            const url = `${window.APP.CONTEXT_PATH}/api/payroll/salary-components${currentComponentId ? '/' + currentComponentId : ''}`;

            await $.ajax({
                url: url,
                method: method,
                contentType: 'application/json',
                data: JSON.stringify(componentData)
            });

            window.showToast('success', `Component ${currentComponentId ? 'updated' : 'created'} successfully`);
            componentModal.hide();
            loadComponents();
        } catch (error) {
            console.error('Error saving component:', error);
            const errorMsg = error.responseJSON?.message || 'Failed to save component';
            window.showToast('error', errorMsg);
        } finally {
            btnSave.prop('disabled', false).html('<i class="fas fa-save me-2"></i>Save Component');
        }
    };

    const editComponent = (componentId) => {
        const component = componentsData.find(c => c.id === componentId);
        if (component) {
            openComponentModal(component);
        }
    };

    const deactivateComponent = async (componentId) => {
        const component = componentsData.find(c => c.id === componentId);
        
        const confirmed = await window.showConfirmation({
            title: 'Deactivate Component',
            message: `Are you sure you want to deactivate "${component.name}"? This component will no longer be available for new assignments.`,
            confirmText: 'Deactivate',
            confirmClass: 'btn-danger'
        });

        if (!confirmed) return;

        try {
            await $.ajax({
                url: `${window.APP.CONTEXT_PATH}/api/payroll/salary-components/${componentId}/deactivate`,
                method: 'PATCH'
            });

            window.showToast('success', 'Component deactivated successfully');
            loadComponents();
        } catch (error) {
            console.error('Error deactivating component:', error);
            window.showToast('error', 'Failed to deactivate component');
        }
    };

    const applyFilters = () => {
        const typeFilter = $('#filterType').val();
        const calcTypeFilter = $('#filterCalcType').val();
        const searchTerm = $('#searchComponent').val().toLowerCase();

        let filtered = [...componentsData];

        if (typeFilter) {
            filtered = filtered.filter(c => c.type === typeFilter);
        }

        if (calcTypeFilter) {
            filtered = filtered.filter(c => c.calculationType === calcTypeFilter);
        }

        if (searchTerm) {
            filtered = filtered.filter(c => 
                c.name.toLowerCase().includes(searchTerm) || 
                c.abbreviation.toLowerCase().includes(searchTerm)
            );
        }

        renderComponents(filtered);
    };

    const resetFilters = () => {
        $('#filterType').val('');
        $('#filterCalcType').val('');
        $('#searchComponent').val('');
        renderComponents(componentsData);
    };

    const showLoading = (show) => {
        if (show) {
            $('#loadingState').removeClass('d-none');
            $('#componentsTable').closest('.table-responsive').addClass('d-none');
            $('#emptyState').addClass('d-none');
        } else {
            $('#loadingState').addClass('d-none');
            $('#componentsTable').closest('.table-responsive').removeClass('d-none');
        }
    };

    const showEmptyState = (show) => {
        if (show) {
            $('#emptyState').removeClass('d-none');
            $('#componentsTable').closest('.table-responsive').addClass('d-none');
        } else {
            $('#emptyState').addClass('d-none');
            $('#componentsTable').closest('.table-responsive').removeClass('d-none');
        }
    };


    const debounce = (func, wait) => {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func.apply(this, args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    };

    // Public API
    return {
        init,
        editComponent,
        deactivateComponent
    };
})();

// Initialize on DOM ready
document.addEventListener('DOMContentLoaded', () => {
    SalaryComponents.init();
});

// Expose to global scope for inline event handlers
window.SalaryComponents = SalaryComponents;