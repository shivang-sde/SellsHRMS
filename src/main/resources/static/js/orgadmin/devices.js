document.addEventListener("DOMContentLoaded", function () {
    loadDevices();
});

let addDeviceModal;

function loadDevices() {
    const orgId = window.APP.ORG_ID;

    axios.get(`${window.APP.CONTEXT_PATH}/api/admin/devices/org/${orgId}`)
        .then(response => {
            const devices = response.data;
            const tbody = document.getElementById("deviceTableBody");
            tbody.innerHTML = "";

            if (devices.length === 0) {
                tbody.innerHTML = `<tr><td colspan="4" class="text-center text-muted">No devices found.</td></tr>`;
                return;
            }

            devices.forEach(device => {
                const row = `
                    <tr>
                        <td>
                            <div class="fw-bold">${device.name}</div>
                        </td>
                        <td><code>${device.deviceCode}</code></td>
                        <td>
                            <span class="badge ${device.status === 'ACTIVE' ? 'bg-success' : 'bg-secondary'}">
                                ${device.status}
                            </span>
                        </td>
                        <td>${new Date(device.createdAt).toLocaleDateString()}</td>
                    </tr>
                `;
                tbody.insertAdjacentHTML("beforeend", row);
            });
        })
        .catch(error => {
            console.error("Error loading devices", error);
            document.getElementById("deviceTableBody").innerHTML =
                `<tr><td colspan="4" class="text-center text-danger">Error loading devices</td></tr>`;
        });
}

function openAddDeviceModal() {
    addDeviceModal = new bootstrap.Modal(document.getElementById('addDeviceModal'));
    document.getElementById("addDeviceForm").reset();
    document.getElementById("apiKeySection").classList.add("d-none");
    document.getElementById("addDeviceForm").classList.remove("d-none");
    document.getElementById("saveDeviceBtn").classList.remove("d-none");
    addDeviceModal.show();
}

function saveDevice() {
    const orgId = window.APP.ORG_ID;
    const form = document.getElementById("addDeviceForm");

    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }

    const name = form.querySelector('[name="name"]').value;
    const deviceCode = form.querySelector('[name="deviceCode"]').value;

    document.getElementById("saveDeviceBtn").disabled = true;

    axios.post(`${window.APP.CONTEXT_PATH}/api/admin/devices/org/${orgId}`, null, {
        params: {
            name: name,
            deviceCode: deviceCode
        }
    })
        .then(response => {
            // Success
            const device = response.data;
            document.getElementById("addDeviceForm").classList.add("d-none");
            document.getElementById("saveDeviceBtn").classList.add("d-none");

            document.getElementById("apiKeySection").classList.remove("d-none");
            document.getElementById("generatedApiKey").value = device.apiKey;

            loadDevices(); // Refresh list
        })
        .catch(error => {
            console.error("Error creating device", error);
            alert(error.response?.data?.message || "Failed to create device");
            document.getElementById("saveDeviceBtn").disabled = false;
        });
}

function copyApiKey() {
    const copyText = document.getElementById("generatedApiKey");
    copyText.select();
    copyText.setSelectionRange(0, 99999);
    navigator.clipboard.writeText(copyText.value);

    // Optional: Tooltip or toast could be added here
}
