const PayrollUtils = {
    formatCurrency: (amount) => {
        return new Intl.NumberFormat('en-IN', {
            style: 'currency',
            currency: 'INR'
        }).format(amount);
    },

    formatDate: (dateString, format = 'short') => {
        const date = new Date(dateString);
        return date.toLocaleDateString('en-IN', {
            year: 'numeric',
            month: format === 'long' ? 'long' : '2-digit',
            day: '2-digit'
        });
    },

    calculateCTC: (base, variable, benefits) => {
        return (base || 0) + (variable || 0) + (benefits || 0);
    },

    getStatusBadge: (status) => {
        const badges = {
            'ACTIVE': 'bg-success',
            'INACTIVE': 'bg-secondary',
            'DRAFT': 'bg-secondary',
            'READY': 'bg-info',
            'APPROVED': 'bg-primary',
            'PROCESSING': 'bg-warning',
            'COMPLETED': 'bg-success',
            'CANCELLED': 'bg-danger'
        };
        return badges[status] || 'bg-secondary';
    },

    debounce: (func, wait) => {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }
};

window.PayrollUtils = PayrollUtils;