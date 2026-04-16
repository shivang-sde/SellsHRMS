<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description"
        content="Register your organisation on Sellspark HRMS - Free 6 month trial with up to 20 employees">
    <title>Start Your Free Trial | Sellspark HRMS</title>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="/plugins/bootstrap/css/bootstrap.min.css" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" />

    <style>
        *,
        *::before,
        *::after {
            box-sizing: border-box;
        }

        body {
            font-family: 'Inter', sans-serif;
            margin: 0;
            min-height: 100vh;
            background: #f4f7f6;
            /* Off-white background */
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 2rem 1rem;
        }

        .onboard-container {
            width: 100%;
            max-width: 680px;
            animation: slideUp 0.6s cubic-bezier(0.16, 1, 0.3, 1);
        }

        @keyframes slideUp {
            from {
                opacity: 0;
                transform: translateY(30px);
            }

            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        /* ── Header ──────────────────────── */
        .onboard-header {
            text-align: center;
            margin-bottom: 2rem;
        }

        .onboard-header .brand-icon {
            width: 56px;
            height: 56px;
            background: linear-gradient(135deg, #4f46e5, #3b82f6);
            border-radius: 16px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            color: #fff;
            font-size: 1.6rem;
            margin-bottom: 1rem;
            box-shadow: 0 8px 24px rgba(59, 130, 246, 0.25);
        }

        .onboard-header h1 {
            color: #1e293b;
            font-size: 1.75rem;
            font-weight: 700;
            margin: 0 0 0.35rem;
        }

        .onboard-header p {
            color: #64748b;
            font-size: 0.92rem;
            margin: 0;
        }

        /* ── Card ────────────────────────── */
        .onboard-card {
            background: #ffffff;
            border: 1px solid #e2e8f0;
            border-radius: 20px;
            padding: 2.5rem;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.05);
        }

        .section-label {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            color: #334155;
            font-weight: 600;
            font-size: 0.85rem;
            text-transform: uppercase;
            letter-spacing: 0.06em;
            margin-bottom: 1.25rem;
            padding-bottom: 0.5rem;
            border-bottom: 1px solid #f1f5f9;
        }

        .section-label i {
            color: #4f46e5;
        }

        .section-label+.section-label {
            margin-top: 1.5rem;
        }

        /* ── Form fields ─────────────────── */
        .form-label {
            color: #475569;
            font-size: 0.85rem;
            font-weight: 500;
            margin-bottom: 0.4rem;
        }

        .form-control,
        .form-select {
            background: #f8fafc;
            border: 1px solid #cbd5e1;
            color: #1e293b;
            border-radius: 10px;
            padding: 0.6rem 0.85rem;
            font-size: 0.95rem;
            transition: border-color 0.2s, box-shadow 0.2s, background 0.2s;
        }

        .form-control::placeholder {
            color: #94a3b8;
        }

        .form-control:focus,
        .form-select:focus {
            background: #ffffff;
            border-color: #6366f1;
            box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.2);
            color: #1e293b;
            outline: none;
        }

        .form-select option {
            background: #ffffff;
            color: #1e293b;
        }

        .form-text {
            color: #64748b;
            font-size: 0.8rem;
            margin-top: 0.35rem;
        }

        .text-danger {
            color: #ef4444 !important;
        }

        /* ── Password strength ───────────── */
        .pw-strength-bar {
            height: 4px;
            border-radius: 2px;
            background: #e2e8f0;
            margin-top: 0.5rem;
            overflow: hidden;
        }

        .pw-strength-bar .fill {
            height: 100%;
            width: 0;
            border-radius: 2px;
            transition: width 0.3s, background 0.3s;
        }

        .pw-hint {
            color: #64748b;
            font-size: 0.78rem;
            margin-top: 0.35rem;
        }

        /* ── Trial badge ─────────────────── */
        .trial-badge {
            display: flex;
            align-items: center;
            gap: 0.75rem;
            background: #eff6ff;
            border: 1px solid #bfdbfe;
            border-radius: 12px;
            padding: 1rem 1.25rem;
            margin-bottom: 1.75rem;
        }

        .trial-badge i {
            color: #3b82f6;
            font-size: 1.4rem;
        }

        .trial-badge .trial-text {
            font-size: 0.85rem;
            color: #1e40af;
            line-height: 1.5;
        }

        .trial-badge .trial-text strong {
            color: #1d4ed8;
            font-weight: 600;
        }

        /* ── Submit button ───────────────── */
        .btn-onboard {
            width: 100%;
            padding: 0.85rem;
            background: linear-gradient(135deg, #4f46e5, #3b82f6);
            border: none;
            border-radius: 12px;
            color: #fff;
            font-weight: 600;
            font-size: 1rem;
            cursor: pointer;
            transition: transform 0.2s, box-shadow 0.2s, opacity 0.2s;
            position: relative;
        }

        .btn-onboard:hover:not(:disabled) {
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(59, 130, 246, 0.3);
        }

        .btn-onboard:active:not(:disabled) {
            transform: translateY(0);
        }

        .btn-onboard:disabled {
            opacity: 0.65;
            cursor: not-allowed;
        }

        .btn-onboard .spinner-border {
            width: 1rem;
            height: 1rem;
            border-width: 2px;
        }

        /* ── Footer link ─────────────────── */
        .login-link {
            text-align: center;
            margin-top: 1.75rem;
            color: #64748b;
            font-size: 0.9rem;
        }

        .login-link a {
            color: #4f46e5;
            text-decoration: none;
            font-weight: 600;
        }

        .login-link a:hover {
            text-decoration: underline;
            color: #3730a3;
        }

        /* ── Toast ────────────────────────── */
        .onboard-toast {
            position: fixed;
            top: 1.5rem;
            right: 1.5rem;
            min-width: 300px;
            padding: 1rem 1.25rem;
            border-radius: 12px;
            color: #fff;
            font-size: 0.9rem;
            font-weight: 500;
            z-index: 9999;
            animation: toastIn 0.35s cubic-bezier(0.175, 0.885, 0.32, 1.275);
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
        }

        .onboard-toast.success {
            background: #10b981;
        }

        .onboard-toast.error {
            background: #ef4444;
        }

        @keyframes toastIn {
            from {
                opacity: 0;
                transform: translateX(30px);
            }

            to {
                opacity: 1;
                transform: translateX(0);
            }
        }

        /* ── Prefix example ──────────────── */
        #prefixExample {
            color: #4f46e5;
            font-weight: 600;
        }

        /* ── Responsive ──────────────────── */
        @media (max-width: 576px) {
            .onboard-card {
                padding: 1.5rem;
                border-radius: 16px;
            }

            .onboard-header h1 {
                font-size: 1.4rem;
            }

            body {
                padding: 1.5rem 1rem;
            }
        }
    </style>
