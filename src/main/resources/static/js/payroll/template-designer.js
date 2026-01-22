/**
 * Salary Slip Template Designer - JavaScript
 * Path: /resources/static/js/payroll/template-designer.js
 */

// Global variables
const ORG_ID = $("#globalOrgId").val() || window.APP.ORG_ID;

let availableFields = {};
let selectedFields = {
    organisation: [],
    employee: [],
    bank: [],
    payRun: [],
    earnings: [],
    deductions: [],
    summary: []
};

// Initialize on document ready
$(document).ready(function() {
    initializeTemplateDesigner();
});

/**
 * Initialize template designer
 */
function initializeTemplateDesigner() {
    loadAvailableFields();
    
    // Load existing template if editing
    const templateId = $('#templateId').val();
    if (templateId) {
        loadExistingTemplate(templateId);
    }

    // Setup event listeners
    setupEventListeners();
}

/**
 * Setup event listeners
 */
function setupEventListeners() {
    // Logo file input change
    $('#logoFile').on('change', function() {
        const file = this.files[0];
        if (file) {
            // Show preview
            const reader = new FileReader();
            reader.onload = function(e) {
                $('#logoPreviewImage').attr('src', e.target.result);
                $('#logoPreview').show();
            };
            reader.readAsDataURL(file);
        }
    });

    // Template name input
    $('#templateName').on('input', function() {
        // Auto-save indicator could go here
    });
}

/**
 * Load available fields from backend
 */
function loadAvailableFields() {
    $.ajax({
        url: `/api/salary-slip-template/${ORG_ID}/available-fields`,
        type: 'GET',
        success: function(response) {
            console.log("reponse ", response);
            if (response.success) {
                availableFields = response.data;
                renderFieldSections();
            } else {
                showAlert('danger', response.message);
            }
        },
        error: function(xhr) {
            showAlert('danger', 'Failed to load available fields. Please refresh the page.');
            console.error('Error loading fields:', xhr);
        }
    });
}

/**
 * Render field selection sections 
 */
function renderFieldSections() {
    const container = $('#fieldSections');
    container.empty();

    if (!availableFields || Object.keys(availableFields).length === 0) {
        container.html(`
            <div class="alert alert-warning text-center">
                No available fields found. Please check your backend response.
            </div>
        `);
        return;
    }

    const sectionTitles = {
        organisation: 'Organisation Details',
        employee: 'Employee Information',
        bank: 'Bank Details',
        payRun: 'Pay Period Information',
        earnings: 'Earnings Components',
        deductions: 'Deduction Components',
        summary: 'Salary Summary'
    };

    Object.entries(availableFields).forEach(([section, fields]) => {
        if (!Array.isArray(fields) || fields.length === 0) return;

        const sectionId = `section-${section}`;
        const sectionHtml = `
            <div class="card mb-3 shadow-sm">
                <div class="card-header bg-light fw-bold">
                    <i class="bi bi-${getSectionIcon(section)} me-2"></i>
                    ${sectionTitles[section] || section}
                </div>
                <div class="card-body d-flex flex-wrap gap-3" id="${sectionId}">
                    ${fields.map(field => `
                        <div class="form-check" style="min-width: 220px;">
                            <input class="form-check-input"
                                type="checkbox"
                                id="field-${section}-${field.key}"
                                value="${field.key}"
                                data-section="${section}"
                                onchange="updateSelectedFields()">
                            <label class="form-check-label" for="field-${section}-${field.key}">
                                ${escapeHtml(field.label || field.key)}
                            </label>
                        </div>
                    `).join('')}
                </div>
            </div>
        `;

        container.append(sectionHtml);
    });

    // Smoothly reveal once loaded
    container.hide().fadeIn(300);
}


/**
 * Get icon for section
 */
function getSectionIcon(section) {
    const icons = {
        organisation: 'building',
        employee: 'person-badge',
        bank: 'bank',
        payRun: 'calendar3',
        earnings: 'arrow-up-circle-fill',
        deductions: 'arrow-down-circle-fill',
        summary: 'calculator-fill'
    };
    return icons[section] || 'dot';
}

/**
 * Update selected fields when checkboxes change
 */
