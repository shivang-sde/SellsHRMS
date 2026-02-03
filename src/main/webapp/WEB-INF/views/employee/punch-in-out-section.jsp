<!-- ============================= -->
<!--  Punch In/Out + Summary (Unified Layout) -->
<!-- ============================= -->


<div class="container-fluid px-4 my-4">
    <div class="row g-4 my-4 d-flex flex-column-reverse flex-lg-row-reverse align-items-stretch">

        <div class="col-lg-4 col-xl-3">
            <div class="card shadow-lg border-0 rounded-4 h-100 overflow-hidden punch-card-refined" id="mainPunchCard">
                <div class="status-indicator-glow"></div>
                <div class="card-body p-4 d-flex flex-column">
                    <div class="d-flex justify-content-between align-items-start mb-4">
                        <div>
                            <h6 id="currentDate" class="text-muted small fw-bold text-uppercase mb-0 ls-1">Loading...
                            </h6>
                            <div id="currentTime" class="display-6 fw-bold text-dark">--:--:--</div>
                        </div>
                        <div id="punchStatusBadge">
                            <span
                                class="badge rounded-pill bg-soft-secondary text-secondary border border-secondary px-3 py-2">
                                <i class="fas fa-moon me-1"></i> Off Duty
                            </span>
                        </div>
                    </div>

                    <div id="punchedFromDiv" class="mb-auto"> <label
                            class="form-label small fw-bold text-muted mb-2">Working From</label>
                        <div class="input-group bg-light rounded-pill p-1 shadow-sm mb-4">
                            <span class="input-group-text border-0 bg-transparent text-primary">
                                <i class="fas fa-map-marker-alt"></i>
                            </span>
                            <select class="form-select border-0 bg-transparent fw-semibold" id="punchedFrom">
                                <option value="WFO">Office (WFO)</option>
                                <option value="WFH">Home (WFH)</option>
                            </select>
                        </div>

                        <div id="workingHours" class="mb-4 text-center py-3 bg-soft-primary rounded-4"
                            style="display:none">
                            <span class="text-uppercase small fw-bold text-primary opacity-75 d-block mb-1">Shift
                                Duration</span>
                            <h2 id="hoursWorked" class="fw-bold text-primary mb-0">00h 00m</h2>
                        </div>
                    </div>

                    <div class="d-grid gap-3">
                        <button id="btnPunchIn"
                            class="btn btn-primary rounded-pill py-3 fw-bold shadow-sm transition-all">
                            <i class="fas fa-play me-2"></i> Start Workday
                        </button>
                        <button id="btnPunchOut"
                            class="btn btn-outline-danger rounded-pill py-3 fw-bold shadow-sm transition-all"
                            style="display:none">
                            <i class="fas fa-power-off me-2"></i> End Workday
                        </button>
                    </div>

                    <div id="lng-lat" class="text-muted x-small mt-3 d-flex align-items-center justify-content-center">
                        <i class="fas fa-location-arrow me-1"></i> Waiting for GPS...
                    </div>
                </div>
            </div>
        </div>

    <div class="col-lg-8 col-xl-9">
        <div class="row g-3 h-100">
    
            <div class="col-6 col-xl-6">
                <div class="card summary-card border-0 shadow-sm rounded-4 text-center h-100 transition-all">
                    <div class="card-body d-flex flex-column justify-content-center p-3">
                        <div class="icon-shape bg-soft-primary text-primary rounded-circle mx-auto mb-2">
                            <i class="fas fa-clock"></i>
                        </div>
                        <h6 class="text-muted x-small fw-bold text-uppercase mb-1">Punch In</h6>
                        <h4 id="todayPunchIn" class="fw-bold text-dark mb-0">--:--</h4>
                    </div>
                </div>
            </div>
    
            <div class="col-6 col-xl-6">
                <div class="card summary-card border-0 shadow-sm rounded-4 text-center h-100 transition-all">
                    <div class="card-body d-flex flex-column justify-content-center p-3">
                        <div class="icon-shape bg-soft-danger text-danger rounded-circle mx-auto mb-2">
                            <i class="fas fa-sign-out-alt"></i>
                        </div>
                        <h6 class="text-muted x-small fw-bold text-uppercase mb-1">Punch Out</h6>
                        <h4 id="todayPunchOut" class="fw-bold text-dark mb-0">--:--</h4>
                    </div>
                </div>
            </div>
    
            <div class="col-6 col-xl-6">
                <div class="card summary-card border-0 shadow-sm rounded-4 text-center h-100 transition-all">
                    <div class="card-body d-flex flex-column justify-content-center p-3">
                        <div class="icon-shape bg-soft-success text-success rounded-circle mx-auto mb-2">
                            <i class="fas fa-hourglass-half"></i>
                        </div>
                        <h6 class="text-muted x-small fw-bold text-uppercase mb-1">Total Hours</h6>
                        <h4 id="todayTotalHours" class="fw-bold text-dark mb-0">0h</h4>
                    </div>
                </div>
            </div>
    
            <div class="col-6 col-xl-6">
                <div class="card summary-card border-0 shadow-sm rounded-4 text-center h-100 transition-all">
                    <div class="card-body d-flex flex-column justify-content-center p-3">
                        <div class="icon-shape bg-soft-info text-info rounded-circle mx-auto mb-2">
                            <i class="fas fa-calendar-check"></i>
                        </div>
                        <h6 class="text-muted x-small fw-bold text-uppercase mb-1">Attendance</h6>
                        <h4 id="monthDaysPresent" class="fw-bold text-dark mb-0">0 Days</h4>
                    </div>
                </div>
            </div>
    
        </div>
    </div>

    </div>
</div>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/employee/punch-in-out-section.css">
<script src="${pageContext.request.contextPath}/js/employee/punch-in-out-section.js"></script>