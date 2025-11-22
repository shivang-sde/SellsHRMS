// orgs.js - for organisations list, edit, delete
async function loadOrgs() {
    try {
        const res = await fetch('/api/superadmin/organisations');
        if (!res.ok) { console.warn('Failed to load orgs'); return; }
        const list = await res.json();
        const tbody = document.querySelector('#orgTable tbody');
        tbody.innerHTML = '';
        list.forEach(o => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${o.id ?? ''}</td>
                <td>${o.name ?? ''}</td>
                <td>${o.domain ?? ''}</td>
                <td>${o.contactEmail ?? ''}</td>
                <td>${o.isActive ? 'Yes' : 'No'}</td>
                <td>
                    <button class="btn btn-sm btn-primary me-1" onclick="openEdit(${o.id})">Edit</button>
                    <button class="btn btn-sm btn-danger" onclick="removeOrg(${o.id})">Delete</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (e) {
        console.error('loadOrgs error', e);
    }
}

window.loadOrgs = loadOrgs;

function openEdit(id) {
    fetch('/api/superadmin/organisations')
        .then(r => r.json())
        .then(list => {
            const org = list.find(x => x.id === id);
            if (!org) return;
            document.getElementById('editOrgId').value = org.id;
            document.getElementById('editName').value = org.name || '';
            document.getElementById('editDomain').value = org.domain || '';
            document.getElementById('editContactEmail').value = org.contactEmail || '';
            document.getElementById('editActive').checked = !!org.isActive;
            const modalEl = document.getElementById('orgEditModal');
            const modal = new bootstrap.Modal(modalEl);
            modal.show();
        });
}

document.addEventListener('click', function (e) {
    if (e.target && e.target.id === 'saveOrgBtn') {
        (async () => {
            const id = document.getElementById('editOrgId').value;
            const payload = {
                name: document.getElementById('editName').value,
                domain: document.getElementById('editDomain').value,
                contactEmail: document.getElementById('editContactEmail').value,
                isActive: document.getElementById('editActive').checked
            };
            try {
                const res = await fetch(`/api/superadmin/organisation/${id}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });
                if (!res.ok) { alert('Update failed'); return; }
                loadOrgs();
                const modalInstance = bootstrap.Modal.getInstance(document.getElementById('orgEditModal'));
                if (modalInstance) modalInstance.hide();
                showToast('Organisation updated', 'success');
            } catch (e) {
                alert('Network error');
                console.error(e);
            }
        })();
    }
});

async function removeOrg(id) {
    if (!confirm('Delete organisation? This cannot be undone.')) return;
    try {
        const res = await fetch(`/api/superadmin/organisation/${id}`, { method: 'DELETE' });
        if (!res.ok) { alert('Delete failed'); return; }
        loadOrgs();
        showToast('Organisation deleted', 'warning');
    } catch (e) {
        alert('Network error');
    }
}

// Auto-load if table present
document.addEventListener('DOMContentLoaded', () => {
    if (document.querySelector('#orgTable')) loadOrgs();
});
