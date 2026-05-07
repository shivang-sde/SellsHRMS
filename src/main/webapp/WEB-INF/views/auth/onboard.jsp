<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description"
        content="Register your organisation on Sellspark HRMS - Free 6 month trial with up to 20 employees">
    <title>Start Your Free Trial | Sellspark HRMS</title>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="/plugins/bootstrap/css/bootstrap.min.css" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" />

    <style>
        :root {
            --primary: #18181b;
            --primary-hover: #27272a;
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
            --radius: 1.25rem;
        }

        *,
        *::before,
        *::after {
            box-sizing: border-box;
        }

        body {
            font-family: 'Inter', sans-serif;
            margin: 0;
            background: #ffffff;
            overflow-x: hidden;
        }

        .onboard-wrapper {
            display: flex;
            min-height: 100vh;
            width: 100%;
        }

        .onboard-side {
            flex: 1;
            display: flex;
            flex-direction: column;
        }

        /* ── Left Side: Hero ──────────────── */
        .onboard-hero {
            background-color: var(--zinc-100);
            padding: 3rem 2rem;
            justify-content: center;
            align-items: center;
            position: sticky;
            top: 0;
            height: 100vh;
        }

        .onboard-hero-content {
            max-width: 440px;
            animation: slideInLeft 0.6s cubic-bezier(0.16, 1, 0.3, 1);
        }

        @keyframes slideInLeft {
            from {
                opacity: 0;
                transform: translateX(-20px);
            }

            to {
                opacity: 1;
                transform: translateX(0);
            }
        }

        .brand-icon {
            width: 56px;
            height: 56px;
            background: var(--zinc-900);
            border-radius: 1rem;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            color: #fff;
            font-size: 1.5rem;
            margin-bottom: 1.5rem;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
        }

        .onboard-hero h1 {
            color: var(--zinc-900);
            font-size: 2.25rem;
            font-weight: 800;
            letter-spacing: -0.04em;
            line-height: 1.1;
            margin: 0 0 0.75rem;
        }

        .onboard-hero p {
            color: var(--zinc-500);
            font-size: 1rem;
            margin-bottom: 2rem;
            line-height: 1.5;
        }

        /* ── Right Side: Form ──────────────── */
        .onboard-form-side {
            padding: 2.5rem 2rem;
            background: #ffffff;
            justify-content: flex-start;
            align-items: center;
            overflow-y: auto;
        }

        .onboard-card {
            width: 100%;
            max-width: 680px;
            animation: slideInRight 0.6s cubic-bezier(0.16, 1, 0.3, 1);
        }

        @keyframes slideInRight {
            from {
                opacity: 0;
                transform: translateX(20px);
            }

            to {
                opacity: 1;
                transform: translateX(0);
            }
        }

        .section-label {
            display: flex;
            align-items: center;
            gap: 0.625rem;
            color: var(--zinc-900);
            font-weight: 700;
            font-size: 0.75rem;
            text-transform: uppercase;
            letter-spacing: 0.05em;
            margin-bottom: 1.25rem;
            padding-bottom: 0.5rem;
            border-bottom: 1px solid var(--zinc-100);
        }

        .section-label i {
            color: var(--zinc-400);
        }

        .section-label+.section-label {
            margin-top: 1.5rem;
        }

        /* ── Form fields ─────────────────── */
        .form-label {
            color: var(--zinc-700);
            font-size: 0.875rem;
            font-weight: 600;
            margin-bottom: 0.5rem;
        }

        .form-control,
        .form-select {
            background: #ffffff;
            border: 1px solid var(--zinc-200);
            color: var(--zinc-900);
            border-radius: 0.625rem;
            padding: 0.6rem 0.875rem;
            font-size: 0.9375rem;
            transition: all 0.2s ease;
        }

        .form-control:focus,
        .form-select:focus {
            border-color: var(--zinc-900);
            box-shadow: 0 0 0 4px rgba(24, 24, 27, 0.08);
            outline: none;
        }

        /* ── Trial badge ── */
        .trial-badge {
            display: flex;
            align-items: flex-start;
            gap: 1.25rem;
            background: #ffffff;
            border: 1px solid var(--zinc-200);
            border-radius: 1.5rem;
            padding: 1.75rem;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.04);
        }

        .trial-badge i {
            color: var(--zinc-900);
            font-size: 1.75rem;
            margin-top: 0.25rem;
        }

        .trial-badge .trial-text {
            font-size: 1rem;
            color: var(--zinc-600);
            line-height: 1.6;
        }

        .trial-badge .trial-text strong {
            color: var(--zinc-900);
            font-weight: 700;
        }

        /* ── Submit button ── */
        .btn-onboard {
            width: 100%;
            padding: 1rem;
            background: var(--zinc-900);
            border: none;
            border-radius: 0.75rem;
            color: #fff;
            font-weight: 700;
            font-size: 1rem;
            cursor: pointer;
            transition: all 0.2s ease;
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 0.5rem;
            margin-top: 1rem;
        }

        .btn-onboard:hover:not(:disabled) {
            background: var(--zinc-800);
            transform: translateY(-1px);
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
        }

        .btn-onboard:disabled {
            opacity: 0.6;
            cursor: not-allowed;
        }

        /* ── Footer ── */
        .login-link {
            text-align: center;
            margin-top: 2.5rem;
            color: var(--zinc-500);
            font-size: 0.9375rem;
        }

        .login-link a {
            color: var(--zinc-900);
            text-decoration: none;
            font-weight: 700;
        }

        .login-link a:hover {
            text-decoration: underline;
        }

        .pw-strength-bar {
            height: 4px;
            border-radius: 2px;
            background: var(--zinc-100);
            margin-top: 0.625rem;
            overflow: hidden;
        }

        .pw-strength-bar .fill {
            height: 100%;
            width: 0;
            border-radius: 2px;
            transition: width 0.3s, background 0.3s;
        }

        .text-danger {
            color: #ef4444 !important;
        }

        .form-text,
        .pw-hint {
            color: var(--zinc-500);
            font-size: 0.8125rem;
            margin-top: 0.5rem;
        }

        #prefixExample {
            color: var(--zinc-900);
            font-weight: 700;
        }

        /* ── Responsive ── */
        @media (max-width: 992px) {
            .onboard-wrapper {
                flex-direction: column;
            }

            .onboard-hero {
                height: auto;
                position: relative;
                padding: 5rem 2rem;
            }

            .onboard-form-side {
                padding: 4rem 1.5rem;
            }

            .onboard-hero h1 {
                font-size: 2.25rem;
            }

            .onboard-hero-content {
                max-width: 100%;
            }
        }
    </style>
