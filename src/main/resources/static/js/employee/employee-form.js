$(document).ready(async function () {
  const orgId = window.APP.ORG_ID || $("#globalOrgId").val();
  const employeeId = $("#employeeId").val();
  const isEditMode = !!employeeId;
  let photoUrl = "";

  $('input[name="dateOfJoining"]').attr(
    "min",
    new Date().toISOString().split("T")[0],
  );
  $('input[name="dob"]').attr("max", new Date().toISOString().split("T")[0]);
  $("#doeId").hide();
  $("#exitStatusValId").hide();

  $('input[name="photo"]').on("change", uploadPhoto);

  // Load departments & managers in parallel
  await Promise.all([loadDepartments(), loadManagers()]);

  // If edit mode, load employee data (await ensures correct dropdown values)
  if (isEditMode) {
    await loadEmployeeData(employeeId);
    $('input[name="password"]').prop("required", false);
    $('input[name="password"]').prop("disabled", true);
    $('input[name="workEmail"]').prop("disabled", true);
    $('input[name="dateOfJoining"]').removeAttr("min").prop("readonly", true);
    $("#doeId").show();
    $("#exitStatusValId").show();

    if ($("#status").val() === "EXIT") {
      $("#doeId").attr("required", true);
    } else {
      $("#doeId").attr("required", false);
    }
  }

  // Copy local address to permanent
  $("#copyLocalAddress").on("click", function () {
    $('input[name="permanentAddress.addressLine1"]').val(
      $('input[name="localAddress.addressLine1"]').val(),
    );
    $('input[name="permanentAddress.addressLine2"]').val(
      $('input[name="localAddress.addressLine2"]').val(),
    );
    $('input[name="permanentAddress.city"]').val(
      $('input[name="localAddress.city"]').val(),
    );
    $('input[name="permanentAddress.state"]').val(
      $('input[name="localAddress.state"]').val(),
    );
    $('input[name="permanentAddress.country"]').val(
      $('input[name="localAddress.country"]').val(),
    );
    $('input[name="permanentAddress.pincode"]').val(
      $('input[name="localAddress.pincode"]').val(),
    );
    showToast("info", "Address copied");
  });

  // Load designations dynamically when department changes
  $("#departmentSelect").on("change", function () {
    const deptId = $(this).val();
    loadDesignations(deptId);
  });

  // -------------------------------
  // FORM SUBMISSION
  // -------------------------------
  $("#employeeForm").on("submit", function (e) {
    e.preventDefault();

    const formData = serializeFormData();
    formData.organisationId = parseInt(orgId);

    Object.keys(formData).forEach((key) => {
      if (formData[key] === "") formData[key] = null;
    });

    const url = isEditMode ? `/api/employees/${employeeId}` : "/api/employees";
    const method = isEditMode ? "PUT" : "POST";

    $("#submitBtn")
      .prop("disabled", true)
      .html(
        '<span class="spinner-border spinner-border-sm me-2"></span>Saving...',
      );

    $.ajax({
      url,
      method,
      contentType: "application/json",
      data: JSON.stringify({ ...formData, photoUrl }),
      success: function () {
        showToast(
          "success",
          isEditMode
            ? "Employee updated successfully"
            : "Employee created successfully",
        );
        setTimeout(() => (window.location.href = "/org/employees"), 1500);
      },
      error: function (xhr) {
        let errorMsg = xhr.responseJSON?.message || "Failed to save employee";
        showToast("error", errorMsg);
        $("#submitBtn")
          .prop("disabled", false)
          .html(
            '<i class="fas fa-save me-2"></i>' +
            (isEditMode ? "Update Employee" : "Create Employee"),
          );
      },
    });
  });

  // -------------------------------
  // LOAD EMPLOYEE DATA (EDIT MODE)
  // -------------------------------
  async function loadEmployeeData(id) {
    try {
      const data = await $.ajax({
        url: `/api/employees/${id}`,
        method: "GET",
      });

      populateForm(data);

      // Load designations after we know departmentId
      await loadDesignations(data.departmentId);

      // Now safely set dropdowns
      $("#departmentSelect").val(data.departmentId);
      $("#designationSelect").val(data.designationId);
      $("#reportingToSelect").val(data.reportingToId);
    } catch (err) {
      console.error(err);
      showToast("error", err.message);
    }
  }

  // -------------------------------
  // POPULATE FORM
  // -------------------------------
  function populateForm(data) {
    console.log("Employee Data:", data);
    $('input[name="firstName"]').val(data.firstName);
    $('input[name="lastName"]').val(data.lastName);
    $('input[name="dob"]').val(data.dob);
    photoUrl = data.photoUrl || "";
    $('select[name="bloodGroup"]').val(data.bloodGroup);
    $('select[name="gender"]').val(data.gender);
    $('input[name="personalEmail"]').val(data.personalEmail);
    $('input[name="phone"]').val(data.phone);
    $('input[name="alternatePhone"]').val(data.alternatePhone);
    $('input[name="fatherName"]').val(data.fatherName);
    $('input[name="nationality"]').val(data.nationality);
    $('select[name="maritalStatus"]').val(data.maritalStatus);
    $('input[name="referenceName"]').val(data.referenceName);
    $('input[name="referencePhone"]').val(data.referencePhone);

    // identity proof
    $('input[name="aadharNumber"]').val(data.aadharNumber);
    $('input[name="panNumber"]').val(data.panNumber);
    $('input[name="uanNumber"]').val(data.uanNumber);


    // Addresses
    if (data.localAddress) fillAddress("localAddress", data.localAddress);
    if (data.permanentAddress)
      fillAddress("permanentAddress", data.permanentAddress);

    // Company Info
    $('input[name="employeeCode"]').val(data.employeeCode);
    $('input[name="dateOfJoining"]').val(data.dateOfJoining);
    $('input[name="dateOfExit"]').val(data.dateOfExit);
    $('select[name="employmentType"]').val(data.employmentType);
    $('select[name="status"]').val(data.status);
    $('input[name="workEmail"]').val(data.email);

    $('input[name="departmentId"]').val(data.departmentId);
    $('input[name="designationId"]').val(data.designationId);
    $('input[name="reportingToId"]').val(data.reportingToId);
  }

  function fillAddress(prefix, addr) {
    $(`input[name="${prefix}.addressLine1"]`).val(addr.addressLine1);
    $(`input[name="${prefix}.addressLine2"]`).val(addr.addressLine2);
    $(`input[name="${prefix}.city"]`).val(addr.city);
    $(`input[name="${prefix}.state"]`).val(addr.state);
    $(`input[name="${prefix}.country"]`).val(addr.country);
    $(`input[name="${prefix}.pincode"]`).val(addr.pincode);
  }

  // -------------------------------
  // SERIALIZE FORM DATA
  // -------------------------------
  function serializeFormData() {
    const formData = {};
    $("#employeeForm")
      .find("input, select, textarea")
      .each(function () {
        const name = $(this).attr("name");
        if (!name || $(this).attr("type") === "file") return;

        let value =
          $(this).attr("type") === "checkbox"
            ? $(this).is(":checked")
            : $(this).val();

        if (name.includes(".")) {
          const [parent, child] = name.split(".");
          formData[parent] = formData[parent] || {};
          formData[parent][child] = value || null;
        } else {
          formData[name] =
            name.endsWith("Id") && value ? parseInt(value) : value || null;
        }
      });
    return formData;
  }

  // -------------------------------
  // LOAD DROPDOWNS
  // -------------------------------
  async function loadDepartments() {
    return $.ajax({
      url: `/api/departments/org/${orgId}`,
      method: "GET",
    }).then((data) => {
      let options = '<option value="">Select Department</option>';
      data.forEach(
        (d) => (options += `<option value="${d.id}">${d.name}</option>`),
      );
      $("#departmentSelect").html(options);
    });
  }

  function loadDesignations(departmentId) {
    const $designationSelect = $("#designationSelect");
    if (!departmentId) {
      $designationSelect.html(
        '<option value="">Select Department First</option>',
      );
      return $.Deferred().resolve().promise();
    }
    $designationSelect.html('<option value="">Loading...</option>');
    return $.ajax({
      url: `/api/designations/department/${departmentId}`,
      method: "GET",
    }).then((data) => {
      let options = '<option value="">Select Designation</option>';
      data.forEach(
        (d) => (options += `<option value="${d.id}">${d.title}</option>`),
      );
      $designationSelect.html(options);
    });
  }

  async function loadManagers() {
    return $.ajax({
      url: `/api/employees/org/${orgId}`,
      method: "GET",
    }).then((data) => {
      let options = '<option value="">Select Manager</option>';
      data.forEach((emp) => {
        if (isEditMode && emp.id == employeeId) return;
        options += `<option value="${emp.id}">${emp.fullName} (${emp.employeeCode})</option>`;
      });
      $("#reportingToSelect").html(options);
    });
  }

  async function uploadPhoto() {
    const fileInput = document.querySelector('input[name="photo"]');
    const file = fileInput.files[0];
    if (!file) return;

    window.validateImage(file, {
      minW: 150,
      minH: 150,
      maxW: 800,
      maxH: 800,
      maxSizeMB: 3,
      allowedTypes: ["image/png", "image/jpeg", "image/webp"],
      squareRatio: true,
    });

    const res = await fileAPI.upload([file], "employee", "documents");
    console.log("Photo Upload Response:", res);
    photoUrl = res[0].fileUrl;
    showToast("success", "Photo uploaded successfully");
  }
});
