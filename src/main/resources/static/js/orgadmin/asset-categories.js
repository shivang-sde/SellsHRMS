(function () {
    const ORG_ID = window.APP.ORG_ID;
    const API = `/api/asset-categories`;

    let modal;

    document.addEventListener('DOMContentLoaded', () => {
        modal = new bootstrap.Modal(document.getElementById('categoryModal'));
        loadCategories();
        document.getElementById('categoryModalForm').addEventListener('submit', saveCategory);
    });

    function loadCategories() {
        axios.get(`${API}/org/${ORG_ID}`).then(r => {
            const tbody = document.getElementById('categoryTableBody');
            if (!r.data.length) {
                tbody.innerHTML = '<tr><td colspan="4" class="text-center text-muted">No categories found</td></tr>';
                return;
            }
            tbody.innerHTML = r.data.map(c => `
                <tr>
                    <td>${c.name}</td>
                    <td>${c.description || '-'}</td>
                    <td><span class="badge bg-${c.isActive ? 'success' : 'secondary'}">${c.isActive ? 'Active' : 'Inactive'}</span></td>
                    <td>
                        <button class="btn btn-sm btn-outline-primary me-1" onclick="editCategory(${c.id})"><i class="fa fa-edit"></i></button>
                        <button class="btn btn-sm btn-outline-danger" onclick="deleteCategory(${c.id})"><i class="fa fa-trash"></i></button>
                    </td>
                </tr>
            `).join('');
        });
    }

    window.openCategoryModal = function (data) {
        document.getElementById('categoryModalForm').reset();
        document.getElementById('categoryId').value = '';
        document.getElementById('categoryModalTitle').textContent = 'Add Category';
        document.getElementById('categoryActiveCheck').checked = true;
        if (data) {
            document.getElementById('categoryId').value = data.id;
            document.getElementById('categoryNameInput').value = data.name;
            document.getElementById('categoryDescInput').value = data.description || '';
            document.getElementById('categoryActiveCheck').checked = data.isActive;
            document.getElementById('categoryModalTitle').textContent = 'Edit Category';
        }
        modal.show();
    };

    window.editCategory = function (id) {
        axios.get(`${API}/${id}`).then(r => openCategoryModal(r.data));
    };

    function saveCategory(e) {
        e.preventDefault();
        const id = document.getElementById('categoryId').value;
        const payload = {
            name: document.getElementById('categoryNameInput').value,
            description: document.getElementById('categoryDescInput').value,
            isActive: document.getElementById('categoryActiveCheck').checked,
            orgId: ORG_ID
        };
        const req = id ? axios.patch(`${API}/${id}`, payload) : axios.post(API, payload);
        req.then(() => { modal.hide(); loadCategories(); Swal.fire('Saved!', '', 'success'); })
            .catch(err => Swal.fire('Error', err.response?.data?.message || 'Failed', 'error'));
    }

    window.deleteCategory = function (id) {
        Swal.fire({ title: 'Delete this category?', icon: 'warning', showCancelButton: true, confirmButtonText: 'Delete' })
            .then(r => { if (r.isConfirmed) axios.delete(`${API}/${id}`).then(() => loadCategories()); });
    };
})();
