<!-- ========================== -->
<!--  Responsive Attendance Card (Balanced Dashboard Design) -->
<!-- ========================== -->
<div class="col-lg-4 col-md-6 mb-4">
    <div class="card attendance-card border-0 shadow-sm rounded-4 h-100 overflow-hidden position-relative">
        <div class="gradient-bar-top"></div>

        <div class="card-body text-center py-4 px-3">
            <!-- Time -->
            <div class="time-section mb-3">
                <div id="currentTime" class="fw-bold fs-3 text-dark mb-1">--:--:--</div>
                <div id="currentDate" class="text-muted small fw-medium">Loading date...</div>
            </div>

            <!-- Status -->
            <div id="punchStatus" class="mb-3">
                <span class="badge bg-light text-dark px-3 py-2 rounded-pill border">
                    <i class="fas fa-user-clock me-1 text-primary"></i> Not Punched In
                </span>
            </div>

            <!-- Punch Buttons -->
            <div class="d-flex flex-column flex-sm-row justify-content-center align-items-center gap-2">
                <button id="btnPunchIn" class="btn btn-gradient-green btn-sm fw-semibold px-4">
                    <i class="fas fa-sign-in-alt me-1"></i> In
                </button>
                <button id="btnPunchOut" class="btn btn-gradient-red btn-sm fw-semibold px-4" style="display:none">
                    <i class="fas fa-sign-out-alt me-1"></i> Out
                </button>
            </div>

            <!-- Select Mode -->
            <div id="punchedFromDiv" class="mt-3">
                <select class="form-select text-center fw-semibold rounded-pill py-1 border-0 shadow-sm"
                    id="punchedFrom">
                    <option value="">-- Select Mode --</option>
                    <option value="WFO"><i class="fas fa-building me-1 text-primary"></i> Work From Office</option>
                    <option value="WFH"><i class="fas fa-home me-1 text-primary"></i> Work From Home</option>
                </select>
            </div>

            <!-- Location -->
            <div id="lng-lat" class="text-success small mt-2"></div>

            <!-- Working Hours -->
            <div id="workingHours" class="mt-4" style="display:none">
                <small class="text-muted d-block mb-1 fw-medium">Working Duration</small>
                <div class="badge bg-light text-dark shadow-sm fs-7 px-3 py-2 rounded-pill">
                    <i class="far fa-clock me-1 text-primary"></i>
                    <strong id="hoursWorked" class="text-primary">0h 0m</strong>
                </div>
            </div>
        </div>

        <div class="gradient-bar-bottom"></div>
    </div>
</div>

<!-- JS -->
<script src="${pageContext.request.contextPath}/js/employee/punch-in-out-section.js"></script>

<!-- Improved Responsive Styles -->
<style>
    /* === Gradient Bars === */
    .gradient-bar-top {
        height: 4px;
        background: linear-gradient(90deg, #0d6efd, #198754);
    }

    .gradient-bar-bottom {
        height: 3px;
        background: linear-gradient(90deg, #198754, #0d6efd);
    }

    /* === Card Styling === */
    .attendance-card {
        background: #ffffff;
        transition: all 0.3s ease;
    }

    .attendance-card:hover {
        transform: translateY(-3px);
        box-shadow: 0 6px 15px rgba(0, 0, 0, 0.08);
    }

    /* === Time Section === */
    #currentTime {
        font-size: clamp(1.4rem, 2vw, 2rem);
        color: #212529;
    }

    #currentDate {
        font-size: 0.85rem;
    }

    /* === Buttons === */
    .btn-gradient-green {
        background: linear-gradient(90deg, #28a745, #20c997);
        border: none;
        color: #fff;
        transition: all 0.25s ease;
    }

    .btn-gradient-green:hover {
        background: linear-gradient(90deg, #20c997, #28a745);
        transform: translateY(-1px);
    }

    .btn-gradient-red {
        background: linear-gradient(90deg, #dc3545, #fd7e14);
        border: none;
        color: #fff;
        transition: all 0.25s ease;
    }

    .btn-gradient-red:hover {
        background: linear-gradient(90deg, #fd7e14, #dc3545);
        transform: translateY(-1px);
    }

    /* === Responsive Adjustments === */
    @media (max-width: 992px) {
        .attendance-card {
            margin-bottom: 1rem;
        }

        .card-body {
            padding: 1.5rem;
        }

        .btn {
            font-size: 0.9rem;
        }

        .badge {
            font-size: 0.8rem;
        }
    }

    @media (max-width: 576px) {
        .btn {
            width: 100%;
        }

        #punchedFromDiv {
            width: 100%;
        }
    }
</style>