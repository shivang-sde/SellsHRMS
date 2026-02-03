//employee-dashboard.js 

$(document).ready(function () {
  const employeeId = window.APP.EMPLOYEE_ID || $('#loggedEmployeeId').val();
  let currentPunchId = null;
  let punchInTime = null;
  let timerInterval = null;

    if (!employeeId) {
        showToast("error", "Invalid Session: Employee ID Missing");
        return;
    }



  // Initial load
  loadEmployeeProfile(employeeId);
  loadTodayPunch();

  // =============================
  // LOAD TODAY'S PUNCH STATUS
  // =============================
  function loadTodayPunch() {
    $.ajax({
      url: `/api/attendance/today/${employeeId}`,
      method: 'GET',
      success: function (data) {
        if (data && data.punchIn && !data.punchOut) {
          // Already punched in
          currentPunchId = data.id;
          punchInTime = new Date(data.punchIn);
          setAsPunchedIn();
          startTimer();
        } else {
          // Not punched in
          resetPunchButton();
        }
      },
      error: function () {
        resetPunchButton();
      }
    });
  }

  // =============================
  // HANDLE BUTTON CLICK
  // =============================
  $('#punchBtn').on('click', async function () {
    const isPunchIn = $(this).text().includes("PUNCH IN");

    if (isPunchIn) {
      await handlePunchIn();
    } else {
      await handlePunchOut();
    }
  });

  // =============================
  // HANDLE PUNCH IN
  // =============================
  async function handlePunchIn() {
    const now = new Date().toISOString();
    try {
      const { lat, lng } = await getLocation();
      $.ajax({
        url: '/api/attendance/punch-in',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
          employeeId,
          punchIn: now,
          source: 'WEB',
          punchedFrom: 'WFO', // default for dashboard, can be modified to take user input
          lat,
          lng
        }),
        success: function (data) {
          showToast('success', 'Punched in successfully!');
          currentPunchId = data.id;
          punchInTime = new Date(data.punchIn);
          setAsPunchedIn();
          startTimer();
        },
        error: function () {
          showToast('error', 'Failed to punch in.');
        }
      });
    } catch (err) {
      showToast('error', 'Location access denied or unavailable.');
    }
  }

  // =============================
  // HANDLE PUNCH OUT
  // =============================
  async function handlePunchOut() {
    if (!currentPunchId) {
      showToast('error', 'No active punch found.');
      return;
    }

    const now = new Date().toISOString();

    $.ajax({
      url: '/api/attendance/punch-out',
      method: 'POST',
      contentType: 'application/json',
      data: JSON.stringify({ punchId: currentPunchId, punchOut: now }),
      success: function (data) {
        showToast('success', 'Punched out successfully!');
        stopTimer();
        resetPunchButton();
      },
      error: function () {
        showToast('error', 'Failed to punch out.');
      }
    });
  }

  // =============================
  // TIMER FUNCTIONS
  // =============================
  function startTimer() {
    stopTimer();
    timerInterval = setInterval(function () {
      const diff = new Date() - punchInTime;
      const hours = String(Math.floor(diff / (1000 * 60 * 60))).padStart(2, '0');
      const mins = String(Math.floor((diff / (1000 * 60)) % 60)).padStart(2, '0');
      const secs = String(Math.floor((diff / 1000) % 60)).padStart(2, '0');
      $('#punchTimer').text(`${hours}:${mins}:${secs}`);
    }, 1000);
  }

  function stopTimer() {
    if (timerInterval) {
      clearInterval(timerInterval);
      timerInterval = null;
    }
  }

  // =============================
  // UI HELPERS
  // =============================
  function setAsPunchedIn() {
    $('#punchBtn')
      .removeClass('btn-primary')
      .addClass('btn-danger')
      .html('<i class="fa fa-sign-out-alt me-2"></i> PUNCH OUT');
  }

  function resetPunchButton() {
    $('#punchBtn')
      .removeClass('btn-danger')
      .addClass('btn-primary')
      .html('<i class="fa fa-fingerprint me-2"></i> PUNCH IN');
    $('#punchTimer').text('00:00:00');
    currentPunchId = null;
    punchInTime = null;
  }


  async function loadEmployeeProfile(id) {
    const container = $('#employeeProfileContainer');   

    try {
        const res = await fetch(`/api/employees/${id}`);
        if (!res.ok) throw new Error("Failed to load employee details");

        const emp = await res.json();

        container.innerHTML = `
            <div class="row">

                <div class="col-md-4">
                    <div class="card p-3 shadow-sm">
                        <h5 class="mb-2">Profile</h5>
                        <p><strong>Name:</strong> ${emp.fullName}</p>
                        <p><strong>Email:</strong> ${emp.email}</p>
                        <p><strong>Phone:</strong> ${emp.phone}</p>
                        <p><strong>Status:</strong> 
                            <span class="badge bg-${emp.status === 'ACTIVE' ? 'success' : 'secondary'}">
                                ${emp.status}
                            </span>
                        </p>
                    </div>
                </div>

                <div class="col-md-8">
                    <div class="card p-3 shadow-sm">
                        <h5 class="mb-3">Organisation Info</h5>

                        <p><strong>Organisation:</strong> ${emp.organisation}</p>
                        <p><strong>Department:</strong> ${emp.department}</p>
                        <p><strong>Designation:</strong> ${emp.designation}</p>

                        <hr>
                        <a class="btn btn-primary" href="/employee/profile">View Detailed Profile</a>
                    </div>
                </div>

            </div>
        `;
    } catch (err) {
        container.innerHTML = `<div class="alert alert-danger">${err.message}</div>`;
    }
}

  // =============================
  // GET LOCATION (PROMISE)
  // =============================
  async function getLocation() {
    return new Promise((resolve, reject) => {
      if (!navigator.geolocation) {
        return reject(new Error('Geolocation not supported'));
      }
      navigator.geolocation.getCurrentPosition(
        pos => resolve({ lat: pos.coords.latitude, lng: pos.coords.longitude }),
        err => reject(err)
      );
    });
  }

  // =============================
  // TOAST (UTIL)
  // =============================
});







