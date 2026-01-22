$(document).ready(function () {
  const ctx = {
    ORG_ID: window.APP?.ORG_ID || 1,
    API_BASE: `${window.APP?.CONTEXT_PATH || ""}/api/payroll`,
  };

  // ===========================
  //  🏛️ STATUTORY COMPONENTS
  // ===========================

  function loadStatutory() {
    $.getJSON(`${ctx.API_BASE}/statutory/components/organisation/${ctx.ORG_ID}`, function (data) {
      const tbody = $("#statutoryTable tbody").empty();
      data.forEach((c) => {
        tbody.append(`
          <tr>
            <td>${c.name}</td>
            <td>${c.code}</td>
            <td>${c.countryCode || ""}</td>
            <td>${c.stateCode || ""}</td>
            <td>${c.isActive ? "Yes" : "No"}</td>
            <td>
              <button class="btn btn-sm btn-primary editStatutory" data-id="${c.id}">
                <i class="fa fa-edit"></i>
              </button>
            </td>
          </tr>
        `);
      });
    });
  }

  function addStatutoryRuleRow(rule = {}) {
    $("#statutoryRuleTable tbody").append(`
      <tr data-rule-id="${rule.id || ""}">
        <td><input type="date" class="form-control ruleEffectiveFrom" value="${rule.effectiveFrom || ""}"></td>
        <td><input type="date" class="form-control ruleEffectiveTo" value="${rule.effectiveTo || ""}"></td>
        <td><input type="number" class="form-control ruleEmployeePercent" value="${rule.employeeContributionPercent || ""}"></td>
        <td><input type="number" class="form-control ruleEmployerPercent" value="${rule.employerContributionPercent || ""}"></td>
        <td><input type="number" class="form-control ruleMinSalary" value="${rule.minApplicableSalary || ""}"></td>
        <td><input type="number" class="form-control ruleMaxSalary" value="${rule.maxApplicableSalary || ""}"></td>
        <td>
          <select class="form-select ruleDeductionCycle">
            <option value="MONTHLY" ${rule.deductionCycle === "MONTHLY" ? "selected" : ""}>MONTHLY</option>
            <option value="QUARTERLY" ${rule.deductionCycle === "QUARTERLY" ? "selected" : ""}>QUARTERLY</option>
            <option value="YEARLY" ${rule.deductionCycle === "YEARLY" ? "selected" : ""}>YEARLY</option>
          </select>
        </td>
        <td><button type="button" class="btn btn-sm btn-danger removeRow">✖</button></td>
      </tr>
    `);
  }

  $("#addStatutoryRule").click(() => addStatutoryRuleRow());
  $(document).on("click", ".removeRow", function () {
    $(this).closest("tr").remove();
  });

  $("#saveStatutory").click(function () {
    const id = $("#statutoryId").val();
    const dto = {
      name: $("#statutoryName").val(),
      code: $("#statutoryCode").val(),
      countryCode: $("#statutoryCountry").val(),
      stateCode: $("#statutoryState").val(),
      organisationId: ctx.ORG_ID,
      isActive: true,
    };

    const url = id
      ? `${ctx.API_BASE}/statutory/components/${id}`
      : `${ctx.API_BASE}/statutory/components`;
    const method = id ? "PUT" : "POST";

    $.ajax({
      url,
      method,
      contentType: "application/json",
      data: JSON.stringify(dto),
      success: function (component) {
         const componentId = component.id || id;
         const currentIds = $("#statutoryRuleTable tbody tr")
        .map((_, tr) => $(tr).data("rule-id"))
        .get()
        .filter(Boolean);

        const deleted = window.oldStatutoryRuleIds?.filter(
        (rid) => !currentIds.includes(rid)
      ) || [];

      deleted.forEach((rid) => {
        $.ajax({
          url: `${ctx.API_BASE}/statutory/rules/${rid}/deactivate`,
          method: "PATCH",
        });
      });

        // Save or update rules sequentially
        $("#statutoryRuleTable tbody tr").each(function () {
          const row = $(this);
          const ruleId = row.data("rule-id");
          const ruleDto = {
            effectiveFrom: row.find(".ruleEffectiveFrom").val(),
            effectiveTo: row.find(".ruleEffectiveTo").val(),
            employeeContributionPercent: parseFloat(row.find(".ruleEmployeePercent").val()) || 0,
            employerContributionPercent: parseFloat(row.find(".ruleEmployerPercent").val()) || 0,
            minApplicableSalary: parseFloat(row.find(".ruleMinSalary").val()) || 0,
            maxApplicableSalary: parseFloat(row.find(".ruleMaxSalary").val()) || 0,
            deductionCycle: row.find(".ruleDeductionCycle").val(),
          };
          if (ruleId) {
          // ✅ Update existing rule
          $.ajax({
            url: `${ctx.API_BASE}/statutory/rules/${ruleId}`,
            method: "PUT",
            contentType: "application/json",
            data: JSON.stringify(ruleDto),
          });
        } else {
          // ➕ Create new rule
          $.ajax({
            url: `${ctx.API_BASE}/statutory/components/${componentId}/rules`,
            method: "POST",
            contentType: "application/json",
            data: JSON.stringify(ruleDto),
          });
        }
        });

        $("#statutoryModal").modal("hide");
        loadStatutory();
        showToast("Statutory component saved successfully!");
      },
    });
  });

  $(document).on("click", ".editStatutory", function () {
    const id = $(this).data("id");
    $.getJSON(`${ctx.API_BASE}/statutory/components/id/${id}`, function (c) {
      $("#statutoryId").val(c.id);
      $("#statutoryName").val(c.name);
      $("#statutoryCode").val(c.code);
      $("#statutoryCountry").val(c.countryCode);
      $("#statutoryState").val(c.stateCode);
      $("#statutoryRuleTable tbody").empty();
      $.getJSON(`${ctx.API_BASE}/statutory/components/${c.id}/rules`, function (rules) {
        window.oldStatutoryRuleIds = rules.map((r) => r.id);
        rules.forEach((r) => addStatutoryRuleRow(r));
      });
      $("#statutoryModal").modal("show");
    });
  });

// ===========================
// 🔗 STATUTORY MAPPINGS
// ===========================
function loadStatutoryComponentSelect(selectedId = null) {
  $.getJSON(`${ctx.API_BASE}/statutory/components/organisation/${ctx.ORG_ID}`, function (data) {
    const select = $("#mappingStatutoryComponent").empty().append('<option value="">Select...</option>');
    data.forEach(c => {
      select.append(`<option value="${c.id}" ${selectedId == c.id ? "selected" : ""}>${c.name} (${c.code})</option>`);
    });
  });
}

function loadSalaryComponentSelect(selectedId = null) {
  $.getJSON(`${ctx.API_BASE}/salary-components/organisation/${ctx.ORG_ID}`, function (data) {
    const select = $("#mappingSalaryComponent").empty().append('<option value="">Select...</option>');
    data.forEach(c => {
      select.append(`<option value="${c.id}" ${selectedId == c.id ? "selected" : ""}>${c.name} (${c.abbreviation})</option>`);
    });
  });
}

function loadMappings() {
  $.getJSON(`${ctx.API_BASE}/statutory-mappings/organisation/${ctx.ORG_ID}`, function (data) {
    const tbody = $("#mappingTable tbody").empty();
    console.log("mapping data", data)
    data.forEach((m) => {
      tbody.append(`
        <tr data-id="${m.id}" data-stat-id="${m.statutoryComponentId}" data-sal-id="${m.salaryComponentId}">
          <td>${m.statutoryComponentName}</td>
          <td>${m.salaryComponentName}</td>
           <td>${m.employeePercent || 0}</td>
           <td>${m.employerPercent || 0}</td>
          <td>
            <button class="btn btn-sm btn-primary editMapping" data-id="${m.id}"><i class="fa fa-edit"></i></button>
            <button class="btn btn-sm btn-danger deleteMapping" data-id="${m.id}"><i class="fa fa-trash"></i></button>
          </td>
        </tr>
      `);
    });
  });
}

// 🧭 Frontend duplicate check
function checkDuplicateMapping(statId, salId, existingId = null) {
  let duplicate = false;
  $("#mappingTable tbody tr").each(function () {
    const tr = $(this);
    const currentStatId = tr.data("stat-id");
    const currentSalId = tr.data("sal-id");
    const currentRowId = tr.data("id");
    if (
      currentStatId == statId &&
      currentSalId == salId &&
      currentRowId != existingId
    ) {
      duplicate = true;
      return false; // break
    }
  });
  return duplicate;
}

// 📥 Create or Update Mapping
$("#saveMapping").click(function () {
  const id = $("#mappingId").val();
  const statId = $("#mappingStatutoryComponent").val();
  const salId = $("#mappingSalaryComponent").val();

  if (!statId || !salId) {
    showToast("Please select both Statutory and Salary components!", "warning");
    return;
  }

  // Prevent duplicate frontend entry
  if (checkDuplicateMapping(statId, salId, id)) {
    showToast("This mapping already exists!", "warning");
    return;
  }

  const dto = {
    statutoryComponentId: statId,
    salaryComponentId: salId,
    organisationId: ctx.ORG_ID,
    employeePercent: parseFloat($("#mappingEmployeePercent").val()) || 0,
    employerPercent: parseFloat($("#mappingEmployerPercent").val()) || 0,
    // countryCode: $("#mappingCountry").val(),
    // stateCode: $("#mappingState").val(),
    includeInCalculation: $("#mappingInclude").is(":checked"),
  };

  const url = id
    ? `${ctx.API_BASE}/statutory-mappings/${id}`
    : `${ctx.API_BASE}/statutory-mappings`;
  const method = id ? "PUT" : "POST";

  $.ajax({
    url,
    method,
    contentType: "application/json",
    data: JSON.stringify(dto),
    success: function () {
      $("#mappingModal").modal("hide");
      loadMappings();
      showToast(`Mapping ${id ? "updated" : "created"} successfully!`);
    },
  });
});

// 🖋 Edit Mapping
$(document).on("click", ".editMapping", function () {
  const id = $(this).data("id");
  $.getJSON(`${ctx.API_BASE}/statutory-mappings/${id}`, function (m) {
    $("#mappingId").val(m.id);
    $("#mappingEmployeePercent").val(m.employeePercent);
    $("#mappingEmployerPercent").val(m.employerPercent);
    // $("#mappingCountry").val(m.countryCode);
    // $("#mappingState").val(m.stateCode);
    $("#mappingInclude").prop("checked", m.includeInCalculation);

    // Load dropdowns with current selections
    loadStatutoryComponentSelect(m.statutoryComponentId);
    loadSalaryComponentSelect(m.salaryComponentId);

    $("#mappingModal").modal("show");
  });
});

// ❌ Delete Mapping
$(document).on("click", ".deleteMapping", function () {
  const id = $(this).data("id");
  $.ajax({
    url: `${ctx.API_BASE}/statutory-mappings/${id}/deactivate`,
    method: "PATCH",
    success: function () {
      loadMappings();
      showToast("Mapping removed!");
    },
  });
});

// When modal opens (for create)
$("#mappingModal").on("show.bs.modal", function () {
  if (!$("#mappingId").val()) {
    loadStatutoryComponentSelect();
    loadSalaryComponentSelect();
  }
});

  // ===========================
  //  💰 INCOME TAX SLABS
  // ===========================

  function loadTax() {
    $.getJSON(`${ctx.API_BASE}/tax/slabs/organisation/${ctx.ORG_ID}`, function (data) {
      const tbody = $("#taxTable tbody").empty();
      data.forEach((t) => {
        tbody.append(`
          <tr>
            <td>${t.name}</td>
            <td>${t.effectiveFrom || ""}</td>
            <td>${t.effectiveTo || ""}</td>
            <td>${t.allowTaxExemption ? "Yes" : "No"}</td>
            <td>
              <button class="btn btn-sm btn-primary editTax" data-id="${t.id}"><i class="fa fa-edit"></i></button>
            </td>
          </tr>
        `);
      });
    });
  }

  function addTaxRuleRow(rule = {}) {
    $("#taxRuleTable tbody").append(`
      <tr data-rule-id="${rule.id || ""}" >
        <td><input type="number" class="form-control ruleMinIncome" value="${rule.minIncome || ""}"></td>
        <td><input type="number" class="form-control ruleMaxIncome" value="${rule.maxIncome || ""}"></td>
        <td><input type="number" class="form-control ruleDeductionPercent" value="${rule.deductionPercent || ""}"></td>
        <td><input type="text" class="form-control ruleCondition" value="${rule.condition || ""}"></td>
        <td><button type="button" class="btn btn-sm btn-danger removeRow">✖</button></td>
      </tr>
    `);
  }

  $("#addTaxRule").click(() => addTaxRuleRow());

  $("#saveTax").click(function () {
    const id = $("#taxId").val();
    const dto = {
      name: $("#taxName").val(),
      effectiveFrom: $("#taxFrom").val(),
      effectiveTo: $("#taxTo").val(),
      allowTaxExemption: $("#taxExemption").is(":checked"),
      organisationId: ctx.ORG_ID,
      countryCode: "IN",
    };

    const url = id ? `${ctx.API_BASE}/tax/slabs/${id}` : `${ctx.API_BASE}/tax/slabs`;
    const method = id ? "PUT" : "POST";

    $.ajax({
      url,
      method,
      contentType: "application/json",
      data: JSON.stringify(dto),
      success: function (slab) {

        const slabId = slab.id || id;

        // 🧠 STEP 2: Detect deleted rules (optional enhancement)
      const currentIds = $("#taxRuleTable tbody tr")
        .map((_, tr) => $(tr).data("rule-id"))
        .get()
        .filter(Boolean); // filter out empty ones

      const deleted = (window.existingTaxRuleIds || []).filter(
        (id) => !currentIds.includes(id)
      );

      deleted.forEach((id) => {
        $.ajax({
          url: `${ctx.API_BASE}/tax/rules/${id}`,
          method: "DELETE",
        });
      });

        $("#taxRuleTable tbody tr").each(function () {
          const row = $(this);
          const ruleId = row.data("rule-id");
          const ruleDto = {
            minIncome: parseFloat(row.find(".ruleMinIncome").val()) || 0,
            maxIncome: parseFloat(row.find(".ruleMaxIncome").val()) || 0,
            deductionPercent: parseFloat(row.find(".ruleDeductionPercent").val()) || 0,
            condition: row.find(".ruleCondition").val(),
          };
          if (ruleId) {
          // ✅ Update existing rule
          $.ajax({
            url: `${ctx.API_BASE}/tax/rules/${ruleId}`,
            method: "PUT",
            contentType: "application/json",
            data: JSON.stringify(ruleDto),
          });
        } else {
          // ➕ Create new rule
          $.ajax({
            url: `${ctx.API_BASE}/tax/slabs/${slabId}/rules`,
            method: "POST",
            contentType: "application/json",
            data: JSON.stringify(ruleDto),
          });
        }
        });

        $("#taxModal").modal("hide");
        loadTax();
        showToast("Tax slab saved successfully!");
      },
    });
  });

  $(document).on("click", ".editTax", function () {
    const id = $(this).data("id");
    $.getJSON(`${ctx.API_BASE}/tax/slabs/${id}`, function (t) {
      $("#taxId").val(t.id);
      $("#taxName").val(t.name);
      $("#taxFrom").val(t.effectiveFrom);
      $("#taxTo").val(t.effectiveTo);
      $("#taxExemption").prop("checked", t.allowTaxExemption);
      $("#taxRuleTable tbody").empty();
      $.getJSON(`${ctx.API_BASE}/tax/slabs/${t.id}/rules`, function (rules) {

       window.existingTaxRuleIds = rules.map((r) => r.id); // store globally
       rules.forEach((r) => addTaxRuleRow(r));
      });
      $("#taxModal").modal("show");
    });
  });


  // ===========================
  //  🚀 INITIAL LOAD
  // ===========================
  loadStatutory();
  loadMappings();
  loadTax();
});
