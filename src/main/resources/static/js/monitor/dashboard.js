// dashboard.js - Fixed duplicate declaration issue

// Only declare if not already declared
var responseTimeChart = null;

let advancedChart = null;
let uptimeChart = null;
let groupList = [];
let urlList = [];

console.log("dashboard.js");

$(document).ready(function () {
    // Make sure monitorAPI is ready
    if (typeof window.monitorAPI !== 'undefined' && window.monitorAPI) {
        loadDashboard();
        setInterval(loadDashboard, 60000);
    } else {
        // Wait for monitorAPI
        let attempts = 0;
        const checkInterval = setInterval(function () {
            attempts++;
            if (typeof window.monitorAPI !== 'undefined' && window.monitorAPI) {
                clearInterval(checkInterval);
                loadDashboard();
                setInterval(loadDashboard, 60000);
            } else if (attempts >= 50) {
                clearInterval(checkInterval);
                console.error('monitorAPI failed to load');
            }
        }, 100);
    }
});


$(document).ready(() => {
    $('#analyticsType').change(() => {
        const type = $('#analyticsType').val();
        $('#analyticsGroupSelect').toggle(type === 'group');
        $('#analyticsUrlSelect').toggle(type === 'url');
        refreshAdvancedChart();
    });
    $('#analyticsGroupSelect, #analyticsUrlSelect').change(refreshAdvancedChart);

    // Add uptime trend button in URL table rows (modify updateUrls)
});


// Load initial advanced analytics
async function loadAdvancedAnalytics() {
    // Load group summary cards
    const groups = await monitorAPI.getGroupSummaries();
    renderGroupSummary(groups);

    // Load slowest URLs
    const slowest = await monitorAPI.getSlowestUrls(5);
    renderSlowestUrls(slowest);

    // Populate dropdowns for group and URL
    groupList = groups;
    urlList = (await monitorAPI.getUrls(1, 100)).urls || [];
    populateSelectors();

    // Initial chart (overall)
    await refreshAdvancedChart();
}

function renderGroupSummary(groups) {
    const container = $('#groupSummaryContainer');
    if (!groups || groups.length === 0) {
        container.html('<div class="col-12 text-muted">No groups yet.</div>');
        return;
    }
    let html = '';
    groups.forEach(g => {
        let statusClass = 'success';
        if (g.avgUptime < 95) statusClass = 'warning';
        if (g.avgUptime < 85) statusClass = 'danger';
        html += `
            <div class="col-md-4 col-lg-3">
                <div class="card border-0 shadow-sm h-100">
                    <div class="card-body">
                        <h6 class="fw-bold">${escapeHtml(g.groupName)}</h6>
                        <div class="small text-muted">${g.totalUrls} URLs · ${g.totalMembers} members</div>
                        <div class="mt-2">
                            <span class="badge bg-${statusClass}">Avg uptime ${g.avgUptime}%</span>
                            ${g.activeIncidents > 0 ? `<span class="badge bg-danger ms-1">${g.activeIncidents} incidents</span>` : ''}
                        </div>
                    </div>
                </div>
            </div>
        `;
    });
    container.html(html);
}

function populateSelectors() {
    const groupSelect = $('#analyticsGroupSelect');
    groupSelect.empty().append('<option value="">Select Group</option>');
    groupList.forEach(g => groupSelect.append(`<option value="${g.groupId}">${escapeHtml(g.groupName)}</option>`));

    const urlSelect = $('#analyticsUrlSelect');
    urlSelect.empty().append('<option value="">Select URL</option>');
    urlList.forEach(u => urlSelect.append(`<option value="${u.id}">${escapeHtml(u.name)}</option>`));
}

