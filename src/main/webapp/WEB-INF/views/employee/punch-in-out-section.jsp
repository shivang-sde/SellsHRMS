<%-- ============================= --%>
<%--  Punch In/Out + Summary (Unified Layout) --%>
<%-- ============================= --%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/employee/punch-in-out-section.css">

<div class="punch-section-wrapper">
    <div class="row g-4 align-items-stretch punch-section-row">

        <%-- ──────────────────────────────────────────────────────
             LEFT / BOTTOM on mobile: Punch Card
        ────────────────────────────────────────────────────── --%>
        <div class="col-12 col-lg-4 col-xl-3 order-2 order-lg-1">
            <div class="card shadow-lg border-0 rounded-4 h-100 overflow-hidden punch-card-refined" id="mainPunchCard">
                <div class="status-indicator-glow"></div>

                <div class="card-body p-4 d-flex flex-column gap-3">

                    <%-- Card Header: Date + Time (left) · Badge (right) --%>
                    <div class="d-flex justify-content-between align-items-start">
                        <div class="punch-clock-block">
                            <h6 id="currentDate"
                                class="text-muted small fw-bold text-uppercase mb-0 ls-1 punch-date-label">
                                Loading...
                            </h6>
                            <div id="currentTime" class="punch-time fw-bold text-dark">--:--:--</div>
                        </div>

                        <div id="punchStatusBadge" class="flex-shrink-0 ms-2">
                            <span class="status-badge status-badge--offduty">
                                <span class="status-badge__dot"></span>
                                <i class="status-badge__icon fas fa-moon"></i>
                                <span class="status-badge__label">Off Duty</span>
                            </span>
                        </div>
                    </div>

                    <%-- Working-From Selector + Live Timer --%>
                    <div id="punchedFromDiv" class="flex-grow-1">
                        <label class="form-label small fw-bold text-muted mb-2">Working From</label>
                        <div class="input-group bg-light rounded-pill p-1 shadow-sm mb-3">
                            <span class="input-group-text border-0 bg-transparent text-primary">
                                <i class="fas fa-map-marker-alt"></i>
                            </span>
                            <select class="form-select border-0 bg-transparent fw-semibold" id="punchedFrom">
                                <option value="WFO">Office (WFO)</option>
                                <option value="WFH">Home (WFH)</option>
                            </select>
                        </div>

                        <div id="workingHours"
                             class="text-center py-3 bg-soft-primary rounded-4"
                             style="display:none">
                            <span class="text-uppercase small fw-bold text-primary opacity-75 d-block mb-1">
                                Shift Duration
                            </span>
                            <h2 id="hoursWorked" class="fw-bold text-primary mb-0">00h 00m</h2>
                        </div>
                    </div>

                    <%-- Action Buttons --%>
                    <div class="d-grid gap-2">
                        <button id="btnPunchIn"
                                class="btn btn-primary rounded-pill py-2 py-md-3 fw-bold shadow-sm punch-btn">
                            <i class="fas fa-play me-2"></i> Start Workday
                        </button>
                        <button id="btnPunchOut"
                                class="btn btn-outline-danger rounded-pill py-2 py-md-3 fw-bold shadow-sm punch-btn"
                                style="display:none">
                            <i class="fas fa-power-off me-2"></i> End Workday
                        </button>
                    </div>

                    <%-- GPS Tag --%>
                    <div id="lng-lat"
                         class="text-muted x-small d-flex align-items-center justify-content-center">
                        <i class="fas fa-location-arrow me-1"></i> Waiting for GPS...
                    </div>

                </div>
            </div>
        </div>

        <%-- ──────────────────────────────────────────────────────
             RIGHT / TOP on mobile: 2×2 Summary Cards
             xs-sm  → 2 per row (col-6)
             md+    → 4 per row (col-md-3)
        ────────────────────────────────────────────────────── --%>
        <div class="col-12 col-lg-8 col-xl-9 order-1 order-lg-2">
            <div class="row g-3 h-100">

                <div class="col-6 col-md-3">
                    <div class="card summary-card border-0 shadow-sm rounded-4 text-center h-100">
                        <div class="card-body d-flex flex-column justify-content-center p-3">
                            <div class="icon-shape bg-soft-primary text-primary rounded-circle mx-auto mb-2">
                                <i class="fas fa-clock"></i>
                            </div>
                            <h6 class="text-muted x-small fw-bold text-uppercase mb-1">Punch In</h6>
                            <h4 id="todayPunchIn" class="summary-value fw-bold text-dark mb-0">--:--</h4>
                        </div>
                    </div>
                </div>

                <div class="col-6 col-md-3">
                    <div class="card summary-card border-0 shadow-sm rounded-4 text-center h-100">
                        <div class="card-body d-flex flex-column justify-content-center p-3">
                            <div class="icon-shape bg-soft-danger text-danger rounded-circle mx-auto mb-2">
                                <i class="fas fa-sign-out-alt"></i>
                            </div>
                            <h6 class="text-muted x-small fw-bold text-uppercase mb-1">Punch Out</h6>
                            <h4 id="todayPunchOut" class="summary-value fw-bold text-dark mb-0">--:--</h4>
                        </div>
                    </div>
                </div>

                <div class="col-6 col-md-3">
                    <div class="card summary-card border-0 shadow-sm rounded-4 text-center h-100">
                        <div class="card-body d-flex flex-column justify-content-center p-3">
                            <div class="icon-shape bg-soft-success text-success rounded-circle mx-auto mb-2">
                                <i class="fas fa-hourglass-half"></i>
                            </div>
                            <h6 class="text-muted x-small fw-bold text-uppercase mb-1">Total Hours</h6>
                            <h4 id="todayTotalHours" class="summary-value fw-bold text-dark mb-0">0h</h4>
                        </div>
                    </div>
                </div>

                <div class="col-6 col-md-3">
                    <div class="card summary-card border-0 shadow-sm rounded-4 text-center h-100">
                        <div class="card-body d-flex flex-column justify-content-center p-3">
                            <div class="icon-shape bg-soft-info text-info rounded-circle mx-auto mb-2">
                                <i class="fas fa-calendar-check"></i>
                            </div>
                            <h6 class="text-muted x-small fw-bold text-uppercase mb-1">Attendance</h6>
                            <h4 id="monthDaysPresent" class="summary-value fw-bold text-dark mb-0">0 Days</h4>
                        </div>
                    </div>
                </div>

            </div>
        </div>

    </div>
</div>

<script src="${pageContext.request.contextPath}/js/employee/punch-in-out-section.js"></script>