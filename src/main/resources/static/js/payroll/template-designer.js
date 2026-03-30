// @ts-nocheck
const ORG_ID = $("#globalOrgId").val() || window.APP.ORG_ID;

let availableFields = {};
let selectedFields = {
  organisation: [],
  employee: [],
  bank: [],
  payRun: [],
  earnings: [],
  deductions: [],
  summary: [],
};

$(document).ready(function () {
  initializeTemplateDesigner();
});


function initializeTemplateDesigner() {
  // Load available fields first
  loadAvailableFields().then(() => {
    const templateId = $("#templateId").val();
    if (templateId) {
      loadExistingTemplate(templateId);
    }
  });

  setupEventListeners();
}


/**
 * Setup event listeners
 */
function setupEventListeners() {
  // Logo file input change
  $("#logoFile").on("change", function () {
    const file = this.files[0];
    if (file) {
      // Show preview
      const reader = new FileReader();
      reader.onload = function (e) {
        $("#logoPreviewImage").attr("src", e.target.result);
        $("#logoPreview").show();
      };
      reader.readAsDataURL(file);
    }
  });

  // Template name input
  $("#templateName").on("input", function () {
    // Auto-save indicator could go here
  });
}

function loadAvailableFields() {
  return $.ajax({
    url: `/api/salary-slip-template/${ORG_ID}/available-fields`,
    type: "GET",
  })
    .done(function (response) {
      if (response.success) {
        availableFields = response.data;
        console.log("Available fields:", availableFields);
        renderFieldSections();
      } else {
        showAlert("danger", response.message);
      }
    })
    .fail(function (xhr) {
      showAlert("danger", "Failed to load available fields. Please refresh the page.");
      console.error("Error loading fields:", xhr);
    });
}

/**
 * Render field selection sections
 */
function renderFieldSections() {
  const container = $("#fieldSections");
  container.empty();

  if (!availableFields || Object.keys(availableFields).length === 0) {
    container.html(`
            <div class="alert alert-warning text-center">
                No available fields found. Please check your backend response.
            </div>
        `);
    return;
  }

  const sectionTitles = {
    organisation: "Organisation Details",
    employee: "Employee Information",
    bank: "Bank Details",
    payRun: "Pay Period Information",
    earnings: "Earnings Components",
    deductions: "Deduction Components",
    summary: "Salary Summary",
  };

  Object.entries(availableFields).forEach(([section, fields]) => {
    if (!Array.isArray(fields) || fields.length === 0) return;

    const sectionId = `section-${section}`;
    const sectionHtml = `
            <div class="card mb-3 shadow-sm">
                <div class="card-header bg-light fw-bold">
                    <i class="bi bi-${getSectionIcon(section)} me-2"></i>
                    ${sectionTitles[section] || section}
                </div>
                <div class="card-body d-flex flex-wrap gap-3" id="${sectionId}">
                    ${fields
        .map(
          (field) => `
                        <div class="form-check" style="min-width: 220px;">
                            <input class="form-check-input"
                                type="checkbox"
                                id="field-${section}-${field.key}"
                                value="${field.key}"
                                data-section="${section}"
                                onchange="updateSelectedFields()">
                            <label class="form-check-label" for="field-${section}-${field.key}">
                                ${escapeHtml(field.label || field.key)}
                            </label>
                        </div>
                    `,
        )
        .join("")}
                </div>
            </div>
        `;

    container.append(sectionHtml);
  });

  // Pre-select default summary fields
  ["basePay", "totalEarnings", "totalDeductions", "netPay"].forEach(function (key) {
    $("#field-summary-" + key).prop("checked", true);
  });

  // Sync JS state with the pre-checked boxes
  updateSelectedFields();

  // Smoothly reveal once loaded
  container.hide().fadeIn(300);
}

/**
 * Get icon for section
 */
function getSectionIcon(section) {
  const icons = {
    organisation: "building",
    employee: "person-badge",
    bank: "bank",
    payRun: "calendar3",
    earnings: "arrow-up-circle-fill",
    deductions: "arrow-down-circle-fill",
    summary: "calculator-fill",
  };
  return icons[section] || "dot";
}

