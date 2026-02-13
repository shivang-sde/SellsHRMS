<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

    <div class="container-fluid py-3">
        <!-- Header -->
        <div class="card shadow-sm p-3 mb-3">
            <div class="d-flex justify-content-between align-items-center">
                <h4 class="fw-bold mb-0">
                    <i class="fa-solid fa-wallet me-2 text-primary"></i> Accountant Panel
                </h4>
                <button id="btnRefresh" class="btn btn-sm btn-outline-primary">
                    <i class="fa-solid fa-rotate-right me-1"></i> Refresh
                </button>
            </div>
        </div>

        <!-- Filters -->
        <div class="card shadow-sm p-3 mb-3">
            <form id="filterForm" class="row g-2 align-items-end">
                <div class="col-md-2">
                    <label class="form-label">Month</label>
                    <select class="form-select" id="filterMonth">
                        <c:forEach var="m" begin="1" end="12">
                            <option value="${m}" ${m==currentMonth ? 'selected' : '' }>${m}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="col-md-2">
                    <label class="form-label">Year</label>
                    <input type="number" id="filterYear" class="form-control" value="${currentYear}" min="2020"
                        max="2035" />
                </div>

                <div class="col-md-3">
                    <label class="form-label">Department</label>
                    <select id="filterDepartment" class="form-select">
                        <option value="">All Departments</option>
                        <c:forEach var="dept" items="${departments}">
                            <option value="${dept.id}">${dept.name}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="col-md-2">
                    <label class="form-label">Status</label>
                    <select id="filterCredited" class="form-select">
                        <option value="">All</option>
                        <option value="false">Pending</option>
                        <option value="true">Credited</option>
                    </select>
                </div>

                <div class="col-md-3">
                    <label class="form-label">Search</label>
                    <input type="text" id="filterSearch" class="form-control" placeholder="Employee, Dept, Code..." />
                </div>

                <div class="col-12 text-end mt-2">
                    <button type="submit" class="btn btn-primary">
                        <i class="fa-solid fa-filter me-1"></i> Apply Filters
                    </button>
                </div>
            </form>
        </div>


        <div class="d-flex justify-content-end mb-2">
            <label class="me-2 small text-muted">Rows per page:</label>
            <select id="pageSizeSelect" class="form-select form-select-sm" style="width: 80px;">
                <option value="10" selected>10</option>
                <option value="20">20</option>
                <option value="50">50</option>
            </select>
        </div>


        <!-- Sticky Toolbar -->
        <div id="stickyToolbar"
            class="sticky-toolbar card shadow-sm mb-3 p-2 bg-light d-flex justify-content-between align-items-center">
            <div>
                <span class="fw-bold text-primary" id="selectedCount">0</span>
                <span class="text-muted">selected</span>
            </div>
            <div class="d-flex gap-2">
                <button class="btn btn-success btn-sm" id="btnMarkBulkCredited">
                    <i class="fa-solid fa-check-double me-1"></i> Mark Selected as Credited
                </button>
                <button id="btnGenerateSelectedPDF" class="btn btn-outline-primary mb-2">
                    <i class="fa-solid fa-file-pdf"></i> Generate PDF for Selected
                </button>

                <button class="btn btn-outline-success btn-sm" id="btnExportExcel">
                    <i class="fa-solid fa-file-excel me-1"></i> Export Excel
                </button>

                <!-- <button class="btn btn-outline-danger btn-sm" id="btnExportPDF">
                    <i class="fa-solid fa-file-pdf me-1"></i> Export PDF
                </button> -->
            </div>
        </div>

        <!-- Data Table -->
        <div class="card shadow-sm">
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-hover align-middle" id="salarySlipTable">
                        <thead class="table-light">
                            <tr>
                                <th style="width: 40px;"><input type="checkbox" id="selectAll" /></th>
                                <th>Employee & Bank Details</th>
                                <th class="table-info">Gross & Net Target</th>
                                <th class="table-warning">Statutory Deductions (Emp)</th>
                                <th class="table-info">Statutory Contribution (Org)</th>
                                <th>Other Ded.</th>
                                <th class="table-primary">Net Payable</th>
                                <th>Status</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody id="salarySlipBody">
                            <tr>
                                <td colspan="9" class="text-center text-muted py-4">
                                    No records found
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>

                <div class="d-flex justify-content-between align-items-center mt-3">
                    <span id="paginationInfo" class="text-muted small"></span>
                    <ul class="pagination pagination-sm mb-0" id="paginationControls"></ul>
                </div>
            </div>
        </div>

        <!-- Pagination -->
        <div class="d-flex justify-content-between align-items-center mt-3">
            <span id="paginationInfo" class="text-muted small"></span>
            <ul class="pagination pagination-sm mb-0" id="paginationControls"></ul>
        </div>

    </div>
    </div>
    </div>