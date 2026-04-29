/**
 * Notification Preferences Module
 * Path: /js/org/notifications/preferences.js
 */

(function () {
    'use strict';

    const API = {
        EVENTS: `${APP.CONTEXT_PATH}/api/notifications/events`,
        PREFERENCES: `${APP.CONTEXT_PATH}/api/notifications/preferences`,
        SET_PREFERENCE: `${APP.CONTEXT_PATH}/api/notifications/preference`
    };

    let allEvents = [];
    let preferences = {};

    // ===============================
    // Utility Functions
    // ===============================

    function escapeXml(str) {
        if (typeof str !== 'string') return str;

        return str
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#039;');
    }

    function showLoader() {
        document.getElementById('loadingOverlay')?.classList.remove('d-none');
    }

    function hideLoader() {
        document.getElementById('loadingOverlay')?.classList.add('d-none');
    }

    // ===============================
    // Init
    // ===============================

    document.addEventListener('DOMContentLoaded', async () => {
        await loadData();
    });

    async function loadData() {
        showLoader();

        try {
            await Promise.all([
                loadEvents(),
                loadPreferences()
            ]);

            renderTable();

        } catch (error) {
            console.error(error);
            showToast('error', 'Failed to load notification preferences');
        } finally {
            hideLoader();
        }
    }

    // ===============================
    // API Calls
    // ===============================

    async function loadEvents() {
        const response = await axios.get(API.EVENTS);

        if (response.data?.success) {
            allEvents = response.data.data.filter(event => event.isActive === true);
        }
    }

    async function loadPreferences() {
        try {
            const response = await axios.get(`${API.PREFERENCES}/${APP.ORG_ID}`);

            if (response.data?.success) {
                response.data.data.forEach(pref => {
                    preferences[pref.eventId] = pref.emailEnabled;
                });
            }

        } catch (error) {
            console.warn('No preferences found yet');
        }
    }

    // ===============================
    // Render
    // ===============================

    function renderTable() {
        const tbody = document.getElementById('preferencesBody');

        if (!tbody) return;

        if (!allEvents.length) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="4" class="text-center py-4">
                        <i class="fa fa-inbox fa-2x text-muted mb-2 d-block"></i>
                        No notification events configured
                    </td>
                </tr>
            `;
            return;
        }

        tbody.innerHTML = allEvents.map(event => {

            const checked = preferences[event.id] === true ? 'checked' : '';

            return `
                <tr>
                    <td>
                        <code class="bg-light px-2 py-1 rounded">
                            ${escapeXml(event.eventCode)}
                        </code>
                    </td>

                    <td>
                        <span class="badge bg-primary-subtle text-primary">
                            ${escapeXml(event.module)}
                        </span>
                    </td>

                    <td class="text-muted small">
                        ${escapeXml(event.description || 'No description')}
                    </td>

                    <td class="text-center">
                        <div class="form-check form-switch d-inline-block">
                            <input 
                                class="form-check-input preference-toggle"
                                type="checkbox"
                                data-event-id="${event.id}"
                                ${checked}
                            >
                        </div>
                    </td>
                </tr>
            `;
        }).join('');

        bindEvents();
    }

    // ===============================
    // Bind Events
    // ===============================

    function bindEvents() {
        document.querySelectorAll('.preference-toggle').forEach(toggle => {
            toggle.addEventListener('change', handleToggle);
        });
    }

    // ===============================
    // Toggle Preference
    // ===============================

    async function handleToggle(e) {
        const checkbox = e.target;
        const eventId = Number(checkbox.dataset.eventId);
        const enabled = checkbox.checked;

        // optimistic update
        preferences[eventId] = enabled;

        checkbox.disabled = true;

        try {
            await axios.post(API.SET_PREFERENCE, {
                orgId: APP.ORG_ID,
                eventId: eventId,
                emailEnabled: enabled
            });

            showToast('success', 'Preference updated successfully');

        } catch (error) {

            // revert UI
            preferences[eventId] = !enabled;
            checkbox.checked = !enabled;

            console.error(error);
            showToast('error', 'Failed to update preference');

        } finally {
            checkbox.disabled = false;
        }
    }

})();