async function refreshAdvancedChart() {
    const type = $('#analyticsType').val();
    let data = null;
    let label = '';
    if (type === 'overall') {
        // Use existing responseTimeData from dashboard
        data = dashboardResponseTimeData; // store globally when loadDashboard runs
        label = 'Overall Response Time (ms)';
    } else if (type === 'group') {
        const groupId = $('#analyticsGroupSelect').val();
        if (!groupId) { showToast('warning', 'Please select a group'); return; }
        data = await monitorAPI.getGroupResponseTrend(groupId);
        label = `Group Response Trend (${$('#analyticsGroupSelect option:selected').text()})`;
    } else if (type === 'url') {
        const urlId = $('#analyticsUrlSelect').val();
        if (!urlId) { showToast('warning', 'Please select a URL'); return; }
        data = await monitorAPI.getUrlResponseTrend(urlId);
        label = `URL Response Trend - ${$('#analyticsUrlSelect option:selected').text()}`;
    }
    if (data && data.labels && data.values) {
        updateAdvancedChart(data.labels, data.values, label);
    }
}
let dashboardResponseTimeData = null;   // store from loadDashboard

async function loadDashboard() {
    try {
        const response = await window.monitorAPI.getDashboard();
        if (response && response.success !== false) {
            const data = response.data || response;

            console.log("dashboard data", data);
            if (data.stats) updateStats(data.stats);
            if (data.activeIncidents) updateIncidents(data.activeIncidents);
            if (data.recentUrls) updateUrls(data.recentUrls);
            if (data.responseTimeData) updateResponseTimeChart(data.responseTimeData);

            await loadAdvancedAnalytics();
        }
    } catch (error) {
        console.error('Failed to load dashboard:', error);
    }
}

function updateAdvancedChart(labels, values, labelText) {
    const ctx = document.getElementById('advancedChart').getContext('2d');
    if (advancedChart) advancedChart.destroy();
    advancedChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: labelText,
                data: values,
                borderColor: '#8b5cf6',
                backgroundColor: 'rgba(139, 92, 246, 0.1)',
                tension: 0.3,
                fill: true
            }]
        },
        options: { responsive: true, maintainAspectRatio: true }
    });
}

function renderSlowestUrls(slowest) {
    const tbody = $('#slowestUrlsTable tbody');
    tbody.empty();
    if (!slowest || slowest.length === 0) {
        tbody.html('<tr><td colspan="3" class="text-muted">No data yet</td></tr>');
        return;
    }
    slowest.forEach(u => {
        tbody.append(`
            <tr>
                <td>${escapeHtml(u.name)}</td>
                <td><small class="text-muted">${escapeHtml(u.url)}</small></td>
                <td>${Math.round(u.avgResponseTime)} ms</td>
            </tr>
        `);
    });
}

// Show uptime trend modal when clicking on a URL row (optional)
async function showUptimeTrend(urlId, urlName) {
    const data = await monitorAPI.getUptimeTrend(urlId, 30);
    const ctx = document.getElementById('uptimeChart').getContext('2d');
    if (uptimeChart) uptimeChart.destroy();
    uptimeChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: data.labels,
            datasets: [{
                label: `Uptime % - ${urlName}`,
                data: data.values,
                borderColor: '#10b981',
                backgroundColor: 'rgba(16, 185, 129, 0.1)',
                tension: 0.2,
                fill: true
            }]
        },
        options: {
            responsive: true,
            scales: { y: { min: 0, max: 100, title: { display: true, text: 'Uptime %' } } }
        }
    });
    $('#uptimeModal').modal('show');
}

function updateStats(stats) {
    $('#totalUrls').text(stats.totalUrls || 0);
    $('#upCount').text(stats.upCount || 0);
    $('#downCount').text(stats.downCount || 0);
    $('#avgUptime').text((stats.avgUptime || 100) + '%');
}

function updateIncidents(incidents) {
    const container = $('#incidentsList');
    const card = $('#incidentsCard');

    if (!incidents || incidents.length === 0) {
        card.hide();
        return;
    }

    card.show();
    container.empty();

    incidents.forEach(incident => {
        container.append(`
            <div class="alert alert-danger rounded mb-3">
                <div class="d-flex justify-content-between align-items-center">
                    <div class="text-black">
                        <strong>${escapeHtml(incident.urlName)}</strong>
                        <div class="small text-muted">${escapeHtml(incident.url)}</div>
                        <div class="small mt-1">
                            <i class="fa-regular fa-clock"></i> Down since: ${formatDate(incident.startedAt)}
                            ${incident.cause ? `<br><i class="fa-solid fa-circle-info"></i> ${escapeHtml(incident.cause)}` : ''}
                        </div>
                    </div>
                    <span class="badge bg-danger animate-pulse">DOWN</span>
                </div>
            </div>
        `);
    });
}

