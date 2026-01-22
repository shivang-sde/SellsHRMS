<div class="container-fluid py-3">
  <div class="d-flex justify-content-between align-items-center mb-3">
    <h4 class="fw-bold mb-0">Pay Runs</h4>
    <button id="btnNewPayRun" class="btn btn-primary">
      <i class="fa fa-play-circle"></i> Start New Pay Run
    </button>
  </div>

  <div class="card shadow-sm">
    <div class="card-body">
      <table class="table table-bordered table-hover align-middle" id="payrunTable">
        <thead class="table-light">
          <tr>
            <th>Period</th>
            <th>Start</th>
            <th>End</th>
            <th>Status</th>
            <th>Total Gross</th>
            <th>Total Deduction</th>
            <th>Total Net</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody></tbody>
      </table>
    </div>
  </div>
</div>
