// URLs management


if (typeof currentPage === 'undefined') {
    var currentPage = 1;
}

if (typeof currentStatus === 'undefined') {
    var currentStatus = '';
}

if (typeof currentSearch === 'undefined') {
    var currentSearch = '';
}


$(document).ready(function () {
    loadUrls();

    $('#searchInput').on('keypress', function (e) {
        if (e.which === 13) {
            loadUrls();
        }
    });

    $('#statusFilter').on('change', function () {
        loadUrls();
    });
});

async function loadUrls(page = 1) {
    currentPage = page;
    currentStatus = $('#statusFilter').val();
    currentSearch = $('#searchInput').val();

    try {
        const response = await monitorAPI.getUrls(currentPage, 20, currentStatus, currentSearch);
        if (response && response.urls) {
            renderUrls(response.urls);
            renderPagination(response.pagination);
        }
    } catch (error) {
        console.error('Failed to load URLs:', error);
        $('#urlsTableBody').html('<tr><td colspan="9" class="text-center text-danger">Failed to load URLs</td></tr>');
        showToast('error', error.message || 'Failed to load URLs');
    }
}

function renderUrls(urls) {
    if (!urls || urls.length === 0) {
        $('#urlsTableBody').html('<tr><td colspan="9" class="text-center text-muted">No URLs configured</td></tr>');
        return;
    }

    const html = urls.map(url => {
        const statusClass = url.currentStatus === 'up' ? 'success' : (url.currentStatus === 'down' ? 'danger' : 'warning');
        const statusIcon = url.currentStatus === 'up' ? 'fa-check-circle' : (url.currentStatus === 'down' ? 'fa-exclamation-circle' : 'fa-clock');

        return `
            <tr>
                <td><i class="fa-solid ${statusIcon} text-${statusClass} fs-5"></i> <span class="badge bg-${statusClass}">${url.currentStatus}</span></td>
                <td><strong>${escapeHtml(url.name)}</strong></td>
                <td><small class="text-muted">${escapeHtml(url.url)}</small></td>
                <td><span class="badge bg-secondary">${url.method || 'GET'}</span></td>
                <td>${formatInterval(url.checkInterval)}</td>
                <td><span class="fw-bold">${url.uptimePercentage}%</span></td>
                <td>${url.lastResponseTime ? url.lastResponseTime + 'ms' : '--'}</td>
                <td><small>${url.lastCheckedAt ? formatDate(url.lastCheckedAt) : 'Never'}</small></td>
                <td>
                    <button class="btn btn-sm btn-outline-primary me-1" onclick="checkNow('${url.id}')" title="Check Now">
                        <i class="fa-solid fa-play"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-secondary me-1" onclick="toggleUrl('${url.id}')" title="${url.isActive ? 'Pause' : 'Resume'}">
                        <i class="fa-solid ${url.isActive ? 'fa-pause' : 'fa-play'}"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-info me-1" onclick="showUrlDetail('${url.id}')" title="Details">
                        <i class="fa-solid fa-chart-line"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-danger" onclick="deleteUrl('${url.id}', '${escapeHtml(url.name)}')" title="Delete">
                        <i class="fa-solid fa-trash"></i>
                    </button>
                </td>
            </tr>
        `;
    }).join('');
    $('#urlsTableBody').html(html);
}

function renderPagination(pagination) {
    if (!pagination || pagination.totalPages <= 1) {
        $('#paginationContainer').empty();
        return;
    }

    let html = '<nav><ul class="pagination pagination-sm">';
    html += `<li class="page-item ${pagination.page <= 1 ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="loadUrls(${pagination.page - 1}); return false;">Previous</a>
            </li>`;

    for (let i = 1; i <= pagination.totalPages; i++) {
        if (i === 1 || i === pagination.totalPages || (i >= pagination.page - 2 && i <= pagination.page + 2)) {
            html += `<li class="page-item ${pagination.page === i ? 'active' : ''}">
                        <a class="page-link" href="#" onclick="loadUrls(${i}); return false;">${i}</a>
                    </li>`;
        } else if (i === pagination.page - 3 || i === pagination.page + 3) {
            html += '<li class="page-item disabled"><span class="page-link">...</span></li>';
        }
    }

    html += `<li class="page-item ${pagination.page >= pagination.totalPages ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="loadUrls(${pagination.page + 1}); return false;">Next</a>
            </li>`;
    html += '</ul></nav>';

    $('#paginationContainer').html(html);
}