function updateUrls(urls) {
    const tbody = $('#urlsTableBody');
    tbody.empty();

    if (!urls || urls.length === 0) {
        tbody.html('<tr><td colspan="7" class="text-center">No URLs monitored yet</td></tr>');
        return;
    }

    urls.forEach(url => {
        const statusClass = url.currentStatus === 'up' ? 'success' : (url.currentStatus === 'down' ? 'danger' : 'warning');
        tbody.append(`
            <tr>
                <td><span class="badge bg-${statusClass}">${url.currentStatus}</span></td>
                <td><strong>${escapeHtml(url.name)}</strong></td>
                <td><small class="text-muted">${escapeHtml(url.url)}</small></td>
                <td>${url.uptimePercentage}%</td>
                <td>${url.lastResponseTime ? url.lastResponseTime + 'ms' : '--'}</td>
                <td><small>${url.lastCheckedAt ? formatDate(url.lastCheckedAt) : 'Never'}</small></td>
                <td>
                <button class="btn btn-sm btn-outline-info" onclick="showUptimeTrend('${url.id}', '${escapeHtml(url.name)}')">
                    <i class="fa-solid fa-chart-line"></i> Trend
                </button>
                    <button class="btn btn-sm btn-outline-primary" onclick="checkNow('${url.id}')"><i class="fa-solid fa-play"></i></button>
                 </td>
            </tr>
        `);
    });
}

function updateResponseTimeChart(data) {

    console.log("response time data", data);
    if (!data || !data.labels || !data.values || data.labels.length === 0) return;


    const ctx = document.getElementById('responseTimeChart').getContext('2d');

    // Destroy existing chart if it exists
    if (responseTimeChart) {
        responseTimeChart.destroy();
        responseTimeChart = null;
    }

    responseTimeChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: data.labels,
            datasets: [{
                label: 'Response Time (ms)',
                data: data.values,
                borderColor: '#0ea5e9',
                backgroundColor: 'rgba(14, 165, 233, 0.1)',
                tension: 0.4,
                fill: true
            }]
        },
        options: { responsive: true, maintainAspectRatio: true }
    });
}

// Helper functions
function formatDate(dateStr) {
    if (!dateStr) return '--';
    const date = new Date(dateStr);
    return date.toLocaleString('en-IN', {
        day: '2-digit',
        month: 'short',
        hour: '2-digit',
        minute: '2-digit'
    });
}

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/[&<>]/g, m => m === '&' ? '&amp;' : (m === '<' ? '&lt;' : '&gt;'));
}

// API action functions
async function checkNow(urlId) {
    try {
        const result = await window.monitorAPI.checkNow(urlId);
        if (result && result.isUp) {
            alert(`✅ UP - ${result.statusCode} (${result.responseTime}ms)`);
        } else {
            alert(`🔴 DOWN - ${result?.error || 'Connection failed'}`);
        }
        loadDashboard();
    } catch (error) {
        alert('Check failed');
    }
}

// FIXED: Use monitorAPI
async function toggleUrl(urlId) {
    try {
        const result = await monitorAPI.toggleUrl(urlId);
        if (result) {
            showToast('success', 'URL status toggled');
            loadDashboard();
        }
    } catch (error) {
        showToast('error', 'Failed to toggle status: ' + (error.message || 'Unknown error'));
    }
}

// FIXED: Use monitorAPI
async function deleteUrl(urlId, urlName) {
    const confirmed = await confirmDialog(`Delete "${urlName}"?`, 'All check history will be lost.');
    if (!confirmed) return;

    try {
        await monitorAPI.deleteUrl(urlId);
        showToast('success', 'URL deleted successfully');
        loadDashboard();
    } catch (error) {
        showToast('error', 'Failed to delete URL: ' + (error.message || 'Unknown error'));
    }
}

// FIXED: Use monitorAPI
async function showUrlDetail(urlId) {
    try {
        const data = await monitorAPI.getUrl(urlId);
        if (data) {
            renderUrlDetailModal(data);
            $('#urlDetailModal').modal('show');
        }
    } catch (error) {
        showToast('error', 'Failed to load URL details: ' + (error.message || 'Unknown error'));
    }
}

