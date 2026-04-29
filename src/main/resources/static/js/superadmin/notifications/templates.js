(function () {
    'use strict';

    const API = {
        BASE: `${APP.CONTEXT_PATH}/api/superadmin/notification/templates`
    };

    const COMMON_VARIABLES = [
        'employeeName', 'recipientName', 'department', 'dateOfJoining', 'startDate',
        'endDate', 'leaveDays', 'reason', 'approver', 'remarks'
    ];

    let templatesData = [];

    document.addEventListener('DOMContentLoaded', function () {
        buildVariableHelper();
        bindForm();
        bindTextareas();
        loadTemplates();
    });

    window.loadTemplates = function loadTemplates() {
        $.get(API.BASE)
            .done(function (response) {
                if (response.success) {
                    templatesData = response.data || [];
                    renderEventFilterOptions();
                    renderTable();
                    updateStats();
                    updateDebugPanel(null);
                }
            })
            .fail(handleAjaxError);
    };

    window.renderTable = function renderTable() {
        const tbody = $('#templatesTableBody');
        const data = getFilteredData();
        tbody.empty();

        if (!data.length) {
            $('#emptyState').removeClass('d-none');
            return;
        }

        $('#emptyState').addClass('d-none');

        data.forEach(function (item) {
            const statusBadge = item.isActive
                ? '<span class="badge bg-success-subtle text-success">Active</span>'
                : '<span class="badge bg-secondary-subtle text-secondary">Disabled</span>';

            tbody.append(`
                <tr>
                    <td>${escapeHtml(String(item.id || '-'))}</td>
                    <td><code>${escapeHtml(item.eventCode || '')}</code></td>
                    <td><span class="badge bg-info-subtle text-info">${escapeHtml(item.targetRole || '')}</span></td>
                    <td class="text-truncate" style="max-width: 280px;">${escapeHtml(item.subject || '')}</td>
                    <td>${statusBadge}</td>
                    <td>${escapeHtml(item.updatedTime || '-')}</td>
                    <td class="text-end">
                        <button class="btn btn-sm btn-outline-secondary me-1" onclick="viewTemplate(${item.id})">View</button>
                        <button class="btn btn-sm btn-outline-primary me-1" onclick="openEditModal(${item.id})">Edit</button>
                        <button class="btn btn-sm btn-outline-info me-1" onclick="previewTemplate(${item.id})">Preview</button>
                        <button class="btn btn-sm ${item.isActive ? 'btn-outline-warning' : 'btn-outline-success'}" onclick="toggleStatus(${item.id}, ${item.isActive})">${item.isActive ? 'Disable' : 'Enable'}</button>
                    </td>
                </tr>
            `);
        });
    };

    window.openCreateModal = function openCreateModal() {
        $('#templateModalTitle').text('Create Template');
        $('#templateForm')[0].reset();
        $('#templateId').val('');
        $('#isActive').prop('checked', true);
        updateCounters();
        new bootstrap.Modal(document.getElementById('templateModal')).show();
    };

    window.openEditModal = function openEditModal(id) {
        $.get(`${API.BASE}/${id}`)
            .done(function (response) {
                if (!response.success || !response.data) return;
                const t = response.data;
                $('#templateModalTitle').text('Edit Template');
                $('#templateId').val(t.id);
                $('#eventCode').val(t.eventCode || '');
                $('#targetRole').val(t.targetRole || '');
                $('#subject').val(t.subject || '');
                $('#body').val(t.body || '');
                $('#isActive').prop('checked', t.isActive !== false);
                updateCounters();
                autoResize($('#subject')[0]);
                autoResize($('#body')[0]);
                updateDebugPanel(t);
                new bootstrap.Modal(document.getElementById('templateModal')).show();
            })
            .fail(handleAjaxError);
    };

    window.saveTemplate = function saveTemplate() {
        const id = $('#templateId').val();
        const payload = {
            eventCode: ($('#eventCode').val() || '').trim().toUpperCase(),
            targetRole: $('#targetRole').val(),
            subject: ($('#subject').val() || '').trim(),
            body: ($('#body').val() || '').trim(),
            isActive: $('#isActive').is(':checked')
        };

        if (!payload.eventCode || !payload.targetRole || !payload.subject || !payload.body) {
            showToast('error', 'Please fill all required fields');
            return;
        }

        const method = id ? 'PUT' : 'POST';
        const url = id ? `${API.BASE}/${id}` : API.BASE;

        $.ajax({ url: url, method: method, contentType: 'application/json', data: JSON.stringify(payload) })
            .done(function (response) {
                if (response.success) {
                    showToast('success', response.message || 'Template saved');
                    bootstrap.Modal.getInstance(document.getElementById('templateModal')).hide();
                    loadTemplates();
                }
            })
            .fail(handleAjaxError);
    };

    window.toggleStatus = function toggleStatus(id, currentState) {
        if (currentState && !confirm('Disable this template? This will stop automatic delivery for this role.')) {
            return;
        }
        $.ajax({ url: `${API.BASE}/${id}/toggle`, method: 'PATCH' })
            .done(function (response) {
                if (response.success) {
                    showToast('success', 'Template status updated');
                    loadTemplates();
                }
            })
            .fail(handleAjaxError);
    };

    window.previewTemplate = function previewTemplate(id) {
        $.ajax({ url: `${API.BASE}/${id}/preview`, method: 'POST', contentType: 'application/json', data: JSON.stringify({}) })
            .done(function (response) {
                if (!response.success || !response.data) return;
                $('#previewSubject').text(response.data.subject || '');
                $('#previewBody').html(response.data.body || '');
                updateDebugPanel(response.data);
                new bootstrap.Modal(document.getElementById('previewModal')).show();
            })
            .fail(handleAjaxError);
    };

    window.viewTemplate = function viewTemplate(id) {
        $.get(`${API.BASE}/${id}`)
            .done(function (response) {
                if (!response.success || !response.data) return;
                const t = response.data;
                const variables = (t.variables || []).map(v => `<span class="badge bg-light text-dark border me-1">${escapeHtml(v)}</span>`).join('');
                $('#viewContent').html(`
                    <div class="mb-2"><strong>Event Code:</strong> <code>${escapeHtml(t.eventCode || '')}</code></div>
                    <div class="mb-2"><strong>Target Role:</strong> ${escapeHtml(t.targetRole || '')}</div>
                    <div class="mb-2"><strong>Status:</strong> ${t.isActive ? 'Active' : 'Disabled'}</div>
                    <div class="mb-2"><strong>Subject:</strong><div class="border rounded p-2 bg-light">${escapeHtml(t.subject || '')}</div></div>
                    <div class="mb-2"><strong>Body:</strong><div class="border rounded p-2">${escapeHtml(t.body || '')}</div></div>
                    <div class="mb-1"><strong>Variables Used:</strong></div>
                    <div>${variables || '<span class="text-muted">None</span>'}</div>
                `);
                updateDebugPanel(t);
                new bootstrap.Modal(document.getElementById('viewModal')).show();
            })
            .fail(handleAjaxError);
    };

    window.filterTemplates = function filterTemplates() {
        renderTable();
    };

    window.resetFilters = function resetFilters() {
        $('#searchInput').val('');
        $('#roleFilter').val('');
        $('#eventFilter').val('');
        $('#statusFilter').val('');
        renderTable();
    };

    window.seedDefaultTemplates = function seedDefaultTemplates() {
        $.post(`${API.BASE}/seed-defaults`)
            .done(function (response) {
                if (response.success) {
                    showToast('success', 'Default template seed completed');
                    loadTemplates();
                }
            })
            .fail(handleAjaxError);
    };

    function bindForm() {
        $('#templateForm').on('submit', function (e) {
            e.preventDefault();
            saveTemplate();
        });

        $('#eventCode').on('input', function () {
            this.value = this.value.toUpperCase().replace(/[^A-Z0-9_]/g, '');
        });
    }

    function bindTextareas() {
        $('.auto-resize').on('input', function () {
            autoResize(this);
            updateCounters();
        });
        updateCounters();
    }

    function autoResize(element) {
        element.style.height = 'auto';
        element.style.height = `${element.scrollHeight}px`;
    }

    function updateCounters() {
        $('#subjectCount').text(($('#subject').val() || '').length);
        $('#bodyCount').text(($('#body').val() || '').length);
    }

    function updateStats() {
        const total = templatesData.length;
        const active = templatesData.filter(t => t.isActive).length;
        const disabled = total - active;
        const roles = new Set(templatesData.map(t => t.targetRole).filter(Boolean)).size;
        $('#totalCount').text(total);
        $('#activeCount').text(active);
        $('#disabledCount').text(disabled);
        $('#rolesCount').text(roles);
    }

    function renderEventFilterOptions() {
        const select = $('#eventFilter');
        const current = select.val();
        const events = Array.from(new Set(templatesData.map(t => t.eventCode).filter(Boolean))).sort();
        select.empty().append('<option value="">All Events</option>');
        events.forEach(function (code) {
            select.append(`<option value="${escapeHtml(code)}">${escapeHtml(code)}</option>`);
        });
        select.val(current);
    }

    function getFilteredData() {
        const search = ($('#searchInput').val() || '').toLowerCase().trim();
        const role = $('#roleFilter').val();
        const eventCode = $('#eventFilter').val();
        const status = $('#statusFilter').val();

        return templatesData.filter(function (t) {
            const matchesSearch = !search
                || (t.eventCode || '').toLowerCase().includes(search)
                || (t.subject || '').toLowerCase().includes(search)
                || (t.body || '').toLowerCase().includes(search);
            const matchesRole = !role || t.targetRole === role;
            const matchesEvent = !eventCode || t.eventCode === eventCode;
            const matchesStatus = !status || String(t.isActive) === status;
            return matchesSearch && matchesRole && matchesEvent && matchesStatus;
        });
    }

    function buildVariableHelper() {
        const container = $('#variableHelper');
        container.empty();
        COMMON_VARIABLES.forEach(function (variableName) {
            const token = `[[\${${variableName}}]]`.replace('\\', '');
            container.append(`<button type="button" class="btn btn-sm btn-outline-dark" data-token="${token}">${variableName}</button>`);
        });

        container.on('click', 'button', function () {
            const token = $(this).data('token');
            navigator.clipboard.writeText(token).then(function () {
                showToast('success', `Copied ${token}`);
            }).catch(function () {
                showToast('warning', 'Clipboard not available in this browser');
            });
        });
    }

    function updateDebugPanel(template) {
        if (!template) {
            $('#debugPanel').text(`Templates loaded: ${templatesData.length}\nSelect a template to inspect details and variables.`);
            return;
        }

        const debug = {
            id: template.id,
            eventCode: template.eventCode,
            targetRole: template.targetRole,
            isActive: template.isActive,
            subjectLength: (template.subject || '').length,
            bodyLength: (template.body || '').length,
            variables: template.variables || []
        };
        $('#debugPanel').text(JSON.stringify(debug, null, 2));
    }

    function showToast(message, type) {
        if (typeof window.showToast === 'function') {
            window.showToast(type || 'info', message);
        } else {
            console.log(`[${type || 'info'}] ${message}`);
        }
    }

    function handleAjaxError(xhr) {
        const msg = xhr?.responseJSON?.message || xhr?.responseJSON?.error || 'Request failed. Please try again.';
        showToast('error', msg);
    }

    function escapeHtml(value) {
        return String(value)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;');
    }
})();
