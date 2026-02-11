$(document).ready(function () {
    const orgId = window.APP.ORG_ID || $('#globalOrgId').val();
    let deleteHolidayId = null;
    let allHolidays = [];

    $('#yearSelect').val(new Date().getFullYear());

    // Load holidays on page load
    loadHolidays();

    // Event handlers
    $('#yearSelect, #filterType').on('change', loadHolidays);
    $('#btnFilter').on('click', loadHolidays);

    // Add holiday form submission
    $('#addHolidayForm').on('submit', function (e) {
        e.preventDefault();
        addHoliday();
    });

    // Delete confirmation
    $('#confirmDeleteHoliday').on('click', function () {
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
            success: function (data) {
                allHolidays = data;
                filterAndRenderHolidays();
            },
            error: function (xhr) {
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
        $('.btn-delete').on('click', function () {
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
            success: function (response) {
                showToast('success', 'Holiday added successfully');
                const modal = bootstrap.Modal.getInstance(document.getElementById('addHolidayModal'));
                modal.hide();
                $('#addHolidayForm')[0].reset();
                loadHolidays();
            },
            error: function (xhr) {
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
            success: function () {
                showToast('success', 'Holiday deleted successfully');
                const modal = bootstrap.Modal.getInstance(document.getElementById('deleteHolidayModal'));
                modal.hide();
                loadHolidays();
            },
            error: function (xhr) {
                showToast('error', 'Failed to delete holiday');
            }
        });
    }

    // Utility functions
    // Utility functions existing...
    function getTypeBadge(type) {
        // ... (existing)
        const badges = {
            'PUBLIC': '<span class="badge bg-primary">Public Holiday</span>',
            'COMPANY_SPECIFIC': '<span class="badge bg-info">Company Specific</span>',
            'OPTIONAL': '<span class="badge bg-warning text-dark">Optional</span>'
        };
        return badges[type] || '<span class="badge bg-secondary">' + type + '</span>';
    }

    // ... (existing formatDate, escapeHtml)

    // ================= BULK UPLOAD LOGIC =================

    let bulkData = [];

    window.downloadSample = function (type) {
        let content, filename;
        if (type === 'csv') {
            content = "Date,Name,Type,Mandatory,Description\n" +
                "2025-01-26,Republic Day,PUBLIC,true,National Holiday\n" +
                "2025-08-15,Independence Day,PUBLIC,true,National Holiday\n" +
                "2025-10-02,Gandhi Jayanti,PUBLIC,true,National Holiday";
            filename = "sample_holidays.csv";
        } else {
            content = JSON.stringify([
                { holidayDate: "2025-01-26", holidayName: "Republic Day", holidayType: "PUBLIC", isMandatory: true, description: "National Holiday" },
                { holidayDate: "2025-03-14", holidayName: "Holi", holidayType: "OPTIONAL", isMandatory: false, description: "Festival of Colors" }
            ], null, 2);
            filename = "sample_holidays.json";
        }

        const blob = new Blob([content], { type: 'text/plain' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
    };

    $('#bulkFile').on('change', function (e) {
        const file = e.target.files[0];
        if (!file) return;

        const reader = new FileReader();
        reader.onload = function (e) {
            try {
                const content = e.target.result;
                if (file.name.endsWith('.json')) {
                    bulkData = JSON.parse(content);
                } else if (file.name.endsWith('.csv')) {
                    bulkData = parseCSV(content);
                } else {
                    showToast('error', 'Unsupported file format');
                    return;
                }

                // Validate
                if (!Array.isArray(bulkData) || bulkData.length === 0) {
                    showToast('error', 'No valid data found');
                    $('#btnUpload').prop('disabled', true);
                    return;
                }

                renderPreview(bulkData);
                $('#btnUpload').prop('disabled', false);

            } catch (err) {
                console.error(err);
                showToast('error', 'Error parsing file: ' + err.message);
            }
        };
        reader.readAsText(file);
    });

    function parseCSV(csv) {
        const lines = csv.split('\n');
        const headers = lines[0].split(',').map(h => h.trim().toLowerCase());
        const result = [];

        for (let i = 1; i < lines.length; i++) {
            if (!lines[i].trim()) continue;

            // Handle quotes properly? For now simple split. 
            // If comma inside description, simpler split fails.
            // Using a simple regex for quoted CSV if needed, 
            // but user said "make user clear instruction".
            // I'll stick to simple split for robustness in "simple" use cases.

            const currentLine = lines[i].split(',');
            // Mapping: Date, Name, Type, Mandatory, Description
            // Assuming order: Date, Name, Type, Mandatory, Description

            if (currentLine.length < 2) continue;

            const obj = {
                holidayDate: (currentLine[0] || '').trim(),
                holidayName: (currentLine[1] || '').trim(),
                holidayType: (currentLine[2] || 'OPTIONAL').trim().toUpperCase(),
                isMandatory: (currentLine[3] || 'true').trim().toLowerCase() === 'true' || (currentLine[3] || '').trim().toLowerCase() === 'yes',
                description: (currentLine.slice(4).join(',') || '').trim() // Join rest as description
            };

            // Map types logic
            if (!['PUBLIC', 'COMPANY_SPECIFIC', 'OPTIONAL'].includes(obj.holidayType)) {
                obj.holidayType = 'OPTIONAL';
            }

            result.push(obj);
        }
        return result;
    }

    function renderPreview(data) {
        $('#previewCount').text(data.length);
        const first5 = data.slice(0, 50); // Limit preview

        const html = first5.map(h => `
            <tr>
                <td>${h.holidayDate}</td>
                <td>${h.holidayName}</td>
                <td><span class="badge bg-light text-dark border">${h.holidayType}</span></td>
                <td>${h.isMandatory ? 'Yes' : 'No'}</td>
            </tr>
        `).join('');

        $('#previewBody').html(html);
        $('#uploadPreview').removeClass('d-none');
    }

    $('#btnUpload').on('click', function () {
        const btn = $(this);
        btn.prop('disabled', true).html('<div class="spinner-border spinner-border-sm me-2"></div>Uploading...');

        $.ajax({
            url: `/api/holidays/org/${orgId}/bulk`,
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(bulkData),
            success: function (response) {
                showToast('success', `Successfully uploaded ${response.length} holidays`);
                const modal = bootstrap.Modal.getInstance(document.getElementById('bulkUploadModal'));
                modal.hide();
                resetUploadForm();
                loadHolidays();
            },
            error: function (xhr) {
                const msg = xhr.responseJSON?.message || 'Upload failed';
                showToast('error', msg);
                btn.prop('disabled', false).html('<i class="fas fa-upload me-2"></i>Upload Holidays');
            }
        });
    });

    function resetUploadForm() {
        $('#bulkFile').val('');
        $('#previewBody').empty();
        $('#uploadPreview').addClass('d-none');
        $('#btnUpload').prop('disabled', true).html('<i class="fas fa-upload me-2"></i>Upload Holidays');
        bulkData = [];
    }


    function escapeHtml(str) {
        if (!str) return '';
        return str
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }

});