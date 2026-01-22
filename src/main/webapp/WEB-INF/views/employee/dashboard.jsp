<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container-fluid">
  <div class="row mb-4">
    <div class="col">
      <h3 class="fw-bold text-primary">Welcome, ${sessionScope.USER_NAME} 👋</h3>
      <p class="text-muted">Here’s a quick overview of your day and activities.</p>
    </div>
  </div>

  <!-- Summary Cards -->
  <!-- <div class="row g-3 mb-4">
    <div class="col-md-3 col-sm-6">
      <div class="card p-3 shadow-sm h-100 text-center border-0">
        <div class="icon-box mb-2"><i class="fa fa-calendar-check text-primary"></i></div>
        <h6 class="text-muted">Total Leaves</h6>
        <h4 id="empTotalLeaves" class="fw-bold mb-0">--</h4>
      </div>
    </div>
    <div class="col-md-3 col-sm-6">
      <div class="card p-3 shadow-sm h-100 text-center border-0">
        <div class="icon-box mb-2"><i class="fa fa-user-clock text-primary"></i></div>
        <h6 class="text-muted">Today’s Attendance</h6>
        <h4 id="empTodayStatus" class="fw-bold mb-0">--</h4>
      </div>
    </div>
    <div class="col-md-3 col-sm-6">
      <div class="card p-3 shadow-sm h-100 text-center border-0">
        <div class="icon-box mb-2"><i class="fa fa-clock text-primary"></i></div>
        <h6 class="text-muted">Work Hours</h6>
        <h4 id="empWorkHours" class="fw-bold mb-0">--</h4>
      </div>
    </div>
    <div class="col-md-3 col-sm-6">
      <div class="card p-3 shadow-sm h-100 text-center border-0">
        <div class="icon-box mb-2"><i class="fa fa-cake-candles text-primary"></i></div>
        <h6 class="text-muted">Upcoming Birthday</h6>
        <h4 id="empUpcomingBirthday" class="fw-bold mb-0">--</h4>
      </div>
    </div>
  </div> -->

  <jsp:include page="/WEB-INF/views/organisation/dashboard-sections.jsp"/>

  <!-- Punch In / Out + Noticeboard -->
  <div class="row g-3 mb-4">
    <div class="col-lg-4 col-md-6">
      <div class="card shadow-sm border-0 p-3 text-center">
        <h6 class="fw-semibold mb-3">Attendance Punch</h6>
        <button id="punchBtn" class="btn btn-primary px-4 py-2 rounded-pill fw-semibold">
          <i class="fa fa-fingerprint me-2"></i> PUNCH IN
        </button>
        <div class="mt-3">
          <span id="punchTimer" class="timer-pill">00:00:00</span>
        </div>
      </div>
    </div>

    <div class="col-lg-8 col-md-6">
      <div class="card shadow-sm border-0 p-3 h-100">
        <h6 class="fw-semibold mb-3"><i class="fa fa-bullhorn text-primary me-2"></i>Company Announcements</h6>
        <ul id="announcementList" class="list-unstyled small mb-0">
          <li class="mb-2"><i class="fa fa-circle text-success me-2"></i> Welcome to the new HRMS platform!</li>
          <li class="mb-2"><i class="fa fa-circle text-info me-2"></i> Remember to punch in before 10:30 AM.</li>
          <li class="mb-2"><i class="fa fa-circle text-warning me-2"></i> Annual team outing scheduled next week.</li>
        </ul>
      </div>
    </div>
  </div>

  <!-- Charts + Activity Log -->
  <!-- <div class="row g-3">
    <div class="col-md-8">
      <div class="card shadow-sm border-0 p-3">
        <h6 class="fw-semibold mb-3"><i class="fa fa-chart-line text-primary me-2"></i> Attendance Overview</h6>
        <canvas id="attendanceChart" height="140"></canvas>
      </div>
    </div>
    <div class="col-md-4">
      <div class="card shadow-sm border-0 p-3">
        <h6 class="fw-semibold mb-3"><i class="fa fa-list text-primary me-2"></i> Recent Activity</h6>
        <ul id="activityLog" class="list-unstyled small mb-0">
          <li class="mb-2"><i class="fa fa-check-circle text-success me-2"></i> You punched in at 09:31 AM</li>
          <li class="mb-2"><i class="fa fa-calendar text-info me-2"></i> Applied leave for 24–25 Dec</li>
          <li><i class="fa fa-clock text-warning me-2"></i> Worked 7h 45m yesterday</li>
        </ul>
      </div>
    </div>
  </div> -->
</div>

<!-- JS (chart & logic) -->
<script>
document.addEventListener("DOMContentLoaded", () => {
  // Chart.js Attendance graph
//   const ctx = document.getElementById('attendanceChart');
//   if (ctx) {
//     new Chart(ctx, {
//       type: 'line',
//       data: {
//         labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri'],
//         datasets: [{
//           label: 'Hours Worked',
//           data: [8, 7.5, 8.5, 6, 7],
//           fill: true,
//           borderColor: '#2666F6',
//           backgroundColor: 'rgba(38,102,246,0.1)',
//           tension: 0.4
//         }]
//       },
//       options: {
//         scales: { y: { beginAtZero: true, grid: { color: '#f1f5f9' } } },
//         plugins: { legend: { display: false } },
//         responsive: true,
//         maintainAspectRatio: false
//       }
//     });
//   }

  // Punch Button simulation
  const punchBtn = document.getElementById('punchBtn');
  if (punchBtn) {
    let punchedIn = false;
    punchBtn.addEventListener('click', () => {
      punchedIn = !punchedIn;
      punchBtn.innerHTML = punchedIn 
        ? '<i class="fa fa-sign-out-alt me-2"></i> PUNCH OUT'
        : '<i class="fa fa-fingerprint me-2"></i> PUNCH IN';
      punchBtn.classList.toggle('btn-success', punchedIn);
      punchBtn.classList.toggle('btn-primary', !punchedIn);
      showToast('success', punchedIn ? 'You punched in!' : 'You punched out!');
    });
  }
});
</script>
