(function () {
    const ORG_ID = window.APP.ORG_ID;
    const API = `/api/vendors`;

    let modal;

    document.addEventListener('DOMContentLoaded', () => {
        modal = new bootstrap.Modal(document.getElementById('vendorModal'));
        loadVendors();
        document.getElementById('vendorModalForm').addEventListener('submit', saveVendor);
    });

    function loadVendors() {
        axios.get(`${API}/org/${ORG_ID}`).then(r => {
            const tbody = document.getElementById('vendorTableBody');
            if (!r.data.length) {
                tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted">No vendors found</td></tr>';
                return;
            }
            tbody.innerHTML = r.data.map(v => `
                <tr>
                    <td>${v.name}</td>
                    <td>${v.contactPerson || '-'}</td>
                    <td>${v.email || '-'}</td>
                    <td>${v.phone || '-'}</td>
                    <td>${v.gstNumber || '-'}</td>
                    <td>
                        <button class="btn btn-sm btn-outline-primary me-1" onclick="editVendor(${v.id})"><i class="fa fa-edit"></i></button>
                        <button class="btn btn-sm btn-outline-danger" onclick="deleteVendor(${v.id})"><i class="fa fa-trash"></i></button>
                    </td>
                </tr>
            `).join('');
        });
    }

    window.openVendorModal = function (data) {
        document.getElementById('vendorModalForm').reset();
        document.getElementById('vendorId').value = '';
        document.getElementById('vendorModalTitle').textContent = 'Add Vendor';
        if (data) {
            document.getElementById('vendorId').value = data.id;
            document.getElementById('vendorNameInput').value = data.name;
            document.getElementById('vendorContactInput').value = data.contactPerson || '';
            document.getElementById('vendorEmailInput').value = data.email || '';
            document.getElementById('vendorPhoneInput').value = data.phone || '';
            document.getElementById('vendorAddressInput').value = data.address || '';
            document.getElementById('vendorGstInput').value = data.gstNumber || '';
            document.getElementById('vendorModalTitle').textContent = 'Edit Vendor';
        }
        modal.show();
    };

    window.editVendor = function (id) {
        axios.get(`${API}/${id}`).then(r => openVendorModal(r.data));
    };

    function saveVendor(e) {
        e.preventDefault();
        const id = document.getElementById('vendorId').value;
        const payload = {
            name: document.getElementById('vendorNameInput').value,
            contactPerson: document.getElementById('vendorContactInput').value,
            email: document.getElementById('vendorEmailInput').value,
            phone: document.getElementById('vendorPhoneInput').value,
            address: document.getElementById('vendorAddressInput').value,
            gstNumber: document.getElementById('vendorGstInput').value,
            orgId: ORG_ID
        };
        const req = id ? axios.patch(`${API}/${id}`, payload) : axios.post(API, payload);
        req.then(() => { modal.hide(); loadVendors(); Swal.fire('Saved!', '', 'success'); })
            .catch(err => Swal.fire('Error', err.response?.data?.message || 'Failed', 'error'));
    }

    window.deleteVendor = function (id) {
        Swal.fire({ title: 'Delete this vendor?', icon: 'warning', showCancelButton: true, confirmButtonText: 'Delete' })
            .then(r => { if (r.isConfirmed) axios.delete(`${API}/${id}`).then(() => loadVendors()); });
    };
})();
