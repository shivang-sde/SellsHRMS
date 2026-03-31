const EMPLOYEE_ID = $("#globalEmployeeId").val() || window.APP.EMPLOYEE_ID;

let creditedSlipsCache = [];

$(document).ready(function () {
  loadEmployeeSalarySlips();
});

function loadEmployeeSalarySlips() {
  $.ajax({
    url: `/api/payroll/salary-slips/employee/${EMPLOYEE_ID}`,
    type: "GET",
    success: function (response) {
      $("#slipTableLoader").hide();
      $("#slipTableContainer").show();

      if (response.success && response.data && response.data.length > 0) {

        creditedSlipsCache = response.data;
        renderSlipTable(response.data);
      } else {
        $("#salarySlipTableBody").html(`
                    <tr>
                        <td colspan="5" class="text-center text-muted py-4">
                            <i class="bi bi-inbox" style="font-size: 2rem; display:block; margin-bottom:10px;"></i>
                            No salary slips found
                        </td>
                    </tr>
                `);
      }
    },
    error: function () {
      $("#slipTableLoader").hide();
      showAlert("danger", "Failed to load salary slips");
    },
  });
}

function renderSlipTable(data) {
  const tbody = $("#salarySlipBody");
  tbody.empty();

  // Filter only credited salary slips
  const creditedSlips = (data || []).filter(slip => slip.isCredited === true);


  if (creditedSlips.length === 0) {
    tbody.append(`<tr><td colspan="6" class="text-center text-muted py-4">
      <i class="bi bi-inbox" style="font-size: 2rem; display:block; margin-bottom:10px;"></i>
      No credited salary slips found
    </td></tr>`);
    return;
  }

  creditedSlips.forEach((s) => {
    console.log("Employee Slip with credited:", s);

    // Statutory deductions summary
    let statutoryList = "";
    let totalStatutory = 0;

    if (s.components && s.components.length > 0) {
      s.components.forEach(comp => {
        if (comp.isStatutory) {
          statutoryList += `
            <div class="d-flex justify-content-between small border-bottom mb-1">
              <span class="text-muted">${comp.componentAbbreviation || comp.componentName}:</span>
              <span class="fw-bold text-danger">₹${comp.amount.toFixed(2)}</span>
            </div>`;
          totalStatutory += comp.amount;
        }
      });
    }

    const otherDeductions = (s.totalDeductions || 0) - totalStatutory;
    const statusBadge = `<span class="badge bg-success">Credited</span>`;
    const netPayFormatted = s.netPay ? `₹${s.netPay.toLocaleString()}` : "₹0.00";

    // Download & preview buttons
    const downloadBtn = s.pdfUrl
      ? `<a href="${window.APP.REQUEST_URL}/${s.pdfUrl}" class="btn btn-outline-success btn-sm" target="_blank">
          <i class="bi bi-file-earmark-arrow-down"></i> Download
        </a>`
      : `<span class="badge bg-secondary">Not Available</span>`;

    const previewBtn = `<button class="btn btn-outline-primary btn-sm" onclick="previewSlip(${s.id})">
                          <i class="bi bi-eye"></i> Preview
                        </button>`;

    // Table row (clean and readable)
    tbody.append(`
      <tr>
        <td>
          <div class="fw-bold text-primary">${s.payMonth} ${s.payYear}</div>
          <small class="text-muted">${s.departmentName || "-"}</small>
        </td>
        <td>
          <div><strong>Gross:</strong> ₹${s.actualGross || 0}</div>
          <div><strong>Deductions:</strong> ₹${s.totalDeductions || 0}</div>
        </td>
        <td>${netPayFormatted}</td>
        <td>${statusBadge}</td>
        <td>
          ${previewBtn} ${downloadBtn}
        </td>
      </tr>
    `);
  });
}



function previewSlip(id) {
  const slip = creditedSlipsCache.find(s => s.id === id);
  if (!slip) {
    showAlert("warning", "Slip data not found in cache");
    return;
  }

  $("#slipPreviewModal").modal("show");

  // directly render using existing slip DTO
  renderSlipPreview(slip);
}


