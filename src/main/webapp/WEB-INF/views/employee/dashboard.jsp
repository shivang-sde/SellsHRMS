<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container-fluid">
  <div class="row mb-4">
    <div class="col">
      <h3 class="fw-bold text-primary">Welcome, ${sessionScope.USER_NAME} 👋</h3>
      <p class="text-muted">Here’s a quick overview of your day and activities.</p>
    </div>
  </div>


  <jsp:include page="/WEB-INF/views/organisation/dashboard-sections.jsp"/>

<c:import url="/WEB-INF/views/employee/punch-in-out-section.jsp" />


  <!-- Punch In / Out + Noticeboard -->
  <!-- <div class="row g-3 mb-4">
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
  </div> -->
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

 
</div>

<!-- JS (chart & logic) -->

