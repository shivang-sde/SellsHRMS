/**
 * Salary Slip Template List - JavaScript
 * Path: /resources/static/js/payroll/template-list.js
 */

const ORG_ID = $("#globalOrgId").val() || window.APP.ORG_ID;

// Initialize on document ready
$(document).ready(function() {
    loadAllTemplates();
    setupSearchFilter();
});

/**
 * Load all templates from API
 */
function loadAllTemplates() {
    showLoading();
    
    $.ajax({
        url: `/api/salary-slip-template/${ORG_ID}/list`,
        type: 'GET',
        success: function(response) {
            console.log("salary template list ", response);
            hideLoading();
            
            if (response.success) {
                renderTemplateTable(response.data);
            } else {
                showError('Failed to load templates: ' + response.message);
            }
        },
        error: function(xhr) {
            hideLoading();
            showError('Failed to load templates. Please refresh the page.');
            console.error('Error loading templates:', xhr);
        }
    });
}

/**
 * Render template table
 */
function renderTemplateTable(templates) {
    const tbody = $('#templateTableBody');
    tbody.empty();
    
    if (!templates || templates.length === 0) {
        showEmptyState();
        return;
    }
    
    $('#emptyState').hide();
    $('#tableContent').show();
    
    templates.forEach(template => {
        const row = `
            <tr data-template-id="${template.id}">
                <td>
                    <div class="d-flex align-items-center">
                        <i class="bi bi-file-earmark-text text-primary me-2" style="font-size: 1.5rem;"></i>
                        <div>
                            <strong>${escapeHtml(template.templateName)}</strong>
                            ${template.isDefault ? '<span class="badge bg-success ms-2"><i class="bi bi-star-fill"></i> Default</span>' : ''}
                        </div>
                    </div>
                </td>
                <td>${template.createdBy || '-'}</td>
                <td>${formatDate(template.createdDate)}</td>
                <td>${formatDate(template.updatedDate)}</td>
                <td class="text-center">
                    <span class="badge ${template.isActive ? 'bg-success' : 'bg-secondary'}">
                        ${template.isActive ? 'Active' : 'Inactive'}
                    </span>
                </td>
                <td class="text-center">
                    <div class="btn-group btn-group-sm" role="group">
                        <button class="btn btn-outline-primary" onclick="editTemplate(${template.id})" title="Edit Template">
                            <i class="bi bi-pencil-square"></i> Edit
                        </button>
                        ${!template.isDefault ? `
                            <button class="btn btn-outline-success" onclick="setAsDefault(${template.id})" title="Set as Default">
                                <i class="bi bi-star"></i> Default
                            </button>
                        ` : ''}
                        <button class="btn btn-outline-danger" onclick="showDeleteConfirm(${template.id}, '${escapeHtml(template.templateName)}')" title="Delete Template">
                            <i class="bi bi-trash"></i> Delete
                        </button>
                    </div>
                </td>
            </tr>
        `;
        tbody.append(row);
    });
    
    // Add click handler for rows
    $('#templateTableBody tr').on('click', function(e) {
        if (!$(e.target).closest('button').length) {
            const templateId = $(this).data('template-id');
            editTemplate(templateId);
        }
    });
}

/**
 * Show loading state
 */
function showLoading() {
    $('#loadingState').show();
    $('#tableContent').hide();
}

/**
 * Hide loading state
 */
function hideLoading() {
    $('#loadingState').hide();
}

/**
 * Show empty state
 */
function showEmptyState() {
    $('#tableContent').show();
    $('#emptyState').show();
}

/**
 * Show error message
 */
function showError(message) {
    const alertHtml = `
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="bi bi-exclamation-triangle-fill"></i> ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
    
    $('.card-body').prepend(alertHtml);
    
    setTimeout(function() {
        $('.alert').fadeOut(function() {
            $(this).remove();
        });
    }, 5000);
}

/**
 * Setup search filter
 */
function setupSearchFilter() {
    $('#searchTemplate').on('input', function() {
        const searchTerm = $(this).val().toLowerCase();
        
        $('#templateTableBody tr').each(function() {
            const templateName = $(this).find('td:first strong').text().toLowerCase();
            
            if (templateName.includes(searchTerm)) {
                $(this).show();
            } else {
                $(this).hide();
            }
        });
        
        // Show/hide empty state based on visible rows
        const visibleRows = $('#templateTableBody tr:visible').length;
        if (visibleRows === 0) {
            $('#emptyState').html(`
                <div class="text-center py-5">
                    <i class="bi bi-search" style="font-size: 4rem; color: #dee2e6;"></i>
                    <h5 class="text-muted mt-3">No templates found</h5>
                    <p class="text-muted">Try adjusting your search criteria</p>
                </div>
            `).show();
        } else {
            $('#emptyState').hide();
        }
    });
}

/**
 * Refresh templates
 */
function refreshTemplates() {
    $('#searchTemplate').val('');
    loadAllTemplates();
}

/**
 * Edit template
 */
function editTemplate(id) {
    window.location.href = '/salary-slip-template/edit/' + id;
}

/**
 * Set template as default
 */
function setAsDefault(id) {
    $.ajax({
        url: `/api/salary-slip-template/${ORG_ID}/template/${id}/set-default`,
        type: 'PUT',
        success: function(response) {
            if (response.success) {
                showSuccessMessage('Template set as default successfully');
                loadAllTemplates(); // Reload to update badges
            } else {
                showError(response.message);
            }
        },
        error: function(xhr) {
            showError('Failed to set template as default');
            console.error('Set default error:', xhr);
        }
    });
}

/**
 * Show delete confirmation modal
 */
function showDeleteConfirm(id, name) {
    $('#templateIdToDelete').val(id);
    $('#templateNameToDelete').text(name);
    
    const deleteModal = new bootstrap.Modal(document.getElementById('deleteConfirmModal'));
    deleteModal.show();
}

/**
 * Confirm and execute delete
 */
function confirmDelete() {
    const templateId = $('#templateIdToDelete').val();
    
    $.ajax({
        url: `/api/salary-slip-template/${ORG_ID}/template/${templateId}`,
        type: 'DELETE',
        success: function(response) {
            if (response.success) {
                // Hide modal
                const deleteModal = bootstrap.Modal.getInstance(document.getElementById('deleteConfirmModal'));
                deleteModal.hide();
                
                showSuccessMessage('Template deleted successfully');
                loadAllTemplates(); // Reload table
            } else {
                showError(response.message);
            }
        },
        error: function(xhr) {
            showError('Failed to delete template');
            console.error('Delete error:', xhr);
        }
    });
}

/**
 * Show success message
 */
function showSuccessMessage(message) {
    const alertHtml = `
        <div class="alert alert-success alert-dismissible fade show position-fixed top-0 start-50 translate-middle-x mt-3" 
             role="alert" style="z-index: 9999; min-width: 400px;">
            <i class="bi bi-check-circle-fill"></i> ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
    
    $('body').append(alertHtml);
    
    setTimeout(function() {
        $('.alert.position-fixed').fadeOut(function() {
            $(this).remove();
        });
    }, 3000);
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
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
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