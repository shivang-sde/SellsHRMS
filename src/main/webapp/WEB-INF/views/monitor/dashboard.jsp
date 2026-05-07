<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

            <div class="monitor-dashboard">

                <jsp:include page="_notification-info.jsp" />

                <!-- Stats Cards -->
                <div class="row g-4 mb-4" id="statsContainer">
                    <div class="col-md-3">
                        <div class="card border-0 shadow-sm h-100">
                            <div class="card-body">
                                <div class="d-flex align-items-center">
                                    <div class="flex-shrink-0">
                                        <div class="bg-primary bg-opacity-10 p-3 rounded">
                                            <i class="fa-solid fa-link fa-2x text-primary"></i>
                                        </div>
                                    </div>
                                    <div class="flex-grow-1 ms-3">
                                        <h6 class="text-muted mb-1">Total URLs</h6>
                                        <h3 class="mb-0" id="totalUrls">--</h3>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="card border-0 shadow-sm h-100">
                            <div class="card-body">
                                <div class="d-flex align-items-center">
                                    <div class="flex-shrink-0">
                                        <div class="bg-success bg-opacity-10 p-3 rounded">
                                            <i class="fa-solid fa-check-circle fa-2x text-success"></i>
                                        </div>
                                    </div>
                                    <div class="flex-grow-1 ms-3">
                                        <h6 class="text-muted mb-1">Up</h6>
                                        <h3 class="mb-0" id="upCount">--</h3>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="card border-0 shadow-sm h-100">
                            <div class="card-body">
                                <div class="d-flex align-items-center">
                                    <div class="flex-shrink-0">
                                        <div class="bg-danger bg-opacity-10 p-3 rounded">
                                            <i class="fa-solid fa-exclamation-triangle fa-2x text-danger"></i>
                                        </div>
                                    </div>
                                    <div class="flex-grow-1 ms-3">
                                        <h6 class="text-muted mb-1">Down</h6>
                                        <h3 class="mb-0" id="downCount">--</h3>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="card border-0 shadow-sm h-100">
                            <div class="card-body">
                                <div class="d-flex align-items-center">
                                    <div class="flex-shrink-0">
                                        <div class="bg-info bg-opacity-10 p-3 rounded">
                                            <i class="fa-solid fa-chart-line fa-2x text-info"></i>
                                        </div>
                                    </div>
                                    <div class="flex-grow-1 ms-3">
                                        <h6 class="text-muted mb-1">Avg Uptime</h6>
                                        <h3 class="mb-0" id="avgUptime">--%</h3>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Active Incidents Section -->
                <div class="card border-0 shadow-sm mb-4" id="incidentsCard" style="display: none;">
                    <div class="card-header bg-white border-0 pt-4 pb-0">
                        <h5 class="fw-bold text-danger">
                            <i class="fa-solid fa-bell"></i> Active Incidents
                        </h5>
                    </div>
                    <div class="card-body" id="incidentsList"></div>
                </div>

                <!-- Response Time Chart -->
                <div class="card border-0 shadow-sm mb-4">
                    <div class="card-header bg-white border-0 pt-4 pb-0">
                        <h5 class="fw-bold">Response Time Trends</h5>
                    </div>
                    <div class="card-body">
                        <canvas id="responseTimeChart" height="100"></canvas>
                    </div>
                </div>

                <!-- Advanced Analytics Section -->
                <div class="card border-0 shadow-sm mb-4">
                    <div
                        class="card-header bg-white border-0 pt-4 pb-0 d-flex justify-content-between align-items-center flex-wrap">
                        <h5 class="fw-bold mb-2"><i class="fa-solid fa-chart-simple"></i> Advanced Analytics</h5>
                        <div class="d-flex gap-2">
                            <select id="analyticsType" class="form-select form-select-sm w-auto">
                                <option value="overall">Overall (All URLs)</option>
                                <option value="group">By Group</option>
                                <option value="url">By URL</option>
                            </select>
                            <select id="analyticsGroupSelect" class="form-select form-select-sm w-auto"
                                style="display:none">
                                <option value="">Select Group</option>
                            </select>
                            <select id="analyticsUrlSelect" class="form-select form-select-sm w-auto"
                                style="display:none">
                                <option value="">Select URL</option>
                            </select>
                        </div>
                    </div>
                    <div class="card-body">
                        <canvas id="advancedChart" height="100"></canvas>
                    </div>
                </div>

                <!-- Group Summary Cards -->
                <div class="row g-3 mb-4" id="groupSummaryContainer"></div>

                <!-- Slowest URLs Table -->
                <div class="card border-0 shadow-sm mb-4">
                    <div class="card-header bg-white border-0 pt-4 pb-0">
                        <h5 class="fw-bold"><i class="fa-solid fa-tachometer-alt"></i> Slowest Responding URLs</h5>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-sm" id="slowestUrlsTable">
                                <thead>
                                    <tr>
                                        <th>Name</th>
                                        <th>URL</th>
                                        <th>Avg Response (ms)</th>
                                    </tr>
                                </thead>
                                <tbody></tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <!-- Uptime Trend Modal (optional) -->
                <div class="modal fade" id="uptimeModal" tabindex="-1">
                    <div class="modal-dialog modal-lg">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title">Uptime Trend (Last 30 days)</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <div class="modal-body">
                                <canvas id="uptimeChart" height="200"></canvas>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Recent URLs Table -->
                <div class="card border-0 shadow-sm">
                    <div
                        class="card-header bg-white border-0 pt-4 pb-0 d-flex justify-content-between align-items-center">
                        <h5 class="fw-bold">Monitored URLs</h5>
                        <button class="btn btn-primary btn-sm" onclick="showAddUrlModal()">
                            <i class="fa-solid fa-plus"></i> Add URL
                        </button>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-hover align-middle" id="urlsTable">
                                <thead class="table-light">
                                    <tr>
                                        <th>Status</th>
                                        <th>Name</th>
                                        <th>URL</th>
                                        <th>Uptime</th>
                                        <th>Response</th>
                                        <th>Last Checked</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody id="urlsTableBody"></tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Add/Edit URL Modal -->
            <div class="modal fade" id="urlModal" tabindex="-1">
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
                                        <input type="number" class="form-control" id="timeout" value="30" min="5"
                                            max="120">
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="col-md-6 mb-3">
                                        <label class="form-label">Failure Threshold</label>
                                        <input type="number" class="form-control" id="failureThreshold" value="3"
                                            min="1" max="10">
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


            <!-- At the bottom of dashboard.jsp -->
            <script src="${pageContext.request.contextPath}/js/monitor/common.js"></script>
            <script src="${pageContext.request.contextPath}/js/monitor/monitorAPI.js"></script>
            <script src="${pageContext.request.contextPath}/js/monitor/dashboard.js"></script>