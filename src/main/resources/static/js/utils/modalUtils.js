// modalUtils.js - Bootstrap modal management utilities

document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.modal').forEach(modal => {
        document.body.appendChild(modal);
    });
});


const modalUtils = {
    instances: {},

    show() {
        if (document.getElementById('globalLoadingSpinner')) return;
        const spinner = document.createElement('div');
        spinner.className = 'loading-overlay';
        spinner.id = 'globalLoadingSpinner';
        spinner.innerHTML = `
    <div class="spinner-border text-primary" role="status">
      <span class="visually-hidden">Loading...</span>
    </div>
  `;
        document.body.appendChild(spinner);
    },

    open(modalId, data = null) {
        const modalEl = document.getElementById(modalId);
        if (!modalEl) {
            console.error(`Modal ${modalId} not found`);
            return;
        }

        if (!this.instances[modalId]) {
            this.instances[modalId] = new bootstrap.Modal(modalEl);
        }

        if (data) {
            this.populateForm(modalId, data);
        }

        this.instances[modalId].show();
    },

    close(modalId) {
        let instance = this.instances[modalId];
        const modalEl = document.getElementById(modalId);
        if (!instance && modalEl) {
            // Bootstrap auto-created modal instance
            instance = bootstrap.Modal.getInstance(modalEl) || new bootstrap.Modal(modalEl);
            this.instances[modalId] = instance;
        }
        if (instance) instance.hide();
    },

    populateForm(modalId, data) {
        const form = document.querySelector(`#${modalId} form`);
        if (!form) return;

        Object.keys(data).forEach(key => {
            const input = form.querySelector(`[name="${key}"]`);
            if (input) {
                if (input.type === 'checkbox') {
                    input.checked = data[key];
                } else {
                    input.value = data[key] || '';
                }
            }
        });
    },

    resetForm(modalId) {
        const form = document.querySelector(`#${modalId} form`);
        if (form) {
            form.reset();
            form.removeAttribute('data-edit-id');
        }
    },

    getFormData(modalId) {
        const form = document.querySelector(`#${modalId} form`);
        if (!form) return {};

        const formData = new FormData(form);
        const data = {};

        for (let [key, value] of formData.entries()) {
            data[key] = value;
        }

        return data;
    },

    async confirm(title, message, onConfirm) { // Add async here
        const result = await Swal.fire({ // Use await instead of .then()
            title: title,
            text: message,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#0ea5e9',
            cancelButtonColor: '#6b7280',
            confirmButtonText: 'Yes, proceed',
            cancelButtonText: 'Cancel'
        });

        if (result.isConfirmed && onConfirm) {
            await onConfirm(); // Use await here to ensure the API call finishes
        }
    }
};

// Toast Notifications
function showToast(message, type = 'info', duration = 3000) {
    const container = document.getElementById('toast-container');
    if (!container) return;

    const icons = {
        success: '<i class="fas fa-check-circle"></i>',
        error: '<i class="fas fa-exclamation-circle"></i>',
        warning: '<i class="fas fa-exclamation-triangle"></i>',
        info: '<i class="fas fa-info-circle"></i>'
    };

    const toast = document.createElement('div');
    toast.className = `toast-item toast-${type} show`;
    toast.innerHTML = `
        ${icons[type] || icons.info}
        <span>${message}</span>
        <button class="toast-close" onclick="this.parentElement.remove()">×</button>
    `;

    container.appendChild(toast);

    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 300);
    }, duration);
}

// Loading Spinner
const loadingUtils = {
    show(target = 'body') {
        const spinner = document.createElement('div');
        spinner.className = 'loading-overlay';
        spinner.innerHTML = `
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Loading...</span>
            </div>
        `;
        spinner.id = 'globalLoadingSpinner';
        document.querySelector(target).appendChild(spinner);
    },

    hide() {
        const spinner = document.getElementById('globalLoadingSpinner');
        if (spinner) spinner.remove();
    }
};

// Date Formatting
function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

function formatDateTime(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Status Badge Generator
function getStatusBadge(status) {
    const badges = {
        'ACTIVE': 'success',
        'COMPLETED': 'primary',
        'ON_HOLD': 'warning',
        'CANCELLED': 'danger',
        'PENDING': 'warning',
        'IN_PROGRESS': 'info',
        'OPEN': 'info',
        'CLOSED': 'secondary',
        'RESOLVED': 'success'
    };

    const color = badges[status] || 'secondary';
    return `<span class="badge bg-${color}">${status.replace(/_/g, ' ')}</span>`;
}

// Priority Badge Generator
function getPriorityBadge(priority) {
    const badges = {
        'HIGH': 'danger',
        'MEDIUM': 'warning',
        'LOW': 'info'
    };

    const color = badges[priority] || 'secondary';
    return `<span class="badge bg-${color}">${priority}</span>`;
}