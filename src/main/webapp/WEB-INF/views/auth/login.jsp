<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Login | SellsHRMS</title>
            <!-- Google Fonts: Inter -->
            <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap"
                rel="stylesheet">
            <!-- Font Awesome -->
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
            <!-- Bootstrap 5.3 -->
            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">

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
                }

                body {
                    font-family: 'Inter', sans-serif;
                    background-color: var(--zinc-50);
                    color: var(--zinc-900);
                    min-height: 100vh;
                    margin: 0;
                }

                .auth-wrapper {
                    display: flex;
                    min-height: 100vh;
                    width: 100%;
                }

                /* Left Side: Illustration & Branding */
                .auth-hero {
                    flex: 1;
                    background-color: var(--zinc-100);
                    display: flex;
                    flex-direction: column;
                    justify-content: center;
                    align-items: center;
                    padding: 4rem;
                    position: relative;
                    overflow: hidden;
                }

                @media (max-width: 992px) {
                    .auth-hero {
                        display: none;
                    }
                }

                .hero-content {
                    z-index: 2;
                    text-align: center;
                    max-width: 400px;
                }

                .hero-title {
                    font-weight: 800;
                    font-size: 2.5rem;
                    letter-spacing: -0.02em;
                    color: var(--zinc-900);
                    margin-bottom: 1rem;
                }

                .hero-subtitle {
                    color: var(--zinc-600);
                    font-size: 1.1rem;
                    line-height: 1.6;
                }

                .hero-illustration {
                    max-width: 100%;
                    max-height: 60vh;
                    height: auto;
                    margin-top: 3rem;
                    object-fit: contain;
                    filter: drop-shadow(0 20px 40px rgba(79, 70, 229, 0.1));
                    animation: float 6s ease-in-out infinite;
                }

                @media (max-height: 800px) {
                    .hero-title {
                        font-size: 2rem;
                    }

                    .hero-illustration {
                        margin-top: 1rem;
                        max-height: 50vh;
                    }

                    .auth-hero {
                        padding: 2rem;
                    }
                }

                @keyframes float {

                    0%,
                    100% {
                        transform: translateY(0);
                    }

                    50% {
                        transform: translateY(-20px);
                    }
                }

                /* Right Side: Login Form */
                .auth-form-side {
                    width: 500px;
                    background-color: white;
                    display: flex;
                    flex-direction: column;
                    justify-content: center;
                    padding: 4rem;
                    box-shadow: -10px 0 50px rgba(0, 0, 0, 0.02);
                    z-index: 10;
                }

                @media (max-width: 992px) {
                    .auth-form-side {
                        width: 100%;
                        padding: 2rem;
                    }
                }

                .brand-logo {
                    height: 40px;
                    margin-bottom: 3rem;
                }

                .login-header {
                    margin-bottom: 2.5rem;
                }

                .login-header h2 {
                    font-weight: 700;
                    font-size: 1.75rem;
                    letter-spacing: -0.01em;
                    margin-bottom: 0.5rem;
                }

                .login-header p {
                    color: var(--zinc-500);
                }

                .form-label {
                    font-size: 0.875rem;
                    font-weight: 600;
                    color: var(--zinc-700);
                    margin-bottom: 0.5rem;
                }

                .form-control {
                    padding: 0.75rem 1rem;
                    border-radius: 0.75rem;
                    border: 1px solid var(--zinc-200);
                    font-size: 0.95rem;
                    transition: all 0.2s ease;
                }

                .form-control:focus {
                    border-color: var(--primary);
                    box-shadow: 0 0 0 4px rgba(24, 24, 27, 0.08);
                }

                .password-toggle {
                    position: relative;
                }

                .toggle-btn {
                    position: absolute;
                    right: 1rem;
                    top: 50%;
                    transform: translateY(-50%);
                    background: none;
                    border: none;
                    color: var(--zinc-400);
                    cursor: pointer;
                    padding: 0;
                    font-size: 1rem;
                }

                .toggle-btn:hover {
                    color: var(--zinc-600);
                }

                .btn-login {
                    background-color: var(--primary);
                    border: none;
                    color: white;
                    padding: 0.75rem;
                    border-radius: 0.75rem;
                    font-weight: 600;
                    font-size: 1rem;
                    margin-top: 1rem;
                    transition: all 0.2s ease;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    gap: 0.5rem;
                }

                .btn-login:hover {
                    background-color: var(--primary-hover);
                    transform: translateY(-1px);
                    box-shadow: 0 4px 12px rgba(24, 24, 27, 0.15);
                }

                .btn-login:active {
                    transform: translateY(0);
                }

                .btn-login .spinner-border {
                    display: none;
                }

                .btn-login.loading .spinner-border {
                    display: inline-block;
                }

                .btn-login.loading span {
                    display: none;
                }

                .form-footer {
                    margin-top: 2rem;
                    text-align: center;
                    font-size: 0.875rem;
                }

                .form-footer a {
                    color: var(--primary);
                    text-decoration: none;
                    font-weight: 600;
                }

                .form-footer a:hover {
                    text-decoration: underline;
                }

                .alert {
                    border-radius: 0.75rem;
                    font-size: 0.875rem;
                    padding: 0.75rem 1rem;
                    margin-bottom: 1.5rem;
                }
            </style>
        </head>

        <body>

            <div class="auth-wrapper">
                <!-- Hero Side -->
                <div class="auth-hero">
                    <div class="hero-content">
                        <h1 class="hero-title">Elevate Your Workforce</h1>
                        <p class="hero-subtitle">Experience the future of Human Resource Management. Seamless,
                            intelligent, and designed for growth.</p>
                    </div>
                    <!-- Illustration Placeholder - Recommended: /images/login-hero.png -->
                    <img src="/images/login_page-hrm.png" alt="HRMS Illustration" class="hero-illustration">
                </div>

                <!-- Form Side -->
                <div class="auth-form-side">
                    <div class="login-container-inner">
                        <div class="brand-logo">
                            <c:choose>
                                <c:when test="${not empty orgLogo}">
                                    <img src="${orgLogo}" alt="Logo" style="height: 100%;">
                                </c:when>
                                <c:otherwise>
                                    <span
                                        style="font-weight: 800; font-size: 1.5rem; letter-spacing: -0.03em;">Sells<span
                                            style="color: var(--primary);">HRMS</span></span>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <div class="login-header">
                            <h2>Welcome back</h2>
                            <p>Enter your credentials to access your account</p>
                        </div>

                        <div id="loginMessage"></div>

                        <form id="loginForm">
                            <div class="mb-4">
                                <label for="email" class="form-label">Email Address</label>
                                <input type="email" class="form-control" id="email" placeholder="name@company.com"
                                    required autocomplete="username">
                            </div>

                            <div class="mb-4">
                                <div class="d-flex justify-content-between">
                                    <label for="password" class="form-label">Password</label>
                                    <a href="#" id="forgotPasswordLink"
                                        style="font-size: 0.8125rem; font-weight: 500; color: var(--primary); text-decoration: none;">Forgot
                                        password?</a>
                                </div>
                                <div class="password-toggle">
                                    <input type="password" class="form-control" id="password" placeholder="••••••••"
                                        required autocomplete="current-password">
                                    <button type="button" class="toggle-btn" id="togglePassword">
                                        <i class="fa-regular fa-eye"></i>
                                    </button>
                                </div>
                            </div>

                            <!-- <div class="mb-4 form-check">
                                <input type="checkbox" class="form-check-input" id="rememberMe">
                                <label class="form-check-label" for="rememberMe"
                                    style="font-size: 0.875rem; color: var(--zinc-600);">Remember me for 30 days</label>
                            </div> -->

                            <button type="submit" class="btn btn-login w-100" id="loginBtn">
                                <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
                                <span>Sign In</span>
                            </button>
                        </form>

                        <div class="form-footer">
                            <p class="text-muted">Don't have an account? <a href="/public/onboard">Click here to get
                                    started</a>
                            </p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Forgot Password Modal -->
            <div class="modal fade" id="forgotPasswordModal" tabindex="-1">
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content border-0 shadow-lg" style="border-radius: 1.25rem;">
                        <div class="modal-header border-0 pb-0">
                            <h5 class="modal-title font-weight-bold" style="font-weight: 700;">Reset Password</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body py-4">
                            <p class="text-muted mb-4" style="font-size: 0.9rem;">Enter your registered email address
                                and we'll send you a link to reset your password.</p>
                            <div class="mb-3">
                                <label for="forgotEmail" class="form-label">Email Address</label>
                                <input type="email" id="forgotEmail" class="form-control"
                                    placeholder="admin@yourorg.com">
                            </div>
                        </div>
                        <div class="modal-footer border-0 pt-0">
                            <button type="button" class="btn btn-link text-muted text-decoration-none"
                                data-bs-dismiss="modal">Cancel</button>
                            <button type="button" class="btn btn-primary px-4 py-2" id="submitForgot"
                                style="border-radius: 0.75rem; font-weight: 600;">Send Reset Link</button>
                        </div>
                    </div>
                </div>
            </div>

            <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

            <script>
                $(document).ready(function () {
                    // Password Toggle logic
                    const togglePassword = document.querySelector("#togglePassword");
                    const password = document.querySelector("#password");

                    togglePassword.addEventListener("click", function () {
                        const type = password.getAttribute("type") === "password" ? "text" : "password";
                        password.setAttribute("type", type);

                        const icon = this.querySelector("i");
                        icon.classList.toggle("fa-eye");
                        icon.classList.toggle("fa-eye-slash");
                    });

                    // Login logic
                    $("#loginForm").submit(function (e) {
                        e.preventDefault();
                        const btn = $("#loginBtn");
                        const email = $("#email").val();
                        const password = $("#password").val();
                        const messageDiv = $("#loginMessage");

                        messageDiv.empty();
                        btn.addClass("loading").attr("disabled", true);

                        $.ajax({
                            type: "POST",
                            url: "/api/auth/login",
                            contentType: "application/json",
                            data: JSON.stringify({ email: email, password: password }),
                            xhrFields: { withCredentials: true },
                            success: function (response) {
                                messageDiv.html('<div class="alert alert-success"><i class="fa-solid fa-circle-check me-2"></i>Login successful! Redirecting...</div>');

                                setTimeout(() => {
                                    if (response.systemRole === 'SUPER_ADMIN') {
                                        window.location.href = "/superadmin/dashboard";
                                    } else if (response.systemRole === 'ORG_ADMIN') {
                                        window.location.href = "/org/dashboard";
                                    } else if (response.systemRole === 'ACCOUNTANT') {
                                        window.location.href = "/accountant-panel";
                                    } else {
                                        window.location.href = "/employee/dashboard";
                                    }
                                }, 800);
                            },
                            error: function (jqXHR) {
                                btn.removeClass("loading").attr("disabled", false);
                                const errorMsg = jqXHR.responseJSON ? jqXHR.responseJSON.error : "Invalid credentials. Please try again.";
                                messageDiv.html('<div class="alert alert-danger"><i class="fa-solid fa-triangle-exclamation me-2"></i>' + errorMsg + '</div>');
                            }
                        });
                    });

                    // Forgot Password Link
                    $('#forgotPasswordLink').on('click', function (e) {
                        e.preventDefault();
                        const modal = new bootstrap.Modal(document.getElementById('forgotPasswordModal'));
                        modal.show();
                    });

                    // Submit Forgot Password
                    $('#submitForgot').on('click', function () {
                        const email = $('#forgotEmail').val().trim();
                        const btn = $(this);

                        if (!email) {
                            alert('Please enter your email address');
                            return;
                        }

                        btn.attr("disabled", true).text("Sending...");

                        $.ajax({
                            url: '/api/auth/forgot-password',
                            method: 'POST',
                            contentType: 'application/json',
                            data: JSON.stringify({ email: email }),
                            success: function (res) {
                                alert(res.message || 'If this email is valid, a reset link has been sent.');
                                $('#forgotPasswordModal').modal('hide');
                            },
                            error: function (xhr) {
                                alert(xhr.responseJSON?.message || 'Something went wrong. Please try again later.');
                            },
                            complete: function () {
                                btn.attr("disabled", false).text("Send Reset Link");
                            }
                        });
                    });
                });
            </script>
        </body>

        </html>