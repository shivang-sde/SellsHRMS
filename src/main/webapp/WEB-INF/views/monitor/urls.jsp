<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

        <div class="container-fluid p-3">
            <div class="row mb-4">
                <div class="col-12">
                    <h4 class="fw-bold">Monitor URLs</h4>
                    <p class="text-muted">Manage and monitor your website URLs</p>
                </div>
            </div>
            <jsp:include page="_notification-info.jsp" />


            <!-- Filters -->
            <div class="card border-0 shadow-sm mb-4">
                <div class="card-body">
                    <div class="row g-3">
                        <div class="col-md-4">
                            <input type="text" class="form-control" id="searchInput"
                                placeholder="Search by name or URL...">
                        </div>
                        <div class="col-md-3">
                            <select class="form-select" id="statusFilter">
                                <option value="">All Status</option>
                                <option value="up">Up</option>
                                <option value="down">Down</option>
                                <option value="pending">Pending</option>
                            </select>
                        </div>
                        <div class="col-md-2">
                            <button class="btn btn-primary w-100" onclick="loadUrls()">
                                <i class="fa-solid fa-search"></i> Search
                            </button>
                        </div>
                        <div class="col-md-3 text-end">
                            <button class="btn btn-success" onclick="showAddUrlModal()">
                                <i class="fa-solid fa-plus"></i> Add URL
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <!-- URLs Table -->
            <div class="card border-0 shadow-sm">
                <div class="card-header bg-white border-0 pt-4 pb-0">
                    <h5 class="fw-bold">All Monitored URLs</h5>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle">
                            <thead class="table-light">
                                <tr>
                                    <th>Status</th>
                                    <th>Name</th>
                                    <th>URL</th>
                                    <th>Method</th>
                                    <th>Interval</th>
                                    <th>Uptime</th>
                                    <th>Response</th>
                                    <th>Last Checked</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody id="urlsTableBody">
                                <tr>
                                    <td colspan="9" class="text-center py-5">
                                        <div class="spinner-border text-primary" role="status"></div>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                    <!-- Pagination -->
                    <div id="paginationContainer" class="mt-3 d-flex justify-content-end"></div>
                </div>
            </div>
        </div>


        <!-- Add/Edit URL Modal -->
        <div class="modal fade shadow-lg modal-dialog-scrollable" id="urlModal" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="urlModalTitle">Add URL</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <form id="urlForm">
                            <input type="hidden" id="urlId">
                            <div class="mb-3">
                                <label class="form-label">Name *</label>
                                <input type="text" class="form-control" id="urlName" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">URL *</label>
                                <input type="url" class="form-control" id="urlAddress" required>
                            </div>
                            <div class="row">
                                <div class="col-md-4 mb-3">
                                    <label class="form-label">Method</label>
                                    <select class="form-select" id="urlMethod">
                                        <option value="GET">GET</option>
                                        <option value="POST">POST</option>
                                        <option value="HEAD">HEAD</option>
                                    </select>
                                </div>
                                <div class="col-md-4 mb-3">
                                    <label class="form-label">Check Interval</label>
                                    <select class="form-select" id="checkInterval">
                                        <option value="60">1 minute</option>
                                        <option value="300" selected>5 minutes</option>
                                        <option value="600">10 minutes</option>
                                        <option value="1800">30 minutes</option>
                                        <option value="3600">1 hour</option>
                                    </select>
                                </div>
                                <div class="col-md-4 mb-3">
                                    <label class="form-label">Timeout (seconds)</label>
                                    <input type="number" class="form-control" id="timeout" value="30" min="5" max="120">
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Failure Threshold</label>
                                    <input type="number" class="form-control" id="failureThreshold" value="3" min="1"
                                        max="10">
                                    <small class="text-muted">Consecutive failures before marking down</small>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Add to Group (Optional)</label>
                                    <select class="form-select" id="groupId">
                                        <option value="">No group</option>
                                    </select>
                                </div>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="button" class="btn btn-primary" onclick="saveUrl()">Save</button>
                    </div>
                </div>
            </div>
        </div>


        <!-- URL Detail Modal -->
        <div class="modal fade" id="urlDetailModal" tabindex="-1">
            <div class="modal-dialog modal-xl">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="urlDetailTitle">URL Details</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body" id="urlDetailBody"></div>
                </div>
            </div>
        </div>

        <!-- Include the common modals and scripts -->
        <script src="${pageContext.request.contextPath}/js/monitor/common.js"></script>
        <script src="${pageContext.request.contextPath}/js/monitor/urls.js"></script>