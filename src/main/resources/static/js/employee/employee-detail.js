$(document).ready(function() {

    // If Org Admin viewing another employee, use the employeeId from hidden field in page
const employeeId = $('#employeeId').val() || window.APP.EMPLOYEE_ID || $('#globalEmployeeId').val();
console.log("Resolved Employee ID:", employeeId);


    if (!employeeId) {
        showToast('error', 'Employee ID not found');
        return;
    }

    // Load employee details
    loadEmployeeDetails();
    
    // Load documents and bank details when tabs are clicked
    $('#documents-tab').on('click', function() {
        loadDocuments();
    });

    $('#bank-tab').on('click', function() {
        loadBankDetails();
    });

    // Upload document form
    $('#uploadDocForm').on('submit', function(e) {
        e.preventDefault();
        uploadDocument();
    });

    // Add bank form
    $('#addBankForm').on('submit', function(e) {
        e.preventDefault();
        addBankAccount();
    });

    // Load employee details
    function loadEmployeeDetails() {
        $.ajax({
            url: `/api/employees/${employeeId}`,
            method: 'GET',
            success: function(data) {
                populateEmployeeDetails(data);
                $('#loadingState').hide();
                $('#employeeContent').show();
            },
            error: function(xhr) {
                $('#loadingState').html(`
                    <div class="alert alert-danger">
                        <i class="fas fa-exclamation-triangle me-2"></i>
                        Failed to load employee details
                    </div>
                `);
            }
        });
    }

    // Populate employee details
    function populateEmployeeDetails(data) {
      console.log(data)

        // Basic info in header
        $('#employeeName').text(data.fullName);
        $('#employeeCode').text(data.employeeCode);
        $('#workEmail').text(data.workEmail || data.email);
        $('#phone').text(data.phone || 'N/A');
        
        // Status badge
        const statusClass = getStatusClass(data.status);
        $('#statusBadge').removeClass().addClass('badge ' + statusClass).text(formatText(data.status));
        
        $('#department').text(data.department || 'N/A');
        $('#designation').text(data.designation || 'N/A');
        $('#employmentType').text(formatText(data.employmentType));

        // Personal info tab
        $('#firstName').text(data.fullName.split(' ')[0] || '--');
        $('#lastName').text(data.fullName.split(' ')[1] || '--');
        $('#dob').text(data.dob || '--');
        $('#gender').text(formatText(data.gender) || '--');
        $('#personalEmail').text(data.personalEmail || data.email);
        $('#alternatePhone').text(data.alternatePhone || data.phone || '--');
        $('#fatherName').text(data.fatherName || '--');
        $('#nationality').text(data.nationality || '--');
        $('#maritalStatus').text(formatText(data.maritalStatus) || '--');
        $('#referenceName').text(data.referenceName || '--');
        $('#referencePhone').text(data.referencePhone || '--');

        // Address tab
        if (data.localAddress) {
            $('#localAddressContent').html(formatAddress(data.localAddress));
        }
        if (data.permanentAddress) {
            $('#permanentAddressContent').html(formatAddress(data.permanentAddress));
        }

        // Company info tab
        $('#empCode').text(data.employeeCode || '--');
        $('#dateOfJoining').text(data.dateOfJoining || '--');
        $('#dateOfExit').text(data.dateOfExit || '--');
        $('#empType').text(formatText(data.employmentType) || '--');
        $('#empStatus').text(formatText(data.status) || '--');
        $('#organisation').text(data.organisation || '--');
        $('#dept').text(data.department || '--');
        $('#desg').text(data.designation || '--');
        $('#reportingTo').text(data.reportingToName || '--');
        $('#shift').text(data.shift || '--');

        // Photo
        if (data.photoUrl) {
            $('#employeePhoto').attr('src', data.photoUrl);
        }

        // Set employeeId for modals
        $('#bankEmployeeId').val(employeeId);
    }

    // Load documents
    function loadDocuments() {
        $('#documentsList').html('<p class="text-center text-muted">Loading...</p>');
        
        $.ajax({
            url: `/api/employee/documents/${employeeId}`,
            method: 'GET',
            success: function(data) {
                if (!data || data.length === 0) {
                    $('#documentsList').html('<p class="text-center text-muted">No documents uploaded</p>');
                    return;
                }

                let html = '<div class="list-group">';
                data.forEach(doc => {
                    const icon = getDocIcon(doc.documentType);
                    html += `
                        <div class="list-group-item d-flex justify-content-between align-items-center">
                            <div>
                                <i class="${icon} me-2"></i>
                                <strong>${formatText(doc.documentType)}</strong>
                                <br>
                                <small class="text-muted">Uploaded: ${doc.uploadedAt || 'N/A'}</small>
                            </div>
                            <div class="btn-group btn-group-sm">
                                ${doc.fileUrl ? `<a href="${doc.fileUrl}" target="_blank" class="btn btn-outline-primary">
                                    <i class="fas fa-download"></i>
                                </a>` : ''}
                                ${doc.externalUrl ? `<a href="${doc.externalUrl}" target="_blank" class="btn btn-outline-info">
                                    <i class="fas fa-external-link-alt"></i>
                                </a>` : ''}

                            </div>
                        </div>
                    `;
                });
                html += '</div>';

                                //                 <button class="btn btn-outline-danger btn-delete-doc" data-id="${doc.id}">
                                //     <i class="fas fa-trash"></i>
                                // </button>

                $('#documentsList').html(html);

                // Attach delete handlers
                $('.btn-delete-doc').on('click', function() {
                    const docId = $(this).data('id');
                    if (confirm('Are you sure you want to delete this document?')) {
                        deleteDocument(docId);
                    }
                });
            },
            error: function() {
                $('#documentsList').html('<p class="text-center text-danger">Failed to load documents</p>');
            }
        });
    }

    // Upload document
    function uploadDocument() {
        const docType = $('#uploadDocForm select[name="documentType"]').val();
        const file = $('#docFile')[0].files[0];
        const url = $('#docUrl').val();

        if (!docType) {
            showToast('error', 'Please select document type');
            return;
        }

        if (!file && !url) {
            showToast('error', 'Please choose a file or provide a URL');
            return;
        }

        if (file) {
            // Upload file
            const formData = new FormData();
            formData.append('employeeId', employeeId);
            formData.append('documentType', docType);
            formData.append('file', file);

            $.ajax({
                url: '/api/employee/documents/upload',
                method: 'POST',
                data: formData,
                processData: false,
                contentType: false,
                success: function() {
                    showToast('success', 'Document uploaded successfully');
                    $('#uploadDocModal').modal('hide');
                    $('#uploadDocForm')[0].reset();
                    loadDocuments();
                },
                error: function() {
                    showToast('error', 'Failed to upload document');
                }
            });
        } else if (url) {
            // Save URL
            $.ajax({
                url: '/api/employee/documents/link',
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({
                    employeeId: parseInt(employeeId),
                    documentType: docType,
                    externalUrl: url
                }),
                success: function() {
                    showToast('success', 'Document link saved successfully');
                    $('#uploadDocModal').modal('hide');
                    $('#uploadDocForm')[0].reset();
                    loadDocuments();
                },
                error: function() {
                    showToast('error', 'Failed to save document link');
                }
            });
        }
    }

    // Delete document
    function deleteDocument(docId) {
        $.ajax({
            url: `/api/employee/documents/${docId}`,
            method: 'DELETE',
            success: function() {
                showToast('success', 'Document deleted successfully');
                loadDocuments();
            },
            error: function() {
                showToast('error', 'Failed to delete document');
            }
        });
    }

    // Load bank details
    function loadBankDetails() {
        $('#bankDetailsList').html('<p class="text-center text-muted">Loading...</p>');
        
        $.ajax({
            url: `/api/employee/bank/${employeeId}`,
            method: 'GET',
            success: function(data) {
                if (!data || data.length === 0) {
                    $('#bankDetailsList').html('<p class="text-center text-muted">No bank accounts added</p>');
                    return;
                }

                let html = '<div class="row">';
                data.forEach(bank => {
                    html += `
                        <div class="col-md-6 mb-3">
                            <div class="card">
                                <div class="card-body">
                                    <div class="d-flex justify-content-between align-items-start">
                                        <div>
                                            <h6 class="card-title">
                                                ${bank.bankName}
                                                ${bank.isPrimaryAccount ? '<span class="badge bg-success ms-2">Primary</span>' : ''}
                                            </h6>
                                        </div>
                                        <button class="btn btn-sm btn-outline-danger btn-delete-bank" data-id="${bank.id}">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                    </div>
                                    <hr>
                                    <p class="mb-1"><strong>Account Number:</strong> ${bank.accountNumber}</p>
                                    <p class="mb-1"><strong>IFSC Code:</strong> ${bank.ifscCode}</p>
                                    <p class="mb-0"><strong>Branch:</strong> ${bank.branch || 'N/A'}</p>
                                </div>
                            </div>
                        </div>
                    `;
                });
                html += '</div>';

                $('#bankDetailsList').html(html);

                // Attach delete handlers
                $('.btn-delete-bank').on('click', function() {
                    const bankId = $(this).data('id');
                    if (confirm('Are you sure you want to delete this bank account?')) {
                        deleteBankAccount(bankId);
                    }
                });
            },
            error: function() {
                $('#bankDetailsList').html('<p class="text-center text-danger">Failed to load bank details</p>');
            }
        });
    }

    // Add bank account
    function addBankAccount() {
        const formData = {
            employeeId: parseInt(employeeId),
            bankName: $('#addBankForm input[name="bankName"]').val(),
            accountNumber: $('#addBankForm input[name="accountNumber"]').val(),
            ifscCode: $('#addBankForm input[name="ifscCode"]').val(),
            branch: $('#addBankForm input[name="branch"]').val(),
            isPrimaryAccount: $('#addBankForm input[name="isPrimaryAccount"]').is(':checked')
        };

        $.ajax({
            url: '/api/employee/bank',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            success: function() {
                showToast('success', 'Bank account added successfully');
                $('#addBankModal').modal('hide');
                $('#addBankForm')[0].reset();
                loadBankDetails();
            },
            error: function() {
                showToast('error', 'Failed to add bank account');
            }
        });
    }

    // Delete bank account
    function deleteBankAccount(bankId) {
        $.ajax({
            url: `/api/employee/bank/${bankId}`,
            method: 'DELETE',
            success: function() {
                showToast('success', 'Bank account deleted successfully');
                loadBankDetails();
            },
            error: function() {
                showToast('error', 'Failed to delete bank account');
            }
        });
    }

    // Utility functions
    function formatAddress(addr) {
        if (!addr) return '<p class="text-muted">No address available</p>';
        
        const parts = [
            addr.addressLine1,
            addr.addressLine2,
            addr.city,
            addr.state,
            addr.country,
            addr.pincode
        ].filter(p => p);

        return parts.length > 0 ? '<p>' + parts.join('<br>') + '</p>' : '<p class="text-muted">No address available</p>';
    }

    function getStatusClass(status) {
        const classes = {
            'ACTIVE': 'bg-success',
            'INACTIVE': 'bg-secondary',
            'ON_LEAVE': 'bg-warning text-dark',
            'TERMINATED': 'bg-danger'
        };
        return classes[status] || 'bg-secondary';
    }

    function getDocIcon(type) {
        const icons = {
            'AADHAR': 'fas fa-id-card',
            'PAN': 'fas fa-credit-card',
            'PASSPORT': 'fas fa-passport',
            'DRIVING_LICENSE': 'fas fa-car',
            'RESUME': 'fas fa-file-alt',
            'OFFER_LETTER': 'fas fa-file-contract',
            'CERTIFICATE': 'fas fa-certificate',
            'OTHER': 'fas fa-file'
        };
        return icons[type] || 'fas fa-file';
    }

    function formatText(text) {
        if (!text) return 'N/A';
        return text.replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase());
    }
});