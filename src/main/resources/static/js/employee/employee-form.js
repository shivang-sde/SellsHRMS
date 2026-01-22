$(document).ready(function() {
    const orgId = window.APP.ORG_ID || $('#globalOrgId').val();
    const employeeId = $('#employeeId').val();
    const isEditMode = !!employeeId;

    // Load dropdown data
    loadDepartments();
    loadDesignations();
    loadManagers();
    // loadShifts();

    // If edit mode, load employee data
    if (isEditMode) {
        loadEmployeeData(employeeId);
        $('input[name="password"]').prop('required', false);
    }

    // Copy local address to permanent
    $('#copyLocalAddress').on('click', function() {
        $('input[name="permanentAddress.addressLine1"]').val($('input[name="localAddress.addressLine1"]').val());
        $('input[name="permanentAddress.addressLine2"]').val($('input[name="localAddress.addressLine2"]').val());
        $('input[name="permanentAddress.city"]').val($('input[name="localAddress.city"]').val());
        $('input[name="permanentAddress.state"]').val($('input[name="localAddress.state"]').val());
        $('input[name="permanentAddress.country"]').val($('input[name="localAddress.country"]').val());
        $('input[name="permanentAddress.pincode"]').val($('input[name="localAddress.pincode"]').val());
        showToast('info', 'Address copied');
    });

    $('#departmentSelect').on('change', function() {
    const deptId = $(this).val();
    loadDesignations(deptId);
    });

    // Form submission
    $('#employeeForm').on('submit', function(e) {
        e.preventDefault();
        
        const formData = serializeFormData();
        formData.organisationId = parseInt(orgId);

        // Convert empty strings to null for optional fields
        Object.keys(formData).forEach(key => {
            if (formData[key] === '') {
                formData[key] = null;
            }
        });

        const url = isEditMode ? `/api/employees/${employeeId}` : '/api/employees';
        const method = isEditMode ? 'PUT' : 'POST';

        $('#submitBtn').prop('disabled', true).html('<span class="spinner-border spinner-border-sm me-2"></span>Saving...');

        $.ajax({
            url: url,
            method: method,
            contentType: 'application/json',
            data: JSON.stringify(formData),
            success: function(response) {
                showToast('success', isEditMode ? 'Employee updated successfully' : 'Employee created successfully');
                setTimeout(() => {
                    window.location.href = '/org/employees';
                }, 1500);
            },
            error: function(xhr) {
                let errorMsg = 'Failed to save employee';
                if (xhr.responseJSON && xhr.responseJSON.message) {
                    errorMsg = xhr.responseJSON.message;
                }
                showToast('error', errorMsg);
                $('#submitBtn').prop('disabled', false).html(
                    '<i class="fas fa-save me-2"></i>' + (isEditMode ? 'Update Employee' : 'Create Employee')
                );
            }
        });
    });

    // Load employee data for edit mode
    function loadEmployeeData(id) {
        $.ajax({
            url: `/api/employees/${id}`,
            method: 'GET',
            success: function(data) {
              console.log(data)
                populateForm(data);
            },
            error: function(xhr) {
                showToast('error', 'Failed to load employee data');
            }
        });
    }

    // Populate form with employee data
    function populateForm(data) {
        // Personal info
        $('input[name="firstName"]').val(data.firstName);
        $('input[name="lastName"]').val(data.lastName);
        $('input[name="dob"]').val(data.dob);
        $('select[name="gender"]').val(data.gender);
        $('input[name="personalEmail"]').val(data.personalEmail);
        $('input[name="phone"]').val(data.phone);
        $('input[name="alternatePhone"]').val(data.alternatePhone);
        $('input[name="fatherName"]').val(data.fatherName);
        $('input[name="nationality"]').val(data.nationality);
        $('select[name="maritalStatus"]').val(data.maritalStatus);
        $('input[name="referenceName"]').val(data.referenceName);
        $('input[name="referencePhone"]').val(data.referencePhone);

        // Address
        if (data.localAddress) {
            $('input[name="localAddress.addressLine1"]').val(data.localAddress.addressLine1);
            $('input[name="localAddress.addressLine2"]').val(data.localAddress.addressLine2);
            $('input[name="localAddress.city"]').val(data.localAddress.city);
            $('input[name="localAddress.state"]').val(data.localAddress.state);
            $('input[name="localAddress.country"]').val(data.localAddress.country);
            $('input[name="localAddress.pincode"]').val(data.localAddress.pincode);
        }

        if (data.permanentAddress) {
            $('input[name="permanentAddress.addressLine1"]').val(data.permanentAddress.addressLine1);
            $('input[name="permanentAddress.addressLine2"]').val(data.permanentAddress.addressLine2);
            $('input[name="permanentAddress.city"]').val(data.permanentAddress.city);
            $('input[name="permanentAddress.state"]').val(data.permanentAddress.state);
            $('input[name="permanentAddress.country"]').val(data.permanentAddress.country);
            $('input[name="permanentAddress.pincode"]').val(data.permanentAddress.pincode);
        }

        // Company info
        $('input[name="employeeCode"]').val(data.employeeCode);
        $('input[name="dateOfJoining"]').val(data.dateOfJoining);
        $('input[name="dateOfExit"]').val(data.dateOfExit);
        $('select[name="employmentType"]').val(data.employmentType);
        $('select[name="status"]').val(data.status);

        $('select[name="departmentId"]').val(data.departmentId);
        window.pendingDesignationId = data.designationId;
        loadDesignations(data.departmentId);
        
        $('select[name="designationId"]').val(data.designationId);
        $('select[name="reportingToId"]').val(data.reportingToId);
        $('select[name="shiftId"]').val(data.shiftId);
        
        // Account
        $('input[name="workEmail"]').val(data.email);
    }

    // Serialize form data to JSON
    function serializeFormData() {
        const formData = {};
        
        $('#employeeForm').find('input, select, textarea').each(function() {
            const name = $(this).attr('name');
            if (!name || $(this).attr('type') === 'file') return;
            

      let value = $(this).attr('type') === 'checkbox' ? $(this).is(':checked') : $(this).val();

            
            // Handle nested objects (address)
            if (name.includes('.')) {
                const parts = name.split('.');
                if (!formData[parts[0]]) {
                    formData[parts[0]] = {};
                }
                formData[parts[0]][parts[1]] = value || null;
            } else {
                // Handle numeric fields
                if (name.endsWith('Id') && value) {
                    formData[name] = parseInt(value);
                } else {
                    formData[name] = value || null;
                }
            }
        });

        return formData;
    }

    // ===============================
    // LOAD DROPDOWNS
    // ===============================

    // Load departments
    function loadDepartments() {
        $.ajax({
            url: `/api/departments/org/${orgId}`,
            method: 'GET',
            success: function(data) {
                let options = '<option value="">Select Department</option>';
                data.forEach(dept => {
                    options += `<option value="${dept.id}">${dept.name}</option>`;
                });
                $('#departmentSelect').html(options);
            },
            error: function() {
                console.error('Failed to load departments');
            }
        });
    }

    // Load designations
    function loadDesignations(departmentId) {

        if (!departmentId) {
        $('#designationSelect').html('<option value="">Select Department First</option>');
        return;
        }

        $.ajax({
            url: `/api/designations/department/${departmentId}`,
            method: 'GET',
            success: function(data) {
                let options = '<option value="">Select Designation</option>';
                data.forEach(desg => {
                    options += `<option value="${desg.id}">${desg.title}</option>`;
                });
                $('#designationSelect').html(options);

                if (window.pendingDesignationId) {
                $('#designationSelect').val(window.pendingDesignationId);
                delete window.pendingDesignationId;
            }
            },
            error: function() {
                console.error('Failed to load designations');
            }
        });
    }

    // Load managers (employees for reporting)
    function loadManagers() {
        $.ajax({
            url: `/api/employees/org/${orgId}`,
            method: 'GET',
            success: function(data) {
              console.log("managers data", data)
                let options = '<option value="">Select Manager</option>';
                data.forEach(emp => {
                    // Skip current employee in edit mode
                    if (isEditMode && emp.id == employeeId) return;
                    options += `<option value="${emp.id}">${emp.fullName} (${emp.employeeCode})</option>`;
                });
                $('#reportingToSelect').html(options);
            },
            error: function() {
                console.error('Failed to load managers');
            }
        });
    }

    // // Load shifts
    // function loadShifts() {
    //     $.ajax({
    //         url: `/api/shifts/org/${orgId}`,
    //         method: 'GET',
    //         success: function(data) {
    //             let options = '<option value="">Select Shift</option>';
    //             data.forEach(shift => {
    //                 options += `<option value="${shift.id}">${shift.name}</option>`;
    //             });
    //             $('#shiftSelect').html(options);
    //         },
    //         error: function() {
    //             console.error('Failed to load shifts');
    //         }
    //     });
    // }
});