</head>

<body>

    <div class="onboard-wrapper">

        <!-- Left Side: Hero -->
        <div class="onboard-side onboard-hero">
            <div class="onboard-hero-content">
                <div class="row g-3 align-items-center">
                    <div class="col-auto brand-icon">
                        <i class="fas fa-building"></i>
                    </div>
                    <div class="col">
                        <h1>Start Your Free Trial</h1>
                    </div>
                </div>

                <p>Set up your organisation in under 2 minutes &mdash; no credit card needed.</p>

                <!-- Trial Info Badge -->
                <div class="trial-badge">
                    <i class="fas fa-gift"></i>
                    <div class="trial-text">
                        Your trial includes <strong>6 months</strong> of full access with up to <strong>20
                            employees</strong>. Upgrade anytime.
                    </div>
                </div>
            </div>
        </div>

        <!-- Right Side: Form Side -->
        <div class="onboard-side onboard-form-side">
            <div class="onboard-card">

                <form id="onboardForm" class="row g-3" autocomplete="off">

                    <!-- ── Organisation Section ── -->
                    <div class="col-12">
                        <div class="section-label"><i class="fas fa-building"></i> Organisation Details</div>
                    </div>

                    <div class="col-md-6">
                        <label for="orgName" class="form-label">Organisation Name <span
                                class="text-danger">*</span></label>
                        <input type="text" id="orgName" class="form-control" placeholder="Acme Corp" required>
                    </div>

                    <div class="col-md-6">
                        <label for="prefix" class="form-label">Employee Code Prefix <span
                                class="text-danger">*</span></label>
                        <input type="text" id="prefix" class="form-control" placeholder="e.g. ACME" maxlength="6"
                            required style="text-transform:uppercase;">
                        <div class="form-text">Example code: <strong id="prefixExample">ACME001</strong></div>
                    </div>

                    <div class="col-md-6">
                        <label for="orgDomain" class="form-label">Domain <span class="text-danger">*</span></label>
                        <input type="text" id="orgDomain" class="form-control" placeholder="yourcompany.com" required>
                        <div class="form-text">Without http:// or www</div>
                    </div>

                    <div class="col-md-6">
                        <label for="orgTimeZone" class="form-label">Time Zone <span class="text-danger">*</span></label>
                        <select id="orgTimeZone" class="form-select" required>
                            <option value="">Select Time Zone</option>
                            <option value="Asia/Kolkata" selected>India Standard Time (IST)</option>
                            <option value="America/New_York">Eastern Time (ET)</option>
                            <option value="Europe/London">Greenwich Mean Time (GMT)</option>
                            <option value="Asia/Dubai">Gulf Standard Time (GST)</option>
                            <option value="Asia/Singapore">Singapore Time (SGT)</option>
                            <option value="America/Los_Angeles">Pacific Time (PT)</option>
                        </select>
                    </div>

                    <div class="col-md-6">
                        <label for="orgEmail" class="form-label">Contact Email <span
                                class="text-danger">*</span></label>
                        <input type="email" id="orgEmail" class="form-control" placeholder="hr@yourcompany.com"
                            required>
                    </div>

                    <div class="col-md-6">
                        <label for="orgPhone" class="form-label">Contact Phone</label>
                        <input type="text" id="orgPhone" class="form-control" placeholder="+91 98765 43210">
                    </div>

                    <div class="col-md-6">
                        <label for="logoFile" class="form-label">Upload Logo</label>
                        <input type="file" id="logoFile" class="form-control" accept="image/*">
                        <input type="hidden" id="logoUrl">
                        <div class="mt-2">
                            <img id="logoPreview" src="" alt="Logo preview"
                                style="max-height: 60px; display: none; border-radius: 8px; border: 1px solid var(--zinc-200);">
                        </div>
                    </div>

                    <!-- ── Admin Section ── -->
                    <div class="col-12">
                        <div class="section-label"><i class="fas fa-user-shield"></i> Admin Account</div>
                    </div>

                    <div class="col-md-6">
                        <label for="adminFullName" class="form-label">Full Name <span
                                class="text-danger">*</span></label>
                        <input type="text" id="adminFullName" class="form-control" placeholder="John Doe" required>
                    </div>

                    <div class="col-md-6">
                        <label for="adminEmail" class="form-label">Admin Email <span
                                class="text-danger">*</span></label>
                        <input type="email" id="adminEmail" class="form-control" placeholder="admin@yourcompany.com"
                            required>
                    </div>

                    <div class="col-md-6">
                        <label for="adminPassword" class="form-label">Password <span
                                class="text-danger">*</span></label>
                        <input type="password" id="adminPassword" class="form-control" placeholder="Min. 8 characters"
                            required minlength="8">
                        <div class="pw-strength-bar">
                            <div class="fill" id="pwFill"></div>
                        </div>
                        <div class="pw-hint" id="pwHint">Use at least 8 characters</div>
                    </div>

                    <div class="col-md-6">
                        <label for="confirmPassword" class="form-label">Confirm Password <span
                                class="text-danger">*</span></label>
                        <input type="password" id="confirmPassword" class="form-control" placeholder="Re-enter password"
                            required>
                    </div>

                    <!-- ── Submit ── -->
                    <div class="col-12 mt-2">
                        <button type="submit" class="btn-onboard" id="onboardBtn">
                            <span class="btn-text"><i class="fas fa-rocket me-2"></i>Create My Organisation</span>
                            <span class="btn-loader" style="display:none;">
                                <span class="spinner-border spinner-border-sm me-2" role="status"></span> Creating...
                            </span>
                        </button>
                    </div>

                </form>

                <div class="login-link">
                    Already have an account? <a href="/login">Sign in here</a>
                </div>
            </div>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="/js/public-onboard.js"></script>
</body>

</html>