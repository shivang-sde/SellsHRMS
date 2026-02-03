$(document).ready(function () {
  const employeeId = window.APP.EMPLOYEE_ID || $("#globalEmployeeId").val();
  if (!employeeId) return console.error("Employee ID missing!");

  initAttendanceCard(employeeId);
});

function initAttendanceCard(employeeId) {
  let currentPunchId = null;
  let timerInterval = null;

  // -----------------------
  // Initialization
  // -----------------------
  $("#btnPunchIn").prop("disabled", true);

  $("#punchedFrom")
    .val("WFO")
    .on("change", function () {
      const value = $(this).val();
      $("#btnPunchIn").prop("disabled", !value);
    })
    .trigger("change");

  updateClock();
  setInterval(updateClock, 1000);
  loadTodayPunch();

  // Event bindings
  $("#btnPunchIn").on("click", punchIn);
  $("#btnPunchOut").on("click", punchOut);

  // -----------------------
  // Functions
  // -----------------------
  function updateClock() {
    const now = new Date();
    $("#currentTime").text(now.toLocaleTimeString("en-US", { hour12: false }));
    $("#currentDate").text(
      now.toLocaleDateString("en-US", {
        weekday: "long",
        year: "numeric",
        month: "long",
        day: "numeric",
      }),
    );
  }

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
        if (xhr.status === 404) resetPunchUI();
      },
    });
  }

  function displayPunchStatus(data) {
    currentPunchId = data.id;

    // ✅ Update summary section
    $("#todayPunchIn").text(formatTime(data.punchIn));
    $("#todayPunchOut").text(data.punchOut ? formatTime(data.punchOut) : "--");
    $("#todayTotalHours").text(
      data.workHours ? data.workHours.toFixed(2) + "h" : "0h",
    );

    if (data.punchOut) {
      $("#attendanceSummaryText").html(
        `You punched out at <strong>${formatTime(data.punchOut)}</strong>.`,
      );
    } else if (data.punchIn) {
      $("#attendanceSummaryText").html(
        `You are currently punched in since <strong>${formatTime(data.punchIn)}</strong>.`,
      );
    } else {
      $("#attendanceSummaryText").html(
        `You haven’t punched in yet. Your shift starts at <strong>09:30 AM</strong>.`,
      );
    }

    // ✅ Update punch card UI
    if (data.punchOut) {
      // Already punched out
      $("#punchStatus").html(`
        <span class="badge bg-secondary fs-6 px-3 py-2 rounded-pill shadow-sm">
          <i class="fas fa-user-check me-1"></i> Punched Out
        </span>
      `);

      $("#btnPunchIn, #btnPunchOut").hide();
      $("#workingHours").hide();

      $(".attendance-card")
        .removeClass("border-success border-danger pulse-border")
        .addClass("border-secondary");
    } else {
      // Currently punched in
      $("#punchStatus").html(`
        <span class="badge bg-success fs-6 px-3 py-2 rounded-pill shadow-sm animate__animated animate__pulse animate__infinite">
          <i class="fas fa-user-clock me-1"></i> Punched In
        </span>
      `);

      $("#btnPunchIn").hide();
      $("#btnPunchOut").show();
      $("#workingHours").fadeIn();

      $(".attendance-card")
        .removeClass("border-secondary border-danger")
        .addClass("border-success pulse-border");

      startWorkTimer(data.punchIn);
    }
  }

  function resetPunchUI() {
    $("#punchStatus").html(
      '<span class="badge bg-secondary fs-6">Not Punched In</span>',
    );
    $("#btnPunchIn, #punchedFromDiv").show();
    $("#btnPunchOut").hide();
    $("#todayPunchIn, #todayPunchOut").text("--:--");
    $("#todayTotalHours").text("0h");
    $("#workingHours").hide();
    stopWorkTimer();
  }

  function startWorkTimer(punchInTime) {
    stopWorkTimer();
    timerInterval = setInterval(() => {
      const diff = new Date() - new Date(punchInTime);
      const hours = Math.floor(diff / 3600000);
      const minutes = Math.floor((diff % 3600000) / 60000);
      $("#hoursWorked").text(`${hours}h ${minutes}m`);
    }, 1000);
  }

  function stopWorkTimer() {
    if (timerInterval) clearInterval(timerInterval);
  }

  async function punchIn() {
    const punchedFrom = $("#punchedFrom").val();
    if (!punchedFrom) {
      showToast(
        "error",
        "Please select where you are punching in from (WFO/WFH)",
      );
      return;
    }
    try {
      const now = new Date().toISOString();
      const { lat, lng } = await getLocation();
      $.ajax({
        url: "/api/attendance/punch-in",
        method: "POST",
        contentType: "application/json",
        data: JSON.stringify({
          employeeId,
          punchIn: now,
          punchedFrom,
          lat,
          lng,
        }),
        success: function (data) {
          showToast("success", "Punched in successfully!");
          displayPunchStatus(data);
        },
      });
    } catch (e) {
      showToast("error", "Unable to get location.");
    }
  }

  function punchOut() {
    if (!currentPunchId) {
      showToast("error", "No active punch record found");
      return;
    }
    const now = new Date().toISOString();
    $.ajax({
      url: "/api/attendance/punch-out",
      method: "POST",
      contentType: "application/json",
      data: JSON.stringify({ punchId: currentPunchId, punchOut: now }),
      success: function (data) {
        showToast("success", "Punched out successfully!");
        displayPunchStatus(data);
        stopWorkTimer();
      },
    });
  }

  async function getLocation() {
    return new Promise((resolve, reject) => {
      if (!navigator.geolocation)
        return reject(new Error("Geolocation not supported"));
      navigator.geolocation.getCurrentPosition(
        (pos) =>
          resolve({
            lat: pos.coords.latitude,
            lng: pos.coords.longitude,
          }),
        (err) => reject(err),
      );
    });
  }

  function formatTime(dateTimeStr) {
    if (!dateTimeStr) return "--";
    const date = new Date(dateTimeStr);
    return date.toLocaleTimeString("en-IN", { hour12: true });
  }
}
