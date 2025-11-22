<%-- Create Organisation Admin Content - REFRACTORED for OrgAdminDTO --%>
    <div class="row">
        <div class="col-lg-8">
            <div class="card shadow-sm">
                <div class="card-header">
                    Create New Organisation Admin
                </div>
                <div class="card-body">
                    <form id="createAdminForm">
                        <div class="mb-3">
                            <label for="adminFullName" class="form-label">Full Name</label>
                            <input type="text" class="form-control" id="adminFullName" required>
                        </div>
                        <div class="mb-3">
                            <label for="adminEmail" class="form-label">Admin Email</label>
                            <input type="email" class="form-control" id="adminEmail" required>
                        </div>
                        <div class="mb-3">
                            <label for="adminPassword" class="form-label">Password</label>
                            <input type="password" class="form-control" id="adminPassword" required>
                        </div>

                        <div class="mb-3">
                            <label for="organisationId" class="form-label">Assign to Organisation</label>
                            <select class="form-select" id="organisationId" required>
                                <option value="">Loading Organisations...</option>
                            </select>
                            <small id="orgLoadError" class="text-danger"></small>
                        </div>
                        <div class="d-grid">
                            <button type="submit" class="btn btn-success">Create Admin</button>
                        </div>
                        <div id="createAdminMessage" class="mt-3"></div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <script>
        $(document).ready(function () {

            // --- 1. FETCH AND POPULATE ORGANISATIONS DROPDOWN ---
            fetchOrganisationsForDropdown();

            function fetchOrganisationsForDropdown() {
                const selectOrg = $("#organisationId");
                const errorMsg = $("#orgLoadError");

                // Re-using the same /api/organisations endpoint
                $.get("/api/organisations")
                    .done(function (organisations) {
                        selectOrg.empty();
                        errorMsg.empty();

                        if (organisations.length === 0) {
                            selectOrg.append('<option value="">No organisations available</option>');
                        } else {
                            selectOrg.append('<option value="">-- Select Organisation --</option>');
                            organisations.forEach(function (org) {
                                // The value passed to the backend will be org.id
                                selectOrg.append(`<option value="${org.id}">${org.name} (ID: ${org.id})</option>`);
                            });
                        }
                    })
                    .fail(function (jqXHR, textStatus, errorThrown) {
                        selectOrg.empty();
                        selectOrg.append('<option value="">Failed to load organisations</option>');
                        errorMsg.text("Error loading organisations. Check API endpoint /api/organisations.");
                    });
            }

            // --- 2. FORM SUBMISSION HANDLER ---
            $("#createAdminForm").submit(function (e) {
                e.preventDefault();
                const messageDiv = $("#createAdminMessage");
                messageDiv.empty();

                const adminData = {
                    fullName: $("#adminFullName").val(),
                    email: $("#adminEmail").val(),
                    password: $("#adminPassword").val(),
                    // organisationId comes directly from the selected option's value
                    organisationId: $("#organisationId").val()
                };

                $.ajax({
                    type: "POST",
                    url: "/api/superadmin/organisation-admin",
                    contentType: "application/json",
                    data: JSON.stringify(adminData),
                    success: function (response) {
                        messageDiv.html('<div class="alert alert-success">Organisation Admin created successfully! ID: ' + response.id + '</div>');
                        $("#createAdminForm")[0].reset();
                        // Optional: Re-fetch list to update Org Admin count if applicable
                        fetchOrganisationsForDropdown();
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        const errorMsg = jqXHR.responseJSON ? (jqXHR.responseJSON.error || JSON.stringify(jqXHR.responseJSON)) : "An unknown error occurred.";
                        messageDiv.html('<div class="alert alert-danger">Creation Failed: ' + errorMsg + '</div>');
                    }
                });
            });
        });
    </script>