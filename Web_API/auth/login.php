<?php
session_start();

if(isset($_SESSION["loggedin"]) && $_SESSION["loggedin"] === true){
    header("Location: https://www.omnic-systems.com/shareboard/mypage?user_string=" . $_SESSION["user_string"]);
    exit;
}

require_once "config.php";

$username = $password = "";
$username_err = $password_err = $login_err = "";

if ($_SERVER["REQUEST_METHOD"] == "POST") {

    if (empty(trim($_POST["username"]))) {
        $username_err = "Please enter username.";
    } else {
        $username = trim($_POST["username"]);
    }

    if (empty(trim($_POST["password"]))) {
        $password_err = "Please enter your password.";
    } else {
        $password = trim($_POST["password"]);
    }

    if (empty($username_err) && empty($password_err)) {
        $sql = "SELECT id, username, password, user_string FROM users WHERE username = ?";

        if ($stmt = mysqli_prepare($link, $sql)) {
            mysqli_stmt_bind_param($stmt, "s", $param_username);

            $param_username = $username;

            if (mysqli_stmt_execute($stmt)) {
                mysqli_stmt_store_result($stmt);

                if (mysqli_stmt_num_rows($stmt) == 1) {
                    mysqli_stmt_bind_result($stmt, $id, $username, $hashed_password, $user_string);
                    if (mysqli_stmt_fetch($stmt)) {
                        if ($password === $hashed_password) {
                            session_start();

                            $_SESSION["loggedin"] = true;
                            $_SESSION["id"] = $id;
                            $_SESSION["username"] = $username;
                            $_SESSION["user_string"] = $user_string;

                            header("Location: https://www.omnic-systems.com/shareboard/mypage");
                        } else {
                            $password_err = "Invalid username or password.";
                        }
                    }
                } else {
                    $password_err = "Invalid username or password.";
                }
            } else {
                echo "Something went wrong. Please try again later.";
            }

            mysqli_stmt_close($stmt);
        }
    }

    mysqli_close($link);
}
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login | Shareboard</title>
    <link rel="stylesheet" href="../css/auth.css">
</head>
<body>
<div class="hd"></div>
<div class="loginForm">
    <div class="mg"></div>
    <h1>Shareboard</h1>
    <h2>Login</h2>

    <form action="<?php echo htmlspecialchars($_SERVER["PHP_SELF"]); ?>" method="post">
        <div class="input">
            <input type="text" name="username" class="border <?php echo (!empty($username_err)) ? 'is-invalid' : ''; ?>"
                   placeholder="Username" value="<?php echo $username; ?>" size="50%">
            <span class="invalid-feedback"><?php echo $username_err; ?></span>
        </div>
        <div class="input">
            <input type="password" name="password"
                   class="border <?php echo (!empty($password_err)) ? 'is-invalid' : ''; ?>" placeholder="Password"
                   size="50%">
            <span class="invalid-feedback"><?php echo $password_err; ?></span>
        </div>
        <div class="container">
            <div class="left">
                <a class="btn left hyperlink" href="https://www.omnic-systems.com/shareboard/auth/register.php">Create
                    account</a>
            </div>
            <div class="right">
                <input type="submit" class="btn right" value="Login">
            </div>
        </div>
    </form>
</div>
</body>
</html>
