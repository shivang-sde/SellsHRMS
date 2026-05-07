<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

    <div class="container-fluid p-3">
        <div class="row mb-4">
            <div class="col-12">
                <h4 class="fw-bold">Incident History</h4>
                <p class="text-muted">View all downtime incidents and resolution details</p>
            </div>
        </div>

        <!-- Filters -->
        <div class="card border-0 shadow-sm mb-4">
            <div class="card-body">
                <div class="row g-3">
                    <div class="col-md-3">
                        <select class="form-select" id="statusFilter">
                            <option value="">All Incidents</option>
                            <option value="false">Active Incidents</option>
                            <option value="true">Resolved Incidents</option>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <button class="btn btn-primary" onclick="loadIncidents()">
                            <i class="fa-solid fa-filter"></i> Filter
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Incidents Table -->
        <div class="card border-0 shadow-sm">
            <div class="card-header bg-white border-0 pt-4 pb-0">
                <h5 class="fw-bold">Incident Records</h5>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead class="table-light">
                            <tr>
                                <th>URL</th>
                                <th>Started</th>
                                <th>Ended</th>
                                <th>Duration</th>
                                <th>Cause</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody id="incidentsTableBody">
                            <tr>
                                <td colspan="6" class="text-center py-5">
                                    <div class="spinner-border text-primary" role="status"></div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <div id="paginationContainer" class="mt-3 d-flex justify-content-end"></div>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/js/monitor/incidents.js"></script>