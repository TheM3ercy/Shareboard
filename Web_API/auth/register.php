<?php
require_once "config.php";

$username = $email = $password = $confirm_password = "";
$username_err = $email_err = $password_err = $confirm_password_err = "";

if ($_SERVER["REQUEST_METHOD"] == "POST") {

    if (empty(trim($_POST["username"]))) {
        $username_err = "Please enter a username.";
    } elseif (!preg_match('/^[a-zA-Z0-9_]+$/', trim($_POST["username"]))) {
        $username_err = "Username can only contain letters, numbers, and underscores.";
    } else {
        $sql = "SELECT username FROM users WHERE username = ?";

        if ($stmt = mysqli_prepare($link, $sql)) {
            mysqli_stmt_bind_param($stmt, "s", $param_username);

            $param_username = trim($_POST["username"]);

            if (mysqli_stmt_execute($stmt)) {
                mysqli_stmt_store_result($stmt);

                if (mysqli_stmt_num_rows($stmt) == 1) {
                    $username_err = "This username is already taken.";
                } else {
                    $username = trim($_POST["username"]);
                }
            } else {
                echo "Something went wrong. Please try again later.";
            }

            mysqli_stmt_close($stmt);
        }
    }

    if (empty(trim($_POST["email"]))) {
        $email_err = "Please enter a email.";
    } elseif (!filter_var(trim($_POST["email"]), FILTER_VALIDATE_EMAIL)) {
        $email_err = "Please enter an email address.";
    } else {
        $email = trim($_POST["email"]);
    }

    if (empty(trim($_POST["password"]))) {
        $password_err = "Please enter a password.";
    } elseif (strlen(trim($_POST["password"])) < 6) {
        $password_err = "Password must have at least 6 characters.";
    } else {
        $password = trim($_POST["password"]);
    }

    if (empty($password_err)) {
        if (empty(trim($_POST["confirm_password"]))) {
            $confirm_password_err = "Please confirm password.";
        } else {
            $confirm_password = trim($_POST["confirm_password"]);
            if (empty($password_err) && ($password != $confirm_password)) {
                $confirm_password_err = "Password did not match.";
            }
        }
    }

    if (empty($username_err) &&  empty($email_err) && empty($password_err) && empty($confirm_password_err)) {

        $sql = "INSERT INTO users (username, password, email, user_string) VALUES (?, ?, ?, ?)";

        if ($stmt = mysqli_prepare($link, $sql)) {
            mysqli_stmt_bind_param($stmt, "ssss", $param_username, $param_password, $param_email, $param_user_string);

            // Set parameters
            $param_username = $username;
            $param_email = $email;
            $param_password = $password;
            $param_user_string = uniqid(25);
            if (mysqli_stmt_execute($stmt)) {
                if($stmt = mysqli_prepare($link,"SELECT id FROM users WHERE user_string='$param_user_string'")) {
                    mysqli_stmt_bind_param($stmt, "s", $id);
                    if (mysqli_stmt_execute($stmt)) {
                        session_start();

                        $_SESSION["loggedin"] = true;
                        $_SESSION["id"] = $id;
                        $_SESSION["username"] = $username;
                        $_SESSION["user_string"] = $param_user_string;
                        header("location: login.php");
                    }else{
                        echo "Something went wrong. Please try again later.";
                    }
                }else{
                    echo "Something went wrong. Please try again later.";
                }
            } else {
                echo "Something went wrong. Please try again later.";
            }

            // Close statement
            mysqli_stmt_close($stmt);
        }
    }

    // Close connection
    mysqli_close($link);
}
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Sign Up | Shareboard</title>
    <link rel="stylesheet" href="../css/auth.css">
</head>
<body>
<div class="hd"></div>
<div class="loginForm">
    <div class="mg">
        <h1>Shareboard</h1>
        <h2>Sign Up</h2>
        <form action="<?php echo htmlspecialchars($_SERVER["PHP_SELF"]); ?>" method="post">
            <div class="input">
                <input type="text" name="username"
                       class="border <?php echo (!empty($username_err)) ? 'is-invalid' : ''; ?>"
                       value="<?php echo $username; ?>" placeholder="Username" size="50%">
                <span class="invalid-feedback"><?php echo $username_err; ?></span>
            </div>
            <div class="input">
                <input type="text" name="email"
                       class="border <?php echo (!empty($email_err)) ? 'is-invalid' : ''; ?>"
                       value="<?php echo $email; ?>" placeholder="E-mail" size="50%">
                <span class="invalid-feedback"><?php echo $email_err; ?></span>
            </div>
            <div class="input">
                <input type="password" name="password"
                       class="border <?php echo (!empty($password_err)) ? 'is-invalid' : ''; ?>"
                       value="<?php echo $password; ?>" placeholder="Password" size="50%">
                <span class="invalid-feedback"><?php echo $password_err; ?></span>
            </div>
            <div class="input">
                <input type="password" name="confirm_password"
                       class="border <?php echo (!empty($confirm_password_err)) ? 'is-invalid' : ''; ?>"
                       value="<?php echo $confirm_password; ?>" placeholder="Confirm Password" size="50%">
                <span class="invalid-feedback"><?php echo $confirm_password_err; ?></span>
            </div>
            <div class="container">
                <div class="left">
                    <input type="submit" class="btn left" value="Submit">
                </div>
                <div class="right">
                    <input type="reset" class="btn right" value="Reset">
                </div>
            </div>
            <div class="login">
                <p>Already have an account? <a href="login.php">Login here</a>.</p>
            </div>
        </form>
    </div>
</div>
</body>
</html>