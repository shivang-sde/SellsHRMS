$(document).ready(function () {
  const ORG_ID = window.APP?.ORG_ID;
  const API_BASE = `${window.APP.CONTEXT_PATH}/api/payroll/slips`;
  const $tbody = $("#payslipTableBody");

  loadPayslips();

  // ───────────────────────────────────────────────
  // Load all payslips for organisation
  // ───────────────────────────────────────────────
  function loadPayslips(filters = {}) {
    $tbody.html(`<tr><td colspan="7" class="text-center text-muted py-4">Loading...</td></tr>`);

    $.getJSON(`${API_BASE}/organisation/${ORG_ID}`, filters, function (slips) {
      $tbody.empty();

      if (!slips || slips.length === 0) {
        $tbody.html(`<tr><td colspan="7" class="text-center text-muted py-4">No payslips found</td></tr>`);
        return;
      }

      slips.forEach(slip => {
        const row = `
          <tr>
            <td>${slip.employeeName}</td>
            <td>${slip.department || "-"}</td>
            <td>${slip.periodLabel || `${slip.fromDate} → ${slip.toDate}`}</td>
            <td class="text-end">${formatCurrency(slip.grossPay)}</td>
            <td class="text-end text-danger">${formatCurrency(slip.totalDeductions)}</td>
            <td class="text-end text-success fw-bold">${formatCurrency(slip.netPay)}</td>
            <td class="text-center">
              <button class="btn btn-sm btn-outline-primary me-1" onclick="viewPayslip(${slip.id})">
                <i class="fas fa-eye"></i>
              </button>
              <button class="btn btn-sm btn-outline-danger" onclick="downloadPdf(${slip.id})">
                <i class="fas fa-file-pdf"></i>
              </button>
            </td>
          </tr>`;
        $tbody.append(row);
      });
    }).fail(() => {
      showToast("error", "Failed to load payslips");
    });
  }

  // ───────────────────────────────────────────────
  // Filtering
  // ───────────────────────────────────────────────
  $("#btnFilter").click(() => {
    const emp = $("#filterEmployee").val();
    const period = $("#filterPeriod").val();
    loadPayslips({ employee: emp, period: period });
  });

  $("#btnClear").click(() => {
    $("#filterEmployee").val("");
    $("#filterPeriod").val("");
    loadPayslips();
  });

  // ───────────────────────────────────────────────
  // Actions
  // ───────────────────────────────────────────────
  window.viewPayslip = function (id) {
    window.location.href = `${window.APP.CONTEXT_PATH}/payroll/payslip/${id}`;
  };

  window.downloadPdf = function (id) {
    window.open(`${API_BASE}/${id}/pdf`, "_blank");
  };

  // ───────────────────────────────────────────────
  // Helpers
  // ───────────────────────────────────────────────
  function formatCurrency(v) {
    return v ? `₹${v.toFixed(2).toLocaleString()}` : "-";
  }

  $("#btnExportAll").click(() => {
    $.getJSON(`${API_BASE}/organisation/${ORG_ID}`, function (slips) {
      if (!slips.length) return showToast("warning", "No data to export");

      let csv = "Employee,Department,Period,Gross,Deductions,Net Pay\n";
      slips.forEach(s =>
        csv += `${s.employeeName},${s.department || ""},${s.periodLabel || ""},${s.grossPay},${s.totalDeductions},${s.netPay}\n`
      );

      const blob = new Blob([csv], { type: "text/csv" });
      const link = document.createElement("a");
      link.href = URL.createObjectURL(blob);
      link.download = "Payslips_All.csv";
      link.click();
    });
  });
});
