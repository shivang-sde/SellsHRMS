<div class="card border-0 shadow-lg p-5 mx-auto text-center" style="max-width: 550px; border-radius: 20px;">
  <div class="mb-4">
    <span class="display-1 text-danger opacity-75">
      <i class="fa-solid fa-building-circle-exclamation"></i>
    </span>
  </div>
  <h2 class="fw-bold text-dark mb-2">Organisation Update Needed</h2>
  <p class="text-muted px-3">
    ${errorMessage != null ? errorMessage : "Your organization's workspace is currently on hold."}
  </p>
  <div class="bg-light p-3 rounded-3 mb-4 text-start">
    <small class="text-uppercase fw-bold text-muted d-block mb-1">How to resolve:</small>
    <ul class="small text-muted mb-0">
      <li>Check with your billing administrator.</li>
      <li>Renew your license via the admin panel.</li>
      <li>Contact our support team for immediate help.</li>
    </ul>
  </div>
  <div class="d-flex flex-column gap-2">
    <a href="mailto:support@sellspark.in" class="btn btn-primary py-2">
      <i class="fa-solid fa-envelope me-2"></i>Contact SellsPark Support
    </a>
    <a href="/logout" class="btn btn-link text-decoration-none text-muted">
      <i class="fa-solid fa-right-from-bracket me-2"></i>Sign in as different user
    </a>
  </div>
</div>