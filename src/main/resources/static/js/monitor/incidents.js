// Incidents management


if (typeof currentPage === 'undefined') {
    var currentPage = 1;
}

if (typeof currentResolved === 'undefined') {
    var currentResolved = null;
}

$(document).ready(function () {
    loadIncidents();

    $('#statusFilter').on('change', function () {
        loadIncidents();
    });
});

async function loadIncidents(page = 1) {
    currentPage = page;
    const statusFilter = $('#statusFilter').val();
    currentResolved = statusFilter === '' ? null : (statusFilter === 'true');

    try {
        const response = await monitorAPI.getIncidents(currentPage, 20, currentResolved);
        if (response && response.incidents) {
            renderIncidents(response.incidents);
            renderPagination(response.pagination);
        }
    } catch (error) {
        console.error('Failed to load incidents:', error);
        $('#incidentsTableBody').html('<tr><td colspan="6" class="text-center text-danger">Failed to load incidents</td></tr>');
        showToast('error', error.message || 'Failed to load incidents');
    }
}

function renderIncidents(incidents) {
    if (!incidents || incidents.length === 0) {
        $('#incidentsTableBody').html('<tr><td colspan="6" class="text-center text-muted">No incidents recorded</td></tr>');
        return;
    }

    const html = incidents.map(i => `
        <tr>
            <td>
                <strong>${escapeHtml(i.urlName)}</strong><br>
                <small class="text-muted">${escapeHtml(i.url)}</small>
            </td>
            <td><small>${formatDate(i.startedAt)}</small></td>
            <td><small>${i.endedAt ? formatDate(i.endedAt) : '--'}</small></td>
            <td><span class="badge bg-secondary">${formatDuration(i.durationSeconds)}</span></td>
            <td><small class="text-muted">${escapeHtml(i.cause) || '--'}</small></td>
            <td><span class="badge ${i.resolved ? 'bg-success' : 'bg-danger'}">${i.resolved ? 'Resolved' : 'Active'}</span></td>
        </tr>
    `).join('');
    $('#incidentsTableBody').html(html);
}

function renderPagination(pagination) {
    if (!pagination || pagination.totalPages <= 1) {
        $('#paginationContainer').empty();
        return;
    }

    let html = '<nav><ul class="pagination pagination-sm">';
    html += `<li class="page-item ${pagination.page <= 1 ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="loadIncidents(${pagination.page - 1}); return false;">Previous</a>
            </li>`;

    for (let i = 1; i <= pagination.totalPages; i++) {
        if (i === 1 || i === pagination.totalPages || (i >= pagination.page - 2 && i <= pagination.page + 2)) {
            html += `<li class="page-item ${pagination.page === i ? 'active' : ''}">
                        <a class="page-link" href="#" onclick="loadIncidents(${i}); return false;">${i}</a>
                    </li>`;
        } else if (i === pagination.page - 3 || i === pagination.page + 3) {
            html += '<li class="page-item disabled"><span class="page-link">...</span></li>';
        }
    }

    html += `<li class="page-item ${pagination.page >= pagination.totalPages ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="loadIncidents(${pagination.page + 1}); return false;">Next</a>
            </li>`;
    html += '</ul></nav>';

    $('#paginationContainer').html(html);
}