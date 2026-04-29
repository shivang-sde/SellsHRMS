/**
 * SMTP Configuration Module
 * Path: /js/org/notifications/smtp-config.js
 */

(function () {
    'use strict';

    // DOM Elements
    const form = document.getElementById('smtpConfigForm');
    const testBtn = document.getElementById('testSmtpBtn');
    const resetBtn = document.getElementById('resetBtn');
    const togglePassword = document.getElementById('togglePassword');
    const testResult = document.getElementById('testResult');
    const loadingOverlay = document.getElementById('loadingOverlay');
    const configStatus = document.getElementById('configStatus');

    // API Endpoints
    const API = {
        CONFIG: `${APP.CONTEXT_PATH}/api/notifications/email-config`,
        TEST: `${APP.CONTEXT_PATH}/api/notifications/email-config/test`,
        FETCH: `${APP.CONTEXT_PATH}/api/notifications/email-config/org/${APP.ORG_ID}`
    };

    // Initialize
    document.addEventListener('DOMContentLoaded', () => {
        initEventListeners();
        loadCurrentConfig();
    });

    function initEventListeners() {
        form.addEventListener('submit', handleSave);
        testBtn.addEventListener('click', handleTest);
        resetBtn.addEventListener('click', handleReset);
        togglePassword.addEventListener('click', togglePasswordVisibility);

        // Real-time validation
        form.querySelectorAll('input[required]').forEach(input => {
            input.addEventListener('blur', validateField);
        });
    }

    function togglePasswordVisibility() {
        const password = document.getElementById('password');
        const icon = togglePassword.querySelector('i');
        if (password.type === 'password') {
            password.type = 'text';
            icon.classList.replace('fa-eye', 'fa-eye-slash');
        } else {
            password.type = 'password';
            icon.classList.replace('fa-eye-slash', 'fa-eye');
        }
    }

    function validateField(e) {
        const input = e.target;
        if (!input.checkValidity()) {
            input.classList.add('is-invalid');
        } else {
            input.classList.remove('is-invalid');
        }
    }

    async function loadCurrentConfig() {
        try {
            showLoading(true);
            const response = await axios.get(API.FETCH, {
                headers: { 'X-Org-Id': APP.ORG_ID }
            });

            if (response.data?.data) {
                populateForm(response.data.data);
                updateConfigStatus(true);
                updateSummary(response.data.data);
            } else {
                updateConfigStatus(false);
            }
        } catch (error) {
            console.log('No existing config or error:', error.message);
            updateConfigStatus(false);
        } finally {
            showLoading(false);
        }
    }

    function populateForm(config) {
        document.getElementById('smtpHost').value = config.smtpHost || '';
        document.getElementById('smtpPort').value = config.smtpPort || 587;
        document.getElementById('useTls').checked = config.useTls !== false;
        document.getElementById('username').value = config.smtpUsername || '';
        document.getElementById('fromEmail').value = config.fromEmail || '';
        document.getElementById('fromName').value = config.fromName || '';
        document.getElementById('dailyLimit').value = config.dailyLimit || 1000;
        document.getElementById('hourlyLimit').value = config.hourlyLimit || 100;
        document.getElementById('isActive').value = config.isActive ? 'true' : 'false';
        // Never populate password field for security
    }

    function updateConfigStatus(isActive) {
        if (isActive) {
            configStatus.innerHTML = '<i class="fa fa-circle fa-xs text-success me-1"></i>Configured';
            configStatus.className = 'badge bg-success-subtle text-success';
        } else {
            configStatus.innerHTML = '<i class="fa fa-circle fa-xs text-warning me-1"></i>Not Configured';
            configStatus.className = 'badge bg-warning-subtle text-warning';
        }
    }

    function updateSummary(config) {
        document.getElementById('summaryStatus').textContent = config.isActive ? 'Active' : 'Inactive';
        document.getElementById('summaryHost').textContent = config.smtpHost || '-';
        document.getElementById('summaryFrom').textContent =
            `${config.fromName || ''} <${config.fromEmail || '-'}>`;
        document.getElementById('summarySent').textContent =
            `${config.sentToday || 0}/${config.dailyLimit || 1000} today`;
    }

    async function handleSave(e) {
        e.preventDefault();

        if (!form.checkValidity()) {
            e.stopPropagation();
            form.classList.add('was-validated');
            showToast('warning', 'Please fill all required fields');
            return;
        }

        const payload = {
            orgId: APP.ORG_ID,
            smtpHost: document.getElementById('smtpHost').value.trim(),
            smtpPort: parseInt(document.getElementById('smtpPort').value),
            useTls: document.getElementById('useTls').checked,
            smtpUsername: document.getElementById('username').value.trim(),
            smtpPassword: document.getElementById('password').value,
            fromEmail: document.getElementById('fromEmail').value.trim(),
            fromName: document.getElementById('fromName').value.trim(),
            isActive: document.getElementById('isActive').value === 'true',
            dailyLimit: parseInt(document.getElementById('dailyLimit').value),
            hourlyLimit: parseInt(document.getElementById('hourlyLimit').value)
        };

        console.log("payload::", payload);

        try {
            showLoading(true);
            const response = await axios.post(API.CONFIG, payload);

            if (response.data?.success) {
                showToast('success', 'SMTP configuration saved successfully!');
                form.classList.remove('was-validated');
                document.getElementById('password').value = ''; // Clear password after save
                loadCurrentConfig();
            }
        } catch (error) {
            const message = error.response?.data?.message || 'Failed to save configuration';
            showToast('error', message);
            showErrorModal('Save Failed', message);
        } finally {
            showLoading(false);
        }
    }

    async function handleTest() {
        // Validate required fields first
        const requiredFields = ['smtpHost', 'smtpPort', 'username', 'password', 'fromEmail'];
        let isValid = true;

        requiredFields.forEach(fieldId => {
            const field = document.getElementById(fieldId);
            if (!field.value.trim()) {
                field.classList.add('is-invalid');
                isValid = false;
            }
        });

        if (!isValid) {
            showToast('warning', 'Please fill all required fields before testing');
            return;
        }

        const payload = {
            orgId: APP.ORG_ID,
            smtpHost: document.getElementById('smtpHost').value.trim(),
            smtpPort: parseInt(document.getElementById('smtpPort').value),
            useTls: document.getElementById('useTls').checked,
            smtpUsername: document.getElementById('username').value.trim(),
            smtpPassword: document.getElementById('password').value,
            fromEmail: document.getElementById('fromEmail').value.trim(),
            fromName: document.getElementById('fromName').value.trim(),
            isActive: document.getElementById('isActive').value === 'true',
            dailyLimit: parseInt(document.getElementById('dailyLimit').value),
            hourlyLimit: parseInt(document.getElementById('hourlyLimit').value)
        };

        console.log("payload::", payload);

        try {
            showLoading(true);
            showTestResult('info', 'Testing connection...', 'Please wait');

            const response = await axios.post(API.TEST, payload);

            if (response.data?.success) {
                showTestResult('success', '✓ Connection Successful!',
                    'Your SMTP settings are working correctly. A test email was sent.');
                showToast('success', 'SMTP test successful!');
            } else {
                showTestResult('danger', '✗ Connection Failed',
                    response.data?.message || 'Unable to connect to SMTP server');
            }
        } catch (error) {
            const message = error.response?.data?.message || error.message || 'Test failed';
            showTestResult('danger', '✗ Test Failed',
                `Error: ${message}. Please verify your credentials and network.`);
            showErrorModal('SMTP Test Failed', message);
        } finally {
            showLoading(false);
        }
    }

    function showTestResult(type, title, message) {
        testResult.classList.remove('d-none', 'alert-success', 'alert-danger', 'alert-warning', 'alert-info');
        testResult.classList.add(`alert-${type}`);

        document.getElementById('testResultIcon').innerHTML =
            type === 'success' ? '<i class="fa fa-check-circle me-1"></i>' :
                type === 'danger' ? '<i class="fa fa-times-circle me-1"></i>' :
                    '<i class="fa fa-info-circle me-1"></i>';
        document.getElementById('testResultTitle').textContent = title;
        document.getElementById('testResultMessage').textContent = message;

        // Auto-hide success after 5 seconds
        if (type === 'success') {
            setTimeout(() => {
                testResult.classList.add('d-none');
            }, 5000);
        }
    }

    function handleReset() {
        Swal.fire({
            title: 'Reset Form?',
            text: 'This will clear all entered values. Continue?',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Yes, Reset',
            cancelButtonText: 'Cancel',
            customClass: { confirmButton: 'btn-primary-hrms', cancelButton: 'btn-secondary-hrms' }
        }).then((result) => {
            if (result.isConfirmed) {
                form.reset();
                form.classList.remove('was-validated');
                document.getElementById('smtpPort').value = 587;
                document.getElementById('useTls').checked = true;
                document.getElementById('dailyLimit').value = 1000;
                document.getElementById('hourlyLimit').value = 100;
                testResult.classList.add('d-none');
                showToast('Form reset', 'info');
            }
        });
    }

    function showLoading(show) {
        if (show) {
            loadingOverlay.classList.remove('d-none');
            document.body.style.overflow = 'hidden';
        } else {
            loadingOverlay.classList.add('d-none');
            document.body.style.overflow = '';
        }
    }

    function showToast(message, type = 'info') {
        if (typeof window.showToast === 'function') {
            window.showToast(message, type);
        } else {
            // Fallback toast
            const toast = document.createElement('div');
            toast.className = `toast align-items-center text-bg-${type === 'error' ? 'danger' : type} border-0`;
            toast.setAttribute('role', 'alert');
            toast.innerHTML = `
                <div class="d-flex">
                    <div class="toast-body">${message}</div>
                    <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
                </div>
            `;
            document.getElementById('toast-container')?.appendChild(toast);
            const bsToast = new bootstrap.Toast(toast);
            bsToast.show();
            toast.addEventListener('hidden.bs.toast', () => toast.remove());
        }
    }

    function showErrorModal(title, message) {
        if (typeof window.showErrorModal === 'function') {
            window.showErrorModal(title, message);
        } else {
            Swal.fire({
                icon: 'error',
                title: title || 'Error',
                text: message || 'An unexpected error occurred',
                confirmButtonText: 'OK',
                customClass: { confirmButton: 'btn-primary-hrms' }
            });
        }
    }

})();