<html>

<head>
    <title>Login</title>
</head>

<body>

    <h2>HRMS Login</h2>

    <form action="/authenticate" method="post">
        <label>Email</label>
        <input type="text" name="username" required />

        <label>Password</label>
        <input type="password" name="password" required />

        <button type="submit">Login</button>
    </form>

</body>

</html>