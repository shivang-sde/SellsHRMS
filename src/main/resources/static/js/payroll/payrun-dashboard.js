$(document).ready(function () {
  const ORG_ID = window.APP?.ORG_ID;
  const API_BASE = `${window.APP.CONTEXT_PATH}/api/payroll/payruns`;
  const $tableBody = $("#payrunsTableBody");
  const $empty = $("#emptyState");

  loadPayRuns();

  $("#btnRefreshRuns").on("click", loadPayRuns);

  // ───────────────────────────────────────────────
  // Load payruns (completed + upcoming)
  // ───────────────────────────────────────────────
  function loadPayRuns() {
    $tableBody.html(`<tr><td colspan="6" class="text-center text-muted py-4">Loading...</td></tr>`);
    $.getJSON(`${API_BASE}/organisation/${ORG_ID}`, function (runs) {
      if (!runs || runs.length === 0) {
        $empty.removeClass("d-none");
        $tableBody.empty();
        updateStats([]);
        return;
      }

      $empty.addClass("d-none");
      runs.sort((a, b) => new Date(b.startDate) - new Date(a.startDate));
      updateStats(runs);
      renderTable(runs);
    }).fail(() => {
      showToast("error", "Failed to load payruns");
    });
  }

  // ───────────────────────────────────────────────
  // Render PayRun Table
  // ───────────────────────────────────────────────
  function renderTable(runs) {
    $tableBody.empty();
    runs.forEach(run => {
      const row = `
        <tr>
          <td>${run.periodLabel || `${run.startDate} → ${run.endDate}`}</td>
          <td>${statusBadge(run.status)}</td>
          <td class="text-center">${run.totalEmployees || "-"}</td>
          <td class="text-end">${formatCurrency(run.totalNet)}</td>
          <td class="text-end">${formatDate(run.runDate)}</td>
          <td class="text-end">
            <button class="btn btn-sm btn-outline-primary" onclick="viewPayRun(${run.id})">
              <i class="fas fa-eye"></i> View
            </button>
          </td>
        </tr>`;
      $tableBody.append(row);
    });
  }

  // ───────────────────────────────────────────────
  // View payrun details
  // ───────────────────────────────────────────────
  window.viewPayRun = function (id) {
    window.location.href = `${window.APP.CONTEXT_PATH}/payroll/payruns/${id}/details`;
  };

  // ───────────────────────────────────────────────
  // Update Stats + Next Payroll Reminder
  // ───────────────────────────────────────────────
  function updateStats(runs) {
    const total = runs.length;
    const completed = runs.filter(r => r.status === "COMPLETED").length;
    const processing = runs.filter(r => r.status === "PROCESSING").length;
    const totalNet = runs.reduce((sum, r) => sum + (r.totalNet || 0), 0);

    const nextRun = runs.find(r => ["READY", "UPCOMING"].includes(r.status));
    $("#statTotal").text(total);
    $("#statCompleted").text(completed);
    $("#statProcessing").text(processing);
    $("#statPayout").text(formatCurrency(totalNet));
    $("#nextRunDate").text(nextRun ? formatDate(nextRun.runDate) : "-");
  }

  // ───────────────────────────────────────────────
  // Helper Functions
  // ───────────────────────────────────────────────
  function statusBadge(status) {
    const map = {
      "READY": "secondary",
      "UPCOMING": "info",
      "PROCESSING": "warning",
      "COMPLETED": "success",
      "FAILED": "danger"
    };
    return `<span class="badge bg-${map[status] || 'secondary'}">${status}</span>`;
  }

  function formatCurrency(val) {
    return `₹${(val || 0).toLocaleString()}`;
  }

  function formatDate(dateStr) {
    return dateStr ? new Date(dateStr).toLocaleDateString() : "-";
  }
});
