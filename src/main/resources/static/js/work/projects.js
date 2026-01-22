let allProjects = [];
let allEmployees = [];
let editingProjectId = null;

$(document).ready(async function() {
    await loadProjects();
    $('#searchInput').on('input', debounce(handleSearch, 500));
    $('#statusFilter, #typeFilter').on('change', filterProjects);
});

// =========================== LOAD PROJECTS ===========================
async function loadProjects() {
    try {
        loadingUtils.show('#projectsTable tbody');
        allProjects = await projectAPI.getByOrganisation();
        renderProjects(allProjects);
    } catch (error) {
        console.error('Failed to load projects:', error);
        showToast('Failed to load projects', 'error');
    } finally {
        loadingUtils.hide();
    }
}

// =========================== RENDER ===========================
function renderProjects(projects) {
    const tbody = $('#projectsTable tbody');
    if (projects.length === 0) {
        tbody.html(`
            <tr>
                <td colspan="7" class="text-center text-muted py-5">
                    <i class="fas fa-folder-open fa-3x mb-3 opacity-50"></i>
                    <p>No projects found</p>
                </td>
            </tr>
        `);
        return;
    }

    const html = projects.map(p => `
        
        <tr>
            <td>
                <a href="${window.APP.CONTEXT_PATH}/work/projects/${p.id}" class="fw-semibold text-decoration-none">
                    ${p.name}
                </a>
            </td>
            <td><span class="badge bg-info">${p.projectType || '-'}</span></td>
            <td>${getStatusBadge(p.status)}</td>
            <td>${getPriorityBadge(p.priority)}</td>
            <td>${formatDate(p.startDate)}</td>
            <td>${formatDate(p.endDate)}</td>
             <td>${p.createdByName}</td>
            <td>
                <div class="btn-group btn-group-sm">
                    <button class="btn btn-outline-primary" onclick="viewProject(${p.id})" title="View Details">
                        <i class="fas fa-eye"></i>
                    </button>
                    <button class="btn btn-outline-warning" onclick="editProject(${p.id})" title="Edit">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="btn btn-outline-danger" onclick="deleteProject(${p.id})" title="Delete">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
    tbody.html(html);
}

// =========================== OPEN MODAL ===========================
async function openProjectModal() {
    editingProjectId = null;
    $('#modalTitle').text('Add New Project');
    modalUtils.resetForm('projectModal');

    $('[name="status"]').val('PLANNING');
    $('[name="priority"]').val('MEDIUM');

    await loadSubordinatesForProject();// Load employees that report to current user
    modalUtils.open('projectModal');
}

// =========================== LOAD SUBORDINATES ===========================
async function loadSubordinates() {
    try {
        const employees = await employeeAPI.getSubordinates(window.APP.EMPLOYEE_ID);
        allEmployees = employees || [];
      
        const options = employees.map(e => 
            `<option value="${e.id}">${e.fullName} ${e.employeeCode
} (${e.department || 'N/A'} - ${e.designation})</option>`
        ).join('');
        $('#projectMembersSelect').html(options);
    } catch (error) {
        console.error('Failed to load subordinates:', error);
        showToast('Could not load available members', 'warning');
    }
}

// =========================== EDIT PROJECT ===========================
async function editProject(id) {
    try {
        loadingUtils.show();
        const project = await projectAPI.getById(id);
        editingProjectId = id;
        $('#modalTitle').text('Edit Project');

        // Populate base project fields
        Object.entries(project).forEach(([key, value]) => {
            const el = $(`[name="${key}"]`);
            if (el.length) {
                if (el.attr('type') === 'date' && value) el.val(value.split('T')[0]);
                else el.val(value);
            }
        });

        // Populate members (if any)
       // Populate manager and team lead
        if (project.projectManagerId) $('#projectManagerSelect').val(project.projectManagerId);
        if (project.projectTeamLeadId) $('#projectTeamLeadSelect').val(project.projectTeamLeadId);
        await loadSubordinatesForProject();


        modalUtils.open('projectModal');
    } catch (error) {
        console.error(error);
        showToast('Failed to load project details', 'error');
    } finally {
        loadingUtils.hide();
    }
}


// =========================== SAVE PROJECT ===========================
async function saveProject() {
    const form = document.getElementById('projectForm');
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }

    const formData = new FormData(form);
    const data = {};
    formData.forEach((v, k) => data[k] = v || null);
    try {
        loadingUtils.show();
        if (editingProjectId) {
            await projectAPI.update(editingProjectId, data);
            showToast('Project updated successfully', 'success');
        } else {
            await projectAPI.create(data);
            showToast('Project created successfully', 'success');
        }
        modalUtils.close('projectModal');
        await loadProjects();
    } catch (error) {
        console.error(error);
        showToast('Failed to save project', 'error');
    } finally {
        loadingUtils.hide();
    }
}

// =========================== DELETE PROJECT ===========================
function deleteProject(id) {
    modalUtils.confirm(
        'Delete Project',
        'Are you sure you want to delete this project?',
        async () => {
            try {
                loadingUtils.show();
                await projectAPI.delete(id);
                showToast('Project deleted successfully', 'success');
                await loadProjects();
            } catch (error) {
                showToast('Failed to delete project', 'error');
            } finally {
                loadingUtils.hide();
            }
        }
    );
}

async function loadSubordinatesForProject() {
  try {
    const res = await employeeAPI.getSubordinates(window.APP.EMPLOYEE_ID, window.APP.ORG_ID);
    const subs = res || [];
    console.log("res sub", subs)

    const options = subs.map(e => 
      `<option value="${e.id}">${e.fullName} ${e.employeeCode} (${e.department || 'N/A'} - ${e.designation})</option>`
    ).join('');

    $('#projectManagerSelect').append(options);
    $('#projectTeamLeadSelect').append(options);
  } catch (err) {
    console.error('Failed to load subordinates:', err);
  }
}


// =========================== SEARCH + FILTER ===========================
async function handleSearch() {
    const keyword = $('#searchInput').val().trim();
    if (keyword.length < 2) return renderProjects(allProjects);

    try {
        const results = await projectAPI.search(keyword);
        renderProjects(results);
    } catch (error) {
        console.error('Search failed:', error);
    }
}

function filterProjects() {
    const status = $('#statusFilter').val();
    const type = $('#typeFilter').val();

    let filtered = [...allProjects];
    if (status) filtered = filtered.filter(p => p.status === status);
    if (type) filtered = filtered.filter(p => p.projectType === type);
    renderProjects(filtered);
}

function resetFilters() {
    $('#searchInput, #statusFilter, #typeFilter').val('');
    renderProjects(allProjects);
}

// =========================== UTILITY ===========================
function debounce(func, delay) {
    let timeout;
    return function(...args) {
        clearTimeout(timeout);
        timeout = setTimeout(() => func.apply(this, args), delay);
    };
}


function viewProject(id) {
  window.location.href = `${window.APP.CONTEXT_PATH}/work/projects/${id}`;
}
