<%-- Super Admin Dashboard Content --%>
    <div class="card shadow-sm mb-4">
        <div class="card-body">
            <h5 class="card-title">Welcome Back, Super Admin!</h5>
            <p class="card-text">Use the sidebar to manage all organisations and organisation administrators.</p>
        </div>
    </div>

    <div class="row">
        <div class="col-md-6 col-lg-4 mb-4">
            <div class="card text-white bg-primary h-100">
                <div class="card-body">
                    <h5 class="card-title">Total Organisations</h5>
                    <h1 id="orgCount">...</h1>
                </div>
                <div class="card-footer">
                    <a href="/superadmin/organisations" class="text-white">View Details <i
                            class="bi bi-arrow-right"></i></a>
                </div>
            </div>
        </div>
        <div class="col-md-6 col-lg-4 mb-4">
            <div class="card text-white bg-success h-100">
                <div class="card-body">
                    <h5 class="card-title">Total Org Admins</h5>
                    <h1 id="adminCount">...</h1>
                </div>
                <div class="card-footer">
                    <a href="/superadmin/orgadmins" class="text-white">View Details <i
                            class="bi bi-arrow-right"></i></a>
                </div>
            </div>
        </div>
    </div>

    <script>
        $(document).ready(function () {
            // Fetch total organisations
            $.get("/api/organisations")
                .done(function (data) {
                    $("#orgCount").text(data.length);
                })
                .fail(function () {
                    $("#orgCount").text("N/A");
                });

            // Fetch total organisation admins
            $.get("/api/superadmin/org-admins")
                .done(function (data) {
                    $("#adminCount").text(data.length);
                })
                .fail(function () {
                    $("#adminCount").text("N/A");
                });
        });
    </script>