/**
 * Update selected fields when checkboxes change
 */
function updateSelectedFields() {
  Object.keys(selectedFields).forEach((section) => {
    selectedFields[section] = [];
    $(`input[data-section="${section}"]:checked`).each(function () {
      selectedFields[section].push($(this).val());
    });
  });
}

/**
 * Generate preview with current selections
 */
function generatePreview() {
  updateSelectedFields();

  // Validate at least some fields are selected
  const totalSelected = Object.values(selectedFields).reduce(
    (sum, arr) => sum + arr.length,
    0,
  );
  if (totalSelected === 0) {
    showToast("warning", "Please select at least one field to preview");
    return;
  }

  const templateHtml = buildTemplateHtml();
  const configJson = JSON.stringify({
    sections: Object.keys(selectedFields)
      .map((section) => ({
        name: section,
        fields: selectedFields[section],
      }))
      .filter((s) => s.fields.length > 0),
  });

  // Show loading in preview
  $("#previewContainer").html(`
        <div class="text-center py-5">
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Generating preview...</span>
            </div>
            <p class="text-muted mt-3">Generating preview...</p>
        </div>
    `);

  $.ajax({
    url: `/api/salary-slip-template/${ORG_ID}/preview`,
    type: "POST",
    contentType: "application/json",
    data: JSON.stringify({
      templateHtml: templateHtml,
      configJson: configJson,
    }),
    success: function (response) {
      if (response.success) {
        $("#previewContainer").html(response.data);
      } else {
        $("#previewContainer").html(`
                    <div class="alert alert-danger m-4">
                        <i class="bi bi-exclamation-triangle"></i> ${response.message}
                    </div>
                `);
      }
    },
    error: function (xhr) {
      $("#previewContainer").html(`
                <div class="alert alert-danger m-4">
                    <i class="bi bi-exclamation-triangle"></i> Failed to generate preview. Please try again.
                </div>
            `);
      console.error("Preview error:", xhr);
    },
  });
}

/**
 * Build template HTML from selected fields (Dynamic FreeMarker version)
 */
