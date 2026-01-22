<div class="row g-3">

  <!-- Reminders -->
<!-- Upcoming Reminders -->
<div class="col-md-4">
  <div class="card shadow-sm">
    <div class="card-header bg-warning text-dark fw-bold">
      <i class="fas fa-bell me-1"></i>Upcoming Reminders
    </div>
    <div class="card-body p-2" id="remindersContainer">
      <div class="text-muted small">Loading...</div>
    </div>
  </div>
</div>


  <!-- Birthdays -->
  <div class="col-md-4">
    <div class="card shadow-sm">
      <div class="card-header bg-info text-dark fw-bold">Upcoming Birthday</div>
      <div class="card-body p-2" id="birthdaysContainer"></div>
    </div>
  </div>

  <!-- Work Anniversary -->
  <div class="col-md-4">
    <div class="card shadow-sm">
      <div class="card-header bg-info text-dark fw-bold">Upcoming Work Anniversary</div>
      <div class="card-body p-2" id="anniversaryContainer"></div>
    </div>
  </div>

  <!-- Holidays -->
  <div class="col-md-4">
    <div class="card shadow-sm">
      <div class="card-header bg-info text-dark fw-bold">Upcoming Holiday</div>
      <div class="card-body p-2" id="holidayContainer"></div>
    </div>
  </div>

  <!-- Events + Notices -->
  <div class="col-md-6">
    <div class="card shadow-sm">
      <div class="card-header bg-info text-dark fw-bold">Upcoming Events and Notice</div>
      <div class="card-body p-2" id="eventsContainer"></div>
    </div>
  </div>

  <!-- Knowledge Base -->
  <!-- <div class="col-md-6">
    <div class="card shadow-sm">
      <div class="card-header bg-info text-dark fw-bold">Knowledge Base</div>
      <div class="card-body p-2" id="knowledgeContainer"></div>
    </div>
  </div> -->

</div>

<!-- <c:set var="pageScript" value="/js/dashboard-sections" /> -->
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="/js/dashboard-sections.js"></script>
