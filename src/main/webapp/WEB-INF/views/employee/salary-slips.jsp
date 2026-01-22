<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container mt-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h4><i class="bi bi-cash-stack me-2"></i>My Salary Slips</h4>
    </div>

    <div class="card shadow-sm">
        <div class="card-body">
            <div id="slipTableLoader" class="text-center py-5">
                <div class="spinner-border text-primary" role="status"></div>
                <p class="text-muted mt-2">Loading salary slips...</p>
            </div>

            <div class="table-responsive" id="slipTableContainer" style="display:none;">
                <table class="table table-striped align-middle">
                    <thead class="table-light">
                        <tr>
                            <th>Month</th>
                            <th>Year</th>
                            <th>Net Pay</th>
                            <th>Status</th>
                            <th class="text-center">Actions</th>
                        </tr>
                    </thead>
                    <tbody id="salarySlipTableBody"></tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<!-- Modal for slip preview -->
<div class="modal fade" id="slipPreviewModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-lg modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Salary Slip Preview</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body" id="slipPreviewContent" style="min-height: 400px;">
        <div class="text-center py-5 text-muted">
          <div class="spinner-border text-primary" role="status"></div>
          <p>Loading salary slip...</p>
        </div>
      </div>
    </div>
  </div>
</div>
