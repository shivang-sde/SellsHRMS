(function () {
    const ORG_ID = window.APP.ORG_ID;
    const BASE = `/api/assets/org/${ORG_ID}`;
    const API = `/api/assets`;
    const CATEGORY_API = `/api/asset-categories/org/${ORG_ID}`;
    const VENDOR_API = `/api/vendors/org/${ORG_ID}`;
    const EMP_API = `/api/employees/org/${ORG_ID}`;

    let assetModal, assignModal;

    document.addEventListener('DOMContentLoaded', () => {
        assetModal = new bootstrap.Modal(document.getElementById('assetModal'));
        assignModal = new bootstrap.Modal(document.getElementById('assignModal'));
        loadAssets();
        loadDropdowns();

        document.getElementById('assetModalForm').addEventListener('submit', saveAsset);
        document.getElementById('assignForm').addEventListener('submit', assignAsset);
    });

    function loadDropdowns() {
        axios.get(CATEGORY_API).then(r => {
            const sel = document.getElementById('assetCategorySelect');
            sel.innerHTML = '<option value="">-- None --</option>';
            r.data.forEach(c => sel.innerHTML += `<option value="${c.id}">${c.name}</option>`);
        });
        axios.get(VENDOR_API).then(r => {
            const sel = document.getElementById('assetVendorSelect');
            sel.innerHTML = '<option value="">-- None --</option>';
            r.data.forEach(v => sel.innerHTML += `<option value="${v.id}">${v.name}</option>`);
        });
    }

    function loadAssets() {
        axios.get(BASE).then(r => {
            const tbody = document.getElementById('assetTableBody');
            if (!r.data.length) {
                tbody.innerHTML = '<tr><td colspan="8" class="text-center text-muted">No assets found</td></tr>';
                return;
            }
            tbody.innerHTML = r.data.map(a => `
                <tr>
                    <td><strong>${a.assetCode}</strong></td>
                    <td>${a.name}</td>
                    <td>${a.categoryName || '-'}</td>
                    <td><span class="badge bg-${statusColor(a.status)}">${a.status}</span></td>
                    <td>${a.condition || '-'}</td>
                    <td>${a.assignedToName || '-'}</td>
                    <td>${a.cost ? '₹' + a.cost.toLocaleString() : '-'}</td>
                    <td>
                        <button class="btn btn-sm btn-outline-primary me-1" onclick="editAsset(${a.id})"><i class="fa fa-edit"></i></button>
                        <button class="btn btn-sm btn-outline-danger me-1" onclick="deleteAsset(${a.id})"><i class="fa fa-trash"></i></button>
                        ${a.status === 'AVAILABLE' ? `<button class="btn btn-sm btn-outline-success me-1" onclick="openAssignModal(${a.id})"><i class="fa fa-user-plus"></i></button>` : ''}
                        ${a.status === 'ASSIGNED' ? `<button class="btn btn-sm btn-outline-warning" onclick="returnAsset(${a.id})"><i class="fa fa-undo"></i></button>` : ''}
                    </td>
                </tr>
            `).join('');
        });
    }

    function statusColor(s) {
        switch (s) {
            case 'AVAILABLE': return 'success';
            case 'ASSIGNED': return 'primary';
            case 'UNDER_MAINTENANCE': return 'warning';
            case 'DISPOSED': return 'secondary';
            default: return 'info';
        }
    }

    window.openAssetModal = function (data) {
        document.getElementById('assetModalForm').reset();
        document.getElementById('assetId').value = '';
        document.getElementById('assetModalTitle').textContent = 'Add Asset';
        if (data) {
            document.getElementById('assetId').value = data.id;
            document.getElementById('assetCodeInput').value = data.assetCode;
            document.getElementById('assetNameInput').value = data.name;
            document.getElementById('assetDescInput').value = data.description || '';
            document.getElementById('assetCategorySelect').value = data.categoryId || '';
            document.getElementById('assetVendorSelect').value = data.vendorId || '';
            document.getElementById('assetConditionSelect').value = data.condition || 'NEW';
            document.getElementById('assetPurchaseDateInput').value = data.purchaseDate || '';
            document.getElementById('assetCostInput').value = data.cost || '';
            document.getElementById('assetModalTitle').textContent = 'Edit Asset';
        }
        assetModal.show();
    };

    window.editAsset = function (id) {
        axios.get(`${API}/${id}`).then(r => openAssetModal(r.data));
    };

    function saveAsset(e) {
        e.preventDefault();
        const id = document.getElementById('assetId').value;
        const payload = {
            assetCode: document.getElementById('assetCodeInput').value,
            name: document.getElementById('assetNameInput').value,
            description: document.getElementById('assetDescInput').value,
            categoryId: document.getElementById('assetCategorySelect').value || null,
            vendorId: document.getElementById('assetVendorSelect').value || null,
            condition: document.getElementById('assetConditionSelect').value,
            purchaseDate: document.getElementById('assetPurchaseDateInput').value || null,
            cost: document.getElementById('assetCostInput').value || null,
            orgId: ORG_ID
        };

        const req = id ? axios.patch(`${API}/${id}`, payload) : axios.post(API, payload);
        req.then(() => { assetModal.hide(); loadAssets(); Swal.fire('Saved!', '', 'success'); })
            .catch(err => Swal.fire('Error', err.response?.data?.message || 'Something went wrong', 'error'));
    }

    window.deleteAsset = function (id) {
        Swal.fire({ title: 'Delete this asset?', icon: 'warning', showCancelButton: true, confirmButtonText: 'Delete' })
            .then(r => { if (r.isConfirmed) axios.delete(`${API}/${id}`).then(() => loadAssets()); });
    };

    window.openAssignModal = function (assetId) {
        document.getElementById('assignAssetId').value = assetId;
        const sel = document.getElementById('assignEmployeeSelect');
        sel.innerHTML = '<option>Loading...</option>';
        axios.get(EMP_API).then(r => {
            sel.innerHTML = r.data.map(e => `<option value="${e.id}">${e.fullName} (${e.employeeCode})</option>`).join('');
        });
        assignModal.show();
    };

    function assignAsset(e) {
        e.preventDefault();
        const assetId = document.getElementById('assignAssetId').value;
        const payload = {
            employeeId: document.getElementById('assignEmployeeSelect').value,
            remarks: document.getElementById('assignRemarks').value
        };
        axios.post(`${API}/${assetId}/assign`, payload)
            .then(() => { assignModal.hide(); loadAssets(); Swal.fire('Assigned!', '', 'success'); })
            .catch(err => Swal.fire('Error', err.response?.data?.message || 'Failed', 'error'));
    }

    window.returnAsset = function (assetId) {
        Swal.fire({ title: 'Return this asset?', icon: 'question', showCancelButton: true, confirmButtonText: 'Return' })
            .then(r => {
                if (r.isConfirmed)
                    axios.post(`${API}/${assetId}/return`)
                        .then(() => { loadAssets(); Swal.fire('Returned!', '', 'success'); });
            });
    };
})();
