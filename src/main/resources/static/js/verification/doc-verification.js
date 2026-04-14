/**
 * Document Verification Wizard — Frontend Logic
 * All steps now upload documents (multipart) alongside API verification.
 */
$(document).ready(function () {
    'use strict';

    let currentStep = 1;
    const TOTAL_STEPS = 4;
    let verificationStatus = {};
    let aadhaarRefId = '';
    let otpTimerInterval = null;

    // Init
    loadVerificationStatus();

    // ═══════════════════════════════════════════════════════
    //  LOAD STATUS
    // ═══════════════════════════════════════════════════════

    function loadVerificationStatus() {
        $.get('/api/verify/status', function (res) {
            if (res.success && res.data) {
                verificationStatus = res.data;
                applyStatus(res.data);
            }
        }).fail(function () {
            showToast('error', 'Failed to load verification status');
        });
    }

    function applyStatus(s) {
        $('#verifiedCountBadge').text(s.verifiedCount);

        if (s.panVerified) markCompleted(1);
        if (s.aadhaarVerified) markCompleted(2);
        if (s.gstVerified) markCompleted(3);
        if (s.tanVerified) markCompleted(4);

        // Pre-fill
        if (s.pan) $('#panNumber').val(s.pan);
        if (s.gst) $('#gstinNumber').val(s.gst);
        if (s.tan) $('#tanNumber').val(s.tan);

        // Disable verified steps
        if (s.panVerified) lockStep('pan', '#panVerifiedBadge', '#panForm', '#panSubmitBtn');
        if (s.aadhaarVerified) lockStep('aadhaar', '#aadhaarVerifiedBadge', '#aadhaarOtpForm', '#aadhaarOtpBtn');
        if (s.gstVerified) lockStep('gst', '#gstVerifiedBadge', '#gstForm', '#gstSubmitBtn');
        if (s.tanVerified) {
            lockStep('tan', '#tanVerifiedBadge', '#tanForm', '#tanSubmitBtn');
        } else if (s.tanUrl) {
            $('#tanPendingBadge').show();
        }

        // Navigate to first unverified
        if (!s.panVerified) goToStep(1);
        else if (!s.aadhaarVerified) goToStep(2);
        else if (!s.gstVerified) goToStep(3);
        else if (!s.tanVerified) goToStep(4);
        else checkCompletion();

        if (s.verifiedCount >= 2) showContinueBtn();
    }

    function lockStep(name, badgeSel, formSel, btnSel) {
        $(badgeSel).show();
        $(formSel).find('input').prop('disabled', true);
        $(btnSel).prop('disabled', true).find('.btn-text').html('<i class="fas fa-check me-1"></i> Verified');
    }

    // ═══════════════════════════════════════════════════════
    //  STEP 1: PAN (multipart: pan, name, dob, file)
    // ═══════════════════════════════════════════════════════

    $('#panForm').on('submit', function (e) {
        e.preventDefault();
        if (verificationStatus.panVerified) return;

        var pan = $('#panNumber').val().trim().toUpperCase();
        var name = $('#panHolderName').val().trim();
        var dob = $('#panDob').val().trim();
        var file = $('#panFile')[0].files[0];

        if (!pan || !name) return showToast('error', 'PAN and name are required');
        if (!file) return showToast('error', 'Please upload PAN card document');

        var fd = new FormData();
        fd.append('pan', pan);
        fd.append('name', name);
        fd.append('dateOfBirth', dob);
        fd.append('file', file);

        setLoading('#panSubmitBtn', true);
        $.ajax({
            url: '/api/verify/pan', method: 'POST',
            data: fd, processData: false, contentType: false,
            success: function (res) {
                setLoading('#panSubmitBtn', false);
                var d = res.data;
                if (d && d.verified) {
                    showResult('#panResult', 'success', d);
                    markCompleted(1);
                    verificationStatus.panVerified = true;
                    verificationStatus.verifiedCount++;
                    updateCount();
                    lockStep('pan', '#panVerifiedBadge', '#panForm', '#panSubmitBtn');
                    showToast('success', 'PAN verified!');
                    setTimeout(function () { goToStep(2); }, 1200);
                } else {
                    showResult('#panResult', 'error', d);
                    showToast('error', d ? d.message : 'PAN verification failed');
                }
            },
            error: function () { setLoading('#panSubmitBtn', false); showToast('error', 'Request failed'); }
        });
    });

    // ═══════════════════════════════════════════════════════
    //  STEP 2: AADHAAR (OTP flow + file upload)
    // ═══════════════════════════════════════════════════════

    // Phase 1: Send OTP + upload document
    $('#aadhaarOtpForm').on('submit', function (e) {
        e.preventDefault();
        if (verificationStatus.aadhaarVerified) return;

        var aadhaar = $('#aadhaarNumber').val().trim();
        var file = $('#aadhaarFile')[0].files[0];

        if (!aadhaar || aadhaar.length !== 12) return showToast('error', 'Enter valid 12-digit Aadhaar');
        if (!file) return showToast('error', 'Please upload Aadhaar card document');

        var fd = new FormData();
        fd.append('aadhaarNumber', aadhaar);
        fd.append('file', file);

        setLoading('#aadhaarOtpBtn', true);
        $.ajax({
            url: '/api/verify/aadhaar/otp', method: 'POST',
            data: fd, processData: false, contentType: false,
            success: function (res) {
                setLoading('#aadhaarOtpBtn', false);
                var d = res.data;
                if (d && d.refId) {
                    aadhaarRefId = d.refId;
                    $('#aadhaarRefId').val(d.refId);
                    $('#aadhaarOtpForm').slideUp(250);
                    setTimeout(function () {
                        $('#aadhaarVerifyForm').slideDown(250);
                        $('.otp-digit').first().focus();
                    }, 300);
                    startOtpTimer();
                    showToast('info', 'OTP sent to Aadhaar-linked mobile');
                } else {
                    showToast('error', d ? d.message : 'OTP generation failed');
                }
            },
            error: function () { setLoading('#aadhaarOtpBtn', false); showToast('error', 'Request failed'); }
        });
    });

    // Phase 2: Verify OTP
    $('#aadhaarVerifyForm').on('submit', function (e) {
        e.preventDefault();
        var otp = '';
        $('.otp-digit').each(function () { otp += $(this).val(); });
        if (otp.length !== 6) return showToast('error', 'Enter all 6 OTP digits');

        setLoading('#aadhaarVerifyBtn', true);
        $.ajax({
            url: '/api/verify/aadhaar/verify', method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ otp: otp, refId: aadhaarRefId }),
            success: function (res) {
                setLoading('#aadhaarVerifyBtn', false);
                var d = res.data;
                if (d && d.verified) {
                    showResult('#aadhaarResult', 'success', d);
                    markCompleted(2);
                    verificationStatus.aadhaarVerified = true;
                    verificationStatus.verifiedCount++;
                    updateCount();
                    $('#aadhaarVerifiedBadge').show();
                    $('#aadhaarVerifyForm').slideUp(200);
                    clearInterval(otpTimerInterval);
                    showToast('success', 'Aadhaar verified!');
                    setTimeout(function () { goToStep(3); }, 1200);
                } else {
                    showToast('error', d ? d.message : 'OTP verification failed');
                    $('.otp-digit').val('');
                    $('.otp-digit').first().focus();
                }
            },
            error: function () { setLoading('#aadhaarVerifyBtn', false); showToast('error', 'Request failed'); }
        });
    });

    // OTP digit navigation
    $(document).on('input', '.otp-digit', function () {
        if ($(this).val().length === 1) $(this).next('.otp-digit').focus();
    });
    $(document).on('keydown', '.otp-digit', function (e) {
        if (e.key === 'Backspace' && !$(this).val()) $(this).prev('.otp-digit').focus();
    });

    $('#resendOtpBtn').on('click', function () {
        if ($(this).prop('disabled')) return;
        $('#aadhaarOtpForm').trigger('submit');
        startOtpTimer();
    });

    function startOtpTimer() {
        var sec = 30;
        $('#resendOtpBtn').prop('disabled', true);
        clearInterval(otpTimerInterval);
        otpTimerInterval = setInterval(function () {
            sec--;
            $('#otpTimer').text('(' + sec + 's)');
            if (sec <= 0) {
                clearInterval(otpTimerInterval);
                $('#resendOtpBtn').prop('disabled', false);
                $('#otpTimer').text('');
            }
        }, 1000);
    }

    // ═══════════════════════════════════════════════════════
    //  STEP 3: GST (multipart: gstin, file)
    // ═══════════════════════════════════════════════════════

    $('#gstForm').on('submit', function (e) {
        e.preventDefault();
        if (verificationStatus.gstVerified) return;

        var gstin = $('#gstinNumber').val().trim().toUpperCase();
        var file = $('#gstFile')[0].files[0];

        if (!gstin || gstin.length !== 15) return showToast('error', 'Enter valid 15-char GSTIN');
        if (!file) return showToast('error', 'Please upload GST certificate');

        var fd = new FormData();
        fd.append('gstin', gstin);
        fd.append('file', file);

        setLoading('#gstSubmitBtn', true);
        $.ajax({
            url: '/api/verify/gst', method: 'POST',
            data: fd, processData: false, contentType: false,
            success: function (res) {
                setLoading('#gstSubmitBtn', false);
                var d = res.data;
                if (d && d.verified) {
                    showResult('#gstResult', 'success', d);
                    markCompleted(3);
                    verificationStatus.gstVerified = true;
                    verificationStatus.verifiedCount++;
                    updateCount();
                    lockStep('gst', '#gstVerifiedBadge', '#gstForm', '#gstSubmitBtn');
                    showToast('success', 'GST verified!');
                    setTimeout(function () { goToStep(4); }, 1200);
                } else {
                    showResult('#gstResult', 'error', d);
                    showToast('error', d ? d.message : 'GST verification failed');
                }
            },
            error: function () { setLoading('#gstSubmitBtn', false); showToast('error', 'Request failed'); }
        });
    });

    // ═══════════════════════════════════════════════════════
    //  STEP 4: TAN (multipart: tan, file)
    // ═══════════════════════════════════════════════════════

    $('#tanForm').on('submit', function (e) {
        e.preventDefault();
        if (verificationStatus.tanVerified) return;

        var tan = $('#tanNumber').val().trim().toUpperCase();
        var file = $('#tanFile')[0].files[0];

        if (!tan) return showToast('error', 'TAN number is required');
        if (!file) return showToast('error', 'Please upload TAN certificate');

        var fd = new FormData();
        fd.append('tan', tan);
        fd.append('file', file);

        setLoading('#tanSubmitBtn', true);
        $.ajax({
            url: '/api/verify/tan', method: 'POST',
            data: fd, processData: false, contentType: false,
            success: function (res) {
                setLoading('#tanSubmitBtn', false);
                showToast('success', 'TAN document uploaded for admin review');
                $('#tanPendingBadge').show();
                showResult('#tanResult', 'success', res.data);
                checkCompletion();
            },
            error: function () { setLoading('#tanSubmitBtn', false); showToast('error', 'Upload failed'); }
        });
    });

    // ═══════════════════════════════════════════════════════
    //  NAVIGATION
    // ═══════════════════════════════════════════════════════

    function goToStep(step) {
        if (step < 1 || step > TOTAL_STEPS) return;
        currentStep = step;
        $('.step-panel').removeClass('active');
        $('#step' + step + 'Panel').addClass('active');
        $('.step-dot').removeClass('active');
        $('#dot' + step).addClass('active');
        $('#stepFill').css('width', ((step - 1) / (TOTAL_STEPS - 1) * 100) + '%');
        $('#stepCounter').text('Step ' + step + ' of ' + TOTAL_STEPS);
        $('#prevStepBtn').toggle(step > 1);
        $('#nextStepBtn').toggle(step < TOTAL_STEPS);
    }

    $('#prevStepBtn').on('click', function () { goToStep(currentStep - 1); });
    $('#nextStepBtn').on('click', function () { goToStep(currentStep + 1); });
    $('.step-dot').on('click', function () { goToStep(parseInt($(this).data('step'))); });

    function markCompleted(step) { $('#dot' + step).addClass('completed'); }

    function updateCount() {
        var c = verificationStatus.verifiedCount || 0;
        $('#verifiedCountBadge').text(c);
        if (c >= 2) showContinueBtn();
    }

    function showContinueBtn() {
        if ($('#skipBtn').length === 0) {
            var $b = $('<a href="/org/dashboard" class="btn btn-success btn-sm" id="skipBtn"><i class="fas fa-check-circle me-1"></i>Continue to Dashboard</a>');
            $('.step-nav').append($b);
        }
    }

    function checkCompletion() {
        var c = verificationStatus.verifiedCount || 0;
        if (c >= 4) {
            setTimeout(function () { $('#completionPanel').fadeIn(300); }, 600);
        } else if (c >= 2) {
            showContinueBtn();
        }
    }

    // ═══════════════════════════════════════════════════════
    //  RESULTS & UTILS
    // ═══════════════════════════════════════════════════════

    function showResult(sel, type, data) {
        var $p = $(sel);
        $p.attr('class', 'result-panel ' + type).show();
        var html = '';
        if (data && data.details) {
            $.each(data.details, function (k, v) {
                if (v && v.toString().trim()) {
                    html += '<div class="result-row"><span class="result-key">' + fmtKey(k) +
                            '</span><span class="result-value">' + v + '</span></div>';
                }
            });
        }
        if (data && data.transactionId) {
            html += '<div class="result-row"><span class="result-key">Transaction ID</span>' +
                    '<span class="result-value" style="font-size:0.75rem;opacity:0.6;">' + data.transactionId + '</span></div>';
        }
        if (data && data.nameMatchScore != null) {
            var c = data.nameMatchScore >= 70 ? 'color:#166534' : 'color:#991b1b';
            html += '<div class="result-row"><span class="result-key">Name Match</span>' +
                    '<span class="result-value" style="' + c + '">' + data.nameMatchScore + '/100</span></div>';
        }
        $p.html(html || '<div style="text-align:center;">' + (data ? data.message : 'No details') + '</div>');
    }

    function fmtKey(k) {
        return k.replace(/([A-Z])/g, ' $1').replace(/^./, function (s) { return s.toUpperCase(); }).replace(/_/g, ' ');
    }

    function setLoading(sel, on) {
        var $b = $(sel);
        $b.prop('disabled', on);
        $b.find('.btn-text').toggle(!on);
        $b.find('.btn-loader').toggle(on);
    }

    function showToast(type, msg) {
        $('.verify-toast').remove();
        var icon = type === 'success' ? 'fa-check-circle' : type === 'error' ? 'fa-exclamation-circle' : 'fa-info-circle';
        var $t = $('<div class="verify-toast ' + type + '"><i class="fas ' + icon + ' me-2"></i>' + msg + '</div>');
        $('body').append($t);
        setTimeout(function () { $t.fadeOut(300, function () { $t.remove(); }); }, 4000);
    }
});