function updateSelectedFields() {
    Object.keys(selectedFields).forEach(section => {
        selectedFields[section] = [];
        $(`input[data-section="${section}"]:checked`).each(function() {
            selectedFields[section].push($(this).val());
        });
    });
}

/**
 * Generate preview with current selections
 */
function generatePreview() {
    updateSelectedFields();
    
    // Validate at least some fields are selected
    const totalSelected = Object.values(selectedFields).reduce((sum, arr) => sum + arr.length, 0);
    if (totalSelected === 0) {
        showAlert('warning', 'Please select at least one field to preview');
        return;
    }
    
    const templateHtml = buildTemplateHtml();
    const configJson = JSON.stringify({
        sections: Object.keys(selectedFields).map(section => ({
            name: section,
            fields: selectedFields[section]
        })).filter(s => s.fields.length > 0)
    });
    
    // Show loading in preview
    $('#previewContainer').html(`
        <div class="text-center py-5">
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Generating preview...</span>
            </div>
            <p class="text-muted mt-3">Generating preview...</p>
        </div>
    `);
    
    $.ajax({
        url: `/api/salary-slip-template/${ORG_ID}/preview`,
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
            templateHtml: templateHtml,
            configJson: configJson
        }),
        success: function(response) {
            if (response.success) {
                $('#previewContainer').html(response.data);
            } else {
                $('#previewContainer').html(`
                    <div class="alert alert-danger m-4">
                        <i class="bi bi-exclamation-triangle"></i> ${response.message}
                    </div>
                `);
            }
        },
        error: function(xhr) {
            $('#previewContainer').html(`
                <div class="alert alert-danger m-4">
                    <i class="bi bi-exclamation-triangle"></i> Failed to generate preview. Please try again.
                </div>
            `);
            console.error('Preview error:', xhr);
        }
    });
}


/**
 * Build template HTML from selected fields (Dynamic FreeMarker version)
 */
