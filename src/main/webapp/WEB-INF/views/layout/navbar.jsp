<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

    <nav class="main-header navbar navbar-expand navbar-white navbar-light shadow-sm">

        <!-- Left -->
        <ul class="navbar-nav">
            <li class="nav-item">
                <a class="nav-link" data-widget="pushmenu" href="#"><i class="fas fa-bars"></i></a>
            </li>
            <li class="nav-item d-none d-sm-inline-block">
                <a href="/" class="navbar-brand navbar-brand-text">SellsPark HRMS</a>
            </li>
        </ul>

        <!-- Right -->
        <ul class="navbar-nav ml-auto">
            <li class="nav-item">
                <span class="nav-link">Hello, <strong>
                        <c:out value="${email}" />
                    </strong></span>
            </li>

            <li class="nav-item">
                <form method="post" id="logoutForm">
                    <button class="btn btn-outline-danger btn-sm" type="button" onclick="doLogout()">Logout</button>
                </form>
            </li>
        </ul>
    </nav>

    <script>
        async function doLogout() {
            await fetch('/api/auth/logout', { method: 'POST' });
            window.location = '/login';
        }
    </script>