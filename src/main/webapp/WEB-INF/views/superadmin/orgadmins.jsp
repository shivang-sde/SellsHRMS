<%-- Org Admin Dashboard Placeholder --%>
    <h3>Organisation Admin Dashboard</h3>
    <p>Content for the Organisation Admin's main page.</p>
    <p>Employee count: <span id="employeeCount">...</span></p>

    <script>
        $(document).ready(function () {
            // NOTE: You'll need the current Org ID (e.g., from session) to call this API.
            // Assuming currentOrgId is 1 for testing:
            const currentOrgId = 1;

            $.get(`/api/org-admin/stats/employees/${currentOrgId}`)
                .done(function (count) {
                    $("#employeeCount").text(count);
                })
                .fail(function () {
                    $("#employeeCount").text("N/A - Failed to fetch count.");
                });
        });
    </script>