function buildTemplateHtml() {
    let html = `
        <div class="salary-slip" style="font-family: Arial, sans-serif; max-width: 800px; margin: 0 auto;">
    `;

    // Organisation header
    if (selectedFields.organisation.length > 0) {
        html += `
            <div class="org-header" style="text-align: center; padding: 20px; border-bottom: 3px solid #333;">
                ${selectedFields.organisation.includes('logoUrl') ? '<img src="\${organisation.logoUrl!""}" alt="Logo" style="max-height: 80px; margin-bottom: 10px;">' : ''}
                ${selectedFields.organisation.includes('name') ? '<h2 style="margin: 10px 0; color: #333;">\${organisation.name!""}</h2>' : ''}
                ${selectedFields.organisation.includes('address') ? '<p style="margin: 5px 0; color: #666;">\${organisation.address!""}' : ''}
                ${selectedFields.organisation.includes('city') ? ', \${organisation.city!""}' : ''}
                ${selectedFields.organisation.includes('state') ? ', \${organisation.state!""}' : ''}
                ${selectedFields.organisation.includes('pincode') ? ' - \${organisation.pincode!""}' : ''}
                ${selectedFields.organisation.includes('address') ? '</p>' : ''}
                ${(selectedFields.organisation.includes('email') || selectedFields.organisation.includes('phone')) ? '<p style="margin: 5px 0; color: #666;">' : ''}
                ${selectedFields.organisation.includes('email') ? 'Email: \${organisation.email!""}' : ''}
                ${(selectedFields.organisation.includes('email') && selectedFields.organisation.includes('phone')) ? ' | ' : ''}
                ${selectedFields.organisation.includes('phone') ? 'Phone: \${organisation.phone!""}' : ''}
                ${(selectedFields.organisation.includes('email') || selectedFields.organisation.includes('phone')) ? '</p>' : ''}
            </div>
        `;
    }

    // Title section
    html += `
        <div style="text-align: center; padding: 20px; background-color: #f8f9fa;">
            <h3 style="margin: 0; color: #495057;">SALARY SLIP</h3>
            ${selectedFields.payRun.includes('payPeriod') ? '<p style="margin: 5px 0; color: #6c757d;">For the month of \${payRun.payPeriod!""}</p>' : ''}
        </div>
    `;

    // Employee details
    if (selectedFields.employee.length > 0) {
        html += `
            <div style="padding: 20px;">
                <table style="width: 100%; border-collapse: collapse;">
                    <tbody>
        `;

        const empFields = selectedFields.employee;
        for (let i = 0; i < empFields.length; i += 2) {
            html += '<tr>';
            html += `<td style="padding: 8px; width: 25%; font-weight: bold;">${getLabelForField('employee', empFields[i])}:</td>`;
            html += `<td style="padding: 8px; width: 25%;">\${employee.${empFields[i]}!""}</td>`;

            if (empFields[i + 1]) {
                html += `<td style="padding: 8px; width: 25%; font-weight: bold;">${getLabelForField('employee', empFields[i + 1])}:</td>`;
                html += `<td style="padding: 8px; width: 25%;">\${employee.${empFields[i + 1]}!""}</td>`;
            } else {
                html += '<td colspan="2"></td>';
            }
            html += '</tr>';
        }

        html += `
                    </tbody>
                </table>
            </div>
        `;
    }

    // Bank details
    if (selectedFields.bank.length > 0) {
        html += `
            <div style="padding: 0 20px 20px 20px;">
                <h5 style="margin-bottom: 10px; color: #495057; border-bottom: 2px solid #e9ecef; padding-bottom: 8px;">Bank Details</h5>
                <table style="width: 100%; border-collapse: collapse;">
                    <tbody>
        `;

        const bankFields = selectedFields.bank;
        for (let i = 0; i < bankFields.length; i += 2) {
            html += '<tr>';
            html += `<td style="padding: 8px; width: 25%; font-weight: bold;">${getLabelForField('bank', bankFields[i])}:</td>`;
            html += `<td style="padding: 8px; width: 25%;">\${bank.${bankFields[i]}!""}</td>`;

            if (bankFields[i + 1]) {
                html += `<td style="padding: 8px; width: 25%; font-weight: bold;">${getLabelForField('bank', bankFields[i + 1])}:</td>`;
                html += `<td style="padding: 8px; width: 25%;">\${bank.${bankFields[i + 1]}!""}</td>`;
            } else {
                html += '<td colspan="2"></td>';
            }
            html += '</tr>';
        }

        html += `
                    </tbody>
                </table>
            </div>
        `;
    }

    // ✅ Dynamic Salary Components
    html += `
    <div style="padding: 20px;">
        <table style="width: 100%; border-collapse: collapse; border: 1px solid #dee2e6;">
            <thead>
                <tr style="background-color: #e9ecef;">
                    <th style="padding: 12px; text-align: left; border: 1px solid #dee2e6; width: 40%;">Earnings</th>
                    <th style="padding: 12px; text-align: right; border: 1px solid #dee2e6; width: 10%;">Amount</th>
                    <th style="padding: 12px; text-align: left; border: 1px solid #dee2e6; width: 40%;">Deductions</th>
                    <th style="padding: 12px; text-align: right; border: 1px solid #dee2e6; width: 10%;">Amount</th>
                </tr>
            </thead>
            <tbody>
    <#assign maxRows = [(earnings?size)!0, (deductions?size)!0]?max>
    <#list 0..(maxRows - 1) as i>
        <tr>
            <td style="padding:10px;border:1px solid #dee2e6;">
                <#if earnings[i]??>\${earnings[i].name}</#if>
            </td>
            <td style="padding:10px;text-align:right;border:1px solid #dee2e6;">
                <#if earnings[i]??>₹\${earnings[i].amount?string["#,##0.00"]}</#if>
            </td>
            <td style="padding:10px;border:1px solid #dee2e6;">
                <#if deductions[i]??>\${deductions[i].name}</#if>
            </td>
            <td style="padding:10px;text-align:right;border:1px solid #dee2e6;">
                <#if deductions[i]??>₹\${deductions[i].amount?string["#,##0.00"]}</#if>
            </td>
        </tr>
    </#list>
</tbody>

        </table>
    </div>
`;


    // Summary section
    if (selectedFields.summary.length > 0) {
        html += `
            <div style="padding: 20px; background-color: #f8f9fa; margin-top: 20px;">
                <table style="width: 100%; border-collapse: collapse;">
                    <tbody>
                    ${selectedFields.summary.includes('basePay') ? '<tr><td style="padding: 8px; font-weight: bold; font-size: 1.05em;">Total Earnings:</td><td style="padding: 8px; text-align: right; font-weight: bold; font-size: 1.05em;">\${summary.basePay!""}</td></tr>' : ''}
                        ${selectedFields.summary.includes('totalEarnings') ? '<tr><td style="padding: 8px; font-weight: bold; font-size: 1.05em;">Total Earnings:</td><td style="padding: 8px; text-align: right; font-weight: bold; font-size: 1.05em;">\${summary.totalEarnings!""}</td></tr>' : ''}
                        ${selectedFields.summary.includes('totalDeductions') ? '<tr><td style="padding: 8px; font-weight: bold; font-size: 1.05em;">Total Deductions:</td><td style="padding: 8px; text-align: right; font-weight: bold; font-size: 1.05em;">\${summary.totalDeductions!""}</td></tr>' : ''}
                        ${selectedFields.summary.includes('netPay') ? '<tr style="background-color: #28a745; color: white;"><td style="padding: 15px; font-weight: bold; font-size: 1.2em;">NET PAY:</td><td style="padding: 15px; text-align: right; font-weight: bold; font-size: 1.2em;">\${summary.netPay!""}</td></tr>' : ''}
                        ${selectedFields.summary.includes('netPayInWords') ? '<tr><td colspan="2" style="padding: 12px; font-style: italic; text-align: center; color: #495057;">(\${summary.netPayInWords!""})</td></tr>' : ''}
                    </tbody>
                </table>
            </div>
        `;
    }

    // Footer
    html += `
        <div style="padding: 20px; text-align: center; color: #6c757d; font-size: 0.9em; border-top: 1px solid #dee2e6; margin-top: 30px;">
            <p style="margin: 0;">This is a computer-generated salary slip and does not require a signature.</p>
        </div>
    </div>
    `;

    return html;
}


