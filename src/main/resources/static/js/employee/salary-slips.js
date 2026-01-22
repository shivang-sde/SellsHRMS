const EMPLOYEE_ID = $("#globalEmployeeId").val() || window.APP.EMPLOYEE_ID;

$(document).ready(function() {
    loadEmployeeSalarySlips();
});

function loadEmployeeSalarySlips() {
    $.ajax({
        url: `/api/payroll/salary-slips/employee/${EMPLOYEE_ID}`,
        type: 'GET',
        success: function(response) {
            console.log("response ", response);
            $('#slipTableLoader').hide();
            $('#slipTableContainer').show();

            if (response.success && response.data && response.data.length > 0) {
                renderSlipTable(response.data);
            } else {
                $('#salarySlipTableBody').html(`
                    <tr>
                        <td colspan="5" class="text-center text-muted py-4">
                            <i class="bi bi-inbox" style="font-size: 2rem; display:block; margin-bottom:10px;"></i>
                            No salary slips found
                        </td>
                    </tr>
                `);
            }
        },
        error: function() {
            $('#slipTableLoader').hide();
            showAlert('danger', 'Failed to load salary slips');
        }
    });
}

function renderSlipTable(slips) {
    const tbody = $('#salarySlipTableBody');
    tbody.empty();

    slips.forEach(slip => {
        const downloadBtn = slip.pdfUrl
    ? `<a href="${slip.pdfUrl}" class="btn btn-outline-success btn-sm" target="_blank">
         <i class="bi bi-file-earmark-arrow-down"></i> Download
       </a>`
    : `<span class="badge bg-secondary">Not Generated</span>`;

        const row = `
            <tr>
                <td>${slip.month}</td>
                <td>${slip.year}</td>
                <td>${slip.netPayFormatted || '₹0.00'}</td>
                <td>${slip.status || 'Generated'}</td>
                <td class="text-center">
                    <button class="btn btn-outline-primary btn-sm me-2" onclick="previewSlip(${slip.id})">
                        <i class="bi bi-eye"></i> View
                    </button>
                    ${downloadBtn}
                </td>
            </tr>
        `;
        tbody.append(row);
    });
}

function previewSlip(id) {
    $('#slipPreviewModal').modal('show');
    $('#slipPreviewContent').html(`
        <div class="text-center py-5 text-muted">
            <div class="spinner-border text-primary" role="status"></div>
            <p>Loading salary slip...</p>
        </div>
    `);

    $.ajax({
        url: `/api/payroll/salary-slips/${id}`,
        type: 'GET',
        success: function(response) {
            if (response && response.salarySlipHtml) {
                $('#slipPreviewContent').html(response.salarySlipHtml);
            } else {
                $('#slipPreviewContent').html('<p class="text-center text-muted py-5">No preview available.</p>');
            }
        },
        error: function() {
            $('#slipPreviewContent').html('<p class="text-center text-danger py-5">Error loading slip preview.</p>');
        }
    });
}

function downloadSlipPdf(id) {
    window.location.href = `/api/payroll/salary-slips/${id}/pdf`;
}

function showAlert(type, message) {
    const alertHtml = `
        <div class="alert alert-${type} alert-dismissible fade show position-fixed top-0 start-50 translate-middle-x mt-3"
             role="alert" style="z-index:9999; min-width:400px;">
            <i class="bi bi-${getAlertIcon(type)}"></i> ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>`;
    $('.alert.position-fixed').remove();
    $('body').append(alertHtml);
    setTimeout(() => $('.alert.position-fixed').fadeOut(() => $(this).remove()), 4000);
}

function getAlertIcon(type) {
    const icons = {
        success: 'check-circle-fill',
        danger: 'exclamation-triangle-fill',
        warning: 'exclamation-circle-fill',
        info: 'info-circle-fill'
    };
    return icons[type] || 'info-circle-fill';
}
