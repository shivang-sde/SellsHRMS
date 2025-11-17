<!DOCTYPE html>
<html>
<head>
    <title>Register Super Admin</title>
    <link rel="stylesheet" href="https://unpkg.com/@basecoat/core/dist/basecoat.min.css">
</head>

<body class="p-6 bg-gray-100">

<div class="container max-w-md mx-auto">

    <div class="card elevation-2 p-6 rounded-xl bg-white">
        <h2 class="text-2xl mb-4 font-bold">Create Super Admin</h2>

        <!-- ERROR ALERT -->
        <c:if test="${not empty error}">
            <div class="alert danger mb-4">
                <span>${error}</span>
            </div>
        </c:if>

        <!-- SUCCESS ALERT -->
        <c:if test="${not empty success}">
            <div class="alert success mb-4">
                <span>${success}</span>
            </div>
        </c:if>

        <form action="/sa/register" method="post" class="space-y-4">

            <div class="form-group">
                <label class="form-label">Email</label>
                <input type="email" name="email" class="input w-full" required>
            </div>

            <div class="form-group">
                <label class="form-label">Password</label>
                <input type="password" name="password" class="input w-full" required>
            </div>

            <button class="btn primary w-full">Register</button>
        </form>

        <div class="mt-4 text-sm">
            <a href="/sa/login" class="text-blue-600 hover:underline">
                Already have an account? Login
            </a>
        </div>
    </div>

</div>

</body>
</html>
