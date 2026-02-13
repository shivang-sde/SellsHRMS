<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <html>

        <head>
            <title>Create Accountant User</title>
            <style>
                body {
                    background-color: #f7f9fb;
                }

                .container {
                    max-width: 600px;
                    margin-top: 60px;
                }

                .card {
                    border-radius: 12px;
                    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.08);
                }

                .card-header {
                    background-color: #007bff;
                    color: white;
                    font-weight: bold;
                }

                .btn-primary {
                    border-radius: 8px;
                }

                .alert {
                    display: none;
                }
            </style>
        </head>

        <body>

            <div class="container">
                <div class="card">
                    <div class="card-header"><i class="bi bi-person-plus"></i> Create Accountant User</div>
                    <div class="card-body">
                        <form id="accountantForm">
                            <div class="mb-3">
                                <label for="email" class="form-label">Email address</label>
                                <input type="email" id="email" name="email" class="form-control"
                                    placeholder="Enter accountant email" required />
                            </div>

                            <div class="mb-3">
                                <label for="password" class="form-label">Password</label>
                                <input type="password" id="password" name="password" class="form-control"
                                    placeholder="Enter temporary password" required />
                            </div>

                            <button type="submit" class="btn btn-primary w-100">Create Accountant</button>
                        </form>

                        <div id="successAlert" class="alert alert-success mt-3" role="alert">
                            <i class="bi bi-check-circle"></i> Accountant user created successfully!
                        </div>
                        <div id="errorAlert" class="alert alert-danger mt-3" role="alert">
                            <i class="bi bi-x-circle"></i> Failed to create accountant.
                        </div>
                    </div>
                </div>
            </div>

        </body>

        </html>