function buildTemplateHtml() {
  const curr = "₹";

  let html = `
  <div style="font-family: Arial, sans-serif; max-width: 850px; margin: 20px auto; border: 1px solid #ccc; background: #fff;">
  `;

  // ================= HEADER =================
  if (selectedFields.organisation.length > 0) {
    html += `
    <table style="width:100%; border-bottom:2px solid #444; padding:20px;">
      <tr>
        <td style="text-align:left;">
          ${selectedFields.organisation.includes("logoUrl")
        ? '<img src="${organisation.logoUrl!""}" style="max-height:60px;" />'
        : ""}
          ${selectedFields.organisation.includes("name")
        ? '<div style="font-size:20px; font-weight:bold;">${organisation.name!""}</div>'
        : ""}
        </td>
        <td style="text-align:right; font-size:12px; color:#555;">
          ${selectedFields.organisation.includes("address")
        ? '<div>${organisation.address!""}</div>'
        : ""}
          <div>
            ${selectedFields.organisation.includes("email")
        ? 'Email: ${organisation.email!""}'
        : ""}
            ${selectedFields.organisation.includes("phone")
        ? '<br/>Ph: ${organisation.phone!""}'
        : ""}
          </div>
        </td>
      </tr>
    </table>
    `;
  }

  // ================= TITLE =================
  html += `
  <div style="text-align:center; padding:10px; border-bottom:1px solid #ddd;">
    <div style="font-size:16px; font-weight:bold;">PAYSLIP</div>
    ${selectedFields.payRun.includes("payPeriod")
      ? '<div style="font-size:12px; color:#666;">Period: ${payRun.payPeriod!""}</div>'
      : ""
    }
  </div>
  `;

  // ================= EMPLOYEE + BANK =================
  html += `<table style="width:100%; padding:15px; border-bottom:1px solid #eee;"><tr>`;

  // Employee
  html += `<td style="width:50%; vertical-align:top;">
    <table style="width:100%; font-size:12px;">`;

  selectedFields.employee.forEach(field => {
    html += `
      <tr>
        <td style="color:#777; padding:3px 0;">${getLabelForField("employee", field)}</td>
        <td style="font-weight:bold;">: \${employee.${field}!""}</td>
      </tr>`;
  });

  html += `</table></td>`;

  // Bank
  if (selectedFields.bank.length > 0) {
    html += `<td style="width:50%; vertical-align:top;">
      <table style="width:100%; font-size:12px;">`;

    selectedFields.bank.forEach(field => {
      html += `
        <tr>
          <td style="color:#777; padding:3px 0;">${getLabelForField("bank", field)}</td>
          <td style="font-weight:bold;">: \${bank.${field}!""}</td>
        </tr>`;
    });

    html += `</table></td>`;
  }

  html += `</tr></table>`;

  // ================= EARNINGS / DEDUCTIONS =================
  if (selectedFields.earnings.length > 0 || selectedFields.deductions.length > 0) {
    html += `
    <div style="padding:15px;">
      <table style="width:100%; border-collapse:collapse; border:1px solid #ddd;">
        <thead>
          <tr style="background:#f5f5f5;">
            <th style="padding:8px; text-align:left;">EARNINGS</th>
            <th style="padding:8px; text-align:right;">AMOUNT</th>
            <th style="padding:8px; text-align:left;">DEDUCTIONS</th>
            <th style="padding:8px; text-align:right;">AMOUNT</th>
          </tr>
        </thead>
        <tbody>
          <#assign maxRows = [(earnings?size)!0, (deductions?size)!0]?max>
          <#list 0..(maxRows - 1) as i>
          <tr>
            <td style="padding:6px; border-top:1px solid #eee;">
              <#if earnings[i]??>\${earnings[i].name}</#if>
            </td>
            <td style="padding:6px; text-align:right; border-top:1px solid #eee;">
              <#if earnings[i]??>${curr}\${earnings[i].amount?string["0.00"]}</#if>
            </td>
            <td style="padding:6px; border-top:1px solid #eee;">
              <#if deductions[i]??>\${deductions[i].name}</#if>
            </td>
            <td style="padding:6px; text-align:right; border-top:1px solid #eee;">
              <#if deductions[i]??>${curr}\${deductions[i].amount?string["0.00"]}</#if>
            </td>
          </tr>
          </#list>
        </tbody>
      </table>
    </div>
    `;
  }

  // ================= SUMMARY =================
  const sumFields = selectedFields.summary;

  if (sumFields.length > 0) {

    html += `<table style="width:100%; padding:10px 15px;">`;

    if (sumFields.includes("basePay")) {
      html += `
      <tr>
        <td>Base Pay</td>
        <td style="text-align:right;">\${summary.basePay!""}</td>
      </tr>`;
    }

    if (sumFields.includes("totalEarnings")) {
      html += `
      <tr>
        <td>Total Earnings</td>
        <td style="text-align:right; color:green;">\${summary.totalEarnings!""}</td>
      </tr>`;
    }

    if (sumFields.includes("totalDeductions")) {
      html += `
      <tr>
        <td>Total Deductions</td>
        <td style="text-align:right; color:red;">\${summary.totalDeductions!""}</td>
      </tr>`;
    }

    html += `</table>`;

    // Net Pay Box
    if (sumFields.includes("netPay")) {
      html += `
      <table style="width:100%; margin:10px 0; background:#28a745; color:#fff;">
        <tr>
          <td style="padding:10px;">
            <div style="font-size:12px;">NET PAY</div>
            ${sumFields.includes("netPayInWords")
          ? '<div style="font-size:11px;">\${summary.netPayInWords!""}</div>'
          : ""
        }
          </td>
          <td style="text-align:right; padding:10px; font-size:18px; font-weight:bold;">
            \${summary.netPay!""}
          </td>
        </tr>
      </table>
      `;
    }
  }

  // ================= FOOTER =================
  html += `
  <div style="text-align:center; font-size:10px; color:#777; padding:10px; border-top:1px solid #eee;">
    This is a system-generated document and does not require a signature.
  </div>
  </div>
  `;

  return html;
}
/**
 * Get label for a field
 */
