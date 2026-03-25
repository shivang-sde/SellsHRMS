$(document).ready(function () {
  const orgId = window.APP.ORG_ID;
  const employeeId = window.APP.EMPLOYEE_ID || $("#globalEmployeeId").val();
  if (!employeeId) return console.error("Employee ID missing!");

  initAttendanceCard(employeeId, orgId);
});

function initAttendanceCard(employeeId, orgId) {
  let currentPunchId = null;
  let timerInterval = null;

  // Tracks today's punch state so applyPreCheckResult() never overwrites
  // a real "Punched In" / "Punched Out" badge with a pre-check badge.
  // Values: "none" | "punched-in" | "punched-out"
  let punchState = "none";

  // ── pre-check results (populated by runPreCheck) ─────────────────────────
  let preCheck = {
    canPunchIn: false,
    holiday: false,
    holidayName: null,
    onLeave: false,
    weekOff: false,
    weekOffDay: null,
    shiftStatus: "BEFORE_SHIFT",
    officeStart: "--:--",
    officeClosed: "--:--",
    lateGraceMinutes: 10,
  };

  // ── Status Badge Helper ───────────────────────────────────────────────────
  // type: 'offduty'|'not-punched'|'punched-in'|'punched-out'|'holiday'|'on-leave'|'week-off'
  const ALL_BADGE_MODIFIERS = [
    "status-badge--offduty",
    "status-badge--not-punched",
    "status-badge--punched-in",
    "status-badge--punched-out",
    "status-badge--holiday",
    "status-badge--on-leave",
    "status-badge--week-off",
  ];

  function setStatusBadge(type, label, icon) {
    const $span = $("#punchStatusBadge .status-badge");
    $span
      .removeClass(ALL_BADGE_MODIFIERS.join(" "))
      .addClass("status-badge--" + type);
    $span.find(".status-badge__icon").attr("class", "status-badge__icon fas " + icon);
    $span.find(".status-badge__label").text(label);
  }

  // ── Throttle/debounce for punch actions ───────────────────────────────────
  // Prevents double-submissions if user clicks quickly more than once.
  function throttle(fn, limitMs) {
    let lastCall = 0;
    let pending = false;
    return function (...args) {
      const now = Date.now();
      if (pending || now - lastCall < limitMs) return;
      pending = true;
      lastCall = now;
      const btn = $(this);
      btn.prop("disabled", true);
      Promise.resolve(fn.apply(this, args)).finally(() => {
        pending = false;
        // Re-enable only if the pre-check still allows it
        btn.prop("disabled", !preCheck.canPunchIn);
      });
    };
  }

  // ── Single pre-check call ─────────────────────────────────────────────────
  // Replaces the old checkHoliday() + checkLeave() pair.
  // Priority: holiday → leave → week-off → shift-window → all-clear
  function runPreCheck() {
    $("#btnPunchIn").prop("disabled", true);

    $.ajax({
      url: `/api/attendance/pre-check?employeeId=${employeeId}&orgId=${orgId}`,
      method: "GET",
      success: function (data) {
        preCheck = data;
        applyPreCheckResult(data);
      },
      error: function (xhr) {
        // If we can't even run the pre-check, fail safe → disable punch-in
        const msg = xhr.responseJSON?.message || "Unable to verify attendance eligibility.";
        showToast("error", msg);
        setStatusBadge("offduty", "Unavailable", "fa-exclamation-triangle");
        $("#btnPunchIn").prop("disabled", true);
      },
    });
  }

  /**
   * Applies the pre-check result in strict priority order and updates the
   * badge + button state accordingly.
   */
  function applyPreCheckResult(data) {
    // ── Guard: if the employee already has a punch record today,
    //    do NOT let the pre-check badge overwrite it.
    if (punchState !== "none") return;

    // 1 ─ Holiday (highest priority)
    if (data.holiday) {
      const name = data.holidayName || "Holiday";
      setStatusBadge("holiday", name, "fa-calendar-times");
      updateGlowStrip("holiday");
      $("#btnPunchIn").prop("disabled", true);
      showShiftInfo(data);
      return;
    }

    // 2 ─ On Leave
    if (data.onLeave) {
      setStatusBadge("on-leave", "On Leave", "fa-umbrella-beach");
      updateGlowStrip("on-leave");
      $("#btnPunchIn").prop("disabled", true);
      showShiftInfo(data);
      return;
    }

    // 3 ─ Week Off
    if (data.weekOff) {
      const day = data.weekOffDay
        ? data.weekOffDay.charAt(0) + data.weekOffDay.slice(1).toLowerCase()
        : "Week Off";
      setStatusBadge("week-off", day + " (Off)", "fa-couch");
      updateGlowStrip("week-off");
      $("#btnPunchIn").prop("disabled", true);
      showShiftInfo(data);
      return;
    }

    // 4 ─ Before shift window
    if (data.shiftStatus === "BEFORE_SHIFT") {
      setStatusBadge("not-punched", "Too Early", "fa-clock");
      updateGlowStrip("default");
      $("#btnPunchIn").prop("disabled", true);
      showShiftInfo(data, `Office opens at ${data.officeStart} — punch-in available from ${earliestPunchTime(data.officeStart)}`);
      return;
    }

    // 5 ─ After shift window
    if (data.shiftStatus === "AFTER_SHIFT") {
      setStatusBadge("not-punched", "Shift Ended", "fa-moon");
      updateGlowStrip("default");
      $("#btnPunchIn").prop("disabled", true);
      showShiftInfo(data, `Office closed at ${data.officeClosed}`);
      return;
    }

    // 6 ─ All clear — within shift, enable punch-in
    setStatusBadge("not-punched", "Not Punched In", "fa-clock");
    updateGlowStrip("default");
    const punchedFrom = $("#punchedFrom").val();
    $("#btnPunchIn").prop("disabled", !punchedFrom);
    showShiftInfo(data);
  }

  /** Returns a readable "earliest punch time" string given an HH:mm officeStart. */
  function earliestPunchTime(officeStart) {
    try {
      const [h, m] = officeStart.split(":").map(Number);
      const d = new Date();
      d.setHours(h, m - 30, 0);
      return d.toLocaleTimeString("en-IN", { hour: "2-digit", minute: "2-digit", hour12: true });
    } catch (_) {
      return officeStart;
    }
  }

  /** Shows shift info text in the GPS/status line area. */
  function showShiftInfo(data, overrideText) {
    const text = overrideText
      || `Office hours: ${data.officeStart} – ${data.officeClosed} · Grace: ${data.lateGraceMinutes} min`;
    $("#lng-lat").html(`<i class="fas fa-business-time me-1"></i> ${text}`);
  }

  /** Updates the coloured top strip of the punch card to match state. */
  function updateGlowStrip(state) {
    const $glow = $("#mainPunchCard .status-indicator-glow");
    const colours = {
      "punched-in": "#10b981",
      "holiday": "#f472b6",
      "on-leave": "#f87171",
      "week-off": "#94a3b8",
      "default": "#adb5bd",
    };
    $glow.css({
      background: colours[state] || colours.default,
      "box-shadow": state === "punched-in"
        ? "0 2px 12px rgba(16,185,129,0.45)"
        : "none",
    });
  }

  // ── Initialization ────────────────────────────────────────────────────────
  runPreCheck();

  $("#punchedFrom")
    .val("WFO")
    .on("change", function () {
      // Only re-evaluate disabled state; never override a pre-check block
      if (preCheck.canPunchIn) {
        $("#btnPunchIn").prop("disabled", !$(this).val());
      }
    })
    .trigger("change");

  updateClock();
  setInterval(updateClock, 1000);
  loadTodayPunch();

  // ── Event bindings ────────────────────────────────────────────────────────
  $("#btnPunchIn").on("click", throttle(punchIn, 3000));
  $("#btnPunchOut").on("click", throttle(punchOut, 3000));

  // ── Clock ─────────────────────────────────────────────────────────────────
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

  // ── Load today's punch record ─────────────────────────────────────────────
  function loadTodayPunch() {
    $.ajax({
      url: `/api/attendance/today/${employeeId}`,
      method: "GET",
      success: function (data) {
        console.log("data", data);
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
          showToast("error", xhr.responseJSON?.message || "Failed to load punch status.");
        }
      },
    });
  }

  // ── Display punch status ──────────────────────────────────────────────────
  function displayPunchStatus(data) {
    currentPunchId = data.id;

    $("#todayPunchIn").text(formatTime(data.punchIn));
    $("#todayPunchOut").text(data.punchOut ? formatTime(data.punchOut) : "--");
    $("#todayTotalHours").text(data.workHours ? data.workHours.toFixed(2) + "h" : "0h");

    if (data.punchOut) {
      punchState = "punched-out";
      setStatusBadge("punched-out", "Punched Out", "fa-user-check");
      updateGlowStrip("default");
      $("#btnPunchIn, #btnPunchOut").hide();
      $("#workingHours").hide();
      $("#punchedFromDiv").hide();
    } else if (data.punchIn) {
      punchState = "punched-in";
      setStatusBadge("punched-in", "Punched In", "fa-user-clock");
      updateGlowStrip("punched-in");
      $("#btnPunchIn").hide();
      $("#btnPunchOut").show();
      $("#punchedFromDiv").hide();
      $("#workingHours").fadeIn();
      startWorkTimer(data.punchIn);
    } else {
      resetPunchUI();
    }
  }

  // ── Reset punch UI ────────────────────────────────────────────────────────
  function resetPunchUI() {
    // Clear punch state BEFORE calling applyPreCheckResult so the
    // guard inside it doesn't short-circuit the badge update.
    punchState = "none";
    // Re-apply pre-check badge (don't blindly say "Not Punched In" if it's a holiday)
    applyPreCheckResult(preCheck);
    $("#btnPunchIn").show();
    $("#punchedFromDiv").show();
    $("#btnPunchOut").hide();
    $("#todayPunchIn, #todayPunchOut").text("--:--");
    $("#todayTotalHours").text("0h");
    $("#workingHours").hide();
    stopWorkTimer();
  }

  // ── Work timer ────────────────────────────────────────────────────────────
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

  // ── Punch In ──────────────────────────────────────────────────────────────
  async function punchIn() {
    // Guard — pre-check is the authority
    if (!preCheck.canPunchIn) {
      if (preCheck.holiday) showToast("error", `Today is ${preCheck.holidayName || "a holiday"}.`);
      else if (preCheck.onLeave) showToast("error", "You are on approved leave today.");
      else if (preCheck.weekOff) showToast("error", "Today is your week-off.");
      else if (preCheck.shiftStatus === "BEFORE_SHIFT")
        showToast("error", `Punch-in opens at ${earliestPunchTime(preCheck.officeStart)}.`);
      else if (preCheck.shiftStatus === "AFTER_SHIFT")
        showToast("error", `Punch-in closed. Office hours ended at ${preCheck.officeClosed}.`);
      else
        showToast("error", "You are not eligible to punch in at this time.");
      return;
    }

    const punchedFrom = $("#punchedFrom").val();
    if (!punchedFrom) {
      showToast("error", "Please select where you are punching in from (WFO/WFH).");
      return;
    }

    const now = new Date().toISOString();
    try {
      const { lat, lng } = await getLocation();
      await new Promise((resolve, reject) => {
        $.ajax({
          url: "/api/attendance/punch-in",
          method: "POST",
          contentType: "application/json",
          data: JSON.stringify({ employeeId, punchIn: now, punchedFrom, lat, lng }),
          success: function (data) {
            showToast("success", "Punched in successfully!");
            displayPunchStatus(data);
            resolve(data);
          },
          error: function (xhr) {
            const msg = xhr.responseJSON?.message || "Punch-in failed.";
            showToast("error", msg);
            reject(new Error(msg));
          },
        });
      });
    } catch (e) {
      if (e.message === "Unable to get location.") {
        showToast("error", "Unable to get your location. Please allow location access.");
      }
      // Other errors already toasted above
    }
  }

  // ── Punch Out ─────────────────────────────────────────────────────────────
  async function punchOut() {
    if (!currentPunchId) {
      showToast("error", "No active punch record found.");
      return;
    }
    const now = new Date().toISOString();
    await new Promise((resolve, reject) => {
      $.ajax({
        url: "/api/attendance/punch-out",
        method: "POST",
        contentType: "application/json",
        data: JSON.stringify({ punchId: currentPunchId, punchOut: now }),
        success: function (data) {
          showToast("success", "Punched out successfully!");
          displayPunchStatus(data);
          stopWorkTimer();
          resolve(data);
        },
        error: function (xhr) {
          const msg = xhr.responseJSON?.message || "Punch-out failed.";
          showToast("error", msg);
          reject(new Error(msg));
        },
      });
    });
  }

  // ── Location helper ───────────────────────────────────────────────────────
  async function getLocation() {
    return new Promise((resolve, reject) => {
      if (!navigator.geolocation)
        return reject(new Error("Geolocation not supported."));
      navigator.geolocation.getCurrentPosition(
        (pos) => resolve({ lat: pos.coords.latitude, lng: pos.coords.longitude }),
        () => reject(new Error("Unable to get location.")),
      );
    });
  }

  // ── Format helpers ────────────────────────────────────────────────────────
  function formatTime(dateTimeStr) {
    if (!dateTimeStr) return "--";
    return new Date(dateTimeStr).toLocaleTimeString("en-IN", { hour12: true });
  }
}
