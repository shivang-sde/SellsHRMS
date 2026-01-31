$(document).ready(function () {
  const employeeId = window.APP.EMPLOYEE_ID || $("#globalEmployeeId").val();
  let currentPunchId = null;
  let timerInterval = null;

  $("#btnPunchIn").prop("disabled", true);

  $("#punchedFrom")
    .val("WFO")
    .on("change", function () {
      const value = $(this).val();
      $("#btnPunchIn").prop("disabled", !value);
    })
    .trigger("change"); // trigger after binding listener




  // Initialize
  updateClock();
  setInterval(updateClock, 1000);
  loadTodayPunch();
  setDefaultDates();
  loadAttendanceHistory();

  // Punch In Button
  $("#btnPunchIn").on("click", function () {
    punchIn();
  });

  // Punch Out Button
  $("#btnPunchOut").on("click", function () {
    punchOut();
  });

  // Filter Button
  $("#btnFilter").on("click", function () {
    loadAttendanceHistory();
  });

  // Update clock
  function updateClock() {
    const now = new Date();
    const timeStr = now.toLocaleTimeString("en-US", { hour12: false });
    const dateStr = now.toLocaleDateString("en-US", {
      weekday: "long",
      year: "numeric",
      month: "long",
      day: "numeric",
    });
    $("#currentTime").text(timeStr);
    $("#currentDate").text(dateStr);
  }

  // Set default date range (last 30 days)
  function setDefaultDates() {
    const today = new Date();
    const startDateofMonth = new Date(today.getFullYear(), today.getMonth(), 1);

    $("#endDate").val(today.toISOString().split("T")[0]);
    $("#startDate").val(startDateofMonth.toISOString().split("T")[0]);
  }

  // Load today's punch status
  function loadTodayPunch() {
    $.ajax({
      url: `/api/attendance/today/${employeeId}`,
      method: "GET",
      success: function (data) {
        if (data && data.punchIn) {
          displayPunchStatus(data);
        } else {
          resetPunchUI();
        }
      },
      error: function (xhr) {
        if (xhr.status === 404) {
          resetPunchUI();
        } else {
          console.error("Failed to load punch status");
        }
      },
    });
  }

  // Display punch status
  function displayPunchStatus(data) {
    currentPunchId = data.id;

    // Default reset
    $("#todayPunchIn").text(formatTime(data.punchIn));

    
    if (data.punchOut) {
  $('#attendanceSummaryText').html(`You punched out at <strong>${formatTime(data.punchOut)}</strong>.`);
} else if (data.punchIn) {
  $('#attendanceSummaryText').html(`You are currently punched in since <strong>${formatTime(data.punchIn)}</strong>.`);
} else {
  $('#attendanceSummaryText').html(`You haven’t punched in yet. Your shift starts at <strong>09:30 AM</strong>.`);
}

    if (data.punchOut) {
      // ✅ Already punched out
      $("#punchStatus").html(`
      <span class="badge bg-secondary fs-6 px-3 py-2 rounded-pill shadow-sm">
        <i class="fas fa-user-check me-1"></i> Punched Out
      </span>
    `);

      $("#btnPunchIn").hide();
      $("#btnPunchOut").hide();
      $("#todayPunchOut").text(formatTime(data.punchOut));
      $("#todayTotalHours").text(
        data.workHours ? data.workHours.toFixed(2) + "h" : "0h",
      );
      $("#workingHours").hide();

      // Card appearance
      $(".attendance-card")
        .removeClass("border-success border-danger pulse-border")
        .addClass("border-secondary");
    } else {
      // ✅ Currently punched in
      $("#punchStatus").html(`
      <span class="badge bg-success fs-6 px-3 py-2 rounded-pill shadow-sm animate__animated animate__pulse animate__infinite">
        <i class="fas fa-user-clock me-1"></i> Punched In
      </span>
    `);

      $("#btnPunchIn").hide();
      $("#btnPunchOut").show();
      $("#todayPunchOut").text("--:--");
      $("#workingHours").fadeIn();

      // Card appearance
      $(".attendance-card")
        .removeClass("border-secondary border-danger")
        .addClass("border-success pulse-border");

      // Start live work timer
      startWorkTimer(data.punchIn);
    }
  }

  // Reset punch UI
  function resetPunchUI() {
    $("#punchStatus").html(
      '<span class="badge bg-secondary fs-6">Not Punched In</span>',
    );
    $("#btnPunchIn").show();
    $("#btnPunchOut").hide();
    $("#todayPunchIn").text("--:--");
    $("#todayPunchOut").text("--:--");
    $("#todayTotalHours").text("0h");
    $("#workingHours").hide();
    stopWorkTimer();
  }

  // Start work timer
  function startWorkTimer(punchInTime) {
    stopWorkTimer();

    timerInterval = setInterval(function () {
      const start = new Date(punchInTime);
      const now = new Date();
      const diff = now - start;

      const hours = Math.floor(diff / (1000 * 60 * 60));
      const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));

      $("#hoursWorked").text(`${hours}h ${minutes}m`);
    }, 1000);
  }

  // Stop work timer
  function stopWorkTimer() {
    if (timerInterval) {
      clearInterval(timerInterval);
      timerInterval = null;
    }
  }

  // Punch In
  async function punchIn() {
    const punchedFrom = $("#punchedFrom").val();
    if (!punchedFrom) {
      showToast(
        "error",
        "Please select where you are punching in from (WFO/WFH)",
      );
      return;
    }

    $("#punchedFromDiv").hide();
    const now = new Date().toISOString();
    console.log("punch in time", now);
    $("#lng-lat").show();

    try {
      const { lat, lng } = await getLocation(); // ✅ await fixed async call

      $.ajax({
        url: "/api/attendance/punch-in",
        method: "POST",
        contentType: "application/json",
        data: JSON.stringify({
          employeeId: parseInt(employeeId),
          punchIn: now,
          source: "WEB",
          punchedFrom,
          lat,
          lng,
        }),
        success: function (data) {
          showToast("success", "Punched in successfully!");
          displayPunchStatus(data);
          loadAttendanceHistory();
        },
        error: function () {
          showToast("error", "Failed to punch in");
        },
      });
    } catch (err) {
      console.error("Unable to get location:", err);
      showToast(
        "error",
        "Unable to get your location. Please enable location permission.",
      );
    }
  }

  // Punch Out
  function punchOut() {
    if (!currentPunchId) {
      showToast("error", "No active punch record found");
      return;
    }

    const now = new Date().toISOString();

    console.log("punch in time", now);

    $.ajax({
      url: "/api/attendance/punch-out",
      method: "POST",
      contentType: "application/json",
      data: JSON.stringify({
        punchId: currentPunchId,
        punchOut: now,
      }),
      success: function (data) {
        showToast("success", "Punched out successfully!");
        displayPunchStatus(data);
        loadAttendanceHistory();
        stopWorkTimer();
      },
      error: function (xhr) {
        showToast("error", "Failed to punch out");
      },
    });
  }

  // Load attendance history
  function loadAttendanceHistory() {
    const startDate = $("#startDate").val();
    const endDate = $("#endDate").val();

    if (!startDate || !endDate) {
      showToast("error", "Please select date range");
      return;
    }

    $.ajax({
      url: `/api/attendance/employee/${employeeId}`,
      method: "GET",
      data: { startDate, endDate },
      success: function (data) {
        console.log("data atten", data);
        renderAttendanceTable(data);
        updateMonthSummary(data);
      },
      error: function () {
        $("#attendanceTableBody").html(`
                    <tr><td colspan="6" class="text-center text-danger">Failed to load attendance</td></tr>
                `);
      },
    });
  }

  // Render attendance table
  function renderAttendanceTable(data) {
    if (!data || data.length === 0) {
      $("#attendanceTableBody").html(`
                <tr><td colspan="6" class="text-center text-muted">No attendance records found</td></tr>
            `);
      return;
    }

    let html = "";
    data.forEach((record) => {
      const statusBadge = record.punchOut
        ? '<span class="badge bg-success">Completed</span>'
        : '<span class="badge bg-warning text-dark">In Progress</span>';

      html += `
                <tr>
                    <td>${formatDate(record.punchIn)}</td>
                    <td>${formatTime(record.punchIn)}</td>
                    <td>${record.punchOut ? formatTime(record.punchOut) : "--"}</td>
                    <td>${record.workHours ? record.workHours.toFixed(2) + "h" : "--"}</td>
                    <td>${statusBadge}</td>
                    <td><span class="badge bg-info">${record.punchSource}</span></td>
                </tr>
            `;
    });

    $("#attendanceTableBody").html(html);
  }

  // Update month summary
  function updateMonthSummary(data) {
    const daysPresent = data.filter((r) => r.punchOut).length;
    $("#monthDaysPresent").text(daysPresent + " days");
  }

  // Utility functions
  function formatDate(dateTimeStr) {
    if (!dateTimeStr) return "--";
    const date = new Date(dateTimeStr);
    return date.toLocaleDateString("en-US");
  }

  function formatTime(dateTimeStr) {
    if (!dateTimeStr) return "--";
    const date = new Date(dateTimeStr);
    return date.toLocaleTimeString("en-IN", { hour12: true });
  }

  async function getLocation() {
    return new Promise((resolve, reject) => {
      if (!navigator.geolocation) {
        reject(new Error("Geolocation is not supported by this browser."));
      } else {
        navigator.geolocation.getCurrentPosition(
          (position) => {
            const lat = position.coords.latitude;
            const lng = position.coords.longitude;
            const accuracy = position.coords.accuracy;

            console.log("Latitude:", lat);
            console.log("Longitude:", lng);
            console.log("Accuracy (meters):", accuracy);

            $("#lng-lat").html(`
            <div class="text-center">
              <strong>
                <small>
                  <code>Latitude: ${lat}</code>
                  <code>Longitude: ${lng}</code>
                  <code>Accuracy: ${accuracy} meters</code>
                </small>
              </strong>
            </div>
          `);

            resolve({ lat, lng });
          },
          (error) => {
            switch (error.code) {
              case error.PERMISSION_DENIED:
                showConfirmation(
                  "Location Permission Denied",
                  "Please allow location permission to get your current location.",
                  "OK",
                  "Cancel",
                  () => reject(error),
                );
                break;
              case error.POSITION_UNAVAILABLE:
                showConfirmation(
                  "Location Unavailable",
                  "Location information is unavailable.",
                  "OK",
                  "Cancel",
                  () => reject(error),
                );
                break;
              case error.TIMEOUT:
                showConfirmation(
                  "Location Timeout",
                  "The request to get user location timed out.",
                  "OK",
                  "Cancel",
                  () => reject(error),
                );
                break;
              default:
                showError("Location Error", "An unknown error occurred.");
                reject(error);
            }
          },
        );
      }
    });
  }
});