function getLabelForField(section, fieldKey) {
  const fields = availableFields[section];
  if (!fields) return fieldKey;

  const field = fields.find((f) => f.key === fieldKey);
  return field ? field.label : fieldKey;
}

/**
 * Upload logo file
 */
function uploadLogo() {
  const fileInput = $("#logoFile")[0];
  if (!fileInput.files || !fileInput.files[0]) {
    showToast("warning", "Please select a logo file first");
    return;
  }

  const formData = new FormData();
  formData.append("file", fileInput.files[0]);

  // Show uploading state
  showToast("info", "Uploading logo...");

  $.ajax({
    url: `/api/salary-slip-template/${ORG_ID}/upload-logo`,
    type: "POST",
    data: formData,
    processData: false,
    contentType: false,
    success: function (response) {
      if (response.success) {
        $("#logoUrl").val(response.data);
        showToast("success", "Logo uploaded successfully");
      } else {
        showToast("danger", response.message);
      }
    },
    error: function (xhr) {
      showToast("danger", "Failed to upload logo. Please try again.");
      console.error("Upload error:", xhr);
    },
  });
}

/**
 * Save template
 */
function saveTemplate() {
  const templateName = $("#templateName").val().trim();
  if (!templateName) {
    showToast("warning", "Please enter a template name");
    $("#templateName").focus();
    return;
  }

  updateSelectedFields();

  // Validate at least some fields are selected
  const totalSelected = Object.values(selectedFields).reduce(
    (sum, arr) => sum + arr.length,
    0,
  );
  if (totalSelected === 0) {
    showToast("warning", "Please select at least one field before saving");
    return;
  }

  const templateHtml = buildTemplateHtml();
  const configJson = JSON.stringify({
    sections: Object.keys(selectedFields)
      .map((section) => ({
        name: section,
        fields: selectedFields[section],
      }))
      .filter((s) => s.fields.length > 0),
  });

  const data = {
    id: $("#templateId").val() || null,
    templateName: templateName,
    templateHtml: templateHtml,
    configJson: configJson,
    isDefault: $("#isDefault").is(":checked"),
    logoUrl: $("#logoUrl").val() || null,
  };

  $.ajax({
    url: `/api/salary-slip-template/${ORG_ID}/save`,
    type: "POST",
    contentType: "application/json",
    data: JSON.stringify(data),
    success: function (response) {
      if (response.success) {
        console.log("template saved successfully", response);
        showToast("success", response.message);
        $("#templateId").val(response.data.id);

        // Update URL if it was a new template
        if (!data.id) {
          window.history.pushState(
            {},
            "",
            "/salary-slip-template/edit/" + response.data.id,
          );
        }
      } else {
        console.log("template save failed", response);
        showToast("warning", response.message);
      }
    },
    error: function (xhr) {
      showToast("error", "Failed to save template. Please try again.");
      console.error("Save error:", xhr);
    },
  });
}

/**
 * Load templates list modal
 */
function loadTemplates() {
  $("#templateListModal").modal("show");
  $("#templateListLoader").show();
  $("#templateListContent").hide();

  $.ajax({
    url: `/api/salary-slip-template/${ORG_ID}/list`,
    type: "GET",
    success: function (response) {
      if (response.success) {
        renderTemplateList(response.data);
      } else {
        showToast("danger", response.message);
      }
      $("#templateListLoader").hide();
      $("#templateListContent").show();
    },
    error: function (xhr) {
      $("#templateListLoader").hide();
      showToast("danger", "Failed to load templates");
      console.error("Load templates error:", xhr);
    },
  });
}

/**
 * Render template list in modal
 */
