// employees.js - add/update/list/delete employees
async function loadEmployees(orgId) {
    try {
        // If orgId provided, fetch employees for org; otherwise, assume current org
        const url = orgId ? `/api/employees/organisation/${orgId}` : `/api/org-admin/employees`;
        // Default endpoint: /api/org-admin/employees/{orgId} — but our controller is /api/employees/organisation/{orgId}
        // To keep it simple, expect server to provide employees by org via page context or call with orgId.
        const res = await fetch(url);
        if (!res.ok) { console.warn('Failed to load employees'); return; }
        const list = await res.json();
        const tbody = document.querySelector('#empTable tbody');
        tbody.innerHTML = '';
        list.forEach(e => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${e.id ?? ''}</td>
                <td>${e.name ?? ''}</td>
                <td>${e.email ?? ''}</td>
                <td>${e.systemRole ?? 'EMPLOYEE'}</td>
                <td>
                  <button class="btn btn-sm btn-primary me-1" onclick="openEditEmp(${e.id})">Edit</button>
                  <button class="btn btn-sm btn-danger" onclick="deleteEmployee(${e.id})">Delete</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (e) {
        console.error('loadEmployees error', e);
    }
}

window.loadEmployees = loadEmployees;

function openCreateEmp() {
    document.getElementById('empModalTitle').innerText = 'Add Employee';
    document.getElementById('empId').value = '';
    document.getElementById('empName').value = '';
    document.getElementById('empEmail').value = '';
    document.getElementById('empRole').value = 'EMPLOYEE';
    const modal = new bootstrap.Modal(document.getElementById('empModal'));
    modal.show();
}

async function openEditEmp(id) {
    try {
        const res = await fetch(`/api/employees/${id}`);
        if (!res.ok) return;
        const e = await res.json();
        document.getElementById('empModalTitle').innerText = 'Edit Employee';
        document.getElementById('empId').value = e.id;
        document.getElementById('empName').value = e.name || '';
        document.getElementById('empEmail').value = e.email || '';
        document.getElementById('empRole').value = e.systemRole || 'EMPLOYEE';
        new bootstrap.Modal(document.getElementById('empModal')).show();
    } catch (err) {
        console.error(err);
    }
}

document.addEventListener('click', function (ev) {
    if (ev.target && ev.target.id === 'saveEmpBtn') {
        (async () => {
            const id = document.getElementById('empId').value;
            const payload = {
                name: document.getElementById('empName').value,
                email: document.getElementById('empEmail').value,
                systemRole: document.getElementById('empRole').value
            };
            try {
                let res;
                if (id) {
                    res = await fetch(`/api/employees/${id}`, {
                        method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload)
                    });
                } else {
                    res = await fetch('/api/employees', {
                        method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload)
                    });
                }
                if (!res.ok) { alert('Save failed'); return; }
                loadEmployees();
                const modal = bootstrap.Modal.getInstance(document.getElementById('empModal'));
                if (modal) modal.hide();
                showToast('Employee saved', 'success');
            } catch (e) {
                console.error(e); alert('Network error');
            }
        })();
    }
});

async function deleteEmployee(id) {
    if (!confirm('Delete employee?')) return;
    try {
        const res = await fetch(`/api/employees/${id}`, { method: 'DELETE' });
        if (!res.ok) { alert('Delete failed'); return; }
        loadEmployees();
        showToast('Employee deleted', 'warning');
    } catch (e) {
        console.error(e);
    }
}

// Auto load if table exists
document.addEventListener('DOMContentLoaded', () => {
    if (document.querySelector('#empTable')) loadEmployees();
});
