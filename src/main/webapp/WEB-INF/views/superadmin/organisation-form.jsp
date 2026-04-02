<div class="container mt-4">
    <h3>Create Organisation + Admin</h3>

    <form id="createOrgForm" class="row g-3">

        <!-- ORG SECTION -->
        <h5 class="mt-3">Organisation Details</h5>

        <div class="col-md-6">
            <label for="orgName" class="form-label">Organisation Name <span class="text-danger">*</span></label>
            <input id="orgName" class="form-control" required>
        </div>

        <div class="col-md-6">
            <label for="prefix" class="form-label">Prefix <span class="text-danger">*</span></label>
            <input type="text" class="form-control" required name="prefix" id="prefix"
                placeholder="e.g. HRMS, SELLS, SPARK or EMP">
        </div>

        <div class="col-md-6">
            <label for="padding" class="form-label">Padding <span class="text-danger">*</span></label>
            <input type="number" class="form-control" required name="padding" id="padding" placeholder="e.g. 3">
            <small class="form-text text-muted">
                Employee code seris will be like <strong id="prefixExample">SPARK001,
                    SELLS002, HRMS003</strong>
            </small>
        </div>

        <div class="col-md-6">
            <label for="orgDomain" class="form-label">
                Domain <span class="text-danger">*</span>
            </label>
            <input id="orgDomain" name="domain" class="form-control" placeholder="e.g. yourhrms.com or abc.hrms.com"
                required>
            <small class="form-text text-muted">
                Enter your organization&apos;s domain &quot;(without http://&quot; or &quot;www&quot;). Example:
                <strong>yourhrms.com</strong>
            </small>
        </div>

        <div class="col-md-6">
            <label for="orgTimeZone" class="form-label">Time Zone <span class="text-danger">*</span></label>
            <select id="orgTimeZone" class="form-select" required>
                <option value="">Select Time Zone</option>
                <option value="Asia/Kolkata">India Standard Time (IST)</option>
                <option value="America/New_York">Eastern Time (ET)</option>
                <option value="Europe/London">Greenwich Mean Time (GMT)</option>
                <option value="Asia/Dubai">Gulf Standard Time (GST)</option>
                <option value="Asia/Singapore">Singapore Time (SGT)</option>
                <option value="America/Los_Angeles">Pacific Time (PT)</option>
            </select>
        </div>


        <div class="col-md-6">
            <label for="orgEmail" class="form-label">Contact Email <span class="text-danger">*</span></label>
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
            <label for="logoFile" class="form-label">Upload Logo</label>
            <input type="file" id="logoFile" class="form-control" accept="image/*">
            <input type="hidden" id="logoUrl">
            <div class="mt-2">
                <img id="logoPreview" src="" alt="Logo preview" style="max-height: 80px; display: none;">
            </div>


        </div>


        <div class="col-md-4">
            <label for="orgPan" class="form-label">PAN <span class="text-danger">*</span></label>
            <input id="orgPan" class="form-control" required>
        </div>

        <div class="col-md-4">
            <label for="orgTan" class="form-label">TAN</label>
            <input id="orgTan" class="form-control">
        </div>

        <div class="col-md-4">
            <label for="maxEmployees" class="form-label">Max Employees <span class="text-danger">*</span></label>
            <input id="maxEmployees" type="number" class="form-control" value="10" required>
        </div>

        <div class="col-md-4">
            <label for="validity" class="form-label">Validity <span class="text-danger">*</span></label>
            <input id="validity" type="date" class="form-control" required>
        </div>

        <!-- ADMIN SECTION -->
        <h5 class="mt-4">Admin Details</h5>

        <div class="col-md-6">
            <label for="adminFullName" class="form-label">Full Name <span class="text-danger">*</span></label>
            <input id="adminFullName" class="form-control" required>
        </div>

        <div class="col-md-6">
            <label for="adminEmail" class="form-label">Email <span class="text-danger">*</span></label>
            <input id="adminEmail" type="email" class="form-control" required>
        </div>

        <div class="col-md-6">
            <label for="adminPassword" class="form-label">Password <span class="text-danger">*</span></label>
            <input id="adminPassword" type="password" class="form-control" required>
        </div>

        <div class="col-12 mt-4">
            <button type="submit" id="createOrgBtn" class="btn btn-success">Create Organisation</button>
            <a class="btn btn-secondary ms-2"
                href="${pageContext.request.contextPath}/superadmin/organisations">Cancel</a>
        </div>

    </form>
</div>

<script src="/js/organisation.js"></script>