<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="description" content="Document Verification - Verify your organisation documents to continue">
        <title>Document Verification | Sellspark HRMS</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="/plugins/bootstrap/css/bootstrap.min.css" />
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" />
        <link rel="stylesheet" href="/css/verification.css" />
    </head>

    <body>

        <div class="verify-page">

            <!-- ── Header ──────────────────────────────────────────── -->
            <header class="verify-header">
                <div class="header-left">
                    <i class="fas fa-shield-alt header-icon"></i>
                    <div>
                        <h1 id="orgNameTitle">Document Verification</h1>
                        <p>Complete KYC to activate your organisation</p>
                    </div>
                </div>
                <div class="header-right">
                    <span class="verified-counter" id="progressBadge">
                        <strong id="verifiedCountBadge">0</strong> / 4 verified
                    </span>
                    <a href="/logout" class="logout-link"><i class="fas fa-sign-out-alt"></i> Logout</a>
                </div>
            </header>

            <!-- ── Step Progress ───────────────────────────────────── -->
            <div class="step-bar">
                <div class="step-track">
                    <div class="step-fill" id="stepFill"></div>
                </div>
                <div class="step-dots">
                    <div class="step-dot active" data-step="1" id="dot1">
                        <span class="dot-circle"><i class="fas fa-id-card"></i></span>
                        <span class="dot-text">PAN</span>
                    </div>
                    <div class="step-dot" data-step="2" id="dot2">
                        <span class="dot-circle"><i class="fas fa-fingerprint"></i></span>
                        <span class="dot-text">Aadhaar</span>
                    </div>
                    <div class="step-dot" data-step="3" id="dot3">
                        <span class="dot-circle"><i class="fas fa-building"></i></span>
                        <span class="dot-text">GST</span>
                    </div>
                    <div class="step-dot" data-step="4" id="dot4">
                        <span class="dot-circle"><i class="fas fa-file-invoice"></i></span>
                        <span class="dot-text">TAN</span>
                    </div>
                </div>
            </div>

            <!-- ── Steps Container ─────────────────────────────────── -->
            <div class="steps-wrapper">

                <!-- ══ STEP 1: PAN ══════════════════════════════════ -->
                <div class="step-panel active" id="step1Panel" data-step="1">
                    <div class="step-card">
                        <h2><i class="fas fa-id-card text-warning me-2"></i>PAN Card Verification</h2>
                        <p class="text-muted">Verify your PAN via Sandbox API. Upload a scanned copy for records.</p>

                        <div class="alert alert-success verified-badge" id="panVerifiedBadge" style="display:none;">
                            <i class="fas fa-check-circle me-1"></i> PAN Verified
                        </div>

                        <form id="panForm" class="row g-3">
                            <div class="col-md-6">
                                <label for="panNumber" class="form-label">PAN Number <span
                                        class="text-danger">*</span></label>
                                <input type="text" id="panNumber" class="form-control" placeholder="ABCDE1234F"
                                    maxlength="10" required style="text-transform:uppercase;">
                                <small class="text-muted">Format: ABCDE1234F</small>
                            </div>
                            <div class="col-md-6">
                                <label for="panHolderName" class="form-label">Name as on PAN <span
                                        class="text-danger">*</span></label>
                                <input type="text" id="panHolderName" class="form-control" placeholder="Enter name"
                                    required>
                            </div>
                            <div class="col-md-6">
                                <label for="panDob" class="form-label">Date of Birth <span
                                        class="text-danger">*</span></label>
                                <input type="text" id="panDob" class="form-control" placeholder="DD/MM/YYYY">
                                <small class="text-muted">Optional — improves match accuracy</small>
                            </div>
                            <div class="col-md-6">
                                <label for="panFile" class="form-label">PAN Card Document <span
                                        class="text-danger">*</span></label>
                                <input type="file" id="panFile" class="form-control" accept=".pdf,.jpg,.jpeg,.png"
                                    required>
                                <small class="text-muted">PDF, JPG, PNG (max 5MB)</small>
                            </div>
                            <div class="col-12">
                                <div class="form-check mb-3">
                                    <input class="form-check-input consent-checkbox" type="checkbox" id="panConsent"
                                        data-target="#panSubmitBtn" required>
                                    <label class="form-check-label text-muted" for="panConsent"
                                        style="font-size: 0.85rem;">
                                        I have read and agree to the <a href="/terms-and-conditions"
                                            target="_blank">Terms and Condition</a> and consent to use my PAN details
                                        for
                                        verification.
                                    </label>
                                </div>
                                <button type="submit" class="btn btn-primary" id="panSubmitBtn" disabled>
                                    <span class="btn-text"><i class="fas fa-check me-1"></i> Verify PAN</span>
                                    <span class="btn-loader" style="display:none;"><i
                                            class="fas fa-spinner fa-spin me-1"></i> Verifying...</span>
                                </button>
                            </div>
                        </form>
                        <div class="result-panel" id="panResult" style="display:none;"></div>
                    </div>
                </div>

                <!-- ══ STEP 2: AADHAAR ══════════════════════════════ -->
                <div class="step-panel" id="step2Panel" data-step="2">
                    <div class="step-card">
                        <h2><i class="fas fa-fingerprint text-primary me-2"></i>Aadhaar Verification</h2>
                        <p class="text-muted">Verify via Aadhaar Offline KYC with OTP. Upload a scanned copy for
                            records.</p>

                        <div class="alert alert-success verified-badge" id="aadhaarVerifiedBadge" style="display:none;">
                            <i class="fas fa-check-circle me-1"></i> Aadhaar Verified
                        </div>

                        <!-- Phase 1: Enter Aadhaar + Upload + Get OTP -->
                        <form id="aadhaarOtpForm" class="row g-3">
                            <div class="col-md-6">
                                <label for="aadhaarNumber" class="form-label">Aadhaar Number <span
                                        class="text-danger">*</span></label>
                                <input type="text" id="aadhaarNumber" class="form-control" placeholder="123456789012"
                                    maxlength="12" required>
                                <small class="text-muted">12-digit Aadhaar number</small>
                            </div>
                            <div class="col-md-6">
                                <label for="aadhaarFile" class="form-label">Aadhaar Card Document <span
                                        class="text-danger">*</span></label>
                                <input type="file" id="aadhaarFile" class="form-control" accept=".pdf,.jpg,.jpeg,.png"
                                    required>
                                <small class="text-muted">PDF, JPG, PNG (max 5MB)</small>
                            </div>
                            <div class="col-12">
                                <div class="form-check mb-3">
                                    <input class="form-check-input consent-checkbox" type="checkbox" id="aadhaarConsent"
                                        data-target="#aadhaarOtpBtn" required>
                                    <label class="form-check-label text-muted" for="aadhaarConsent"
                                        style="font-size: 0.85rem;">
                                        I have read and agree to the <a href="/terms-and-conditions"
                                            target="_blank">Terms and Condition</a> and consent to use my Aadhaar
                                        details for verification.
                                    </label>
                                </div>
                                <button type="submit" class="btn btn-primary" id="aadhaarOtpBtn" disabled>
                                    <span class="btn-text"><i class="fas fa-paper-plane me-1"></i> Send OTP</span>
                                    <span class="btn-loader" style="display:none;"><i
                                            class="fas fa-spinner fa-spin me-1"></i> Sending...</span>
                                </button>
                            </div>
                        </form>

                        <!-- Phase 2: Enter OTP -->
                        <form id="aadhaarVerifyForm" style="display:none;" class="mt-3">
                            <div class="alert alert-info">
                                <i class="fas fa-mobile-alt me-1"></i> OTP sent to your Aadhaar-linked mobile number
                            </div>
                            <div class="otp-input-group" id="otpInputGroup">
                                <input type="text" class="otp-digit" maxlength="1" data-idx="0">
                                <input type="text" class="otp-digit" maxlength="1" data-idx="1">
                                <input type="text" class="otp-digit" maxlength="1" data-idx="2">
                                <input type="text" class="otp-digit" maxlength="1" data-idx="3">
                                <input type="text" class="otp-digit" maxlength="1" data-idx="4">
                                <input type="text" class="otp-digit" maxlength="1" data-idx="5">
                            </div>
                            <input type="hidden" id="aadhaarRefId">
                            <div class="mt-3 d-flex align-items-center gap-2">
                                <button type="submit" class="btn btn-primary" id="aadhaarVerifyBtn">
                                    <span class="btn-text"><i class="fas fa-check-double me-1"></i> Verify OTP</span>
                                    <span class="btn-loader" style="display:none;"><i
                                            class="fas fa-spinner fa-spin me-1"></i> Verifying...</span>
                                </button>
                                <button type="button" class="btn btn-outline-secondary btn-sm" id="resendOtpBtn"
                                    disabled>
                                    Resend OTP <span id="otpTimer">(30s)</span>
                                </button>
                            </div>
                        </form>

                        <div class="result-panel" id="aadhaarResult" style="display:none;"></div>
                    </div>
                </div>

                <!-- ══ STEP 3: GST ══════════════════════════════════ -->
                <div class="step-panel" id="step3Panel" data-step="3">
                    <div class="step-card">
                        <h2><i class="fas fa-building text-success me-2"></i>GST Verification</h2>
                        <p class="text-muted">Verify your GSTIN via Sandbox API. Business name will be matched. Upload
                            GST certificate.</p>

                        <div class="alert alert-success verified-badge" id="gstVerifiedBadge" style="display:none;">
                            <i class="fas fa-check-circle me-1"></i> GST Verified
                        </div>

                        <form id="gstForm" class="row g-3">
                            <div class="col-md-6">
                                <label for="gstinNumber" class="form-label">GSTIN <span
                                        class="text-danger">*</span></label>
                                <input type="text" id="gstinNumber" class="form-control" placeholder="22AAAAA0000A1Z5"
                                    maxlength="15" required style="text-transform:uppercase;">
                                <small class="text-muted">15-character GSTIN</small>
                            </div>
                            <div class="col-md-6">
                                <label for="gstFile" class="form-label">GST Certificate <span
                                        class="text-danger">*</span></label>
                                <input type="file" id="gstFile" class="form-control" accept=".pdf,.jpg,.jpeg,.png"
                                    required>
                                <small class="text-muted">PDF, JPG, PNG (max 5MB)</small>
                            </div>
                            <div class="col-12">
                                <div class="form-check mb-3">
                                    <input class="form-check-input consent-checkbox" type="checkbox" id="gstConsent"
                                        data-target="#gstSubmitBtn" required>
                                    <label class="form-check-label text-muted" for="gstConsent"
                                        style="font-size: 0.85rem;">
                                        I have read and agree to the <a href="/terms-and-conditions"
                                            target="_blank">Terms and Conditions</a> and consent to use my GST details
                                        for verification.
                                    </label>
                                </div>
                                <button type="submit" class="btn btn-primary" id="gstSubmitBtn" disabled>
                                    <span class="btn-text"><i class="fas fa-check me-1"></i> Verify GSTIN</span>
                                    <span class="btn-loader" style="display:none;"><i
                                            class="fas fa-spinner fa-spin me-1"></i> Verifying...</span>
                                </button>
                            </div>
                        </form>
                        <div class="result-panel" id="gstResult" style="display:none;"></div>
                    </div>
                </div>

                <!-- ══ STEP 4: TAN ══════════════════════════════════ -->
                <div class="step-panel" id="step4Panel" data-step="4">
                    <div class="step-card">
                        <h2><i class="fas fa-file-invoice text-info me-2"></i>TAN Verification</h2>
                        <p class="text-muted">Upload your TAN certificate for admin review. No automated API available.
                        </p>

                        <div class="alert alert-success verified-badge" id="tanVerifiedBadge" style="display:none;">
                            <i class="fas fa-check-circle me-1"></i> TAN Verified
                        </div>
                        <div class="alert alert-warning pending-badge" id="tanPendingBadge" style="display:none;">
                            <i class="fas fa-clock me-1"></i> Pending Admin Review
                        </div>

                        <form id="tanForm" class="row g-3" enctype="multipart/form-data">
                            <div class="col-md-6">
                                <label for="tanNumber" class="form-label">TAN Number <span
                                        class="text-danger">*</span></label>
                                <input type="text" id="tanNumber" class="form-control" placeholder="AAAA99999A"
                                    maxlength="10" required style="text-transform:uppercase;">
                                <small class="text-muted">Format: AAAA99999A</small>
                            </div>
                            <div class="col-md-6">
                                <label for="tanFile" class="form-label">TAN Certificate <span
                                        class="text-danger">*</span></label>
                                <input type="file" id="tanFile" class="form-control" accept=".pdf,.jpg,.jpeg,.png"
                                    required>
                                <small class="text-muted">PDF, JPG, PNG (max 5MB)</small>
                            </div>
                            <div class="col-12">
                                <div class="form-check mb-3">
                                    <input class="form-check-input consent-checkbox" type="checkbox" id="tanConsent"
                                        data-target="#tanSubmitBtn" required>
                                    <label class="form-check-label text-muted" for="tanConsent"
                                        style="font-size: 0.85rem;">
                                        I have read and agree to the <a href="/terms-and-conditions"
                                            target="_blank">Terms and Conditions</a> and consent to use my TAN details
                                        for verification.
                                    </label>
                                </div>
                                <button type="submit" class="btn btn-primary" id="tanSubmitBtn" disabled>
                                    <span class="btn-text"><i class="fas fa-upload me-1"></i> Upload for Review</span>
                                    <span class="btn-loader" style="display:none;"><i
                                            class="fas fa-spinner fa-spin me-1"></i> Uploading...</span>
                                </button>
                            </div>
                        </form>
                        <div class="result-panel" id="tanResult" style="display:none;"></div>
                    </div>
                </div>

            </div><!-- /steps-wrapper -->

            <!-- ── Step Navigation ─────────────────────────────────── -->
            <div class="step-nav">
                <button type="button" class="btn btn-outline-secondary" id="prevStepBtn" style="display:none;">
                    <i class="fas fa-chevron-left me-1"></i> Previous
                </button>
                <span class="step-counter" id="stepCounter">Step 1 of 4</span>
                <button type="button" class="btn btn-outline-primary" id="nextStepBtn">
                    Next <i class="fas fa-chevron-right ms-1"></i>
                </button>
            </div>

            <!-- ── Completion Overlay ──────────────────────────────── -->
            <div class="completion-overlay" id="completionPanel" style="display:none;">
                <div class="completion-box">
                    <i class="fas fa-check-circle text-success completion-icon"></i>
                    <h2>Verification Complete!</h2>
                    <p class="text-muted">Your organisation documents are verified. You can now access the platform.</p>
                    <a href="/org/dashboard" class="btn btn-success btn-lg" id="continueToDashboard">
                        Continue to Dashboard <i class="fas fa-arrow-right ms-1"></i>
                    </a>
                </div>
            </div>

        </div><!-- /verify-page -->

        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <script src="/plugins/bootstrap/js/bootstrap.bundle.min.js"></script>
        <script src="/js/verification/doc-verification.js"></script>
    </body>

    </html>