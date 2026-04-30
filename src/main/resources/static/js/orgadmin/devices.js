let addDeviceModal;

// Generate device code based on device name
function generateDeviceCode() {
    const deviceName = document.getElementById("deviceName").value;
    const customCode = document.getElementById("customDeviceCode").value;

    if (customCode) {
        document.getElementById("deviceCode").value = customCode.toUpperCase().replace(/[^A-Z0-9]/g, '-');
        return;
    }

    if (deviceName) {
        // Generate code: uppercase, replace spaces with hyphens, remove special chars
        let generatedCode = deviceName.toUpperCase().replace(/[^A-Z0-9]/g, '-');
        generatedCode = generatedCode.replace(/-+/g, '-').replace(/^-|-$/g, '');

        // Add random suffix for uniqueness
        const randomSuffix = Math.floor(Math.random() * 1000).toString().padStart(3, '0');
        generatedCode = generatedCode + '-' + randomSuffix;

        document.getElementById("deviceCode").value = generatedCode;
    } else {
        document.getElementById("deviceCode").value = '';
    }
}

// Listen for custom device code changes
document.addEventListener("DOMContentLoaded", function () {
    const customCodeInput = document.getElementById("customDeviceCode");
    if (customCodeInput) {
        customCodeInput.addEventListener("input", function () {
            if (this.value) {
                const formattedCode = this.value.toUpperCase().replace(/[^A-Z0-9]/g, '-');
                document.getElementById("deviceCode").value = formattedCode;
            } else {
                generateDeviceCode();
            }
        });
    }

    loadDevices();
});

// Toggle API key visibility in modal
function toggleApiKeyVisibility() {
    const apiKeyInput = document.getElementById("generatedApiKey");
    const toggleIcon = document.getElementById("toggleApiKeyIcon");

    if (apiKeyInput.type === "password") {
        apiKeyInput.type = "text";
        toggleIcon.className = "fas fa-eye-slash";
    } else {
        apiKeyInput.type = "password";
        toggleIcon.className = "fas fa-eye";
    }
}

// Toggle device status (activate/deactivate)
function toggleDeviceStatus(deviceId, currentStatus) {
    const newStatus = currentStatus === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';

    axios.put(`${window.APP.CONTEXT_PATH}/api/admin/devices/${deviceId}/status`, null, {
        params: { status: newStatus }
    })
        .then(response => {
            showToast("success", `Device ${newStatus.toLowerCase()}d successfully`);
            loadDevices(); // Refresh the list
        })
        .catch(error => {
            console.error("Error toggling device status", error);
            showToast("error", error.response?.data?.message || "Failed to update device status");
        });
}

// Copy function for table API keys
function copyApiKey(apiKey) {
    if (!apiKey) {
        showToast("error", "No API key to copy");
        return;
    }
    navigator.clipboard.writeText(apiKey).then(() => {
        showToast("success", "API Key copied to clipboard");
    }).catch(() => {
        showToast("error", "Failed to copy API key");
    });
}

// Copy API key from modal
function copyApiKeyFromModal() {
    const apiKeyInput = document.getElementById("generatedApiKey");
    if (apiKeyInput && apiKeyInput.value) {
        navigator.clipboard.writeText(apiKeyInput.value).then(() => {
            showToast("success", "API Key copied to clipboard");
        }).catch(() => {
            showToast("error", "Failed to copy API key");
        });
    }
}

// Open add device modal
function openAddDeviceModal() {
    addDeviceModal = new bootstrap.Modal(document.getElementById('addDeviceModal'));
    document.getElementById("addDeviceForm").reset();
    document.getElementById("customDeviceCode").value = '';
    document.getElementById("deviceCode").value = '';
    document.getElementById("deviceName").value = '';
    document.getElementById("apiKeySection").classList.add("d-none");
    document.getElementById("addDeviceForm").classList.remove("d-none");
    document.getElementById("saveDeviceBtn").classList.remove("d-none");
    addDeviceModal.show();
}

// Save device function
function saveDevice() {
    const orgId = window.APP.ORG_ID;
    const form = document.getElementById("addDeviceForm");
    const nameInput = form.querySelector('[name="name"]');

    if (!nameInput.value.trim()) {
        showToast("error", "Device name is required");
        nameInput.focus();
        return;
    }

    let deviceCode = document.getElementById("deviceCode").value;
    const customCode = document.getElementById("customDeviceCode").value;

    // If custom code provided, use it; otherwise use auto-generated
    if (customCode) {
        deviceCode = customCode.toUpperCase().replace(/[^A-Z0-9]/g, '-');
    } else if (!deviceCode && nameInput.value) {
        // Auto-generate if no code available
        deviceCode = nameInput.value.toUpperCase().replace(/[^A-Z0-9]/g, '-') + '-' + Math.floor(Math.random() * 1000).toString().padStart(3, '0');
    }

    if (!deviceCode) {
        showToast("error", "Device code is required");
        return;
    }

    document.getElementById("saveDeviceBtn").disabled = true;
    document.getElementById("saveDeviceBtn").innerHTML = '<i class="fas fa-spinner fa-spin"></i> Creating...';

    axios.post(`${window.APP.CONTEXT_PATH}/api/admin/devices/org/${orgId}`, null, {
        params: {
            name: nameInput.value.trim(),
            deviceCode: deviceCode
        }
    })
        .then(response => {
            const device = response.data;
            document.getElementById("addDeviceForm").classList.add("d-none");
            document.getElementById("saveDeviceBtn").classList.add("d-none");

            // Reset and show API key section with hidden key
            document.getElementById("apiKeySection").classList.remove("d-none");
            const apiKeyInput = document.getElementById("generatedApiKey");
            apiKeyInput.value = device.apiKey;
            apiKeyInput.type = "password"; // Hidden by default

            // Reset toggle icon
            const toggleIcon = document.getElementById("toggleApiKeyIcon");
            if (toggleIcon) toggleIcon.className = "fas fa-eye";

            loadDevices(); // Refresh list
            showToast("success", "Device created successfully!");

            // Reset save button after 2 seconds (modal might be closed)
            setTimeout(() => {
                document.getElementById("saveDeviceBtn").disabled = false;
                document.getElementById("saveDeviceBtn").innerHTML = 'Create Device';
            }, 2000);
        })
        .catch(error => {
            console.error("Error creating device", error);
            const errorMsg = error.response?.data?.message || error.message || "Failed to create device";
            showToast("error", errorMsg);
            document.getElementById("saveDeviceBtn").disabled = false;
            document.getElementById("saveDeviceBtn").innerHTML = 'Create Device';
        });
}

