<%-- List All Organisations Content --%>
    <div class="mb-3">
        <a href="/superadmin/create-organisation" class="btn btn-primary">
            <i class="bi bi-plus-lg"></i> Add New Organisation
        </a>
    </div>

    <div class="card shadow-sm">
        <div class="card-header">
            Organisations List
        </div>
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-striped table-hover">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Name</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody id="organisationsTableBody">
                        <tr>
                            <td colspan="4" class="text-center">Loading...</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <script>
        $(document).ready(function () {
            fetchOrganisations();

            function fetchOrganisations() {
                $.get("/api/organisations")
                    .done(function (organisations) {
                        let rows = '';
                        if (organisations.length === 0) {
                            rows = '<tr><td colspan="4" class="text-center">No organisations found.</td></tr>';
                        } else {
                            organisations.forEach(function (org) {
                                rows += `
                                <tr>
                                    <td>${org.id}</td>
                                    <td>${org.name}</td>
                                    <td><span class="badge bg-success">Active</span></td>
                                    <td>
                                        <button class="btn btn-sm btn-info me-2 view-admins" data-id="${org.id}">Admins</button>
                                        <button class="btn btn-sm btn-danger delete-org" data-id="${org.id}">Delete</button>
                                    </td>
                                </tr>
                            `;
                            });
                        }
                        $("#organisationsTableBody").html(rows);
                    })
                    .fail(function () {
                        $("#organisationsTableBody").html('<tr><td colspan="4" class="text-center text-danger">Failed to load organisations.</td></tr>');
                    });
            }

            // Example Delete Handler
            $(document).on('click', '.delete-org', function () {
                const orgId = $(this).data('id');
                if (confirm(`Are you sure you want to delete Organisation ID: ${orgId}?`)) {
                    $.ajax({
                        url: `/api/organisations/${orgId}`,
                        type: 'DELETE',
                        success: function () {
                            alert('Organisation deleted successfully!');
                            fetchOrganisations(); // Refresh the list
                        },
                        error: function () {
                            alert('Failed to delete organisation.');
                        }
                    });
                }
            });

            // Example View Admins Handler - A dedicated modal/page would be better
            $(document).on('click', '.view-admins', function () {
                const orgId = $(this).data('id');
                alert(`Viewing Admins for Organisation ID: ${orgId}. (API call to /api/organisations/${orgId}/admins)`);
                // You would typically open a modal or navigate to a details page here.
            });
        });
    </script>