async function checkNow(urlId) {
    try {
        showLoading();
        const result = await monitorAPI.checkNow(urlId);
        if (result.isUp) {
            showToast('success', `✅ UP - ${result.statusCode} (${result.responseTime}ms)`);
        } else {
            showToast('error', `🔴 DOWN - ${result.error || 'Connection failed'}`);
        }
        loadUrls(currentPage);
    } catch (error) {
        showToast('error', error.message || 'Check failed');
    } finally {
        hideLoading();
    }
}

async function toggleUrl(urlId) {
    try {
        const result = await monitorAPI.toggleUrl(urlId);
        showToast('success', result.isActive ? 'URL resumed' : 'URL paused');
        loadUrls(currentPage);
    } catch (error) {
        showToast('error', error.message || 'Failed to toggle status');
    }
}

async function deleteUrl(urlId, urlName) {
    const confirmed = await confirmDialog(`Delete "${urlName}"? All check history will be lost.`);
    if (!confirmed) return;

    try {
        await monitorAPI.deleteUrl(urlId);
        showToast('success', 'URL deleted successfully');
        loadUrls(currentPage);
    } catch (error) {
        showToast('error', error.message || 'Failed to delete URL');
    }
}

async function showUrlDetail(urlId) {
    try {
        const data = await monitorAPI.getUrl(urlId);
        renderUrlDetailModal(data);
        $('#urlDetailModal').modal('show');
    } catch (error) {
        showToast('error', error.message || 'Failed to load URL details');
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
                <td>${check.isUp ? '<i class="fa-solid fa-check-circle text-success"></i>' : '<i class="fa-solid fa-times-circle text-danger"></i>'}</td>
                <td>${check.statusCode || '--'}</td>
                <td>${check.responseTime || '--'}ms</td>
                <td>${formatDate(check.checkedAt)}</td>
                <td class="text-truncate" style="max-width: 200px;">${escapeHtml(check.error || '')}</td>
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
                    <tr><th>Status</th><th>Code</th><th>Response</th><th>Time</th><th>Error</th></tr>
                </thead>
                <tbody>
                    ${checksHtml || '<tr><td colspan="5" class="text-center">No checks yet</td></tr>'}
                </tbody>
            </table>
        </div>
        
        ${incidents.length > 0 ? `<h6 class="mt-3">Incident History</h6>${incidentsHtml}` : ''}
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

async function editUrl(urlId) {
    try {
        const response = await monitorAPI.getUrl(urlId);
        const url = response.url;
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
    } catch (error) {
        showToast('error', error.message || 'Failed to load URL');
    }
}

async function loadGroupsForSelect() {
    try {
        const groups = await monitorAPI.getGroups();
        const select = $('#groupId');
        select.empty();
        select.append('<option value="">No group</option>');
        groups.forEach(group => {
            select.append(`<option value="${group.id}">${escapeHtml(group.name)}</option>`);
        });
    } catch (error) {
        console.error('Failed to load groups', error);
    }
}

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
        if (urlId) {
            await monitorAPI.updateUrl(urlId, data);
            showToast('success', 'URL updated');
        } else {
            await monitorAPI.createUrl(data);
            showToast('success', 'URL created');
        }
        $('#urlModal').modal('hide');
        loadUrls(currentPage);
    } catch (error) {
        showToast('error', error.message || 'Failed to save URL');
    }
}