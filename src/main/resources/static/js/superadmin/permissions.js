$(document).ready(function () {
  const API_URL = APP.CONTEXT_PATH + "/api/permissions";

  function loadPermissions() {
    $.get(API_URL, function (permissions) {
      const tbody = $("#permissionTableBody");
      tbody.empty();

      permissions.forEach((p) => {
        const statusBadge = p.active
          ? `<span class="badge bg-success">Active</span>`
          : `<span class="badge bg-secondary">Inactive</span>`;

        const toggleBtn = `
                    <button class="btn btn-sm ${p.active ? "btn-danger" : "btn-success"} toggle-btn" data-id="${p.id}">
                        ${p.active ? "Deactivate" : "Activate"}
                    </button>`;

        tbody.append(`
                    <tr>
                        <td>${p.id}</td>
                        <td>${p.module}</td>
                        <td>${p.action}</td>
                        <td><code>${p.code}</code></td>
                        <td>${statusBadge}</td>
                        <td>${toggleBtn}</td>
                    </tr>
                `);
      });
    });
  }

  loadPermissions();

  // Save Permission
  $("#savePermissionBtn").click(function () {
    const data = {
      module: $("#module").val().trim(),
      action: $("#action").val().trim(),
      code: $("#code").val().trim(),
    };

    if (!data.module || !data.action || !data.code) {
      Swal.fire("Error", "All fields are required.", "error");
      return;
    }

    $.ajax({
      url: API_URL,
      type: "POST",
      contentType: "application/json",
      data: JSON.stringify(data),
      success: function () {
        $("#createPermissionModal").modal("hide");
        Swal.fire("Success", "Permission created successfully!", "success");
        loadPermissions();
      },
      error: function (xhr) {
        Swal.fire(
          "Error",
          xhr.responseText || "Failed to create permission.",
          "error",
        );
      },
    });
  });

  // Toggle Activation
  $(document).on("click", ".toggle-btn", function () {
    const id = $(this).data("id");

    $.ajax({
      url: `${API_URL}/${id}/toggle`,
      type: "PUT",
      success: function () {
        Swal.fire("Updated", "Permission status changed.", "success");
        loadPermissions();
      },
      error: function () {
        Swal.fire("Error", "Failed to update permission status.", "error");
      },
    });
  });
});
