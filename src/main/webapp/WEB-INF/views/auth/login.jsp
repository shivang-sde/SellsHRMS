<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Login | SellsHRMS</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            body {
                background-color: #f8f9fa;
            }

            .login-container {
                max-width: 400px;
                margin-top: 10vh;
                padding: 30px;
                box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
                border-radius: 0.5rem;
                background-color: white;
            }
        </style>
    </head>

    <body>
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-md-8 col-lg-6 login-container">
                    <h3 class="text-center mb-4">SellsHRMS Login</h3>
                    <form id="loginForm">
                        <div class="mb-3">
                            <label for="email" class="form-label">Email address</label>
                            <input type="email" class="form-control" id="email" required>
                        </div>
                        <div class="mb-3">
                            <label for="password" class="form-label">Password</label>
                            <input type="password" class="form-control" id="password" required>
                        </div>
                        <div class="d-grid mb-3">
                            <button type="submit" class="btn btn-primary">Login</button>
                        </div>
                        <p class="text-center">
                            <a href="/register">Register Super Admin</a>
                        </p>
                        <div id="loginMessage" class="mt-3"></div>
                    </form>
                </div>
            </div>
        </div>

        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <script>
            $(document).ready(function () {
                $("#loginForm").submit(function (e) {
                    e.preventDefault();
                    const email = $("#email").val();
                    const password = $("#password").val();
                    const messageDiv = $("#loginMessage");
                    messageDiv.empty();

                    $.ajax({
                        type: "POST",
                        url: "/api/auth/login",
                        contentType: "application/json",
                        data: JSON.stringify({ email: email, password: password }),
                        success: function (response) {
                            messageDiv.html('<div class="alert alert-success">Login successful! Redirecting...</div>');

                            // Simple redirection based on systemRole
                            if (response.systemRole === 'SUPER_ADMIN') {
                                window.location.href = "/superadmin/dashboard";
                            } else if (response.systemRole === 'ORG_ADMIN') {
                                window.location.href = "/orgadmin/dashboard";
                            } else {
                                // Assuming other roles go to a generic employee dashboard
                                window.location.href = "/user/dashboard";
                            }
                        },
                        error: function (jqXHR, textStatus, errorThrown) {
                            const errorMsg = jqXHR.responseJSON ? jqXHR.responseJSON.error : "An unknown error occurred.";
                            messageDiv.html('<div class="alert alert-danger">Login Failed: ' + errorMsg + '</div>');
                        }
                    });
                });
            });
        </script>
    </body>

    </html>