/**
 * Get label for a field
 */
function getLabelForField(section, fieldKey) {
    const fields = availableFields[section];
    if (!fields) return fieldKey;
    
    const field = fields.find(f => f.key === fieldKey);
    return field ? field.label : fieldKey;
}

/**
 * Upload logo file
 */
function uploadLogo() {
    const fileInput = $('#logoFile')[0];
    if (!fileInput.files || !fileInput.files[0]) {
        showAlert('warning', 'Please select a logo file first');
        return;
    }
    
    const formData = new FormData();
    formData.append('file', fileInput.files[0]);
    
    // Show uploading state
    showAlert('info', 'Uploading logo...');
    
    $.ajax({
        url: `/api/salary-slip-template/${ORG_ID}/upload-logo`,
        type: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        success: function(response) {
            if (response.success) {
                $('#logoUrl').val(response.data);
                showAlert('success', 'Logo uploaded successfully');
            } else {
                showAlert('danger', response.message);
            }
        },
        error: function(xhr) {
            showAlert('danger', 'Failed to upload logo. Please try again.');
            console.error('Upload error:', xhr);
        }
    });
}

/**
 * Save template
 */
function saveTemplate() {
    const templateName = $('#templateName').val().trim();
    if (!templateName) {
        showAlert('warning', 'Please enter a template name');
        $('#templateName').focus();
        return;
    }
    
    updateSelectedFields();
    
    // Validate at least some fields are selected
    const totalSelected = Object.values(selectedFields).reduce((sum, arr) => sum + arr.length, 0);
    if (totalSelected === 0) {
        showAlert('warning', 'Please select at least one field before saving');
        return;
    }
    
    const templateHtml = buildTemplateHtml();
    const configJson = JSON.stringify({
        sections: Object.keys(selectedFields).map(section => ({
            name: section,
            fields: selectedFields[section]
        })).filter(s => s.fields.length > 0)
    });
    
    const data = {
        id: $('#templateId').val() || null,
        templateName: templateName,
        templateHtml: templateHtml,
        configJson: configJson,
        isDefault: $('#isDefault').is(':checked'),
        logoUrl: $('#logoUrl').val() || null
    };
    
    $.ajax({
        url: `/api/salary-slip-template/${ORG_ID}/save`,
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: function(response) {
            if (response.success) {
                showAlert('success', response.message);
                $('#templateId').val(response.data.id);
                
                // Update URL if it was a new template
                if (!data.id) {
                    window.history.pushState({}, '', '/salary-slip-template/edit/' + response.data.id);
                }
            } else {
                showAlert('danger', response.message);
            }
        },
        error: function(xhr) {
            showAlert('danger', 'Failed to save template. Please try again.');
            console.error('Save error:', xhr);
        }
    });
}

