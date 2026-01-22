$(document).ready(function () {
  const slipId = window.location.pathname.split("/").pop();
  const SLIP_API = `${window.APP.CONTEXT_PATH}/api/payroll/salary-slips/${slipId}`;
  const PDF_API = `${SLIP_API}/pdf`;

  loadPayslip();

  // ───────────────────────────────────────────────
  // Load Salary Slip Details
  // ───────────────────────────────────────────────
  function loadPayslip() {
    $.getJSON(SLIP_API, function (slip) {
      $("#empName").text(slip.employeeName);
      $("#payPeriod").text(`${slip.fromDate} → ${slip.toDate}`);
      $("#workingDays").text(slip.workingDays || 0);
      $("#paymentDays").text(slip.paymentDays || 0);
      $("#lopDays").text(slip.lopDays || 0);

      $("#grossPay").text(formatCurrency(slip.grossPay));
      $("#totalDeductions").text(formatCurrency(slip.totalDeductions));
      $("#netPay").text(formatCurrency(slip.netPay));
      $("#netPayWords").text(`In words: ${toWords(Math.round(slip.netPay))} only`);

      renderComponents(slip.components);
    }).fail(() => {
      showToast("error", "Failed to load payslip details");
    });
  }

  // ───────────────────────────────────────────────
  // Render components (earnings / deductions)
  // ───────────────────────────────────────────────
  function renderComponents(components) {
    const earnings = components.filter(c => c.componentType === "EARNING");
    const deductions = components.filter(c => c.componentType === "DEDUCTION");

    const $earn = $("#earningsBody").empty();
    const $ded = $("#deductionsBody").empty();

    if (earnings.length === 0)
      $earn.append(`<tr><td colspan="2" class="text-center text-muted py-3">No earnings</td></tr>`);
    else
      earnings.forEach(c =>
        $earn.append(`<tr><td>${c.componentName}</td><td class="text-end">${formatCurrency(c.amount)}</td></tr>`)
      );

    if (deductions.length === 0)
      $ded.append(`<tr><td colspan="2" class="text-center text-muted py-3">No deductions</td></tr>`);
    else
      deductions.forEach(c =>
        $ded.append(`<tr><td>${c.componentName}</td><td class="text-end">${formatCurrency(c.amount)}</td></tr>`)
      );
  }

  // ───────────────────────────────────────────────
  // PDF Download
  // ───────────────────────────────────────────────
  $("#btnDownloadPdf").click(function () {
    window.open(PDF_API, "_blank");
  });

  // ───────────────────────────────────────────────
  // Helpers
  // ───────────────────────────────────────────────
  function formatCurrency(v) {
    return v ? `₹${Number(v).toLocaleString(undefined, { minimumFractionDigits: 2 })}` : "₹0.00";
  }

  // Simple number-to-words (Indian system)
  function toWords(num) {
    if (num === 0) return "Zero Rupees";
    const units = ["", "Thousand", "Lakh", "Crore"];
    let parts = [];
    let unitIndex = 0;
    while (num > 0) {
      let part = num % 1000;
      if (part > 0) parts.unshift(part + " " + units[unitIndex]);
      num = Math.floor(num / 1000);
      unitIndex++;
    }
    return parts.join(" ") + " Rupees";
  }
});
