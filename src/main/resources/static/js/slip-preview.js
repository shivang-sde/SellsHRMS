/**
 * Salary Slip Preview - JavaScript
 * Path: /resources/static/js/payroll/slip-preview.js
 */

// Initialize on document ready
$(document).ready(function() {
    // Any initialization needed for preview page
    console.log('Salary slip preview loaded');
});

/**
 * Print salary slip
 */
function printSlip() {
    window.print();
}

/**
 * Download salary slip as PDF
 */
function downloadSlipPDF() {
    const salarySlipId = $('#salarySlipId').val();
    
    if (!salarySlipId) {
        showAlert('danger', 'Invalid salary slip ID');
        return;
    }
    
    // Show loading indicator
    showAlert('info', 'Generating PDF... Please wait.');
    
    // Trigger PDF download
    window.location.href = `/api/salary-slip-template/pdf/${salarySlipId}`;
    
    // Clear the loading message after a delay
    setTimeout(function() {
        $('.alert').fadeOut(function() {
            $(this).remove();
        });
    }, 3000);
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
 * Utility: Get alert icon based on type
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