/**
 * Load templates list modal
 */
function loadTemplates() {
    $('#templateListModal').modal('show');
    $('#templateListLoader').show();
    $('#templateListContent').hide();
    
    $.ajax({
        url: `/api/salary-slip-template/${ORG_ID}/list`,
        type: 'GET',
        success: function(response) {
            if (response.success) {
                renderTemplateList(response.data);
            } else {
                showAlert('danger', response.message);
            }
            $('#templateListLoader').hide();
            $('#templateListContent').show();
        },
        error: function(xhr) {
            $('#templateListLoader').hide();
            showAlert('danger', 'Failed to load templates');
            console.error('Load templates error:', xhr);
        }
    });
}

/**
 * Render template list in modal
 */
function renderTemplateList(templates) {
    const tbody = $('#templateListBody');
    tbody.empty();
    
    if (!templates || templates.length === 0) {
        tbody.append(`
            <tr>
                <td colspan="4" class="text-center py-4 text-muted">
                    <i class="bi bi-inbox" style="font-size: 2rem; display: block; margin-bottom: 10px;"></i>
                    No templates found
                </td>
            </tr>
        `);
        return;
    }
    
    templates.forEach(template => {
        const row = `
            <tr>
                <td>
                    <strong>${escapeHtml(template.templateName)}</strong>
                </td>
                <td>${formatDate(template.createdDate)}</td>
                <td class="text-center">
                    ${template.isDefault 
                        ? '<span class="badge bg-success"><i class="bi bi-star-fill"></i> Default</span>' 
                        : '<span class="badge bg-secondary">Active</span>'}
                </td>
                <td class="text-center">
                    <div class="btn-group btn-group-sm" role="group">
                        <button class="btn btn-outline-primary" onclick="editTemplate(${template.id})" title="Edit">
                            <i class="bi bi-pencil"></i>
                        </button>
                        ${!template.isDefault ? `
                            <button class="btn btn-outline-success" onclick="setTemplateAsDefault(${template.id})" title="Set as Default">
                                <i class="bi bi-star"></i>
                            </button>
                        ` : ''}
                        <button class="btn btn-outline-danger" onclick="confirmDeleteTemplate(${template.id}, '${escapeHtml(template.templateName)}')" title="Delete">
                            <i class="bi bi-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `;
        tbody.append(row);
    });
}

/**
 * Edit template
 */
function editTemplate(id) {
    window.location.href = `/salary-slip-template/edit/` + id;
}

/**
 * Set template as default
 */
function setTemplateAsDefault(id) {
    $.ajax({
        url: `/api/salary-slip-template/${ORG_ID}/template/${id}/set-default`,
        type: 'PUT',
        success: function(response) {
            if (response.success) {
                showAlert('success', 'Template set as default');
                loadTemplates(); // Refresh list
            } else {
                showAlert('danger', response.message);
            }
        },
        error: function(xhr) {
            showAlert('danger', 'Failed to set template as default');
            console.error('Set default error:', xhr);
        }
    });
}

/**
 * Confirm template deletion
 */
function confirmDeleteTemplate(id, name) {
    if (confirm(`Are you sure you want to delete template "${name}"? This action cannot be undone.`)) {
        deleteTemplate(id);
    }
}

/**
 * Delete template
 */
function deleteTemplate(id) {
    $.ajax({
        url: `/api/salary-slip-template/${ORG_ID}/template/${id}`,
        type: 'DELETE',
        success: function(response) {
            if (response.success) {
                showAlert('success', 'Template deleted successfully');
                loadTemplates(); // Refresh list
            } else {
                showAlert('danger', response.message);
            }
        },
        error: function(xhr) {
            showAlert('danger', 'Failed to delete template');
            console.error('Delete error:', xhr);
        }
    });
}

