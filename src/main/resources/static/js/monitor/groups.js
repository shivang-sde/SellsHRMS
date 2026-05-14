$(document).ready(function () {
    loadGroups();

    // EVENT DELEGATION: Handle clicks dynamically added rows
    // 1. Handle Row Click (Show Detail)
    $(document).on('click', '.group-row', function (e) {
        // Ignore if clicking the delete button
        if ($(e.target).closest('.btn-delete-group').length) return;
        if ($(e.target).closest('.btn-add-member').length) return;
        if ($(e.target).closest('.btn-add-url').length) return;

        const groupId = $(this).data('group-id');
        showGroupDetail(groupId);
    });

    // 2. Handle Edit Button Click
    $(document).on('click', '.btn-edit-group', function (e) {
        e.stopPropagation();
        const groupId = $(this).closest('tr').data('group-id');
        editGroup(groupId);
    });

    // 3. Handle Delete Button Click
    $(document).on('click', '.btn-delete-group', function (e) {
        e.stopPropagation();
        const $row = $(this).closest('tr');
        const groupId = $row.data('group-id');
        const groupName = $row.find('td[data-label="Name"] strong').text();

        deleteGroup(groupId, groupName);
    });
});

async function loadGroups() {
    try {
        const groups = await monitorAPI.getGroups();
        console.log("groups", groups)
        renderGroups(groups);
    } catch (error) {
        console.error('Failed to load groups:', error);
        $('#groupsTableBody').html('<tr><td colspan="6" class="text-center text-danger">Failed to load groups</td></tr>');
        showToast('error', 'Failed to load groups');
    }
}

function renderGroups(groups) {
    const $tbody = $('#groupsTableBody');

    if (!groups || groups.length === 0) {
        $tbody.html('<tr><td colspan="6" class="text-center text-muted">No groups configured</td></tr>');
        return;
    }

    const html = groups.map(g => {
        const name = escapeHtml(g.name);
        const desc = escapeHtml(g.description) || '--';
        const date = g.createdAt ? new Date(g.createdAt).toLocaleDateString() : '--';

        return `
            <tr data-group-id="${g.id}" class="group-row" style="cursor: pointer;">
                <td data-label="Name"><strong>${name}</strong></td>
                <td data-label="Description" class="text-truncate" style="max-width: 200px;" title="${desc}">${desc}</td>
                <td data-label="URLs" class="text-center"><span class="badge bg-info cursor: pointer;">${g.urlCount || 0}</span></td>
                <td data-label="Members" class="text-center"><span class="badge bg-primary">${g.memberCount || 0}</span></td>
                <td data-label="Created"><small>${date}</small></td>
        <td data-label="Actions" class="text-end">
                    <button class="btn btn-sm btn-outline-primary btn-edit-group" title="Edit Group">
                        <i class="fa-solid fa-edit"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-danger btn-delete-group" title="Delete Group">
                        <i class="fa-solid fa-trash"></i>
                    </button>
                    <button class="btn btn-sm btn-primary btn-add-member" onclick="showAddMemberOffcanvas('${g.id}')">
                        <i class="fa-solid fa-plus"></i> Member
                    </button>
                    <button class="btn btn-sm btn-primary btn-add-url" onclick="showAddUrlOffcanvas('${g.id}')">
                        <i class="fa-solid fa-plus"></i> URL
                    </button>
                </td>
            </tr>
        `;
    }).join('');

    $tbody.html(html);
}

// --- Modal & Action Functions ---

function showCreateGroupModal() {
    $('#createGroupModal .modal-title').text('Create New Group');
    $('#saveGroupBtn').text('Create');
    $('#groupId').val('');
    $('#groupName').val('');
    $('#groupDescription').val('');
    $('#createGroupModal').modal('show');
}

async function editGroup(groupId) {
    try {
        const response = await monitorAPI.getGroup(groupId);
        const group = response.group;
        $('#createGroupModal .modal-title').text('Edit Group');
        $('#saveGroupBtn').text('Save Changes');
        $('#groupId').val(group.id);
        $('#groupName').val(group.name);
        $('#groupDescription').val(group.description);
        $('#createGroupModal').modal('show');
    } catch (error) {
        showToast('error', 'Failed to load group details');
    }
}

async function saveGroup() {
    const groupId = $('#groupId').val();
    const name = $('#groupName').val().trim();
    const description = $('#groupDescription').val().trim();

    if (!name) {
        showToast('error', 'Group name is required');
        return;
    }

    const data = { name, description };

    try {
        if (groupId) {
            await monitorAPI.updateGroup(groupId, data);
            showToast('success', 'Group updated successfully');
        } else {
            await monitorAPI.createGroup(data);
            showToast('success', 'Group created successfully');
        }
        $('#createGroupModal').modal('hide');
        loadGroups();
    } catch (error) {
        showToast('error', groupId ? 'Failed to update group' : 'Failed to create group');
    }
}

