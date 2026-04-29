/**
 * Notification Events Module (SuperAdmin)
 * Path: /js/superadmin/notifications/events.js
 */

(function () {
    'use strict';

    const API = {
        EVENTS: `${APP.CONTEXT_PATH}/api/notifications/events`,
        DELETE: (id) => `${APP.CONTEXT_PATH}/api/notifications/events/${id}`
    };

    let eventsData = [];
    let deleteEventId = null;

    document.addEventListener('DOMContentLoaded', () => {
        loadEvents();
        initForm();
        initDeleteModal();
    });

    async function loadEvents() {
        try {
            showLoading(true);
            const response = await axios.get(API.EVENTS);

            if (response.data?.success) {
                console.log(response.data.data);
                eventsData = response.data.data;
                renderEvents();
            }
        } catch (error) {
            console.error('Failed to load events:', error);
            showToast('error', 'Failed to load notification events');
        } finally {
            showLoading(false);
        }
    }

    // ✅ Utility: Escape XML/HTML for text content
    function escapeXml(str) {
        if (typeof str !== 'string') return str;
        return str
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&apos;');
    }

    // ✅ Utility: Escape XML/HTML for attribute values (single quotes)
    function escapeXmlAttribute(str) {
        if (typeof str !== 'string') return str;
        return str
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#x27;');
    }

    function renderEvents() {
        const tbody = document.getElementById('eventsBody');
        const emptyState = document.getElementById('emptyState');
        const searchTerm = document.getElementById('searchInput').value.toLowerCase();
        const moduleFilter = document.getElementById('moduleFilter').value;
        const statusFilter = document.getElementById('statusFilter').value;

        const filtered = eventsData.filter(event => {

            const matchesSearch = !searchTerm ||
                (event.eventCode || '').toLowerCase().includes(searchTerm) ||
                (event.description || '').toLowerCase().includes(searchTerm) ||
                (event.module || '').toLowerCase().includes(searchTerm);
            const matchesModule = !moduleFilter || event.module === moduleFilter;
            const matchesStatus = !statusFilter || String(event.isActive) === statusFilter;
            return matchesSearch && matchesModule && matchesStatus;
        });

        if (!filtered.length) {
            tbody.innerHTML = '';
            emptyState.classList.remove('d-none');
            return;
        }

        emptyState.classList.add('d-none');

        // ✅ FIX: Use template literals (backticks) instead of JSX syntax
        tbody.innerHTML = filtered.map(event => `
            <tr>
                <td>
                    <code class="bg-light px-2 py-1 rounded small">${escapeXml(event.eventCode)}</code>
                </td>
                <td>
                    <span class="badge bg-primary-subtle text-primary">
                        ${escapeXml(event.module)}
                    </span>
                </td>
                <td class="text-muted small">
                    ${escapeXml(event.description || '—')}
                </td>
                <td>
                    <span class="badge ${event.isActive !== false ? 'bg-success-subtle text-success' : 'bg-secondary-subtle text-secondary'}">
                        ${event.isActive !== false ? 'Active' : 'Inactive'}
                    </span>
                </td>
                <td class="text-end">
                    <button class="btn btn-sm btn-outline-primary me-1"
                        onclick="openEditModal('${escapeXmlAttribute(event.id)}')"
                        title="Edit">
                        <i class="fa fa-pen"></i>
                    </button>

                    <button class="btn btn-sm btn-outline-warning" onclick="toggleStatus('${escapeXmlAttribute(event.id)}', ${event.isActive !== false})" title="${event.isActive !== false ? 'Deactivate' : 'Activate'} Event">
                        <i class="fa fa-eye${event.isActive === false ? '-slash' : ''}"></i>
                    </button>

                    <button class="btn btn-sm btn-outline-danger"
                        onclick="confirmDelete('${escapeXmlAttribute(event.id)}')"
                        title="Delete">
                        <i class="fa fa-trash"></i>
                    </button>
                </td>
            </tr>
        `).join('');
    }

    function formatDate(dateStr) {
        if (!dateStr) return '—';
        const date = new Date(dateStr);
        return date.toLocaleDateString('en-US', {
            year: 'numeric', month: 'short', day: 'numeric'
        });
    }

    window.filterEvents = function filterEvents() {
        renderEvents();
    }

    // ============ MODAL & FORM HANDLING ============

    function initForm() {
        const form = document.getElementById('eventForm');
        const eventIdInput = document.getElementById('formEventCode');

        // Auto-format event ID as user types
        eventIdInput.addEventListener('input', (e) => {
            e.target.value = e.target.value.toUpperCase().replace(/[^A-Z_]/g, '');
        });

        form.addEventListener('submit', async (e) => {
            e.preventDefault();

            if (!form.checkValidity()) {
                e.stopPropagation();
                form.classList.add('was-validated');
                return;
            }

            const payload = {
                eventCode: document.getElementById('formEventCode').value.trim(),
                module: document.getElementById('formModule').value,
                description: document.getElementById('formDescription').value.trim(),
                isActive: document.getElementById('formIsActive').checked
            };

            const isEdit = document.getElementById('eventId').value;
            const url = isEdit
                ? `${API.EVENTS}/${isEdit}`
                : API.EVENTS;
            const method = isEdit ? 'put' : 'post';

            try {
                showLoading(true);
                const response = await axios[method](url, payload);

                if (response.data?.success) {
                    const modalEl = document.getElementById('eventModal');
                    const modal = bootstrap.Modal.getInstance(modalEl) || new bootstrap.Modal(modalEl);
                    modal.hide();
                    // ✅ FIX: Correct parameter order - message first, then type
                    showToast('Event ' + (isEdit ? 'updated' : 'created') + ' successfully', 'success');
                    loadEvents();
                    form.reset();
                    form.classList.remove('was-validated');
                }
            } catch (error) {
                const message = error.response?.data?.message ||
                    'Failed to ' + (isEdit ? 'update' : 'create') + ' event';
                showToast(message, 'error');
                showErrorModal('Save Failed', message);
            } finally {
                showLoading(false);
            }
        });
    }

    window.openCreateModal = function () {
        document.getElementById('modalTitle').textContent = 'Add Notification Event';
        document.getElementById('eventForm').reset();
        document.getElementById('eventId').value = '';
        document.getElementById('formEventCode').disabled = false;
        document.getElementById('formEventCode').value = '';
        document.getElementById('formIsActive').checked = true;
        document.querySelector('#eventForm').classList.remove('was-validated');
    };

    window.openEditModal = function (eventId) {
        const event = eventsData.find(e => String(e.id) === String(eventId));
        if (!event) return;

        document.getElementById('modalTitle').textContent = 'Edit Notification Event';
        document.getElementById('eventId').value = event.id;
        document.getElementById('formEventCode').value = event.eventCode;
        document.getElementById('formEventCode').disabled = true; // Can't change ID
        document.getElementById('formModule').value = event.module;
        document.getElementById('formDescription').value = event.description || '';
        document.getElementById('formIsActive').checked = event.isActive !== false;

        new bootstrap.Modal(document.getElementById('eventModal')).show();
    };

    // ============ DELETE HANDLING ============

    function initDeleteModal() {
        const confirmBtn = document.getElementById('confirmDeleteBtn');
        if (confirmBtn) {
            confirmBtn.addEventListener('click', async () => {
                if (!deleteEventId) return;

                try {
                    showLoading(true);
                    const response = await axios.delete(API.DELETE(deleteEventId));

                    if (response.data?.success) {
                        const modalEl = document.getElementById('deleteModal');
                        const modal = bootstrap.Modal.getInstance(modalEl) || new bootstrap.Modal(modalEl);
                        modal.hide();
                        showToast('success', 'Event deleted successfully');
                        loadEvents();
                    }
                } catch (error) {
                    const message = error.response?.data?.message || 'Failed to delete event';
                    showToast('error', message);
                } finally {
                    showLoading(false);
                    deleteEventId = null;
                }
            });
        }
    }

    window.confirmDelete = function (eventId) {
        deleteEventId = eventId;
        const deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));
        deleteModal.show();
    };


    window.toggleStatus = async function (eventId, currentState) {
        try {
            showLoading(true);
            const response = await axios.patch(`${API.EVENTS}/${eventId}/toggle`);

            if (response.data?.success) {
                showToast('success', 'Event status updated successfully');
                loadEvents(); // reload to refresh UI
            }
        } catch (error) {
            const message = error.response?.data?.message || 'Failed to update status';
            showToast(message, 'error');
        } finally {
            showLoading(false);
        }
    };

    // ============ UTILITIES ============

    function showLoading(show) {
        const overlay = document.getElementById('loadingOverlay');
        if (overlay) {
            if (show) {
                overlay.classList.remove('d-none');
                document.body.style.overflow = 'hidden';
            } else {
                overlay.classList.add('d-none');
                document.body.style.overflow = '';
            }
        }
    }

    function showToast(message, type = 'info') {
        if (typeof window.showToast === 'function') {
            window.showToast(message, type);
        } else {
            console.log(`[${type.toUpperCase()}] ${message}`);
        }
    }

    function showErrorModal(title, message) {
        if (typeof window.showErrorModal === 'function') {
            window.showErrorModal(title, message);
        } else if (typeof Swal !== 'undefined') {
            Swal.fire({
                icon: 'error',
                title: title,
                text: message,
                confirmButtonText: 'OK',
                customClass: { confirmButton: 'btn-primary-hrms' }
            });
        } else {
            alert(`${title}: ${message}`);
        }
    }

})();