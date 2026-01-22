<!doctype html>
<html lang="en" dir="ltr">

<!-- Mirrored from nsdbytes.com/template/epic/main/login.html by HTTrack Website Copier/3.x [XR&CO'2014], Tue, 06 Jul 2021 19:21:47 GMT -->
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
<meta http-equiv="X-UA-Compatible" content="ie=edge">
 <link rel="shortcut icon" href="img/icons/icon-48x48.png" />
<title> Sellspark HRM</title>

<link rel="stylesheet" href="/plugins/bootstrap/css/bootstrap.min.css" />

<link rel="stylesheet" href="/css/main.css" />
<link rel="stylesheet" href="/css/theme2.css" />
</head>

<body class="font-montserrat">
<div class="auth">
<div class="auth_left">
<div class="card">
<div class="text-center mb-2">
<a class="header-brand" href="dashboard.jsp">
    <img alt="Sellspark" src="/img/sellsparkLogo.png"  width="200" height="100" />
</a>

</div>
<div class="card-body">
<form role="loginForm" name="form" id="loginForm"  accept-charset="UTF-8">
<div class="card-title">Login to your account</div>
 
<span style="color: red;" id="success"> </span>	
<br>
<div class="form-group">
    <label class="form-label" >Email</label>
<input type="email" class="form-control" id="email" name="email" placeholder="Enter email">
 <span style="color: red;" id="emailerr"> </span>
</div>
<div class="form-group">
<label class="form-label">Password<!-- <a href="forgot-password.html" class="float-right small">I forgot password</a> --></label>
<input type="password" name="password" class="form-control" id="password" placeholder="Password" requiredd>
 <span style="color: red;" id="passworderr"> </span>
</div>

<div class="form-footer">
<button type="submit" class="btn btn-primary btn-block">LOGIN</button>
</div>
<div id="loginMessage" class="mt-3"></div>
</form>
</div>

</div>
</div>
<div class="auth_right">
<div class="carousel slide" data-ride="carousel" data-interval="3000">
<div class="carousel-inner">
<div class="carousel-item active">
<img src="/images/3.png" class="img-fluid" alt="login page" />
<div class="px-4 mt-4">
</div>
</div>
<div class="carousel-item">
<img src="/images/2.png" class="img-fluid" alt="login page" />
<div class="px-4 mt-4">
</div>
</div>
<div class="carousel-item">
<img src="/img/avatars/avatar-3.jpg" class="img-fluid" alt="login page" />
<div class="px-4 mt-4"></div>
	<!-- <%-- <%
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    String userIpAddress = httpRequest.getHeader("X-Forwarded-For");
    System.out.print(userIpAddress);
    %> --%> -->
</div>
</div>
</div>
</div>
</div>
        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.2.4/jquery.min.js"></script>
	
<script src="/bundles/lib.vendor.bundle.js" type="af61b2ec1cc3befd67ed78d5-text/javascript"></script>
<script src="/js/core.js" type="af61b2ec1cc3befd67ed78d5-text/javascript"></script>
<script src="https://ajax.cloudflare.com/cdn-cgi/scripts/7d0fa10a/cloudflare-static/rocket-loader.min.js"></script>

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
                                window.location.href = "/org/dashboard";
                            } else {
                                // Assuming other roles go to a generic employee dashboard
                                window.location.href = "/employee/dashboard";
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

<!-- Mirrored from nsdbytes.com/template/epic/main/login.html by HTTrack Website Copier/3.x [XR&CO'2014], Tue, 06 Jul 2021 19:21:48 GMT -->
</html>