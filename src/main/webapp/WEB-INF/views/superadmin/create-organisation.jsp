<%-- Create Organisation Content - REFRACTORED for OrganisationRequest DTO --%>
    <div class="row">
        <div class="col-lg-8">
            <div class="card shadow-sm">
                <div class="card-header">
                    New Organisation Details
                </div>
                <div class="card-body">
                    <form id="createOrgForm">
                        <div class="mb-3">
                            <label for="orgName" class="form-label">Organisation Name</label>
                            <input type="text" class="form-control" id="orgName" required>
                        </div>
                        <div class="mb-3">
                            <label for="orgDomain" class="form-label">Domain (e.g., example.com)</label>
                            <input type="text" class="form-control" id="orgDomain" required>
                        </div>
                        <div class="mb-3">
                            <label for="orgEmail" class="form-label">Contact Email</label>
                            <input type="email" class="form-control" id="orgEmail" required>
                        </div>
                        <div class="mb-3">
                            <label for="orgPhone" class="form-label">Contact Phone Number</label>
                            <input type="text" class="form-control" id="orgPhone">
                        </div>
                        <div class="mb-3">
                            <label for="maxEmployees" class="form-label">Maximum Employees</label>
                            <input type="number" class="form-control" id="maxEmployees" value="10" required>
                        </div>
                        <div class="d-grid">
                            <button type="submit" class="btn btn-success">Create Organisation</button>
                        </div>
                        <div id="createOrgMessage" class="mt-3"></div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <script>
        $(document).ready(function () {
            $("#createOrgForm").submit(function (e) {
                e.preventDefault();
                const messageDiv = $("#createOrgMessage");
                messageDiv.empty();

                const orgData = {
                    name: $("#orgName").val(),
                    domain: $("#orgDomain").val(),
                    contactEmail: $("#orgEmail").val(),
                    contactPhone: $("#orgPhone").val(),
                    maxEmployees: parseInt($("#maxEmployees").val()) // Added and parsed
                };

                $.ajax({
                    type: "POST",
                    url: "/api/organisations",
                    contentType: "application/json",
                    data: JSON.stringify(orgData),
                    success: function (response) {
                        messageDiv.html('<div class="alert alert-success">Organisation created successfully! ID: ' + response.id + '</div>');
                        $("#createOrgForm")[0].reset();
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        const errorMsg = jqXHR.responseJSON ? (jqXHR.responseJSON.error || JSON.stringify(jqXHR.responseJSON)) : "An unknown error occurred.";
                        messageDiv.html('<div class="alert alert-danger">Creation Failed: ' + errorMsg + '</div>');
                    }
                });
            });
        });
    </script>