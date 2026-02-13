$(document).ready(function () {
    const orgId = $("#globalOrgId").val();
    const userId = $("#globalUserId").val();


    let page = 0,
        size = 10,
        sort = "creditedAt,desc";


    // ✅ Initial load
    loadSalarySlips();

    $("#pageSizeSelect").change(function () {
        size = parseInt($(this).val());
        page = 0;
        loadSalarySlips();
    });


    // 🔄 Refresh button
    $("#btnRefresh").click(() => loadSalarySlips());

    // 🧾 Filter form submit
    $("#filterForm").on("submit", function (e) {
        e.preventDefault();
        page = 0;
        loadSalarySlips();
    });

    // ───────────────────────────────────────────────
    // 🧩 Update Selected Count & Toolbar
    // ───────────────────────────────────────────────
    function updateSelectedCount() {
        const count = $(".slip-checkbox:checked").length;
        $("#selectedCount").text(count);
    }


    $("#btnExportExcel").click(async function () {
        try {
            const filters = { /* get your filters as you already do */ };
            const res = await axiosClient.get(`/api/accountant/${orgId}/slips`, filters);

            const flattenedData = res.content.map(slip => {
                const row = {
                    // 1. Employee Identity & Compliance
                    "Employee Code": slip.employeeCode || "N/A",
                    "Employee Name": slip.employeeName,
                    "PAN Number": slip.panNumber || "N/A",
                    "UAN Number": slip.uanNumber || "N/A",
                    "Department": slip.departmentName,
                    "Designation": slip.designationName,

                    // 2. Bank Details (Necessary for Bank Excel)
                    "Bank Name": slip.bankName,
                    "Account Number": slip.bankAccountNumber,
                    "IFSC Code": slip.bankIfscCode,

                    // 3. Attendance & Targets
                    "Month-Year": `${slip.payMonth}-${slip.payYear}`,
                    "Paid Days": slip.paymentDays,
                    "Gross Target": slip.monthlyGrossTarget,
                    "Net Target": slip.monthlyNetTarget,

                    // 4. Actual Earnings
                    "Actual Gross Earned": slip.actualGross,
                };

                // 5. Dynamic Statutory Deductions (Employee Share)
                if (slip.components) {
                    slip.components.forEach(comp => {
                        if (comp.isStatutory) {
                            row[comp.componentName + " (Emp)"] = comp.amount;
                        }
                    });
                }

                // 6. Total Deductions & Final Payout
                row["Total Deductions"] = slip.totalDeductions;
                row["Net Pay Paid"] = slip.netPay;

                // 7. Organization Contributions (Employer Share)
                row["Employer Contribution (Total)"] = slip.statutoryContributionOrg || 0;

                return row;
            });

            exportToExcel(flattenedData, `Payroll_Export_${new Date().getTime()}.csv`);
        } catch (err) {
            showToast("error", "Export failed: " + err.message);
        }
    });

    $("#btnExportPDF").click(async function () {
        try {
            const filters = {
                month: $("#filterMonth").val(),
                year: $("#filterYear").val(),
                credited: $("#filterCredited").val(),
                departmentId: $("#filterDepartment").val(),
                search: $("#filterSearch").val(),
            };

            const res = await axiosClient.get(`/api/accountant/${orgId}/slips`, filters);
            exportToPDF(res.content, "SalarySlips.pdf");
        } catch (err) {
            showToast("error", err.message || "Failed to export PDF");
        }
    });


    // ───────────────────────────────────────────────
    // 🔹 Load salary slips (Axios)
    // ───────────────────────────────────────────────
    async function loadSalarySlips() {
        const filters = {
            month: $("#filterMonth").val(),
            year: $("#filterYear").val(),
            credited: $("#filterCredited").val(),
            departmentId: $("#filterDepartment").val(),
            search: $("#filterSearch").val(),
            page,
            size,
            sort,
        };

        try {
            const res = await axiosClient.get(`/api/accountant/${orgId}/slips`, filters);
            renderTable(res.content);
            renderPagination(res.meta);
            updateSelectedCount();
        } catch (err) {
            console.error("Error loading salary slips:", err.message);
            showToast("error", err.message || "Failed to load salary slips");
        }
    }

    // ───────────────────────────────────────────────
    // 🧾 Render Table
    // ───────────────────────────────────────────────
    function renderTable(data) {
        const tbody = $("#salarySlipBody");
        tbody.empty();

        if (!data || data.length === 0) {
            tbody.append(`<tr><td colspan="9" class="text-center text-muted py-4">No records found</td></tr>`);
            return;
        }

        data.forEach((s) => {

            console.log(s);
            let statutoryEmpList = "";
            let totalStatutoryDed = 0;

            // Loop through components to find dynamic statutory items
            if (s.components) {
                s.components.forEach(comp => {
                    if (comp.isStatutory) {
                        statutoryEmpList += `
                        <div class="d-flex justify-content-between small border-bottom mb-1">
                            <span class="text-muted">${comp.componentAbbreviation || comp.componentName}:</span>
                            <span class="fw-bold text-danger">₹${comp.amount.toFixed(2)}</span>
                        </div>`;
                        totalStatutoryDed += comp.amount;
                    }
                });
            }

            const otherDeductions = (s.totalDeductions || 0) - totalStatutoryDed;
            const statusBadge = s.isCredited
                ? `<span class="badge bg-success">Credited</span>`
                : `<span class="badge bg-warning text-dark">Pending</span>`;

            tbody.append(`
            <tr>
                <td><input type="checkbox" class="slip-checkbox" value="${s.id}"></td>
                <td>
                    <div class="fw-bold">${s.employeeName} <small class="text-muted">${s.employeeCode}</small></div>
                    <div class="small text-muted">${s.departmentName} | LOP : ${s.lopDays || 0}</div>
                    <div class="small border-top mt-1 text-primary">
                        ${s.bankName || 'N/A'} - ${s.bankAccountNumber || 'No Account'}
                    </div>
                </td>
                <td class="table-info">
                    <div class="small d-flex justify-content-between border-bottom mb-1"><span class="text-muted">Gross:</span> <span class="fw-bold">₹${s.monthlyGrossTarget || 0}</span></div>
                    <div class="small d-flex justify-content-between border-bottom mb-1"><span class="text-muted">Net:</span> <span class="fw-bold">₹${s.monthlyNetTarget || 0}</span></div>
                    <div class="small d-flex justify-content-between"><span class="text-muted">Actual Gross:</span> <span class="fw-bold">₹${s.actualGross || 0}</span></div>
                </td>
                <td class="table-warning">${statutoryEmpList || '<small class="text-muted">No statutory ded.</small>'}</td>
                <td class="table-info">
                    <small class="fw-bold">PF Org: ₹${s.statutoryContributionOrg || 0}</small>
                </td>
                <td>₹${otherDeductions.toFixed(2)}</td>
                <td class="table-primary fw-bold text-dark">₹${(s.netPay || 0).toLocaleString()}</td>
                <td>${statusBadge}</td>
                <td>
                    <div class="dropdown">
                        <button class="btn btn-sm btn-light border dropdown-toggle" type="button" data-bs-toggle="dropdown"></button>
                       <ul class="dropdown-menu">
    ${!s.isCredited ? `<li><a class="dropdown-item btnMarkCredited" data-id="${s.id}" href="javascript:void(0)">Mark Credited</a></li>` : ''}
    
    ${s.pdfUrl
                    ? `<li><a class="dropdown-item" href="${s.pdfUrl}" target="_blank"><i class="fa-solid fa-download text-success"></i> Download PDF</a></li>`
                    : `<li><a class="dropdown-item btnGeneratePDF" data-id="${s.id}" href="javascript:void(0)"><i class="fa-solid fa-file-pdf text-danger"></i> Generate PDF</a></li>`
                }
</ul>
                    </div>
                </td>
            </tr>
        `);
        });
    }


    // ───────────────────────────────────────────────
    // 🧩 Select All / Unselect All (Only Pending)
    // ───────────────────────────────────────────────
    $(document).on("change", "#selectAll", function () {
        const isChecked = $(this).is(":checked");
        $(".slip-checkbox").each(function () {
            const row = $(this).closest("tr");
            const isCredited = row.find(".badge").hasClass("bg-success");
            // Select only pending ones
            if (!isCredited) {
                $(this).prop("checked", isChecked);
            }
        });
        updateSelectedCount()
    });


    // ───────────────────────────────────────────────
    // 📄 Pagination Rendering
    // ───────────────────────────────────────────────
    function renderPagination(meta) {
        const pagination = $("#paginationControls");
        const info = $("#paginationInfo");
        pagination.empty();

        if (!meta) return;

        info.text(
            `Page ${meta.page + 1} of ${meta.totalPages} (${meta.totalElements} records)`
        );

        for (let i = 0; i < meta.totalPages; i++) {
            const active = i === meta.page ? "active" : "";
            pagination.append(
                `<li class="page-item ${active}"><a class="page-link" href="#">${i + 1}</a></li>`
            );
        }

        $(".page-item").click(function (e) {
            e.preventDefault();
            page = parseInt($(this).text()) - 1;
            loadSalarySlips();
        });
    }

    // ───────────────────────────────────────────────
    // ✅ Single Mark Credited (Axios)
    // ───────────────────────────────────────────────
    $(document).on("click", ".btnMarkCredited", async function () {
        const slipId = $(this).data("id");

        try {
            await axiosClient.post(
                `/api/accountant/${orgId}/mark-credited/${slipId}?accountantUserId=${userId}`
            );
            showToast("success", "Marked as credited");
            loadSalarySlips();
        } catch (err) {
            showToast("error", err.message || "Failed to mark slip as credited");
        }
    });

    // ───────────────────────────────────────────────
    // ✅ Bulk Mark Credited (Axios)
    // ───────────────────────────────────────────────
    $("#btnMarkBulkCredited").click(async function () {
        const ids = $(".slip-checkbox:checked")
            .map(function () {
                return $(this).val();
            })
            .get();

        if (ids.length === 0)
            return showToast("info", "Select at least one slip");

        try {
            await axiosClient.post(
                `/api/accountant/${orgId}/mark-credited/bulk?accountantUserId=${userId}`,
                ids
            );
            showToast("success", "Selected slips credited successfully");
            loadSalarySlips();
        } catch (err) {
            showToast("error", err.message || "Failed to mark slips as credited");
        }
    });

    $(document).on("click", ".btnGeneratePDF", async function () {
        const slipId = $(this).data("id");
        const $btn = $(this);
        $btn.prop("disabled", true).html('<i class="fa fa-spinner fa-spin"></i> Generating...');

        try {
            const res = await axiosClient.post(`/api/accountant/${orgId}/slip/${slipId}/generate-pdf`);
            showToast("success", "Payslip PDF generated successfully");

            // Replace button with Download link
            $btn.replaceWith(`
            <a href="${res.pdfUrl}" target="_blank" class="btn btn-sm btn-outline-success">
                <i class="fa-solid fa-download"></i> Download PDF
            </a>
        `);
        } catch (err) {
            console.error("PDF generation failed:", err);
            showToast("error", "Failed to generate PDF");
            $btn.prop("disabled", false).html('<i class="fa-solid fa-file-pdf"></i> Generate PDF');
        }
    });


    $("#btnGenerateSelectedPDF").on("click", async function () {
        const selectedIds = $(".slip-checkbox:checked").map((_, el) => $(el).val()).get();

        if (selectedIds.length === 0) {
            showToast("warning", "Select at least one employee.");
            return;
        }

        try {
            for (const id of selectedIds) {
                await axiosClient.post(`/api/accountant/${orgId}/slip/${id}/generate-pdf`);
            }
            showToast("success", "PDFs generated successfully for selected employees");
            loadSalarySlips(); // reload to update buttons
        } catch (err) {
            showToast("error", "Some PDFs failed to generate");
            console.error(err);
        }

        updateSelectedCount();
    });



    // ───────────────────────────────────────────────
    // 📄 View Payslip PDF
    // ───────────────────────────────────────────────
    $(document).on("click", ".btnViewPDF", function () {
        const slipId = $(this).data("id");
        window.open(`/api/payroll/payslip/${slipId}/pdf`, "_blank");
    });
});