function renderTemplateList(templates) {
  const tbody = $("#templateListBody");
  tbody.empty();

  if (!templates || templates.length === 0) {
    tbody.append(`
            <tr>
                <td colspan="4" class="text-center py-4 text-muted">
                    <i class="bi bi-inbox" style="font-size: 2rem; display: block; margin-bottom: 10px;"></i>
                    No templates found
                </td>
            </tr>
        `);
    return;
  }

  templates.forEach((template) => {
    console.log("template", template);
    const row = `
            <tr>
                <td>
                    <strong>${escapeHtml(template.templateName)}</strong>
                </td>
                <td>${formatDate(template.createdDate)}</td>
                <td class="text-center">
                    ${template.isDefault
        ? '<span class="badge bg-success"><i class="bi bi-star-fill"></i> Default</span>'
        : '<span class="badge bg-secondary">Active</span>'
      }
                </td>
                <td class="text-center">
                    <div class="btn-group btn-group-sm" role="group">
                        <button class="btn btn-outline-primary" onclick="editTemplate(${template.id})" title="Edit">
                            <i class="fa fa-pencil"></i>
                        </button>
                        ${!template.isDefault
        ? `
                            <button class="btn btn-outline-success" onclick="setTemplateAsDefault(${template.id})" title="Set as Default">
                                <i class="fa fa-star"></i>
                            </button>
                        `
        : ""
      }
                        <button class="btn btn-outline-danger" onclick="confirmDeleteTemplate(${template.id}, '${escapeHtml(template.templateName)}')" title="Delete">
                            <i class="fa fa-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `;
    tbody.append(row);
  });
}

/**
 * Edit template
 */
function editTemplate(id) {
  window.location.href = `/salary-slip-template/edit/` + id;
}

/**
 * Set template as default
 */
function setTemplateAsDefault(id) {
  $.ajax({
    url: `/api/salary-slip-template/${ORG_ID}/template/${id}/set-default`,
    type: "PUT",
    success: function (response) {
      if (response.success) {
        showToast("success", "Template set as default");
        loadTemplates(); // Refresh list
      } else {
        showToast("danger", response.message);
      }
    },
    error: function (xhr) {
      showToast("danger", "Failed to set template as default");
      console.error("Set default error:", xhr);
    },
  });
}

/**
 * Confirm template deletion
 */
function confirmDeleteTemplate(id, name) {
  if (
    confirm(
      `Are you sure you want to delete template "${name}"? This action cannot be undone.`,
    )
  ) {
    deleteTemplate(id);
  }
}

/**
 * Delete template
 */
function deleteTemplate(id) {
  $.ajax({
    url: `/api/salary-slip-template/${ORG_ID}/template/${id}`,
    type: "DELETE",
    success: function (response) {
      if (response.success) {
        showToast("success", "Template deleted successfully");
        loadTemplates(); // Refresh list
      } else {
        showToast("error", response.message);
      }
    },
    error: function (xhr) {
      showToast("danger", "Failed to delete template");
      console.error("Delete error:", xhr);
    },
  });
}

/**
 * Load existing template for editing
 */
function loadExistingTemplate(id) {
  $.ajax({
    url: `/api/salary-slip-template/${ORG_ID}/template/${id}`,
    type: "GET",
    success: function (response) {
      if (response.success && response.data) {
        const template = response.data;

        // Populate form fields
        $("#templateName").val(template.templateName);
        $("#isDefault").prop("checked", template.isDefault);

        if (template.logoUrl) {
          $("#logoUrl").val(template.logoUrl);
          $("#logoPreviewImage").attr("src", template.logoUrl);
          $("#logoPreview").show();
        }

        // Parse config and select fields
        try {
          const config = JSON.parse(template.configJson);
          config.sections.forEach((section) => {
            section.fields.forEach((field) => {
              $(`#field-${section.name}-${field}`).prop("checked", true);
            });
          });

          updateSelectedFields();

          // Auto-generate preview
          setTimeout(() => generatePreview(), 500);
        } catch (e) {
          console.error("Error parsing template config:", e);
          showToast(
            "warning",
            "Template loaded but configuration may be invalid",
          );
        }
      }
    },
    error: function (xhr) {
      showToast("danger", "Failed to load template");
      console.error("Load template error:", xhr);
    },
  });
}

/**
 * Reset template form
 */