function renderSlipPreview(slip) {
  $("#slipPreviewModal").modal("show");
  $("#slipPreviewContent").html(`
    <div class="text-center py-5 text-muted">
      <div class="spinner-border text-primary" role="status"></div>
      <p>Loading salary slip...</p>
    </div>
  `);

  setTimeout(() => {
    const earnings = slip.components?.filter(c => c.componentType === "EARNING") || [];
    const deductions = slip.components?.filter(c => c.componentType === "DEDUCTION") || [];

    const earningRows = earnings.map(c => `
        <tr>
          <td>${c.componentName}</td>
          <td class="text-end">₹${c.amount.toFixed(2)}</td>
          <td class="text-muted small">${c.calculationLog || "-"}</td>
        </tr>
      `).join("");

    const deductionRows = deductions.map(c => `
        <tr>
          <td>${c.componentName}</td>
          <td class="text-end text-danger">₹${c.amount.toFixed(2)}</td>
          <td class="text-muted small">${c.calculationLog || "-"}</td>
        </tr>
      `).join("");

    const headerHtml = `
        <div class="text-center mb-3">
          <h5 class="fw-bold text-primary mb-1">${slip.employeeName}</h5>
          <div class="text-muted">${slip.designationName || "-"} | ${slip.departmentName || "-"}</div>
          <div class="small mt-1 text-secondary">${slip.payMonth} ${slip.payYear}</div>
        </div>
        <hr>
      `;

    const summaryHtml = `
        <div class="row text-center mb-4">
          <div class="col-md-3 mb-2">
            <div class="border rounded p-2 bg-light">
              <div class="small text-muted">LOP Days</div>
              <div class="fw-bold text-danger">${slip.lopDays || 0}</div>
            </div>
          </div>
          <div class="col-md-3 mb-2">
            <div class="border rounded p-2 bg-light">
              <div class="small text-muted">Annual CTC</div>
              <div class="fw-bold">₹${slip.annualCtc?.toLocaleString() || 0}</div>
            </div>
          </div>
          <div class="col-md-4 mb-2">
            <div class="border rounded p-2 bg-light">
              <div class="small text-muted">Monthly CTC</div>
              <div class="fw-bold">₹${slip.monthlyGrossTarget?.toLocaleString() || 0}</div>
            </div>
          </div>
        </div>

        <div class="row text-center mb-4">
         
          <div class="col-md-4 mb-2">
            <div class="border rounded p-2 bg-light">
              <div class="small text-muted">Monthly Net Target (without LOP)</div>
              <div class="fw-bold">₹${slip.monthlyNetTarget?.toLocaleString() || 0}</div>
            </div>
          </div>
          <div class="col-md-4 mb-2">
            <div class="border rounded p-2 bg-success bg-opacity-10">
              <div class="small text-muted">Actual Gross (after LOP)</div>
              <div class="fw-bold text-success">₹${slip.actualGross?.toLocaleString() || 0}</div>
            </div>
          </div>
        </div>
      `;

    const breakdownHtml = `
        <div class="row">
          <div class="col-md-6">
            <h6 class="text-primary mb-2">Earnings</h6>
            <table class="table table-sm table-bordered align-middle">
              <thead class="table-light">
                <tr>
                  <th>Component</th>
                  <th class="text-end">Amount</th>
                  <th class="text-muted small">Formula</th>
                </tr>
              </thead>
              <tbody>${earningRows || `<tr><td colspan="3" class="text-center text-muted">No earnings</td></tr>`}</tbody>
            </table>
          </div>

          <div class="col-md-6">
            <h6 class="text-danger mb-2">Deductions</h6>
            <table class="table table-sm table-bordered align-middle">
              <thead class="table-light">
                <tr>
                  <th>Component</th>
                  <th class="text-end">Amount</th>
                  <th class="text-muted small">Formula</th>
                </tr>
              </thead>
              <tbody>${deductionRows || `<tr><td colspan="3" class="text-center text-muted">No deductions</td></tr>`}</tbody>
            </table>
          </div>
        </div>
      `;

    const totalsHtml = `
        <div class="row text-center mt-4">
          <div class="col-md-4">
            <div class="border rounded p-2 bg-light">
              <div class="small text-muted">Gross Pay</div>
              <div class="fw-bold">₹${slip.grossPay?.toLocaleString() || 0}</div>
            </div>
          </div>
          <div class="col-md-4">
            <div class="border rounded p-2 bg-light">
              <div class="small text-muted">Total Deductions</div>
              <div class="fw-bold text-danger">₹${slip.totalDeductions?.toLocaleString() || 0}</div>
            </div>
          </div>
          <div class="col-md-4">
            <div class="border rounded p-2 bg-success bg-opacity-10">
              <div class="small text-muted">Net Pay</div>
              <div class="fw-bold text-success">₹${slip.netPay?.toLocaleString() || 0}</div>
            </div>
          </div>
        </div>
      `;

    const footerHtml = slip.pdfUrl
      ? `<div class="text-center mt-4">
            <a href="${slip.pdfUrl}" class="btn btn-outline-success" target="_blank">
              <i class="bi bi-file-earmark-arrow-down"></i> Download PDF
            </a>
          </div>`
      : `<div class="text-center mt-3 text-muted"><small>PDF not available</small></div>`;

    $("#slipPreviewContent").html(`
        ${headerHtml}
        ${summaryHtml}
        ${breakdownHtml}
        ${totalsHtml}
        ${footerHtml}
      `);
  },
    200
  );
}

function downloadSlipPdf(id) {
  window.location.href = `/api/payroll/salary-slips/${id}/pdf`;
}

function showAlert(type, message) {
  const alertHtml = `
        <div class="alert alert-${type} alert-dismissible fade show position-fixed top-0 start-50 translate-middle-x mt-3"
             role="alert" style="z-index:9999; min-width:400px;">
            <i class="bi bi-${getAlertIcon(type)}"></i> ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>`;
  $(".alert.position-fixed").remove();
  $("body").append(alertHtml);
  setTimeout(
    () => $(".alert.position-fixed").fadeOut(() => $(this).remove()),
    4000,
  );
}

function getAlertIcon(type) {
  const icons = {
    success: "check-circle-fill",
    danger: "exclamation-triangle-fill",
    warning: "exclamation-circle-fill",
    info: "info-circle-fill",
  };
  return icons[type] || "info-circle-fill";
}
