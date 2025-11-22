<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Register Super Admin | SellsHRMS</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            body {
                background-color: #f8f9fa;
            }

            .register-container {
                max-width: 500px;
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
                <div class="col-md-10 col-lg-8 register-container">
                    <h3 class="text-center mb-4">Register Super Admin</h3>
                    <form id="registerForm">
                        <div class="mb-3">
                            <label for="email" class="form-label">Email address</label>
                            <input type="email" class="form-control" id="email" required>
                        </div>
                        <div class="mb-3">
                            <label for="password" class="form-label">Password</label>
                            <input type="password" class="form-control" id="password" required>
                        </div>
                        <div class="d-grid mb-3">
                            <button type="submit" class="btn btn-success">Register</button>
                        </div>
                        <p class="text-center">
                            <a href="/login">Back to Login</a>
                        </p>
                        <div id="registerMessage" class="mt-3"></div>
                    </form>
                </div>
            </div>
        </div>

        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <script>
            $(document).ready(function () {
                $("#registerForm").submit(function (e) {
                    e.preventDefault();
                    const email = $("#email").val();
                    const password = $("#password").val();
                    const messageDiv = $("#registerMessage");
                    messageDiv.empty();

                    $.ajax({
                        type: "POST",
                        url: "/api/auth/register-superadmin",
                        contentType: "application/json",
                        data: JSON.stringify({ email: email, password: password }),
                        success: function (response) {
                            messageDiv.html('<div class="alert alert-success">Super Admin created successfully! You can now log in.</div>');
                            $("#registerForm")[0].reset(); // Clear the form
                        },
                        error: function (jqXHR, textStatus, errorThrown) {
                            const errorMsg = jqXHR.responseJSON ? jqXHR.responseJSON.error : "An unknown error occurred.";
                            messageDiv.html('<div class="alert alert-danger">Registration Failed: ' + errorMsg + '</div>');
                        }
                    });
                });
            });
        </script>
    </body>

    </html>