function resetTemplate() {
  if (
    !confirm(
      "Are you sure you want to reset all selections? This will clear all your current work.",
    )
  ) {
    return;
  }

  $("#templateName").val("");
  $("#isDefault").prop("checked", false);
  $("#logoUrl").val("");
  $("#logoFile").val("");
  $("#logoPreview").hide();
  $('input[type="checkbox"][data-section]').prop("checked", false);

  $("#previewContainer").html(`
        <div class="text-center text-muted py-5">
            <i class="bi bi-file-earmark-text" style="font-size: 4rem; opacity: 0.3;"></i>
            <p class="mt-3">Select fields and click "Preview Template" to see your design</p>
            <small class="text-muted">Choose at least organisation and employee fields to get started</small>
        </div>
    `);

  updateSelectedFields();
  showAlert("info", "Template reset successfully");
}

/**
 * Print preview
 */
function printPreview() {
  const previewContent = document.getElementById("previewContainer").innerHTML;
  const printWindow = window.open("", "_blank", "width=900,height=1000");
  printWindow.document.write(`
    <html>
      <head>
        <title>Salary Slip Preview</title>
        <style>
          body {
            font-family: Arial, sans-serif;
            margin: 20px;
            color: #333;
          }
          .salary-slip {
            max-width: 800px;
            margin: 0 auto;
            border: 1px solid #ddd;
            padding: 20px;
          }
          table { width: 100%; border-collapse: collapse; }
          th, td { padding: 8px; border: 1px solid #dee2e6; }
          h2, h3, h5 { margin: 5px 0; }
          .text-center { text-align: center; }
          @media print {
            body { margin: 0; }
            .salary-slip { border: none; }
          }
        </style>
      </head>
      <body>
        ${previewContent}
      </body>
    </html>
  `);
  printWindow.document.close();
  printWindow.focus();
  printWindow.print();
  printWindow.close();
}


/**
 * Export preview to PDF (mock - needs actual implementation)
 */
function exportToPDF() {
  const previewElement = document.getElementById("previewContainer");

  if (!previewElement || previewElement.innerText.trim() === "") {
    showAlert("warning", "Nothing to export. Please generate a preview first.");
    return;
  }

  const opt = {
    margin: 0.5,
    filename: 'Salary_Slip_Template.pdf',
    image: { type: 'jpeg', quality: 0.98 },
    html2canvas: { scale: 2, useCORS: true },
    jsPDF: { unit: 'in', format: 'a4', orientation: 'portrait' }
  };

  showAlert("info", "Generating PDF...");

  html2pdf()
    .from(previewElement)
    .set(opt)
    .save()
    .then(() => showAlert("success", "PDF downloaded successfully"))
    .catch((err) => {
      console.error("PDF export error:", err);
      showAlert("danger", "Failed to generate PDF. Please try again.");
    });
}

/**
 * Utility: Format date
 */
function formatDate(dateString) {
  if (!dateString) return "-";
  const date = new Date(dateString);
  return date.toLocaleDateString("en-IN", {
    year: "numeric",
    month: "short",
    day: "numeric",
  });
}

/**
 * Utility: Escape HTML
 */
function escapeHtml(text) {
  if (!text) return "";
  const map = {
    "&": "&amp;",
    "<": "&lt;",
    ">": "&gt;",
    '"': "&quot;",
    "'": "&#039;",
  };
  return text.replace(/[&<>"']/g, (m) => map[m]);
}

/**
 * Utility: Show alert message
 */
function showAlert(type, message) {
  const alertHtml = `
        <div class="alert alert-${type} alert-dismissible fade show position-fixed top-0 start-50 translate-middle-x mt-3" 
             role="alert" style="z-index: 9999; min-width: 400px;">
            <i class="bi bi-${getAlertIcon(type)}"></i> ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    `;

  // Remove existing alerts
  $(".alert.position-fixed").remove();

  // Add new alert
  $("body").append(alertHtml);

  // Auto dismiss after 5 seconds
  setTimeout(function () {
    $(".alert.position-fixed").fadeOut(function () {
      $(this).remove();
    });
  }, 5000);
}

/**
 * Utility: Get alert icon
 */
function getAlertIcon(type) {
  const icons = {
    success: "check-circle-fill",
    danger: "exclamation-triangle-fill",
    warning: "exclamation-circle-fill",
    info: "info-circle-fill",
  };
  return icons[type] || "info-circle-fill";
}

