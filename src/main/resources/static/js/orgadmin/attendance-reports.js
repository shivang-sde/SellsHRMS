$(document).ready(function () {
  const orgId = window.APP.ORG_ID || $("#globalOrgId").val();
  let reportData = [];

  // Set default dates (last 30 days)
  setDefaultDates();
  loadEmployees();

  $("#btnGenerateReport").on("click", generateReport);
  $("#btnExportReport").on("click", exportReport);

  function setDefaultDates() {
    const today = new Date();
    const thirtyDaysAgo = new Date();
    thirtyDaysAgo.setDate(today.getDate() - 30);

    $("#endDate").val(today.toISOString().split("T")[0]);
    $("#startDate").val(thirtyDaysAgo.toISOString().split("T")[0]);
  }

  function loadEmployees() {
    $.ajax({
      url: `/api/employees/org/${orgId}`,
      method: "GET",
      success: function (data) {
        let options = '<option value="">All Employees</option>';
        data.forEach((emp) => {
          options += `<option value="${emp.id}">${emp.fullName} (${emp.employeeCode})</option>`;
        });
        $("#filterEmployee").html(options);
      },
      error: function (xhr) {
        console.error("Failed to load employees");
        showToast("error", xhr.responseJSON.message);
      },
    });
  }

  function generateReport() {
    const startDate = $("#startDate").val();
    const endDate = $("#endDate").val();
    const employeeId = $("#filterEmployee").val();

    if (!startDate || !endDate) {
      showToast("error", "Please select start and end dates");
      return;
    }

    if (new Date(endDate) < new Date(startDate)) {
      showToast("error", "End date must be after start date");
      return;
    }

    $("#emptyState").hide();
    $("#reportSection").show();
    $("#statsSection").show();

    $("#reportTableBody").html(`
            <tr>
                <td colspan="8" class="text-center">
                    <div class="spinner-border text-primary"></div>
                    <p class="mt-2">Generating report...</p>
                </td>
            </tr>
        `);

    if (employeeId) {
      // Single employee report
      generateEmployeeReport(employeeId, startDate, endDate);
    } else {
      // Organization-wide report
      generateOrgReport(startDate, endDate);
    }
  }

  function generateEmployeeReport(employeeId, startDate, endDate) {
    $.ajax({
      url: `/api/attendance/employee/${employeeId}`,
      method: "GET",
      data: { startDate, endDate },
      success: function (data) {
        console.log("Employee Report Data", data);
        reportData = data;
        updateStatistics(data);
        renderReportTable(data);
      },
      error: function () {
        showToast("error", "Failed to generate report");
        $("#reportTableBody").html(`
    <tr>
        <td colspan="8" class="text-center">
            <div class="spinner-border text-primary"></div>
            <p class="mt-2">Generating report...</p>
        </td>
    </tr>
`);
      },
    });
  }

  function generateOrgReport(startDate, endDate) {
    $.ajax({
      url: `/api/attendance/org/${orgId}`,
      method: 'GET',
      data: { startDate, endDate },
      beforeSend: function () {
        $('#reportTableBody').html(`
                <tr>
                    <td colspan="8" class="text-center">
                        <div class="spinner-border text-primary"></div>
                        <p class="mt-2">Generating report...</p>
                    </td>
                </tr>
            `);
      },
      success: function (data) {

        reportData = data;
        updateStatistics(reportData);
        renderReportTable(reportData);
      },
      error: function () {
        showToast('error', 'Failed to generate report');
        $('#reportTableBody').html(`
                <tr>
                    <td colspan="8" class="text-center text-danger">
                        Failed to load report data
                    </td>
                </tr>
            `);
      }
    });
  }


  function updateStatistics(data) {
    const presentCount = data.filter(
      (d) =>
        d.status === "PRESENT" || d.status === "HALF_DAY" || d.status === "WFH",
    ).length;

    const absentCount = data.filter((d) => d.status === "ABSENT").length;
    const leaveCount = data.filter((d) => d.status === "ON_LEAVE").length;
    const total = data.length;
    const totalHours = data.reduce((sum, d) => sum + (d.workHours || 0), 0);

    $('#statPresent').text(`${presentCount} (${((presentCount / total) * 100).toFixed(1)}%)`);
    $("#statAbsent").text(absentCount);
    $("#statLeaves").text(leaveCount);
    $("#statHours").text(totalHours.toFixed(2) + "h");
  }

  function renderReportTable(data) {
    if (!data || data.length === 0) {
      $("#reportTableBody").html(`
                <tr>
                    <td colspan="8" class="text-center text-muted">
                        No attendance records found for selected criteria
                    </td>
                </tr>
            `);
      return;
    }

    let html = "";
    data.forEach((record) => {
      const date = record.punchIn
  ? new Date(record.punchIn).toLocaleDateString("en-GB", { timeZone: "Asia/Kolkata" })
  : new Date(record.attendanceDate).toLocaleDateString("en-GB", { timeZone: "Asia/Kolkata" });


      const lateEarly = [];
      if (record.isLate)
        lateEarly.push('<span class="badge bg-warning text-dark" title="Late In">Late</span>');
      if (record.isEarlyOut)
        lateEarly.push('<span class="badge bg-info" title="Early Out">Early</span>');
      const lateEarlyStr = lateEarly.length > 0 ? lateEarly.join(" ") : "--";

      const currentEmpId = parseInt(window.APP.EMPLOYEE_ID) || 0;
      const isOrgAdmin = window.APP.ROLE === "ORG_ADMIN" || window.APP.ROLE === "SUPER_ADMIN";

      let actionBtn = "";
      if (record.employeeId == currentEmpId && !isOrgAdmin) {
        actionBtn = `<button class="btn btn-sm btn-outline-secondary" disabled title="Cannot edit own record"><i class="fas fa-edit"></i></button>`;
      } else {
        actionBtn = `
            <button class="btn btn-sm btn-outline-primary edit-attendance" onclick="editAttendance('${record.summaryId || record.id}')">
                <i class="fas fa-edit"></i>
            </button>
        `;
      }

      html += `
                <tr>
                    <td>${date}</td>
                    <td>${escapeHtml(record.employeeName || "N/A")}</td>
                    <td>${escapeHtml(record.department || "N/A")}</td>
                    <td>${record.punchIn ? formatTime(record.punchIn) : "--"}</td>
                    <td>${record.punchOut ? formatTime(record.punchOut) : "--"}</td>
                    <td>${record.workHours ? record.workHours.toFixed(2) + "h" : "--"}</td>
                    <td>${getStatusBadge(record.status)}</td>
                    <td>${lateEarlyStr}</td>
                    <td>${record.remarks ? `<span class="badge bg-secondary">${record.remarks}</span>` : "--"}</td>
                    <td>${actionBtn}</td>
                </tr>
            `;
    });

    $("#reportTableBody").html(html);
  }



  let currentEditEmployeeId = null;

  window.editAttendance = function (attendanceId) {
    const record = reportData.find(r => r.summaryId == attendanceId);

    if (!record) {
      showToast("error", "Attendance record not found");
      return;
    }
    
    currentEditEmployeeId = record.employeeId;

    $('#editAttendanceId').val(record.summaryId);
    $('#editEmployeeName').val(record.employeeName || '');
    const dateStr = record.punchIn
      ? record.punchIn.split('T')[0]
      : record.attendanceDate.split('T')[0];
    $('#editAttendanceDate').val(dateStr);

    function formatTimeForInput(dateTimeStr) {
      if (!dateTimeStr) return "";
      const date = new Date(dateTimeStr);
      const hr = String(date.getHours()).padStart(2, '0');
      const min = String(date.getMinutes()).padStart(2, '0');
      return `${hr}:${min}`;
    }

    $('#editPunchIn').val(record.punchIn ? formatTimeForInput(record.punchIn) : '');
    $('#editPunchOut').val(record.punchOut ? formatTimeForInput(record.punchOut) : '');
    $('#editStatus').val(record.status || '');
    $('#editRemarks').val(record.remarks || '');

    const lateEarly = [];
    if (record.isLate) lateEarly.push("Late");
    if (record.isEarlyOut) lateEarly.push("Early");
    $('#editLateEarly').val(lateEarly.join(", ") || 'No');
    
    $('#leaveFieldsContainer').addClass('d-none');
    $('#halfDayTypeContainer').addClass('d-none');
    $('#editLeaveType').val('');
    $('#editHalfDayType').val('FIRST_HALF');
    $('#editLeaveReason').val('');
    $('#editAutoApproveLeave').prop('checked', true);
    $('#leaveBalanceInfo').addClass('d-none').text('');

    $('#editStatus').trigger('change');

    $('#editAttendanceModal').modal('show');
  };

  $('#editStatus').on('change', function() {
      const status = $(this).val();
      if (status === 'ON_LEAVE' || status === 'HALF_DAY') {
          $('#leaveFieldsContainer').removeClass('d-none');
          if (status === 'HALF_DAY') {
              $('#halfDayTypeContainer').removeClass('d-none');
          } else {
              $('#halfDayTypeContainer').addClass('d-none');
          }
          loadLeaveTypesForModal();
      } else {
          $('#leaveFieldsContainer').addClass('d-none');
          $('#halfDayTypeContainer').addClass('d-none');
      }
  });

  function loadLeaveTypesForModal() {
      if ($('#editLeaveType option').length > 1) return;

      $.ajax({
          url: `/api/leave-type/org/${orgId}`,
          method: 'GET',
          success: function(data) {
            console.log("leave types : ",data);
              let options = '<option value="">Select Leave Type</option>';
              data.forEach(type => {
                  if (type.visibleToEmployees) {
                      options += `<option value="${type.id}">${type.name}</option>`;
                  }
              });
              $('#editLeaveType').html(options);
          }
      });
  }

  $('#btnSaveAttendance').on('click', function (e) {
    e.preventDefault();
    updateAttendance();
  });

  function updateAttendance() {
    const attendanceId = $('#editAttendanceId').val();
    const punchInTime = $('#editPunchIn').val(); // HH:mm
    const punchOutTime = $('#editPunchOut').val(); // HH:mm

    const status = $('#editStatus').val();
    const remarks = $('#editRemarks').val();

    const oldRecord = reportData.find(r => r.summaryId == attendanceId);
    if (!oldRecord) return;

    let newPunchIn = null;
    let newPunchOut = null;
    const baseDateStr = $('#editAttendanceDate').val();

    if (punchInTime) {
      newPunchIn = `${baseDateStr}T${punchInTime}:00`;
    }

    if (punchOutTime) {
      newPunchOut = `${baseDateStr}T${punchOutTime}:00`;
    }

    const payload = { ...oldRecord };
    payload.punchIn = newPunchIn;
    payload.punchOut = newPunchOut;
    payload.status = status;
    payload.remarks = remarks;
    
    if (status === 'ON_LEAVE' || status === 'HALF_DAY') {
        const leaveTypeId = $('#editLeaveType').val();
        if (!leaveTypeId) {
            showToast("error", "Please select a Leave Type");
            return;
        }
        payload.leaveTypeId = parseInt(leaveTypeId);
        payload.leaveReason = $('#editLeaveReason').val();
        payload.autoApproveLeave = $('#editAutoApproveLeave').is(':checked');
        if (status === 'HALF_DAY') {
            payload.halfDayType = $('#editHalfDayType').val();
        }
    }

    $.ajax({
      url: `/api/attendance/update`,
      method: "PUT",
      data: JSON.stringify(payload),
      contentType: "application/json",
      success: function (data) {
        showToast("success", "Attendance updated successfully");
        $('#editAttendanceModal').modal('hide');
        generateReport();
      },
      error: function (xhr) {
        const msg = xhr.responseJSON?.message || "Failed to update attendance";
        showToast("error", msg);
      }
    });
  }





  function exportReport() {
    if (!reportData || reportData.length === 0) {
      showToast("error", "No data to export");
      return;
    }

    let csv =
      "Date,Employee,Department,Punch In,Punch Out,Work Hours,Status,Late/Early\n";

    reportData.forEach((record) => {
      const date = record.punchIn
        ? new Date(record.punchIn).toLocaleDateString()
        : new Date().toLocaleDateString();

      const lateEarly = [];
      if (record.isLate) lateEarly.push("Late");
      if (record.isEarlyOut) lateEarly.push("Early");

      csv += `"${date}",`;
      csv += `"${record.employeeName || "N/A"}",`;
      csv += `"${record.department || "N/A"}",`;
      csv += `"${record.punchIn ? formatTime(record.punchIn) : "--"}",`;
      csv += `"${record.punchOut ? formatTime(record.punchOut) : "--"}",`;
      csv += `"${record.workHours ? record.workHours.toFixed(2) : "--"}",`;
      csv += `"${record.status || "--"}",`;
      csv += `"${record.isLate ? "Late" : ""}",`;
      csv += `"${record.isEarlyOut ? "Early" : ""}",`;
      csv += `"${record.remarks ? record.remarks : "--"}",`;
    });

    const blob = new Blob([csv], { type: "text/csv" });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = `attendance_report_${employeeId || 'ALL'}_${startDate}_to_${endDate}.csv`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(url);

    showToast("success", "Report exported successfully");
  }

  function getStatusBadge(status) {
    const badges = {
      PRESENT: '<span class="badge bg-success fs-6" title="Present for full day">Present</span>',
      ABSENT: '<span class="badge bg-danger">Absent</span>',
      ON_LEAVE: '<span class="badge bg-warning text-dark">On Leave</span>',
      HALF_DAY: '<span class="badge bg-info">Half Day</span>',
      SHORT_DAY: '<span class="badge bg-warning">Short Day</span>',
      HOLIDAY: '<span class="badge bg-secondary">Holiday</span>',
      WEEK_OFF: '<span class="badge bg-secondary">Week Off</span>',
      WFH: '<span class="badge bg-primary">WFH</span>',
    };
    return (
      badges[status] || '<span class="badge bg-secondary">' + status + "</span>"
    );
  }

  function formatTime(dateTimeStr) {
    if (!dateTimeStr) return "--";
    const date = new Date(dateTimeStr);
    return date.toLocaleTimeString("en-IN", {
      hour: "2-digit",
      minute: "2-digit",
      hour12: false,
    });
  }

  function escapeHtml(text) {
    if (!text) return "";
    const map = {
      "&": "&amp;",
      "<": "&lt;",
      ">": "&gt;",
      '"': "&quot;",
      "'": "&#039;",
    };
    return String(text).replace(/[&<>"']/g, (m) => map[m]);
  }
});
