<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>${pageTitle} | SellsHRMS</title>
            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
            <style>
                /* Simple Custom Styling for HRMS */
                :root {
                    --primary-color: #0d6efd;
                    /* Bootstrap Blue */
                    --sidebar-width: 280px;
                }

                .sidebar {
                    width: var(--sidebar-width);
                    position: fixed;
                    top: 0;
                    left: 0;
                    bottom: 0;
                    background-color: #343a40;
                    /* Dark background */
                    padding-top: 56px;
                    /* Space for fixed navbar */
                    color: white;
                    transition: all 0.3s;
                }

                .main-content {
                    margin-left: var(--sidebar-width);
                    padding: 20px;
                    transition: all 0.3s;
                }

                @media (max-width: 991.98px) {
                    .sidebar {
                        margin-left: calc(-1 * var(--sidebar-width));
                    }

                    .sidebar.active {
                        margin-left: 0;
                    }

                    .main-content {
                        margin-left: 0;
                    }

                    .main-content.active {
                        margin-left: var(--sidebar-width);
                    }
                }

                .nav-link.active {
                    background-color: var(--primary-color);
                    color: white !important;
                }
            </style>
        </head>

        <body>

            <nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top">
                <div class="container-fluid">
                    <button class="navbar-toggler d-lg-none" type="button" data-bs-toggle="collapse"
                        data-bs-target="#sidebarMenu" aria-controls="sidebarMenu" aria-expanded="false"
                        aria-label="Toggle navigation">
                        <span class="navbar-toggler-icon"></span>
                    </button>
                    <a class="navbar-brand" href="#">SellsHRMS</a>
                    <div class="collapse navbar-collapse">
                        <ul class="navbar-nav ms-auto">
                            <li class="nav-item">
                                <a class="nav-link" href="#" onclick="logout(event)">
                                    <i class="bi bi-box-arrow-right"></i> Logout
                                </a>
                            </li>
                        </ul>
                    </div>
                </div>
            </nav>

            <div class="d-flex">
                <nav id="sidebarMenu" class="sidebar collapse d-lg-block">
                    <div class="position-sticky">
                        <div class="list-group list-group-flush mx-3 mt-4">
                            <c:import url="/WEB-INF/views/layout/sidebar-nav.jsp" />
                        </div>
                    </div>
                </nav>

                <main class="main-content flex-grow-1">
                    <div class="pt-5">
                        <h2 class="mb-4">${pageTitle}</h2>
                        <c:choose>
                            <c:when test="${not empty contentPage}">
                                <c:import url="/WEB-INF/views/${contentPage}.jsp" />
                            </c:when>
                            <c:otherwise>
                                <p>Welcome to SellsHRMS. Select a navigation link.</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </main>
            </div>

            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
            <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

            <script>
                function logout(event) {
                    event.preventDefault();
                    // Assuming your logout API is accessible at /api/auth/logout
                    $.post("/api/auth/logout")
                        .done(function (data) {
                            alert("Logged out successfully!");
                            window.location.href = "/login"; // Redirect to login page
                        })
                        .fail(function (jqXHR, textStatus, errorThrown) {
                            alert("Logout failed: " + jqXHR.responseJSON.error);
                        });
                }

                // Toggle sidebar on smaller screens
                const sidebar = document.getElementById('sidebarMenu');
                const toggler = document.querySelector('.navbar-toggler');
                const mainContent = document.querySelector('.main-content');

                toggler.addEventListener('click', () => {
                    sidebar.classList.toggle('active');
                    mainContent.classList.toggle('active');
                });

                // Set active class on sidebar links based on the current URL
                $(function () {
                    var path = window.location.pathname;
                    $('.list-group-item-action').each(function () {
                        if (path.startsWith($(this).attr('href'))) {
                            $(this).addClass('active');
                        } else {
                            $(this).removeClass('active');
                        }
                    });
                });
            </script>
        </body>

        </html>