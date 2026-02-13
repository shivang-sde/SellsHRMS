(function () {
    const ORG_ID = window.APP.ORG_ID;
    const API = `/api/asset-maintenance`;
    const ASSET_API = `/api/assets/org/${ORG_ID}`;

    let modal;

    document.addEventListener('DOMContentLoaded', () => {
        modal = new bootstrap.Modal(document.getElementById('maintenanceModal'));
        loadLogs();
        document.getElementById('maintenanceModalForm').addEventListener('submit', saveLog);
    });

    function loadLogs() {
        axios.get(`${API}/org/${ORG_ID}`).then(r => {
            const tbody = document.getElementById('maintenanceTableBody');
            if (!r.data.length) {
                tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted">No logs found</td></tr>';
                return;
            }
            tbody.innerHTML = r.data.map(l => `
                <tr>
                    <td>${l.assetCode || '-'}</td>
                    <td>${l.assetName || '-'}</td>
                    <td>${l.maintenanceDate || '-'}</td>
                    <td>${l.description || '-'}</td>
                    <td>${l.cost ? '₹' + l.cost.toLocaleString() : '-'}</td>
                    <td>${l.performedBy || '-'}</td>
                    <td>
                        <button class="btn btn-sm btn-outline-danger" onclick="deleteLog(${l.id})"><i class="fa fa-trash"></i></button>
                    </td>
                </tr>
            `).join('');
        });
    }

    window.openMaintenanceModal = function () {
        document.getElementById('maintenanceModalForm').reset();
        // Load assets dropdown
        const sel = document.getElementById('maintenanceAssetSelect');
        sel.innerHTML = '<option>Loading...</option>';
        axios.get(ASSET_API).then(r => {
            sel.innerHTML = r.data.map(a => `<option value="${a.id}">${a.assetCode} - ${a.name}</option>`).join('');
        });
        modal.show();
    };

    function saveLog(e) {
        e.preventDefault();
        const payload = {
            assetId: document.getElementById('maintenanceAssetSelect').value,
            maintenanceDate: document.getElementById('maintenanceDateInput').value,
            description: document.getElementById('maintenanceDescInput').value,
            cost: document.getElementById('maintenanceCostInput').value || null,
            performedBy: document.getElementById('maintenancePerformedByInput').value,
            orgId: ORG_ID
        };
        axios.post(API, payload)
            .then(() => { modal.hide(); loadLogs(); Swal.fire('Saved!', '', 'success'); })
            .catch(err => Swal.fire('Error', err.response?.data?.message || 'Failed', 'error'));
    }

    window.deleteLog = function (id) {
        Swal.fire({ title: 'Delete this log?', icon: 'warning', showCancelButton: true, confirmButtonText: 'Delete' })
            .then(r => { if (r.isConfirmed) axios.delete(`${API}/${id}`).then(() => loadLogs()); });
    };
})();
