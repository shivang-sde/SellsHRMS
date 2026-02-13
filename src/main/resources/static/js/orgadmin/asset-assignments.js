(function () {
    const ORG_ID = window.APP.ORG_ID;
    const API = `/api/asset-assignments/org/${ORG_ID}`;

    document.addEventListener('DOMContentLoaded', loadAssignments);

    function loadAssignments() {
        axios.get(API).then(r => {
            const tbody = document.getElementById('assignmentTableBody');
            if (!r.data.length) {
                tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted">No assignments found</td></tr>';
                return;
            }
            tbody.innerHTML = r.data.map(a => `
                <tr>
                    <td>${a.assetCode || '-'}</td>
                    <td>${a.assetName || '-'}</td>
                    <td>${a.employeeName || '-'}</td>
                    <td>${a.assignedDate || '-'}</td>
                    <td>${a.returnDate || '-'}</td>
                    <td><span class="badge bg-${a.activeFlag ? 'success' : 'secondary'}">${a.activeFlag ? 'Active' : 'Returned'}</span></td>
                    <td>${a.remarks || '-'}</td>
                </tr>
            `).join('');
        });
    }
})();
