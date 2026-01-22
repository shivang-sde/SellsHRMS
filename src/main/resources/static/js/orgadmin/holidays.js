$(document).ready(function() {
    const orgId = window.APP.ORG_ID || $('#globalOrgId').val();
    let deleteHolidayId = null;
    let allHolidays = [];

    // Load holidays on page load
    loadHolidays();

    // Event handlers
    $('#yearSelect, #filterType').on('change', loadHolidays);
    $('#btnFilter').on('click', loadHolidays);

    // Add holiday form submission
    $('#addHolidayForm').on('submit', function(e) {
        e.preventDefault();
        addHoliday();
    });

    // Delete confirmation
    $('#confirmDeleteHoliday').on('click', function() {
        deleteHoliday(deleteHolidayId);
    });

    // Load holidays
    function loadHolidays() {
        const year = $('#yearSelect').val();
        const startDate = `${year}-01-01`;
        const endDate = `${year}-12-31`;

        $('#holidaysTableBody').html(`
            <tr>
                <td colspan="6" class="text-center">
                    <div class="spinner-border text-primary"></div>
                    <p class="mt-2 text-muted">Loading holidays...</p>
                </td>
            </tr>
        `);

        $.ajax({
            url: `/api/holidays/org/${orgId}/range`,
            method: 'GET',
            data: { startDate, endDate },
            success: function(data) {
                allHolidays = data;
                filterAndRenderHolidays();
            },
            error: function(xhr) {
                showToast('error', 'Failed to load holidays');
                $('#holidaysTableBody').html(`
                    <tr>
                        <td colspan="6" class="text-center text-danger">
                            Failed to load holidays
                        </td>
                    </tr>
                `);
            }
        });
    }

    // Filter and render holidays
    function filterAndRenderHolidays() {
        const typeFilter = $('#filterType').val();
        
        let filtered = typeFilter ? 
            allHolidays.filter(h => h.holidayType === typeFilter) : 
            allHolidays;

        // Sort by date
        filtered.sort((a, b) => new Date(a.holidayDate) - new Date(b.holidayDate));

        renderHolidays(filtered);
    }

    // Render holidays table
    function renderHolidays(data) {
        if (!data || data.length === 0) {
            $('#holidaysTableBody').html(`
                <tr>
                    <td colspan="6" class="text-center text-muted">
                        <i class="fas fa-calendar-times fa-2x mb-2"></i>
                        <p>No holidays found for selected year/type</p>
                    </td>
                </tr>
            `);
            return;
        }

        let html = '';
        data.forEach(holiday => {
            const typeBadge = getTypeBadge(holiday.holidayType);
            const mandatoryBadge = holiday.isMandatory ? 
                '<span class="badge bg-success">Yes</span>' : 
                '<span class="badge bg-secondary">No</span>';

            html += `
                <tr>
                    <td><strong>${formatDate(holiday.holidayDate)}</strong></td>
                    <td>${escapeHtml(holiday.holidayName)}</td>
                    <td>${typeBadge}</td>
                    <td>${mandatoryBadge}</td>
                    <td><small>${escapeHtml(holiday.description || '--')}</small></td>
                    <td>
                        <button class="btn btn-sm btn-outline-danger btn-delete" data-id="${holiday.id}">
                            <i class="fas fa-trash"></i>
                        </button>
                    </td>
                </tr>
            `;
        });

        $('#holidaysTableBody').html(html);

        // Attach delete button handlers
        $('.btn-delete').on('click', function() {
            deleteHolidayId = $(this).data('id');
            const modal = new bootstrap.Modal(document.getElementById('deleteHolidayModal'));
            modal.show();
        });
    }

    // Add holiday
    function addHoliday() {
        const formData = {
            orgId: parseInt(orgId),
            holidayName: $('input[name="holidayName"]').val(),
            holidayDate: $('input[name="holidayDate"]').val(),
            holidayType: $('select[name="holidayType"]').val(),
            description: $('textarea[name="description"]').val(),
            isMandatory: $('input[name="isMandatory"]').is(':checked')
        };

        $.ajax({
            url: '/api/holidays',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            success: function(response) {
                showToast('success', 'Holiday added successfully');
                const modal = bootstrap.Modal.getInstance(document.getElementById('addHolidayModal'));
                modal.hide();
                $('#addHolidayForm')[0].reset();
                loadHolidays();
            },
            error: function(xhr) {
                let errorMsg = 'Failed to add holiday';
                if (xhr.responseJSON && xhr.responseJSON.message) {
                    errorMsg = xhr.responseJSON.message;
                }
                showToast('error', errorMsg);
            }
        });
    }

    // Delete holiday
    function deleteHoliday(holidayId) {
        $.ajax({
            url: `/api/holidays/${holidayId}`,
            method: 'DELETE',
            success: function() {
                showToast('success', 'Holiday deleted successfully');
                const modal = bootstrap.Modal.getInstance(document.getElementById('deleteHolidayModal'));
                modal.hide();
                loadHolidays();
            },
            error: function(xhr) {
                showToast('error', 'Failed to delete holiday');
            }
        });
    }

    // Utility functions
    function getTypeBadge(type) {
        const badges = {
            'PUBLIC': '<span class="badge bg-primary">Public Holiday</span>',
            'COMPANY_SPECIFIC': '<span class="badge bg-info">Company Specific</span>',
            'OPTIONAL': '<span class="badge bg-warning text-dark">Optional</span>'
        };
        return badges[type] || '<span class="badge bg-secondary">' + type + '</span>';
    }

    function formatDate(dateStr) {
        if (!dateStr) return '--';
        const date = new Date(dateStr);
        return date.toLocaleDateString('en-US', {
            weekday: 'short',
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        });
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
        return String(text).replace(/[&<>"']/g, m => map[m]);
    }
});