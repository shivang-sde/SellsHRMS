// monitorAPI.js - Fixed version with proper waiting

(function () {
    let attempts = 0;
    const maxAttempts = 800; // 10 seconds max

    const initMonitorAPI = function () {
        attempts++;

        // Check if dependencies are ready AND initialized
        const hasApp = typeof window.APP !== 'undefined' && window.APP !== null;
        const hasApiClient = typeof apiClient !== 'undefined' && apiClient !== null && typeof apiClient.get === 'function';

        console.log(`Attempt ${attempts}: hasApp=${hasApp}, hasApiClient=${hasApiClient}`);

        if (!hasApp || !hasApiClient) {
            if (attempts < maxAttempts) {
                setTimeout(initMonitorAPI, 100);
            } else {
                console.error('Failed to load dependencies after', maxAttempts, 'attempts');
                console.log('window.APP:', window.APP);
                console.log('apiClient:', typeof apiClient);
            }
            return;
        }

        console.log('Dependencies ready! Initializing monitorAPI...');

        const organisationId = window.APP.ORG_ID;
        const userId = window.APP.USER_ID;

        const monitorAPI = {
            // ==================== DASHBOARD ====================
            getDashboard() {
                return apiClient.get(
                    `/monitor/dashboard?organisationId=${organisationId}&userId=${userId}`
                );
            },

            getGroupSummaries() {
                return apiClient.get(`/monitor/groups/summary?organisationId=${organisationId}`);
            },
            getUrlResponseTrend(urlId) {
                return apiClient.get(`/monitor/analytics/url/${urlId}/response-trend`);
            },
            getGroupResponseTrend(groupId) {
                return apiClient.get(`/monitor/analytics/group/${groupId}/response-trend`);
            },
            getUptimeTrend(urlId, days = 30) {
                return apiClient.get(`/monitor/analytics/url/${urlId}/uptime-trend?days=${days}`);
            },
            getSlowestUrls(limit = 5) {
                return apiClient.get(`/monitor/slowest-urls?organisationId=${organisationId}&limit=${limit}`);
            },

            // ==================== URLS ====================
            getUrls(page = 1, limit = 10, status = '', search = '') {
                let url = `/monitor/urls?organisationId=${organisationId}&userId=${userId}&page=${page}&limit=${limit}`;
                if (status) url += `&status=${status}`;
                if (search) url += `&search=${encodeURIComponent(search)}`;
                return apiClient.get(url);
            },

            getUrl(id) {
                return apiClient.get(
                    `/monitor/urls/${id}?organisationId=${organisationId}&userId=${userId}`
                );
            },

            createUrl(data) {
                return apiClient.post(
                    `/monitor/urls?organisationId=${organisationId}&createdBy=${userId}`,
                    data
                );
            },

            updateUrl(id, data) {
                return apiClient.put(
                    `/monitor/urls/${id}?organisationId=${organisationId}&userId=${userId}`,
                    data
                );
            },

            deleteUrl(id) {
                return apiClient.delete(
                    `/monitor/urls/${id}?organisationId=${organisationId}&userId=${userId}`
                );
            },

            checkNow(id) {
                return apiClient.post(
                    `/monitor/urls/${id}/check?organisationId=${organisationId}&userId=${userId}`
                );
            },

            toggleUrl(id) {
                return apiClient.post(
                    `/monitor/urls/${id}/toggle?organisationId=${organisationId}&userId=${userId}`
                );
            },

            // ==================== GROUPS ====================
            getGroups() {
                return apiClient.get(
                    `/monitor/groups?organisationId=${organisationId}&userId=${userId}`
                );
            },

            getGroup(id) {
                return apiClient.get(
                    `/monitor/groups/${id}?organisationId=${organisationId}&userId=${userId}`
                );
            },

            createGroup(data) {
                return apiClient.post(
                    `/monitor/groups?organisationId=${organisationId}&createdBy=${userId}`,
                    data
                );
            },

            updateGroup(id, data) {
                return apiClient.put(
                    `/monitor/groups/${id}?organisationId=${organisationId}&userId=${userId}`,
                    data
                );
            },

            deleteGroup(id) {
                return apiClient.delete(
                    `/monitor/groups/${id}?organisationId=${organisationId}&userId=${userId}`
                );
            },
            getAvailableUrlsForGroup(groupId, search = '') {
                let url = `/monitor/groups/${groupId}/available-urls?organisationId=${organisationId}&userId=${userId}`;
                if (search) {
                    url += `&search=${encodeURIComponent(search)}`;
                }
                return apiClient.get(url);
            },

            addUrlToGroup(groupId, urlId) {
                return apiClient.post(
                    `/monitor/groups/${groupId}/urls/${urlId}?organisationId=${organisationId}&userId=${userId}`
                );
            },

            removeUrlFromGroup(groupId, urlId) {
                return apiClient.delete(
                    `/monitor/groups/${groupId}/urls/${urlId}?organisationId=${organisationId}&userId=${userId}`
                );
            },

            addMemberToGroup(groupId, memberUserId) {
                return apiClient.post(
                    `/monitor/groups/${groupId}/members/${memberUserId}?organisationId=${organisationId}&addedBy=${userId}`
                );
            },

            removeMemberFromGroup(groupId, memberUserId) {
                return apiClient.delete(
                    `/monitor/groups/${groupId}/members/${memberUserId}?organisationId=${organisationId}&userId=${userId}`
                );
            },

            // ==================== INCIDENTS ====================
            getIncidents(page = 1, limit = 20, resolved = null) {
                let url = `/monitor/incidents?organisationId=${organisationId}&userId=${userId}&page=${page}&limit=${limit}`;
                if (resolved !== null) url += `&resolved=${resolved}`;
                return apiClient.get(url);
            }
        };

        window.monitorAPI = monitorAPI;
        console.log('monitorAPI successfully initialized!');

        // Dispatch an event to notify that monitorAPI is ready
        window.dispatchEvent(new Event('monitorAPIReady'));
    };

    // Start initialization
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initMonitorAPI);
    } else {
        initMonitorAPI();
    }
})();