// Load devices function
function loadDevices() {
    const orgId = window.APP.ORG_ID;

    axios.get(`${window.APP.CONTEXT_PATH}/api/admin/devices/org/${orgId}`)
        .then(response => {
            const devices = response.data;
            const tbody = document.getElementById("deviceTableBody");
            tbody.innerHTML = "";

            if (devices.length === 0) {
                tbody.innerHTML = `<tr><td colspan="6" class="text-center text-muted">No devices found.</td></tr>`;
                return;
            }

            devices.forEach(device => {
                const row = `
                    <tr>
                        <td>
                            <div class="fw-bold">${escapeHtml(device.name)}</div>
                        </td>
                        <td><code>${escapeHtml(device.deviceCode)}</code></td>
                        <td onclick="copyApiKey('${escapeHtml(device.apiKey)}')" style="cursor: pointer;" title="Click to copy API Key">
                            <i class="fas fa-copy me-1"></i>
                            <code class="api-key-preview">${maskApiKey(device.apiKey)}</code>
                        </td>
                        <td>
                            <span class="badge ${device.status === 'ACTIVE' ? 'bg-success' : 'bg-secondary'}">
                                ${device.status}
                            </span>
                        </td>
                        <td>${formatDate(device.createdAt)}</td>
                        <td>
                            <button class="btn btn-sm ${device.status === 'ACTIVE' ? 'btn-warning' : 'btn-success'}" 
                                    onclick="toggleDeviceStatus(${device.id}, '${device.status}')"
                                    title="${device.status === 'ACTIVE' ? 'Deactivate' : 'Activate'} device">
                                <i class="fas ${device.status === 'ACTIVE' ? 'fa-stop-circle' : 'fa-play-circle'}"></i>
                                ${device.status === 'ACTIVE' ? 'Deactivate' : 'Activate'}
                            </button>
                        </td>
                    </tr>
                `;
                tbody.insertAdjacentHTML("beforeend", row);
            });
        })
        .catch(error => {
            console.error("Error loading devices", error);
            showToast("error", error.response?.data?.message || error.message || "Error loading devices");
            document.getElementById("deviceTableBody").innerHTML =
                `<tr><td colspan="6" class="text-center text-danger">Error loading devices</td></tr>`;
        });
}

// Helper function to mask API key for display
function maskApiKey(apiKey) {
    if (!apiKey) return '••••••••';
    if (apiKey.length <= 8) return '••••••••';
    return apiKey.substring(0, 4) + '••••••••' + apiKey.substring(apiKey.length - 4);
}

// Helper function to format date
function formatDate(dateString) {
    if (!dateString) return 'N/A';
    try {
        const date = new Date(dateString);
        return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
    } catch (e) {
        return dateString;
    }
}

// Helper function to escape HTML
function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/[&<>]/g, function (m) {
        if (m === '&') return '&amp;';
        if (m === '<') return '&lt;';
        if (m === '>') return '&gt;';
        return m;
    });
}

// Show toast notification
function showToast(type, message) {
    // Check if toast container exists, if not create it
    let toastContainer = document.getElementById('toast-container');
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.id = 'toast-container';
        toastContainer.style.position = 'fixed';
        toastContainer.style.bottom = '20px';
        toastContainer.style.right = '20px';
        toastContainer.style.zIndex = '9999';
        document.body.appendChild(toastContainer);
    }

    const toastId = 'toast-' + Date.now();
    const bgColor = type === 'success' ? 'bg-success' : 'bg-danger';

    const toastHtml = `
        <div id="${toastId}" class="toast align-items-center text-white ${bgColor} border-0 mb-2" role="alert" aria-live="assertive" aria-atomic="true" data-bs-autohide="true" data-bs-delay="3000">
            <div class="d-flex">
                <div class="toast-body">
                    <i class="fas ${type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle'} me-2"></i>
                    ${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        </div>
    `;

    toastContainer.insertAdjacentHTML('beforeend', toastHtml);
    const toastElement = document.getElementById(toastId);
    const toast = new bootstrap.Toast(toastElement, { delay: 3000 });
    toast.show();

    toastElement.addEventListener('hidden.bs.toast', function () {
        toastElement.remove();
    });
}