function renderUrlDetailModal(data) {
    const url = data.url;
    const checks = data.checks || [];
    const incidents = data.incidents || [];
    const groups = data.groups || [];

    const statusClass = url.currentStatus === 'up' ? 'success' : (url.currentStatus === 'down' ? 'danger' : 'warning');

    let checksHtml = '';
    checks.slice(0, 20).forEach(check => {
        checksHtml += `
            <tr>
                <td>${check.isUp ? '<i class="fa-solid fa-check-circle text-success"></i>' : '<i class="fa-solid fa-times-circle text-danger"></i>'} </td>
                <td>${check.statusCode || '--'} </td>
                <td>${check.responseTime || '--'}ms </td>
                <td>${formatDate(check.checkedAt)} </td>
                <td class="text-truncate" style="max-width: 200px;">${escapeHtml(check.error || '')} </td>
             </tr>
        `;
    });

    let incidentsHtml = '';
    incidents.forEach(incident => {
        incidentsHtml += `
            <div class="border-start border-4 ${incident.resolved ? 'border-success' : 'border-danger'} p-3 mb-2 bg-light rounded">
                <div class="d-flex justify-content-between">
                    <div>
                        <span class="badge ${incident.resolved ? 'bg-success' : 'bg-danger'} mb-2">
                            ${incident.resolved ? 'RESOLVED' : 'ACTIVE'}
                        </span>
                        <div class="small text-muted">Started: ${formatDate(incident.startedAt)}</div>
                        ${incident.endedAt ? `<div class="small text-muted">Ended: ${formatDate(incident.endedAt)}</div>` : ''}
                        ${incident.cause ? `<div class="small mt-2">Cause: ${escapeHtml(incident.cause)}</div>` : ''}
                    </div>
                    ${incident.durationSeconds ? `<div class="text-end"><strong>${formatDuration(incident.durationSeconds)}</strong></div>` : ''}
                </div>
            </div>
        `;
    });

    const html = `
        <div class="row">
            <div class="col-md-6">
                <div class="card bg-light mb-3">
                    <div class="card-body">
                        <h6 class="card-subtitle mb-2 text-muted">URL Details</h6>
                        <h4 class="card-title">${escapeHtml(url.name)}</h4>
                        <p class="card-text text-muted small">${escapeHtml(url.url)}</p>
                        <hr>
                        <div class="row">
                            <div class="col-6">
                                <small class="text-muted">Status</small>
                                <div><span class="badge bg-${statusClass}">${url.currentStatus.toUpperCase()}</span></div>
                            </div>
                            <div class="col-6">
                                <small class="text-muted">Uptime</small>
                                <div><strong>${url.uptimePercentage}%</strong></div>
                            </div>
                            <div class="col-6 mt-2">
                                <small class="text-muted">Check Interval</small>
                                <div>${formatInterval(url.checkInterval)}</div>
                            </div>
                            <div class="col-6 mt-2">
                                <small class="text-muted">Created</small>
                                <div>${formatDate(url.createdAt)}</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-6">
                <div class="card bg-light mb-3">
                    <div class="card-body">
                        <h6 class="card-subtitle mb-2 text-muted">Groups</h6>
                        ${groups.length > 0 ? groups.map(g => `<span class="badge bg-secondary me-1">${escapeHtml(g.name)}</span>`).join('') : '<span class="text-muted">No groups</span>'}
                    </div>
                </div>
            </div>
        </div>
        
        <h6 class="mt-3">Recent Checks</h6>
        <div class="table-responsive">
            <table class="table table-sm">
                <thead>
                    <tr><th>Status</th><th>Code</th><th>Response</th><th>Time</th><th>Error</th> </tr>
                </thead>
                <tbody>
                    ${checksHtml || '<tr><td colspan="5" class="text-center">No checks yet</td></tr>'}
                </tbody>
             </table>
        </div>
        
        ${incidents.length > 0 ? `
            <h6 class="mt-3">Incident History</h6>
            ${incidentsHtml}
        ` : ''}
    `;

    $('#urlDetailBody').html(html);
    $('#urlDetailTitle').text(url.name);
}