/**
 * Load existing template for editing
 */
function loadExistingTemplate(id) {
    $.ajax({
        url: `/api/salary-slip-template/${ORG_ID}/template/${id}`,
        type: 'GET',
        success: function(response) {
            if (response.success && response.data) {
                const template = response.data;
                
                // Populate form fields
                $('#templateName').val(template.templateName);
                $('#isDefault').prop('checked', template.isDefault);
                
                if (template.logoUrl) {
                    $('#logoUrl').val(template.logoUrl);
                    $('#logoPreviewImage').attr('src', template.logoUrl);
                    $('#logoPreview').show();
                }
                
                // Parse config and select fields
                try {
                    const config = JSON.parse(template.configJson);
                    config.sections.forEach(section => {
                        section.fields.forEach(field => {
                            $(`#field-${section.name}-${field}`).prop('checked', true);
                        });
                    });
                    
                    updateSelectedFields();
                    
                    // Auto-generate preview
                    setTimeout(() => generatePreview(), 500);
                } catch (e) {
                    console.error('Error parsing template config:', e);
                    showAlert('warning', 'Template loaded but configuration may be invalid');
                }
            }
        },
        error: function(xhr) {
            showAlert('danger', 'Failed to load template');
            console.error('Load template error:', xhr);
        }
    });
}

/**
 * Reset template form
 */
function resetTemplate() {
    if (!confirm('Are you sure you want to reset all selections? This will clear all your current work.')) {
        return;
    }
    
    $('#templateName').val('');
    $('#isDefault').prop('checked', false);
    $('#logoUrl').val('');
    $('#logoFile').val('');
    $('#logoPreview').hide();
    $('input[type="checkbox"][data-section]').prop('checked', false);
    
    $('#previewContainer').html(`
        <div class="text-center text-muted py-5">
            <i class="bi bi-file-earmark-text" style="font-size: 4rem; opacity: 0.3;"></i>
            <p class="mt-3">Select fields and click "Preview Template" to see your design</p>
            <small class="text-muted">Choose at least organisation and employee fields to get started</small>
        </div>
    `);
    
    updateSelectedFields();
    showAlert('info', 'Template reset successfully');
}

/**
 * Print preview
 */
function printPreview() {
    window.print();
}

/**
 * Export preview to PDF (mock - needs actual implementation)
 */
function exportToPDF() {
    const templateId = $('#templateId').val();
    if (!templateId) {
        showAlert('warning', 'Please save the template first before generating PDF');
        return;
    }
    
    showAlert('info', 'PDF export is available when viewing actual salary slips');
}

/**
 * Utility: Format date
 */
function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-IN', { 
        year: 'numeric', 
        month: 'short', 
        day: 'numeric' 
    });
}

/**
 * Utility: Escape HTML
 */
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

/**
 * Utility: Show alert message
 */
function showAlert(type, message) {
    const alertHtml = `
        <div class="alert alert-${type} alert-dismissible fade show position-fixed top-0 start-50 translate-middle-x mt-3" 
             role="alert" style="z-index: 9999; min-width: 400px;">
            <i class="bi bi-${getAlertIcon(type)}"></i> ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    `;
    
    // Remove existing alerts
    $('.alert.position-fixed').remove();
    
    // Add new alert
    $('body').append(alertHtml);
    
    // Auto dismiss after 5 seconds
    setTimeout(function() {
        $('.alert.position-fixed').fadeOut(function() {
            $(this).remove();
        });
    }, 5000);
}

/**
 * Utility: Get alert icon
 */
function getAlertIcon(type) {
    const icons = {
        'success': 'check-circle-fill',
        'danger': 'exclamation-triangle-fill',
        'warning': 'exclamation-circle-fill',
        'info': 'info-circle-fill'
    };
    return icons[type] || 'info-circle-fill';
}


/**
 * Escape HTML to prevent rendering issues
 */
function escapeHtml(text) {
    if (!text) return '';
    return text.replace(/[&<>"']/g, function(m) {
        return {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#39;'
        }[m];
    });
}
