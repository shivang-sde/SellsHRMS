$(document).ready(function () {
  const orgId = window.APP.ORG_ID;
  const employeeId = window.APP.EMPLOYEE_ID || $("#globalEmployeeId").val();
  if (!employeeId) return console.error("Employee ID missing!");

  initAttendanceCard(employeeId, orgId);
});

function initAttendanceCard(employeeId, orgId) {
  let currentPunchId = null;
  let timerInterval = null;
  let isOnLeave = false;
  let isHoliday = false;

  // ─── Status Badge Helper ────────────────────────────────────────────────────
  // type: 'offduty' | 'not-punched' | 'punched-in' | 'punched-out' | 'holiday' | 'on-leave'
  function setStatusBadge(type, label, icon) {
    const $span = $("#punchStatusBadge .status-badge");
    const allModifiers = [
      "status-badge--offduty",
      "status-badge--not-punched",
      "status-badge--punched-in",
      "status-badge--punched-out",
      "status-badge--holiday",
      "status-badge--on-leave"
    ];
    $span
      .removeClass(allModifiers.join(" "))
      .addClass("status-badge--" + type);
    $span.find(".status-badge__icon")
      .attr("class", "status-badge__icon fas " + icon);
    $span.find(".status-badge__label").text(label);
  }


  function checkHoliday() {
    $.ajax({
      url: `/api/holidays/isHoliday?orgId=${orgId}&date=${new Date().toISOString().split("T")[0]}`,
      method: "GET",
      success: function (data) {
        if (data.success && data.data) {
          isHoliday = true;
        }
        // later fetch the holiday details itself and show to users
        if (isHoliday) {
          setStatusBadge("holiday", "Holiday", "fa-calendar-times");
        } else {
          setStatusBadge("not-punched", "Not Punched In", "fa-clock");
        }
      },
      error: function (xhr) {
        const err = xhr.responseJSON;
        showToast('error', err.message);
      }
    });
  }

  function checkLeave() {
    $.ajax({
      url: `/api/leaves/check?employeeId=${employeeId}&date=${new Date().toISOString().split("T")[0]}`,
      method: "GET",
      success: function (data) {
        if (data.success && data.data) {
          isOnLeave = true;
        }

        if (isOnLeave) {
          setStatusBadge("on-leave", "On Leave", "fa-umbrella-beach");
        } else {
          setStatusBadge("not-punched", "Not Punched In", "fa-clock");
        }
      },
      error: function (xhr) {
        const err = xhr.responseJSON;
        showToast('error', err.message);
      }
    });
  }

  // -----------------------
  // Initialization
  // -----------------------
  checkLeave();
  checkHoliday();
  $("#btnPunchIn").prop("disabled", true);

  $("#punchedFrom")
    .val("WFO")
    .on("change", function () {
      const value = $(this).val();
      $("#btnPunchIn").prop("disabled", !value || isOnLeave || isHoliday);
    })
    .trigger("change");

  updateClock();
  setInterval(updateClock, 1000);
  loadTodayPunch();

  // Event bindings
  $("#btnPunchIn").on("click", function () {
    if (isOnLeave || isHoliday) {
      showToast("error", `${isOnLeave ? "You are on leave today" : "It is a holiday"}`);
      return;
    }
    punchIn();
  });
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
      setStatusBadge("punched-out", "Punched Out", "fa-user-check");
    } else if (data.punchIn) {
      $("#attendanceSummaryText").html(
        `You are currently punched in since <strong>${formatTime(data.punchIn)}</strong>.`,
      );
      setStatusBadge("punched-in", "Punched In", "fa-user-clock");
    } else {
      $("#attendanceSummaryText").html(
        `You haven't punched in yet. Your shift starts at <strong>09:30 AM</strong>.`,
      );
      setStatusBadge("not-punched", "Not Punched In", "fa-clock");
    }

    // ✅ Update punch card UI
    if (data.punchOut) {
      // Already punched out
      $("#btnPunchIn, #btnPunchOut").hide();
      $("#workingHours").hide();

      $(".attendance-card")
        .removeClass("border-success border-danger pulse-border")
        .addClass("border-secondary");
    } else {
      // Currently punched in
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
    setStatusBadge("not-punched", "Not Punched In", "fa-clock");
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
    if (isOnLeave || isHoliday) {
      showToast("error", `${isOnLeave ? "You are on leave today" : "It is a holiday"}`);
      return;
    }
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
    if (isOnLeave && isHoliday) {
      showToast("error", `${isOnLeave ? "You are on leave today" : "It is a holiday"}`);
      return;
    }
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
