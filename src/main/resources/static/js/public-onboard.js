/**
 * Public Onboarding — Frontend Logic
 * POST /api/public/onboard (no auth required)
 */
$(document).ready(function () {
    'use strict';

    // ═══════════════════════════════════════════════════════
    //  PREFIX PREVIEW
    // ═══════════════════════════════════════════════════════

    $('#prefix').on('input', function () {
        var v = $(this).val().toUpperCase();
        $(this).val(v);
        $('#prefixExample').text(v ? v + '001' : 'ACME001');
    });

    // ═══════════════════════════════════════════════════════
    //  PREFIX UNIQUENESS CHECK (debounced)
    // ═══════════════════════════════════════════════════════

    var prefixTimer = null;
    $('#prefix').on('input', function () {
        var el = $(this), val = el.val().trim();
        clearTimeout(prefixTimer);
        if (val.length < 2) return;
        prefixTimer = setTimeout(function () {
            $.get('/api/public/prefix/' + val, function (exists) {
                if (exists) {
                    el.addClass('is-invalid');
                    showToast('error', 'Prefix "' + val + '" is already taken');
                } else {
                    el.removeClass('is-invalid');
                }
            });
        }, 500);
    });

    // ═══════════════════════════════════════════════════════
    //  PASSWORD STRENGTH METER
    // ═══════════════════════════════════════════════════════

    $('#adminPassword').on('input', function () {
        var pw = $(this).val();
        var score = 0;
        if (pw.length >= 8) score++;
        if (/[A-Z]/.test(pw)) score++;
        if (/[0-9]/.test(pw)) score++;
        if (/[^A-Za-z0-9]/.test(pw)) score++;

        var pct = (score / 4) * 100;
        var color = score <= 1 ? '#ef4444' : score === 2 ? '#f59e0b' : score === 3 ? '#3b82f6' : '#10b981';
        var hints = ['Very weak', 'Weak', 'Fair', 'Good', 'Strong'];
        $('#pwFill').css({ width: pct + '%', background: color });
        $('#pwHint').text(hints[score]);
    });

    // ═══════════════════════════════════════════════════════
    //  LOGO UPLOAD
    // ═══════════════════════════════════════════════════════

    $('#logoFile').on('change', function () {
        var file = this.files[0];
        if (!file) return;

        var formData = new FormData();
        formData.append('file', file);

        var $input = $(this);
        $input.prop('disabled', true);

        $.ajax({
            url: '/api/public/upload-logo',
            method: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function (res) {
                $input.prop('disabled', false);
                if (res.url) {
                    $('#logoUrl').val(res.url);
                    $('#logoPreview').attr('src', res.url).show();
                }
            },
            error: function () {
                $input.prop('disabled', false);
                $input.val('');
                showToast('error', 'Failed to upload logo. Please try again.');
            }
        });
    });

    // ═══════════════════════════════════════════════════════
    //  FORM SUBMIT
    // ═══════════════════════════════════════════════════════

    $('#onboardForm').on('submit', function (e) {
        e.preventDefault();

        var orgName = $.trim($('#orgName').val());
        var prefix = $.trim($('#prefix').val()).toUpperCase();
        var domain = $.trim($('#orgDomain').val());
        var timeZone = $('#orgTimeZone').val();
        var contactEmail = $.trim($('#orgEmail').val());
        var contactPhone = $.trim($('#orgPhone').val());
        var adminFullName = $.trim($('#adminFullName').val());
        var adminEmail = $.trim($('#adminEmail').val());
        var adminPassword = $('#adminPassword').val();
        var confirmPassword = $('#confirmPassword').val();

        // Validations
        if (!orgName || !prefix || !domain || !timeZone || !contactEmail || !adminFullName || !adminEmail || !adminPassword) {
            return showToast('error', 'Please fill in all required fields');
        }

        if (prefix.length < 2 || prefix.length > 6) {
            return showToast('error', 'Prefix must be 2-6 characters');
        }

        if ($('#prefix').hasClass('is-invalid')) {
            return showToast('error', 'Please choose a different prefix — this one is taken');
        }

        if (adminPassword.length < 8) {
            return showToast('error', 'Password must be at least 8 characters');
        }

        if (adminPassword !== confirmPassword) {
            return showToast('error', 'Passwords do not match');
        }

        var payload = {
            name: orgName,
            prefix: prefix,
            padding: 3,
            domain: domain,
            timeZone: timeZone,
            contactEmail: contactEmail,
            contactPhone: contactPhone,
            adminFullName: adminFullName,
            adminEmail: adminEmail,
            adminPassword: adminPassword,
            logoUrl: $('#logoUrl').val()
        };

        setLoading(true);

        $.ajax({
            url: '/api/public/onboard',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(payload),
            success: function (res) {
                setLoading(false);
                showToast('success', 'Organisation created successfully! Redirecting to login...');
                setTimeout(function () {
                    window.location.href = '/login?onboarded=true';
                }, 1800);
            },
            error: function (xhr) {
                setLoading(false);
                var msg = 'Something went wrong. Please try again.';
                if (xhr.responseJSON && xhr.responseJSON.message) {
                    msg = xhr.responseJSON.message;
                } else if (xhr.responseJSON && xhr.responseJSON.error) {
                    msg = xhr.responseJSON.error;
                }
                showToast('error', msg);
            }
        });
    });

    // ═══════════════════════════════════════════════════════
    //  UTILS
    // ═══════════════════════════════════════════════════════

    function setLoading(on) {
        var $btn = $('#onboardBtn');
        $btn.prop('disabled', on);
        $btn.find('.btn-text').toggle(!on);
        $btn.find('.btn-loader').toggle(on);
    }

    function showToast(type, msg) {
        $('.onboard-toast').remove();
        var icon = type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle';
        var $t = $('<div class="onboard-toast ' + type + '"><i class="fas ' + icon + ' me-2"></i>' + msg + '</div>');
        $('body').append($t);
        setTimeout(function () { $t.fadeOut(300, function () { $t.remove(); }); }, 4000);
    }
});