function showAddUrlModal() {
    $('#urlModalTitle').text('Add URL');
    $('#urlId').val('');
    $('#urlForm')[0].reset();
    $('#urlMethod').val('GET');
    $('#checkInterval').val('300');
    $('#timeout').val('30');
    $('#failureThreshold').val('3');
    loadGroupsForSelect();
    $('#urlModal').modal('show');
}

// FIXED: Use monitorAPI
async function editUrl(urlId) {
    try {
        const data = await monitorAPI.getUrl(urlId);
        if (data && data.url) {
            const url = data.url;
            $('#urlModalTitle').text('Edit URL');
            $('#urlId').val(url.id);
            $('#urlName').val(url.name);
            $('#urlAddress').val(url.url);
            $('#urlMethod').val(url.method);
            $('#checkInterval').val(url.checkInterval);
            $('#timeout').val(url.timeout);
            $('#failureThreshold').val(url.failureThreshold);
            await loadGroupsForSelect();
            $('#urlModal').modal('show');
        }
    } catch (error) {
        showToast('error', 'Failed to load URL: ' + (error.message || 'Unknown error'));
    }
}

// FIXED: Use monitorAPI
async function loadGroupsForSelect() {
    try {
        const groups = await monitorAPI.getGroups();
        const select = $('#groupId');
        select.empty();
        select.append('<option value="">No group</option>');
        if (groups && groups.length > 0) {
            groups.forEach(group => {
                select.append(`<option value="${group.id}">${escapeHtml(group.name)}</option>`);
            });
        }
    } catch (error) {
        console.error('Failed to load groups', error);
    }
}

// FIXED: Use monitorAPI
async function saveUrl() {
    const urlId = $('#urlId').val();
    const data = {
        name: $('#urlName').val(),
        url: $('#urlAddress').val(),
        method: $('#urlMethod').val(),
        checkInterval: parseInt($('#checkInterval').val()),
        timeout: parseInt($('#timeout').val()),
        failureThreshold: parseInt($('#failureThreshold').val()),
        groupId: $('#groupId').val()
    };

    if (!data.name || !data.url) {
        showToast('error', 'Name and URL are required');
        return;
    }

    try {
        let result;
        if (urlId) {
            result = await monitorAPI.updateUrl(urlId, data);
        } else {
            result = await monitorAPI.createUrl(data);
        }

        if (result) {
            showToast('success', urlId ? 'URL updated' : 'URL created');
            $('#urlModal').modal('hide');
            loadDashboard();
        }
    } catch (error) {
        showToast('error', error.message || 'Failed to save URL');
    }
}

// Helper functions
function formatDate(dateStr) {
    if (!dateStr) return '--';
    const date = new Date(dateStr);
    return date.toLocaleString('en-IN', {
        day: '2-digit',
        month: 'short',
        hour: '2-digit',
        minute: '2-digit'
    });
}

function formatInterval(seconds) {
    if (seconds < 60) return seconds + 's';
    if (seconds < 3600) return (seconds / 60) + 'm';
    return (seconds / 3600) + 'h';
}

function formatDuration(seconds) {
    if (seconds < 60) return seconds + 's';
    if (seconds < 3600) return Math.floor(seconds / 60) + 'm ' + (seconds % 60) + 's';
    return Math.floor(seconds / 3600) + 'h ' + Math.floor((seconds % 3600) / 60) + 'm';
}

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/[&<>]/g, function (m) {
        if (m === '&') return '&amp;';
        if (m === '<') return '&lt;';
        if (m === '>') return '&gt;';
        return m;
    });
}


function showLoading() {
    if (typeof Swal !== 'undefined') {
        Swal.fire({
            title: 'Loading...',
            allowOutsideClick: false,
            didOpen: () => {
                Swal.showLoading();
            }
        });
    }
}

function hideLoading() {
    if (typeof Swal !== 'undefined') {
        Swal.close();
    }
}

async function confirmDialog(message, title = 'Confirm') {
    return new Promise((resolve) => {
        Swal.fire({
            title: title,
            text: message,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            cancelButtonColor: '#6c757d',
            confirmButtonText: 'Yes, delete it!'
        }).then((result) => {
            resolve(result.isConfirmed);
        });
    });
}