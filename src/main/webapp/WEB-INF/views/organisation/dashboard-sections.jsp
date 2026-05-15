<div class="row g-2 hrms-dashboard-sections">
    <!-- 🔔 Reminders -->
    <div class="col-xl-3 col-lg-4 col-md-6">
        <div class="hrms-card dashboard-card kpi-style section-reminders">
            <div class="card-header-kpi">
                <div class="kpi-icon">
                    <i class="fas fa-bell"></i>
                </div>
                <div class="kpi-info">
                    <h6>Reminders</h6>
                    <h3 id="remindersCount">0</h3>
                </div>
            </div>
            <div class="card-body" id="remindersContainer">
                <div class="loading-state">
                    <div class="spinner-border spinner-border-sm" role="status"></div>
                </div>
            </div>
        </div>
    </div>

    <!-- 🎂 Birthdays -->
    <div class="col-xl-3 col-lg-4 col-md-6">
        <div class="hrms-card dashboard-card kpi-style section-birthdays">
            <div class="card-header-kpi">
                <div class="kpi-icon">
                    <i class="fas fa-birthday-cake"></i>
                </div>
                <div class="kpi-info">
                    <h6>Birthdays</h6>
                    <h3 id="birthdaysCount">0</h3>
                </div>
            </div>
            <div class="card-body" id="birthdaysContainer">
                <div class="loading-state">
                    <div class="spinner-border spinner-border-sm" role="status"></div>
                </div>
            </div>
        </div>
    </div>

    <!-- 🏆 Work Anniversaries -->
    <div class="col-xl-3 col-lg-4 col-md-6">
        <div class="hrms-card dashboard-card kpi-style section-anniversaries">
            <div class="card-header-kpi">
                <div class="kpi-icon">
                    <i class="fas fa-medal"></i>
                </div>
                <div class="kpi-info">
                    <h6>Anniversaries</h6>
                    <h3 id="anniversariesCount">0</h3>
                </div>
            </div>
            <div class="card-body" id="anniversaryContainer">
                <div class="loading-state">
                    <div class="spinner-border spinner-border-sm" role="status"></div>
                </div>
            </div>
        </div>
    </div>

    <!-- 🎉 Holidays -->
    <div class="col-xl-3 col-lg-4 col-md-6">
        <div class="hrms-card dashboard-card kpi-style section-holidays">
            <div class="card-header-kpi">
                <div class="kpi-icon">
                    <i class="fas fa-umbrella-beach"></i>
                </div>
                <div class="kpi-info">
                    <h6>Holidays</h6>
                    <h3 id="holidaysCount">0</h3>
                </div>
            </div>
            <div class="card-body" id="holidayContainer">
                <div class="loading-state">
                    <div class="spinner-border spinner-border-sm" role="status"></div>
                </div>
            </div>
        </div>
    </div>

    <!-- 📅 Events & Notices -->
    <div class="col-xl-6 col-lg-8 col-md-12">
        <div class="hrms-card dashboard-card kpi-style section-events">
            <div class="card-header-kpi">
                <div class="kpi-icon">
                    <i class="fas fa-calendar-check"></i>
                </div>
                <div class="kpi-info">
                    <h6>Events & Notices</h6>
                    <h3 id="eventsCount">0</h3>
                </div>
            </div>
            <div class="card-body" id="eventsContainer">
                <div class="loading-state">
                    <div class="spinner-border spinner-border-sm" role="status"></div>
                </div>
            </div>
        </div>
    </div>
</div>

