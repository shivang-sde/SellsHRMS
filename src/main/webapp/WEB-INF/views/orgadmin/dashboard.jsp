<%@ page contentType="text/html;charset=UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

        <style>
            :root {
                --slate-50: #f8fafc;
                --slate-100: #f1f5f9;
                --slate-200: #e2e8f0;
                --slate-600: #475569;
                --slate-900: #0f172a;

                --zinc-50: #fafafa;
                --zinc-100: #f4f4f5;
                --zinc-200: #e4e4e7;
                --zinc-300: #d4d4d8;
                --zinc-400: #a1a1aa;
                --zinc-500: #71717a;
                --zinc-600: #52525b;
                --zinc-700: #3f3f46;
                --zinc-800: #27272a;
                --zinc-900: #18181b;
                --zinc-950: #09090b;
            }

            .dashboard-wrapper {
                padding: 2rem;
                background-color: var(--slate-100);
                min-height: calc(100vh - 64px);
            }

            /* ── Header ────────────────────────── */
            .dashboard-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 2rem;
            }

            .header-content h1 {
                font-size: 1.75rem;
                font-weight: 800;
                color: var(--zinc-900);
                letter-spacing: -0.03em;
                margin: 0;
            }

            .header-content p {
                color: var(--zinc-500);
                font-size: 0.9375rem;
                margin: 0.25rem 0 0;
            }

            /* ── KPI Cards ─────────────────────── */
            .kpi-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
                gap: 1.25rem;
                margin-bottom: 2.5rem;
            }

            .kpi-card {
                background: #ffffff;
                border: 1px solid var(--slate-200);
                border-radius: 1.25rem;
                padding: 1.5rem;
                display: flex;
                align-items: center;
                gap: 1.25rem;
                transition: all 0.3s ease;
                box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
            }

            .kpi-card:hover {
                border-color: var(--slate-300);
                box-shadow: 0 12px 25px -5px rgba(0, 0, 0, 0.08);
                transform: translateY(-4px);
            }

            .kpi-icon {
                width: 52px;
                height: 52px;
                background: var(--slate-100);
                color: var(--slate-900);
                border-radius: 1rem;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 1.35rem;
            }

            .kpi-info h6 {
                font-size: 0.8125rem;
                font-weight: 600;
                color: var(--zinc-500);
                text-transform: uppercase;
                letter-spacing: 0.05em;
                margin: 0 0 0.25rem;
            }

            .kpi-info h3 {
                font-size: 1.5rem;
                font-weight: 700;
                color: var(--zinc-900);
                margin: 0;
            }

            /* ── Quick Actions ────────────────── */
            .section-title {
                font-size: 1.125rem;
                font-weight: 700;
                color: var(--zinc-900);
                margin-bottom: 1.25rem;
                display: flex;
                align-items: center;
                gap: 0.75rem;
            }

            .quick-actions-card {
                background: #ffffff;
                border: 1px solid var(--zinc-200);
                border-radius: 1rem;
                padding: 1.5rem;
                margin-bottom: 2.5rem;
            }

            .actions-grid {
                display: flex;
                flex-wrap: wrap;
                gap: 1rem;
            }

            .btn-action {
                display: inline-flex;
                align-items: center;
                gap: 0.625rem;
                padding: 0.625rem 1.25rem;
                background: #ffffff;
                border: 1px solid var(--zinc-200);
                border-radius: 0.75rem;
                color: var(--zinc-700);
                font-size: 0.875rem;
                font-weight: 600;
                text-decoration: none;
                transition: all 0.2s ease;
            }

            .btn-action:hover {
                background: var(--zinc-50);
                border-color: var(--zinc-900);
                color: var(--zinc-950);
                transform: translateY(-1px);
            }

            .btn-action i {
                color: var(--zinc-400);
                font-size: 0.9375rem;
            }

            .btn-primary-custom {
                background: var(--zinc-900);
                border-color: var(--zinc-900);
                color: #ffffff;
            }

            .btn-primary-custom:hover {
                background: var(--zinc-800);
                border-color: var(--zinc-800);
                color: #ffffff;
            }

            .btn-primary-custom i {
                color: rgba(255, 255, 255, 0.7);
            }

            /* ── Responsive ── */
            @media (max-width: 768px) {
                .dashboard-header {
                    flex-direction: column;
                    align-items: flex-start;
                    gap: 1.25rem;
                }

                .header-content h1 {
                    font-size: 1.5rem;
                }
            }
        </style>

        <div class="dashboard-wrapper">

            <!-- Header -->
            <div class="dashboard-header">
                <div class="header-content">
                    <h1>Organisation Overview</h1>
                    <p>Welcome back. Here's what's happening with your workforce today.</p>
                </div>

                <button class="btn btn-primary-custom px-4 py-2" onclick="location.href='/org/create-employee'">
                    <i class="fas fa-user-plus me-2"></i> Add Employee
                </button>
            </div>

            <!-- KPI Grid -->
            <div class="kpi-grid">
                <div class="kpi-card">
                    <div class="kpi-icon"><i class="fas fa-users"></i></div>
                    <div class="kpi-info">
                        <h6>Total Employees</h6>
                        <h3 id="countEmployees">0</h3>
                    </div>
                </div>

                <div class="kpi-card">
                    <div class="kpi-icon"><i class="fas fa-building"></i></div>
                    <div class="kpi-info">
                        <h6>Departments</h6>
                        <h3 id="countDepartments">0</h3>
                    </div>
                </div>

                <div class="kpi-card">
                    <div class="kpi-icon"><i class="fas fa-chart-pie"></i></div>
                    <div class="kpi-info">
                        <h6>Max Capacity</h6>
                        <h3 id="maxEmpLimit">--</h3>
                    </div>
                </div>

            </div>


            <!-- Quick Actions -->
            <div class="mt-5">
                <h5 class="section-title"><i class="fas fa-bolt text-warning"></i> Quick Actions</h5>
                <div class="quick-actions-card">
                    <div class="actions-grid">
                        <a href="/org/departments" class="btn-action">
                            <i class="fas fa-building"></i> Manage Departments
                        </a>
                        <a href="/org/designations" class="btn-action">
                            <i class="fas fa-id-badge"></i> Manage Designations
                        </a>
                        <a href="/org/employees" class="btn-action">
                            <i class="fas fa-users"></i> Employee List
                        </a>
                        <a href="/accountant/user" class="btn-action btn-primary-custom">
                            <i class="fas fa-user-plus"></i> Add Accountant
                        </a>
                    </div>
                </div>
            </div>

            <!-- Main Dashboard Sections -->
            <jsp:include page="/WEB-INF/views/organisation/dashboard-sections.jsp" />



        </div>