async function showGroupDetail(groupId) {
    try {
        const data = await monitorAPI.getGroup(groupId);

        renderGroupDetail(data, groupId); // Pass ID explicitly
        $('#groupDetailModal').modal('show');
    } catch (error) {
        showToast('error', 'Failed to load group details');
    }
}

// --- Update renderGroupDetail to include Add Member Button ---

function renderGroupDetail(data, groupId) {
    console.log("gp detail", data)
    const group = data.group;
    const urls = data.urls || [];
    const members = data.members || [];
    // Store available users in a global variable or data attribute for the modal to use
    window.currentAvailableUsers = data.availableUsers || [];




    // 1. Render URLs (Same as before)
    const urlRows = urls.map(url => `
        <tr>
            <td>${escapeHtml(url.name)}
                <span><span class="badge bg-${url.currentStatus === 'up' ? 'success' : 'danger'}">${url.currentStatus}</span></span>
            </td>
            <td>
                <button class="btn btn-sm btn-outline-danger" onclick="removeUrlFromGroup('${groupId}', '${url.id}')">
                    <i class="fa-solid fa-minus"></i>
                </button>
            </td>
        </tr>
    `).join('');

    // 2. Render Members with Remove Button
    const memberRows = members.map(m => `
        <tr>
            <td>${escapeHtml(m.firstName + ' ' + m.lastName)}</td>
            <td>
                <button class="btn btn-sm btn-outline-danger" onclick="removeMemberFromGroup('${groupId}', '${m.userId}')">
                    <i class="fa-solid fa-user-minus"></i>
                </button>
            </td>
        </tr>
    `).join('');

    const html = `
        <div class="mb-4">
            <h5 class="fw-bold">${escapeHtml(group.name)}</h5>
            <p class="text-muted">${escapeHtml(group.description) || 'No description provided.'}</p>
        </div>
        
        <div class="row">
            <!-- URLs Column -->
            <div class="col-md-6 mb-3">
                <div class="card h-100">
                    <div class="card-header bg-light fw-bold d-flex justify-content-between align-items-center">
                        <span>URLs (${urls.length})</span>
                    </div>
                    <div class="card-body p-0">
                        <div class="table-responsive">
                            <table class="table table-sm mb-0">
                                <thead class="table-light"><tr><th>Name</th><th>Action</th></tr></thead>
                                <tbody>${urlRows.length ? urlRows : '<tr><td colspan="2" class="text-center text-muted">No URLs</td></tr>'}</tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Members Column -->
            <div class="col-md-6 mb-3">
                <div class="card h-100">
                    <div class="card-header bg-light fw-bold d-flex justify-content-between align-items-center">
                        <span>Members (${members.length})</span>
                    </div>
                    <div class="card-body p-0">
                        <div class="table-responsive">
                            <table class="table table-sm mb-0">
                                <thead class="table-light"><tr><th>Name</th><th>Action</th></tr></thead>
                                <tbody>${memberRows.length ? memberRows : '<tr><td colspan="2" class="text-center text-muted">No members</td></tr>'}</tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;

    $('#groupDetailBody').html(html);
    $('#groupDetailTitle').text('Group Details');
}

// --- Add Member Modal Logic ---

async function showAddMemberOffcanvas(groupId) {
    $('#targetGroupId').val(groupId);
    $('#memberSearchInput').val('');

    // Load available users from the global variable set in renderGroupDetail
    const data = await monitorAPI.getGroup(groupId);
    const users = data.availableUsers || [];
    const filteredUsers = users.filter(u => u.id !== window.APP.USER_ID);
    window.currentAvailableUsers = filteredUsers;
    renderUserSearchResults(filteredUsers);

    // Show Offcanvas
    const offcanvasEl = document.getElementById('addMemberOffcanvas');
    const bsOffcanvas = new bootstrap.Offcanvas(offcanvasEl);
    bsOffcanvas.show();
}

// Handle Search Input
$(document).on('input', '#memberSearchInput', function () {
    const query = $(this).val().toLowerCase();
    const filtered = (window.currentAvailableUsers || []).filter(u =>
        u.firstName.toLowerCase().includes(query) ||
        u.lastName.toLowerCase().includes(query) ||
        u.email.toLowerCase().includes(query)
    );
    renderUserSearchResults(filtered);
});

function renderUserSearchResults(users) {
    const $container = $('#userSearchResults');
    $container.empty();

    if (users.length === 0) {
        $container.html('<div class="text-muted small p-2">No users found</div>');
        return;
    }

    users.forEach(u => {
        const name = `${u.firstName} ${u.lastName}`;
        const item = `
            <button type="button" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center" 
                    onclick="confirmAddMember('${u.id}', '${escapeHtml(name)}')">
                <div>
                    <strong>${escapeHtml(name)}</strong><br>
                    <small class="text-muted">${escapeHtml(u.email)}</small>
                </div>
                <i class="fa-solid fa-plus text-primary"></i>
            </button>
        `;
        $container.append(item);
    });
}

async function confirmAddMember(userId, userName) {
    const groupId = $('#targetGroupId').val();
    if (!confirm(`Add ${userName} to this group?`)) return;

    try {
        await monitorAPI.addMemberToGroup(groupId, userId);
        showToast('success', 'Member added successfully');
        // Refresh the detail modal to show new member and update available list
        loadGroups();
    } catch (error) {
        showToast('error', 'Failed to add member');
    }
}

// --- Existing Remove Member Logic (Ensure it matches your API) ---

async function removeMemberFromGroup(groupId, userId) {
    if (!confirm('Remove this member from the group?')) return;
    try {
        await monitorAPI.removeMemberFromGroup(groupId, userId);
        showToast('success', 'Member removed');
        showGroupDetail(groupId);
    } catch (error) {
        showToast('error', 'Failed to remove member');
    }
}

async function deleteGroup(groupId, groupName) {
    // Simple confirm replacement if you don't have a custom dialog
    if (!confirm(`Are you sure you want to delete "${groupName}"? This action cannot be undone.`)) return;

    try {
        await monitorAPI.deleteGroup(groupId);
        showToast('success', 'Group deleted');
        loadGroups();
    } catch (error) {
        showToast('error', 'Failed to delete group');
    }
}

async function removeUrlFromGroup(groupId, urlId) {
    try {
        await monitorAPI.removeUrlFromGroup(groupId, urlId);
        showToast('success', 'URL removed');
        // Refresh the detail modal
        showGroupDetail(groupId);
    } catch (error) {
        showToast('error', 'Failed to remove URL');
    }
}

async function removeMemberFromGroup(groupId, userId) {
    try {
        await monitorAPI.removeMemberFromGroup(groupId, userId);
        showToast('success', 'Member removed');
        // Refresh the detail modal
        showGroupDetail(groupId);
    } catch (error) {
        showToast('error', 'Failed to remove member');
    }
}

// Utility: Escape HTML to prevent XSS
function escapeHtml(text) {
    if (!text) return '';
    return String(text)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}


// --- Add URL Offcanvas Logic ---

async function showAddUrlOffcanvas(groupId) {
    $('#targetGroupIdForUrl').val(groupId);
    $('#urlSearchInput').val('');
    $('#urlSearchResults').html('<div class="list-group-item text-muted text-center py-3">Loading...</div>');

    // Show Offcanvas
    const offcanvasEl = document.getElementById('addUrlOffcanvas');
    const bsOffcanvas = new bootstrap.Offcanvas(offcanvasEl);
    bsOffcanvas.show();

    // Load initial available URLs (empty search)
    loadAvailableUrls(groupId, '');
}

// Handle Search Input for URLs
$(document).on('input', '#urlSearchInput', function () {
    const query = $(this).val();
    const groupId = $('#targetGroupIdForUrl').val();
    loadAvailableUrls(groupId, query);
});

async function loadAvailableUrls(groupId, searchQuery) {
    try {
        // Assuming you add this method to monitorAPI (see step 4)
        const urls = await monitorAPI.getAvailableUrlsForGroup(groupId, searchQuery);
        renderUrlSearchResults(urls);
    } catch (error) {
        showToast('error', error.message || 'Failed to load URLs');
        $('#urlSearchResults').html('<div class="list-group-item text-danger text-center">Failed to load URLs</div>');
    }
}

function renderUrlSearchResults(urls) {
    const $container = $('#urlSearchResults');
    $container.empty();

    if (!urls || urls.length === 0) {
        $container.html('<div class="list-group-item text-muted text-center">No available URLs found</div>');
        return;
    }

    urls.forEach(u => {
        const statusBadge = u.currentStatus === 'up'
            ? '<span class="badge bg-success ms-2">UP</span>'
            : '<span class="badge bg-danger ms-2">DOWN</span>';

        const html = `
            <button type="button" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center py-3" 
                    onclick="confirmAddUrlToGroup('${u.id}', '${escapeHtml(u.name)}')">
                <div>
                    <div class="fw-bold">${escapeHtml(u.name)} ${statusBadge}</div>
                    <small class="text-muted text-truncate d-block" style="max-width: 250px;">${escapeHtml(u.url)}</small>
                </div>
                <span class="badge bg-primary rounded-pill">Add</span>
            </button>
        `;
        $container.append(html);
    });
}

async function confirmAddUrlToGroup(urlId, urlName) {
    const groupId = $('#targetGroupIdForUrl').val();

    if (!confirm(`Add "${urlName}" to this group?`)) return;

    try {
        await monitorAPI.addUrlToGroup(groupId, urlId);
        showToast('success', 'URL added successfully');

        // Hide Offcanvas
        const offcanvasEl = document.getElementById('addUrlOffcanvas');
        const bsOffcanvas = bootstrap.Offcanvas.getInstance(offcanvasEl);
        bsOffcanvas.hide();

        // Refresh Detail Modal
        loadGroups();
    } catch (error) {
        showToast('error', 'Failed to add URL');
    }
}