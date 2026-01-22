$(document).ready(function () {
  const ORG_ID = $("#globalOrgId").val();
  const API_BASE = `${window.APP.CONTEXT_PATH}/api/payroll/payrun`;

  loadPayRuns();

  // Load all pay runs for this org
  function loadPayRuns() {
    $.getJSON(`${API_BASE}/organisation/${ORG_ID}`, function (runs) {
      const tbody = $("#payrunTable tbody").empty();
      runs.forEach(run => {
        const row = `
          <tr>
            <td>${run.periodLabel || "-"}</td>
            <td>${run.startDate}</td>
            <td>${run.endDate}</td>
            <td><span class="badge bg-${statusColor(run.status)}">${run.status}</span></td>
            <td>${formatCurrency(run.totalGross)}</td>
            <td>${formatCurrency(run.totalDeduction)}</td>
            <td>${formatCurrency(run.totalNet)}</td>
            <td>
              <button class="btn btn-sm btn-outline-primary me-1" onclick="viewPayRun(${run.id})">
                <i class="fa fa-eye"></i>
              </button>
              ${run.status === 'READY' ? `
                <button class="btn btn-sm btn-success" onclick="processPayRun(${run.id})">
                  <i class="fa fa-play"></i>
                </button>` : ''}
            </td>
          </tr>`;
        tbody.append(row);
      });
    });
  }

  // Start new pay run
  $("#btnNewPayRun").click(function () {
    const now = new Date();
    const start = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-01`;
    const end = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(new Date(now.getFullYear(), now.getMonth() + 1, 0).getDate()).padStart(2, '0')}`;
    $.ajax({
      url: `${API_BASE}/create?orgId=${ORG_ID}`,
      type: "POST",
      contentType: "application/json",
      data: JSON.stringify({ startDate: start, endDate: end }),
      success: () => {
        showToast("success", "Pay run created successfully.");
        loadPayRuns();
      },
      error: () => showToast("error", "Failed to create pay run.")
    });
  });

  // Trigger payroll computation
  window.processPayRun = function (id) {
    $.ajax({
      url: `${API_BASE}/${id}/process`,
      type: "POST",
      success: function () {
        showToast("success", "Pay run processed successfully.");
        loadPayRuns();
      },
      error: () => showToast("error", "Failed to process pay run.")
    });
  };

  // Navigate to details
  window.viewPayRun = function (id) {
    window.location.href = `${window.APP.CONTEXT_PATH}/org/payroll/payrun/${id}`;
  };

  function formatCurrency(v) { return v ? `₹${v.toFixed(2)}` : "-"; }
  function statusColor(status) {
    switch (status) {
      case "COMPLETED": return "success";
      case "APPROVED": return "info";
      case "READY": return "secondary";
      case "CANCELLED": return "danger";
      default: return "dark";
    }
  }
});