</head>

<body>

    <div class="onboard-container">

        <!-- Header -->
        <div class="onboard-header">
            <div class="brand-icon"><i class="fas fa-building"></i></div>
            <h1>Start Your Free Trial</h1>
            <p>Set up your organisation in under 2 minutes &mdash; no credit card needed.</p>
        </div>

        <!-- Card -->
        <div class="onboard-card">

            <!-- Trial Info -->
            <div class="trial-badge">
                <i class="fas fa-gift"></i>
                <div class="trial-text">
                    Your trial includes <strong>6 months</strong> of full access with up to <strong>20
                        employees</strong>. Upgrade anytime.
                </div>
            </div>

            <form id="onboardForm" class="row g-3" autocomplete="off">

                <!-- ── Organisation Section ── -->
                <div class="col-12">
                    <div class="section-label"><i class="fas fa-building"></i> Organisation Details</div>
                </div>

                <div class="col-md-6">
                    <label for="orgName" class="form-label">Organisation Name <span class="text-danger">*</span></label>
                    <input type="text" id="orgName" class="form-control" placeholder="Acme Corp" required>
                </div>

                <div class="col-md-6">
                    <label for="prefix" class="form-label">Employee Code Prefix <span
                            class="text-danger">*</span></label>
                    <input type="text" id="prefix" class="form-control" placeholder="e.g. ACME" maxlength="6" required
                        style="text-transform:uppercase;">
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
                    <label for="orgEmail" class="form-label">Contact Email <span class="text-danger">*</span></label>
                    <input type="email" id="orgEmail" class="form-control" placeholder="hr@yourcompany.com" required>
                </div>

                <div class="col-md-6">
                    <label for="orgPhone" class="form-label">Contact Phone</label>
                    <input type="text" id="orgPhone" class="form-control" placeholder="+91 98765 43210">
                </div>

                <div class="col-md-6">
                    <label for="logoFile" class="form-label">Upload Logo</label>
                    <input type="file" id="logoFile" class="form-control" accept="image/*">
                    <input type="hidden" id="logoUrl">
                    <div class="mb-2">
                        <img id="logoPreview" src="" alt="Logo preview"
                            style="max-height: 80px; display: none; border-radius: 8px; border: 1px solid #e2e8f0;">
                    </div>
                </div>

                <!-- ── Admin Section ── -->
                <div class="col-12 mt-2">
                    <div class="section-label"><i class="fas fa-user-shield"></i> Admin Account</div>
                </div>

                <div class="col-md-6">
                    <label for="adminFullName" class="form-label">Full Name <span class="text-danger">*</span></label>
                    <input type="text" id="adminFullName" class="form-control" placeholder="John Doe" required>
                </div>

                <div class="col-md-6">
                    <label for="adminEmail" class="form-label">Admin Email <span class="text-danger">*</span></label>
                    <input type="email" id="adminEmail" class="form-control" placeholder="admin@yourcompany.com"
                        required>
                </div>

                <div class="col-md-6">
                    <label for="adminPassword" class="form-label">Password <span class="text-danger">*</span></label>
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
                <div class="col-12 mt-3">
                    <button type="submit" class="btn-onboard" id="onboardBtn">
                        <span class="btn-text"><i class="fas fa-rocket me-2"></i>Create My Organisation</span>
                        <span class="btn-loader" style="display:none;">
                            <span class="spinner-border spinner-border-sm me-2" role="status"></span> Creating...
                        </span>
                    </button>
                </div>

            </form>
        </div>

        <div class="login-link">
            Already have an account? <a href="/login">Sign in here</a>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="/js/public-onboard.js"></script>
</body>

</html>