<style>
    /* Slate & Zinc Design Tokens for Contrast */
    :root {
        --accent-reminders: #3b82f6;
        --accent-birthdays: #ec4899;
        --accent-anniversaries: #f59e0b;
        --accent-holidays: #06b6d4;
        --accent-events: #6366f1;

        /* Slate-toned palette for better depth */
        --slate-50: #f8fafc;
        --slate-100: #f1f5f9;
        --slate-200: #e2e8f0;
        --slate-300: #cbd5e1;
        --slate-600: #475569;
        --slate-800: #1e293b;
        --slate-900: #0f172a;

        --zinc-100: #f4f4f5;
        --zinc-200: #e4e4e7;
        --zinc-500: #71717a;
        --zinc-900: #18181b;
    }

    .hrms-dashboard-sections {
        background: var(--slate-50);
        padding: 1rem;
        border-radius: 1rem;
    }

    .dashboard-card.kpi-style {
        height: 100%;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        display: flex;
        flex-direction: column;
        border-radius: 1.25rem;
        border: 1px solid var(--slate-200);
        background: #ffffff;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.02);
    }

    .dashboard-card.kpi-style:hover {
        transform: translateY(-4px);
        box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.05), 0 8px 10px -6px rgba(0, 0, 0, 0.05);
        border-color: var(--slate-300);
    }

    .card-header-kpi {
        padding: 1rem;
        display: flex;
        align-items: center;
        gap: 1.25rem;
        background: linear-gradient(to bottom, #ffffff, var(--slate-50));
        border-bottom: 1px solid var(--slate-100);
        border-radius: 1.25rem 1.25rem 0 0;
    }

    .kpi-icon {
        width: 52px;
        height: 52px;
        border-radius: 1rem;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 1.35rem;
        box-shadow: inset 0 2px 4px rgba(255, 255, 255, 0.3);
    }

    .dashboard-card:hover .kpi-icon {
        transform: scale(1.1) rotate(5deg);
    }

    .kpi-info h6 {
        font-size: 0.75rem;
        font-weight: 600;
        color: var(--slate-600);
        text-transform: uppercase;
        letter-spacing: 0.05em;
        margin: 0 0 0.25rem;
    }

    .kpi-info h3 {
        font-size: 1.75rem;
        font-weight: 800;
        color: var(--slate-900);
        margin: 0;
        letter-spacing: -0.02em;
    }

    /* Color variations for icons with subtle glass effect backgrounds */
    .section-reminders .kpi-icon {
        background: #dbeafe;
        color: var(--accent-reminders);
        border: 1px solid #bfdbfe;
    }

    .section-birthdays .kpi-icon {
        background: #fce7f3;
        color: var(--accent-birthdays);
        border: 1px solid #fbcfe8;
    }

    .section-anniversaries .kpi-icon {
        background: #fef3c7;
        color: var(--accent-anniversaries);
        border: 1px solid #fde68a;
    }

    .section-holidays .kpi-icon {
        background: #cffafe;
        color: var(--accent-holidays);
        border: 1px solid #a5f3fc;
    }

    .section-events .kpi-icon {
        background: #e0e7ff;
        color: var(--accent-events);
        border: 1px solid #c7d2fe;
    }

    .dashboard-card .card-body {
        padding: 1.25rem 1.5rem 1.5rem;
        flex-grow: 1;
        min-height: 120px;
        background: #ffffff;
    }

    /* List group refinements - using slate for better contrast */
    .dashboard-card .list-group-item {
        padding: 0.875rem 0;
        border-color: var(--slate-100);
        background: transparent;
        border-bottom: 1px solid var(--slate-100);
        transition: all 0.2s ease;
    }

    .dashboard-card .list-group-item:hover {
        background: var(--slate-50);
        padding-left: 8px;
        margin-left: -8px;
        margin-right: -8px;
        padding-right: 8px;
        border-radius: 8px;
    }

    .dashboard-card .list-group-item:last-child {
        border-bottom: none;
    }

    /* Loading state */
    .loading-state {
        display: flex;
        align-items: center;
        justify-content: center;
        height: 60px;
        color: var(--zinc-400);
    }

    /* Reminder item specific styling */
    #remindersContainer .border-bottom {
        border-color: var(--zinc-100) !important;
        padding-bottom: 0.75rem !important;
        margin-bottom: 0.75rem !important;
    }

    /* Additional utilities */
    .text-pink {
        color: var(--accent-birthdays);
    }

    .text-indigo {
        color: var(--accent-events);
    }
</style>

<!-- Scripts -->
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="/js/dashboard-sections.js"></script>