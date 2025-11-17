<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <!DOCTYPE html>
    <html>

    <head>
        <title>Super Admin Login</title>
        <link rel="stylesheet" href="https://unpkg.com/@basecoat/core/dist/basecoat.min.css">
    </head>

    <body class="p-6 bg-gray-100">

        <div class="container max-w-md mx-auto">

            <div class="card elevation-2 p-6 rounded-xl bg-white">
                <h2 class="text-2xl mb-4 font-bold">Super Admin Login</h2>

                <!-- ERROR FROM SPRING SECURITY -->
                <c:if test="${param.error != null}">
                    <div class="alert danger mb-4">
                        <span>Invalid email or password!</span>
                    </div>
                </c:if>

                <!-- ERROR FROM CONTROLLER (optional) -->
                <c:if test="${not empty error}">
                    <div class="alert danger mb-4">
                        <span>${error}</span>
                    </div>
                </c:if>

                <form action="${pageContext.request.contextPath}/login" method="post" class="space-y-4">

                    <div class="form-group">
                        <label class="form-label">Email</label>
                        <input type="email" name="email" class="input w-full" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Password</label>
                        <input type="password" name="password" class="input w-full" required>
                    </div>

                    <button class="btn primary w-full">Login</button>
                </form>

                <div class="mt-4 text-sm">
                    <a href="${pageContext.request.contextPath}/sa/register" class="text-blue-600 hover:underline">
                        Create new Super Admin
                    </a>
                </div>
            </div>

        </div>

    </body>

    </html>