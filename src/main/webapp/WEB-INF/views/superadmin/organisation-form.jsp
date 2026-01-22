<div class="container mt-4">
    <h3>Create Organisation + Admin</h3>

    <form id="createOrgForm" class="row g-3">

        <!-- ORG SECTION -->
        <h5 class="mt-3">Organisation Details</h5>

        <div class="col-md-6">
            <label for="orgName" class="form-label">Organisation Name</label>
            <input id="orgName" class="form-control" required>
        </div>

        <div class="col-md-6">
            <label for="orgDomain" class="form-label">Domain</label>
            <input id="orgDomain" class="form-control" required>
        </div>

        <div class="col-md-6">
            <label for="orgEmail" class="form-label">Contact Email</label>
            <input id="orgEmail" type="email" class="form-control" required>
        </div>

        <div class="col-md-6">
            <label for="orgPhone" class="form-label">Contact Phone</label>
            <input id="orgPhone" class="form-control">
        </div>

        <div class="col-md-4">
            <label for="orgAddress" class="form-label">Address</label>
            <input id="orgAddress" class="form-control">
        </div>

        <div class="col-md-4">
            <label for="orgCountry" class="form-label">Country</label>
            <input id="orgCountry" class="form-control">
        </div>

        <div class="col-md-4">
            <label for="logoUrl" class="form-label">Logo URL</label>
            <input id="logoUrl" class="form-control">
        </div>

        <div class="col-md-4">
            <label for="orgPan" class="form-label">PAN</label>
            <input id="orgPan" class="form-control" required>
        </div>

        <div class="col-md-4">
            <label for="orgTan" class="form-label">TAN</label>
            <input id="orgTan" class="form-control" >
        </div>

        <div class="col-md-4">
            <label for="maxEmployees" class="form-label">Max Employees</label>
            <input id="maxEmployees" type="number" class="form-control" value="10" required>
        </div>

        <div  class="col-md-4">
            <label for="validity" class="form-label">Validity</label>
            <input id="validity" type="date" class="form-control" required >
        </div>

        <!-- ADMIN SECTION --> 
        <h5 class="mt-4">Admin Details</h5>

        <div class="col-md-6">
            <label for="adminFullName" class="form-label">Full Name</label>
            <input id="adminFullName" class="form-control" required>
        </div>

        <div class="col-md-6">
            <label for="adminEmail" class="form-label">Email</label>
            <input id="adminEmail" type="email" class="form-control" required>
        </div>

        <div class="col-md-6">
            <label for="adminPassword" class="form-label">Password</label>
            <input id="adminPassword" type="password" class="form-control" required>
        </div>

        <div class="col-12 mt-4">
            <button type="submit" class="btn btn-success">Create Organisation</button>
            <a class="btn btn-secondary ms-2" href="${pageContext.request.contextPath}/superadmin/organisations">Cancel</a>
        </div>

    </form>
</div>

<script src="/js/organisation.js"></script>
