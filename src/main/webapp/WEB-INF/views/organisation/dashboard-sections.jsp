<div class="row g-4">
    <!-- 🔔 Reminders -->
    <div class="col-xl-3 col-lg-4 col-md-6">
        <div class="card dashboard-card">
            <div class="card-header">
                <i class="fas fa-bell"></i>
                <span>Upcoming Reminders</span>
            </div>
            <div class="card-body" id="remindersContainer">
                <div class="loading-state">Loading...</div>
            </div>
        </div>
    </div>

    <!-- 🎂 Birthdays -->
    <div class="col-xl-3 col-lg-4 col-md-6">
        <div class="card dashboard-card">
            <div class="card-header">
                <i class="fas fa-birthday-cake"></i>
                <span>Upcoming Birthdays</span>
            </div>
            <div class="card-body" id="birthdaysContainer">
                <div class="loading-state">Loading...</div>
            </div>
        </div>
    </div>

    <!-- 🏆 Work Anniversaries -->
    <div class="col-xl-3 col-lg-4 col-md-6">
        <div class="card dashboard-card">
            <div class="card-header">
                <i class="fas fa-award"></i>
                <span>Work Anniversaries</span>
            </div>
            <div class="card-body" id="anniversaryContainer">
                <div class="loading-state">Loading...</div>
            </div>
        </div>
    </div>

    <!-- 🎉 Holidays -->
    <div class="col-xl-3 col-lg-4 col-md-6">
        <div class="card dashboard-card">
            <div class="card-header">
                <i class="fas fa-umbrella-beach"></i>
                <span>Upcoming Holidays</span>
            </div>
            <div class="card-body" id="holidayContainer">
                <div class="loading-state">Loading...</div>
            </div>
        </div>
    </div>

    <!-- 📅 Events & Notices -->
    <div class="col-xl-6 col-lg-8 col-md-12">
        <div class="card dashboard-card">
            <div class="card-header">
                <i class="fas fa-calendar-alt"></i>
                <span>Events & Notices</span>
            </div>
            <div class="card-body" id="eventsContainer">
                <div class="loading-state">Loading...</div>
            </div>
        </div>
    </div>
</div>

<style>
    .dashboard-card {
        border: 1px solid var(--zinc-200);
        border-radius: 1rem;
        background: #ffffff;
        height: 100%;
        transition: all 0.2s ease;
        overflow: hidden;
    }

    .dashboard-card:hover {
        border-color: var(--zinc-300);
        transform: translateY(-2px);
        box-shadow: 0 8px 20px rgba(0, 0, 0, 0.04);
    }

    .dashboard-card .card-header {
        background: #ffffff;
        border-bottom: 1px solid var(--zinc-100);
        padding: 1rem 1.25rem;
        display: flex;
        align-items: center;
        gap: 0.75rem;
        font-size: 0.875rem;
        font-weight: 700;
        color: var(--zinc-900);
    }

    .dashboard-card .card-header i {
        color: var(--zinc-400);
        font-size: 1rem;
    }

    .dashboard-card .card-body {
        padding: 1rem 1.25rem;
        font-size: 0.8125rem;
        color: var(--zinc-600);
        min-height: 120px;
    }

    .loading-state {
        display: flex;
        align-items: center;
        justify-content: center;
        height: 80px;
        color: var(--zinc-400);
        font-style: italic;
    }

    /* Standard item styling for injected content */
    .dashboard-item {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        padding: 0.625rem 0;
        border-bottom: 1px solid var(--zinc-50);
    }

    .dashboard-item:last-child {
        border-bottom: none;
    }

    .item-icon {
        width: 32px;
        height: 32px;
        background: var(--zinc-50);
        color: var(--zinc-900);
        border-radius: 0.5rem;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;
    }
</style>

<!-- Scripts -->
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="/js/dashboard-sections.js"></script>