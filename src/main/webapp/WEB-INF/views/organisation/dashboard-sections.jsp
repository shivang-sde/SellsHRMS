<div class="row g-4">
  <!-- 🔔 Reminders -->
  <div class="col-xl-3 col-lg-4 col-md-6">
    <div class="card dashboard-card border-0 shadow-sm rounded-4 h-100">
      <div class="card-header bg-gradient-warning text-dark fw-semibold d-flex align-items-center">
        <i class="fas fa-bell me-2 text-warning-emphasis"></i>
        <span>Upcoming Reminders</span>
      </div>
      <div class="card-body p-3" id="remindersContainer">
        <div class="text-muted small text-center py-3">Loading...</div>
      </div>
    </div>
  </div>

  <!-- 🎂 Birthdays -->
  <div class="col-xl-3 col-lg-4 col-md-6">
    <div class="card dashboard-card border-0 shadow-sm rounded-4 h-100">
      <div class="card-header bg-gradient-primary text-white fw-semibold d-flex align-items-center">
        <i class="fas fa-birthday-cake me-2"></i>
        <span>Upcoming Birthdays</span>
      </div>
      <div class="card-body p-3" id="birthdaysContainer">
        <div class="text-muted small text-center py-3">Loading...</div>
      </div>
    </div>
  </div>

  <!-- 🏆 Work Anniversaries -->
  <div class="col-xl-3 col-lg-4 col-md-6">
    <div class="card dashboard-card border-0 shadow-sm rounded-4 h-100">
      <div class="card-header bg-gradient-success text-white fw-semibold d-flex align-items-center">
        <i class="fas fa-award me-2"></i>
        <span>Work Anniversaries</span>
      </div>
      <div class="card-body p-3" id="anniversaryContainer">
        <div class="text-muted small text-center py-3">Loading...</div>
      </div>
    </div>
  </div>

  <!-- 🎉 Holidays -->
  <div class="col-xl-3 col-lg-4 col-md-6">
    <div class="card dashboard-card border-0 shadow-sm rounded-4 h-100">
      <div class="card-header bg-gradient-danger text-white fw-semibold d-flex align-items-center">
        <i class="fas fa-umbrella-beach me-2"></i>
        <span>Upcoming Holidays</span>
      </div>
      <div class="card-body p-3" id="holidayContainer">
        <div class="text-muted small text-center py-3">Loading...</div>
      </div>
    </div>
  </div>

  <!-- 📅 Events & Notices -->
  <div class="col-xl-6 col-lg-8 col-md-12">
    <div class="card dashboard-card border-0 shadow-sm rounded-4 h-100">
      <div class="card-header bg-gradient-info text-white fw-semibold d-flex align-items-center">
        <i class="fas fa-calendar-alt me-2"></i>
        <span>Events & Notices</span>
      </div>
      <div class="card-body p-3" id="eventsContainer">
        <div class="text-muted small text-center py-3">Loading...</div>
      </div>
    </div>
  </div>
</div>

<!-- JS -->
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="/js/dashboard-sections.js"></script>

<style>
  /* === Dashboard Card Enhancements === */
  .dashboard-card {
    transition: all 0.25s ease-in-out;
    overflow: hidden;
    background: #fff;
  }

  .dashboard-card:hover {
    transform: translateY(-3px);
    box-shadow: 0 6px 18px rgba(0, 0, 0, 0.08);
  }

  .card-header {
    border-bottom: 0;
    padding: 0.75rem 1rem;
    font-size: 0.95rem;
  }

  .card-body {
    font-size: 0.875rem;
    color: #334155;
  }

  /* === Gradient Variants === */
  .bg-gradient-warning {
    background: linear-gradient(90deg, #ffe08a, #ffc107);
  }

  .bg-gradient-primary {
    background: linear-gradient(90deg, #0d6efd, #5ea3ff);
  }

  .bg-gradient-success {
    background: linear-gradient(90deg, #198754, #43d39e);
  }

  .bg-gradient-danger {
    background: linear-gradient(90deg, #dc3545, #fd7e14);
  }

  .bg-gradient-info {
    background: linear-gradient(90deg, #0dcaf0, #3b82f6);
  }

  /* === Empty State === */
  #remindersContainer .text-muted,
  #birthdaysContainer .text-muted,
  #anniversaryContainer .text-muted,
  #holidayContainer .text-muted,
  #eventsContainer .text-muted {
    color: #6c757d !important;
  }

  /* === Responsive adjustments === */
  @media (max-width: 768px) {
    .card-header {
      font-size: 0.9rem;
    }
  }
</style>