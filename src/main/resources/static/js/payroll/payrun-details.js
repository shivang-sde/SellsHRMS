$(document).ready(function () {
  const payRunId = window.location.pathname.split("/").pop();
  const API_BASE = `${window.APP.CONTEXT_PATH}/api/payroll/payrun`;
  const SLIP_API = `${window.APP.CONTEXT_PATH}/api/payroll/slips/payrun/${payRunId}`;
  const SLIP_DOWNLOAD_API = `${window.APP.CONTEXT_PATH}/api/payroll/salary-slips`;

  loadPayRunDetails();

  // ───────────────────────────────────────────────
  // Load pay run summary
  // ───────────────────────────────────────────────
  function loadPayRunDetails() {
    $.getJSON(`${API_BASE}/${payRunId}`, function (run) {
      $("#period").text(`${run.startDate} → ${run.endDate}`);
      $("#runStatus")
        .removeClass()
        .addClass("badge")
        .addClass(statusClass(run.status))
        .text(run.status || "-");

      $("#runDate").text(run.runDate || "-");
      $("#totalGross").text(formatCurrency(run.totalGross));
      $("#totalDeductions").text(formatCurrency(run.totalDeduction));
      $("#totalNet").text(formatCurrency(run.totalNet));

      if (run.status === "UPCOMING") {
        showToast("info", `Next payroll scheduled on ${run.runDate}`);
      }

      loadSalarySlips();
    }).fail(() => {
      showToast("error", "Failed to load pay run details");
    });
  }

  // ───────────────────────────────────────────────
  // Load slips for this pay run
  // ───────────────────────────────────────────────
  function loadSalarySlips() {
    $.getJSON(SLIP_API, function (slips) {
      const tbody = $("#slipTableBody").empty();

      if (!slips || slips.length === 0) {
        tbody.append(`<tr><td colspan="6" class="text-center text-muted py-4">No salary slips available</td></tr>`);
        return;
      }

      slips.forEach(slip => {
        const row = `
          <tr>
            <td>${slip.employeeName}</td>
            <td>${formatCurrency(slip.grossPay)}</td>
            <td>${formatCurrency(slip.totalDeductions)}</td>
            <td>${formatCurrency(slip.netPay)}</td>
            <td><span class="badge bg-${slip.status === "GENERATED" ? "success" : "secondary"}">${slip.status}</span></td>
            <td class="text-end">
              <button class="btn btn-sm btn-outline-primary me-1" onclick="viewSlip(${slip.id})">
                <i class="fas fa-eye"></i> View
              </button>
              <button class="btn btn-sm btn-outline-success" onclick="downloadSlip(${slip.id})">
                <i class="fas fa-download"></i> PDF
              </button>
            </td>
          </tr>`;
        tbody.append(row);
      });
    });
  }

  // ───────────────────────────────────────────────
  // Slip Actions
  // ───────────────────────────────────────────────
  window.viewSlip = function (id) {
    window.location.href = `${window.APP.CONTEXT_PATH}/payroll/payslip/${id}`;
  };

  window.downloadSlip = function (id) {
    window.open(`${SLIP_DOWNLOAD_API}/${id}/pdf`, "_blank");
  };

  // ───────────────────────────────────────────────
  // Export to CSV
  // ───────────────────────────────────────────────
  $("#btnExport").click(function () {
    $.getJSON(SLIP_API, function (slips) {
      if (!slips.length) return showToast("warning", "No data to export.");

      let csv = "Employee,Gross Pay,Deductions,Net Pay,Status\n";
      slips.forEach(s =>
        csv += `${s.employeeName},${s.grossPay},${s.totalDeductions},${s.netPay},${s.status}\n`
      );

      const blob = new Blob([csv], { type: "text/csv" });
      const link = document.createElement("a");
      link.href = URL.createObjectURL(blob);
      link.download = `PayRun_${payRunId}.csv`;
      link.click();
    });
  });

  // ───────────────────────────────────────────────
  // Helpers
  // ───────────────────────────────────────────────
  function formatCurrency(v) {
    return v ? `₹${Number(v).toLocaleString(undefined, { minimumFractionDigits: 2 })}` : "₹0.00";
  }

  function statusClass(status) {
    const map = {
      "UPCOMING": "bg-info",
      "PROCESSING": "bg-warning",
      "COMPLETED": "bg-success",
      "FAILED": "bg-danger"
    };
    return map[status] || "bg-secondary";
  }
});
